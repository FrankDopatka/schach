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

import backend.figuren.Figur;
import backend.spiel.Spiel;


public class Gui extends JFrame{
	private static final long serialVersionUID = 1L;
	public static final JMenuItem mSpielNeu=new JMenuItem("Neu");
	public static final JMenuItem mSpielLaden=new JMenuItem("Laden");
	public static final JMenuItem mSpielSpeichern=new JMenuItem("Speichern");

	private JLabel brett=new JLabel();
	private BufferedImage brettBild=null;
	private JPanel center=new JPanel();
	private JPanel west=new JPanel();
	private JPanel ost=new JPanel();
	private JPanel nord=new JPanel();
	private JPanel sued=new JPanel();
	private Spiel spiel=null;
	private String feldMarkiert=null;
	private ArrayList<String> felderErlaubt=new ArrayList<String>();
	private GuiEventHandler events=new GuiEventHandler(this);
	private JMenuBar menu=new JMenuBar();
	private JMenu mSpiel=new JMenu("Spiel");

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

	
	public Gui(){
		super("Franks Schach-Engine");
		JPanel panelHaupt=new JPanel(); 
		JPanel panelMenu=new JPanel(); 
		panelHaupt.setLayout(new BorderLayout());
		panelMenu.setLayout(new BorderLayout());
		
		mSpiel.add(mSpielNeu);
		mSpielNeu.addActionListener(events);
		mSpiel.add(mSpielLaden);
		mSpielLaden.addActionListener(events);
		mSpiel.add(mSpielSpeichern);
		mSpielSpeichern.addActionListener(events);
		menu.add(mSpiel);
		
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
		
		neuesSpiel();
		spiel.setzeStartbelegung();
		setBrett(spiel.getBild());
	}

	public void setBrett(BufferedImage brettBild){
		this.brettBild=brettBild;
		updateBrett(brettBild);
	}
	
	public void updateBrett(Image bildNeu){
		brett.setIcon(new ImageIcon(bildNeu));		
	}
	
	public void neuesSpiel(){
		spiel=new Spiel();
	}

	public Spiel getSpiel(){
		return spiel;
	}
	
	public void ladenSpiel(String pfad){
		spiel=new Spiel(pfad);
		setBrett(spiel.getBild());
	}

	public void klick(int x,int y){
		if ((x==0)||(y==0)) return;
		if ((felderErlaubt!=null)&&felderErlaubt.contains(toKuerzel(x,y))){
			spiel.ziehe(feldMarkiert, toKuerzel(x,y));
			setBrett(spiel.getBild());
			this.feldMarkiert=null;
			this.felderErlaubt=new ArrayList<String>();
		}
		else{
	 		Figur figur=spiel.getBrett().getFeld(x,y).getFigur();
			if (figur==null){
				markiereFelder(toKuerzel(x,y),null);				
			}
			else{
				markiereFelder(toKuerzel(x,y),figur.getErlaubteZuege(true));
			}
		}		
	}
	
	public void markiereFelder(String feldMarkiert,ArrayList<String> felderErlaubt){
		this.feldMarkiert=feldMarkiert;
		this.felderErlaubt=felderErlaubt;
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
				viereck=getFeldGrenzen(xFeldErlaubt,yFeldErlaubt);
				g.setColor(new Color(255,255,0));
				g.drawRect(viereck[0],viereck[1],50,50);
			}			
		}
		g.setColor(new Color(255,0,0));
		viereck=getFeldGrenzen(xFeld,yFeld);
		g.drawRect(viereck[0],viereck[1],50,50);
		g.dispose();
		updateBrett(im);
	}
	
	private int[] getFeldGrenzen(int x,int y){
		int[] erg=new int[4];
		erg[0]=(x-1)*50+20; // x1
		erg[2]=erg[0]+50; // x2
		erg[3]=470-(y-1)*50; // y2
		erg[1]=erg[3]-50; // y1
		return erg;
	}
}
