package backend.spiel;

import java.util.ArrayList;

import daten.D_Zug;
import daten.D_Zug_Bemerkung;
import backend.figuren.Bauer;
import backend.figuren.Dame;
import backend.figuren.Figur;
import backend.figuren.FigurEnum;
import backend.figuren.Koenig;
import backend.figuren.Laeufer;
import backend.figuren.Springer;
import backend.figuren.Turm;

public class Regelwerk {
	private Spiel spiel;
	private Brett brett;
	private ArrayList<Figur> figuren;
	
	public Regelwerk(Spiel spiel){
		this.spiel=spiel;
		this.brett=spiel.getBrett();
		this.figuren=spiel.getFiguren();
	}
	
	public void setzeStartbelegung(){
		Figur figur;
		for (int i=1;i<=8;i++){
			figur=new Bauer(spiel,true);
			figur.setFeld(brett.getFeld(i,2)); // weisse Bauern
			figuren.add(figur);
			figur=new Bauer(spiel,false);
			figur.setFeld(brett.getFeld(i,7)); // schwarze Bauern
			figuren.add(figur);
		}	
		int x=1;
		boolean weiss=true;
		for (int i=1;i<=2;i++){ // restliche Figuren
			addFigur(new Turm(spiel,weiss),"a"+x);
			addFigur(new Springer(spiel,weiss),"b"+x);
			addFigur(new Laeufer(spiel,weiss),"c"+x);
			addFigur(new Dame(spiel,weiss),"d"+x);
			addFigur(new Koenig(spiel,weiss),"e"+x);
			addFigur(new Laeufer(spiel,weiss),"f"+x);
			addFigur(new Springer(spiel,weiss),"g"+x);
			addFigur(new Turm(spiel,weiss),"h"+x);
			x=8; 
			weiss=false;
		} 
	}

	public ArrayList<Figur> getFigurenAufFeld(boolean weiss){
		ArrayList<Figur> erg=new ArrayList<Figur>();
		for(Figur f:figuren){
			if ((f.istWeiss()==weiss)&&(!f.istGeschlagen())) erg.add(f);
		}
		return erg;
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

	public void ziehe(String sFeldStart,String sFeldZiel){
		Feld feldStart=brett.getFeld(sFeldStart);
		if (feldStart==null) throw new RuntimeException("Das Feld "+sFeldStart+" ist ungueltig!");
		Feld feldZiel=brett.getFeld(sFeldZiel);
		if (feldZiel==null) throw new RuntimeException("Das Feld "+sFeldZiel+" ist ungueltig!");
		Figur figurStart=feldStart.getFigur();
		Figur figurZiel=feldZiel.getFigur();
		if (figurStart==null) throw new RuntimeException("Auf dem Feld "+sFeldStart+" befindet sich keine Figur!");
		if (figurStart.istWeiss()!=spiel.weissAmZug())
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
		D_Zug letzterZug=spiel.getLetzterZug();
		if ((letzterZug!=null)&&(letzterZug.getString("bemerkungSpielzug").equals(""+D_Zug_Bemerkung.BauerDoppelschritt))){
			if ((brett.getFeld(sFeldStart).getFigur() instanceof Bauer)&&(figurZiel==null)&&(koordinatenAlt[0]!=koordinatenNeu[0])){
				Figur bauerZuSchlagen=brett.getFeld(letzterZug.getString("feldZiel")).getFigur();
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
					turm=brett.getFeld("a1").getFigur();
					turm.setFeld(brett.getFeld("d1"));
				}else if (sFeldZiel.equals("g1")){
					turm=brett.getFeld("h1").getFigur();
					turm.setFeld(brett.getFeld("f1"));
				}else if (sFeldZiel.equals("c8")){
					turm=brett.getFeld("a8").getFigur();
					turm.setFeld(brett.getFeld("d8"));					
				}else{
					turm=brett.getFeld("h8").getFigur();				
					turm.setFeld(brett.getFeld("f8"));					
				}
				turm.bereitsBewegt();
			}
		}
		// ZUG DURCHFUEHREN
		figurStart.setFeld(feldZiel);
		figurStart.wurdeBewegt();
		gezogen(figurStart,figurZiel,sFeldStart,sFeldZiel,istRochade,istBauerDoppelschritt,istEnPassant);
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
	
	private void gezogen(Figur figurBewegt,Figur figurGeschlagen,String sFeldStart,String sFeldZiel,
			boolean istRochade,boolean istBauerDoppelschritt,boolean istEnPassant) {
		D_Zug d_zug=new D_Zug();
		d_zug.setInt("nummer",spiel.toD().getInt("zugZaehler"));
		d_zug.setString("figurBewegt",figurBewegt.getKuerzel());
		d_zug.setBool("figurBewegtIstWeiss",figurBewegt.istWeiss());
		if (figurGeschlagen!=null) d_zug.setString("figurGeschlagen",figurGeschlagen.getKuerzel());
		d_zug.setString("feldStart",sFeldStart);
		d_zug.setString("feldZiel",sFeldZiel);
		d_zug.setString("zeitstempel",""+System.currentTimeMillis());
		spiel.getZugHistorie().add(d_zug);

		
		// SCHACH UND SCHACHMATT
		if (binIchImSchach(figurBewegt,!figurBewegt.istWeiss())){
			ArrayList<String> erlaubteZuege=new ArrayList<String>();
			for (Figur f:getFigurenAufFeld(!figurBewegt.istWeiss())){
				ArrayList<String> erlaubt=f.getErlaubteZuege();
				if (erlaubt!=null){
					for (String feld:erlaubt){
						if ((!erlaubteZuege.contains(feld))&&(!binIchImSchachDurchZug(f.getFeld().getKuerzel(),feld))){
							System.out.println(f);
							System.out.println(f.getFeld().getKuerzel());
							System.out.println(feld);
							erlaubteZuege.add(feld);					
						}
					}						
				}				
			}
			if (erlaubteZuege.size()==0){
				if (!figurBewegt.istWeiss()){
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.WeissSchachMatt);
					spiel.toD().setBool("weissSchachMatt",true);					
				}
				else{
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.SchwarzSchachMatt);
					spiel.toD().setBool("schwarzSchachMatt",true);					
				}
			}
			else{
				if (!figurBewegt.istWeiss()){
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.WeissImSchach);
					spiel.toD().setBool("weissImSchach",true);					
				}
				else{
					d_zug.setString("bemerkungSchach",""+D_Zug_Bemerkung.SchwarzImSchach);
					spiel.toD().setBool("schwarzImSchach",true);					
				}
			}
		}

		// SPEZIALZUEGE: BAUER DOPPELSCHRITT, ROCHADE, EN PASSANT
		if (istBauerDoppelschritt) d_zug.setString("bemerkungSpielzug",""+D_Zug_Bemerkung.BauerDoppelschritt);		
		if (istRochade) d_zug.setString("bemerkungSpielzug",""+D_Zug_Bemerkung.Rochade);
		if (istEnPassant) d_zug.setString("bemerkungSpielzug",""+D_Zug_Bemerkung.EnPassant);
		
		// ZUGZAEHLER
		spiel.toD().incInt("zugZaehler");

		// ZUGDATEN
		spiel.toD().invertBool("weissAmZug");

		System.out.println(spiel.toD());
	}

	// bin ich selbst im Schach, wenn ich mich von dieser Position auf die neue Position bewege?
	public boolean binIchImSchachDurchZug(String sFeldStart,String sFeldZiel){
		Figur ziehendeFigur=brett.getFeld(sFeldStart).getFigur();
		Figur geschlageneFigur=zieheTestweise(ziehendeFigur,sFeldStart,sFeldZiel);
		boolean schach=binIchImSchach(ziehendeFigur,ziehendeFigur.istWeiss());
		zieheTestweiseZurueck(ziehendeFigur,geschlageneFigur,sFeldStart,sFeldZiel);
		return schach;
	}
	
	// *********************
	//
	// PRIVATE HILFSMETHODEN
	//
	// *********************
	
	private void addFigur(Figur figur,String position){
		figur.setFeld(brett.getFeld(position));
		figuren.add(figur);
	}
	
	private ArrayList<Figur> getFiguren(FigurEnum typ,boolean weiss){
		ArrayList<Figur> ergebnis=new ArrayList<Figur>();
		for(Figur f:figuren){
			if ((f.istWeiss()==weiss)&&(typ.toString().equals(f.getTyp()))){
				ergebnis.add(f);
			}
		}
		return ergebnis;
	}
	
	private Figur zieheTestweise(Figur ziehendeFigur,String sFeldStart,String sFeldZiel){
		Feld feldZiel=brett.getFeld(sFeldZiel);
		Figur figurGeschlagen=feldZiel.getFigur();		
		if (figurGeschlagen!=null) figurGeschlagen.setFeld(null);
		ziehendeFigur.setFeld(brett.getFeld(sFeldZiel));
		return figurGeschlagen;
	}
	private void zieheTestweiseZurueck(Figur ziehendeFigur,Figur geschlageneFigur,String sFeldStart,String sFeldZiel){
		zieheTestweise(ziehendeFigur,sFeldZiel,sFeldStart);
		if (geschlageneFigur!=null) geschlageneFigur.setFeld(brett.getFeld(sFeldZiel));
	}
	private boolean binIchImSchach(Figur ziehendeFigur,boolean binWeiss){
		String feldMeinKoenig=spiel.getKoenig(binWeiss).getFeld().getKuerzel();
		ArrayList<String> schlagbareFelder=getSchlagbareFelder(!binWeiss,false);	
		if (schlagbareFelder==null) return false;
		return (schlagbareFelder.contains(feldMeinKoenig));
	}
}
