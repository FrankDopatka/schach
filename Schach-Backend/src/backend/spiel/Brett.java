package backend.spiel;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import backend.Parameter;

public class Brett {
	private Feld[][] feld=new Feld[9][9];
	
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
	
	public Brett(){
		boolean setzeWeiss=false;
		for(int i=1;i<=8;i++){
			for(int j=1;j<=8;j++){
				feld[i][j]=new Feld(i,j,toZeichen(i)+j,setzeWeiss);
				setzeWeiss=!setzeWeiss;
			}
			setzeWeiss=!setzeWeiss;
		}
	}
	
	public Feld getFeld (int x,int y){
		return feld[x][y];
	}
	
	public Feld getFeld(String kuerzel){
		try{
			int x=Integer.parseInt(""+(kuerzel.toCharArray()[0]-96));
			int y=Integer.parseInt(kuerzel.substring(1));
			return getFeld(x,y);			
		}
		catch (Exception e){
			return null;
		}
	}

	public Image getBild(boolean vorneWeiss){
		int groesse=Parameter.groesseFeld;
		Image im=new BufferedImage(groesse*8,groesse*8,BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		if (vorneWeiss){
			for(int i=1;i<=8;i++){
				for(int j=1;j<=8;j++){
					g.drawImage(feld[i][j].getBild(),groesse*(i-1),groesse*(8-j),null);
				}
			}			
		}
		else{
			for(int i=1;i<=8;i++){
				for(int j=1;j<=8;j++){
					g.drawImage(feld[i][j].getBild(),groesse*(8-i),groesse*(j-1),null);
				}
			}						
		}
		g.dispose();
		return im;
	}
}
