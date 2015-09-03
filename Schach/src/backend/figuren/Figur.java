package backend.figuren;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import daten.D_Figur;
import daten.Xml;
import backend.Parameter;
import backend.spiel.Brett;
import backend.spiel.Feld;
import backend.spiel.Spiel;

public abstract class Figur {
	private static BufferedImage[] figurenWeiss=new BufferedImage[6];
	private static BufferedImage[] figurenSchwarz=new BufferedImage[6];
	private Spiel spiel;
	private boolean istWeiss=true;
	private boolean bereitsBewegt=false;
	private String kuerzel;
	private Feld feld=null; // null=geschlagen

	static{
		for(int i=0;i<figurenWeiss.length;i++){
			try {
				figurenWeiss[i]=ImageIO.read(new File(Parameter.pfadFiguren+i+"-w"+Parameter.endungFiguren));
				figurenSchwarz[i]=ImageIO.read(new File(Parameter.pfadFiguren+i+"-s"+Parameter.endungFiguren));	
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}	
		}
	}
	
	private static BufferedImage toBufferedImage(Image img){
		if (img instanceof BufferedImage) return (BufferedImage) img;
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();
    return bimage;
	}

	public Figur(){
	}
	
	public Figur(Spiel spiel,String kuerzel,boolean istWeiss){
		setSpiel(spiel);
		setKuerzel(kuerzel);
		setFarbe(istWeiss);
	}
	
	public void setSpiel(Spiel spiel){
		this.spiel=spiel;
	}
	public void setKuerzel(String kuerzel){
		this.kuerzel=kuerzel;
	}
	public void setFarbe(boolean istWeiss){
		this.istWeiss=istWeiss;
	}
	
	public ArrayList<String> getErlaubteZuege(boolean eigeneBewegungImGange){
		return new ArrayList<String>();
	}
	
	public boolean istZugErlaubt(String ziel){
		ArrayList<String> erlaubt=getErlaubteZuege(false);
		if (ziel==null) return false;
		if ((erlaubt==null)||(erlaubt.size()==0)) return false;
		for(String s:erlaubt){
			if (s.equals(ziel)) return true;
		}
		return false;
	}
	
	
	// =false: auf dieser Linie keine weiteren Zuege mehr erlaubt
	protected boolean addZug(ArrayList<String> felder,int x,int y){
		if ((x<1)||(x>8)||(y<1)||(y>8)) return false;
		Feld feldZiel=getSpiel().getBrett().getFeld(x,y);
		if (feldZiel.hatFigur()){
			if (feldZiel.hatGegnerischeFigur(this)){
				felder.add(Brett.toKuerzel(x,y));
			}
			return false;
		}
		else{
			felder.add(Brett.toKuerzel(x,y));
			return true;
		}
	}
	
	// die Zuege aus der Liste entfernen, durch die ich selbst im Schach waere
	protected void removeZuegeSelbstImSchach(ArrayList<String> felderZiel,Figur meineFigur){
		String posIch=meineFigur.getFeld().getKuerzel();
		ArrayList<String> felderZuEntfernen=new ArrayList<String>();
		for(String zugZiel:felderZiel){
			if (getSpiel().binIchImSchachDurchZug(posIch,zugZiel)) felderZuEntfernen.add(zugZiel);
		}
		felderZiel.removeAll(felderZuEntfernen);
	}

	public String getTyp(){
		return this.getClass().getSimpleName();
	}
	
	public Image getBild(){
		Image im=null;
		Graphics2D g=null;
		if (istGeschlagen()){
			im=new BufferedImage(Parameter.groesseFeld/2,Parameter.groesseFeld/2,BufferedImage.TYPE_INT_ARGB);
			g=(Graphics2D)im.getGraphics();
			g.setColor(Parameter.farbeBrettHintergrund);
		}
		else{
			im=new BufferedImage(Parameter.groesseFeld,Parameter.groesseFeld,BufferedImage.TYPE_INT_ARGB);
			g=(Graphics2D)im.getGraphics();
			if (feld.istSchwarz())
				g.setColor(Parameter.farbeBrettSchwarz);
			else 
				g.setColor(Parameter.farbeBrettWeiss);
		}
		g.fillRect(0,0,im.getHeight(null),im.getWidth(null));
		BufferedImage bildFigur=null;
		FigurEnum figurTyp=FigurEnum.valueOf(this.getClass().getSimpleName());
		if (istWeiss)
			bildFigur=figurenWeiss[figurTyp.ordinal()];
		else
			bildFigur=figurenSchwarz[figurTyp.ordinal()];
		if (istGeschlagen())
			bildFigur=toBufferedImage(bildFigur.getScaledInstance(bildFigur.getWidth()/2,bildFigur.getHeight()/2,Image.SCALE_SMOOTH));
		g.drawImage(bildFigur,2,2,null);
		g.dispose();
		return im;
	}

	public String getKuerzel() {
		return kuerzel;
	}
	
	public boolean istWeiss() {
		return istWeiss;
	}

	public boolean istSchwarz() {
		return !istWeiss;
	}

	public Feld getFeld() {
		return feld;
	}

	public void setFeld(Feld feld) { // feld=null -> geschlagen
		if (this.feld!=null) this.feld.setFigur(null);
		if (feld!=null) feld.setFigur(this);
		this.feld = feld;
	}
	
	public boolean istGeschlagen(){
		return this.getFeld()==null;
	}
	
	public boolean bereitsBewegt(){
		return bereitsBewegt;
	}
	
	public void wurdeBewegt(){
		bereitsBewegt=true;
	}
	
	@Override
	public String toString(){
		String s=getTyp();
		if (istWeiss())
			s+=" weiss";
		else
			s+=" schwarz";
		if (istGeschlagen())
			s+=" geschlagen";
		else
			s+=" auf Feld "+feld.toString();
		return s;
	}
	
	public Spiel getSpiel() {
		return spiel;
	}

	public D_Figur toD(){
		D_Figur d_Figur=new D_Figur();
		d_Figur.setString("typ",getTyp());
		d_Figur.setString("kuerzel",getKuerzel());
		d_Figur.setBool("istWeiss",istWeiss());
		d_Figur.setBool("istGeschlagen",istGeschlagen());
		d_Figur.setBool("bereitsBewegt",bereitsBewegt());
		if (!istGeschlagen()) d_Figur.setString("feld",feld.toString());
		return d_Figur;
	}

	public String toXml() {
		return Xml.fromD(toD());
	}
}
