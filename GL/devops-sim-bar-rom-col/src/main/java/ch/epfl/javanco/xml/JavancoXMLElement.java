package ch.epfl.javanco.xml;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;

import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.AbstractElementContainer;

public class JavancoXMLElement implements Serializable {

	public static final long serialVersionUID = 0;

	private JavancoInternalXMLElement backedElement = null;

	private transient AbstractElementContainer associatedContainer = null;

	private String associatedKeyword = null;

	private boolean hasDefiningAttributes = false;

	/** Indicates if the user should be allowed to add sub-elements or not */
	private boolean locked = false;

	public static JavancoXMLElement castElement(Element el) {
		if (el instanceof JavancoInternalXMLElement) {
			return ((JavancoInternalXMLElement)el).getJavancoXMLElement();
		}
		return null;
	}

	/**
	 * Creates a new JavancoXMLElement, indicating if it is modifiable or not,
	 * i.e. if the user should be allowed to add sub-elements or not.<br/>
	 * Note: this is used as an indication and no error is thrown
	 * if the user does add a sub-element.
	 * <br>#author lchatela
	 * @param name The name of the element.
	 * @param lock <code>true<code> if the user should not be allowed to add sub-elements to this element,
	 * 	<code>false</code> otherwise.
	 */
	public JavancoXMLElement(String name, boolean lock) {
		backedElement = new JavancoInternalXMLElement(this, name);
		locked = lock;
	}

	/**
	 * Creates a new modifiable JavancoXMLElement.
	 * <br>#author lchatela
	 * @param name The name of the element.
	 */
	public JavancoXMLElement(String name) {
		this(name, false);
	}

	JavancoXMLElement(JavancoInternalXMLElement el) {
		backedElement = el;
	}

	public Element getBackedElement() {
		return backedElement;
	}

	public JavancoXMLElement(Element backedElement) {
		if (backedElement instanceof JavancoInternalXMLElement) {
			this.backedElement = (JavancoInternalXMLElement)backedElement;
		} else {
			throw new IllegalStateException("Use Javanco XMLfactory only to build XML structure");
		}
	}

	private JavancoXMLElement() {
	}

	public void saveToFile(String s) throws IOException {
		XMLSerialisationManager.writeXML(this, new java.io.FileOutputStream(new JavancoFile(s)));
	}

	@Override
	public String toString() {
		if (associatedContainer != null) {
			return super.toString() + " associated with : " + associatedContainer.toString();
		} else {
			return super.toString();
		}
	}

	@Override
	public Object clone() {
		JavancoXMLElement answer = new JavancoXMLElement();
		answer.associatedKeyword    =  this.associatedKeyword;
		answer.associatedContainer  =  this.associatedContainer;
		answer.backedElement        =  (JavancoInternalXMLElement)this.backedElement.clone();
		answer.locked               =  this.locked;
		return answer;
	}

	public void setAssociatedContainer(AbstractElementContainer cont) {
		associatedContainer = cont;
	}

	public AbstractElementContainer getAssociatedContainer() {
		return associatedContainer;
	}

	public String getAssociatedKeyword() {
		return associatedKeyword;
	}

	public void setAssociatedKeyword(String keyword) {
		associatedKeyword = keyword;
	}

	public boolean hasDefiningAttributes() {
		return hasDefiningAttributes;
	}

	public void setHasDefiningAttributes(boolean z) {
		hasDefiningAttributes = z;
	}

	/**
	 * Removes the given <code>Attribute</code> from this element.<br/>
	 * If the attribute is a {@link NetworkAttribute}, removes the link
	 * from the attribute to this element.
	 * <br>#author lchatela
	 * @param att is the atribute to be removed
	 * @return true if the attribute was removed
	 * */
	public boolean remove(Attribute att) {
		if (att != null){
			if (att instanceof NetworkAttribute){
				((NetworkAttribute)att).removeLinkedElement(this);
			}
			return backedElement.remove(att);
		}
		return false;
	}

	/**
	 * Indicates whether the user should be allowed to add sub-elements to this element.
	 * <br>#author lchatela
	 * @return <code>true</code> if the user should be allowed to add sub-elements,
	 * <code>false</code> otherwise.
	 */
	public boolean isModifiable() {
		return !locked;
	}

	/************************************* bACKED METHODS **********************/
	@SuppressWarnings("unchecked")
	public List<NetworkAttribute> attributes() {
		return backedElement.attributes();
	}

	public NetworkAttribute attribute(String name) {
		return (NetworkAttribute)backedElement.attribute(name);
	}

	public void detach() {
		backedElement.detach();
	}

	public JavancoXMLElement selectSingleElement(String xpathExpression) {
		Node n = backedElement.selectSingleNode(xpathExpression);
		if (n instanceof JavancoInternalXMLElement) {
			return ((JavancoInternalXMLElement)n).getJavancoXMLElement();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<JavancoXMLElement> selectElements(String xpathExpression) {
		List<JavancoXMLElement> toReturn = new ArrayList<JavancoXMLElement>();
		List<Node> toChange = backedElement.selectNodes(xpathExpression);
		for (Node node : toChange) {
			if (node instanceof JavancoInternalXMLElement) {
				toReturn.add(((JavancoInternalXMLElement)node).getJavancoXMLElement());
			}
		}
		return toReturn;
	}

	public void add(JavancoXMLElement element) {
		backedElement.add(element.getBackedElement());
	}

	public void add(Attribute attribute) {
		backedElement.add(attribute);
	}

	public void add(Element element) {
		backedElement.add(element);
	}

	public boolean remove(JavancoXMLElement element) {
		return backedElement.remove(element.getBackedElement());
	}

	public String getName() {
		return backedElement.getName();
	}

	public String attributeValue(String name) {
		return backedElement.attributeValue(name);
	}

	public String asXML() {
		return backedElement.asXML();
	}

	public JavancoXMLElement element(String name) {
		if (backedElement.element(name) != null) {
			return backedElement.element(name).getJavancoXMLElement();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<JavancoXMLElement> elements() {
		List<JavancoXMLElement> toReturn = new ArrayList<JavancoXMLElement>();
		List<JavancoInternalXMLElement> toChange = backedElement.elements();
		for (JavancoInternalXMLElement el : toChange) {
			toReturn.add(el.getJavancoXMLElement());
		}
		return toReturn;
	}

	public JavancoXMLElement getParent() {
		return backedElement.getParent().getJavancoXMLElement();
	}

	@SuppressWarnings("unchecked")
	public boolean removeVoidElements() {
		List<Node> leafNodes = backedElement.selectNodes("descendant-or-self::*/*[count(*)=0]");
		while (leafNodes.size() > 0) {
			Node leaf = leafNodes.iterator().next();
			leafNodes.remove(leaf);
			if ((leaf.selectNodes("descendant-or-self::*/@*").size() == 0) &&
					(leaf.getText().length() == 0)) {
				Node parent = leaf.getParent();
				if (parent != null) {
					leaf.detach();
					if (parent.selectNodes("*").size() == 0) {
						leafNodes.add(parent);
					}
				}
			}
		}
		if ((backedElement.elements().size() == 0) && (backedElement.attributes().size() == 0)) {
			return true;
		} else {
			return false;
		}
	}

	/*	private void triggerElementModifiedEvent() {
		this.getAssociatedContainer().fireElementModificationEvent();
	}*/

	public static class JavancoInternalXMLElement extends DefaultElement implements Element, Serializable {

		private static final long serialVersionUID = 4349354554997996544L;
		private transient JavancoXMLElement elem;


		JavancoInternalXMLElement(String name) {
			this(name, false);
		}

		JavancoInternalXMLElement(String name, boolean lock) {
			super(name);
			elem = new JavancoXMLElement(this);
		}

		JavancoInternalXMLElement(JavancoXMLElement el, String name) {
			super(name);
			elem = el;
		}	

		JavancoXMLElement getJavancoXMLElement() {
			return elem;
		}

		@Override
		public DocumentFactory getDocumentFactory() {
			return NetworkDocumentFactory.getInstance();
		}

		/***** THESE METHODS ARE JUSTE USED TO CAST DIRECTLY TO JAVANCOXMLOBJECT */

		@Override
		public JavancoInternalXMLElement getParent() {
			try {
				return (JavancoInternalXMLElement) super.getParent();
			}
			catch (ClassCastException e) {
				// if the parent is not an JavancoInternalXMLElement
				return null;
			}
		}

		@Override
		public JavancoInternalXMLElement element(String name) {
			return (JavancoInternalXMLElement) super.element(name);
		}
	}

	public void addAttribute(String string, String string2) {
		NetworkAttribute att = new NetworkAttribute(string, string2);
		add(att);
	}


}

