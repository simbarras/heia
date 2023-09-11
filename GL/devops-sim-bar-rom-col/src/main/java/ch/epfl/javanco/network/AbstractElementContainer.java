package ch.epfl.javanco.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.utils.Counter;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.xml.JavancoXMLElement;
import ch.epfl.javanco.xml.Keyword;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;

public abstract class AbstractElementContainer implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, JavancoXMLElement> elementMap = null;

	private HashMap<String, NetworkAttribute> attributeVector = null;

	private boolean fullyCreated = false;

	protected transient AbstractGraphHandler handler = null;

	public abstract LayerContainer getContainingLayerContainer();

	public abstract  Collection<NetworkAttribute> getSortedAttributes();

	protected AbstractElementContainer(Class<? extends AbstractElement> c) {
		/*	if (c != null) {
			attribute(XMLTagKeywords.CLASS).setValue(c.getName());
		}*/
	}

	protected AbstractElementContainer(Element el) {
		/*	if (el != null) {
			attribute(XMLTagKeywords.CLASS).setValue(el.getClass().getName());
		}	*/
	}

	private HashMap<String, NetworkAttribute> getAttVector() {
		if (attributeVector == null) {
			attributeVector = new HashMap<String, NetworkAttribute>(4, 0.85f);
		}
		return attributeVector;
	}

	private void addAttributeToVector(NetworkAttribute att) {
		synchronized (attributeVector) {
			attributeVector.put(att.getName(), att);
		}
	}

	String getIdentifierKey() {
		return this.hashCode() + "_" + this.getAttVector().hashCode();
	}

	void setAbstractGraphHandler(AbstractGraphHandler to) {
		handler = to;
	}

	public void setFullyCreated() {
		fullyCreated = true;
	}

	public boolean isInTheSameGraph(AbstractElementContainer other) {
		return (this.getAbstractGraphHandler() == other.getAbstractGraphHandler());
	}

	public boolean isFullyCreated() {
		return fullyCreated;
	}
	
	public void fireElementEvent() {
		ElementEvent ev = ElementEvent.creationModificationEvent(this, null);
		handler.fireElementEvent(ev);
	}

	public void fireElementEvent(ElementEvent ev) {
		if (fullyCreated) {
			if (handler != null) {
				if (!(ev.getContainer() == this)) {
					throw new IllegalStateException("Cannot fire an event concerning a container on another container");
				}
				handler.fireElementEvent(ev);
			}
		}
	}

	public AbstractGraphHandler getAbstractGraphHandler() {
		return handler;
	}

	public JavancoXMLElement getElement() {
		return getElement(null);
	}

	/**
	 * If element corresponding to given key is not found, then the default
	 * (main_description) element is returned
	 */
	public JavancoXMLElement getElement(String key) {
		if (elementMap != null) {
			if ((key == null) || (elementMap.get(key) == null)) {
				return elementMap.get(XMLTagKeywords.MAIN_DESCRIPTION.toString());
			} else {
				return elementMap.get(key);
			}
		}
		return null;
	}

	public Map<String, JavancoXMLElement> getElementMap() {
		Map<String, JavancoXMLElement> newMap = new Hashtable<String, JavancoXMLElement>();
		for (Map.Entry<String,JavancoXMLElement> entry : elementMap.entrySet()) {
			if (entry.getKey().equals("additional_description")) {
				// encodes the associated AbstractElement if any
				if (getContainedElement() != null) {
					getContainedElement().encodeIn(entry.getValue());
				}
			}
			newMap.put(entry.getKey(), entry.getValue());
		}
		JavancoXMLElement el = new JavancoXMLElement("floating attributes");
		Collection<NetworkAttribute> col = getAttVector().values();
		synchronized (col) {
			for (NetworkAttribute att : col) {
				if (att.hasParent() == false) {
					el.add(att);
				}
			}
		}
		el.setAssociatedContainer(this);
		newMap.put("floating attributes", el);
		return newMap;
	}

	public JavancoXMLElement createXMLElement(String key) {
		JavancoXMLElement linkedXMLElement = new JavancoXMLElement(this.getContainedElementKeyword().toString());
		setElement(linkedXMLElement , key);
		return linkedXMLElement;
	}

	public void removeXMLElement(String key) {
		JavancoXMLElement toRemove = elementMap.get(key);
		toRemove.setAssociatedContainer(null);
		for (NetworkAttribute att :  toRemove.attributes()) {
			att.removeLinkedElement(toRemove);
		}
	}

	private NetworkAttribute getActuallyStoredAttribute(String keyword) {
		return getAttVector().get(keyword);
	}

	public void setElement(JavancoXMLElement element, String key) {
		element.setAssociatedKeyword(key);

		if (elementMap == null) {
			elementMap = new HashMap<String, JavancoXMLElement>(1);
		}
		if (elementMap.get(key) == null) {
			elementMap.put(key, element);
			//	elementList.get(priority).add(element);
			for (NetworkAttribute att : element.attributes()) {
				addAttribute(att);
				linkAttribute(att.getKeyword(), key);
			}
		} else {
			// merge previous in new one, maintaining links

			// remove existing
			JavancoXMLElement toRemove = elementMap.get(key);
			elementMap.remove(toRemove);

			toRemove.detach();

			elementMap.put(key, element);

			// extracting the attribute of the old one : if the same
			// attribute exists, overwrite
			for (NetworkAttribute att : toRemove.attributes()) {
				NetworkAttribute existingAttribute = element.attribute(att.getName());
				if (existingAttribute != null) {
					element.remove(existingAttribute);
				}
				att.transfer(toRemove, element);
			}
			Vector<NetworkAttribute> scheduledToRemove = new Vector<NetworkAttribute>();
			Vector<NetworkAttribute> scheduledToAdd = new Vector<NetworkAttribute>();
			for (NetworkAttribute att : element.attributes()) {

				NetworkAttribute actuallyStored = getActuallyStoredAttribute(att.getName());
				if (actuallyStored != null) {
					if (!(actuallyStored.getValue().equals(att.getValue()))) {
						if (!(actuallyStored.getValue().equals(""))) {
							//	throw new IllegalStateException("Different values in differents blocks with same name");
						}
					} else {
						scheduledToRemove.add(att);
						scheduledToAdd.add(actuallyStored);
					}
				} else {
					addAttributeToVector(att);
					att.setContainer(this);
				}
			}
			//removing scheduled attributes
			for (NetworkAttribute attToRemove : scheduledToRemove) {
				element.remove(attToRemove);
			}
			for (NetworkAttribute attToAdd : scheduledToAdd) {
				element.add(attToAdd);
			}
		}
		// suppressed for rapidity reasons
		//	this.fireElementModifiedEvent();
		element.setAssociatedContainer(this);
	}
	
	public void copyAttributesTo(String[] attNames, AbstractElementContainer aec) {
		for (int i = 0 ; i < attNames.length ; i++) {
			NetworkAttribute att = this.attribute(attNames[i]);
			copyAttributeTo(att, aec);
		}
	}
	
 
	public void copyAttributesTo(AbstractElementContainer aec) {
		for (NetworkAttribute att : this.attributes()) {
			copyAttributeTo(att, aec);
		}
	}
	
	private void copyAttributeTo(NetworkAttribute att, AbstractElementContainer aec) {
		if (!att.getKeyword().isCore()) {
			aec.attribute(att.getName()).setValue(att.getValue());
			if (att.isLinked()) {
				for (String s : att.getLinkKeywords()) {
					aec.linkAttribute(att.getName(), s);
				}
			}
		}		
	}
	
	

	public NetworkAttribute linkAttribute(String name) {
		return linkAttribute(name, XMLTagKeywords.MAIN_DESCRIPTION.toString());
	}

	/**
	 * Links the attribute with the given name to the element corresponding
	 * to <code>elementKeyword</code>.<br>
	 * If there is no attribute with the given name in this container, creates it
	 * with an empty value and adds it to the container.
	 * <br>#author lchatela
	 * @param name the <code>Keyword</code> corresponding
	 * 		to the name of the attribute
	 * @param elementKeyword the "keyword" associated to the element (e.g. "additional_description")
	 * @return the <code>NetworkAttribute</code> linked
	 */
	public NetworkAttribute linkAttribute(Keyword name, String elementKeyword) {
		return linkAttribute(name.toString(), elementKeyword);
	}

	/**
	 * Links the attribute with the given name to the element corresponding
	 * to <code>elementKeyword</code>.<br>
	 * If there is no attribute with the given name in this container, creates it
	 * with an empty value and adds it to the container.
	 * <br>#author lchatela
	 * @param attributeName the name of the attribute
	 * @param elementKeyword the "keyword" associated to the element (e.g. "additional_description")
	 * @return the <code>NetworkAttribute</code> linked
	 */
	public NetworkAttribute linkAttribute(String attributeName, String elementKeyword) {
		NetworkAttribute toLink = attribute(attributeName);
		return linkAttributeInternal(toLink, elementKeyword);
	}

	/**
	 * Links the given attribute to the element corresponding to
	 * <code>elementKeyword</code>. <br>
	 * Adds the attribute to this container if not added yet.
	 * <br>#author lchatela
	 * @param att the <code>NetworkAttribute</code> to link
	 * @param elementKeyword the "keyword" associated to the element (e.g. "additional_description")
	 * @return the <code>NetworkAttribute</code> linked
	 */
	public NetworkAttribute linkAttribute(NetworkAttribute att, String elementKeyword) {
		addAttribute(att);
		return linkAttributeInternal(att,elementKeyword);
	}

	private NetworkAttribute linkAttributeInternal(NetworkAttribute toLink, String elementKeyword) {
		JavancoXMLElement target = getElement(elementKeyword);

		if (target == null) {
			throw new IllegalStateException("Unable to link specified attribute"
					+ " with group " + elementKeyword + "\n. May be this container is "
					+ " not yet attached to a structure");
		}
		if (target.attributes().size() == 0) {
			linkAllDefiningAttributes(elementKeyword);
		}
		// first condition : attribute must not be already linked, except if it
		// is a CORE one
		//	if ((toLink.isLinked() == false) || (toLink.getKeyword().isCore())) {
		linkAttributeAtomicAction(target, toLink);
		/*	} else {
			throw new IllegalStateException("Trying to link an already linked attribute that isn't CORE");
		 */
		return toLink;
	}

	private void linkAttributeAtomicAction(JavancoXMLElement target, NetworkAttribute toLink) {
		if (target.attribute(toLink.getName()) == null) {
			toLink.addLinkedElement(target);
			target.add(toLink);
		} else {
			if (!(target.attribute(toLink.getName()).equals(toLink))) {
				throw new IllegalStateException("Trying to link an attribute already linked with the current element");
			}
		}
	}

	abstract public void writeAllDefiningAttributes(JavancoXMLElement e);

	protected boolean writeAllCoreAttribute(JavancoXMLElement e) {
		if (e.hasDefiningAttributes() == false) {
			boolean toReturn = false;
			Collection<NetworkAttribute> col = getAttVector().values();
			synchronized (col) {
				for (NetworkAttribute att : col) {
					// modified
					if ((e.attribute(att.getKeyword().toString()) == null) && (att.getKeyword().isCore())) {
						e.add(att);
						toReturn = true;
					}
				}
				return toReturn;
			}
		}
		e.setHasDefiningAttributes(true);
		return false;
	}

	abstract void linkAllDefiningAttributes(String elementKeyword);

	protected void linkAllCoreAttributes(String elementKeyword) {
		JavancoXMLElement e = getElement(elementKeyword);
		Collection<NetworkAttribute> col = getAttVector().values();
		synchronized (col) {
			for (NetworkAttribute att : col) {
				if (att.getKeyword().isCore()) {
					addAttribute(att);
					linkAttributeAtomicAction(e, att);
				}
			}
		}
		e.setHasDefiningAttributes(true);
	}
	
	public void linkAllAttributes(String elementKeyword) {
		JavancoXMLElement e = getElement(elementKeyword);
		Collection<NetworkAttribute> col = getAttVector().values();
		synchronized (col) {
			for (NetworkAttribute att : col) {
				addAttribute(att);
				linkAttributeAtomicAction(e, att);
			}			
		}
		e.setHasDefiningAttributes(true);		
	}
	
	public void linkAllAttributes() {
		linkAllAttributes(XMLTagKeywords.MAIN_DESCRIPTION.toString());
	}

	private NetworkAttribute addAttribute(NetworkAttribute att) {
		NetworkAttribute existingAttribute = getActuallyStoredAttribute(att.getName());
		if (existingAttribute == null) {
			addAttributeToVector(att);
			att.setContainer(this);
		} else {
			if (att.textEquals(existingAttribute) == false) {
				throw new IllegalStateException("Adding new attribute with already used name");
			}
		}
		return att;
	}

	/**
	 * Returns the object's attribute corresponding to the given keyword. <br>
	 * <b>Warning :</b> This method never returns <code>null</code> even if the
	 * current object has no attribute with the given keyword. In this case,
	 * a "void" attribute (name=key and no value) is created. This permits
	 * to call directly a method on the returned attribute, without having
	 * to test if it is null, to avoid <code>NullPointerException</code> <br>
	 * If you don't want to create an attribute in the latter case, use
	 * {@link #attribute(Keyword, boolean) attribute(Keyword, false)}.
	 * <br>#author lchatela
	 * @param key the key corresponding to the attribute name
	 * @return the object's attribute corresponding to the given keyword (creates it if none)
	 * @see #attribute(Keyword, boolean)
	 */
	public NetworkAttribute attribute(Keyword key) {
		return attribute(key, true);
	}

	public NetworkAttribute attribute(Keyword key, boolean createIfNull) {
		String keyString = key.getString();
		Counter c = access.get(keyString);
		if (c == null) {
			c = new Counter();
			access.put(keyString, c);
		}
		c.increment();
		
		NetworkAttribute toReturn = getActuallyStoredAttribute(keyString);
		if (toReturn != null) {
			return toReturn;
		}
		if (createIfNull) {
			return addAttribute(new NetworkAttribute(key, this));
		} else {
			return null;
		}
	}
	
	// use to monitor access to attribute, for performance opti
	private static HashMap<String, Counter> access = new HashMap<String, Counter>();
	
	public boolean booleanAttribute(String string) {
		NetworkAttribute att = this.attribute(string, false);
		if (att == null) return false;
		return att.booleanValue();
	}

	/**
	 * Returns the object's attribute corresponding to the given keyword. <br>
	 * <b>Warning :</b> If the current object has no attribute with the given keyword,
	 * this methodes returns:
	 * <li> <code>null</code> if <code>createIfNull=false</code>, </li>
	 * <li> a new attribute of name=key and no value, which is also added
	 * to the container, if <code>createIfNull=true</code>
	 * (equivalent to using {@link #attribute(Keyword)}).
	 * <br>#author lchatela
	 * @param key the key corresponding to the attribute name
	 * @param createIfNull indicates whether the attribute must be created
	 * in case it doesn't exist
	 * @return the attribute corresponding to the given keyword (if some,
	 * otherwise <code>null</code> if <code>createIfNull=false</code> or
	 * a new attribute without value if <code>createIfNull=true</code>).
	 * @see #attribute(Keyword)
	 */
	public NetworkAttribute attribute(String key, boolean createIfNull) {
		
		
		Counter c = access.get(key);
		if (c == null) {
			c = new Counter();
			access.put(key, c);
		}
		c.increment();
		
		NetworkAttribute toReturn = getActuallyStoredAttribute(key);
		if (toReturn != null) {
			return toReturn;
		}
		if (createIfNull) {
			return addAttribute(new NetworkAttribute(key, this));
		} else {
			return null;
		}
	}

	/**
	 * Returns the object's attribute corresponding to the given keyword string. <br>
	 * <b>Warning :</b> This method never returns <code>null</code> even if the
	 * current object has no attribute with the given keyword. In this case,
	 * a "void" attribute (name=key and no value) is created. This permits
	 * to call directly a method on the returned attribute, without having
	 * to test if it is null, to avoid <code>NullPointerException</code> <br>
	 * If you don't want to create an attribute in the latter case, use
	 * {@link #attribute(String, boolean) attribute(String, false)}.
	 * <br>#author lchatela
	 * @param keyword_string the attribute name
	 * @return the object's attribute corresponding to the given keyword (creates it if none)
	 * @see #attribute(String, boolean)
	 */
	public NetworkAttribute attribute(String keyword_string) {
		return this.attribute(XMLTagKeywords.parse(keyword_string));
	}


	public Collection<NetworkAttribute> attributes() {
		if (attributeVector != null) {
			Collection<NetworkAttribute> toRet = null;
			synchronized (attributeVector) {
				toRet = attributeVector.values();
			}
			return toRet;
		} else {
			return new ArrayList<NetworkAttribute>(0);
		}
	}

	/**
	 * Removes the given attribute from the <code>attributeList</code> of the container.
	 * <br>#author lchatela
	 * @param att the <code>NetworkAttribute</code> to remove
	 */
	public void removeAttribute(NetworkAttribute att, EventObject e) {
		att.setContainer(null);
		attributeVector.remove(att.getName());
		if (att.getLinkedElement() == null) {
			return;
		}
		for (JavancoXMLElement el : att.getLinkedElements()) {
			el.getBackedElement().remove(att);
		}
		ElementEvent ev = ElementEvent.creationModificationEvent(this, "remove att");
		ev.setParent(e);
		fireElementEvent(ev);
		/*	NetworkAttribute actuallyStored = getActuallyStoredAttribute(att.getKeyword());
		if (actuallyStored != null) {
			Collection<NetworkAttribute> col = getAttVector();
			synchronized (col) {
				col.remove(actuallyStored);
			}
		}*/
	}

	/**
	 * Removed the attribute corresponding to the given name from the the
	 * <code>attributeList</code> of the container.
	 * <br>#author srumley
	 * @param attributeName the name of the <code>NetworkAttribute</code> to remove
	 */
	public void removeAttribute(String attributeName) {
		NetworkAttribute att = this.attribute(attributeName, false);
		if (att != null) {
			removeAttribute(att, new CasualEvent("Remove attribute event"));
		}
	}

	public void removeAllAttributes() {
		for (NetworkAttribute att : attributes()) {
			removeAttribute(att, new CasualEvent("Remove attribute event"));
		}
	}

	public void removeAllFloatingAttributes() {
		Vector<NetworkAttribute> toRemove = new Vector<NetworkAttribute>();
		for (NetworkAttribute att : attributes()) {
			if (att.isFloating()) {
				toRemove.add(att);
			}
		}
		for (NetworkAttribute att : toRemove) {
			removeAttribute(att, new CasualEvent("Remove attribute event"));
		}
	}

	public String toFullString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AbstractElementContainer containing : " + this.getContainedElementKeyword().toString() + "   Attributes :");
		Collection<NetworkAttribute> col = getAttVector().values();
		synchronized (col) {
			for (NetworkAttribute att : col) {
				sb.append("\n\t " + att.getName() + "=\"" + att.getValue() + "\"\t");
				if (att.hasParent()) {
					sb.append("linked");
				}
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(toShortString() + " (");
		Collection<NetworkAttribute> col = getAttVector().values();
		synchronized (col) {
			for (NetworkAttribute att : col) {
				sb.append(att.getName() + "=\"" + att.getValue() + "\" ");
			}
		}
		sb.append(")\r\n");
		return sb.toString();
	}

	public  abstract String toShortString();

	public abstract Keyword getContainedElementKeyword();

	/**
	 * Returns the <code>AbstractElement</code> object contained in this container.
	 * @return The <code>AbstractElement</code> contained in this container
	 */
	public abstract AbstractElement getContainedElement();
	
	/**
	 * Returns the <code>Object</code> object contained in this container. This is lesser general than the getContainerELement method
	 * @return The <code>Object</code> contained in this container
	 */
	public abstract Object getContainedObject();	

	/**
	 * Replaces the existing <code>AbstractElement</code> implementation object (if any)
	 * contained into the current container with the object given in parameter.
	 * <br>#author lchatela
	 * @param el The new <code>AbstractElement</code> to contain in this container
	 * @return The previously contained <code>AbstractElement</code> or <code>null</code> if
	 * 		there was no previous one.
	 */
	public abstract AbstractElement setContainedElement(AbstractElement el);


	protected Object getInstance(Class<? extends AbstractElement> objectClass) throws InstantiationException {
		try {
			java.lang.reflect.Constructor<? extends AbstractElement> c = objectClass.getConstructor(new Class[]{});
			return c.newInstance(new Object[]{});
		}
		catch (NoSuchMethodException e) {
			throw new InstantiationException("Exception occured : impossible to access the constructor of class "
					+ objectClass + " with no arguments. Please define a void constructor with public access in this class");
		}
		catch (InstantiationException e) {
			// should not be here
			assert (0==1);
			throw new IllegalStateException(e);
		}
		catch (IllegalAccessException e) {
			throw new InstantiationException("Exception occured : impossible to access the constructor of class "
					+ objectClass + " with no arguments. Please define a void constructor with public access in this class");
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

}