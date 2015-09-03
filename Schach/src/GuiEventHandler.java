import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class GuiEventHandler implements ActionListener,MouseListener{
	private Gui gui;
	
	public GuiEventHandler(Gui gui){
		this.gui=gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		Object quelle=ev.getSource();
		if (quelle.equals(Gui.mSpielNeu)){
			gui.neuesSpiel();
			gui.getSpiel().setzeStartbelegung();
			gui.setBrett(gui.getSpiel().getBild());
		} 
		if (quelle.equals(Gui.mSpielLaden)){
			gui.ladenSpiel("spiel.xml");
		}
		if (quelle.equals(Gui.mSpielSpeichern)){
			System.out.println(gui.getSpiel().speichern("spiel.xml"));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		gui.klick(getKoordinateX(x),getKoordinateY(y));
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
			if ((x>=start)&&(x<=start+50)) return i;
			start+=offset;
		}
		return 0;
	}

	private int getKoordinateY(int y){
		int start=472;
		int offset=50;
		for (int i=1;i<=8;i++){
			if ((y<=start)&&(y>=start-50)) return i;
			start-=offset;
		}
		return 0;
	}
}
