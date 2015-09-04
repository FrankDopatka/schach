package backend.figuren;

import java.util.ArrayList;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Koenig extends Figur {

	public Koenig(){
	}
	public Koenig(Spiel spiel,boolean istWeiss) {
		super(spiel,"K",istWeiss);
	}

	@Override
	public ArrayList<String> getErlaubteZuege(boolean eigeneBewegungImGange){
		ArrayList<String> felder=new ArrayList<String>();
		if (istGeschlagen()) return felder;
		Feld feldStart=getFeld();
		int x=feldStart.getPosX();
		int y=feldStart.getPosY();
		addZug(felder,x-1,y+1);		
		addZug(felder,x,y+1);		
		addZug(felder,x+1,y+1);		
		addZug(felder,x-1,y);		
		addZug(felder,x+1,y);		
		addZug(felder,x-1,y-1);		
		addZug(felder,x,y-1);		
		addZug(felder,x+1,y-1);		
		// komme ich durch die Bewegung selbst ins Schach? -> Bewegung wieder entfernen!
		if (eigeneBewegungImGange) removeZuegeSelbstImSchach(felder,this);
		return felder;
	}
}