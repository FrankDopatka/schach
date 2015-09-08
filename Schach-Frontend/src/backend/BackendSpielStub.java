package backend;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import daten.D;
import daten.Xml;
import interfaces.iBackendSpiel;

public class BackendSpielStub implements iBackendSpiel{
	private static final String urlUnterPfad="schach/spiel/";
	private static final boolean log=false;
	private String url;
	private Client client=ClientBuilder.newClient();
	
	public BackendSpielStub(String url){
		if (url.endsWith("/"))
			this.url=url+urlUnterPfad;
		else
			this.url=url+"/"+urlUnterPfad;
	}
	
	private String getXmlvonRest(String pfad){
		String anfrage=url+pfad;
		if (log) System.out.println("CLIENT ANFRAGE: "+anfrage);
		String s=client.target(anfrage).request().accept("application/xml").get(String.class);
		if (log){
			ArrayList<D> daten=Xml.toArray(s);
			System.out.println(daten);
		}
		return s;
	}
	
	
	
	
	@Override
	public BufferedImage getBildWeiss() {
		BufferedImage bild=null;
		try {
			byte[] bildDaten=client.target(url+"getBildWeiss/").request().accept("image/png").get(byte[].class);
			bild=ImageIO.read((InputStream)new ByteArrayInputStream(bildDaten));
			return bild;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Fehler bei der Kommunikation zum Server in getBildWeiss(): "+url+"getBild/");
		}
	}

	@Override
	public BufferedImage getBildSchwarz() {
		BufferedImage bild=null;
		try {
			byte[] bildDaten=client.target(url+"getBildSchwarz/").request().accept("image/png").get(byte[].class);
			bild=ImageIO.read((InputStream)new ByteArrayInputStream(bildDaten));
			return bild;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Fehler bei der Kommunikation zum Server in getBildWeiss(): "+url+"getBild/");
		}
	}

	@Override
	public String getFigur(String feld) {
		return getXmlvonRest("getFigur"+"/"+feld);
	}

	@Override
	public String getErlaubteZuege(String feld) {
		return getXmlvonRest("getErlaubteZuege"+"/"+feld);
	}

	@Override
	public String ziehe(String feldVon, String feldNach) {
		return getXmlvonRest("ziehe"+"/"+feldVon+"/"+feldNach);
	}

	@Override
	public String bauerUmwandlung(String zuFigur) {
		return getXmlvonRest("bauerUmwandlung"+"/"+zuFigur);
	}

	@Override
	public String getSpielDaten() {
		return getXmlvonRest("getSpielDaten");
	}
	
	@Override
	public String getZugHistorie() {
		return getXmlvonRest("getZugHistorie");
	}
}
