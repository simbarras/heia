package ch.epfl.javanco.xml;

import java.util.Collection;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Map;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.event.ElementListener;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;


public class GraphicalDataHandler implements XMLDataHandler, ElementListener {

	private JavancoXMLElement visualRepresentation = null;

	private static final String elementName = "graphical_data";

	public static final String BACKGROUND_ELEMENT = "background";
	public static final String STARTVIEW_ELEMENT = "startview";
	public static final String DEFAULT_VALUES_ELEMENT = "defaultValues";
	public static final String BACKGROUND_IMAGE_ELEMENT = "backgroundImage";
	public static final String DISPLAY             = "display";

	private boolean eventEnabled = true;

	/**
	 * Contains for each variable which corresponds to an attribute in the XML document
	 *  the relative xpath to this attribute from &lt;graphical_data&gt;.
	 * <br>#author lchatela
	 */
	private java.util.Hashtable<String, String[]> xpathTable;
	private java.util.Hashtable<String, JavancoXMLElement> elementTable;

	private Hashtable<String, String> cache = new Hashtable<String, String>();

	private AbstractGraphHandler agh;

//	private UIDelegate uiDel;

	public GraphicalDataHandler(AbstractGraphHandler agh) {
	//	this.uiDel = agh.getUIDelegate();
		this.agh = agh;
		elementTable = new Hashtable<String, JavancoXMLElement>();
		initXpathTable();
	}

	private void setLinksOffsets(LinkContainer lCont, EventObject e) {
		Collection<LinkContainer> c = lCont.getParallelLinkContainers(true);
		if (c.size() == 1) return;
		
		int val = 1;//-(c.size() - 1);
		for (LinkContainer link : c) {
			link.attribute(XMLTagKeywords.OFFSET).setValue(val, e);
			val += 1;
		}
	}

	public void nodeCreated(NodeContainer nodeContainer, ElementEvent e){
		nodeContainer.linkAttribute(XMLTagKeywords.POS_X, XMLTagKeywords.MAIN_DESCRIPTION.toString());
		nodeContainer.linkAttribute(XMLTagKeywords.POS_Y, XMLTagKeywords.MAIN_DESCRIPTION.toString());
		nodeContainer.linkAttribute(XMLTagKeywords.LABEL, XMLTagKeywords.MAIN_DESCRIPTION.toString());
	}



	public void linkCreated(LinkContainer lc, ElementEvent e){
		if (eventEnabled) {
			setLinksOffsets(lc, e);
		}
	}
	public void layerCreated(LayerContainer llc , ElementEvent e){}

	public void nodeSuppressed(NodeContainer nc, ElementEvent e) {}
	public void linkSuppressed(LinkContainer lc, ElementEvent e) {
		if (eventEnabled) {
			this.setLinksOffsets(lc, e);
		}
	}
	public void layerSuppressed(LayerContainer llc, ElementEvent e) {}

	public void beginBigChanges(CasualEvent ev) {
		eventEnabled = false;
	}
	public void endBigChanges(CasualEvent ev) {
		eventEnabled = true;
	}

	public void elementModified(ElementEvent e){}
	public void allElementsModified(CasualEvent e){}
	public void graphLoaded(EventObject o) {}

	/**
	 * Initializes the <code>xpathTable</code>, which contains for each variable which
	 * corresponds to an attribute in the XML document the relative xpath to this attribute
	 * from &lt;graphical_data&gt;.
	 * <br>#author lchatela
	 */
	private void initXpathTable() {
		xpathTable = new java.util.Hashtable<String, String[]>();
		/* background */
		xpathTable.put("backgroundColor", new String[]{BACKGROUND_ELEMENT,"color",""});
		/* backgroundImage */
		xpathTable.put("backImage", new String[]{BACKGROUND_IMAGE_ELEMENT,"name",""});
		xpathTable.put("backgroundImageLeftUpCorner_x", new String[]{BACKGROUND_IMAGE_ELEMENT,"pos_x","0"});
		xpathTable.put("backgroundImageLeftUpCorner_y", new String[]{BACKGROUND_IMAGE_ELEMENT,"pos_y","0"});
		xpathTable.put("backgroundImageSize_width", new String[]{BACKGROUND_IMAGE_ELEMENT,"width","0"});
		xpathTable.put("backgroundImageSize_height", new String[]{BACKGROUND_IMAGE_ELEMENT,"height","0"});
		/* startview */
		xpathTable.put("viewRectangle_x", new String[]{STARTVIEW_ELEMENT,"pos_x","0"});
		xpathTable.put("viewRectangle_y", new String[]{STARTVIEW_ELEMENT,"pos_y","0"});
		xpathTable.put("viewRectangle_width", new String[]{STARTVIEW_ELEMENT,"width","1"});
		xpathTable.put("viewRectangle_height", new String[]{STARTVIEW_ELEMENT,"height","1"});
		xpathTable.put("sizeModificator", new String[]{STARTVIEW_ELEMENT,"sizeModificator","1"});
		/* defaultValues */
		xpathTable.put("defaultNodeSize", new String[]{DEFAULT_VALUES_ELEMENT,"nodeSize","32"});
		xpathTable.put("defaultLinkWidth", new String[]{DEFAULT_VALUES_ELEMENT,"linkWidth","2"});
		xpathTable.put("defaultLabelFontSize", new String[]{DEFAULT_VALUES_ELEMENT,"labelFontSize","9"});
		xpathTable.put("defaultNodeColor", new String[]{DEFAULT_VALUES_ELEMENT,"nodeColor","#FFFFFF"});
		xpathTable.put("defaultNodeIcon", new String[]{DEFAULT_VALUES_ELEMENT,"nodeIcon","node.png"});
		xpathTable.put("defaultLinkColor", new String[]{DEFAULT_VALUES_ELEMENT,"linkColor","#000000"});
		xpathTable.put("defaultLinkColor", new String[]{DEFAULT_VALUES_ELEMENT,"linkColor","#7F7F7F"});
		/* displaySize */
//		xpathTable.put("displaySize_x", new String[]{DISPLAY,"width","100"});
//		xpathTable.put("displaySize_y", new String[]{DISPLAY,"height","100"});
		xpathTable.put("displayType", new String[]{DISPLAY,"type",""});
	}

	/**
	 * Returns the xpath in the XML document corresponding to the variable
	 * with the given name. The xpath is relative to the &lt;graphical_data&gt; node.<br/>
	 * This method should be used whenever one wants to retrieve or update the value
	 * of the attribute corresponding to the variable.
	 * <br>#author lchatela
	 * @param varName The name of the variable we are interested in. If we want to access
	 * 		a field of a complex typed variable, like "variable.x", <code>varName</code>
	 * 		should be "variable_x".
	 * @return The xpath of the attribute corresponding to the given variable name, relative
	 * 		to the &lt;graphical_data&gt; node.
	 */
	private String[] getXpath(String varName) {
		String[] toReturn = xpathTable.get(varName);
		if (toReturn == null) {
			throw new IllegalStateException("No field named \"" + varName + "\" in xpathTable.");
		}
		return toReturn;
	}

	public JavancoXMLElement getXML() {
		if (visualRepresentation == null) {
			visualRepresentation = new JavancoXMLElement(elementName);
		}
		return visualRepresentation;
	}
	
	private JavancoXMLElement getElement(String s) {
		JavancoXMLElement el = elementTable.get(s);
		if (el == null) {
			el = new JavancoXMLElement(s);
			getXML().add(el);
			elementTable.put(s, el);
		}
		return el;		
	}

	public JavancoXMLElement getCanonicalXML() {
		JavancoXMLElement elem = new JavancoXMLElement(elementName);
		for (Map.Entry<String, String[]> entry : xpathTable.entrySet()) {
			String[] path = entry.getValue();
			JavancoXMLElement el = elem.element(path[0]);
			if (el == null) {
				el = new JavancoXMLElement(path[0]);
				elem.add(el);
			}
			NetworkAttribute att = new NetworkAttribute(path[1],attribute(entry.getKey()).getValue(), null);
			el.add(att);
		}
		return elem;
	}

	public NetworkAttribute attribute(String variableName) {
		return attribute(variableName,true);
	}

	public NetworkAttribute attribute(String variableName, boolean createIfNull) {
		String[] path = getXpath(variableName);
		if (path == null) {
			throw new NullPointerException("No attribute of name " + variableName + " exists in this handler");
		}
		JavancoXMLElement el = getElement(path[0]);
		NetworkAttribute att = (NetworkAttribute)el.attribute(path[1]);
		
	//	NetworkAttribute att = (NetworkAttribute)getXML().getBackedElement().selectSingleNode(path[0] + "/@" + path[1]);
		if (att != null) {
			cache.remove(variableName);
		} else {
			if (createIfNull) {
				att = createAttribute(variableName, path[2]);
			}
		}
		return att;
	}

	private NetworkAttribute createAttribute(String variableName, String value) {
		String[] path = getXpath(variableName);
		JavancoXMLElement el = getElement(path[0]);
		NetworkAttribute toReturn = new NetworkAttribute(path[1],value, null);
		el.add(toReturn);
		return toReturn;
	}

	public void setXML(JavancoXMLElement e) {
		visualRepresentation = e;
		
		for (JavancoXMLElement el : e.elements()) {
			elementTable.put(el.getName(), el);
		}

		agh.fireAllElementsModificationEvent(ElementEvent.getAllElementEvent());
	}

	public String getXMLElementName() {
		return elementName;
	}

}