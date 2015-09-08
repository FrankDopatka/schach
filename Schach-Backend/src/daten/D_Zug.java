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
		addString("bemerkungSpielzug","");
		addString("bemerkungSchach","");
		addString("bauernumwandlungFigurNeu","");
	}
	
	public String toPGN(){
		String s="";
		int nr=getInt("nummer");
		if (nr%2!=0){
			s+=((nr-1)/2)+1+". ";
		}
		String zug=getString("bemerkungSpielzug");
		if (zug.equals(""+ZugEnum.Rochade)){
			// Rochaden
			if ((getString("feldZiel").equals("g1")||(getString("feldZiel").equals("g8"))))
				s+="0-0";
			else
				s+="0-0-0";
		}
		else{
			s+=getString("figurBewegt");
			s+=getString("feldStart");
			// Figur geschlagen
			if (getString("figurGeschlagen").equals(""))
				s+="-";
			else
				s+="x";
			s+=getString("feldZiel");

			// en passant
			if (zug.equals(""+ZugEnum.EnPassant)) s+=" e.p.";	
			// Bauernumwandlung
			if (zug.equals(""+ZugEnum.BauerUmwandlung)) s+=getString("bauernumwandlungFigurNeu");
		}
		// Schach und Patt
		String schach=getString("bemerkungSchach");
		if (!schach.equals("")){
			if (schach.equals(""+ZugEnum.WeissImSchach)||schach.equals(""+ZugEnum.SchwarzImSchach)){
				s+="+";				
			}
			else if (schach.equals(""+ZugEnum.WeissSchachMatt)||schach.equals(""+ZugEnum.SchwarzSchachMatt)){
				s+="++";				
			}
			else if (schach.equals(""+ZugEnum.Patt)){
				s+="=";				
			}
		}
		s+=" ";
		return s;
	}
}
