package interfaces;

public interface iBackendSpiel {
	Object getBildWeiss();
	Object getBildSchwarz();
	String getFigur(String feld);
	String getErlaubteZuege(String feld);
	
	String ziehe(String feldVon,String feldNach);
	String bauerUmwandlung(String zuFigur);

	String getSpielDaten();
	String getZugHistorie();
}
