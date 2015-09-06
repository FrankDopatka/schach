package backend;

import interfaces.iBackendSpielAdmin;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.ResourceConfig;

import backend.spiel.Spiel;
import daten.D_Fehler;
import daten.D_OK;
import daten.Xml;

@Path("schach/spiel/admin")
public class BackendSpielAdmin extends ResourceConfig implements iBackendSpielAdmin{

	@GET
	@Path("neuesSpiel")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String neuesSpiel(){
		try {
			Spiel spiel=new Spiel();
			spiel.getRegelwerk().setzeStartbelegung();
			BackendSpiel.setSpiel(spiel);
			return Xml.verpacken(Xml.fromD(new D_OK()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}

	@GET
	@Path("ladenSpiel/{pfad}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String ladenSpiel(
			@PathParam("pfad")String pfad) {
		try{
			Spiel spiel=new Spiel(pfad);
			BackendSpiel.setSpiel(spiel);
			return Xml.verpacken(Xml.fromD(new D_OK()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}		
	}
	
	@GET
	@Path("speichernSpiel/{pfad}")
	@Consumes("text/plain")
	@Produces("application/xml")
	@Override
	public String speichernSpiel(
			@PathParam("pfad")String pfad) {
		try {
			if (!pfad.endsWith(".xml")) pfad=pfad+".xml";
			BackendSpiel.getSpiel().speichern(pfad);
			return Xml.verpacken(Xml.fromD(new D_OK()));
		} catch (Exception e) {
			e.printStackTrace();
			return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
		}
	}
}
