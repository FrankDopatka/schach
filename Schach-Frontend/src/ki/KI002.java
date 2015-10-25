package ki;

import java.util.ArrayList;
import java.util.Random;

import backend.BackendSpielStub;
import daten.*;
import frontend.KI;

public class KI002 extends KI {

	public KI002(){
		super("Zufallszuege, aber schlagen wenn moeglich");
	}

	@Override
	public void ichBinAmZug() {
		BackendSpielStub b=getBackend();
		ArrayList<D_Zug> zuege=new ArrayList<D_Zug>();
		ArrayList<D_Zug> zuegeSchlagen=new ArrayList<D_Zug>();
		ArrayList<D> meineFiguren=Xml.toArray(b.getFigurenAufFeld(binWeiss()));

		if (meineFiguren!=null){ 
			for(D figur:meineFiguren){ // alle erlaubten Zuege auslesen
				if (figur instanceof D_Figur){
					String feldStart=figur.getString("feld");
					ArrayList<D> felderZiel=Xml.toArray(b.getErlaubteZuege(feldStart));				
					 if (felderZiel!=null){
						 for(D zug:felderZiel){
							 if (zug instanceof D_Zug){
								 D_Zug zugNeu=new D_Zug();
								 zugNeu.setString("feldStart",feldStart);
								 zugNeu.setString("feldZiel",zug.getString("feldZiel"));
								 if ((zug.getString("figurGeschlagen")!=null)&&(zug.getString("figurGeschlagen").length()>0))
									 zuegeSchlagen.add(zugNeu);									 
								 else
									 zuege.add(zugNeu);									 
							 }
						 }
					 }
				}
			}
			// zufaelligen Zug durchfuehren, aber schlagen wenn moeglich
			if (zuegeSchlagen.size()>0){
				int zugNummer=getZufallszahl(0,zuegeSchlagen.size());
				D_Zug zugGewaehlt=zuegeSchlagen.get(zugNummer);				
				b.ziehe(zugGewaehlt.getString("feldStart"),zugGewaehlt.getString("feldZiel"));
			}
			else{
				int zugNummer=getZufallszahl(0,zuege.size());
				D_Zug zugGewaehlt=zuege.get(zugNummer);								
				b.ziehe(zugGewaehlt.getString("feldStart"),zugGewaehlt.getString("feldZiel"));
			}
		}
	}

	@Override
	public void ichBinNichtZug() {
		//TODO ggf. weitere Spielzuege analysieren
	}
	
	@Override
	public void ichHabeVerloren() {
		if (binWeiss())
			getFrontend().log("MIST, die KI "+getInfo()+" (weiss) hat VERLOREN!");
		else
			getFrontend().log("MIST, die KI "+getInfo()+" (schwarz) hat VERLOREN!");
	}

	@Override
	public void ichHabeGewonnen() {
		if (binWeiss())
			getFrontend().log("JUCHU, die KI "+getInfo()+" (weiss) hat GEWONNEN!");
		else
			getFrontend().log("JUCHU, die KI "+getInfo()+" (schwarz) hat GEWONNEN!");
	}

	@Override
	public void patt() {
		if (binWeiss())
			getFrontend().log("NAJA, die KI "+getInfo()+" (weiss)  hat PATT gespielt!");
		else
			getFrontend().log("NAJA, die KI "+getInfo()+" (schwarz) hat PATT gespielt!");
		getFrontend().log("Eigentlich eine gute Leistung dafuer, dass ich nur zufaellig ziehe... ;-)");
	}
	
	private int getZufallszahl(int ug,int og){
		Random r=new Random();
		return r.nextInt(og-ug)+ug;
	}
}
