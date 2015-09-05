package backend.figuren;

import java.util.ArrayList;

import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Springer extends Figur {

	public Springer(){
	}
	public Springer(Spiel spiel,boolean istWeiss) {
		super(spiel,"S",istWeiss);
	}

	@Override
	public ArrayList<String> getErlaubteZuege(){
		ArrayList<String> felder=new ArrayList<String>();
		if (istGeschlagen()) return felder;
		if (getSpiel().weissSchachMatt()||getSpiel().schwarzSchachMatt()) return felder;
		Feld feldStart=getFeld();
		int x=feldStart.getPosX();
		int y=feldStart.getPosY();
		addZug(felder,x+2,y+1);		
		addZug(felder,x+2,y-1);		
		addZug(felder,x+1,y+2);		
		addZug(felder,x+1,y-2);		
		addZug(felder,x-1,y+2);		
		addZug(felder,x-1,y-2);		
		addZug(felder,x-2,y+1);		
		addZug(felder,x-2,y-1);		
		return felder;
	}
}
