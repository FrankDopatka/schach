package backend.spiel;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import backend.Parameter;
import backend.figuren.Figur;

public class Feld {
	private boolean istWeiss=true;
	private String kuerzel;
	private int x;
	private int y;
	private Figur figur;
	
	public Feld(int x,int y,String kuerzel,boolean istWeiss){
		this.x=x;
		this.y=y;
		this.kuerzel=kuerzel;
		this.istWeiss=istWeiss;
	}
	
	public int getPosX() {
		return x;
	}

	public int getPosY() {
		return y;
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

	// ist auf diesem Feld eine Figur von derselben Farbe wie diese Figur?
	public boolean hatMeineFigur(Figur figur){ 
		if (figur==null) return false;
		if (getFigur()==null) return false;
		return (figur.istWeiss()==getFigur().istWeiss());
	}
	
	// ist auf diesem Feld eine gegnerische Figur?
	public boolean hatGegnerischeFigur(Figur figur){ 
		if (figur==null) return false;
		if (getFigur()==null) return false;
		return (figur.istWeiss()!=getFigur().istWeiss());
	}
	
	// ist auf diesem Feld eine Figur?
	public boolean hatFigur(){ 
		return (figur!=null);
	}
	
	public Figur getFigur() {
		return figur;
	}

	public void setFigur(Figur figur) {
		this.figur=figur;
	}
	
	public Image getBild(){
		if (figur!=null) return figur.getBild();
		Image im=new BufferedImage(Parameter.groesseFeld,Parameter.groesseFeld,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		if (istSchwarz())
			g.setColor(Parameter.farbeBrettSchwarz);
		else
			g.setColor(Parameter.farbeBrettWeiss);
		g.fillRect(0,0,im.getHeight(null),im.getWidth(null));
		g.dispose();
		return im;
	}
	
	@Override
	public String toString(){
		return kuerzel;
	}
}
