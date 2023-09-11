package ch.epfl.javanco.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Attribute;
import org.dom4j.Element;

import ch.epfl.JavancoGUI;
import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.graphics.ImageWriter;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.event.ElementListener;
import ch.epfl.javanco.event.NetworkEditionEventHandler;
import ch.epfl.javanco.exports.S_D_W_X_Y_Exporter;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.AbstractLayerRepresentation;
import ch.epfl.javanco.network.DefaultGraphImpl;
import ch.epfl.javanco.network.DefaultLinkImpl;
import ch.epfl.javanco.network.Layer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.Link;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.Node;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.network.TopologyHandler;
import ch.epfl.javanco.pluggings.SVGPainter;
import ch.epfl.javanco.remote.MNDService;
import ch.epfl.javanco.remote.MNDServiceException;
import ch.epfl.javanco.scripting.GroovyScriptManager;
import ch.epfl.javanco.ui.AbstractGraphicalUI;
import ch.epfl.javanco.ui.UIDelegate;
import ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI;
import ch.epfl.javanco.utils.NodeIndexManager;
import ch.epfl.javanco.xml.GraphicalDataHandler;
import ch.epfl.javanco.xml.JavancoXMLElement;
import ch.epfl.javanco.xml.MainDataHandler;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLDataHandler;
import ch.epfl.javanco.xml.XMLSerialisationManager;
import ch.epfl.javanco.xml.XMLTagKeywords;


/**
 * Central class for network and topologies management. Centralize all network dependent GUI objets like
 * the menu bar (which contains the layer names and thus depends on the network) or the graph displayer.
 * Provides also an access to the differents <code>XMLDataHandler</code> objects that contains topological,
 * painting or other information.<br>
 *
 * The main purpose of the <code>getHandledGraphName</code> method is to permit the GUI having the
 * name of the graph for convience reasons (naming of the frame, e.g). Even if its recommended to
 * implement a mechanism returning the real name of the graph as it is contained in the XML spring,
 * an implemention returning an empty string or a fixed string should not make problems.
 */

public abstract class AbstractGraphHandler extends NetworkEditionEventHandler {

	public final static String DEFAULT_GRAPH_NAME = "Untitled network";

	private static Logger logger = new ch.epfl.general_libraries.logging.Logger(AbstractGraphHandler.class);

	/**
	 * This constructor just takes note the factory reference and the <code>
	 * isEditable</code> value. Nothing else is done. Graph is really created
	 * when method <code>newNetwork</code> is called
	 */
	protected AbstractGraphHandler(GraphHandlerFactory factory, boolean isEditable) {
		this.factory = factory;
		this.isEditable = isEditable;
		xmlElement = new JavancoXMLElement(getXMLElementName().toString());

		topologyHandler = new TopologyHandler(this);
	}

	private String networkName = DEFAULT_GRAPH_NAME;


	private GraphHandlerFactory               factory               = null;
	private TopologyHandler                   topologyHandler       = null;
	private GroovyScriptManager				  groovyScriptManager   = null;
	private NodeIndexManager                  nodeIndexManager      = null;
	private LayerContainer                    currentlyEdited       = null;
	private boolean                           isCreated             = false;
	private boolean                           isEditable            = false;
	private JavancoXMLElement                 xmlElement            = null;
	private SimpleMap<String, XMLDataHandler> xmlHandlerList        = null;
	private UIDelegate                        uiDelegate            = null;

	public UIDelegate getUIDelegate() {
		if (uiDelegate == null) {
			uiDelegate = new UIDelegate(this);
		}
		return uiDelegate;
	}

	/**
	 * Return the name of the handled graph
	 * @return the name of the handled graph
	 */
	public String getHandledGraphName() {
		return networkName;
	}
	
	public GraphHandlerFactory getGraphHandlerFactory() {
		return factory;
	}

	public void setHandledGraphName(String name) {
		networkName = name;
		factory.fireGraphNameChangedEvent(this);
	}

	/**
	 * This method will detach this graph from any GUI
	 */
	public void deregisterGraph(){
		factory.recycleGraphHandler(this);
	}
	/**
	 * Return <code>true</code> if method newNetwork has allready been called
	 */
	public boolean isCreated() {
		return isCreated;
	}

	public boolean isEditable() {
		return isEditable;
	}

	private TopologyHandler getTopologyHandler() {
		return topologyHandler;
	}

	/**
	 * Offer a central interface for Network instanciation. Must be called once
	 * before trying to call any other method. In the attribute's list,
	 * one expects the standard layer parameter like <code>DEFAULT_NODE_CLASS</code>,
	 * <code>DEFAULT_LINK_COLOR</code>, etc. If the <code>mainLayerClass</code>
	 * parameter is not <code>null</code>, the a new layer will be also instanciated
	 * using this class.
	 * @param networkName The name of the network
	 * @param mainLayerName The name of the layer furnished with (if <code>mainLayerClass</code>
	 *                      is not <code>null</code>
	 * @param mainLayerClass The <code>class</code> to use for instanciation of the
	 *                       main layer (the layer furnished with the new network) or
	 *                       <code>null</code> for a network without a new layer.
	 * @param newLayerAttributes A <code>List</code> of attributes for the new layer.
	 * @throws InstantiationException When instanciation of a new object using the
	 *                               given Class generates any Exception
	 * @see XMLTagKeywords
	 * @see ch.epfl.javanco.gui.NewLayerDialog
	 * @return A <code>LayerContainer</code> object containing the main layer of
	 *         the new network, if such one has been created, otherwise returns
	 *         <code>null</code>
	 */
	public synchronized LayerContainer newNetwork(String networkName,
			String mainLayerName,
			Class<? extends Layer> mainLayerClass,
			List<Attribute> newLayerAttributes)
	throws InstantiationException {
		if (isCreated) {
			throw new IllegalStateException("Network cannot be created twice."
					+ "\r\nIf this statement has been called for a script, launch this"
					+ "script without any network windows opened");
		}
		isCreated = true;
		//	    this.factory.fireNetworkCreatedEvent(this);
		this.networkName = networkName;
		if (mainLayerClass != null) {
			return newLayer(mainLayerName, mainLayerClass, newLayerAttributes, new CasualEvent("New network event"));
		}
		return null;
	}

	public LayerContainer newNetwork(String networkName) {
		return newNetwork(networkName, "default layer", DefaultGraphImpl.class);
	}

	public LayerContainer newNetwork(String networkName, String layerName, Class<? extends Layer> layerClass) {
		try {
			return newNetwork(networkName, layerName, layerClass, null);
		}
		catch (InstantiationException e) {
			logger.error("Excetion when creating new network", e);
		}
		return null;
	}

	public synchronized void openNetwork(java.io.Reader reader) {
		try {
			JavancoXMLElement el = XMLSerialisationManager.openNetwork(reader, this);
			setXML(el);
		} catch (org.xml.sax.SAXException e) {}
	}

	public synchronized boolean openNetworkFile(File file){
		try {
			JavancoXMLElement el = XMLSerialisationManager.openNetwork(file, this);
			setXML(el);
			if (this.networkName.startsWith(DEFAULT_GRAPH_NAME)) {
				this.setHandledGraphName(file.getName());
			}
			logger.info("Loaded XML MND file -->  " + file.toURI());
		}
		catch (org.xml.sax.SAXException e) { 
			logger.warn("SAX exception while parsing file " + file.toURI());
		}
		catch (FileNotFoundException e) {
			logger.warn("File not found : " + file.getPath());
			this.displayWarning("File not found : " + file.getPath(), e);			
		}
		/*catch (java.io.IOException e) {
			logger.warn("Loading error ", e);
			this.displayWarning("Loading error : " + file.getPath(), e);
			return false;
		}*/
		catch (AbstractLayerRepresentation.DuplicateLinkException e) {
			logger.warn("Loading error ", e);
			this.displayWarning("Loading error : file contains twice the link "+ e.i + "-" + e.j);
			return false;
		}
		return true;
	}

	/**
	 * Tries to create and affect an XML structure to the current handler using the StringBuilder provided
	 * #author Christophe Trefois
	 * @param fileStringRespresentation The StringBuilder representing the XML file uploaded via RMI
	 */
	public void openNetworkFromString(String fileStringRespresentation) {
		this.openNetwork(new StringReader(fileStringRespresentation));
	}


	public boolean openNetworkFile(String graphFileName) {
		return openNetworkFile(JavancoFile.findFile(graphFileName, JavancoFile.getDefaultMNDfilesDir()));
	}

	public boolean  openNetworkFile(String graphFileName, String dir) {
		return openNetworkFile(JavancoFile.findFile(graphFileName, dir));
	}

	public void displayWarning(String s) {
		fireDisplayWarningEvent(s);
	}
	public void displayWarning(String s, Throwable t) {
		fireDisplayWarningEvent(s,t);
	}

	/**
	 * Offer a central command for layer creation and addition. The given Class
	 * object will be used to dynamically instanciate a new object.In the
	 * attribute's list, one expects the standard layer parameter like <code>
	 * DEFAULT_NODE_CLASS</code>,<code>DEFAULT_LINK_COLOR</code>, etc. This
	 * method will fire an <code>ElementCreationEvent</code>, and thus all
	 * listeners will be notified of the creation.
	 * 
	 * @param layerName the name of the new layer
	 * @param layerClass the class to use for the construction of new layer
	 * @param layerAttributes a list of <code>Attribute</code> for the new layer
	 *
	 * @see XMLTagKeywords
	 *
	 * @throws InstantiationException If any problem occured when trying to
	 *                               instanciate the new layer using the given
	 *                               class
	 * @throws IllegalArgumentException If a layer with the same name allready
	 *                                 exists in that network.
	 * @return The <code>LayerContainer</code> object containing the new layer
	 */
	public synchronized LayerContainer newLayer(String layerName, Class<? extends Layer> layerClass, List<Attribute> layerAttributes, EventObject e) throws InstantiationException {
		if (layerName == null) {
			throw new NullPointerException("Layer name cannot be null");
		}
		LayerContainer layerContainer = new LayerContainer(layerClass, this);
		layerContainer.attribute(XMLTagKeywords.ID).setValue(layerName, e);
		if (layerAttributes != null) {
			for (Attribute att : layerAttributes) {
				layerContainer.attribute(XMLTagKeywords.parse(att.getName())).setValue(att.getValue());
			}
		}
		return newLayer(layerContainer);
	}

	public LayerContainer newLayer(String layerName, Class<? extends Layer> layerClass) {
		try {
			return this.newLayer(layerName, layerClass, null, new CasualEvent("New layer event"));
		}
		catch (InstantiationException e) {
			throw new IllegalStateException("Bug : creating a defaultImpl should not create an exception",e);
		}
	}
	/**
	 * Adds a new layer to the current graph, using the default implementation class DefaultGraphImpl
	 */
	public LayerContainer newLayer(String layerName) {
		return newLayer(layerName, DefaultGraphImpl.class);
	}

	public synchronized LayerContainer newLayer(LayerContainer layerContainer) {
		String layerName = layerContainer.attribute(XMLTagKeywords.ID).getValue();
		if (topologyHandler.getLayerContainer(layerName) != null) {
			throw new IllegalArgumentException("Layer with the given name already exists");
		}
		ElementEvent ev = ElementEvent.createCreationEvent(layerContainer);
		topologyHandler.layerCreated(layerContainer);
		fireElementEvent(ev);
		layerContainer.setFullyCreated();
		layerContainer.setDisplayed(true);
		return layerContainer;
	}
	
	public void clear() {
		clearLayers(true);
	}
 
	public synchronized void clearLayers(boolean createLayer) {
		if (currentlyEdited == null) return;
		String layerName = currentlyEdited.getName();
		boolean eventEnabled = isEventEnabled();
		if (eventEnabled) {
			this.setModificationEventEnabledWithoutCallingBigChanges(false);
		}
		while (this.getLayerContainers().size() > 0) {
			removeElement(getLayerContainers().iterator().next());
		}

		currentlyEdited = null;
		if (createLayer) {
			newLayer(layerName);
		}
		if (nodeIndexManager != null) {
			this.nodeIndexManager.reset();
		}
		if (eventEnabled) {
			this.setModificationEventEnabledWithoutCallingBigChanges(true);
		}
		this.fireAllElementsModificationEvent(ElementEvent.getAllElementEvent());
	}

	/**
	 * Offer a central interface for node creation and addition. Using the
	 * class object given in parameter, a new node instance will be dynamically
	 * constructed and put into a new NodeContainer. This one will receive
	 * several attributes, for instance the x and y coord (keywords are :
	 * <code>XMLTagKeywords.POS_X</code> and <code>XMLTagKeywords.POS_X</code>)
	 * This method will generate an <code>ElementCreationEvent</code> and thus
	 * all listeners will be notified of the creation of this node. The <code>
	 * TopologyHandler</code>, by default registered as <code>
	 * ElementCreationListener</code> will for instance add the created node to
	 * it internal graph structure, and make possible the later retrieval of
	 * topological information from this node.
	 *
	 * @param x The x coordinate of the new node
	 * @param y The y coordinate of the new node
	 * @param color The color of the new node. To use layer's default node color,
	 *              give <code>default</code> as parameter
	 * @param nodeClass The Class to use for the construction of the new node
	 * @param icon The icon of the new node
	 * @return The <code>NodeContainer</code> object containing the created
	 *         node
	 * @throws InstantiationException If any problem occured while creating and
	 *                               instanciating the new node
	 * @see XMLTagKeywords
	 * @see ElementCreationEvent
	 * @see TopologyHandler
	 * @see ElementCreationListener
	 */
	public synchronized NodeContainer newNode(int x, int y, Class<? extends Node> nodeClass, EventObject e) {

		try {
			NodeContainer nodeC = new NodeContainer(nodeClass);
			nodeC.attribute(XMLTagKeywords.POS_X).setValue(x +"", e);
			nodeC.attribute(XMLTagKeywords.POS_Y).setValue(y+"", e);
			
			nodeC.attribute(XMLTagKeywords.ID).setValue(nodeC.getIndex(), e);
			return nodeC;
		}
		catch (InstantiationException ex) {
			displayWarning("Impossible to create node", ex);
		}
		return null;
	}
	
	public NodeContainer newNode(int x, int y, String color, String size, String icon, Class<? extends Node> nodeClass, EventObject e) {
		boolean ev = isEventEnabled();
		if (ev) {
			super.setModificationEventEnabledWithoutCallingBigChanges(false);
		}	
		NodeContainer nc = newNode(x,y,nodeClass, e);
		nc.attribute(XMLTagKeywords.NODE_COLOR).setValue(color, e);
		nc.attribute(XMLTagKeywords.NODE_SIZE).setValue(size, e);
		nc.attribute(XMLTagKeywords.NODE_ICON).setValue(icon, e);		
		if (ev) {
			super.setModificationEventEnabledWithoutCallingBigChanges(true);
		}
		addNode(nc, getEditedLayer(), e);		
		return nc;
	}
	
	public NodeContainer newNode(int x, int y, String color, String size, String icon, EventObject e) {
		return newNode(x,y,color,size,icon, getUIDelegate().getTypeCreationAdapter().getSelectedNodeType(), e);	
	}

	/**
	 * Same as <code>newNode(int x, int y, String color, Class nodeClass)</code>
	 * but with "default" as color and using the default node implementation
	 */
	public NodeContainer newNode(int x, int y) {
		NodeContainer nc = newNode(x,y,getUIDelegate().getTypeCreationAdapter().getSelectedNodeType(), new CasualEvent("New node event"));		
		addNode(nc, getEditedLayer(), new CasualEvent("New node event"));
		return nc;
	}

	public NodeContainer newNode(NodeContainer nodeC) {
		return addNode(nodeC, currentlyEdited, new CasualEvent("New node event"));
	}

	public synchronized NodeContainer addNode(NodeContainer nodeC, LayerContainer lc, EventObject e) {
		if (nodeC.attribute("id", false) != null && nodeC.attribute("id").intValue() != -1) {
			int index = Integer.parseInt(nodeC.attribute(XMLTagKeywords.ID).getValue());
			if (getNodeIndexManager().isUsed(index)) {
				throw new IllegalStateException("Creation of a node with an already used index");
			}
			getNodeIndexManager().setAsUsed(index);
			nodeC.setIndex(index);
		} else if (nodeC.getIndex() < 0) {
			//	if (nodeC.attribute(XMLTagKeywords.ID, false) == null) {
			int index = getNodeIndexManager().getUnused();
			nodeC.setIndex(index);
		} else {
			int index = nodeC.getIndex();//Integer.parseInt(nodeC.attribute(XMLTagKeywords.ID).getValue());
			if (getNodeIndexManager().isUsed(index)) {
				throw new IllegalStateException("Creation of a node with an already used index " + index);
			}
			getNodeIndexManager().setAsUsed(index);
			//	nodeC.setIndex(index);
		}
		ElementEvent ev = ElementEvent.createCreationEvent(nodeC);
		ev.setParent(e);
		topologyHandler.nodeCreated(nodeC, lc);
		fireElementEvent(ev);
		nodeC.setFullyCreated();
		return nodeC;
	}

	/**
	 * Simply add a node to the current graph structure.
	 */
	public NodeContainer newNode() {
		return newNode(new NodeContainer());
	}
	
	/**
	 * Creates a link between each pair of nodes in the given collection, connecting the smallest index to the largest one
	 * @param c
	 */
	public void interconnect(Collection<NodeContainer> c) {
		for (NodeContainer n1 : c) {
			int n = n1.getIndex();
			for (NodeContainer n2 : c) {
				int nc = n2.getIndex();
				if (n != nc) {
					if (n < nc) {
						if (currentlyEdited.getLinkContainer(n, nc) == null) {
							newLink(n, nc);
						}
					} else {
						if (currentlyEdited.getLinkContainer(nc, n) == null) {
							newLink(nc, n);
						}						
					}
				}
			}
		}
	}

	/**
	 * Offer a central interface for link creation and addition. Links are
	 * created under the same model than the nodes, therefore refer to the
	 * <code>newNode(...)</code> method of this class for more information
	 * @param orig The id of the origin node.
	 * @param dest The id of the destination node
	 * @param linkClass The class to use for creation of the new link
	 * @throws InstantiationException If any problem occured when trying to
	 *                               instanciate the new link object using
	 *                               the constructor of the given class.
	 */
	private synchronized LinkContainer newLinkContainer(int orig, int dest, Class<? extends Link> linkClass) {
		if (!nodeIndexManager.isUsed(orig)) {
			String text = "Impossible to create link " + orig + "-" + dest + " , node " + orig + " does not exist";
			throw new IllegalStateException(text);
		}
		if (!nodeIndexManager.isUsed(dest)) {
			String text = "Impossible to create link " + dest + "-" + dest + " , node " + dest + " does not exist";
			throw new IllegalStateException(text);
		}		
		try {
			//	Class<? extends Link> c = getSelectedLinkClass();
			LinkContainer linkC;
			/*	if (selectedAdapter != null){
				c = selectedAdapter.getSelectedLinkType();*/
			if (!linkClass.equals(DefaultLinkImpl.class)) {
				linkC = new LinkContainer(linkClass, orig, dest);
				return linkC;
			//	return addLink(linkC, lc, e);
			} else {
				linkC = new LinkContainer(orig, dest, new DefaultLinkImpl());
				return linkC;
			//	return addLink(linkC, lc, e);
			}
		}
		catch (InstantiationException ex) {
			displayWarning("Impossible to create link", ex);
		}

		return null;
	}
	
	public LinkContainer newLink(int orig, int dest, Class<? extends Link> linkClass, EventObject e) {
		LinkContainer lc = newLinkContainer(orig, dest, linkClass);
		return addLink(lc, currentlyEdited, e);
	}

	public LinkContainer newLink(NodeContainer nc1, NodeContainer nc2) {
		return newLink(nc1.getIndex(), nc2.getIndex());
	}

	public LinkContainer newLink(int orig, int dest) {
		return newLink(orig, dest, getUIDelegate().getTypeCreationAdapter().getSelectedLinkType(), new CasualEvent("New link event"));
	}

	public LinkContainer newLink(int orig, int dest, LayerContainer target) {
		LinkContainer lc = newLinkContainer(orig, dest, getUIDelegate().getTypeCreationAdapter().getSelectedLinkType());
		return addLink(lc, target, new CasualEvent("New link event"));
	}

	public LinkContainer newLink(LinkContainer linkC) {
		return addLink(linkC, currentlyEdited, new CasualEvent("New link event"));
	}
	
	public LinkContainer newLink(int orig, int dest, String color, String color2, String width, EventObject e) {
		LinkContainer linkC = newLinkContainer(orig, dest, getUIDelegate().getTypeCreationAdapter().getSelectedLinkType());
		linkC.attribute(XMLTagKeywords.LINK_COLOR).setValue(color, e);
		linkC.attribute(XMLTagKeywords.LINK_COLOR2).setValue(color2, e);
		linkC.attribute(XMLTagKeywords.LINK_WIDTH).setValue(width, e);
		linkC.attribute(XMLTagKeywords.LINK_DASH).setValue("", e);
		linkC.attribute(XMLTagKeywords.LINK_SPEED).setValue(0, e);
		linkC.attribute(XMLTagKeywords.LINK_CURVE_START).setValue("default", e);
		linkC.attribute(XMLTagKeywords.LINK_CURVE_END).setValue("default", e);
		linkC.attribute(XMLTagKeywords.LINK_CURVE_START_ANGLE).setValue("default", e);
		linkC.attribute(XMLTagKeywords.LINK_CURVE_END_ANGLE).setValue("default", e);
		addLink(linkC, currentlyEdited, e);
		return linkC;	
	}	

	public synchronized LinkContainer addLink(LinkContainer linkC, LayerContainer layC, EventObject e) {
		int orig;
		int dest;
		if (!(linkC.getStartNodeIndex() > Integer.MIN_VALUE && linkC.getEndNodeIndex() > Integer.MIN_VALUE)) {
			orig = Integer.parseInt(linkC.attribute(XMLTagKeywords.ORIG).getValue());
			dest = Integer.parseInt(linkC.attribute(XMLTagKeywords.DEST).getValue());
			linkC.setStartNodeIndex(orig);
			linkC.setEndNodeIndex(dest);
			if ((orig < 0 ) || (dest < 0)) {
				throw new IllegalStateException("Creating new link with undefined extremities");
			}
			if (!(getNodeIndexManager().isUsed(orig))) {
				throw new IllegalStateException("Link created specifying an unkown node as start node (index="+orig+").");
			}
			if (!(getNodeIndexManager().isUsed(dest))) {
				throw new IllegalStateException("Link created specifying an unkown node as end node (index="+dest+").");
			}
		}
		ElementEvent ev = ElementEvent.createCreationEvent(linkC);
		ev.setParent(e);
		topologyHandler.linkCreated(linkC, layC);
		linkC.setFullyCreated();		
		fireElementEvent(ev);
		return linkC;
	}

	public Collection<AbstractElementContainer> getAllContainers() {
		Collection<AbstractElementContainer> col = new ArrayList<AbstractElementContainer>();
		col.addAll(this.getLayerContainers());
		col.addAll(this.getLinkContainers());
		col.addAll(this.getNodeContainers());
		return col;
	}

	public LayerContainer getLayerContainer(String name) {
		return topologyHandler.getLayerContainer(name);
	}

	public Collection<LayerContainer> getLayerContainers() {
		return topologyHandler.getLayerContainers();
	}

	public NodeContainer getNodeContainer(int i) {
		return topologyHandler.getNodeContainer(i);
	}

	/**
	 * Return a new instancied Collection containing all references of node
	 * present in the graph
	 */
	public Collection<NodeContainer> getNodeContainers() {
		Collection<NodeContainer> col = new ArrayList<NodeContainer>();
		for (LayerContainer c : getLayerContainers()) {
			col.addAll(c.getNodeContainers());
		}
		return col;
	}
	
	/**
	 * Returns all the node containers that have an attribute with the specified value
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 */
	public Collection<NodeContainer> getNodeContainers(String attributeName, String attributeValue) {
		Collection<NodeContainer> col = new ArrayList<NodeContainer>();
		for (LayerContainer c : getLayerContainers()) {
			for (NodeContainer nc : c.getNodeContainers()) {
				NetworkAttribute att = nc.attribute(attributeName, false);
				if (att != null && att.getValue().equals(attributeValue)) {
					col.add(nc);
				}
			}
		}
		return col;		
	}

	public int getNumberOfNodes() {
		return getNodeIndexManager().getNumberOfNodes();
	}
	
	public int[] getExtremities()  {	
		int lowH = Integer.MAX_VALUE;
		int lowV = Integer.MAX_VALUE;
		int hihH = Integer.MIN_VALUE;
		int hihV = Integer.MIN_VALUE;
		for (NodeContainer nc : getNodeContainers()) {
			int x = (int)nc.getCoordinate().getX();
			int y = (int)nc.getCoordinate().getY();
			if (x < lowH) {
				lowH = x;
			}
			if (x > hihH) {
				hihH = x;
			}
			if (y < lowV) {
				lowV = y;
			}
			if (y > hihV) {
				hihV = y;
			}
		}
		return new int[]{lowH, hihH, lowV, hihV};		
	}
	
	public int[] getExtremitiesWithMargin()  {
		int[] exts = getExtremities();
		if (exts[0] == -1 && exts[1] == -1) {
			exts[0] = 0;
			exts[1] = 500;
		} else {
			exts[0] = (int)Math.min(exts[0] -50, exts[0]*0.95);
			exts[1] = (int)Math.max(exts[1] + 50, exts[1]*1.05);	
		}
		if (exts[2] == -1 && exts[3] == -1) {
			exts[2] = 0;
			exts[3] = 500;
		} else {
			exts[2] = (int)Math.min(exts[2] -50, exts[2] * 0.95);
			exts[3] = (int)Math.max(exts[3] +50, exts[3]*1.05);
		}
		return exts;
	}

	public List<Integer> getNodeIndexes() {
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (NodeContainer c : getNodeContainers()) {
			l.add(c.getIndex());
		}
		l.trimToSize();
		Collections.sort(l);
		return l;
	}
	
	public Set<Integer> getNodeIndexesSet() {
		TreeSet<Integer> l = new TreeSet<Integer>();
		for (NodeContainer c : getNodeContainers()) {
			l.add(c.getIndex());
		}
		return l;
	}	

	public LinkContainer getLinkContainer(int orig, int dest, String layerName) {
		return topologyHandler.getLinkContainer(orig,dest,layerName);
	}

	public LinkContainer getLinkContainer(int orig, int dest, boolean directed) {
		LinkContainer lc = getLinkContainer(orig, dest);
		if (directed) {
			return lc;
		}
		if (lc == null) {
			return getLinkContainer(dest, orig);
		}
		return lc;
	}
	
	public LinkContainer getLinkContainer(NodePair np) {
		return getLinkContainer(np.getStartNode(), np.getEndNode());
	}
	
	public LinkContainer getLinkContainer(NodeContainer nc1,
			NodeContainer nc2) {
		return getLinkContainer(nc1.getIndex(), nc2.getIndex());
	}	

	public LinkContainer getLinkContainer(int orirg, int dest) {
		for (LayerContainer layC : getLayerContainers()) {
			LinkContainer lc = layC.getLinkContainer(orirg, dest);
			if (lc != null) {
				return lc;
			}
		}
		return null;
	}

	public Collection<LinkContainer> getLinkContainers(int ext1) {
		Collection<LinkContainer> collector = new ArrayList<LinkContainer>();
		for (LayerContainer llayer : getTopologyHandler().getLayerContainers()) {
			for (LinkContainer llink : llayer.getLinkContainers()) {
				if ((llink.getStartNodeIndex() == ext1) || (llink.getEndNodeIndex() == ext1)) {
					collector.add(llink);
				}
			}
		}
		return collector;
	}

	public Collection<LinkContainer> getLinkContainers(int ext1, int ext2, boolean dir) {
		return topologyHandler.getLinkContainers(ext1, ext2, dir);
	}

	public ArrayList<LinkContainer> getLinkContainers() {
		ArrayList<LinkContainer> collector = new ArrayList<LinkContainer>();
		for (LayerContainer llayer : getTopologyHandler().getLayerContainers()) {
			for (LinkContainer llink : llayer.getLinkContainers()) {
				collector.add(llink);
			}
		}
		return collector;
	}
	
	/**
	 * Returns a list of linkcontainers corresponding to the given path. 
	 * Linkcontainers are taken in the active layer in priority
	 * @param p
	 * @return
	 */
	public List<LinkContainer> getLinkContainers(Path p) {
		if (p.size() < 2) return new ArrayList<LinkContainer>();
		ArrayList<LinkContainer> toRet = new ArrayList<LinkContainer>(p.size()-1);
		for (NodePair np : p.getPathSegments()) {		
			if (currentlyEdited.getLinkContainer(np) != null) {
				toRet.add(currentlyEdited.getLinkContainer(np));
			} else if (currentlyEdited.getLinkContainer(np.reverseNew()) != null) {
				toRet.add(currentlyEdited.getLinkContainer(np.reverseNew()));
			} else if (getAnyLinkContainer(np) !=  null) {
				toRet.add(getAnyLinkContainer(np));
			} else if (getAnyLinkContainer(np.reverseNew()) != null) {
				toRet.add(getAnyLinkContainer(np.reverseNew()));
			}
		}
		return toRet;
	}

	public LinkContainer getAnyLinkContainer(int ext1, int ext2) {
		Collection<LinkContainer> col = getLinkContainers(ext1, ext2, false);
		if (col.size() > 0) {
			return col.iterator().next();
		}
		return null;
	}
	
	public LinkContainer getAnyLinkContainer(NodePair np) {
		return getAnyLinkContainer(np.getStartNode(), np.getEndNode());
	}
	
	public Path getShortestPath(int i, int j) {
		return BFS.getShortestPath(this, i, j);
	}

	/**
	 * Defines the layer with the specified name as the currently edited layer.
	 * @throws NullPointerException if there is no layer with the given name
	 */
	public synchronized void setEditedLayer(String layerName) {
		LayerContainer edited = getTopologyHandler().getLayerContainer(layerName);
		if (edited == null) {
			throw new NullPointerException("No layer with the following name exists");
		}
		setEditedLayer(edited);
	}

	public void setEditedLayer(LayerContainer layC) {
		currentlyEdited = layC;
		fireLayerEditedChangedEvent(layC);
	}

	public LayerContainer getEditedLayer() {
		return	currentlyEdited;
	}

	public void hideAllLayers() {
		for (LayerContainer lc : getLayerContainers()) {
			lc.setDisplayed(false);
		}
	}

	public boolean hasGapsInNodesIndexes() {
		if (nodeIndexManager == null) {
			return true;
		}
		return !this.nodeIndexManager.hasGaps();
	}
	
	public double getDistance(int i , int j) {
		NodeContainer n1 = getNodeContainer(i);
		NodeContainer n2 = getNodeContainer(j);
		int x1 = n1.getX();
		int x2 = n2.getX();
		int y1 = n1.getY();
		int y2 = n2.getY();
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	
	public List<AbstractElementContainer> getElementContainers() {
		List<LinkContainer> links = getLinkContainers();
		Collection<NodeContainer> nodes = getNodeContainers();
		Collection<LayerContainer> layer = getLayerContainers();
		
		ArrayList<AbstractElementContainer> list
			 = new ArrayList<AbstractElementContainer>(links.size() + nodes.size() + layer.size());
		list.addAll(links);
		list.addAll(nodes);
		list.addAll(layer);
		return list;
	}
	
	public void linkAllAttributes() {
		for (AbstractElementContainer aec : getElementContainers()) {
			aec.linkAllAttributes();
		}
	}
	
	
	public void removeElement(AbstractElementContainer cont) {
		removeElement(cont, null);
	}	

	public synchronized void removeElement(AbstractElementContainer cont, EventObject e) {
		if (cont == null) throw new NullPointerException();
		if (cont instanceof NodeContainer) {
			getNodeIndexManager().recycle(((NodeContainer)cont).getIndex());
			topologyHandler.nodeSuppressed((NodeContainer)cont);
		} else if (cont instanceof LinkContainer) {
			topologyHandler.linkSuppressed((LinkContainer)cont);
		} else if (cont instanceof LayerContainer) {
			topologyHandler.layerSuppressed((LayerContainer)cont);
		}
		ElementEvent ev = ElementEvent.creationSuppressionEvent(cont);
		fireElementEvent(ev);
//		fireRefreshDisplayUIEvent();
	}

	public void removeElements(Collection<? extends AbstractElementContainer> cont) {
		for (AbstractElementContainer aec : cont) {
			removeElement(aec);
		}
	}

	public void changeNodeIndex(NodeContainer nc, int newIndex) {
		if (getNodeContainer(newIndex) != null) {
			throw new IllegalStateException("Cannot assign given index to the index : already used");
		}
		NodeContainer newnc = new NodeContainer();
		nc.setIndex(newIndex);
		addNode(newnc, nc.getContainingLayerContainer(), null);
		nc.copyAttributesTo(newnc);
		for (LinkContainer lc : nc.getAllOutgoingLinks()) {
			LayerContainer layC = lc.getContainingLayerContainer();
			LinkContainer newlc = layC.newLink(newIndex, lc.getOtherNodeIndex(nc.getIndex()));
			lc.copyAttributesTo(newlc);
		}
		for (LinkContainer lc : nc.getAllIncomingLinks()) {
			LayerContainer layC = lc.getContainingLayerContainer();
			LinkContainer newlc = layC.newLink(lc.getOtherNodeIndex(nc.getIndex()),newIndex);
			lc.copyAttributesTo(newlc);
		}
		removeElement(nc);
		newnc.setNode(nc.getNode());
		nc.getNode().indexChanged(newIndex);
	}

	public void changeNodeIndex(int oldIndex, int newIndex) {
		changeNodeIndex(getNodeContainer(oldIndex), newIndex);
	}

	public void renumberNodes() {
		int indexMax = 0;
		for (int nc : getNodeIndexes()) {
			if (nc > indexMax) {
				indexMax = nc;
			}
		}
		indexMax++;
		for (NodeContainer nc : getNodeContainers()) {
			changeNodeIndex(nc, indexMax++);
		}
		indexMax = 0;
		for (NodeContainer nc : getNodeContainers()) {
			changeNodeIndex(nc, indexMax);
			indexMax++;
		}
	}

	/**
	 * Removes all links connecting the two given nodes, on all layers
	 */
	public void disconnectNodes(int idx1, int idx2) {
		for (LayerContainer lc : getLayerContainers()) {
			disconnectNodesInternal(idx1, idx2, lc);
		}
	}


	public void disconnect(int nodeIndex) {
		NodeContainer nc = getNodeContainer(nodeIndex);
		Collection<LinkContainer> col = nc.getAllConnectedLinks();
		for (LinkContainer lc : col) {
			removeElement(lc);
		}
	}
	/**
	 * Removes all links connecting the two given nodes, on the specified layer.
	 * If layername is null, all layers are considered
	 */
	public void disconnectNodes(int idx1, int idx2, String layerName) {
		if (layerName == null) {
			disconnectNodes(idx1, idx2);
		}
		LayerContainer lc = getLayerContainer(layerName);
		if (lc == null) {
			throw new NullPointerException("Not layer has the given name " + layerName);
		}
		disconnectNodesInternal(idx1, idx2, lc);
	}

	private void disconnectNodesInternal(int idx1, int idx2, LayerContainer lc) {
		LinkContainer linc;
		while ((linc = lc.getUndirectedLink(idx1, idx2)) != null) {
			removeElement(linc);
		}
	}

/*	public JavancoClassesLoader getJavancoClassesLoader() {
		return this.factory.getJavancoClassesLoader();
	}*/

	/**
	 * Return the <code>GroovyScriptManager</code> associated with this network
	 * @return the <code>GroovyScriptManager</code> associated with this network
	 */
	public GroovyScriptManager getGroovyScriptManager() {
		if (groovyScriptManager == null) {
			groovyScriptManager = new GroovyScriptManager(this);
		}
		return this.groovyScriptManager;
	}

	public String getXMLElementName() {
		return XMLTagKeywords.NETWORK.toString();
	}

	private Map<String, XMLDataHandler> getXMLHandlerList() {
		if (xmlHandlerList == null) {
			xmlHandlerList = new SimpleMap<String, XMLDataHandler>();
		}
		return xmlHandlerList;
	}

	public XMLDataHandler getXMLHandler(String name) {
		if (xmlHandlerList == null) {
			return null;
		} else {
			return getXMLHandlerList().get(name);
		}
	}

	public void registerXMLHandler(XMLDataHandler handler) {
		getXMLHandlerList().put(handler.getXMLElementName(), handler);
		// test
		if (xmlElement.selectSingleElement(handler.getXMLElementName()) == null) {
			JavancoXMLElement givenByHandler = handler.getXML();
			if (givenByHandler != null) {
				givenByHandler.detach();
				xmlElement.add(givenByHandler);
			}
		}
	}

	private synchronized void setXML(Element e) {
		JavancoXMLElement el = JavancoXMLElement.castElement(e);
		if (el != null) {
			setXML(el);
		} else {
			this.displayWarning("Impossible to set given XML element. Should be build using a NetworkDocumentFactory");
		}
	}

	private synchronized void setXML(JavancoXMLElement e) {
		boolean eventEnabled = isEventEnabled();
		try {
			resetNodeIndexManager();
			if (eventEnabled) {
				this.setModificationEventEnabledWithoutCallingBigChanges(false);
			}
			if (!(e.getName().equals(this.getXMLElementName()))) {
				throw new IllegalStateException("Unkown format, root element must be " + getXMLElementName());
			}
			MainDataHandler.setXML(this, e);
			if (xmlHandlerList != null) {
				for (XMLDataHandler handler : xmlHandlerList.values()) {
					JavancoXMLElement subElement = e.element(handler.getXMLElementName());
					if (subElement != null) {
						handler.setXML(subElement);
					} else {
						logger.info("No <" + handler.getXMLElementName() + "> xml element founded in file for handler " + handler);
					}
				}
			}
			xmlElement = e;
		}
		finally {
			if (eventEnabled) {
				this.setModificationEventEnabledWithoutCallingBigChanges(true);
			}
			this.fireGraphLoadedEvent();
		}
	}

	public JavancoXMLElement getXML() {
		return xmlElement;
	}

	public JavancoXMLElement getCanonicalXML() {
		JavancoXMLElement element = new JavancoXMLElement(getXMLElementName().toString());
		for (XMLDataHandler handler : this.xmlHandlerList.values()) {
			element.add(handler.getCanonicalXML());
		}
		return element;
	}

	public void dumpGraph() {
		dumpGraph(System.out);
	}

	public void dumpGraph(OutputStream out) {
		XMLSerialisationManager.dumpGraph(this, out);
	}

	public void dumpGraph(OutputStream out, String format) throws java.io.IOException {
		if (format.equals("xml")) {
			XMLSerialisationManager.dumpGraph(this, out);
		} else if (format.equals("txt")) {
			S_D_W_X_Y_Exporter ex = new S_D_W_X_Y_Exporter();
			ex.exportTopology(this, out);
		}
	}
	
	public void setBestFit() {
		AbstractGraphicalUI ui = getUIDelegate().getDefaultGraphicalUI(this, true);
		ui.setBestFitAndKeepNodeRatio(JavancoDefaultGUI.getAndShowDefaultGUI().getActuallyActiveInternalFrame().getSize());
	}

	public synchronized void saveNetwork(JavancoFile f, String format) throws java.io.IOException {
		if (uiDelegate != null) {
			uiDelegate.prepareForSave();
		}
		dumpGraph(new java.io.FileOutputStream(f), format);
	}

	public synchronized void saveNetwork(String fileName) throws java.io.IOException {
		if (!fileName.endsWith(".xml"))
			fileName += ".xml";
		saveNetwork(JavancoFile.findFile(fileName, JavancoFile.getDefaultMNDfilesDir()), "xml");
	}

	public synchronized void saveNetwork(String fileName, String dir) throws java.io.IOException {
		saveNetwork(JavancoFile.findFile(fileName, dir), "xml");
	}

/*	public boolean saveGraphImageUnsafe(String fileName, int w, int h) {
		try {
			saveGraphImage(fileName, w, h);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}*/
	
/*	public boolean saveGraphImageUnsafe(String layer) {
		return saveGraphImageUnsafe("graph", layer);
	}
	
	public boolean saveGraphImageUnsafe(String fileName, String layer) {
		return saveGraphImageUnsafe(fileName, layer, 800, 800);
	}	

	public boolean saveGraphImageUnsafe(String fileName, String layer, int w, int h) {
		try {
			for (LayerContainer lc : this.getLayerContainers()) {
				if (lc.getName().equals(layer) == false) {
					lc.setDisplayed(false);
				}
			}
			saveGraphImage(fileName, w, h);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}*/

/*	public void saveGraphImage(String fileName, int w, int h) throws Exception {
		AbstractGraphicalUI ui = getUIDelegate().getDefaultGraphicalUI(this, true);
		ui.saveGraphImage(fileName, w, h);
	}*/
	
	public void saveGraphImageAsDisplayed(String fileName) {
		AbstractGraphicalUI ui = getUIDelegate().getDefaultGraphicalUI(this, true);
		ImageWriter.writeImageInFilePNG(fileName, ui.getActualViewImage());
	}
	
	public void saveGraphImage(String fileName, int x, int y) {
		AbstractGraphicalUI ui = getUIDelegate().getDefaultGraphicalUI(this, true);
		ImageWriter.writeImageInFilePNG(fileName, ui.getFullView(x, y));		
	}
	
	public void saveGraphImage(String fileName) {
		AbstractGraphicalUI ui = getUIDelegate().getDefaultGraphicalUI(this, true);
		ImageWriter.writeImageInFilePNG(fileName, ui.getFullView());		
	}
	
	public void saveGraphImage(OutputStream s, int x, int y) {
		AbstractGraphicalUI ui = getUIDelegate().getDefaultGraphicalUI(this, true);
		try {
			ImageWriter.writeImagePNGtostream(s, ui.getFullView(x, y));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveGraphSVG(OutputStream s, int w, int h) throws Exception {
		SVGPainter.saveGraphSVG(this, s, w, h);
	}

	public void show() {
		JavancoGUI.displayGraph(this, 900, 600);
	}

	public int getHighestNodeIndex() {
		if (nodeIndexManager != null) {
			return nodeIndexManager.getHighestIndex();
		}
		return -1;
	}
	
	public boolean hasContainer(AbstractElementContainer cont) {
		if (cont instanceof LinkContainer) {
			return getLinkContainers().contains(cont);
		} else if (cont instanceof NodeContainer) {
			return getNodeContainers().contains(cont);
		} else if (cont instanceof LayerContainer){
			return getLayerContainers().contains(cont);
		} else {
			throw new IllegalStateException("Container of unknown type");
		}
	}

	/**
	 * Return the smallest node index which is higher than the one given in parameter,
	 * or -1 if no such node exists.
	 */
	public int getNextNodeIndex(int i) {
		NodeContainer next = null;
		while ((next == null) && (i <= this.getHighestNodeIndex())) {
			i++;
			next = this.getNodeContainer(i);
		}
		if (next != null) {
			return next.getIndex();
		} else {
			return -1;
		}
	}

	public int getSmallestNodeIndex() {
		if (this.getNodeContainers().size() == 0) { return -1; }
		NodeContainer next = null;
		int i = 0;
		while (next == null) {
			next = this.getNodeContainer(i);
			i++;
		}
		return next.getIndex();
	}

	private void resetNodeIndexManager() {
		if (nodeIndexManager != null) {
			nodeIndexManager.reset();
		}
	}

	private NodeIndexManager getNodeIndexManager() {
		if (nodeIndexManager == null) {
			nodeIndexManager = new NodeIndexManager();
		}
		return nodeIndexManager;
	}

	public AbstractGraphHandler copyLayer(String layerName) {
		GraphHandlerFactory fac = new GraphHandlerFactory(ch.epfl.javanco.base.DefaultGraphHandler.class);
		AbstractGraphHandler graphI = fac.getNewGraphHandler();
		try { graphI.newLayer(layerName); } catch (Exception e) {}
		for (int j = 0 ; j < this.getHighestNodeIndex() +1 ; j++) {
			graphI.newNode();
		}

		LayerContainer lay = this.getLayerContainer(layerName);
		for (LinkContainer lc :  lay.getLinkContainers()) {
			graphI.newLink(lc.getStartNodeIndex(), lc.getEndNodeIndex(), lc.getLink().getClass(), null);
		}
		return graphI;
	}

	/**
	 * This method must be called once if one wants to record the coordinates
	 * and other graphical informations
	 */
	public void activateGraphicalDataHandler() {
		activateMainDataHandler();
		if (getUIDelegate().getGraphicalDataHandler() == null) {
			GraphicalDataHandler dh = new GraphicalDataHandler(this);
			uiDelegate.setGraphicalDataHandler(dh);
			registerXMLHandler(dh);
			notifyExistingGraphInformation(dh);
			addStructuralElementListener(dh);
		}
	}
	
	/**
	 *  This method permits to have a "late refresh" of the graph information when a new handler is added
	 * @param listener
	 */
	private void notifyExistingGraphInformation(ElementListener listener) {
		for (LayerContainer lc : getLayerContainers()) {
			listener.layerCreated(lc, ElementEvent.createCreationEvent(lc));
		}
		for (NodeContainer nc : getNodeContainers()) {
			listener.nodeCreated(nc, ElementEvent.createCreationEvent(nc));
		}
		for (LinkContainer lc : getLinkContainers()) {
			listener.linkCreated(lc, ElementEvent.createCreationEvent(lc));
		}
	}

	//=================================================================
	// CONSIDER MOVING THIS METHOD IN DEFAULTGRAPHHANDLER
	//=================================================================
	public void activateMainDataHandler() {
		for (XMLDataHandler xHandler : getXMLHandlerList().values()) {
			if (xHandler instanceof MainDataHandler) {
				return;
			}
		}
		// MISSING HERE : create xml object for node and links already present
		MainDataHandler mdh = new MainDataHandler(this);
		registerXMLHandler(mdh);
		notifyExistingGraphInformation(mdh);
		addStructuralElementListener(mdh);
	}

	/**
	 * A valid address is of type "http://localhost:8080/ws/ch.epfl.tools.RWA:execute"
	 */
	public void callRemoteMNDService(String address, Hashtable<String, String> table) {
		try {
			Element e = MNDService.callRemoteMNDService(this.getXML().getBackedElement(), address, table);
			setXML(e);
		}
		catch (Exception e) {
			logger.error("Failed to call remote MNDService. " + e.getMessage());
		}
	}

	public Object callLocalMNDService(String class_, boolean local, Hashtable<String, String> table) throws MNDServiceException {
		return MNDService.callLocalMNDService(this, class_, local, table);
	}

	public void removeAllNodesButSome(ArrayList<Integer> previousNodes) {
		ArrayList<NodeContainer> toRemove = new ArrayList<NodeContainer>();
		for (NodeContainer nc : this.getNodeContainers()) {
			if (!previousNodes.contains(nc.getIndex())) {
				toRemove.add(nc);
			}
		}
		this.removeElements(toRemove);
	}

	public ArrayList<LinkContainer> connectAll(ArrayList<NodeContainer> al) {
		ArrayList<LinkContainer> links = new ArrayList<LinkContainer>();
		for (int i = 0 ; i < al.size()-1; i++) {
			for (int j = i+1 ; j < al.size() ; j++) {
				links.add(this.newLink(al.get(i), al.get(j)));
			}
		}
		return links;
	}


}
