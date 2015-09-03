package backend.figuren;

import java.util.ArrayList;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Turm extends Figur {

	public Turm(Spiel spiel,boolean istWeiss) {
		super(spiel,"T",istWeiss);
	}

	@Override
	public ArrayList<String> getErlaubteZuege(boolean eigeneBewegungImGange){
		ArrayList<String> felder=new ArrayList<String>();
		if (istGeschlagen()) return felder;
		Feld feldStart=getFeld();
		int x=feldStart.getPosX();
		int y=feldStart.getPosY();
		int i;
		for (i=y+1;i<=8;i++){
			if (!addZug(felder,x,i)) break;
		}
		for (i=y-1;i>=1;i--){
			if (!addZug(felder,x,i)) break;
		}
		for (i=x+1;i<=8;i++){
			if (!addZug(felder,i,y)) break;
		}
		for (i=x-1;i>=1;i--){
			if (!addZug(felder,i,y)) break;
		}
		// komme ich durch die Bewegung selbst ins Schach? -> Bewegung wieder entfernen!
		if (eigeneBewegungImGange) removeZuegeSelbstImSchach(felder,this);
		return felder;
	}
}
