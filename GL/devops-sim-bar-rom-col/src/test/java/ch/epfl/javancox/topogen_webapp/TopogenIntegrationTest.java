package ch.epfl.javancox.topogen_webapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;

public class TopogenIntegrationTest {

	
	public static String getIp() {
		String s = System.getProperty("testMachineIP");
		if (s != null) return s;
		System.out.println("Property -testMachineIP- not set, set it recommended (default:localhost)");
		return "localhost";
	}
	
	public static String getPort() {
		String s = System.getProperty("testMachinePort");
		if (s != null) return s;
		System.out.println("Property -testMachinePort- not set, set it recommended (default:8080)");

		return "8080";
	}
	
	public static HttpURLConnection connect(String method, String content, String URL_, String session) throws Exception {
		URL url = new URL(URL_);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod(method);	
		if (session != null) {
			con.addRequestProperty("Cookie","SESSIONID="+ session);
		}
		if (content != null) {
			con.setDoOutput(true);
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
			osw.append(content);
			osw.flush();
		}
		con.connect();
		if (con.getResponseCode() != 200) throw new Exception("Server response is not 200");							
		return con;	
	}	
	
	@Test
	public void testTesselisation() throws Exception {
		System.out.println("http://" + getIp() + ":" + getPort()+"/topogen");
		HttpURLConnection con = connect("POST","1=Triangle&2=2&3=4&4=100&generatorName=Tesselation&methodName=generateTessellation&format=XML","http://" + getIp() + ":" + getPort()+"/topogen", null);			
		BufferedReader re = new BufferedReader(new InputStreamReader(con.getInputStream()));
		AbstractGraphHandler agh = 	Javanco.getDefaultGraphHandler(false);
		agh.activateMainDataHandler();
		agh.openNetwork(re);
		
		if (agh.getHighestNodeIndex() != 7) throw new Exception();
		
		if (agh.getCanonicalXML().asXML().length() < 600) throw new Exception();
	}	
	
	
}
