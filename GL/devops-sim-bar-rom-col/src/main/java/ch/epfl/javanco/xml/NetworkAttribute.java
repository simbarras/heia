package ch.epfl.javanco.xml;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultAttribute;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.network.AbstractElementContainer;

public class NetworkAttribute extends DefaultAttribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7499462214486427127L;

	private Keyword keyword = null;

	private boolean hasValue = true;

	private ArrayList<JavancoXMLElement> linkedElements = null;

	private transient AbstractElementContainer container = null;

	/** Indicates if the user should be allowed to delete this attribute or not. */
	private boolean locked = false;

	/**
	 * Creates a new <code>NetworkAttribute</code> with the given name and value,
	 * indicating if the user should be allowed to delete this attribute or not.<br/>
	 * Note: this is used as an indication and no error is thrown
	 * if the user does delete it.
	 * <br>#author lchatela
	 * @param name The keyword name of the attribute
	 * @param value The value of the attribute
	 * @param lock <code>true</code> if the user should not be allowed to delete this attribute,
	 * 	<code>false</code> otherwise.
	 */
	public NetworkAttribute(Keyword name, String value, boolean lock, AbstractElementContainer aec) {
		super(name.toString(), value);
		keyword = name;
		if (value.equals("")) {
			hasValue = false;
		}
		locked = lock;
	}

	/**
	 * Creates a new modifiable attribute, with the given name and value.
	 * <br>#author lchatela
	 * @param name The keyword name of the attribute
	 * @param value The value of the attribute
	 */
	public NetworkAttribute(Keyword name, String value, AbstractElementContainer aec) {
		this(name, value, false, aec);
	}

	NetworkAttribute(String name, String value, AbstractElementContainer aec) {
		this(new AdditionalKeyword(name), value, aec);
	}

	/**
	 * Creates a new modifiable attribute without value.
	 * <br>#author lchatela
	 * @param name The keyword name of the attribute
	 */
	public NetworkAttribute(Keyword name, AbstractElementContainer aec) {
		this(name, "", aec);
	}

	public NetworkAttribute(String name, AbstractElementContainer aec) {
		this(name, "", aec);
	}

	/**
	 * 
	 * <br>#author lchatela
	 * @param name The keyword name of the attribute
	 */
	public NetworkAttribute(String name,  String value) {
		this(XMLTagKeywords.parse(name), value, null);
	}

	@Override
	public Object clone() {
		NetworkAttribute answer = (NetworkAttribute) super.clone();

		answer.keyword = this.keyword;
		answer.hasValue = this.hasValue;
		answer.linkedElements = this.linkedElements;

		return answer;
	}

	/**
	 * Indicates whether the user should be allowed to modify the value of this attribute.<br>
	 * The "class" attribute and the "property_name" attribute aren't modifiable if
	 * <code>locked=true</code>.
	 * <br>#author lchatela
	 * @return <code>true</code> if the user should be allowed to modify it,
	 * <code>false</code> otherwise.
	 */
	public boolean isEditable() {
		return !(locked && (getName().equals("class") || getName().equals("property_name")));
	}

	/**
	 * Indicates whether the user should be allowed to delete this attribute.
	 * <br>#author lchatela
	 * @return <code>true</code> if the user should be allowed to delete it,
	 * <code>false</code> otherwise.
	 */
	public boolean isDeletable() {
		return !locked;
	}

	public boolean hasValue() {
		return hasValue;
	}

	public boolean isLinked() {
		return (!(getLinkedElement() == null));
	}

	public boolean isLinkedTo(String s) {
		if (linkedElements == null) {
			return false;
		}
		for (JavancoXMLElement element : this.linkedElements) {
			if (element.getAssociatedKeyword().equals(s)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getLinkKeywords() {
		ArrayList<String> links = new ArrayList<String>();
		if (linkedElements == null) {
			return links;
		}
		for (JavancoXMLElement element : this.linkedElements) {
			links.add(element.getAssociatedKeyword());
		}
		return links;
	}

	public boolean isFloating() {
		return ((!isLinked()) && (!(keyword.isCore())));
	}

	private void setValueInternal(String s, EventObject e) {
		String previous = super.getValue();
		if (s == null) {
			hasValue = false;
		} else if (s.equals("")) {
			hasValue = false;
			super.setValue(s);
		} else {
			hasValue = true;
			super.setValue(s);
		}
		analyseChange(previous, s, e);
	}

	private void analyseChange(String previous, String new_, EventObject e) {
		if ((previous != null) && (new_ != null)) {
			if (!previous.equals(new_)) {
				notifyChange(e);
				return;
			}
		}
		if ((previous == null) && (new_ != null)) {
			notifyChange(e);
			return;
		}
		if ((previous != null) && (new_ == null)) {
			notifyChange(e);
			return;
		}
	}

	private void notifyChange(EventObject e) {
		if (container != null) {
			ElementEvent ev = ElementEvent.creationModificationEvent(container, this.getName());
			ev.setParent(new CasualEvent(e));
			container.fireElementEvent(ev);
		}
	}


	@Override
	public void setValue(String s) {
		this.setValueInternal(s, new CasualEvent("Set attribute event"));
//		containedObject = null;
	}
	
	public void setValue(String s, EventObject e) {
		this.setValueInternal(s, e);
//		containedObject = null;
	}	
	
	public void setValue(int i) {
		this.setValue(i, new CasualEvent("Set attribute event"));
	}	

	public void setValue(int i, EventObject ev) {
		this.setValueInternal(i + "", ev);
//		containedObject = null;
	}
	
	public void setValue(double i) {
		this.setValue(i, new CasualEvent("Set attribute event"));
	}	

	public void setValue(double d, EventObject ev) {
		this.setValueInternal(d + "", ev);
//		containedObject = null;
	}
	
	public void setValue(float i) {
		this.setValue(i, new CasualEvent("Set attribute event"));
	}	

	public void setValue(float f, EventObject ev) {
//		containedObject = null;
		this.setValueInternal(f + "", ev);
	}
	
	public void setValue(char i) {
		this.setValue(i, new CasualEvent("Set attribute event"));
	}	

	public void setValue(char c, EventObject ev) {
//		containedObject = null;
		this.setValueInternal(c + "", ev);
	}
	
	public void setValue(long i) {
		this.setValue(i,null);
	}	

	public void setValue(long l, EventObject ev) {
//		containedObject = null;
		this.setValueInternal(l + "", ev);
	}
	
	public void setValue(Object o) {
		setValue(o, new CasualEvent("Set attribute event"));
	}
	
	public void setValue(Object o,EventObject ev) {
		if (o != null) {
	//		containedObject = o;
			if (o.getClass().isArray()) {
				if (o instanceof int[]) {
					this.setValueInternal(java.util.Arrays.toString((int[])o), ev);
				} else if (o instanceof long[]) {
					this.setValueInternal(java.util.Arrays.toString((long[])o), ev);
				} else if (o instanceof short[]) {
					this.setValueInternal(java.util.Arrays.toString((short[])o), ev);
				} else if (o instanceof byte[]) {
					this.setValueInternal(java.util.Arrays.toString((byte[])o), ev);
				} else if (o instanceof char[]) {
					this.setValueInternal(java.util.Arrays.toString((char[])o), ev);
				} else if (o instanceof double[]) {
					this.setValueInternal(java.util.Arrays.toString((double[])o), ev);
				} else if (o instanceof float[]) {
					this.setValueInternal(java.util.Arrays.toString((float[])o), ev);
				} else if (o instanceof boolean[]) {
					this.setValueInternal(java.util.Arrays.toString((boolean[])o), ev);
				} else if (o instanceof Object[]) {
					this.setValueInternal(java.util.Arrays.toString((Object[])o), ev);
				}
			} else {
				this.setValueInternal(o.toString(), ev);
			}
		} else {
			this.setValueInternal("null", ev);
		}

	}

	/**
	 * Returns the associated <code>AbstractElementContainer</code>.
	 * <br>#author lchatela
	 */
	public AbstractElementContainer getContainer() {
		return container;
	}

	/**
	 * Sets the associated <code>AbstractElementContainer</code>.
	 * <br>#author lchatela
	 */
	public void setContainer(AbstractElementContainer cont) {
		container = cont;
	}

	@Override
	public String asXML() {
		if (hasValue) {
			return keyword.toString() + "=\"" + super.getValue() + "\"";
		} else {
			return "";
		}
	}

	@Override
	public void write(Writer wr) throws IOException {
		if (hasValue) {
			wr.write(keyword.toString() + "=\"" + super.getValue() + "\"");
		}
	}

	@Override
	public String getName() {
		return keyword.toString();
	}

	public Keyword getKeyword() {
		return keyword;
	}

	public void transfer(JavancoXMLElement from, JavancoXMLElement to) {
		linkedElements.remove(from);
		linkedElements.add(to);
		to.add(this);
	}

	@Override
	public Element getParent() {
		return null;
	}

	public boolean textEquals(NetworkAttribute other) {
		return ((other.value.equals(this.value)) && (other.keyword.equals(this.keyword)));
	}


	public void addLinkedElement(JavancoXMLElement parent) {
		if (linkedElements == null) {
			linkedElements = new ArrayList<JavancoXMLElement>(1);
		}
		if ((linkedElements.size() < 1) || (keyword.isCore())) {
			linkedElements.add(parent);
		} else {
			throw new org.dom4j.IllegalAddException(parent.getBackedElement(), this, " Attribute is not core and has already been linked");
		}
	}


	/**
	 * Returns the XML element to which the current attribute is linked.
	 * If attribute is not linked to any element (floating attribute), method
	 * returns <code>null</code>. If attribute is linked to more than one
	 * element, the first one is returned. To know all elements linked, use the
	 * methods <code>getLinkedElements()</code>.
	 */
	public JavancoXMLElement getLinkedElement() {
		if (linkedElements != null) {
			if (linkedElements.size() == 1) {
				return linkedElements.get(0);
			}
		}
		return null;
	}

	public JavancoXMLElement removeLinkedElement(JavancoXMLElement parent) {
		if (linkedElements != null) {
			linkedElements.remove(parent);
			return parent;
		}
		return null;
	}

	public List<JavancoXMLElement> getLinkedElements() {
		return linkedElements;
	}

	/**
	 * Links the current attribute with the "main_description""
	 */
	public void link() {
		this.getContainer().linkAttribute(this.getName());
	}
	/**
	 * Links the current attribute with the specified element
	 */
	public void link(String toElement) {
		this.getContainer().linkAttribute(this.getName(), toElement);
	}

	public boolean hasParent() {
		if (linkedElements != null) {
			if (linkedElements.size() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Node detach() {
		return null;
	}

	public int intValue() {
		try {
			return Integer.parseInt(super.getValue());
		}
		catch (NumberFormatException e) {}
		try {
			return (int)(Double.parseDouble(super.getValue()));
		}
		catch (NumberFormatException e) {}
		return -1;
	}

	public float floatValue() {
		return Float.parseFloat(super.getValue());
	}

	public float floatValue(float default_) {
		try {
			return Float.parseFloat(super.getValue());
		}
		catch (NumberFormatException e) {
			return default_;
		}
	}

	public double doubleValue() {
		return Double.parseDouble(super.getValue());
	}

	public boolean booleanValue() {
		return Boolean.parseBoolean(super.getValue());
	}

	public java.awt.Color colorValue() {
		return TypeParser.parseColor(super.getValue());
	}

	public java.awt.Color colorValue(java.awt.Color default_) {
		java.awt.Color c = TypeParser.parseColor(super.getValue());
		if (c == null) {
			return default_;
		} else {
			return c;
		}
	}

	public int positionValue() {
		return TypeParser.parsePosition(super.getValue());
	}

	public int[] intArrayValue() {
		return TypeParser.parseIntArray(super.getValue());
	}
	
	public String toString() {
		return getName()+"="+getValue();
	}
}
