package ch.epfl.javanco.xml;

import java.util.EventObject;
import java.util.List;

import org.dom4j.tree.DefaultAttribute;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.event.ElementListener;
import ch.epfl.javanco.network.AbstractElement;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class AdditionalDataHandler
implements ElementListener,
XMLDataHandler {

	private static final XMLTagKeywords elementName = XMLTagKeywords.ADDITIONAL_DESCRIPTION;

	private AbstractGraphHandler handler = null;

	private JavancoXMLElement additionalElement = null;
	private JavancoXMLElement layersElement = null;
	private JavancoXMLElement nodesElement = null;
	private JavancoXMLElement linksElement = null;
	//	private JavancoXMLElement networkElement = null;

	public AdditionalDataHandler(AbstractGraphHandler handler) {
		this.handler = handler;
	}

	private void reset() {
		additionalElement = null;
		layersElement = null;
		nodesElement = null;
		linksElement = null;
	}

	private JavancoXMLElement getAdditionalElement() {
		if (additionalElement == null) {
			additionalElement = new JavancoXMLElement(this.getXMLElementName());
		}
		return additionalElement;
	}

	private void setAdditionalElement(JavancoXMLElement e) {
		if (e == null) {
			throw new NullPointerException();
		}
		additionalElement = e;
	}

	private JavancoXMLElement getLayersElement() {
		if (layersElement == null) {
			layersElement = new JavancoXMLElement(XMLTagKeywords.LAYERS.toString());
			getAdditionalElement().add(layersElement);
		}
		return layersElement;
	}

	private void setLayersElement(JavancoXMLElement  e) {
		if (e == null) {
			throw new NullPointerException();
		}
		layersElement = e;
	}

	private JavancoXMLElement getNodesElement() {
		if (nodesElement == null) {
			nodesElement = new JavancoXMLElement(XMLTagKeywords.NODES.toString());
			getAdditionalElement().add(nodesElement);
		}
		return nodesElement;
	}

	private void setNodesElement(JavancoXMLElement  e) {
		if (e == null) {
			throw new NullPointerException();
		}
		nodesElement = e;
	}

	private JavancoXMLElement getLinksElement() {
		if (linksElement == null) {
			linksElement = new JavancoXMLElement(XMLTagKeywords.LINKS.toString());
			getAdditionalElement().add(linksElement);
		}
		return linksElement;
	}

	private void setLinksElement(JavancoXMLElement  e) {
		if (e == null) {
			throw new NullPointerException();
		}
		linksElement = e;
	}

	public String getXMLElementName() {
		return elementName.toString();
	}

	public void setXML(JavancoXMLElement  e) {
		if (e == null) {
			System.out.println("No additional_description element");
			return;
		}
		reset();

		// select the "<layers>" block in the whole block
		JavancoXMLElement layersNew = e.selectSingleElement(XMLTagKeywords.LAYERS.toString());
		setLayersXML(layersNew);

		JavancoXMLElement nodesNew = e.selectSingleElement(XMLTagKeywords.NODES.toString());
		setNodesXML(nodesNew);

		JavancoXMLElement linksNew = e.selectSingleElement(XMLTagKeywords.LINKS.toString());
		setLinksXML(linksNew);

		//	JavancoXMLElement networkNew = e.selectSingleElement(XMLTagKeywords.GENERAL.toString());
		//	setJavancoXMLElement(networkNew);

		setAdditionalElement(e);
	}

	private void setLayersXML(JavancoXMLElement  layersElement) {
		if (layersElement != null) {
			// select all "<layer>" elements contains in this block
			List<JavancoXMLElement> layerElementList = layersElement.selectElements(XMLTagKeywords.LAYER.toString());

			// for each <layer> element contained in the readed document
			for (JavancoXMLElement readedLayer : layerElementList) {

				if (readedLayer.attributes().size() > 0) {
					// find layerName
					String layerName = readedLayer.attribute(XMLTagKeywords.ID.toString()).getValue();

					// using its name, find its associated contained
					LayerContainer layerC = handler.getLayerContainer(layerName);

					// if container already exists, attach it with the new readed element.
					// this will replace an eventually existing element (and according this
					// element is attached to layersElement, detach it)
					if (layerC != null) {
						layerC.setElement(readedLayer, getXMLElementName());
					} else {
						throw new IllegalStateException("No LayerContainer associated to layer "+layerName);
					}
					
					/*
					 * previously, at this point, XML subelement could be parsed to recontruct the
					 * specific object contained in the node. This feature has been removed
					 * for now
					 */
					
					/*
					// Object typed reconstruction
					JavancoXMLElement toConstruct = readedLayer.selectSingleElement("child::properties");
					if (toConstruct != null) {
					String className = readedLayer.attributeValue("class");
					if (className != null) {
						layerC.setContainedElement(construct(className, toConstruct));
					}
					}*/
				}
			}

			// normally, all element present in the previous version of the <layers>
			// block should have been replaced. However, if the new version does not
			// define additional information for one object, its element will always be
			// in the previous version and thus must be transferred to the new version
			for (JavancoXMLElement  el : getLayersElement().elements()) {
				el.detach();
				layersElement.add(el);
			}
			setLayersElement(layersElement);
		}
	}

	private void setNodesXML(JavancoXMLElement  nodesElement) {
		if (nodesElement != null) {
			List<JavancoXMLElement> nodeElementList = nodesElement.selectElements(XMLTagKeywords.NODE.toString());
			for (JavancoXMLElement readedNode : nodeElementList) {
				if (readedNode.attributes().size() >0) {

					int nodeId = Integer.parseInt(readedNode.attribute(XMLTagKeywords.ID.toString()).getValue());

					NodeContainer nodeC = handler.getNodeContainer(nodeId);

					if (nodeC != null) {
						nodeC.setElement(readedNode, getXMLElementName());
					} else {
						throw new IllegalStateException("No NodeContainer associated to node "+nodeId);
					}
					// Object typed reconstruction
					
					/*
					 * previously, at this point, XML subelement could be parsed to recontruct the
					 * specific object contained in the node. This feature has been removed
					 * for now
					 */
					/*
					JavancoXMLElement toConstruct = readedNode.selectSingleElement("child::properties");
					if (toConstruct != null) {
					String className = readedNode.attributeValue("class");
					if (className != null) {
						nodeC.setContainedElement(construct(className, toConstruct));
					}
					}*/
				}
			}
			for (JavancoXMLElement  el : getNodesElement().elements()) {
				el.detach();
				nodesElement.add(el);
			}
			setNodesElement(nodesElement);
		}
	}

	private void setLinksXML(JavancoXMLElement  linksElement) {
		if (linksElement != null) {
			List<JavancoXMLElement> linkElementList = linksElement.selectElements(XMLTagKeywords.LINK.toString());
			for (JavancoXMLElement readedLink : linkElementList) {

				if (readedLink.attributes().size() > 0) {

					int orig = Integer.parseInt(readedLink.attribute(XMLTagKeywords.ORIG.toString()).getValue());
					int dest = Integer.parseInt(readedLink.attribute(XMLTagKeywords.DEST.toString()).getValue());
					String layerName = readedLink.attributeValue(XMLTagKeywords.ON_LAYER.toString());

					LinkContainer linkC = handler.getLinkContainer(orig, dest, layerName);

					if (linkC != null) {
						linkC.setElement(readedLink, getXMLElementName());
					} else {
						throw new IllegalStateException("No LinkContainer associated to link ("+orig+", "+dest+")");
					}

					
					/*
					 * previously, at this point, XML subelement could be parsed to recontruct the
					 * specific object contained in the node. This feature has been removed
					 * for now
					 */
					/*
					// Object typed reconstruction
					JavancoXMLElement toConstruct = readedLink.selectSingleElement("child::properties");					
					if (toConstruct != null) {
					String className = readedLink.attributeValue("class");
					if (className != null) {
						linkC.setContainedElement(construct(className, toConstruct));
					}
					}*/
				}
			}

			for (JavancoXMLElement  el : getLinksElement().elements()) {
				el.detach();
				linksElement.add(el);
			}
			setLinksElement(linksElement);
		}
	}

	/**Should probably be replaced by save XML
	 **/
	public JavancoXMLElement getXML() {
		JavancoXMLElement copy = (JavancoXMLElement)getAdditionalElement().clone();

		for (JavancoXMLElement e : copy.selectElements("descendant::*")) {
			AbstractElementContainer container = e.getAssociatedContainer();
			if (container != null) {
				boolean needDefiningAttributes = false;
				for (NetworkAttribute att : container.attributes()) {
					if ((att.hasParent() == false) && (e.attribute(att.getName()) == null)) {
						e.add(att);
						needDefiningAttributes = true;
					}
				}
				if (needDefiningAttributes) {
					container.writeAllDefiningAttributes(e);
				}
				encode(container, e);
			}
		}
		// if element was completely empty
		if (copy.removeVoidElements()) {
			return null;
		} else {
			return copy;
		}
	}

	public JavancoXMLElement getCanonicalXML() {
		JavancoXMLElement element = new JavancoXMLElement(this.getXMLElementName());

		JavancoXMLElement layers = new JavancoXMLElement(XMLTagKeywords.LAYERS.toString());
		JavancoXMLElement nodes = new JavancoXMLElement(XMLTagKeywords.NODES.toString());
		JavancoXMLElement links = new JavancoXMLElement(XMLTagKeywords.LINKS.toString());
		element.add(layers);
		element.add(nodes);
		element.add(links);
		for (LayerContainer layer : handler.getLayerContainers()) {
			JavancoXMLElement layEl = new JavancoXMLElement(XMLTagKeywords.LAYER.toString());
			canonicalInternal(layers, layEl, layer);
		}
		for (NodeContainer node : handler.getNodeContainers()) {
			JavancoXMLElement noEl = new JavancoXMLElement(XMLTagKeywords.NODE.toString());
			canonicalInternal(nodes, noEl, node);
		}
		for (LinkContainer link : handler.getLinkContainers()) {
			JavancoXMLElement liEl = new JavancoXMLElement(XMLTagKeywords.LINK.toString());
			canonicalInternal(links, liEl, link);
		}
		return element;
	}

	private void canonicalInternal(JavancoXMLElement group, JavancoXMLElement elem, AbstractElementContainer aec) {
		for (NetworkAttribute att : elem.attributes()) {
			if (att.getKeyword().isCore() || att.isLinkedTo(elem.getName())) {
				DefaultAttribute nAtt = new DefaultAttribute(att.getName(), att.getValue());
				elem.add(nAtt);
			}
		}
		group.add(elem);
	}

	@Override
	public String toString() {
		return "AdditionalInformationHandler";
	}

	/**
	 * Encodes the <code>AbstractElement</code> contained in the given container as a &lt;properties&gt; element,
	 * and adds it to the given "parent" element.
	 * <br>#author lchatela
	 * @param container The container containing the network object to encode
	 * @param parent The parent element of the "properties" element
	 */
	private void encode(AbstractElementContainer container, JavancoXMLElement parent) {
		AbstractElement contEl = container.getContainedElement();
		if (contEl != null) {
			contEl.encodeIn(parent);
		}
	}

	public void elementModified(ElementEvent e) {}
	public void allElementsModified(CasualEvent e) {}
	public void graphLoaded(EventObject e) {}

	public void beginBigChanges(CasualEvent ev) {}
	public void endBigChanges(CasualEvent ev) {}

	public void layerCreated(LayerContainer layerContainer, ElementEvent e) {
		JavancoXMLElement layerXMLElement = layerContainer.getElement(this.getXMLElementName());
		if (layerXMLElement == null) {
			layerXMLElement = layerContainer.createXMLElement(this.getXMLElementName());
			getLayersElement().add(layerXMLElement);
		}
		//		getLayerList().add(layerContainer);
	}

	public void nodeCreated(NodeContainer nodeContainer, ElementEvent e) {
		JavancoXMLElement nodeXMLElement = nodeContainer.getElement(this.getXMLElementName());
		if (nodeXMLElement == null) {
			nodeXMLElement = nodeContainer.createXMLElement(this.getXMLElementName());
			getNodesElement().add(nodeXMLElement);
		}
		//		getNodeList().add(nodeContainer);
	}


	public void linkCreated(LinkContainer linkContainer, ElementEvent e) {
		JavancoXMLElement linkXMLElement = linkContainer.getElement(this.getXMLElementName());
		if (linkXMLElement == null) {
			linkXMLElement = linkContainer.createXMLElement(this.getXMLElementName());
			getLinksElement().add(linkXMLElement);
		}
		//		getLinkList().add(linkContainer);
	}

	public void nodeSuppressed(NodeContainer nodeC, ElementEvent e) {
		getNodesElement().remove(nodeC.getElement(this.getXMLElementName()));
		nodeC.removeXMLElement(this.getXMLElementName());
		//	getNodeList().remove(nodeC);
	}

	public void linkSuppressed(LinkContainer linkC, ElementEvent e) {
		getLinksElement().remove(linkC.getElement(this.getXMLElementName()));
		linkC.removeXMLElement(this.getXMLElementName());
		//	getLinkList().remove(linkC);
	}

	public void layerSuppressed(LayerContainer layerC, ElementEvent e) {
		getLayersElement().remove(layerC.getElement(this.getXMLElementName()));
		layerC.removeXMLElement(this.getXMLElementName());
		//		getLayerList().remove(layerC);
	}

}