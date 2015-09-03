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
	private boolean weissAmZug=true;
	private ArrayList<Figur> figuren=new ArrayList<Figur>();
	
	public Spiel(){
		brett=new Brett();
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
	    D_Spiel d_Spiel=(D_Spiel)spielDaten.get(counter);
	    weissAmZug=d_Spiel.getBool("weissAmZug");
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
	    	figuren.add(figur);
	    	counter++;
	    }
	    
		}
		catch (Exception e){
			e.printStackTrace();
//			throw new RuntimeException("Fehler beim Laden des Spiels von "+pfad+": "+e.getMessage());
		} 	
		finally{
			try {
				br.close();
			} catch (Exception e) {}			
		}
	    
		// TODO Auto-generated constructor stub
	}
	
	

	public void setzeStartbelegung(){
		Figur figur;
		for (int i=1;i<=8;i++){
			figur=new Bauer(this,true);
			figur.setFeld(brett.getFeld(i,2)); // weisse Bauern
			figuren.add(figur);
			figur=new Bauer(this,false);
			figur.setFeld(brett.getFeld(i,7)); // schwarze Bauern
			figuren.add(figur);
		}	
		int x=1;
		boolean weiss=true;
		for (int i=1;i<=2;i++){ // restliche Figuren
			addFigur(new Turm(this,weiss),"a"+x);
			addFigur(new Springer(this,weiss),"b"+x);
			addFigur(new Laeufer(this,weiss),"c"+x);
			addFigur(new Dame(this,weiss),"d"+x);
			addFigur(new Koenig(this,weiss),"e"+x);
			addFigur(new Laeufer(this,weiss),"f"+x);
			addFigur(new Springer(this,weiss),"g"+x);
			addFigur(new Turm(this,weiss),"h"+x);
			x=8; 
			weiss=false;
		} 
	}
	
	private void addFigur(Figur figur,String position){
		figur.setFeld(brett.getFeld(position));
		figuren.add(figur);
	}
	
	public Brett getBrett(){
		return brett;
	}
	
	public BufferedImage getBild(){
		int groesse=Parameter.groesseFeld;
		BufferedImage im=new BufferedImage(groesse*8+45,groesse*8+140,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		g.setColor(Parameter.farbeBrettHintergrund);
		g.fillRect(0,0,im.getWidth(null),im.getHeight(null));
		g.drawImage(brett.getBild(),20,70,null);
		for (int i=1;i<=8;i++){
			g.setFont(new Font("Arial",Font.BOLD,18));
			g.setColor(new Color(0,0,0));
			g.drawString(""+i,3,im.getHeight(null)-(88+groesse*(i-1)));
			g.drawString(""+i,26+groesse*8,im.getHeight(null)-(88+groesse*(i-1)));
			g.drawString(Brett.toZeichen(i),15+groesse/2+groesse*(i-1),63);
			g.drawString(Brett.toZeichen(i),15+groesse/2+groesse*(i-1),90+groesse*8);
		}
		// weisse geschlagene Figuren
		g.drawImage(getBildGeschlagen(true),20,10,null);
		// schwarze geschlagene Figuren
		g.drawImage(getBildGeschlagen(false),20,groesse*8+100,null);
		g.dispose();
		return im;
	}
	
	public ArrayList<Figur> getGeschlageneFiguren(boolean weiss){
		ArrayList<Figur> ergebnis=new ArrayList<Figur>();
		for(Figur f:getFiguren(FigurEnum.Dame,weiss)) if (f.istGeschlagen()) ergebnis.add(f);
		for(Figur f:getFiguren(FigurEnum.Turm,weiss)) if (f.istGeschlagen()) ergebnis.add(f);
		for(Figur f:getFiguren(FigurEnum.Springer,weiss)) if (f.istGeschlagen()) ergebnis.add(f);
		for(Figur f:getFiguren(FigurEnum.Laeufer,weiss)) if (f.istGeschlagen()) ergebnis.add(f);
		for(Figur f:getFiguren(FigurEnum.Bauer,weiss)) if (f.istGeschlagen()) ergebnis.add(f);
		return ergebnis;
	}
	
	public ArrayList<Figur> getFiguren(FigurEnum typ,boolean weiss){
		ArrayList<Figur> ergebnis=new ArrayList<Figur>();
		for(Figur f:figuren){
			if ((f.istWeiss()==weiss)&&(typ.toString().equals(f.getTyp()))){
				ergebnis.add(f);
			}
		}
		return ergebnis;
	}
	
	public Image getBildGeschlagen(boolean weiss){
		int groesse=Parameter.groesseFeld;
		Image im=new BufferedImage(groesse*8,groesse/2+4,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		g.setColor(Parameter.farbeBrettHintergrund);
		g.fillRect(0,0,im.getWidth(null),im.getHeight(null));
		int anzahl=0;
		for(Figur f:getGeschlageneFiguren(weiss)){
			g.drawImage(f.getBild(),2+(groesse/2*anzahl),2,null);
			anzahl++;
		}
		g.dispose();			
		return im;
	}
	
	public boolean weissAmZug() {
		return weissAmZug;
	}

	public boolean schwarzAmZug() {
		return !weissAmZug;
	}

	public void gezogen(Figur figur) {
		String s="";
		if (!figur.istWeiss())
			s+="WEISS";
		else
			s+="SCHWARZ";
		if (binIchImSchach(!figur.istWeiss())){
			if (getSchlagbareFelder(!figur.istWeiss(),true).size()==0)
				System.out.println("SCHACHMATT "+s+"!");
			else
				System.out.println("SCHACH "+s+"!");
		}
		weissAmZug=!weissAmZug;
	}
	
	public void ziehe(String sFeldStart,String sFeldZiel){
		Feld feldStart=getBrett().getFeld(sFeldStart);
		if (feldStart==null) throw new RuntimeException("Das Feld "+sFeldStart+" ist ungueltig!");
		Feld feldZiel=getBrett().getFeld(sFeldZiel);
		if (feldZiel==null) throw new RuntimeException("Das Feld "+sFeldZiel+" ist ungueltig!");
		Figur figurStart=feldStart.getFigur();
		Figur figurZiel=feldZiel.getFigur();
		if (figurStart==null) throw new RuntimeException("Auf dem Feld "+sFeldStart+" befindet sich keine Figur!");
		if (figurStart.istWeiss()!=weissAmZug())
			 throw new RuntimeException("Sie sind nicht am Zug!");
		// Regelcheck
		if (!figurStart.istZugErlaubt(sFeldZiel))
			throw new RuntimeException("Der Zug von "+figurStart.getKuerzel()+sFeldStart+" nach "+sFeldZiel+" ist nicht regelkonform!");
		// bin ich durch den eigenen Zug selbst im Schach?
		if (binIchImSchachDurchZug(sFeldStart,sFeldZiel))
			throw new RuntimeException("Der Zug von "+figurStart.getKuerzel()+sFeldStart+" nach "+sFeldZiel+" ist verboten, da Sie dadurch im Schach stehen wuerden!");		
		// alles OK, Zug durchfuehren
		if (figurZiel!=null){
			// eine Figur auf dem Zielfeld wird geschlagen
			figurZiel.setFeld(null);
		}
		figurStart.setFeld(feldZiel);
		figurStart.wurdeBewegt();
		gezogen(figurStart);
	}
	
	private Figur zieheTestweise(Figur ziehendeFigur,String sFeldStart,String sFeldZiel){
		Feld feldZiel=getBrett().getFeld(sFeldZiel);
		Figur figurGeschlagen=feldZiel.getFigur();		
		if (figurGeschlagen!=null) figurGeschlagen.setFeld(null);
		ziehendeFigur.setFeld(getBrett().getFeld(sFeldZiel));
		return figurGeschlagen;
	}
	private void zieheTestweiseZurueck(Figur ziehendeFigur,Figur geschlageneFigur,String sFeldStart,String sFeldZiel){
		zieheTestweise(ziehendeFigur,sFeldZiel,sFeldStart);
		if (geschlageneFigur!=null) geschlageneFigur.setFeld(getBrett().getFeld(sFeldZiel));
	}

	// bin ich selbst im Schach, wenn ich mich von dieser Position auf die neue Position bewege?
	public boolean binIchImSchachDurchZug(String sFeldStart,String sFeldZiel){
		Figur ziehendeFigur=getBrett().getFeld(sFeldStart).getFigur();
		Figur geschlageneFigur=zieheTestweise(ziehendeFigur,sFeldStart,sFeldZiel);
		boolean schach=binIchImSchach(ziehendeFigur.istWeiss());
		zieheTestweiseZurueck(ziehendeFigur,geschlageneFigur,sFeldStart,sFeldZiel);
		return schach;
	}

	public boolean binIchImSchach(boolean binWeiss){
		String feldMeinKoenig=getKoenig(binWeiss).getFeld().getKuerzel();
		ArrayList<String> schlagbareFelder=getSchlagbareFelder(!binWeiss,false);
		if (schlagbareFelder==null) return false;
		return (schlagbareFelder.contains(feldMeinKoenig));
	}
	
	public ArrayList<String> getSchlagbareFelder(boolean vonWeiss,boolean eigeneBewegungImGange){
		ArrayList<String> erg=new ArrayList<String>();
		for (Figur f:figuren){
			if ((f.istWeiss()==vonWeiss)&&(!f.istGeschlagen())){
				ArrayList<String> erlaubt=f.getErlaubteZuege(eigeneBewegungImGange);
				if (erlaubt==null) return null;
				for (String feld:erlaubt){
					if (!erg.contains(feld)) erg.add(feld);					
				}
			}
		}
		return erg;
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
		D_Spiel d_Spiel=new D_Spiel();
		d_Spiel.setBool("weissAmZug",weissAmZug());

		return d_Spiel;
	}
	
	public String toXml(){
		StringBuffer xml=new StringBuffer();
		xml.append(Xml.fromD(this.toD()));
		for(Figur figur:figuren){
			xml.append(figur.toXml());			
		}
		return xml.toString();
	}
}
