package ch.epfl.javanco.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.utils.ComponentCounter;
import ch.epfl.javanco.xml.JavancoXMLElement;
import ch.epfl.javanco.xml.Keyword;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;



public class LayerContainer extends AbstractElementContainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5326927225919387068L;

	/**
	 * 
	 */
	private static final Logger logger = new Logger(LayerContainer.class);

	/**
	 * The data structure containing the nodes and links.
	 */
	private transient AbstractLayerRepresentation dataStructure = null;

	private boolean displayed = true;

	private Layer 	layer = null;

	private int pos_z;
	
	private NetworkAttribute defaultLinkColor;
	private NetworkAttribute defaultLinkWidth;
	private NetworkAttribute defaultLabelColor;
	private NetworkAttribute defaultNodeColor;
	private NetworkAttribute defaultNodeIcon;

	//	private AbstractGraphHandler               mainHandler           = null;

	/**
	 * Returns the <code>Keyword</code> associated to this container, i.e
	 * <code>XMLTagKeywords.LAYER</code>
	 * @return <code>XMLTagKeywords.LAYER</code>
	 * @see ch.epfl.javanco.xml.XMLTagKeywords#LAYER
	 */
	@Override
	public Keyword getContainedElementKeyword() {
		return XMLTagKeywords.LAYER;
	}
	/**
	 * Return the class of the contained layer
	 * @return The class of the contained layer
	 */
	public Class<? extends Layer> getContainedLayerClass() {
		return layer.getClass();
	}

	@Override
	public LayerContainer getContainingLayerContainer() {
		return this;
	}

	/**
	 * Instantiate a new layer container, containing an instance of the class given
	 * as parameter.
	 * To create the data structure itself, this method will read the <code>
	 * ch.epfl.javanco.graphRepresentation</code> system property an use its
	 * content as class name to instanciate the structure object. An <code>
	 * InstantiationException</code> will be thrown if a problem occurs.
	 * @param c The class of the contained object
	 * @throws InstantiationException If any problem occured when dynamically
	 * instantiating the contained layer object or the data structure.
	 */
	@SuppressWarnings("unchecked")
	public LayerContainer(Class<? extends Layer> c, AbstractGraphHandler handler) throws InstantiationException {
		super((Class<? extends AbstractElement>)c);
		try {
			String representationProperty = Javanco.getProperty(Javanco.JAVANCO_NETWORK_DATA_STRUCTURE_PROPERTY);
			Class dataStructureClass = null;
			if (representationProperty == null) {
				dataStructureClass = VectorRepresentation.class;
			} else {
				dataStructureClass = Class.forName(representationProperty);
			}
			construct(c,handler, dataStructureClass);
		}
		catch (ClassNotFoundException e) {
			throw new InstantiationException("Error, graphRepresentation property probably not correctly set " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public LayerContainer(Class<? extends Layer> c, AbstractGraphHandler handler, Class dataStructureClass) throws InstantiationException {
		super((Class<? extends AbstractElement>)c);
		construct(c, handler, dataStructureClass);
	}

	@SuppressWarnings("unchecked")
	private void construct(Class<? extends Layer> c, AbstractGraphHandler handler, Class dataStructureClass) throws InstantiationException {
		//	mainHandler = handler;
		layer = (Layer)this.getInstance((Class<? extends AbstractElement>)c);
		layer.setContainer(this);
		try {
			logger.trace("Representation used for graph internal storage : " + dataStructureClass.getSimpleName());
			java.lang.reflect.Constructor cstr = dataStructureClass.getDeclaredConstructor(LayerContainer.class);
			dataStructure = (AbstractLayerRepresentation)(cstr.newInstance(this));
			for (NodeContainer nc : handler.getNodeContainers()) {
				dataStructure.addNode(nc, true);
			}
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			throw new InstantiationException("Error, graphRepresentation property probably not correctly set " + e.getMessage());
		}
		catch (NoSuchMethodException e) {
			throw new InstantiationException("Error, graphRepresentation property probably not correctly set " + e.getMessage());
		}
		catch (InstantiationException e) {
			throw new InstantiationException("Error, graphRepresentation property probably not correctly set " + e.getMessage());
		}
		catch (IllegalAccessException e) {
			throw new InstantiationException("Error, graphRepresentation property probably not correctly set " + e.getMessage());
		}
	}

	private final String POS_Z_PROP =XMLTagKeywords.POS_Z.toString();
	private final String DEFAULT_LINK_COLOR_PROP = XMLTagKeywords.DEFAULT_LINK_COLOR.toString();
	private final String DEFAULT_LINK_WIDTH_PROP = XMLTagKeywords.DEFAULT_LINK_WIDTH.toString();
	private final String DEFAULT_LABEL_COLOR_PROP = XMLTagKeywords.DEFAULT_LABEL_COLOR.toString();	
	private final String DEFAULT_NODE_COLOR_PROP = XMLTagKeywords.DEFAULT_NODE_COLOR.toString();	
	private final String DEFAULT_NODE_ICON_PROP = XMLTagKeywords.DEFAULT_NODE_ICON.toString();
	

	@Override
	public void fireElementEvent(ElementEvent ev) {
		super.fireElementEvent(ev);
		if (ev.concernsProperty(POS_Z_PROP)) {
			pos_z = this.attribute(POS_Z_PROP).intValue();
		}
		if (ev.concernsProperty(DEFAULT_LINK_COLOR_PROP)) {
			defaultLinkColor = this.attribute(DEFAULT_LINK_COLOR_PROP);
		}
		if (ev.concernsProperty(DEFAULT_LINK_WIDTH_PROP)) {
			defaultLinkWidth = this.attribute(DEFAULT_LINK_WIDTH_PROP);
		}
		if (ev.concernsProperty(DEFAULT_LABEL_COLOR_PROP)) {
			defaultLabelColor = this.attribute(DEFAULT_LABEL_COLOR_PROP);
		}
		if (ev.concernsProperty(DEFAULT_NODE_COLOR_PROP)) {
			defaultNodeColor = this.attribute(DEFAULT_NODE_COLOR_PROP);
		}	
		if (ev.concernsProperty(DEFAULT_NODE_ICON_PROP)) {
			defaultNodeIcon = this.attribute(DEFAULT_NODE_ICON_PROP);
		}			
	}

	@Override
	public void setElement(JavancoXMLElement element, String key) {
		super.setElement(element, key);
		pos_z = attribute(POS_Z_PROP).intValue();
	}

	public int getZ() {
		return pos_z;
	}

	/**
	 * Returns the key of this layer, i.e its name. This method returns
	 * exactly the same as the <code>getName()</code> method. Both are kept
	 * for convience and naming.
	 * @return the name of this layer
	 */
	public String getKey() {
		return this.attribute(XMLTagKeywords.ID).getValue();
	}

	/**
	 * Returns the name of the layer
	 * @return the layer's name
	 */
	public String getName() {
		return getKey();
	}

	/**
	 * Returns the containers of all elements contained on this layer.
	 * @return a <code>Collection</code> of <code>AbstractElement</code>
	 */
	public Collection<AbstractElementContainer> getElements() {
		return getAllElements();
	}

	/**
	 * Returns the link (and only the links !!!!!) contained in the linkcontainers contained on this layer.
	 * @return A <code>Collection</code> of <code>LinkContainer</code>
	 */
	public Collection<Link> getLinks() {
		Collection<LinkContainer> linksC = getLinkContainers();
		Collection<Link> c = new ArrayList<Link>(linksC.size());
		for (LinkContainer lc : linksC) {
			c.add(lc.getLink());
		}
		return c;
	}

	public float getAverageDegree() {
		return (float)(this.getLinkContainers().size()*2)/(float)this.handler.getNodeContainers().size();
	}

	public boolean containsConvexGraph() {
		ComponentCounter cc = new ComponentCounter(this.getAbstractGraphHandler(), getKey());
		return cc.isConvex();
	}

	public LinkContainer getUndirectedLink(int ext1, int ext2) {
		int min;
		int max;
		if (ext1 < ext2) {
			min = ext1;
			max = ext2;
		} else {
			max = ext1;
			min = ext2;
		}
		LinkContainer cc = getLinkContainer(min, max);
		if (cc != null) {
			return cc;
		} else {
			cc = getLinkContainer(max, min);
			if (cc != null) {
				return cc;
			} else {
				return null;
			}
		}
	}

	/**
	 * Used to denote a layer as a displayed one or invisible one
	 * @param display <code>true</code> to set this layer as displayed, <code>false
	 * </code> otherwise.
	 */
	public void setDisplayed(boolean display) {
		displayed = display;
		getAbstractGraphHandler().fireLayerEditedChangedEvent(this);
	}

	/**
	 * Returns the displayed flag
	 * @return <code>true</code> if layer is set as displayer, <code>false
	 * </code> otherwise.
	 */
	public boolean isDisplayed() {
		return displayed;
	}

	/**
	 * Returns the node container containing the node with the index given in
	 * parameter, if any exists on the current layer
	 * @return a <code>NodeContainer</code> corresponding to the given index
	 * @param id The index of the derised node.
	 */
	public NodeContainer getNode(int id) {
		return dataStructure.nodeAt(id);
	}

	/**
	 * Returns the link container containing the link with the given origin
	 * and destination index, if any exists on the current layer
	 * @return a <code>LinkContainer</code> corresponding to the given parameter,
	 * if any.
	 * @param orig The index to the origin of the desired node
	 * @param dest The index of the destination of the desired node
	 */
	public LinkContainer getLinkContainer(int orig, int dest) {
		Collection<LinkContainer> c = getLinkContainers(orig, dest);
		if (c.size() > 0) {
			return getLinkContainers(orig, dest).iterator().next();
		} else {
			return null;
		}
	}

	public LinkContainer getLinkContainer(int orig, int dest, boolean directed) {
		LinkContainer lc = getLinkContainer(orig, dest);
		if (directed) {
			return lc;
		}
		if (lc == null) {
			lc = getLinkContainer(dest, orig);
		}
		return lc;
	}

	public LinkContainer getLinkContainer(NodePair np) {
		return this.getLinkContainer(np.getStartNode(), np.getEndNode());
	}

	void removeInternal(AbstractElementContainer cont) {
		dataStructure.removeElement(cont);
	}

	void addNodeInternal(NodeContainer nodeContainer, boolean virtual) {
		dataStructure.addNode(nodeContainer, virtual);
		nodeContainer.setContainingLayerContainer(this);
	}

	void addLinkInternal(LinkContainer linkContainer) {
		dataStructure.addLink(linkContainer);
		linkContainer.setContainingLayerContainer(this);
	}

	/*
	 * Already commented in AbstractElementContainer
	 */
	@Override
	public AbstractElement getContainedElement() {
		return (AbstractElement)getLayer();
	}
	
	public Object getContainedObject() {
		return getLayer();
	}

	/*
	 * Already commented in AbstractElementContainer
	 * #author lchatela
	 */
	@Override
	public AbstractElement setContainedElement(AbstractElement el) {
		if (el instanceof Layer) {
			return (AbstractElement)setLayer((Layer)el);
		} else {
			throw new IllegalStateException("The ContainedElement class must be a subclass of AbstractLayer.");
		}
	}

	/** Returns the contained <code>AbstractLayer</code> object */
	public Layer getLayer() {
		return layer;
	}

	/**
	 * Replaces the existing <code>AbstractLayer</code> implementation object (if any)
	 * contained into the current container with the object given in parameter.
	 * <br>#author lchatela
	 * @param layer The new <code>AbstractLayer</code> to contain in this container
	 * @return The previously contained <code>AbstractLayer</code> or <code>null</code> if
	 * 		there was no previous one.
	 */
	public Layer setLayer(Layer layer) {
		Layer toReturn = getLayer();
		this.layer = layer;
		this.layer.setContainer(this);
		return toReturn;
	}
	
	public void symmetrize() {
		List<LinkContainer> toAdd = new ArrayList<LinkContainer>();
		for (LinkContainer lc : getLinkContainers()) {
			if (getLinkContainer(lc.getEndNodeIndex(), lc.getStartNodeIndex()) == null) {
				toAdd.add(lc);
			}
		}
		for (LinkContainer lc : toAdd) {
			LinkContainer newLc = newLink(lc.getEndNodeIndex(), lc.getStartNodeIndex());
			lc.copyAttributesTo(newLc);				
		}	
	}

	@Override
	void linkAllDefiningAttributes(String elementKeyword) {
		super.linkAllCoreAttributes(elementKeyword);
	}

	@Override
	public void writeAllDefiningAttributes(JavancoXMLElement e) {
		super.writeAllCoreAttribute(e);
	}

	public void setAttributeOnAllLinks(String attName, String value, boolean linkWIthMain) {
		for (LinkContainer lc : this.getLinkContainers()) {
			lc.attribute(attName).setValue(value);
			if (linkWIthMain) {
				lc.linkAttribute(attName, "main_description");
			}
		}
	}

	public void setAttributeOnAllNodes(String attName, String value, boolean linkWIthMain) {
		for (NodeContainer lc : this.getNodeContainers()) {
			lc.attribute(attName).setValue(value);
			if (linkWIthMain) {
				lc.linkAttribute(attName, "main_description");
			}
		}
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

	public boolean contains(AbstractElementContainer c) {
		return dataStructure.contains(c);
	}

	/**
	 * Returns all elements (nodes and links) contained on this layer. These elements
	 * are the containers and NOT the elements itselves.
	 * @return a <code>Collection</code> of <code>AbstractElement</code> objects
	 */
	@SuppressWarnings("unchecked")
	public Collection<AbstractElementContainer> getAllElements() {
		Collection c = dataStructure.getAllLinkContainers();
		c.addAll(dataStructure.getAllNodeContainers());
		return c;
	}
	/**
	 * Returns all node containers contained on this layer. Returns the containers only.
	 * These containers may be empty or may contain <code>AbstractNode</code> instances.
	 * @return a <code>Collection</code> of <code>NodeContainer</code> objects
	 */
	public List<NodeContainer> getNodeContainers() {
		return dataStructure.getAllNodeContainers();
	}

	public List<Integer> getNodeContainerIndexes() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (NodeContainer nc : dataStructure.getAllNodeContainers()) {
			list.add(nc.getIndex());
		}
		return list;
	}
	
	public double[][] getIncidenceMatrixDouble(boolean directed) {
		if (directed)
			return getDirectedIncidenceMatrixDouble();
		else
			return getUndirectedIncidenceMatrixDouble();
	}	
	
	
	public double[][] getDirectedIncidenceMatrixDouble() {
		int size = handler.getHighestNodeIndex()+1;
		double[][] mat = new double[size][size];
		for (int i = 0 ; i < size ; i++) {
			for (int j = 0; j < size ; j++) {
				if (i != j) 
					mat[i][j] = getLinkContainer(i,j) != null ? 1 : -1;
			}
		}
		return mat;	
	}
	
	public double[][] getUndirectedIncidenceMatrixDouble() {
		int size = handler.getHighestNodeIndex()+1;
		double[][] mat = new double[size][size];
		for (int i = 0 ; i < size-1 ; i++) {
			for (int j = i+1; j < size ; j++) {
				mat[i][j] = (getLinkContainer(i,j) != null) || (getLinkContainer(j,i) != null) ? 1 : -1;
				mat[j][i] = mat[i][j];
			}
		}
		return mat;
	}	
	
	public boolean[][] getUndirectedIndidenceMatrix() {
		int size = handler.getHighestNodeIndex()+1;
		boolean[][] mat = new boolean[size][size];
		for (int i = 0 ; i < size-1 ; i++) {
			for (int j = i+1; j < size ; j++) {
				mat[i][j] = (getLinkContainer(i,j) != null) || (getLinkContainer(j,i) != null);
				mat[j][i] = mat[i][j];
			}
		}
		return mat;
	}
	
	public boolean[][] getIncidenceMatrix() {
		return getIncidenceMatrix(false);
	}

	public boolean[][] getIncidenceMatrix(boolean symmetrise) {
		int size = handler.getHighestNodeIndex()+1;
		boolean[][] mat = new boolean[size][size];
		for (int i = 0 ; i < size ; i++) {
			for (int j = 0 ; j < size ; j++) {
				mat[i][j] = (getLinkContainer(i,j) != null);
				if (symmetrise)
					mat[i][j] |= getLinkContainer(j,i)!= null;
			}
		}
		return mat;
	}

	public Matrix<NetworkAttribute> getMatrixLinkAttribute(String attributeName) {
		Matrix<NetworkAttribute> matrix = new Matrix<NetworkAttribute>(getAbstractGraphHandler().getHighestNodeIndex()+1);
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				if (i != j) {
					LinkContainer lc = this.getLinkContainer(i,j);
					if (lc != null) {
						matrix.setMatrixElement(i,j,lc.attribute(attributeName));
					}
				}
			}
		}
		return matrix;
	}

	public double[][] getMatrixLinkAttributeD(String attributeName) {
		return getMatrixLinkAttributeD(attributeName, false);
	}

	public double[][] getMatrixLinkAttributeD(String attributeName, boolean sym) {
		int size = getAbstractGraphHandler().getHighestNodeIndex()+1;
		double[][] matrix = new double[size][size];
		for (int i = 0 ; i < matrix.length ; i++) {
			Arrays.fill(matrix[i], -1);
			for (int j = 0 ; j < matrix.length ; j++) {
				if (i != j) {
					LinkContainer lc = this.getLinkContainer(i,j);
					LinkContainer lc2 = this.getLinkContainer(j,i);
					if (lc != null) {
						try {
							double d = lc.attribute(attributeName).doubleValue();
							matrix[i][j] = d;
						}
						catch (Exception e) {
						}
					} else {
						if (lc2 != null && sym) {
							try {
								double d = lc2.attribute(attributeName).doubleValue();
								matrix[i][j] = d;
							}
							catch (Exception e) {
							}							
						}
					}
				}
			}
		}
		return matrix;
	}
	
	public Double[][] getMatrixLinkAttributeDoubleD(String attributeName) {
		int size = getAbstractGraphHandler().getHighestNodeIndex()+1;
		Double[][] matrix = new Double[size][size];
		for (int i = 0 ; i < matrix.length ; i++) {
			Arrays.fill(matrix[i], -1d);
			for (int j = 0 ; j < matrix.length ; j++) {
				if (i != j) {
					LinkContainer lc = this.getLinkContainer(i,j);
					LinkContainer lc2 = this.getLinkContainer(j,i);
					if (lc != null) {
						try {
							double d = lc.attribute(attributeName).doubleValue();
							matrix[i][j] = d;
						}
						catch (Exception e) {
						}
					} else {
						if (lc2 != null) {
							try {
								double d = lc2.attribute(attributeName).doubleValue();
								matrix[i][j] = d;
							}
							catch (Exception e) {
							}							
						}
					}
				}
			}
		}
		return matrix;
	}
	
	public Matrix<Double> getMatrixLinkAttributeDouble(String attributeName) {
		Matrix<Double> matrix = new Matrix<Double>(getAbstractGraphHandler().getHighestNodeIndex()+1);
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				if (i != j) {
					LinkContainer lc = this.getLinkContainer(i,j);
					if (lc != null) {
						try {
							double d = lc.attribute(attributeName).doubleValue();
							matrix.setMatrixElement(i,j,(Double)d);
						}
						catch (Exception e) {
						}
					}
				}
			}
		}
		return matrix;
	}	
	

	public double[][] getMatrixOfLengths(boolean directed) {
		int size = getAbstractGraphHandler().getHighestNodeIndex()+1;
		double[][] matrix = new double[size][size];
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix.length ; j++) {
				if (i != j) {
					LinkContainer lc = this.getLinkContainer(i,j);
					if (lc != null) {
						double d = lc.getGeodesicLinkLength();
						matrix[i][j] = d;
						if (directed == false) {
							matrix[j][i] = d;
						}
					} else {
						matrix[i][j] = -1;
					}
				}
			}
		}
		return matrix;
	}
	
	public void setMatrixOfAttributeValue(Matrix<Object> matrix, String attributeName, boolean create) {
		for (int i = 0 ; i < matrix.size() ; i++) {
			for (int j = 0 ; j < matrix.size() ; j++) {
				if (i != j) {
					LinkContainer lc = this.getLinkContainer(i,j);
					if (lc != null || create) {
						lc = newLink(i,j);
					}
					lc.attribute(attributeName).setValue(matrix.getMatrixElement(i,j).toString());
				}
			}
		}		
	}
	
	public void setMatrixOfAttributeValue(int[][] matrix, String attributeName, boolean create) {
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = 0 ; j < matrix.length ; j++) {
				if (i != j) {
					LinkContainer lc = this.getLinkContainer(i,j);
					if (lc != null || create) {
						lc = newLink(i,j);
					}
					lc.attribute(attributeName).setValue(matrix[i][j]);
				}
			}
		}		
	}

	/**
	 * Returns all link containers contained on this layer. Returns the containers only.
	 * These containers may be empty or may contain <code>AbstractLink</code> instances.
	 * @return a <code>Collection</code> of <code>LinkContainer</code> objects
	 */
	public List<LinkContainer> getLinkContainers() {
		return dataStructure.getAllLinkContainers();
	}

	public List<LinkContainer> getLinksOfPath(Path p) {
		List<LinkContainer> list = new ArrayList<LinkContainer>(p.getNumberOfHops());
		for (NodePair np : p.getCanonicalPathSegments()) {
			LinkContainer lc = this.getLinkContainer(np);
			if (lc == null) {
				np.reverse();
				lc = this.getLinkContainer(np);
			}
			if (lc != null) {
				list.add(lc);
			}
		}
		return list;
	}

	public List<NodePair> getLinksAsNodePairs() {
		List<NodePair> nodePairs = new ArrayList<NodePair>();
		for (LinkContainer lc : getLinkContainers()) {
			nodePairs.add(lc.toNodePair());
		}
		return nodePairs;
	}

	/**
	 * Returns all nodes containers contained on this layer that . Returns the containers only.
	 * These containers may be empty or may contain <code>AbstractNode</code> instances.
	 * @return a <code>Collection</code> of <code>NodeContainer</code> objects
	 */
	public List<LinkContainer> getOutgoingLinks(int start) {
		return dataStructure.getOutgoingLinks(start);
	}
	public List<LinkContainer> getIncomingLinks(int end)  {
		return dataStructure.getIncomingLinks(end);
	}
	public List<LinkContainer> getConnectedLinks(int index) {
		List<LinkContainer> list = getOutgoingLinks(index);
		list.addAll(getIncomingLinks(index));
		return list;
	}
	public Collection<LinkContainer>	getLinkContainers(int start, int end) {
		return dataStructure.getLinkContainers(start, end);
	}

	public LinkContainer newLink(int orig, int dest) {
		LinkContainer lc = handler.newLink(orig, dest, this);
		return lc;
	}
	
	public LinkContainer newLink(LinkContainer lc) {
		return handler.newLink(lc);
	}

	public void setLinksLengths(String attributeName) {
		for(LinkContainer lc : getLinkContainers()){
			int orig_x = lc.getStartNodeContainer().attribute(XMLTagKeywords.POS_X).intValue();
			int orig_y = lc.getStartNodeContainer().attribute(XMLTagKeywords.POS_Y).intValue();
			int dest_x = lc.getEndNodeContainer().attribute(XMLTagKeywords.POS_X).intValue();
			int dest_y = lc.getEndNodeContainer().attribute(XMLTagKeywords.POS_Y).intValue();
			double length = java.awt.geom.Point2D.distance(orig_x, orig_y, dest_x, dest_y);
			length = Math.round(length);

			lc.attribute(attributeName).setValue(length + "");
		}
	}

	public NodeContainer removeNode(NodeContainer node) {
		handler.removeElement(node);
		return node;
	}
	
	public LinkContainer removeLink(int start, int end) {
		LinkContainer link = this.getLinkContainer(start, end);
		if (link != null) {
			handler.removeElement(link);
			return link;
		} else {
			throw new IllegalStateException("Cannot suppress link " + link + " over layer " + this + ". The link is not located over this layer");
		}		
	}	
	
	public LinkContainer removeLink(NodePair np) {
		return removeLink(np.getStartNode(), np.getEndNode());
	}

	public LinkContainer removeLink(LinkContainer link) {
		if (this.getLinkContainer(link.getStartNodeIndex(), link.getEndNodeIndex()) != null) {
			handler.removeElement(link);
			return link;
		} else {
			throw new IllegalStateException("Cannot suppress link " + link + " over layer " + this + ". The link is not located over this layer");
		}
	}

	public void removeIncomingLinks(int index) {
		for (LinkContainer lc : getIncomingLinks(index)) {
			removeLink(lc);
		}
	}

	public void removeOutgoingLinks(int index) {
		for (LinkContainer lc : getOutgoingLinks(index)) {
			removeLink(lc);
		}
	}

	public void removeAllLinks() {
		int counter = 0;
		for (LinkContainer c : this.getLinkContainers()) {
			handler.removeElement(c);
			counter++;
			if (counter % 10000 == 0) {
				logger.debug("Removed 10000 links");
			}
		}
	}

	/*public NodeContainer nodeAt(int idx) {
		return dataStructure.nodeAt(idx);
	}*/

	public Class<? extends AbstractLayerRepresentation> getUsedRepresentation() {
		return dataStructure.getClass();
	}

	@Override
	public String toShortString() {
		return "LayerContainer_name" + this.getName();
	}
	public NetworkAttribute getDefaultLinkColor() {
		return defaultLinkColor;
	}
	public NetworkAttribute getDefaultLinkWidth() {
		return defaultLinkWidth;
	}
	public NetworkAttribute getDefaultLabelColor() {
		return defaultLabelColor;
	}
	public NetworkAttribute getDefaultNodeColor() {
		return defaultNodeColor;
	}
	public NetworkAttribute getDefaultNodeIcon() {
		return defaultNodeIcon;
	}
}