package ch.epfl.general_libraries.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultNamespace;

public class SoapMessage {

	public static final String SOAP_ENV = "SOAP-ENV";

	public static final String BODY = "Body";
	public static final String HEADER = "Header";
	public static final String ENVELOPE = "Envelope";

	private   Document doc = null;
	private   Element wsCall = null;
	private   Element wsResponse = null;
	private Element body = null;

	protected Namespace xsi = null;
	protected Namespace xsd = null;
	protected Namespace msgNameSpace = null;

	//private boolean filled = false;

	public SoapMessage() {
	}

	public SoapMessage(InputStream in) {
		try {
			SAXReader reader = new SAXReader(DocumentFactory.getInstance());
			doc = reader.read(in);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public void createSOAPEnvelope() {
		doc = new DefaultDocument();
		xsi = new DefaultNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
		xsd = new DefaultNamespace("xsd","http://www.w3.org/2001/XMLSchema");
		Namespace soap_env = new DefaultNamespace(SOAP_ENV,"http://schemas.xmlsoap.org/soap/envelope/");
		Namespace soap_enc = new DefaultNamespace("soap-enc","http://schemas.xmlsoap.org/soap/encoding/");
		Attribute enc = new DefaultAttribute(new QName("encodingstyle", soap_env),"http://schemas.xmlsoap.org/soap/encoding/");

		Element envelope = new DefaultElement(new QName(ENVELOPE,soap_env));
		envelope.add(xsi);
		envelope.add(xsd);
		envelope.add(soap_env);
		envelope.add(soap_enc);
		envelope.add(enc);
		Element header = new DefaultElement(new QName(HEADER, soap_env));
		envelope.add(header);
		body = new DefaultElement(new QName(BODY, soap_env));
		envelope.add(body);
		doc.setRootElement(envelope);
	}

	public void createSOAPEnvelope(String prefix, String uri) {
		msgNameSpace = new DefaultNamespace(prefix, uri);
		createSOAPEnvelope();
	}

	public Element getBodyElement() {
		if (body != null) {
			return body;
		} else {
			return doc.getRootElement().element(BODY);
		}
	}

	private Element selectSingleNodeFromDocument(String nameSpace, String elementName) {
		if (doc != null) {
			if (nameSpace != null) {
				return (Element)doc.selectSingleNode("//"+nameSpace+":"+elementName);
			} else {
				return (Element)doc.selectSingleNode("//"+elementName);
			}
		}
		return null;
	}

	public Element getWsCallElement(String nameSpace) {
		if (doc != null) {
			Element wsC = selectSingleNodeFromDocument(nameSpace, "wsCall");
			if (wsC != null) {
				return wsC;
			} else {
				if (body != null) {
					wsCall = new DefaultElement(new QName("wsCall",msgNameSpace));
					body.add(wsCall);
					return wsCall;
				} else {
					// final return
				}
			}
		} else {
			// final return
		}
		return null;
	}

	public Element getWsResponseElement(String nameSpace) {
		if (doc != null) {
			Element wsC = selectSingleNodeFromDocument(nameSpace, "wsResponse");
			if (wsC != null) {
				return wsC;
			} else {
				if (body != null) {
					wsResponse = new DefaultElement(new QName("wsResponse",msgNameSpace));
					body.add(wsResponse);
					return wsResponse;
				} else {
					// final return
				}
			}
		} else {
			// final return
		}
		return null;
	}

	public Namespace getMessageDefaultNamespace() {
		return msgNameSpace;
	}

	public Document getAssociatedDocument() {
		return doc;
	}

	public void setAssociatedDocument(Document doc) {
		this.doc = doc;
	}

	public void setResp(String s) {
		if (s != null) {
			Element resp = new DefaultElement(new QName("response", msgNameSpace));
			resp.setText(s);
			wsCall.add(resp);
		}
	}

	public byte[] getByteMessage() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(bout);
		writer.write(doc);
		return bout.toByteArray();
	}

	public void writeMessage(OutputStream out) throws Exception {
		XMLWriter writer = new XMLWriter(out, new OutputFormat("", false));
		writer.write(doc);
	}

}
