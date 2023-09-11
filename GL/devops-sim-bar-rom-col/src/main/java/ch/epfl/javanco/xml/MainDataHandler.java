package ch.epfl.javanco.xml;

import java.util.EventObject;
import java.util.List;

import org.dom4j.tree.DefaultAttribute;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.event.ElementListener;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.Layer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.Link;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.Node;
import ch.epfl.javanco.network.NodeContainer;

public class MainDataHandler
implements ElementListener,
XMLDataHandler {

	private final static String elementName = XMLTagKeywords.MAIN_DESCRIPTION.toString();

	/**
	 * Root element of the main data
	 */
	private JavancoXMLElement topologyElement = null;

	private AbstractGraphHandler handler = null;

	public MainDataHandler(AbstractGraphHandler handler) {
		this.handler = handler;
	}

	private JavancoXMLElement getTopologyElement() {
		if (topologyElement == null) {
			topologyElement = new JavancoXMLElement(this.getXMLElementName());
		}
		return topologyElement;
	}


	/*	private void setTopologyElement(JavancoXMLElement e) {
		topologyElement = e;
	}*/

	/**
	 * Returns the name of the XML Element associated to this handler. As
	 * the topology handler is responsible for the topology, its element
	 * is the "&lt;main_description&gt;" element and thus this method returns
	 * "main_description"
	 * @return The string "main_description"
	 */
	public String getXMLElementName() {
		return elementName;
	}


	public void setXML(JavancoXMLElement e) {
		topologyElement = e;
	}

	/*	public void setXMLForced(AbAbstractGraphHandler handler, JavancoXMLElement e) {
		e = e.element(elementName);
		setInternal(handler, e);
	}*/

	/**
	 * Parse the element given in parameter and build a new topology with the contained
	 * information. XML information is used to create layers, nodes and links. The whole
	 * network structure is created, with all <code>NodeContainer</code> etc. All network
	 * objects are instanced using the default implementation classes.
	 * @param e The ("main_description") element that contains the topology that will be handled
	 * by this object.
	 */
	public static void setXML(AbstractGraphHandler handler, JavancoXMLElement e) {
		e = e.element(elementName);

		if (e == null) {
			System.out.println("Unable to read graph, unkown format, no main_description element");
			return;
		}
		if (handler.getLayerContainers().size() > 0) {
			//	throw new IllegalStateException("Cannot store a graph read from XML in a not emply AGH");
			handler.clearLayers(false);
		}
		setInternal(handler, e);
		//	setTopologyElement(e);

	}


	private static void setInternal(AbstractGraphHandler handler, JavancoXMLElement e) {
		try {
			Class<? extends Layer> cla = handler.getUIDelegate().getTypeCreationAdapter().getSelectedLayerType();
			Class<? extends Node> cno = handler.getUIDelegate().getTypeCreationAdapter().getSelectedNodeType();
			Class<? extends Link> cli = handler.getUIDelegate().getTypeCreationAdapter().getSelectedLinkType();
			boolean eventEnabled = handler.isEventEnabled();
			if (eventEnabled) {
				handler.setModificationEventEnabledWithoutCallingBigChanges(false);
			}
			List<JavancoXMLElement> layerElementList = e.selectElements(XMLTagKeywords.LAYER.toString());
			for (JavancoXMLElement readedLayer : layerElementList) {
				LayerContainer layerC = new LayerContainer(cla, handler);
				layerC.setElement(readedLayer, elementName);
				handler.newLayer(layerC);

				List<JavancoXMLElement> nodeElementList = readedLayer.selectElements(XMLTagKeywords.NODE.toString());
				for (JavancoXMLElement readedNode : nodeElementList) {
					NodeContainer nodeC = new NodeContainer(cno);
					nodeC.setElement(readedNode, elementName);
					handler.newNode(nodeC);
				}
				List<JavancoXMLElement> linkElementList = readedLayer.selectElements(XMLTagKeywords.LINK.toString());
				for (JavancoXMLElement readedLink : linkElementList) {
					LinkContainer linkC = new LinkContainer(cli);
					linkC.setElement(readedLink, elementName);
					handler.newLink(linkC);
				}
			}
			if (eventEnabled) {
				handler.setModificationEventEnabledWithoutCallingBigChanges(true);
			}
			handler.fireAllElementsModificationEvent(ElementEvent.getAllElementEvent());
		}
		catch (InstantiationException ex) {
			// if in the future one permit the use of other DefaultImpl classes
			// than the DefaultLayerImpl, DefaultNodeImpl etc, this assertation should be removed
			// and replaced by a normal exception handling.
			assert(0==1) : "should never be there. Exception during instanciation of DefaultImpl";
		}
	}

	/**
	 * Returns the XML representation of the currently stored topology.
	 * @return The XML representation of the currently stored topology.
	 */
	public JavancoXMLElement getXML() {
		return getTopologyElement();
	}

	public JavancoXMLElement getCanonicalXML() {
		JavancoXMLElement mainDesc = new JavancoXMLElement(elementName);
		for (LayerContainer layC : handler.getLayerContainers()) {
			JavancoXMLElement layEl = canonicalInternal(mainDesc, XMLTagKeywords.LAYER.toString(), layC);
			for (NodeContainer nodC : layC.getNodeContainers()) {
				canonicalInternal(layEl, XMLTagKeywords.NODE.toString(), nodC);
			}
			for (LinkContainer linkC : layC.getLinkContainers()) {
				canonicalInternal(layEl, XMLTagKeywords.LINK.toString(), linkC);
			}
		}
		return mainDesc;
	}


	private JavancoXMLElement canonicalInternal(JavancoXMLElement target, String name, AbstractElementContainer aec) {
		JavancoXMLElement contEl = new JavancoXMLElement(name);
		target.add(contEl);
		for (NetworkAttribute att : aec.getSortedAttributes()) {
			if (att.getKeyword().isCore() || att.isLinkedTo(elementName)) {
				contEl.add(new DefaultAttribute(att.getName(), att.getValue()));
			}
		}
		return contEl;
	}

	public void elementModified(ElementEvent e) {}
	public void allElementsModified(CasualEvent e){}
	public void graphLoaded(EventObject o) {}
	public void beginBigChanges(CasualEvent ev) {}
	public void endBigChanges(CasualEvent ev) {}

	public void layerCreated(LayerContainer layerContainer, ElementEvent e) {
		if (layerContainer.getElement(this.getXMLElementName()) == null) {
			JavancoXMLElement newLayerElement = layerContainer.createXMLElement(this.getXMLElementName());
			getTopologyElement().add(newLayerElement);
		}
		layerContainer.linkAttribute(XMLTagKeywords.ID, this.getXMLElementName());
	}

	public void nodeCreated(NodeContainer nodeContainer, ElementEvent e) {
		if (nodeContainer.getElement(this.getXMLElementName()) == null) {
			JavancoXMLElement newNodeElement = nodeContainer.createXMLElement(this.getXMLElementName());
			LayerContainer currentlyEditedLayer = handler.getEditedLayer();
			if (currentlyEditedLayer.getElement(getXMLElementName()) == null) {
				layerCreated(currentlyEditedLayer, e);
			}
			currentlyEditedLayer.getElement(this.getXMLElementName()).add(newNodeElement);
		}
		nodeContainer.attribute(XMLTagKeywords.ID).setValue(nodeContainer.getIndex(), e);
		nodeContainer.linkAttribute(XMLTagKeywords.ID, this.getXMLElementName());
	}


	public void linkCreated(LinkContainer linkContainer, ElementEvent e) {
		if (linkContainer.getElement(this.getXMLElementName()) == null) {
			JavancoXMLElement newLinkElement = linkContainer.createXMLElement(this.getXMLElementName());
			LayerContainer currentlyEditedLayer = handler.getEditedLayer();
			if (currentlyEditedLayer.getElement(getXMLElementName()) == null) {
				layerCreated(currentlyEditedLayer, e);
			}
			currentlyEditedLayer.getElement(this.getXMLElementName()).add(newLinkElement);
		}
		linkContainer.attribute(XMLTagKeywords.ORIG).setValue(linkContainer.getStartNodeIndex() + "", e);
		linkContainer.attribute(XMLTagKeywords.DEST).setValue(linkContainer.getEndNodeIndex() + "", e);
		linkContainer.linkAttribute(XMLTagKeywords.ORIG, this.getXMLElementName());
		linkContainer.linkAttribute(XMLTagKeywords.DEST, this.getXMLElementName());
	}

	public void nodeSuppressed(NodeContainer nodeC, ElementEvent e) {
		LayerContainer layerC = nodeC.getContainingLayerContainer();
		layerC.getElement(this.getXMLElementName()).remove(nodeC.getElement(this.getXMLElementName()));
		nodeC.removeXMLElement(this.getXMLElementName());
	}

	public void linkSuppressed(LinkContainer linkC, ElementEvent e) {
		LayerContainer layerC = linkC.getContainingLayerContainer();
		layerC.getElement(this.getXMLElementName()).remove(linkC.getElement(this.getXMLElementName()));
		linkC.removeXMLElement(this.getXMLElementName());
	}

	public void layerSuppressed(LayerContainer layerC, ElementEvent e) {
		this.getTopologyElement().remove(layerC.getElement(this.getXMLElementName()));
		layerC.removeXMLElement(this.getXMLElementName());
	}
}
