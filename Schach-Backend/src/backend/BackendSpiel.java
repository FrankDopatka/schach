package backend;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import interfaces.iBackendSpiel;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;

import daten.*;
import backend.figuren.Figur;
import backend.spiel.Feld;
import backend.spiel.Spiel;

@Path("schach/spiel")
public class BackendSpiel extends ResourceConfig implements iBackendSpiel{
	private static Spiel spiel;

	public BackendSpiel(){
	}
	
	public static Spiel getSpiel(){
		return BackendSpiel.spiel;
	}
	
	public static void setSpiel(Spiel spiel){
		BackendSpiel.spiel=spiel;
	}
	
	@GET
	@Path("getBildWeiss")
	@Produces("image/png")
	@Override
	public Response getBildWeiss(){
	  try {
		  ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(spiel.getBildWeiss(),"png",baos);
		  byte[] imageData=baos.toByteArray();
		  return Response.ok(new ByteArrayInputStream(imageData)).build();
		} catch (Exception e) {
		  return Response.serverError().build();
		}
	}
	

	@GET
	@Path("getBildSchwarz")
	@Produces("image/png")
	@Override
	public Object getBildSchwarz() {
	  try {
		  ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(spiel.getBildSchwarz(),"png",baos);
		  byte[] imageData=baos.toByteArray();
		  return Response.ok(new ByteArrayInputStream(imageData)).build();
		} catch (Exception e) {
		  return Response.serverError().build();
		}
	}

	@GET
	@Path("getFigur/{feld}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String getFigur(
			@PathParam("feld")String kuerzel) {
		try{
			Feld feld=spiel.getBrett().getFeld(kuerzel);
			if (feld==null)
				throw new RuntimeException("getFigur(): Das Feld "+kuerzel+" existiert nicht!");
			Figur figur=feld.getFigur();
			if (figur==null)
				return Xml.verpacken((new D_OK("null").toXml()));
			return Xml.verpacken(figur.toXml());
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@GET
	@Path("getErlaubteZuege/{feld}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String getErlaubteZuege(
			@PathParam("feld")String kuerzel) {
		try{
			Feld feld=spiel.getBrett().getFeld(kuerzel);
			if (feld==null)
				throw new RuntimeException("getFigur(): Das Feld "+kuerzel+" existiert nicht!");
			Figur figur=feld.getFigur();
			if (figur==null)
				return Xml.verpacken((new D_OK("null").toXml()));
			ArrayList<D> dZuege=new ArrayList<D>();
			ArrayList<String> sZuege=figur.getErlaubteZuege();
			figur.removeZuegeSelbstImSchach(sZuege,figur);
			for(String sZug:sZuege){
				D_Zug d_zug=new D_Zug();
				d_zug.setString("figur",figur.getKuerzel());
				d_zug.setString("feldStart",figur.getFeld().getKuerzel());
				d_zug.setString("feldZiel",sZug);
				dZuege.add(d_zug);
			}
			return Xml.verpacken(Xml.fromArray(dZuege));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@GET
	@Path("ziehe/{feldVon}/{feldNach}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String ziehe(
			@PathParam("feldVon") String feldVon,
			@PathParam("feldNach") String feldNach){
		try{
			spiel.getRegelwerk().ziehe(feldVon,feldNach);
			return Xml.verpacken((new D_OK().toXml()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@GET
	@Path("getSpielDaten")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String getSpielDaten() {
		try{
			return Xml.verpacken(Xml.fromD(spiel.toD()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}
}
