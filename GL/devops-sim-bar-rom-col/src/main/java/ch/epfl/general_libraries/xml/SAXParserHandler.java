package ch.epfl.general_libraries.xml;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserHandler extends DefaultHandler {

	public SAXParserHandler() {
	}

	private HashMap<String,String> simple = new HashMap<String,String>();
	private HashMap<String, StringBuilder> large  = new HashMap<String, StringBuilder>();

	private String currentlyReadField = null;
	private StringBuilder currentlyRead = null;
	private boolean currentlyReading = false;

	private boolean bypass = false;
	private String bypassUntil = null;

	public String getSimple(String regEx) {
		return simple.get(regEx);
	}

	public StringBuilder getLarge(String regEx) {
		return large.get(regEx);
	}

	public void addSimpleElementKeyword(String regEx) {
		simple.put(regEx,null);
	}

	public void addLargeElementKeyword(String regEx) {
		large.put(regEx,null);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (bypass == false) {
			for (String regExp : simple.keySet()) {
				if (qName.matches(regExp)) {
					currentlyReading = true;
					currentlyRead = new StringBuilder();
					currentlyReadField = regExp;
					break;
				}
			}
			for (String regExp : large.keySet()) {
				if (qName.matches(regExp)) {
					currentlyReading = true;
					currentlyRead = new StringBuilder();
					appendStartElement(currentlyRead,qName,attributes);
					currentlyReadField = regExp;
					bypass = true;
					bypassUntil = qName;
				}
			}
		} else {
			appendStartElement(currentlyRead, qName, attributes);
		}
	}

	private void appendStartElement(StringBuilder sb, String qName, Attributes attributes) {
		sb.append('<');
		sb.append(qName);
		for (int i = 0 ; i < attributes.getLength() ; i++) {
			sb.append(' ');
			sb.append(attributes.getQName(i));
			sb.append('=');
			sb.append('"');
			sb.append(attributes.getValue(i));
			sb.append('"');
		}
		sb.append('>');
	}

	private void appendEndElement(StringBuilder sb, String qName) {
		sb.append('<');
		sb.append('/');
		sb.append(qName);
		sb.append('>');
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (bypass == true) {
			appendEndElement(currentlyRead, qName);
			if (qName.equals(bypassUntil)) {
				bypassUntil = null;
				bypass = false;
			}
		}
		if (bypass == false) {
			if (qName.matches(currentlyReadField)) {
				if (simple.containsKey(currentlyReadField)) {
					simple.put(currentlyReadField, currentlyRead.toString());
				} else {
					large.put(currentlyReadField, currentlyRead);
				}
				currentlyRead = null;
				currentlyReadField = "";
				currentlyReading = false;
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		if (currentlyReading) {
			currentlyRead.append(ch,start,length);
		}
	}
}
