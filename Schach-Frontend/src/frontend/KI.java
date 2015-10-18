package frontend;

import daten.D;
import daten.D_Spiel;
import daten.Xml;
import daten.ZugEnum;
import backend.BackendSpielStub;

public abstract class KI extends Thread{
	private String info;
	private boolean binWeiss;
	private BackendSpielStub backendSpiel;
	private int pause=1000;
	private boolean ende=false;

	public KI(String info) {
		this.info=info;
	}

	public void init(boolean binWeiss, BackendSpielStub backendSpiel){
		this.binWeiss=binWeiss;
		this.backendSpiel=backendSpiel;
	}
	
	public String getInfo() {
		return info;
	}
	
	public boolean binWeiss(){
		return binWeiss;
	}
	public boolean binSchwarz(){
		return !binWeiss();
	}
	
	public BackendSpielStub getBackend(){
		return backendSpiel;
	}
	
	public void schlafen(int ms){
		try {
			Thread.sleep(ms);
		} catch (Exception e) {}
	}

	@Override
	public void start() {
		super.start();		
	}
	
	@Override
	public void run(){
		while (!ende){
			try {
				D d=Xml.toD(getBackend().getSpielDaten());
				D_Spiel d_Spiel=(D_Spiel)d;
				String bemerkungSchach=d_Spiel.getString("bemerkungSchach");
				boolean weissMatt=bemerkungSchach.equals(""+ZugEnum.WeissSchachMatt);
				boolean schwarzMatt=bemerkungSchach.equals(""+ZugEnum.SchwarzSchachMatt);
				boolean patt=bemerkungSchach.equals(""+ZugEnum.Patt);
				
				
				if (weissMatt||schwarzMatt||patt){
					// Spiel ist zu Ende
					if (binWeiss()&&weissMatt) 
						ichHabeVerloren();
					else if (binSchwarz()&&schwarzMatt) 
						ichHabeVerloren();
					else if (patt) 
						patt();
					else
						ichHabeGewonnen();
					ende=true;
				}
				else{
					// Spiel geht weiter
					if (d_Spiel.getBool("weissAmZug")==binWeiss())
						ichBinAmZug();
					else
						ichBinNichtZug();					
					schlafen(pause);				
				}
			} catch (Exception e) {
				schlafen(pause);				
			} 
		}
	}
	
	public abstract void ichBinAmZug();
	
	public abstract void ichBinNichtZug();	

	public abstract void ichHabeVerloren();	

	public abstract void ichHabeGewonnen();	

	public abstract void patt();	
}

