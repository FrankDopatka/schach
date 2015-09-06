package backend.figuren;

import java.util.ArrayList;

import daten.D_Zug;
import daten.D_Zug_Bemerkung;
import backend.spiel.Brett;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Bauer extends Figur {

	public Bauer(){
	}
	public Bauer(Spiel spiel,boolean istWeiss) {
		super(spiel,"B",istWeiss);
	}

	@Override
	public ArrayList<String> getErlaubteZuege(){
		ArrayList<String> felder=new ArrayList<String>();
		if (istGeschlagen()) return felder;
		if (getSpiel().weissSchachMatt()||getSpiel().schwarzSchachMatt()) return felder;
		Feld feldStart=getFeld();
		Feld feldDazwischen=null;
		Feld feldZiel=null;
		int x=feldStart.getPosX();
		int y=feldStart.getPosY();
		if (istWeiss()){ // Weiss spielt immer nach oben
			// Bewegung nach oben...
			// 1 Schritt
			feldZiel=getSpiel().getBrett().getFeld(x,y+1);
			if ((feldZiel!=null)&&(!feldZiel.hatFigur())) felder.add(Brett.toKuerzel(x,y+1));
			// 2 Schritte
			if(!bereitsBewegt()){
				feldZiel=getSpiel().getBrett().getFeld(x,y+2);
				feldDazwischen=getSpiel().getBrett().getFeld(x,y+1);
				if ((feldZiel!=null)&&(!feldZiel.hatFigur())){
					if ((feldDazwischen!=null)&&(!feldDazwischen.hatFigur())){
						felder.add(Brett.toKuerzel(x,y+2));
					}
				}
			}
			// links schlagen
			if (x>1){
				feldZiel=getSpiel().getBrett().getFeld(x-1,y+1);
				if ((feldZiel!=null)&&(feldZiel.hatGegnerischeFigur(this))) felder.add(Brett.toKuerzel(x-1,y+1));				
			}
			// rechts schlagen
			if (x<8){
				feldZiel=getSpiel().getBrett().getFeld(x+1,y+1);
				if ((feldZiel!=null)&&(feldZiel.hatGegnerischeFigur(this))) felder.add(Brett.toKuerzel(x+1,y+1));				
			}
			// en passant moeglich?
			D_Zug letzterZug=getSpiel().getLetzterZug();
			if ((letzterZug!=null)&&(letzterZug.getString("bemerkungSpielzug").equals(""+D_Zug_Bemerkung.BauerDoppelschritt))){
				int koordinatenAlt[]=Brett.fromKuerzel(letzterZug.getString("feldZiel"));
				if (koordinatenAlt[0]==x-1)
					felder.add(Brett.toKuerzel(x-1,y+1));
				else if (koordinatenAlt[0]==x+1)
					felder.add(Brett.toKuerzel(x+1,y+1));
			}
		}
		else{
			// Bewegung nach unten (schwarz)...
			// 1 Schritt
			feldZiel=getSpiel().getBrett().getFeld(x,y-1);
			if ((feldZiel!=null)&&(!feldZiel.hatFigur())) felder.add(Brett.toKuerzel(x,y-1));
			// 2 Schritte
			if(!bereitsBewegt()){
				feldZiel=getSpiel().getBrett().getFeld(x,y-2);
				feldDazwischen=getSpiel().getBrett().getFeld(x,y-1);
				if ((feldZiel!=null)&&(!feldZiel.hatFigur())){
					if ((feldDazwischen!=null)&&(!feldDazwischen.hatFigur())){
						felder.add(Brett.toKuerzel(x,y-2));
					}
				}
			}
			// links schlagen
			if (x>1){
				feldZiel=getSpiel().getBrett().getFeld(x-1,y-1);
				if ((feldZiel!=null)&&(feldZiel.hatGegnerischeFigur(this))) felder.add(Brett.toKuerzel(x-1,y-1));				
			}
			// rechts schlagen
			if (x<8){
				feldZiel=getSpiel().getBrett().getFeld(x+1,y-1);
				if ((feldZiel!=null)&&(feldZiel.hatGegnerischeFigur(this))) felder.add(Brett.toKuerzel(x+1,y-1));
			}
			// en passant moeglich?
			D_Zug letzterZug=getSpiel().getLetzterZug();
			if ((letzterZug!=null)&&(letzterZug.getString("bemerkungSpielzug").equals(""+D_Zug_Bemerkung.BauerDoppelschritt))){
				int koordinatenAlt[]=Brett.fromKuerzel(letzterZug.getString("feldZiel"));
				if (koordinatenAlt[0]==x-1)
					felder.add(Brett.toKuerzel(x-1,y-1));
				else if (koordinatenAlt[0]==x+1)
					felder.add(Brett.toKuerzel(x+1,y-1));
			}
		}
		return felder;
	}
}
