package frontend;

import java.util.ArrayList;

import backend.BackendSpielStub;
import daten.*;

public class Updater extends Thread{
	private Frontend frontend;
	private BackendSpielStub backendSpiel;
	private int timer;
	private boolean bauernUmwandlungImGange=false;
	
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
				String bemerkungSpielzug=d_spiel.getString("bemerkungSpielzug");
				String bemerkungSchach=d_spiel.getString("bemerkungSchach");
				if (zaehlerClient!=zaehlerServer){
					update(bemerkungSchach,bemerkungSpielzug,zaehlerServer);					
				}
				else if (bemerkungSpielzug.equals(""+ZugEnum.BauerUmwandlungImGange)&&(!bauernUmwandlungImGange)){
					if (frontend.ichSpieleWeiss()==d_spiel.getBool("weissAmZug")) frontend.setBauerUmwandelnImGange();	
					bauernUmwandlungImGange=true;
				}
				else if ((bauernUmwandlungImGange)&&(bemerkungSpielzug.equals(""+ZugEnum.BauerUmwandlung))){
					update(bemerkungSchach,bemerkungSpielzug,zaehlerServer);
					bauernUmwandlungImGange=false;
				}
				Thread.sleep(timer*1000);
			}
			catch (Exception e){}
		}
	}
	
	public void update(String bemerkungSchach,String bemerkungSpielzug,int zaehlerServer){
		if (frontend.ichSpieleWeiss())
			frontend.setBrett(backendSpiel.getBildWeiss());
		else
			frontend.setBrett(backendSpiel.getBildSchwarz());
		if (bemerkungSchach.equals(""+ZugEnum.Patt)) System.out.println("PATT!");
		frontend.setZugZaehler(zaehlerServer);
		frontend.updateLog();
	}
}
