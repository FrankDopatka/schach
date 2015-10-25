package frontend;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import daten.D;
import daten.D_Zug;
import daten.FigurEnum;
import daten.Xml;
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

	private JPanel panelBrett=new JPanel();

	private JPanel panelHistorie=new JPanel();
	private JScrollPane jScrollerHistorie;
	private JTextArea jLog=new JTextArea();
	private JScrollPane jScrollerLog;
	private ArrayList<JButton> historieButtons=new ArrayList<JButton>();
	
	private EventHandler events=null;
	private BackendSpielStub backendSpiel=null;
	private BackendSpielAdminStub backendSpielAdmin=null;

	private boolean binWeiss=true;
	private int zugZaehler=-1;
	private boolean ende=false;

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

		// MENU
		JPanel panelMenu=new JPanel(); 
		panelMenu.setLayout(new BorderLayout());
		initialisiereMenu();
		panelMenu.add(menu,BorderLayout.NORTH);
		add(panelMenu,BorderLayout.NORTH);
		
		// SPIELBRETT
		panelBrett.setLayout(null);
		panelBrett.addMouseListener(events);
		brett.setLayout(null);
		brett.setOpaque(false);
		brett.setSize(445,540);
		panelBrett.add(brett);

		// HISTORIE
		panelHistorie.setLayout(new GridBagLayout());
		JPanel p=new JPanel();
		p.add(panelHistorie);
		jScrollerHistorie=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollerHistorie.setPreferredSize(new Dimension(300,400));

		// SPIELBRETT + HISTORIE EINTRAGEN
		JSplitPane splitter=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelBrett,jScrollerHistorie);
		splitter.setDividerLocation(450);
		add(splitter,BorderLayout.CENTER);

		// LOGGER
		jLog.setLineWrap(true);
		jScrollerLog=new JScrollPane(jLog,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollerLog.setPreferredSize(new Dimension(150,150));
		add(jScrollerLog,BorderLayout.SOUTH);
		
		setSize(800,750);
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
	
	public Frontend(String url,boolean binWeiss,KI ki) {
		this(url);
		if (ki==null)
			throw new RuntimeException("Es muss eine gueltige KI uebergeben werden!");
		ki.init(binWeiss,backendSpiel);
		ki.setFrontend(this);
		ki.start();
		String s="Franks Schach-Engine "+ki.getClass().getSimpleName();
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
	public boolean ichSpieleSchwarz(){
		return !ichSpieleWeiss();
	}
	
	public boolean ichBinAmZug(){
		return(ichSpieleWeiss()==(getZugZaehler()%2==0));
	}
	
	public int getZugZaehler() {
		return zugZaehler;
	}

	public void setZugZaehler(int zugZaehler) {
		this.zugZaehler = zugZaehler;
	}
	
	public void resetHistorie(){
		historieButtons.clear();
		panelHistorie.removeAll();
	}

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
				if (feld==null) continue;
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

	public void setBauerUmwandelnImGange() {
		JOptionPane.showMessageDialog(this,"Der Einfachheit halber bekommen Sie eine Dame.\nNormalerweise koennen Sie zwischen Dame, Turm, Laeufer oder Springer waehlen!",
		    "Bauernumwandlung!", JOptionPane.INFORMATION_MESSAGE);
		backendSpiel.bauerUmwandlung(""+FigurEnum.Dame);
	}

	public void updateLog() {
		ArrayList<D> zugHistorie=Xml.toArray(backendSpiel.getZugHistorie());
		panelHistorie.setVisible(false);
		resetHistorie();
		int x=0;
		int y=0;
		for(D datenwert:zugHistorie){
			D_Zug zug=(D_Zug)datenwert;
			GridBagConstraints cbg=new GridBagConstraints();
			cbg.fill=GridBagConstraints.HORIZONTAL;
			if (x==2) x=0;
			if (x%2==0) y++;
			cbg.gridx=x; cbg.gridy=y;
			x++;
			JButton b=new JButton(zug.toPGN());
			b.setBackground(new Color(200,200,200));
			b.setForeground(Color.BLACK);
			b.setHorizontalAlignment(SwingConstants.LEFT);
			panelHistorie.add(b,cbg);
		}
		//TODO Scrollt leider nicht automatisch nach unten
		panelHistorie.setVisible(true);
		jScrollerHistorie.validate();
		jScrollerHistorie.getVerticalScrollBar().setValue(jScrollerHistorie.getVerticalScrollBar().getMaximum());
		jScrollerHistorie.repaint();
	}
	
	public void resetLog(){
		jLog.setText("");
	}
	
	public void printLog(String text){
		jLog.setText(jLog.getText()+text);
	}

	public void printlnLog(String text){
		printLog(text+"\n");
	}
	
	public void log(String text){
		Date d=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		printLog(df.format(d)+": "+text+"\n");
	}

	public void setEnde(boolean ende) {
		this.ende=ende;
	}
	public boolean istZuEnde(){
		return ende;
	}
}
