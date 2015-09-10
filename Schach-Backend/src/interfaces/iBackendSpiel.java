package interfaces;

public interface iBackendSpiel {
	Object getBildWeiss();
	Object getBildSchwarz();
	
	String getAlleFiguren();
	String getFigurenAufFeld(boolean weiss);
	String getGeschlageneFiguren(boolean weiss);
	String getFigur(String feld);
	String getKoenig(boolean weiss);
	String getErlaubteZuege(String feld);
	
	String ziehe(String feldVon,String feldNach);
	String bauerUmwandlung(String zuFigur);
	
	String getSpielDaten();
	String getZugHistorie();
	String getLetzterZug();
}
