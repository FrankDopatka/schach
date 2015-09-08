package backend.figuren;

import java.util.ArrayList;

import daten.*;
import backend.spiel.Brett;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Bauer extends Figur {

	public Bauer(){
	}
	public Bauer(Spiel spiel,boolean istWeiss) {
		super(spiel,"B",istWeiss); // Pawn, wird meist weggelassen
	}

	@Override
	public ArrayList<String> getErlaubteZuege(){
		ArrayList<String> felder=initFelder();
		if (felder==null) return new ArrayList<String>();
		Feld feldStart=getFeld();
		Feld feldDazwischen=null;
		Feld feldZiel=null;
		int x=feldStart.getPosX();
		int y=feldStart.getPosY();
		if ((y==1)||(y==8)) return felder; // Bauer am Ende angekommen
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
			D letzterZug=getSpiel().getLetzterZug();
			if ((letzterZug!=null)&&(letzterZug.getString("bemerkungSpielzug").equals(""+ZugEnum.BauerDoppelschritt))){
				int koordinatenAlt[]=Brett.fromKuerzel(letzterZug.getString("feldZiel"));
				if (koordinatenAlt[1]==y){
					if (koordinatenAlt[0]==x-1)
						felder.add(Brett.toKuerzel(x-1,y+1));
					else if (koordinatenAlt[0]==x+1)
						felder.add(Brett.toKuerzel(x+1,y+1));					
				}
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
			D letzterZug=getSpiel().getLetzterZug();
			if ((letzterZug!=null)&&(letzterZug.getString("bemerkungSpielzug").equals(""+ZugEnum.BauerDoppelschritt))){
				int koordinatenAlt[]=Brett.fromKuerzel(letzterZug.getString("feldZiel"));
				if (koordinatenAlt[1]==y){
					if (koordinatenAlt[0]==x-1)
						felder.add(Brett.toKuerzel(x-1,y-1));
					else if (koordinatenAlt[0]==x+1)
						felder.add(Brett.toKuerzel(x+1,y-1));
				}
			}
		}
		return felder;
	}
}
