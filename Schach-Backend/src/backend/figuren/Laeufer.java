package backend.figuren;

import java.util.ArrayList;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Laeufer extends Figur {

	public Laeufer(){
	}
	public Laeufer(Spiel spiel,boolean istWeiss) {
		super(spiel,"L",istWeiss);
	}

	@Override
	public ArrayList<String> getErlaubteZuege(){
		ArrayList<String> felder=new ArrayList<String>();
		if (istGeschlagen()) return felder;
		if (getSpiel().weissSchachMatt()||getSpiel().schwarzSchachMatt()) return felder;
		Feld feldStart=getFeld();
		int x=feldStart.getPosX();
		int y=feldStart.getPosY();
		int i,j;
		for (i=x+1,j=y+1;((i<=8)&&(j<=8));i++,j++){
			if (!addZug(felder,i,j)) break;
		}
		for (i=x-1,j=y-1;((i>=1)&&(j>=1));i--,j--){
			if (!addZug(felder,i,j)) break;
		}
		for (i=x-1,j=y+1;((i>=1)&&(j<=8));i--,j++){
			if (!addZug(felder,i,j)) break;
		}
		for (i=x+1,j=y-1;((i<=8)&&(j>=1));i++,j--){
			if (!addZug(felder,i,j)) break;
		}
		return felder;
	}
}
