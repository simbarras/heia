package ch.epfl.general_libraries.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

public class WebServiceClient {


	private String address = "";
	private URL url = null;

	public WebServiceClient(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public URL getURL() throws java.net.MalformedURLException{
		if (url == null) {
			url = new URL(address);
		}
		return url;
	}

	/*	public SoapMessage call(SoapMessage msg) throws IOException {
		return new SoapMessage(sendMessage(msg));
	}*/

	public void call(SoapMessage request, DefaultHandler o1) throws IOException {
		try {
			System.setProperty("org.xml.sax.parser","org.apache.xerces.parsers.SAXParser");
			ParserAdapter parser = new ParserAdapter();
			parser.setContentHandler(o1);
			//	logger.trace("Sending message :");
			//	logger.trace(request.getAssociatedDocument().getRootElement());
			parser.parse(new InputSource(sendMessage(request)));
		}
		catch (SAXException e) {
			throw new IOException("Impossible to connect to WebService located at " + address,e);
		}
	}

	public InputStream sendMessage(SoapMessage request) throws IOException {
		byte[] b = request.getByteMessage();

		// Create the connection where we're going to send the file.
		URLConnection connection = getURL().openConnection();
		HttpURLConnection httpConn = (HttpURLConnection)connection;


		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty( "Content-Length",
				String.valueOf( b.length ) );
		httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
		httpConn.setRequestProperty("Connection", "close");
		httpConn.setRequestMethod( "POST" );
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		// Everything's set up; send the XML that was read in to b.
		OutputStream out = httpConn.getOutputStream();
		out.write( b );
		out.close();

		return httpConn.getInputStream();
	}

}

