package ch.epfl.general_libraries.web;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

public class MNDSoapMessage extends SoapMessage {

	//private final static String MND_REGEXP = ".*mndweb:mnd";
	//private final static String PARAMS_REGEXP = ".*mndweb:parameters";

	public MNDSoapMessage() {
		createSOAPEnvelope();
	}

	public MNDSoapMessage(InputStream in, DocumentFactory xmlFactory) {
		try {
			SAXReader reader = new SAXReader(xmlFactory);
			setAssociatedDocument(reader.read(in));
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public MNDSoapMessage(InputStream in) {
		this(in, DocumentFactory.getInstance());
	}

	public void setMND(Element el) {
		Element mnd = new DefaultElement(new QName("mnd", getMessageDefaultNamespace()));
		el.detach();
		mnd.add(el);
		getWsCallElement(null).add(mnd);
	}

	/*	public void setMND(String s) {
		Element mnd = new DefaultElement(new QName("mnd", getMessageDefaultNamespace()));
		mnd.setText(s);
		getWsCallElement(null).add(mnd);
	}*/

	public Element getMND() {
		Document doc = getAssociatedDocument();
		Element e = null;
		try {
			e = (Element)doc.selectSingleNode("//mndweb:mnd/network");
		} catch (org.dom4j.XPathException ex) {}
		if (e == null) {
			e = (Element)doc.selectSingleNode("//mnd/network");
		}
		return e;
	}

	public void setParams(Hashtable<String, String> keywords) {
		if (keywords != null) {
			Element params = new DefaultElement(new QName("parameters", getMessageDefaultNamespace()));
			for (String key : keywords.keySet()) {
				Element param = new DefaultElement(new QName(key, getMessageDefaultNamespace()));
				param.setText(keywords.get(key));
				params.add(param);
			}
			getWsCallElement(null).add(params);
		}
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String, String> getParams() {
		Element params = null;
		try {
			params = (Element)getAssociatedDocument().selectSingleNode("//mndweb:parameters");
		} catch(org.dom4j.XPathException ex) {}
		if (params == null) {
			params = (Element)getAssociatedDocument().selectSingleNode("//parameters");
		}
		if (params != null) {
			Hashtable<String, String> table = new Hashtable<String, String>(params.elements().size());
			for (Element param : (List<Element>)params.elements()) {
				String key = param.getName();
				String value = param.getText();
				table.put(key, value);
			}
			return table;
		} else {
			return null;
		}
	}
}
