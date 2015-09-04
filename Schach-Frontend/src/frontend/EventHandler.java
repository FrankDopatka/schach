package frontend;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import daten.D;
import daten.D_Figur;
import daten.Xml;
import backend.BackendSpielAdminStub;
import backend.BackendSpielStub;


public class EventHandler implements ActionListener,MouseListener{
	private Frontend frontend;
	private BackendSpielStub backendSpiel=null;
	private BackendSpielAdminStub backendSpielAdmin=null;
	private String feldMarkiert=null;
	private ArrayList<String> felderErlaubt=new ArrayList<String>();
	
	public EventHandler(Frontend frontend){
		this.frontend=frontend;
		backendSpiel=frontend.getBackendSpiel();
		backendSpielAdmin=frontend.getBackendSpielAdmin();
	}
	
	public void reset(){
		this.feldMarkiert=null;
		this.felderErlaubt=new ArrayList<String>();
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		Object quelle=ev.getSource();
		if (quelle.equals(frontend.mSpielNeu)){
			backendSpielAdmin.neuesSpiel();
		} 
		if (quelle.equals(frontend.mSpielLaden)){
			backendSpielAdmin.ladenSpiel("spiel.xml");
		}
		if (quelle.equals(frontend.mSpielSpeichern)){
			backendSpielAdmin.speichernSpiel("spiel.xml");
		}
		if (frontend.ichSpieleWeiss())
			frontend.setBrett(backendSpiel.getBildWeiss());
		else
			frontend.setBrett(backendSpiel.getBildSchwarz());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x=getKoordinateX(e.getX());
		int y=getKoordinateY(e.getY());
		if ((x==0)||(y==0)) return;
		if ((felderErlaubt!=null)&&felderErlaubt.contains(Frontend.toKuerzel(x,y))){
			// 2. Klick auf ein Feld: Zug durchfuehren
			backendSpiel.ziehe(feldMarkiert,Frontend.toKuerzel(x,y));
			if (frontend.ichSpieleWeiss())
				frontend.setBrett(backendSpiel.getBildWeiss());
			else
				frontend.setBrett(backendSpiel.getBildSchwarz());
			reset();
		}
		else{
			// 1. Klick auf ein Feld
	 		D_Figur d_Figur=null;
			D d=Xml.toD(backendSpiel.getFigur(Frontend.toKuerzel(x,y)));
			if (d instanceof D_Figur) d_Figur=(D_Figur)d;
			if ((d_Figur==null)||(frontend.ichSpieleWeiss()!=d_Figur.getBool("istWeiss"))){
				// das Feld hat keine Figur oder eine Figur der anderen Farbe -> ich darf nicht ziehen.
				frontend.markiereFelder(x,y,null);
				this.feldMarkiert=Frontend.toKuerzel(x,y);
				this.felderErlaubt=null;
			}
			else{
				// das Feld hat eine Figur -> moegliche Zuege ermittelm
				String xml=backendSpiel.getErlaubteZuege(Frontend.toKuerzel(x,y));
				ArrayList<D> d_erlaubteZuege=Xml.toArray(xml);
				ArrayList<String> sFelderErlaubt=new ArrayList<String>();
				for(D d_Zug:d_erlaubteZuege){
					sFelderErlaubt.add(d_Zug.getString("feldZiel"));
				}
				frontend.markiereFelder(x,y,sFelderErlaubt);
				this.feldMarkiert=Frontend.toKuerzel(x,y);
				this.felderErlaubt=sFelderErlaubt;
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	private int getKoordinateX(int x){
		int start=20;
		int offset=50;
		for (int i=1;i<=8;i++){
			if ((x>=start)&&(x<=start+50)){
				if (frontend.ichSpieleWeiss())
					return i;
				else
					return 9-i;
			}
			start+=offset;
		}			
		return 0;
	}

	private int getKoordinateY(int y){
		int start=472;
		int offset=50;
		for (int i=1;i<=8;i++){
			if ((y<=start)&&(y>=start-50))
				if (frontend.ichSpieleWeiss())
					return i;
				else
					return 9-i;
			start-=offset;
		}
		return 0;
	}
}
