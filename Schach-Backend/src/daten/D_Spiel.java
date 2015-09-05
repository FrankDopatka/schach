package daten;

public class D_Spiel extends D {
	
	public D_Spiel(){
		addBool("weissAmZug",true);
		addInt("zugZaehler",0);
		addBool("weissImSchach",false);
		addBool("weissSchachMatt",false);
		addBool("schwarzImSchach",false);
		addBool("schwarzSchachMatt",false);
	}
	
}
