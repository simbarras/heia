package ch.epfl.javanco.network;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.path.JavancoShortestPath;
import ch.epfl.javanco.xml.JavancoXMLElement;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;


public class LinkContainer extends AbstractElementContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4202790999848021032L;

	private Link link = null;

	private int startNodeIndex = Integer.MIN_VALUE;
	private int endNodeIndex = Integer.MIN_VALUE;

//	private boolean startNodeDefined = false;
//	private boolean endNodeDefined = false;

	private LayerContainer containingLayerContainer = null;

	@SuppressWarnings("unchecked")
	public LinkContainer(Class<? extends Link> c) throws InstantiationException {
		super((Class<? extends AbstractElement>)c);
		if (c != null) {
			link = (Link)this.getInstance((Class<? extends AbstractElement>)c);
		}
		link.setContainer(this);
	}

	public LinkContainer(Class<? extends Link> c, int orig, int dest) throws InstantiationException {
		this(c);
		setStartNodeIndex(orig);
		setEndNodeIndex(dest);
	}

	public LinkContainer(int orig, int dest, Link _link) {
		super(_link);
		if (_link != null) {
			link = _link;
			link.setContainer(this);
		}
		setStartNodeIndex(orig);
		setEndNodeIndex(dest);
	}
	
	public boolean isPartOfACycle() {
		return isPartOfACycle(false);
	}

	public boolean isPartOfACycle(boolean directed) {
		Collection<LinkContainer> onLayer = getContainingLayerContainer().getLinkContainers();
		onLayer.remove(this);
		if (directed == false) {
			onLayer.remove(getContainingLayerContainer().getLinkContainer(getEndNodeIndex(), getStartNodeIndex()));
		}
		JavancoShortestPath sp = new JavancoShortestPath(getAbstractGraphHandler().getNodeContainers(), onLayer);
		Object o = sp.getPath(this.getStartNodeIndex(), this.getEndNodeIndex());
		return (o != null);
	}

/*	public boolean hasStartNodeDefined() {
		return startNodeDefined;
	}

	public boolean hasEndNodeDefined() {
		return endNodeDefined;
	}*/
	
	public boolean hasStartIndexSmallerThanEnd() {
		return startNodeIndex < endNodeIndex;
	}

	public boolean hasOneExtremitySharedWith(LinkContainer alter) {
		int t1 = getStartNodeIndex();
		if (t1 == alter.getStartNodeIndex() || t1 == alter.getEndNodeIndex()) {
			return true;
		}
		t1 = getEndNodeIndex();
		if (t1 == alter.getStartNodeIndex() || t1 == alter.getEndNodeIndex()) {
			return true;
		}
		return false;
	}

	public boolean hasForExtremity(int i) {
		return (i == getStartNodeIndex() || i == getEndNodeIndex());
	}

	public boolean hasExtremities(int ext1, int ext2) {
		return (((startNodeIndex == ext1) && (endNodeIndex == ext2)) ||
				((startNodeIndex == ext2) && (endNodeIndex == ext1))
		);
	}

	public Path toPath() {
		Path p = new Path();
		p.add(this.startNodeIndex);
		p.add(this.endNodeIndex);
		return p;
	}
	
	private static final String LINK_CURVE_START_ANGLE_PROP = XMLTagKeywords.LINK_CURVE_START_ANGLE.toString();
	private static final String LINK_CURVE_END_ANGLE_PROP = XMLTagKeywords.LINK_CURVE_END_ANGLE.toString();
	private static final String LINK_CURVE_START_PROP = XMLTagKeywords.LINK_CURVE_START.toString();	
	private static final String LINK_CURVE_END_PROP = XMLTagKeywords.LINK_CURVE_END.toString();	
	private static final String LINK_COLOR_PROP = XMLTagKeywords.LINK_COLOR.toString();
	
	NetworkAttribute linkCurveStartAngle;
	NetworkAttribute linkCurveEndAngle;
	NetworkAttribute linkCurveStart;
	NetworkAttribute linkCurveEnd;
	NetworkAttribute linkColor;
	
	public void setElement(JavancoXMLElement element, String key) {
		super.setElement(element, key);
		linkCurveStartAngle = this.attribute(LINK_CURVE_START_ANGLE_PROP, false);
		linkCurveEndAngle = this.attribute(LINK_CURVE_END_ANGLE_PROP, false);
		linkCurveStart = this.attribute(LINK_CURVE_START_PROP, false);
		linkCurveEnd = this.attribute(LINK_CURVE_END_PROP, false);
	}
	
	@Override
	public void fireElementEvent(ElementEvent ev) {
		super.fireElementEvent(ev);
		if (ev.concernsProperty(LINK_CURVE_START_ANGLE_PROP)) {
			linkCurveStartAngle = this.attribute(LINK_CURVE_START_ANGLE_PROP);
		}
		if (ev.concernsProperty(LINK_CURVE_END_ANGLE_PROP)) {
			linkCurveEndAngle = this.attribute(LINK_CURVE_END_ANGLE_PROP);
		}
		if (ev.concernsProperty(LINK_CURVE_START_PROP)) {
			linkCurveStart = this.attribute(LINK_CURVE_START_PROP);
		}
		if (ev.concernsProperty(LINK_CURVE_END_PROP)) {
			linkCurveEnd = this.attribute(LINK_CURVE_END_PROP);
		}	
		if (ev.concernsProperty(LINK_COLOR_PROP)) {
			linkColor = this.attribute(LINK_COLOR_PROP);
		}			
	}	

	public synchronized void setStartNodeIndex(int idx) throws IllegalStateException{
	//	if (startNodeDefined == false){
			startNodeIndex = idx;
	/*	} else {
			throw new IllegalStateException("link's extremities indexes may only be set once");
		}
		startNodeDefined = true;*/
	}

	public synchronized void setEndNodeIndex(int idx) throws IllegalStateException {
	//	if (endNodeDefined == false) {
			endNodeIndex = idx;
	/*	} else {
			throw new IllegalStateException("link's extremities indexes may only be set once");
		}
		endNodeDefined = true;*/
	}

	public int getStartNodeIndex() {
		return this.startNodeIndex;
	}

	public int getEndNodeIndex() {
		return this.endNodeIndex;
	}

	public int getSmallerExtremityIndex() {
		return Math.min(startNodeIndex, endNodeIndex);
	}

	public int getBiggerExtremityIndex() {
		return Math.max(startNodeIndex, endNodeIndex);
	}

	public NodeContainer getSmallerExtremity() {
		return getNodeContainer(getSmallerExtremityIndex());
	}

	public NodeContainer getBiggerExtremity() {
		return getNodeContainer(getBiggerExtremityIndex());
	}

	public int getOtherNodeIndex(int idx) {
		if (idx == endNodeIndex) {
			return startNodeIndex;
		} else if (idx == startNodeIndex) {
			return endNodeIndex;
		} else {
			throw new IllegalStateException("error, getOtherNodeIndex called but given index is not valid");
		}
	}

	public LinkContainer flip() {
		int s = startNodeIndex;
		int e = endNodeIndex;
		LinkContainer alreadyFlipped = getContainingLayerContainer().getLinkContainer(e,s);
		if (alreadyFlipped != null) {
			throw new IllegalStateException("Cannot flip link : a link in the other direction already exists");
		}
		String editedName = getAbstractGraphHandler().getEditedLayer().getName();
		getAbstractGraphHandler().setEditedLayer(getContainingLayerContainer().getName());
		LinkContainer newL = getAbstractGraphHandler().newLink(e,s);
		this.copyAttributesTo(newL);
		getAbstractGraphHandler().removeElement(this);
		getAbstractGraphHandler().setEditedLayer(editedName);
		return newL;
	}

	@Override
	public XMLTagKeywords getContainedElementKeyword() {
		return XMLTagKeywords.LINK;
	}

	@Override
	public AbstractElement getContainedElement() {
		return (AbstractElement)getLink();
	}
	
	public Object getContainedObject() {
		return getLink();
	}

	/*
	 *  Already commented in AbstractElementContainer
	 *  #author lchatela
	 */
	@Override
	public AbstractElement setContainedElement(AbstractElement el) {
		if (el instanceof Link) {
			return (AbstractElement)setLink((Link) el);
		} else {
			throw new IllegalStateException("The ContainedElement class must be a subclass of AbstractLink");
		}
	}

	public void setContainingLayerContainer(LayerContainer layerContainer) {
		containingLayerContainer = layerContainer;
	}

	@Override
	public LayerContainer getContainingLayerContainer() {
		return containingLayerContainer;
	}

	/**
	 * Replaces the existing <code>AbstractLink</code> implementation object (if any)
	 * contained into the current container with the object given in parameter.<br>
	 * Note : the topological values (origin and destination node) of the old object
	 * will be kept and reintroduced into the new object.
	 * <br>#author lchatela
	 * @param _link The new <code>AbstractLink</code> to contain in this container
	 * @return The previously contained <code>AbstractLink</code> or <code>null</code> if
	 * 		there was no previous one.
	 */
	public Link setLink(Link _link) {
		Link toReturn = getLink();
		this.link = _link;
		link.setContainer(this);
		return toReturn;
	}

	/**
	 * Returns the <code>Link</code> object contained into this container.
	 * @return the <code>Link</code> object contained into this container
	 */
	public Link getLink() {
		return link;
	}
	/**
	 * Returns all links of the graph that are parallel to the current one.
	 * This method calls back the <code>getlinkContainers(int, int, boolean)</code> in
	 * <code>AbstractGraphHandler</code>.
	 * @return all links of the graph that are parallel to the current one.
	 */
	public Collection<LinkContainer> getParallelLinkContainers(boolean dir) {
		return getAbstractGraphHandler().getLinkContainers(this.startNodeIndex, this.endNodeIndex, dir);
	}

	/** Returns the NodeContainer of the start node of this link. This methods calls back
	 * the <code>getNodeContainer()</code> in <code>AbstractGraphHandler</code>
	 * @return the NodeContainer of the start node of this link
	 */
	public NodeContainer getStartNodeContainer() {
		return getNodeContainer(getStartNodeIndex());
	}

	/**
	 * Returns the NodeContainer of the end node of this link. This methods calls back
	 * the <code>getNodeContainer()</code> in <code>AbstractGraphHandler</code>
	 * @return the NodeContainer of the end node of this link
	 */
	public NodeContainer getEndNodeContainer() {
		return getNodeContainer(getEndNodeIndex());
	}
	/**
	 * Returns the NodeContainer of the extremity of the link that HAS NOT index
	 * equals to parameter i. This methods call back
	 * the <code>getNodeContainer()</code> in <code>AbstractGraphHandler</code>
	 * @return the NodeContainer of the extrimity that HAS NOT the given index
	 */
	public NodeContainer getOtherNodeContainer(int i) {
		return getNodeContainer(getOtherNodeIndex(i));
	}
	
	private NodeContainer getNodeContainer(int i) {
		if (containingLayerContainer == null) {
			System.out.println("test");
		}
		NodeContainer nc = containingLayerContainer.getNode(i);
		if (nc != null) return nc;
		return getAbstractGraphHandler().getNodeContainer(i);
	}

	@Override
	public void linkAllDefiningAttributes(String elementKeyword) {
		super.linkAllCoreAttributes(elementKeyword);
		if (!(elementKeyword.equals(XMLTagKeywords.MAIN_DESCRIPTION.toString()))) {
			NetworkAttribute att = attribute(XMLTagKeywords.ON_LAYER);
			att.setValue(getContainingLayerContainer().getName());
			this.linkAttribute(att, elementKeyword);
		}
	}

	@Override
	public void writeAllDefiningAttributes(JavancoXMLElement e) {
		if (super.writeAllCoreAttribute(e)) {
			e.add(new NetworkAttribute(XMLTagKeywords.ON_LAYER, getContainingLayerContainer().getName(), this));
		}
	}

	@Override
	public Collection<NetworkAttribute> getSortedAttributes() {
		ArrayList<NetworkAttribute> copy = new ArrayList<NetworkAttribute>(this.attributes());
		ArrayList<NetworkAttribute> toret = new ArrayList<NetworkAttribute>(copy.size());
		NetworkAttribute origA = attribute(XMLTagKeywords.ORIG);
		toret.add(origA);
		copy.remove(origA);
		NetworkAttribute destA = attribute(XMLTagKeywords.DEST);
		toret.add(destA);
		copy.remove(destA);
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
		return this.getStartNodeIndex() + "-" + this.getEndNodeIndex();
	}

	@Override
	public String toShortString() {
		return "LinkContainer_" + "[" + this.getStartNodeIndex() + "," + this.getEndNodeIndex() + "]";
	}

	@Override
	String getIdentifierKey() {
		return "L_" + 	this.getStartNodeIndex() + "," + this.getEndNodeIndex() + "_" + this.getContainingLayerContainer().getName();
	}

	/**
	 * Calls computeLinkLength with the null argument, which means using the "length"
	 * attribute name
	 * @return returns the applied length
	 */
	public double setGeodesicLinkLength() {
		return setGeodesicLinkLength(null);
	}

	/**
	 * Computes the length of the link and store it in an attribute.
	 * The parameter <code>s</code> fixes the name of the attribute. If <code>
	 * null</code>, the default value <code>length</code> will be used.
	 * Distance is computed according to the pos_x and pos_y attributes.
	 * pos_z (in case of 3D) is ignored.
	 * @return returns the applied length
	 */
	public double setGeodesicLinkLength(String s) {
		if (s == null) {
			s = XMLTagKeywords.LENGTH.toString();
		}
		double length = getGeodesicLinkLength();
		length = Math.round(length);
		attribute(s).setValue(length + "");
		linkAttribute(s,XMLTagKeywords.MAIN_DESCRIPTION.toString());
		return length;
	}

	public double getGeodesicLinkLength() {
		Point p = getStartNodeContainer().getCoordinate();
		Point p2 = getEndNodeContainer().getCoordinate();
		return Point2D.distance(p.x, p.y, p2.x, p2.y);
	}

	public NodePair toNodePair() {
		return new NodePair(this.getStartNodeIndex(), this.getEndNodeIndex());
	}

	public NetworkAttribute getCurveStartAngle() {
		return linkCurveStartAngle;
	}

	public NetworkAttribute getCurveEndAngle() {
		return linkCurveEndAngle;
				
	}

	public NetworkAttribute getCurveStart() {
		return linkCurveStart;
	}

	public NetworkAttribute getCurveEnd() {
		return linkCurveEnd;
	}

	public Point2D get2Dequation() {
		Point p = getStartNodeContainer().getCoordinate();
		Point p2 = getEndNodeContainer().getCoordinate();
		if (p.x == p2.x) {
			return new Point2D.Double(Double.POSITIVE_INFINITY, p.x);
		}
		double slope = ((double)p2.y - (double)p.y)/((double)p2.x - (double)p.x);
		double b = (double)p.y - slope*(double)p.x;
		return new Point2D.Double(slope, b);
	}

	public double getRelativePosition(Point p2) {
		Point p = getStartNodeContainer().getCoordinate();
		return Math.sqrt(Math.pow(p2.x - p.x, 2) + Math.pow(p2.y - p.y, 2));
	}

	public int[] getExtremities() {
		return new int[]{getSmallerExtremityIndex(), getBiggerExtremityIndex()};
	}


}