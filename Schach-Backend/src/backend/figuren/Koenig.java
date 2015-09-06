package backend.figuren;

import java.util.ArrayList;

import backend.spiel.Brett;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public class Koenig extends Figur {

	public Koenig(){
	}
	public Koenig(Spiel spiel,boolean istWeiss) {
		super(spiel,"K",istWeiss);
	}

	@Override
	public ArrayList<String> getErlaubteZuege(){
		ArrayList<String> felder=new ArrayList<String>();
		if (getSpiel().weissSchachMatt()||getSpiel().schwarzSchachMatt()) return felder;
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
		// ROCHADE
		Figur figur;
		Brett brett=getSpiel().getBrett();
		if (!bereitsBewegt()){ // ich darf mich nicht bereits bewegt haben
			// 1. Rochade: lange
			figur=brett.getFeld(1,y).getFigur(); // der Turm muss noch da sein und durfte nicht bewegt worden sein
			if ((figur!=null)&&(figur instanceof Turm)&&(!figur.bereitsBewegt())){
				if (!getSpiel().istImSchach(istWeiss())){ // Rochade geht nur, wenn ich nicht gerade im Schach stehe
					if (istWeiss()){
						// Rochade zwischen a1 und e1
						if ((brett.getFeld("b1").getFigur()==null)&&(brett.getFeld("c1").getFigur()==null)&&(brett.getFeld("d1").getFigur()==null)){
							// König über kein Feld ziehen muss, das durch eine feindliche Figur bedroht wird:
							if ((!istFeldBedroht("c1",!istWeiss(),true))&&(!istFeldBedroht("d1",!istWeiss(),true)))
								addZug(felder,"c1"); // Koenig landet dann auf c1
						}
					}
					else{
						// Rochade zwischen a8 und e8
						if ((brett.getFeld("b8").getFigur()==null)&&(brett.getFeld("c8").getFigur()==null)&&(brett.getFeld("d8").getFigur()==null)){
							// König über kein Feld ziehen muss, das durch eine feindliche Figur bedroht wird:
							if ((!istFeldBedroht("c8",!istWeiss(),true))&&(!istFeldBedroht("d8",!istWeiss(),true)))
									addZug(felder,"c8"); // Koenig landet dann auf c8
						}						
					}
				}
			}
			// 2. Rochade: kurze
			figur=brett.getFeld(8,y).getFigur(); // der Turm muss noch da sein und durfte nicht bewegt worden sein
			if ((figur!=null)&&(figur instanceof Turm)&&(!figur.bereitsBewegt())){
				if (!getSpiel().istImSchach(istWeiss())){ // Rochade geht nur, wenn ich nicht gerade im Schach stehe
					if (istWeiss()){
						// Rochade zwischen e1 und h1
						if ((brett.getFeld("f1").getFigur()==null)&&(brett.getFeld("g1").getFigur()==null)){
							// König über kein Feld ziehen muss, das durch eine feindliche Figur bedroht wird:
							if ((!istFeldBedroht("f1",!istWeiss(),true))&&(!istFeldBedroht("g1",!istWeiss(),true)))
								addZug(felder,"g1"); // Koenig landet dann auf g1
						}						
					}
					else{
						// Rochade zwischen e8 und h8
						if ((brett.getFeld("f8").getFigur()==null)&&(brett.getFeld("g8").getFigur()==null)){
							// König über kein Feld ziehen muss, das durch eine feindliche Figur bedroht wird:
							if ((!istFeldBedroht("f8",!istWeiss(),true))&&(!istFeldBedroht("g8",!istWeiss(),true)))
								addZug(felder,"g8"); // Koenig landet dann auf g8
						}						
					}
				}
			}
		}
		return felder;
	}
	
	private boolean istFeldBedroht(String sFeld,boolean durchWeiss,boolean rochadenCheck){
		ArrayList<String> schlagbareFelder=getSpiel().getSchlagbareFelder(durchWeiss,true);
		return schlagbareFelder.contains(sFeld);
	}
}
