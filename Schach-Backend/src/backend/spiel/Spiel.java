package backend.spiel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

import daten.*;
import backend.Parameter;
import backend.figuren.*;

public class Spiel {
	private Brett brett=null;
	private ArrayList<Figur> figuren=new ArrayList<Figur>();
	private ArrayList<D_Zug> zugHistorie=new ArrayList<D_Zug>();
	private Regelwerk regelwerk=null;;
	private D_Spiel d_Spiel=null;
	
	public Spiel(){
		brett=new Brett();
		regelwerk=new Regelwerk(this);
		d_Spiel=new D_Spiel();
	}

	public Spiel(String pfad) {
		this();
		BufferedReader br=null;
		try {
			pfad=URLDecoder.decode(""+pfad,"ISO-8859-1");
			StringBuffer spielXML=new StringBuffer();
			br=new BufferedReader(new FileReader(pfad));
			String zeile=br.readLine(); 
	    while (zeile!=null){
	    	spielXML.append(zeile+"/n");
	      zeile=br.readLine(); 
	    } 
	    ArrayList<D> spielDaten=Xml.toArray(spielXML.toString());    
	    int counter=0;
	    // Daten des Spiels
	    d_Spiel=(D_Spiel)spielDaten.get(counter);
	    counter++;
	    // Daten der Figuren
	    figuren.clear();
	    for(int i=1;i<=32;i++){
	    	D_Figur d_Figur=(D_Figur)spielDaten.get(counter);
	    	@SuppressWarnings("unchecked")
				Class<Figur> c=(Class<Figur>)Class.forName(Parameter.pfadKlassenFiguren+d_Figur.getString("typ"));
	    	Figur figur=(Figur)c.newInstance();
	    	figur.setSpiel(this);
	    	figur.setKuerzel(d_Figur.getString("kuerzel"));
	    	figur.setFarbe(d_Figur.getBool("istWeiss"));
	    	figur.setFeld(brett.getFeld(d_Figur.getString("feld")));
	    	if (d_Figur.getBool("bereitsBewegt")) figur.wurdeBewegt();
	    	figuren.add(figur);
	    	counter++;
	    }
	    // Zughistorie
	    zugHistorie.clear();
	    for(int i=1;i<=d_Spiel.getInt("zugZaehler");i++){
	    	D_Zug d_Zug=(D_Zug)spielDaten.get(counter);
	    	zugHistorie.add(d_Zug);
	    	counter++;
	    }
		}
		catch (Exception e){
			throw new RuntimeException("Fehler beim Laden des Spiels von "+pfad+": "+e.getMessage());
		} 	
		finally{
			try {
				br.close();
			} catch (Exception e) {}			
		}
	}
	
	public Brett getBrett(){
		return brett;
	}
	
	public Regelwerk getRegelwerk(){
		return regelwerk;
	}

	public ArrayList<Figur> getFiguren(){
		return figuren;
	}
	
	public boolean weissAmZug() {
		return d_Spiel.getBool("weissAmZug");
	}
	public boolean weissImSchach() {
		return d_Spiel.getString("bemerkungSchach").equals(ZugEnum.WeissImSchach);
	}
	public boolean weissSchachMatt() {
		return d_Spiel.getString("bemerkungSchach").equals(ZugEnum.WeissSchachMatt);
	}

	public boolean schwarzAmZug() {
		return !weissAmZug();
	}
	public boolean schwarzImSchach() {
		return d_Spiel.getString("bemerkungSchach").equals(ZugEnum.SchwarzImSchach);
	}
	public boolean schwarzSchachMatt() {
		return d_Spiel.getString("bemerkungSchach").equals(ZugEnum.SchwarzSchachMatt);
	}
	
	public boolean istImSchach(boolean weiss) {
		if (weiss) return weissImSchach();
		return schwarzImSchach();
	}
	public boolean istSchachMatt(boolean weiss) {
		if (weiss) return weissSchachMatt();
		return schwarzSchachMatt();
	}
	
	public boolean istPatt(){
		return d_Spiel.getString("bemerkungSchach").equals(ZugEnum.Patt);		
	}
	
	public ArrayList<D_Zug> getZugHistorie(){
		return zugHistorie;
	}
	
	public D_Zug getLetzterZug(){
		if ((zugHistorie==null)||(zugHistorie.size()==0)) return null;
		return (zugHistorie.get(zugHistorie.size()-1));
	}
	
	public BufferedImage getBildWeiss(){
		int groesse=Parameter.groesseFeld;
		BufferedImage im=new BufferedImage(groesse*8+45,groesse*8+140,BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		g.setColor(Parameter.farbeBrettHintergrund);
		g.fillRect(0,0,im.getWidth(null),im.getHeight(null));
		// Brett
		g.drawImage(brett.getBild(true),20,70,null);
		// Beschriftung des Bretts
		zeichneBeschriftung(im,g,true);
		// weisse geschlagene Figuren
		g.drawImage(getBildGeschlagen(true),20,10,null);
		// schwarze geschlagene Figuren
		g.drawImage(getBildGeschlagen(false),20,groesse*8+100,null);
		g.dispose();
		return im;
	}
	
	public BufferedImage getBildSchwarz(){
		int groesse=Parameter.groesseFeld;
		BufferedImage im=new BufferedImage(groesse*8+45,groesse*8+140,BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		g.setColor(Parameter.farbeBrettHintergrund);
		g.fillRect(0,0,im.getWidth(null),im.getHeight(null));
		// Brett
		g.drawImage(brett.getBild(false),20,70,null);
		// Beschriftung des Bretts
		zeichneBeschriftung(im,g,false);
		// schwarze geschlagene Figuren
		g.drawImage(getBildGeschlagen(false),20,10,null);
		// weisse geschlagene Figuren
		g.drawImage(getBildGeschlagen(true),20,groesse*8+100,null);
		g.dispose();
		return im;
	}

	private void zeichneBeschriftung(BufferedImage im,Graphics2D g,boolean vonWeiss){
		int groesse=Parameter.groesseFeld;
		for (int i=1;i<=8;i++){
			g.setFont(new Font("Arial",Font.BOLD,18));
			g.setColor(new Color(0,0,0));
			if (vonWeiss){
				g.drawString(""+i,3,im.getHeight(null)-(88+groesse*(i-1)));
				g.drawString(""+i,26+groesse*8,im.getHeight(null)-(88+groesse*(i-1)));				
				g.drawString(Brett.toZeichen(i),15+groesse/2+groesse*(i-1),63);
				g.drawString(Brett.toZeichen(i),15+groesse/2+groesse*(i-1),90+groesse*8);
			}
			else{
				g.drawString(""+(9-i),3,im.getHeight(null)-(88+groesse*(i-1)));
				g.drawString(""+(9-i),26+groesse*8,im.getHeight(null)-(88+groesse*(i-1)));
				g.drawString(Brett.toZeichen(9-i),15+groesse/2+groesse*(i-1),63);
				g.drawString(Brett.toZeichen(9-i),15+groesse/2+groesse*(i-1),90+groesse*8);
			}
		}
	}

	private Image getBildGeschlagen(boolean weiss){
		int groesse=Parameter.groesseFeld;
		Image im=new BufferedImage(groesse*8,groesse/2+4,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		g.setColor(Parameter.farbeBrettHintergrund);
		g.fillRect(0,0,im.getWidth(null),im.getHeight(null));
		int anzahl=0;
		for(Figur f:regelwerk.getGeschlageneFiguren(weiss)){
			g.drawImage(f.getBild(),2+(groesse/2*anzahl),2,null);
			anzahl++;
		}
		g.dispose();			
		return im;
	}

	public Figur getKoenig(boolean vonWeiss){
		for (Figur f:figuren){
			if (f.istWeiss()==vonWeiss){
				if (f instanceof Koenig) return f;
			}
		}
		return null;
	}
	
	public String speichern(String pfad){
		PrintWriter pw=null;
		try {
			if (!pfad.endsWith(".xml")) pfad=pfad+".xml";
			pw=new PrintWriter(new FileWriter(pfad));
			pw.println(Xml.verpacken(toXml()));
			return Xml.verpacken(Xml.fromD(new D_OK()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
		finally{
			pw.close();			
		}
	}

	public D_Spiel toD(){
		return d_Spiel;
	}
	
	public String toXml(){
		StringBuffer xml=new StringBuffer();
		xml.append(Xml.fromD(this.toD()));
		for(Figur figur:figuren){
			xml.append(figur.toXml());			
		}
		for(D_Zug zug:zugHistorie){
			xml.append(zug.toXml());			
		}
		return xml.toString();
	}
}
