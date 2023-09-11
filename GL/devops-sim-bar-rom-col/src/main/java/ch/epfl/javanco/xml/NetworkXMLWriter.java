package ch.epfl.javanco.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

class NetworkXMLWriter extends XMLWriter {

	public NetworkXMLWriter() {
		super();
	}

	public NetworkXMLWriter(OutputFormat format) throws IOException {
		super(format);
	}

	public NetworkXMLWriter(OutputStream out) throws IOException {
		super(out);
	}

	public NetworkXMLWriter(OutputStream out, OutputFormat format) throws IOException {
		super(out, format);
	}

	public NetworkXMLWriter(Writer writer){
		super(writer);
	}

	public NetworkXMLWriter(Writer writer, OutputFormat format) {
		super(writer, format);
	}

	@Override
	public void write(Attribute att) throws IOException {
		if (att instanceof NetworkAttribute) {
			NetworkAttribute netAtt = (NetworkAttribute)att;
			if (netAtt.hasValue()) {
				super.write(att);
			}
		} else {
			super.write(att);
		}
	}

	@Override
	protected void writeAttribute(Attribute att) throws IOException {
		if (att instanceof NetworkAttribute) {
			NetworkAttribute netAtt = (NetworkAttribute)att;
			if (netAtt.hasValue()) {
				super.writeAttribute(att);
			}
		} else {
			super.writeAttribute(att);
		}
	}

	@Override
	protected void writeAttributes(Element element) throws IOException {
		// I do not yet handle the case where the same prefix maps to
		// two different URIs. For attributes on the same element
		// this is illegal; but as yet we don't throw an exception
		// if someone tries to do this
		for (int i = 0, size = element.attributeCount(); i < size; i++) {
			Attribute attribute = element.attribute(i);

			// If the attribute is a namespace declaration, check if we have
			// already written that declaration elsewhere (if that's the case,
			// it must be in the namespace stack
			@SuppressWarnings("unused")
			String attName = attribute.getName();

			writeAttribute(attribute);
		}
	}

}

