package ch.epfl.javanco.xml;


public interface XMLDataHandler {

	/**
	 * Returns the name of the XML element associated to this handler
	 * @return the name of the XML element associated to this handler
	 */
	public String getXMLElementName();
	/**
	 * Returns the XML version of the data handled by this handler. The name of
	 * the returned element (the root element when a tree is returned) must be
	 * equal to what returns the <code>getXMLElementName()</code> method.
	 * @return the XML version of the data handled by this handler.
	 */
	public JavancoXMLElement getXML();

	public JavancoXMLElement getCanonicalXML();

	/**
	 * Dispatch the data contained in the element given in parameter into the
	 * differents data structure contained into this handler. This is the inverse
	 * operation that <code>getXML()</code>. Structure of the parameter element
	 * depends of the implementation of the handler. Nevertheless, root element's
	 * name must be equals to what returns method <code>getXMLElementName()</code>.
	 * @param e The element to dispath into this handler.
	 */
	public void setXML(JavancoXMLElement e);
}