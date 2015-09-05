package frontend;

import backend.BackendSpielStub;
import daten.*;

public class Updater extends Thread{
	private Frontend frontend;
	private BackendSpielStub backendSpiel;
	private int timer;
	
	public Updater(Frontend frontend,int timer){
		this.frontend=frontend;
		backendSpiel=frontend.getBackendSpiel();
		this.timer=timer;
		this.start();
	}
	
	@Override
	public void run(){
		while(true){
			try{
				D_Spiel d_spiel=(D_Spiel)Xml.toD(backendSpiel.getSpielDaten());
				int zaehlerServer=d_spiel.getInt("zugZaehler");
				int zaehlerClient=frontend.getZugZaehler();
				if (zaehlerClient!=zaehlerServer){
					// komplettes Update noetig
					if (frontend.ichSpieleWeiss())
						frontend.setBrett(backendSpiel.getBildWeiss());
					else
						frontend.setBrett(backendSpiel.getBildSchwarz());
					
					if (d_spiel.getBool("weissImSchach")) System.out.println("SCHACH WEISS!");
					if (d_spiel.getBool("schwarzImSchach")) System.out.println("SCHACH SCHWARZ!");
					if (d_spiel.getBool("weissSchachMatt")) System.out.println("SCHACHMATT WEISS!");
					if (d_spiel.getBool("schwarzSchachMatt")) System.out.println("SCHACHMATT SCHWARZ!");
					
					frontend.setZugZaehler(zaehlerServer);
				}
				Thread.sleep(timer*1000);
			}
			catch (Exception e){}
		}
	}
}
