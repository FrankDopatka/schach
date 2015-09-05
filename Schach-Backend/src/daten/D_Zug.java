package daten;

public class D_Zug extends D{
	public D_Zug(){
		addInt("nummer",0);
		addString("figurBewegt","");
		addBool("figurBewegtIstWeiss",true);
		addString("figurGeschlagen","");
		addString("feldStart","");
		addString("feldZiel","");
		addString("zeitstempel","");
		addString("bemerkung1","");
		addString("bemerkungSpielzug","");
		addString("bemerkungSchach","");
	}
}
