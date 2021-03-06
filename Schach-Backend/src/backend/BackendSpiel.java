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
	public Object getBildWeiss(){
	  try {
		  ByteArrayOutputStream baos=new ByteArrayOutputStream();
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
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
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			ImageIO.write(spiel.getBildSchwarz(),"png",baos);
		  byte[] imageData=baos.toByteArray();
		  return Response.ok(new ByteArrayInputStream(imageData)).build();
		} catch (Exception e) {
		  return Response.serverError().build();
		}
	}
	
	@GET
	@Path("getAlleFiguren")
	@Produces("application/xml")
	@Override
	public String getAlleFiguren() {
		try{
			StringBuffer xml=new StringBuffer(); 
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			ArrayList<Figur> figuren=spiel.getFiguren();
			for(Figur figur:figuren){
				xml.append(figur.toXml());
			}
			return Xml.verpacken(xml.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}
	
	@GET
	@Path("getFigurenAufFeld/{weiss}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String getFigurenAufFeld(
			@PathParam("weiss") boolean weiss) {
		try{
			StringBuffer xml=new StringBuffer(); 
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			ArrayList<Figur> figuren=spiel.getRegelwerk().getFigurenAufFeld(weiss);
			for(Figur figur:figuren){
				xml.append(figur.toXml());
			}
			return Xml.verpacken(xml.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@GET
	@Path("getGeschlageneFiguren/{weiss}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String getGeschlageneFiguren(
			@PathParam("weiss") boolean weiss) {
		try{
			StringBuffer xml=new StringBuffer(); 
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			ArrayList<Figur> figuren=spiel.getRegelwerk().getGeschlageneFiguren(weiss);
			for(Figur figur:figuren){
				xml.append(figur.toXml());
			}
			return Xml.verpacken(xml.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
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
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
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
	@Path("getKoenig/{weiss}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String getKoenig(
			@PathParam("weiss") boolean weiss) {
		try{
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			Figur figur=spiel.getKoenig(weiss);
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
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			Feld feld=spiel.getBrett().getFeld(kuerzel);
			if (feld==null)
				throw new RuntimeException("getFigur(): Das Feld "+kuerzel+" existiert nicht!");
			Figur figur=feld.getFigur();
			if (figur==null)
				return Xml.verpacken((new D_OK("null").toXml()));
			ArrayList<D> dZuege=new ArrayList<D>();
			ArrayList<String> sZuege=figur.getErlaubteZuege();
			figur.removeZuegeSelbstImSchach(sZuege,figur);
			for(String feldZiel:sZuege){
				D_Zug d_zug=new D_Zug();
				d_zug.setString("figur",figur.getKuerzel());
				d_zug.setString("feldStart",figur.getFeld().getKuerzel());
				d_zug.setString("feldZiel",feldZiel);
				Figur figurZiel=spiel.getBrett().getFeld(feldZiel).getFigur();
				if (figurZiel!=null)
					d_zug.setString("figurGeschlagen",figurZiel.getKuerzel());
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
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			spiel.getRegelwerk().ziehe(feldVon,feldNach);
			return Xml.verpacken((new D_OK().toXml()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}
	
	@GET
	@Path("bauerUmwandlung/{zuFigur}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String bauerUmwandlung(
			@PathParam("zuFigur") String zuFigur) {
		try{
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			spiel.getRegelwerk().bauernUmwandlung(zuFigur);
			return Xml.verpacken(Xml.fromD(spiel.toD()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@GET
	@Path("getSpielDaten")
	@Produces("application/xml")
	@Override
	public String getSpielDaten() {
		try{
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			return Xml.verpacken(Xml.fromD(spiel.toD()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@GET
	@Path("getZugHistorie")
	@Produces("application/xml")
	@Override
	public String getZugHistorie() {
		try{
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			return Xml.verpacken(Xml.fromArray(spiel.getZugHistorie()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@Override
	public String getLetzterZug() {
		try{
			if (spiel==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch kein Spiel!")));
			D zug=spiel.getLetzterZug();
			if (zug==null) return Xml.verpacken(Xml.fromD(new D_Fehler("Es gibt noch keinen Zug in diesem Spiel!")));
			return Xml.verpacken(zug.toXml());
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}
}
