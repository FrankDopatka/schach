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
	private D_Spiel d_Spiel=null;
	
	public Spiel(){
		brett=new Brett();
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
	
	private Image getBildGeschlagen(boolean weiss){
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
		return d_Spiel.getBool("weissAmZug");
	}
	public boolean weissImSchach() {
		return d_Spiel.getBool("weissImSchach");
	}
	public boolean weissSchachMatt() {
		return d_Spiel.getBool("weissSchachMatt");
	}

	public boolean schwarzAmZug() {
		return !weissAmZug();
	}
	public boolean schwarzImSchach() {
		return d_Spiel.getBool("schwarzImSchach");
	}
	public boolean schwarzSchachMatt() {
		return d_Spiel.getBool("schwarzSchachMatt");
	}
	
	public boolean istImSchach(boolean weiss) {
		if (weiss) return weissImSchach();
		return schwarzImSchach();
	}
	public boolean istSchachMatt(boolean weiss) {
		if (weiss) return weissSchachMatt();
		return schwarzSchachMatt();
	}
	
	public ArrayList<D_Zug> getZugHistorie(){
		return zugHistorie;
	}
	
	public D_Zug getLetzterZug(){
		if ((zugHistorie==null)||(zugHistorie.size()==0)) return null;
		return (zugHistorie.get(zugHistorie.size()-1));
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
		// alles OK, Zug durchfuehren...
		int[] koordinatenAlt=Brett.fromKuerzel(sFeldStart); // 0:x, 1:y
		int[] koordinatenNeu=Brett.fromKuerzel(sFeldZiel); // 0:x, 1:y

		if (figurZiel!=null){ // eine Figur auf dem Zielfeld wird geschlagen
			figurZiel.setFeld(null);
		}
		// en passant
		boolean istEnPassant=false;
		D_Zug letzterZug=getLetzterZug();
		if ((letzterZug!=null)&&(letzterZug.getString("bemerkungSpielzug").equals(""+D_Zug_Bemerkung.BauerDoppelschritt))){
			if ((figurZiel==null)&&(koordinatenAlt[0]!=koordinatenNeu[0])){
				Figur bauerZuSchlagen=getBrett().getFeld(letzterZug.getString("feldZiel")).getFigur();
				bauerZuSchlagen.setFeld(null);
				istEnPassant=true;
			}
		}
		// Bauer Doppelschritt
		boolean istBauerDoppelschritt=false;
		if (figurStart instanceof Bauer){
			if ((koordinatenAlt[1]==koordinatenNeu[1]+2)||(koordinatenAlt[1]==koordinatenNeu[1]-2)){
				istBauerDoppelschritt=true;
			}
		}
		// Rochade
		boolean istRochade=false;
		if (figurStart instanceof Koenig){
			if ((koordinatenAlt[0]==koordinatenNeu[0]+2)||(koordinatenAlt[0]==koordinatenNeu[0]-2)){
				istRochade=true;
				// passender Turm noch ziehen
				Figur turm=null;
				if (sFeldZiel.equals("c1")){
					turm=getBrett().getFeld("a1").getFigur();
					turm.setFeld(getBrett().getFeld("d1"));
				}else if (sFeldZiel.equals("g1")){
					turm=getBrett().getFeld("h1").getFigur();
					turm.setFeld(getBrett().getFeld("f1"));
				}else if (sFeldZiel.equals("c8")){
					turm=getBrett().getFeld("a8").getFigur();
					turm.setFeld(getBrett().getFeld("d8"));					
				}else{
					turm=getBrett().getFeld("h8").getFigur();				
					turm.setFeld(getBrett().getFeld("f8"));					
				}
				turm.bereitsBewegt();
			}
		}
		// ZUG DURCHFUEHREN
		figurStart.setFeld(feldZiel);
		figurStart.wurdeBewegt();
		gezogen(figurStart,figurZiel,sFeldStart,sFeldZiel,istRochade,istBauerDoppelschritt,istEnPassant);
	}
	
	private void gezogen(Figur figurBewegt,Figur figurGeschlagen,String sFeldStart,String sFeldZiel,
			boolean istRochade,boolean istBauerDoppelschritt,boolean istEnPassant) {
		D_Zug d_zug=new D_Zug();

		// SCHACH
		if (binIchImSchach(figurBewegt,!figurBewegt.istWeiss())){
			
			
			
			System.out.println("SCHACH? Schlagbar:"+getSchlagbareFelder(!figurBewegt.istWeiss(),false));
			ArrayList<String> schlagbar=getSchlagbareFelder(!figurBewegt.istWeiss(),false);
			
			
			
			
			if (getSchlagbareFelder(!figurBewegt.istWeiss(),false).size()==0){
				if (!figurBewegt.istWeiss()){
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.WeissSchachMatt);
					d_Spiel.setBool("weissSchachMatt",true);					
				}
				else
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.SchwarzSchachMatt);
					d_Spiel.setBool("schwarzSchachMatt",true);
			}
			else{
				if (!figurBewegt.istWeiss()){
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.WeissImSchach);
					d_Spiel.setBool("weissImSchach",true);					
				}
				else{
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.SchwarzImSchach);
					d_Spiel.setBool("schwarzImSchach",true);					
				}
			}
		}
		
		// SPEZIALZUEGE: BAUER DOPPELSCHRITT, ROCHADE, EN PASSANT
		if (istBauerDoppelschritt) d_zug.setString("bemerkungSpielzug",""+D_Zug_Bemerkung.BauerDoppelschritt);		
		if (istRochade) d_zug.setString("bemerkungSpielzug",""+D_Zug_Bemerkung.Rochade);
		if (istEnPassant) d_zug.setString("bemerkungSpielzug",""+D_Zug_Bemerkung.EnPassant);
		
		// ZUGZAEHLER
		d_Spiel.incInt("zugZaehler");

		// ZUGDATEN
		d_zug.setInt("nummer",d_Spiel.getInt("zugZaehler"));
		d_zug.setString("figurBewegt",figurBewegt.getKuerzel());
		d_zug.setBool("figurBewegtIstWeiss",figurBewegt.istWeiss());
		if (figurGeschlagen!=null) d_zug.setString("figurGeschlagen",figurGeschlagen.getKuerzel());
		d_zug.setString("feldStart",sFeldStart);
		d_zug.setString("feldZiel",sFeldZiel);
		d_zug.setString("zeitstempel",""+System.currentTimeMillis());
		zugHistorie.add(d_zug);
		d_Spiel.invertBool("weissAmZug");

		System.out.println(d_Spiel);
	}

	// bin ich selbst im Schach, wenn ich mich von dieser Position auf die neue Position bewege?
	public boolean binIchImSchachDurchZug(String sFeldStart,String sFeldZiel){
		Figur ziehendeFigur=getBrett().getFeld(sFeldStart).getFigur();
		Figur geschlageneFigur=zieheTestweise(ziehendeFigur,sFeldStart,sFeldZiel);
		boolean schach=binIchImSchach(ziehendeFigur,ziehendeFigur.istWeiss());
		zieheTestweiseZurueck(ziehendeFigur,geschlageneFigur,sFeldStart,sFeldZiel);
		return schach;
	}

	public boolean istFeldBedroht(String sFeld,boolean durchWeiss,boolean rochadenCheck){
		ArrayList<String> schlagbareFelder=getSchlagbareFelder(durchWeiss,rochadenCheck);
		return schlagbareFelder.contains(sFeld);
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
	private boolean binIchImSchach(Figur ziehendeFigur,boolean binWeiss){
		String feldMeinKoenig=getKoenig(binWeiss).getFeld().getKuerzel();
		ArrayList<String> schlagbareFelder=getSchlagbareFelder(!binWeiss,false);	
		if (schlagbareFelder==null) return false;
		return (schlagbareFelder.contains(feldMeinKoenig));
	}

	public ArrayList<String> getSchlagbareFelder(boolean vonWeiss,boolean rochadenCheck){
		ArrayList<String> erg=new ArrayList<String>();
		for (Figur f:figuren){
			if ((f.istWeiss()==vonWeiss)&&(!f.istGeschlagen())){
				if (rochadenCheck && (f instanceof Koenig)) continue;
				ArrayList<String> erlaubt=f.getErlaubteZuege();
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
