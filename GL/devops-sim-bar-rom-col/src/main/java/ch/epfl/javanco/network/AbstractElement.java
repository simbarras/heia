package ch.epfl.javanco.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Branch;
import org.dom4j.Node;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.xml.JavancoXMLElement;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLSerialisationManager;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * This class is meant to be the common super-class for all Network objects,
 * including the auto-generated ones. Thus, it implements a mechanism permitting
 * the extraction of the value of the fields contained in these objects.<br/>
 * This mechanism uses XML trees to store the values of the fields, following these conventions:
 * <ul>
 * <li> each <code>AbstractElement</code> is encoded in one "properties" element,
 * <li> the fields whose type is a primitive one or <code>String</code> are encoded as attributes of the
 * 		"properties" element, following this scheme : "field_name = value"
 * <li> the fields whose type is not a primitive one are encoded in "property" sub-elements.
 * 		Each "property" element contains an attribute "property_name" which stores the name of the field, and
 * 		a sub-element corresponding to the value of the field encoded by
 * 		<code>java.beans.XMLEncoder</code>.
 * <li> an AbstractElement which doesn't contain any field is not encoded. Thus there won't be any
 * 		"properties" element.
 * </ul>
 * An example of an <code>AbstractElement</code> encoded, with 3 fields:
 * <ul>
 * <li> <code>field1</code> is a <code>long</code>,
 * <li> <code>field2</code> is a <code>String</code>,
 * <li> <code>field3</code> is a </code>java.util.Date</code>.
 * </ul>
 * <br/>
 * <pre>
 * &lt;properties field1="0" field2="abc"&gt;
 * 	&lt;property property_name="field3"&gt;
 * 		&lt;object class="java.util.Date"&gt;
 * 			&lt;long&gt;1150131714738&lt;/long&gt;
 * 		&lt;/object&gt;
 * 	&lt;/property&gt;
 * &lt;/properties&gt;
 * </pre>
 * @author lchatela
 *
 */
public abstract class AbstractElement extends Object implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface DoNotSerialize {
	}


	private AbstractElementContainer container = null;


	/** Returns the associated <code>AbstractElementContainer</code> */
	public AbstractElementContainer getContainer() {
		return container;
	}

	/** Sets the associated <code>AbstractElementContainer</code> */
	public void setContainer(AbstractElementContainer container) {
		this.container = container;
	}

	/**
	 * Permits the definition of the value fields of this object
	 * using the data contained into an XML element.
	 * @param e The &lt;properties&gt; element that must be used to set the values
	 * @param errorBuffer The <code>OutputStream</code> on which the errors are to be written
	 */
	public void construct(JavancoXMLElement e, OutputStream errorBuffer) {
		if (!(e.getName().equals("properties"))) {
			throw new IllegalStateException("The element used for the reconstruction is not a <properties> element.");
		}

		List<NetworkAttribute> attributeList = /*(List<NetworkAttribute>)*/e.attributes();
		for (Node node : attributeList) {
			set(node.getName(), node.getText(), errorBuffer);
		}
		// handles the complex type parameters, encoded by XMLEncoder
		String xPath = "child::property";
		List<JavancoXMLElement> propertyList = e.selectElements(xPath);
		for (JavancoXMLElement el : propertyList) {
			String name = el.attributeValue("property_name");
			JavancoXMLElement toDecode = el.selectSingleElement("child::*");
			StringBuffer buffer = new StringBuffer();
			buffer.append(" <java version='1.0' class='java.beans.XMLDecoder'>");
			// modif par seb, pour test
			if (toDecode != null) {
				buffer.append(toDecode.asXML());
			}
			buffer.append("</java>");
			java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(buffer.toString().getBytes())));
			Object value = decoder.readObject();
			set(name, value, errorBuffer);
		}
	}

	/**
	 * Returns the setter method corresponding to the given field.<br>
	 * Remark: this method uses <code>getDeclaredMethods()</code> instead of using
	 * <code>getDeclaredFields()</code> because <code>field.set(obj,value)</code>
	 * cannot modify the fields declared in <code>AbstractElement</code>'s subclasses
	 * specified as "private".
	 * @param fieldName The name of the field
	 * @param errorBuffer The <code>OutputStream</code> on which to write the errors
	 * @return the setter method corresponding to the given field
	 */
	private Method getFieldSetter(String fieldName, OutputStream errorBuffer) {
		Method[] mArray = this.getClass().getDeclaredMethods();
		String methodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1, fieldName.length());
		for (Method m : mArray) {
			if (m.getName().equals(methodName)) {
				if (m.getParameterTypes().length == 1) {
					return m;
				}
			}
		}
		writeErrorMessage(errorBuffer, "A problem occured while changing the value " +
				"of the field \""+ fieldName+"\". (Reason: could not find \""+methodName+"\").\n");
		return null;
	}

	/**
	 * Sets the given field to the given value, previously transformed into an object
	 * of the correct type.
	 * @param name The name of the field
	 * @param value The value to set (as a <code>String</code>)
	 * @param errorBuffer The <code>OutputStream</code> on which to write the errors
	 */
	protected void set(String name, String value, OutputStream errorBuffer) {
		Method setter = getFieldSetter(name, errorBuffer);
		if (setter == null) {
			return;
		}
		Object objectValue = null;

		// finds the correct Object value for the String value
		if (value.equals("null")) {
			objectValue = null;
		} else {
			Class<?> parameterType = setter.getParameterTypes()[0];

			// String
			if (parameterType == String.class) {
				objectValue = value;
			}
			// primitive types or corresponding object classes
			else {
				try {
					if (parameterType == int.class || parameterType == Integer.class) {
						objectValue = Integer.parseInt(value);
					}
					else if (parameterType == boolean.class || parameterType == Boolean.class) {
						objectValue = Boolean.parseBoolean(value);
					}
					else if (parameterType == double.class || parameterType == Double.class) {
						objectValue = Double.parseDouble(value);
					}
					else if (parameterType == float.class || parameterType == Float.class) {
						objectValue = Float.parseFloat(value);
					}
					else if ((parameterType == char.class || parameterType == Character.class ) && value.length() == 1) {
						objectValue = value.charAt(0);
					}
					else if (parameterType == byte.class || parameterType == Byte.class) {
						objectValue = Byte.parseByte(value);
					}
					else if (parameterType == short.class || parameterType == Short.class) {
						objectValue = Short.parseShort(value);
					}
					else if (parameterType == long.class || parameterType == Long.class) {
						objectValue = Long.parseLong(value);
					} else {
						writeErrorMessage(errorBuffer, "A problem occured while changing the value " +
								"of the field \"" + name+ "\". (Reason: \"" + value + "\" is not of type \"" +
								parameterType + " or \"" + parameterType + "\" is not a simple type).\n");
						return;
					}
				} catch (NumberFormatException ex) {
					writeErrorMessage(errorBuffer, "A problem occured while changing the value " +
							"of the field \""+ name+"\". (Reason: "+ex.toString()+").\n");
					return;
				}
			}
		}
		set(name, setter, objectValue, errorBuffer);
	}

	/**
	 * Sets the field with the given name to the given value.
	 * @param name The name of the field
	 * @param value The value to set to the field (already as a correct object)
	 * @param errorBuffer The <code>OutputStream</code> on which to write the errors
	 */
	protected void set(String name, Object value, OutputStream errorBuffer) {
		Method setter = getFieldSetter(name, errorBuffer);
		if (setter == null) {
			return;
		}
		set(name, setter, value, errorBuffer);
	}

	private void set(String name, Method setter, Object value, OutputStream errorBuffer) {
		try {
			setter.invoke(this, value);
		} catch (IllegalAccessException e) {
			writeErrorMessage(errorBuffer, "A problem occured while changing the value " +
					"of the field \""+ name+"\". (Reason: "+e.getMessage()+").\n");
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			writeErrorMessage(errorBuffer, "A problem occured while changing the value " +
					"of the field \""+ name+"\". (Reason: "+e.getMessage()+").\n");
		}
	}

	/**
	 * Permits the extraction of the value of the fields contained in this object as a
	 * <code>JavancoXMLElement</code> called "properties". This element is added as a sub-element
	 * of the given <code>parent</code>, after having removed the previous one if any. <br/>
	 * @param parent The element in which to add this encoded object (i.e. a "layer", "node" or "link"
	 * sub-element of additional description).
	 */
	public void encodeIn(JavancoXMLElement parent) {
		// removes the possible existing "properties" sub-element
		JavancoXMLElement toRemove = parent.selectSingleElement("child::properties");
		if (toRemove != null) {
			parent.remove(toRemove);
		}
		// creates the new "properties" element
		JavancoXMLElement newProperties = this.encode();
		if (newProperties != null) {
			if (newProperties instanceof Branch) {
				ch.epfl.javanco.xml.NetworkDocumentHelper.clearDefaultTextElement((Branch)newProperties);
			}
			parent.add(newProperties);
		}
	}

	/**
	 * Returns the "properties" XML element having as sub-elements several elements, which contain
	 * the data values of the object.
	 * @return an XML element containing all value stored by this object
	 */
	private JavancoXMLElement encode() {
		Method[] mArray = this.getClass().getDeclaredMethods();
		if (mArray.length != 0) {
			JavancoXMLElement toReturn = new JavancoXMLElement("properties", true);
			for (Method m : mArray) {
				if (m.getAnnotation(DoNotSerialize.class) == null) {
					if ((m.getName().startsWith("get")) || (m.getName().startsWith("is"))) {
						if (m.getParameterTypes().length == 0) {
							buildElement(toReturn, m);
						}
					}
				}
			}
			return toReturn;
		}
		return null;
	}

	/**
	 * This method take as parameter a get[Something] or an is[Something] method. It will
	 * extract the [Something] out of its name, transform it into a [something] string
	 * (making the first char to lower case), encode the field corresponding to this string
	 * into an attribute or an element and add it to <code>properties</code>.
	 * @param properties The <code>properties</code> element which will contain all the encoded value fields
	 * @param m The getter method corresponding to the value field to encode
	 */
	private void buildElement(JavancoXMLElement properties, Method m) {
		String parameterName = m.getName();
		if (parameterName.startsWith("get")) {
			parameterName = parameterName.replaceFirst("get","");
		} else {
			parameterName = parameterName.replaceFirst("is","");
		}
		parameterName = parameterName.substring(0,1).toLowerCase() + parameterName.substring(1, parameterName.length());
		try {
			Object value = m.invoke(this, new Object[]{});
			Class<?> returnType = m.getReturnType();
			if ( returnType.isPrimitive()
					|| returnType == Integer.class
					|| returnType == Boolean.class
					|| returnType == Double.class
					|| returnType == Float.class
					|| returnType == String.class
					|| returnType == Character.class
					|| returnType == Byte.class
					|| returnType == Short.class
					|| returnType == Long.class)
			{
				// "simple" type
				properties.add(new NetworkAttribute(XMLTagKeywords.parse(parameterName), String.valueOf(value), true, this.container));
			} else {
				// "complex" type
				JavancoXMLElement property = new JavancoXMLElement("property", true);
				property.add(new NetworkAttribute(XMLTagKeywords.parse("property_name"), parameterName, true, this.container));
				// writes the encoded field in the output buffer
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(output);
				encoder.flush();
				output.reset();
				encoder.writeObject(value);
				encoder.flush();
				// reads the buffer and creates the xml tree
				ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
				JavancoXMLElement el = null;
				try {
					el = XMLSerialisationManager.openNetwork(new java.io.InputStreamReader(input),(AbstractGraphHandler)null);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
				/*	NetworkDocumentFactory factory = NetworkDocumentFactory.getInstance();
				factory.lock();
				SAXReader reader = new SAXReader(factory);
				 */	try {/*
					org.dom4j.Document doc = reader.read(input);
					JavancoXMLElement toAdd = doc.getRootElement();*/
					 property.add(el);
					 properties.add(property);
					 output.close();
					 input.close();
					 /*	} catch (org.dom4j.DocumentException e) {
					throw new IllegalStateException(e);
					  */	} catch (java.io.IOException e) {
						  throw new IllegalStateException(e);
					  } /*finally {
					factory.unlock();
				}*/
			}
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException(ex);
		} catch (java.lang.reflect.InvocationTargetException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Writes the given message into the given stream.
	 * @param stream The <code>OutputStream</code> on which to write the error messages
	 * @param message The error message to write
	 */
	private void writeErrorMessage(OutputStream stream, String message) {
		try {
			stream.write(message.getBytes());
		} catch (java.io.IOException e) {
			throw new IllegalStateException(e);
		}
	}
}