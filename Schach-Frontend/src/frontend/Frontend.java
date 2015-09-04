package frontend;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import backend.BackendSpielAdminStub;
import backend.BackendSpielStub;

public class Frontend extends JFrame{
	public static final int updateInterval=1;

	private static final long serialVersionUID = 1L;

	private JMenuBar menu=new JMenuBar();
	private JMenu mSpiel=new JMenu("Spiel");
	public final JMenuItem mSpielNeu=new JMenuItem("Neu");
	public final JMenuItem mSpielLaden=new JMenuItem("Laden");
	public final JMenuItem mSpielSpeichern=new JMenuItem("Speichern");
	private JMenu mVerwaltung=new JMenu("Verwaltung");
	public final JMenuItem mVerwaltungEinstellungen=new JMenuItem("Einstellungen");
	public final JMenuItem mVerwaltungInfo=new JMenuItem("Info");

	private JLabel brett=new JLabel();
	private BufferedImage brettBild=null;
	private JPanel center=new JPanel();
	private JPanel west=new JPanel();
	private JPanel ost=new JPanel();
	private JPanel nord=new JPanel();
	private JPanel sued=new JPanel();
	
	private EventHandler events=null;
	private BackendSpielStub backendSpiel=null;
	private BackendSpielAdminStub backendSpielAdmin=null;
	
	private boolean binWeiss=true;
	private int zugZaehler=-1;

	public static String toZeichen(int wert){
		return ""+(char)(96+wert);
	}
	
	public static String toKuerzel(int x,int y){
		return toZeichen(x)+y;
	}
	
	public static int[] fromKuerzel(String kuerzel){ //x,y
		int[] ergebnis=new int[2];
		ergebnis[0]=((int)kuerzel.toCharArray()[0])-96;
		ergebnis[1]=Integer.parseInt(""+kuerzel.toCharArray()[1]);
		return ergebnis;
	}
	
	public static BufferedImage kopiereBild(BufferedImage quelle){
    BufferedImage kopie=new BufferedImage(quelle.getWidth(),quelle.getHeight(),quelle.getType());
    Graphics g=kopie.getGraphics();
    g.drawImage(quelle,0,0,null);
    g.dispose();
    return kopie;
	}

	private Frontend(String url){
		backendSpiel=new BackendSpielStub(url);
		backendSpielAdmin=new BackendSpielAdminStub(url);
		events=new EventHandler(this);
		
		JPanel panelHaupt=new JPanel(); 
		JPanel panelMenu=new JPanel(); 
		panelHaupt.setLayout(new BorderLayout());
		panelMenu.setLayout(new BorderLayout());

		initialisiereMenu();
		panelMenu.add(menu,BorderLayout.NORTH);
		
		center.setLayout(null);
		center.addMouseListener(events);
		
		brett.setLayout(null);
		brett.setOpaque(false);
		brett.setSize(445,540);
		center.add(brett);

		sued.add(new JButton("HALLO?"));
		west.add(new JButton("HALLO?"));
		ost.add(new JButton("HALLO?"));

		panelHaupt.add(nord,BorderLayout.NORTH);
		panelHaupt.add(sued,BorderLayout.SOUTH);
		panelHaupt.add(west,BorderLayout.WEST);
		panelHaupt.add(ost,BorderLayout.EAST);
		panelHaupt.add(center,BorderLayout.CENTER);
		
		add(panelMenu,BorderLayout.NORTH);
		add(panelHaupt,BorderLayout.CENTER);
		setSize(650,750);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Frontend(String url,boolean binWeiss) {
		this(url);
		String s="Franks Schach-Engine ";
		this.binWeiss=binWeiss;
		if (binWeiss)
			setTitle(s+" - Spieler WEISS");
		else
			setTitle(s+" - Spieler SCHWARZ");
		new Updater(this,updateInterval);
	}
	
	public void setBrett(BufferedImage brettBild){
		this.brettBild=brettBild;
		updateBrett(brettBild);
	}
	
	public void updateBrett(Image bildNeu){
		brett.setIcon(new ImageIcon(bildNeu));		
	}
	
	public BackendSpielStub getBackendSpiel(){
		return backendSpiel;
	}
	
	public BackendSpielAdminStub getBackendSpielAdmin(){
		return backendSpielAdmin;
	}

	public EventHandler getEventHandler(){
		return events;
	}
	
	public boolean ichSpieleWeiss(){
		return binWeiss;
	}
	/*
	public void neuesSpiel(){
		spiel=new Spiel();
	}
	
	public void ladenSpiel(String pfad){
		spiel=new Spiel(pfad);
		setBrett(spiel.getBild());
	}
*/

	public void markiereFelder(int x,int y,ArrayList<String> felderErlaubt){
		markiereFelder(toKuerzel(x,y),felderErlaubt);
	}

	public void markiereFelder(String feldMarkiert,ArrayList<String> felderErlaubt){
		int xFeld=fromKuerzel(feldMarkiert)[0];
		int yFeld=fromKuerzel(feldMarkiert)[1];
		if ((xFeld==0)||(yFeld==0)) return;
		int[] viereck=new int[4];
		BufferedImage im=kopiereBild(brettBild);
		Graphics2D g=(Graphics2D) im.getGraphics();
		g.setStroke(new BasicStroke(3));
		if ((felderErlaubt!=null)&&(felderErlaubt.size()>0)){
			for(String feld:felderErlaubt){
				int xFeldErlaubt=fromKuerzel(feld)[0];
				int yFeldErlaubt=fromKuerzel(feld)[1];
				viereck=getFeldStart(xFeldErlaubt,yFeldErlaubt);
				g.setColor(new Color(255,255,0));
				g.drawRect(viereck[0],viereck[1],50,50);
			}			
		}
		g.setColor(new Color(255,0,0));
		viereck=getFeldStart(xFeld,yFeld);
		g.drawRect(viereck[0],viereck[1],50,50);
		g.dispose();
		updateBrett(im);
	}
	
	private int[] getFeldStart(int x,int y){
		int[] erg=new int[4];
		if (ichSpieleWeiss()){
			erg[0]=(x-1)*50+20; // x1
			erg[1]=420-(y-1)*50; // y1			
		}
		else{
			erg[0]=470-((x+1)*50); // x1
			erg[1]=y*50+20; // y1			
		}
		return erg;
	}
	
	private void initialisiereMenu(){
		mSpiel.add(mSpielNeu); mSpielNeu.addActionListener(events);
		mSpiel.add(mSpielLaden); mSpielLaden.addActionListener(events);
		mSpiel.add(mSpielSpeichern); mSpielSpeichern.addActionListener(events);
		menu.add(mSpiel);
		mVerwaltung.add(mVerwaltungEinstellungen); mVerwaltungEinstellungen.addActionListener(events);
		mVerwaltung.add(mVerwaltungInfo); mVerwaltungInfo.addActionListener(events);
		menu.add(mVerwaltung);
	}

	public int getZugZaehler() {
		return zugZaehler;
	}

	public void setZugZaehler(int zugZaehler) {
		this.zugZaehler = zugZaehler;
	}

}
