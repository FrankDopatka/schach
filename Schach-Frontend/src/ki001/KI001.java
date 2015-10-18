package ki001;

import java.util.ArrayList;
import java.util.Random;

import backend.BackendSpielStub;
import daten.*;
import frontend.KI;

public class KI001 extends KI {

	public KI001(){
		super("Referenz-KI Zufallszuege");
	}

	@Override
	public void ichBinAmZug() {
		BackendSpielStub b=getBackend();
		ArrayList<D_Zug> zuege=new ArrayList<D_Zug>();
		ArrayList<D> meineFiguren=Xml.toArray(b.getFigurenAufFeld(binWeiss()));

		if (meineFiguren!=null){ // alle erlaubten Zuege auslesen
			for(D figur:meineFiguren){
				if (figur instanceof D_Figur){
					String feldStart=figur.getString("feld");
					ArrayList<D> felderZiel=Xml.toArray(b.getErlaubteZuege(feldStart));				
					 if (felderZiel!=null){
						 for(D zug:felderZiel){
							 if (zug instanceof D_Zug){
								 D_Zug zugNeu=new D_Zug();
								 zugNeu.setString("feldStart",feldStart);
								 zugNeu.setString("feldZiel",zug.getString("feldZiel"));
								 zuege.add(zugNeu);
							 }
						 }
					 }
				}
			}
			

			// zufaelligen Zug durchfuehren
			int zugNummer=getZufallszahl(0,zuege.size());
			D_Zug zugGewaehlt=zuege.get(zugNummer);
			b.ziehe(zugGewaehlt.getString("feldStart"),zugGewaehlt.getString("feldZiel"));
		}
	}

	@Override
	public void ichBinNichtZug() {
		//TODO ggf. weitere Spielzuege analysieren
	}
	
	@Override
	public void ichHabeVerloren() {
		if (binWeiss())
			System.out.println("MIST, die KI "+getInfo()+" (weiss) hat VERLOREN!");
		else
			System.out.println("MIST, die KI "+getInfo()+" (schwarz) hat VERLOREN!");
	}

	@Override
	public void ichHabeGewonnen() {
		if (binWeiss())
			System.out.println("JUCHU, die KI "+getInfo()+" (weiss) hat GEWONNEN!");
		else
			System.out.println("JUCHU, die KI "+getInfo()+" (schwarz) hat GEWONNEN!");
	}

	@Override
	public void patt() {
		if (binWeiss())
			System.out.println("NAJA, die KI "+getInfo()+" (weiss)  hat PATT gespielt!");
		else
			System.out.println("NAJA, die KI "+getInfo()+" (schwarz) hat PATT gespielt!");
		System.out.println("Eigentlich eine gute Leistung dafuer, dass ich nur zufaellig ziehe... ;-)");
	}
	
	private int getZufallszahl(int ug,int og){
		Random r=new Random();
		return r.nextInt(og-ug)+ug;
	}
}
