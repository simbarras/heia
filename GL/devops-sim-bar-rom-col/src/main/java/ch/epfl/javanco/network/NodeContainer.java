package ch.epfl.javanco.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.xml.JavancoXMLElement;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * Acts as a container for all Node implementation, delivering a complete abstraction
 * between the topology and the object representation.<br>
 * Nodes can take several different aspect in Javanco. They can be just points
 * in a topology, or a complex set of information, or even more. To face this differences,
 * a container mechanism is proposed. A container acts as a box, which can be full or
 * empty. This box will be used to represented the topology in the memory (which node is
 * linked with which one, etc. However, the box has a place to receive any implementation
 * of the <code>AbstractNode</code> class. Objects implementing this class may then
 * contain any value, fonction, or field, independently.
 *
 */
public class NodeContainer extends AbstractElementContainer implements Serializable, Comparable<NodeContainer> {

	private static final Logger logger = new Logger(NodeContainer.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5966089410379793371L;

	/* FIELDS */

	private LayerContainer containingLayerContainer = null;

	private int                           index = -1;
	private boolean 					  indexSet = false;

	private int pos_x;
	private int pos_y;
	private int pos_z;
	
	private NetworkAttribute pos_xAtt;
	private NetworkAttribute pos_yAtt;

	private boolean pos_z_def = false;

	/**
	 * The AbstractNode object contained in this node
	 */
	private Node node = null;


	/**
	 * Build a new NodeContainer with the given node as containing object.
	 * @param node The containing node
	 */
	@SuppressWarnings("unchecked")
	public NodeContainer(Node node) {
		super((Class<? extends AbstractElement>)node.getClass());
		this.node = node;
		this.node.setContainer(this);
		logger.trace("Node container created with AbstractNode " + node);
	}

	/**
	 * Build an empty container. Containing node can further be defined using the
	 * <code>setNode</code> methods.
	 */
	@SuppressWarnings("unchecked")
	public NodeContainer() {
		super((Class)null);
		logger.trace("Void node container created");
	}

	/**
	 * Build a new NodeContainer containing a new instance of the given class.
	 * @param c A class providing a constructor with no parameters.
	 * @throws InstantiationException If something happend during instantiation
	 * process.
	 */
	@SuppressWarnings("unchecked")
	public NodeContainer(Class<? extends Node> c) throws InstantiationException {
		super((Class<? extends AbstractElement>)c);
		node = (Node)this.getInstance((Class<? extends AbstractElement>)c);
		node.setContainer(this);
		logger.trace("Node container created using AbstractNode class " + c);
	}

	/**
	 * Returns the <code>XMLTagKeywords</code> associated to the type of the contained
	 * object. Method needed by the <code>AbstractElementContained</code> superclass.
	 * @return the <code>XMLTagKeywords</code> associated to the type of the contained object
	 */
	@Override
	public XMLTagKeywords getContainedElementKeyword() {
		return XMLTagKeywords.NODE;
	}

	public int compareTo(NodeContainer other) {
		NodeContainer otherNode = other;
		return this.index - otherNode.index;
	}

	private static final String POS_X_PROP =XMLTagKeywords.POS_X.toString();
	private static final String POS_Y_PROP =XMLTagKeywords.POS_Y.toString();
	private static final String POS_Z_PROP =XMLTagKeywords.POS_Z.toString();
	private static final String NODE_SIZE_PROP =XMLTagKeywords.NODE_SIZE.toString();
	private static final String NODE_ICON_PROP =XMLTagKeywords.NODE_ICON.toString();	
	private static final String NODE_SEE_ID_PROP =XMLTagKeywords.NODE_SEE_ID.toString();
	private static final String NODE_COLOR_PROP =XMLTagKeywords.NODE_COLOR.toString();
	private static final String NODE_LABEL_FONT_PROP =XMLTagKeywords.NODE_LABEL_FONT_SIZE.toString();
	
	NetworkAttribute nodeSizeAttribute;
	NetworkAttribute nodeIconAttribute;
	NetworkAttribute visibleAttribute;
	NetworkAttribute nodeColorAttribute;
	NetworkAttribute labelFontSizeAttribute;
	
	
	@Override
	public void fireElementEvent(ElementEvent ev) {
		super.fireElementEvent(ev);
		if (ev.concernsProperty(POS_X_PROP)
				|| ev.concernsProperty(POS_Y_PROP) ) {
			pos_x = this.attribute(POS_X_PROP).intValue();
			pos_y = this.attribute(POS_Y_PROP).intValue();
		}
		if (ev.concernsProperty(POS_Z_PROP)) {
			NetworkAttribute posZ = this.attribute(POS_Z_PROP, false);
			if (posZ != null) {
				pos_z = this.attribute(POS_Z_PROP).intValue();
				pos_z_def = true;
			}
		}
		if (ev.concernsProperty(NODE_SIZE_PROP)) {
			nodeSizeAttribute = this.attribute(NODE_SIZE_PROP);
		}
		if (ev.concernsProperty(NODE_ICON_PROP)) {
			nodeIconAttribute = this.attribute(NODE_ICON_PROP);
		}
		if (ev.concernsProperty(NODE_SEE_ID_PROP)) {
			visibleAttribute = this.attribute(NODE_SEE_ID_PROP);
		}
		if (ev.concernsProperty(NODE_COLOR_PROP)) {
			nodeColorAttribute = this.attribute(NODE_COLOR_PROP);
		}
		if (ev.concernsProperty(NODE_LABEL_FONT_PROP)) {
			labelFontSizeAttribute = this.attribute(NODE_LABEL_FONT_PROP);
		}			
		
		
	}

	@Override
	public void setElement(JavancoXMLElement element, String key) {
		super.setElement(element, key);
		pos_x = attribute(POS_X_PROP).intValue();
		pos_y = attribute(POS_Y_PROP).intValue();

		NetworkAttribute posZ = this.attribute(POS_Z_PROP, false);
		if (posZ != null) {
			pos_z = this.attribute(POS_Z_PROP).intValue();
			pos_z_def = true;
		}
	}

	/**
	 * Returns the contained node under the form of an <code>AbstractElement</code> (the
	 * superclass of the current class). Method needed by the
	 * <code>AbstractElementContainer</code> superclass.
	 * @return the contained node under the form of an <code>AbstractElement</code> or null
	 * if no contained object has been defined yet.
	 */
	@Override
	public AbstractElement getContainedElement() {
		return (AbstractElement)getNode();
	}
	
	@Override
	public Object getContainedObject() {
		return getNode();
	}

	@Override
	public AbstractElement setContainedElement(AbstractElement el) {
		if (el instanceof Node) {
			return (AbstractElement)setNode((Node) el);
		} else {
			throw new IllegalStateException("The ContainedElement class must be a subclass of AbstractNode");
		}
	}

	void setContainingLayerContainer(LayerContainer layerContainer) {
		logger.trace("Containing layer of node " + index + " is set to layer : \"" + layerContainer.getName()+"\"");
		containingLayerContainer = layerContainer;
	}

	@Override
	public LayerContainer getContainingLayerContainer() {
		return containingLayerContainer;
	}

	public int getIndex() {
		return index;
	}
	
	public double getDistanceTo(NodeContainer alt) {
		return ch.epfl.javanco.graphics.Util2DFunctions.distance(this, alt);
	}

	public void setIndex(int i) {
		if (indexSet == false) {
			logger.trace("Index of NodeContainer set to " + i);
			index = i;
			indexSet = true;
		} else {
			throw new IllegalStateException("Cannot set the index of a node twice. Use changeIndex instead");
		}
	}

	public void changeIndex(int newIndex) {
		if (indexSet == false) {
			throw new IllegalStateException("The index of the node must be set prior to change it");
		}
		containingLayerContainer.getAbstractGraphHandler().changeNodeIndex(this, newIndex);
	}

	void changeIndexInternal(int newIndex) {
		index = newIndex;
	}

	/**
	 * Replaces the existing <code>AbstractNode/code> implementation object (if any)
	 * contained into the current container with the object given in parameter.
	 * <br>#author lchatela
	 * @param node The new <code>AbstractNode</code> to contain in this container
	 * @return The previously contained <code>AbstractNode</code> or <code>null</code> if
	 * 		there was no previous one.
	 */
	public Node setNode(Node node) {
		Node toReturn = getNode();
		this.node = node;
		this.node.setContainer(this);
		logger.trace("AbstractNode of NodeContainer set to " + node);
		return toReturn;
	}

	/**
	 * Returns the contained node
	 * @return the contained node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Return all links having current node as starting point, on the node's containing layer.
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getOutgoingLinks() {
		return getContainingLayerContainer().getOutgoingLinks(index);
	}

	/**
	 * Return all links having current node as starting point, on all layers
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getAllOutgoingLinks() {
		List<LinkContainer> list = new ArrayList<LinkContainer>(4);
		for (LayerContainer lc : getAbstractGraphHandler().getLayerContainers()) {
			list.addAll(lc.getOutgoingLinks(index));
		}
		return list;
	}

	/**
	 * Return all links having current node as ending point, on the node's containing layer.
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getIncomingLinks() {
		return getContainingLayerContainer().getIncomingLinks(index);
	}

	/**
	 * Return all links having current node as ending point, on all layers
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getAllIncomingLinks() {
		List<LinkContainer> list = new ArrayList<LinkContainer>(4);
		for (LayerContainer lc : getAbstractGraphHandler().getLayerContainers()) {
			list.addAll(lc.getIncomingLinks(index));
		}
		return list;
	}

	public void removeAllOutgoingLinks() {
		List<LinkContainer> list = getAllOutgoingLinks();
		for (LinkContainer lc : list) {
			getAbstractGraphHandler().removeElement(lc);
		}
	}

	public void removeAllIncomingLinks() {
		List<LinkContainer> list = getAllIncomingLinks();
		for (LinkContainer lc : list) {
			getAbstractGraphHandler().removeElement(lc);
		}
	}
	
	public void removeAllLinks() {
		List<LinkContainer> list = getAllConnectedLinks();
		for (LinkContainer lc : list) {
			getAbstractGraphHandler().removeElement(lc);
		}
	}

	/**
	 * Return all links having current node as starting point, on the given layer.
	 * If no layer if specified (null), all layers are considered.
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getOutgoingLinks(String layerName) {
		if (layerName != null) {
			return getAbstractGraphHandler().getLayerContainer(layerName).getOutgoingLinks(index);
		} else {
			return getOutgoingLinks();
		}
	}

	public List<LinkContainer> getOutgoingLinks(LayerContainer layer) {
		return layer.getOutgoingLinks(index);
	}

	/**
	 * Return all links having current node as ending point, on the given layer.
	 * If no layer if specified (null), all layers are considered.
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getIncomingLinks(String layerName) {
		if (layerName != null) {
			return getAbstractGraphHandler().getLayerContainer(layerName).getIncomingLinks(index);
		} else {
			return getIncomingLinks();
		}
	}

	public List<LinkContainer> getIncomingLinks(LayerContainer layer) {
		return layer.getIncomingLinks(index);
	}
	/**
	 * Return all links having current node as starting or ending point, on its layer
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getConnectedLinks() {
		List<LinkContainer> coll = getOutgoingLinks();
		if (coll != null) {
			coll.addAll(getIncomingLinks());
		} else {
			coll = getIncomingLinks();
		}
		return coll;
	}

	/**
	 * Return all links having current node as starting or ending point, on all layers
	 * @return a <code>java.util.Collection</code> of <code>LinkContainer</code>
	 */
	public List<LinkContainer> getAllConnectedLinks() {
		List<LinkContainer> coll = getAllOutgoingLinks();
		if (coll != null) {
			coll.addAll(getAllIncomingLinks());
		} else {
			coll = getAllIncomingLinks();
		}
		return coll;
	}
	
	public List<NodeContainer> getAllConnectedLinkExtremities() {
		List<LinkContainer> ln = getAllConnectedLinks();
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public TreeSet<Integer> getAllConnectedLinksExtremityIndexes() {
		List<LinkContainer> ln = getAllConnectedLinks();
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}	

	/**
	 * Return all links having current as starting or ending point available on layer layerName
	 * If no layer if specified (null), all layers are considered.
	 */
	public List<LinkContainer> getConnectedLinks(String layerName) {
		if (layerName != null) {
			List<LinkContainer> coll = getOutgoingLinks(layerName);
			if (coll != null) {
				coll.addAll(getIncomingLinks(layerName));
			} else {
				coll = getIncomingLinks(layerName);
			}
			return coll;
		} else {
			return getConnectedLinks();
		}
	}

	public List<LinkContainer> getConnectedLinks(LayerContainer layer) {
		if (layer != null) {
			if (layer.isInTheSameGraph(this) == false) {
				throw new IllegalArgumentException("Objects of distincts graphs");
			}
			List<LinkContainer> coll = getOutgoingLinks(layer);
			if (coll != null) {
				coll.addAll(getIncomingLinks(layer));
			} else {
				coll = getIncomingLinks(layer);
			}
			return coll;
		} else {
			return getConnectedLinks();
		}
	}


	public List<LinkContainer> getAllLinksTo(int a) {
		List<LinkContainer> list = new ArrayList<LinkContainer>(2);
		for (LayerContainer lay : getAbstractGraphHandler().getLayerContainers()) {
			LinkContainer lc = lay.getLinkContainer(index, a);
			if (lc != null) {
				list.add(lc);
			}
		}
		return list;
	}
	
	public List<LinkContainer> getAllLinksFrom(int a) {
		List<LinkContainer> list = new ArrayList<LinkContainer>(2);
		for (LayerContainer lay : getAbstractGraphHandler().getLayerContainers()) {
			LinkContainer lc = lay.getLinkContainer(a, index);
			if (lc != null) {
				list.add(lc);
			}
		}
		return list;
	}
	
	public List<LinkContainer> getAllLinksWith(int a) {
		List<LinkContainer> list = new ArrayList<LinkContainer>(2);
		for (LayerContainer lay : getAbstractGraphHandler().getLayerContainers()) {
			LinkContainer lc = lay.getLinkContainer(a, index);
			if (lc != null) {
				list.add(lc);
			}
			lc = lay.getLinkContainer(index, a);
			if (lc != null) {
				list.add(lc);
			}			
		}
		return list;
	}
	
	public LinkContainer getLinkWith(int a) {
		LinkContainer lc = getLinkTo(a);
		if (lc != null) {
			return lc;
		} else {
			return getLinkFrom(a);
		}
	}

	/** 
	 * If current node has an outgoing link to a node with index a on its layer, return the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkTo(int a) {
		return getLinkTo(a, this.containingLayerContainer);
	}

	/** 
	 * If current node has an outgoing link to a node with index a on the provided layer, returns the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkTo(int a, LayerContainer layC) {
		LinkContainer lc = layC.getLinkContainer(index, a);
		if (lc != null) {
			return lc;
		}
		return null;
	}
	
	/** 
	 * If current node has an outgoing link to a node n on the provided layer, returns the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkTo(NodeContainer n, LayerContainer layC) {
		LinkContainer lc = layC.getLinkContainer(index, n.index);
		if (lc != null) {
			return lc;
		}
		return null;
	}	
	
	/** 
	 * If current node has an incoming link from node n on its layer, return the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkTo(NodeContainer n) {
		return getLinkTo(n, this.containingLayerContainer);
	}	
	
	/** 
	 * If current node has an incoming link from node  with index a on its layer, return the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkFrom(int a) {
		return getLinkFrom(a, this.containingLayerContainer);
	}
	
	/** 
	 * If current node has an incoming link from node n on its layer, return the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkFrom(NodeContainer n) {
		return getLinkFrom(n, this.containingLayerContainer);
	}	
	
	/** 
	 * If current node has an incoming link from node  with index a on the provided layer, returns the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkFrom(int a, LayerContainer layC) {
		LinkContainer lc = layC.getLinkContainer(a, index);
		if (lc != null) {
			return lc;
		}
		return null;
	}
	
	/** 
	 * If current node has an incoming link from node  with index a on the provided layer, returns the connecting link, else returns null
	 * @param a
	 * @return
	 */	
	public LinkContainer getLinkFrom(NodeContainer n, LayerContainer layC) {
		LinkContainer lc = layC.getLinkContainer(n.index, index);
		if (lc != null) {
			return lc;
		}
		return null;
	}	

	/**
	 * Get all connected links (on its layer) and returns the other extremities
	 * @return
	 */
	public List<NodeContainer> getConnectedNodes() {
		java.util.Stack<NodeContainer> neighboursList = new java.util.Stack<NodeContainer>();
		for (LinkContainer lc : this.getConnectedLinks()) {
			neighboursList.push(lc.getOtherNodeContainer(index));
		}
		return neighboursList;
	}

	public TreeSet<Integer> getConnectedNodeIndexes() {
		List<NodeContainer> ln = getConnectedNodes();
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (NodeContainer nc : ln) {
			indexes.add(nc.index);
		}
		return indexes;
	}

	public TreeSet<Integer> getConnectedNodeIndexes(String layerName) {
		List<NodeContainer> ln = getConnectedNodes(layerName);
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (NodeContainer nc : ln) {
			indexes.add(nc.index);
		}
		return indexes;
	}
	
	public TreeSet<Integer> getConnectedNodeIndexes(LayerContainer layer) {
		List<NodeContainer> ln = getConnectedNodes(layer);
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (NodeContainer nc : ln) {
			indexes.add(nc.index);
		}
		return indexes;
	}	

	public TreeSet<Integer> getOutgoingLinksExtremityIndexes() {
		List<LinkContainer> ln = getOutgoingLinks();
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}
	
	public TreeSet<Integer> getOutgoingLinksExtremityIndexes(String layerName) {
		List<LinkContainer> ln = getOutgoingLinks(layerName);
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}
	
	public TreeSet<Integer> getOutgoingLinksExtremityIndexes(LayerContainer layer) {
		List<LinkContainer> ln = getOutgoingLinks(layer);
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}	
	
	public TreeSet<Integer> getAllOutgoingLinksExtremityIndexes() {
		List<LinkContainer> ln = getAllOutgoingLinks();
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}	
	
	public List<NodeContainer> getAllOutgoingLinksExtremities() {
		List<LinkContainer> ln = getAllOutgoingLinks();
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public List<NodeContainer> getOutgoingLinksExtremities() {
		List<LinkContainer> ln = getOutgoingLinks();
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public List<NodeContainer> getOutgoingLinksExtremities(String layerName) {
		List<LinkContainer> ln = getOutgoingLinks(layerName);
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public List<NodeContainer> getOutgoingLinksExtremities(LayerContainer layC) {
		List<LinkContainer> ln = getOutgoingLinks(layC);
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public TreeSet<Integer> getAllIncomingLinksExtremityIndexes() {
		List<LinkContainer> ln = getAllIncomingLinks();
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}	
	
	public List<NodeContainer> getAllIncomingLinksExtremities() {
		List<LinkContainer> ln = getAllIncomingLinks();
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}	

	public List<NodeContainer> getIncomingLinksExtremities() {
		List<LinkContainer> ln = getIncomingLinks();
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public List<NodeContainer> getIncomingLinksExtremities(String layerName) {
		List<LinkContainer> ln = getIncomingLinks(layerName);
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public List<NodeContainer> getIncomingLinksExtremities(LayerContainer layC) {
		List<LinkContainer> ln = getIncomingLinks(layC);
		List<NodeContainer> toReturn = new ArrayList<NodeContainer>(ln.size());
		for (int i = 0 ; i < ln.size(); i++) {
			LinkContainer link = ln.get(i);
			toReturn.add(link.getOtherNodeContainer(this.index));
		}
		return toReturn;
	}
	
	public TreeSet<Integer> getIncomingLinksExtremityIndexes() {
		List<LinkContainer> ln = getIncomingLinks();
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}
	
	public TreeSet<Integer> getIncomingLinksExtremityIndexes(String layerName) {
		List<LinkContainer> ln = getIncomingLinks(layerName);
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}
	
	public TreeSet<Integer> getIncomingLinksExtremityIndexes(LayerContainer layer) {
		List<LinkContainer> ln = getIncomingLinks(layer);
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		for (LinkContainer lc : ln) {
			indexes.add(lc.getOtherNodeIndex(this.index));
		}
		return indexes;
	}	


	public List<NodeContainer> getConnectedNodes(String layerName) {
		java.util.Stack<NodeContainer> neighboursList = new java.util.Stack<NodeContainer>();
		for (LinkContainer lc : this.getConnectedLinks(layerName)) {
			neighboursList.push(lc.getOtherNodeContainer(index));
		}
		return neighboursList;
	}

	public List<NodeContainer> getConnectedNodes(LayerContainer layc) {
		java.util.Stack<NodeContainer> neighboursList = new java.util.Stack<NodeContainer>();
		for (LinkContainer lc : this.getConnectedLinks(layc)) {
			neighboursList.push(lc.getOtherNodeContainer(index));
		}
		return neighboursList;

	}

	/**
	 * Return the coordinates of the contained node.
	 * @return a java.awt.Point object representing the coordinates of the contained node
	 */
	public java.awt.Point getCoordinate() {
		//		int pos_x_cache = attribute(XMLTagKeywords.POS_X).positionValue();
		//		int pos_y_cache = attribute(XMLTagKeywords.POS_Y).positionValue();
		return new java.awt.Point(pos_x,pos_y);
	}

	public int getZ() {
		return pos_z;
	}

	public boolean isZDefined() {
		return pos_z_def;
	}

	@Override
	void linkAllDefiningAttributes(String elementKeyword) {
		super.linkAllCoreAttributes(elementKeyword);
	}

	@Override
	public void writeAllDefiningAttributes(JavancoXMLElement e) {
		super.writeAllCoreAttribute(e);
	}

	@Override
	public Collection<NetworkAttribute> getSortedAttributes() {
		ArrayList<NetworkAttribute> copy = new ArrayList<NetworkAttribute>(this.attributes());
		ArrayList<NetworkAttribute> toret = new ArrayList<NetworkAttribute>(copy.size());
		NetworkAttribute idA = attribute(XMLTagKeywords.ID);
		toret.add(idA);
		copy.remove(idA);
		TreeMap<String, NetworkAttribute> map = new TreeMap<String, NetworkAttribute>();
		for (NetworkAttribute att : copy) {
			map.put(att.getName(), att);
		}
		for (NetworkAttribute att : map.values()) {
			toret.add(att);
		}
		return toret;
	}
	

	public String toSimpleString() {
		return "" + index;
	}	

	@Override
	public String toShortString() {
		return "NodeContainer_id " + index;
	}

	@Override
	String getIdentifierKey() {
		return "N_" + 	index;
	}

	public static Comparator<NodeContainer> getComparator() {
		return new Comparator<NodeContainer>() {
			public int compare(NodeContainer n1, NodeContainer n2) {
				return n1.index - n2.index;
			}
		};
	}
	
	private void setPosXY() {
		if (pos_xAtt == null) {
			pos_xAtt = attribute(XMLTagKeywords.POS_X);
		}
		if (pos_yAtt == null) {
			pos_yAtt = attribute(XMLTagKeywords.POS_Y);
		}
 	}

	public int getX() {
		setPosXY();
		return pos_xAtt.intValue();
	}
	
	public int getY() {
		setPosXY();
		return pos_yAtt.intValue();
	}
	
	public void setX(int x) {
		setPosXY();
		pos_xAtt.setValue(x);
		pos_x = x;
	}
	
	public void setY(int y) {
		setPosXY();
		pos_yAtt.setValue(y);
		pos_y = y;
	}

	public NetworkAttribute getNodeSizeAttribute() {
		return nodeSizeAttribute;
	}

	public NetworkAttribute getNodeIconAttribute() {
		return nodeIconAttribute;
	}

	public NetworkAttribute getVisibleAttribute() {
		return visibleAttribute;
	}

	public NetworkAttribute getNodeColorAttribute() {
		return nodeColorAttribute;
	}

	public NetworkAttribute getLabelFontSizeAttribute() {
		return labelFontSizeAttribute;
	}


}