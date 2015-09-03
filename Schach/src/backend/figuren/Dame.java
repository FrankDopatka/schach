package backend.figuren;

import java.util.ArrayList;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Dame extends Figur {

	public Dame(){
	}
	public Dame(Spiel spiel,boolean istWeiss) {
		super(spiel,"D",istWeiss);
	}

	@Override
	public ArrayList<String> getErlaubteZuege(boolean eigeneBewegungImGange){
		ArrayList<String> felder=new ArrayList<String>();
		if (istGeschlagen()) return felder;
		Feld feldStart=getFeld();
		int x=feldStart.getPosX();
		int y=feldStart.getPosY();
		int i,j;
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
		// komme ich durch die Bewegung selbst ins Schach? -> Bewegung wieder entfernen!
		if (eigeneBewegungImGange) removeZuegeSelbstImSchach(felder,this);
		return felder;
	}
}
