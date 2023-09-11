package ch.epfl.general_libraries.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

public class MNDWebServiceClient extends WebServiceClient {

	private DocumentFactory fac;

	public MNDWebServiceClient(String address) {
		super(address);
	}

	public Element call(Element el, Hashtable<String, String> keywords) throws IOException {
		return call(el,keywords, DocumentFactory.getInstance());
	}

	public Element call(Element el, Hashtable<String, String> keywords, DocumentFactory fac) throws IOException {
		this.fac = fac;
		// Open the input file. After we copy it to a byte array, we can see
		// how big it is so that we can set the HTTP Cotent-Length
		// property. (See complete e-mail below for more on this.)
		try {
			MNDSoapMessage msg = new MNDSoapMessage();

			msg.setMND(el);
			msg.setParams(keywords);

			MNDSoapMessage resp = new MNDSoapMessage(super.sendMessage(msg), fac);
			return resp.getMND();
		}
		finally {
			fac = null;
		}
	}

	public MNDSoapMessage createResponseContainer(InputStream inp) {
		return new MNDSoapMessage(inp, fac);
	}

}
