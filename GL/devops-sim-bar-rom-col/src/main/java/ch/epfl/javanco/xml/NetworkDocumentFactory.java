package ch.epfl.javanco.xml;

import org.dom4j.Attribute;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

import ch.epfl.javanco.network.AbstractElementContainer;

public class NetworkDocumentFactory extends DocumentFactory {

	public static final long serialVersionUID = 0;
	/**
	 * This flag indicates whether the created elements and attributes
	 * should be completely modifiable or not.
	 * If the flag is set to <code>true</code> :
	 * <ul>
	 * <li> all created elements are set as locked, i.e. the user should not be allowed to add sub-elements.
	 * <li> all created attributes are set as locked, ie. the user should not be allowed to delete them.
	 * </ul>
	 * Note that this is just an indication and there is no mechanism in JavancoXMLElement or NetworkAttribute
	 * classes which explicitly prevents some actions when they are locked.
	 * <br>#author lchatela
	 */
	private boolean locked = false;

	/** The Singleton instance */
	private static NetworkDocumentFactory singleton = new NetworkDocumentFactory();

	/**
	 * Access to the singleton instance of this factory.
	 * @return the default singleon instance
	 */
	public static NetworkDocumentFactory getInstance() {
		return singleton;
	}

	/**
	 * Locks the <code>NetworkDocumentFactory</code>.<br/>
	 * If the <code>NetworkDocumentFactory</code> is locked, elements and attributes
	 * are created with a flag indicating that the user should not be allowed to add
	 * sub-elements to the created elements nor delete the created attributes.
	 * <br>#author lchatela
	 */
	public void lock() {
		locked = true;
	}

	/**
	 * Unlocks the <code>NetworkDocumentFactory</code>.<br/>
	 * If the <code>NetworkDocumentFactory</code> is unlocked, elements and attributes are
	 * created "normally".
	 * <br>#author lchatela
	 */
	public void unlock() {
		locked = false;
	}

	/**
	 * Indicates if the <code>NetworkDocumentFactory</code> is locked.<br/>
	 * If the <code>NetworkDocumentFactory</code> is locked, elements and attributes are created
	 * with a flag indicating that the user should not be allowed to add sub-elements to the created
	 * elements nor delete the created attributes.
	 * @return <code>true</code> if the <code>NetworkDocumentFactory</code> is locked and the user
	 * should not be allowed to fully modify elements and attributes, <code>false</code> otherwise.
	 * <br>#author lchatela
	 */
	public boolean isLocked() {
		return locked;
	}

	private NetworkDocumentFactory() {
	}

	@Override
	public Attribute createAttribute(Element parent, String name, String value) {
		if (parent instanceof JavancoXMLElement.JavancoInternalXMLElement) {
			JavancoXMLElement.JavancoInternalXMLElement el = (JavancoXMLElement.JavancoInternalXMLElement)parent;
			AbstractElementContainer aec = el.getJavancoXMLElement().getAssociatedContainer();
			NetworkAttribute att = new NetworkAttribute(AdditionalKeyword.parseLong(name), value, locked, aec);
			att.addLinkedElement(el.getJavancoXMLElement());
			return att;
		} else {
			return null;
		}
	}

	@Override
	public Attribute createAttribute(Element parent, QName name, String value) {
		return createAttribute(parent, name.getName(), value);
	}

	@Override
	public JavancoXMLElement.JavancoInternalXMLElement createElement(String name) {
		JavancoXMLElement.JavancoInternalXMLElement el = new JavancoXMLElement.JavancoInternalXMLElement(name, locked);
		return el;
	}

	@Override
	public JavancoXMLElement.JavancoInternalXMLElement createElement(QName qname) {
		return createElement(qname.getName());
	}

	@Override
	public JavancoXMLElement.JavancoInternalXMLElement createElement(String qualifiedName, String namespaceURI)  {
		return createElement(qualifiedName);
	}

	protected void handleException(Exception e) {
		// ignore introspection exceptions
		System.out.println("#### Warning: couldn't create bean: " + e);
	}


}