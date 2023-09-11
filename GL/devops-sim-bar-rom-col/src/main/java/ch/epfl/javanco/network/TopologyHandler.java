package ch.epfl.javanco.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.NetworkEditionEventHandler;

public class TopologyHandler {

	/**
	 * Root handler. Permit the access to all information or modules concerned
	 * by this graph
	 */
	private AbstractGraphHandler                        handler               = null;
	/**
	 * This collection contains all layers, which contain the topologies. The real
	 * topological information is stored in this object
	 */
	private SimpleMap<String, LayerContainer>   layerContainerTable   = null;

	NetworkEditionEventHandler getEventHandler() {
		return handler;
	}

	/**
	 * Default constructor, needs the root handler as argument
	 */
	public TopologyHandler(AbstractGraphHandler handler) {
		this.handler = handler;
	}

	/**
	 * Returns a collection of strings containing all the names of the different layers of the
	 * contained graph. This strings shall be used to identify the layers (in menus, etc).
	 * @return All the names of the layers under the form of a <code>Collection&lt;String&gt;</code>
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getLayerNames() {
		if (layerContainerTable == null) {
			return Collections.EMPTY_SET;
		}
		return layerContainerTable.keySet();
	}

	/**
	 * Returns the container of the link with the specified extremities on the specified layer
	 * (if any). Otherwise returns null.
	 * @return the container of the link with the specified extremities or null
	 */
	public LinkContainer getLinkContainer(int orig, int dest, String layerName) {
		LayerContainer layerC = getLayerContainer(layerName);
		if (layerC != null) {
			return layerC.getLinkContainer(orig, dest);
		}
		return null;
	}

	/**
	 * Returns the container of the link with the specified extremities. If network
	 * has many layers, containing multiple links with the given extremities, only
	 * one (arbitrally choosed) will be returned. Specify the layer name using
	 * the <code>getLinkContainer(int orig, int dest, String layerName)</code>
	 * method.
	 * @param orig Origin index of the desired link
	 * @param dest Destination index of the desired link
	 */
	public LinkContainer getLinkContainer(int orig, int dest) {
		for (LayerContainer layerC : this.getLayerContainers()) {
			LinkContainer link = layerC.getLinkContainer(orig, dest);
			if (link != null) {
				return link;
			}
		}
		return null;
	}


	/**
	 * Returns the container of the link with the specified extremities. If param
	 * <code>dir</code> is <code>true</code>, the method will returned exactly
	 * the same as the <code>getLinkContainer(int orig, int dest)</code> method.
	 * If <code>dir</code> is <code>false</code>, it will neglect the direction
	 * of the link, searching only for a link having the specified extremities
	 * @param ext1 Origin index of the desired link
	 * @param ext2 Destination index of the desired link
	 * @param dir  If direction of link must be taken in account
	 */
	public LinkContainer getLinkContainer(int ext1, int ext2, boolean dir) {
		LinkContainer link = this.getLinkContainer(ext1,ext2);
		if (dir == true) {
			return link;
		}
		if (link != null) {
			return link;
		} else {
			return this.getLinkContainer(ext2,ext1);
		}
	}

	/**
	 * Return all links presents between two nodes. Takes into account the
	 * direction of the link if boolean parameter is <code>true</code>. In this
	 * case, the <code> ext1 </code> parameter acts for origin and <code> ext2
	 * </code> for destination. This method nevers returns null, at least an
	 * empty list
	 */
	public Collection<LinkContainer> getLinkContainers(int etx1, int etx2, boolean dir) {
		Collection<LinkContainer> c = new ArrayList<LinkContainer>();
		for (LayerContainer layerC : this.getLayerContainers()) {
			c.addAll(layerC.getLinkContainers(etx1, etx2));
			if (dir == false) {
				c.addAll(layerC.getLinkContainers(etx2, etx1));
			}
		}
		return c;
	}

	/**
	 * Returns the container of the node with the specified index on the specified layer
	 * (if any). Otherwise returns null.
	 * @return the container of the node with the specified index or null
	 */
	public NodeContainer getNodeContainer(int nodeId) {
		for (LayerContainer layerContainer : layerContainerTable.values()) {
			NodeContainer nodeC = layerContainer.getNode(nodeId);
			if (nodeC != null) {
				return nodeC;
			}
		}
		return null;
	}

	/**
	 * Returns the container of the layer with the specified name
	 * (if any). Otherwise returns null.
	 * @return the container of the node with the specified name or null
	 */
	public LayerContainer getLayerContainer(String layerName) {
		if (layerContainerTable == null) {
			return null;
		}
		if (layerName == null) {
			return null;
		}
		return layerContainerTable.get(layerName);
	}

	/**
	 * Returns all containers of layers
	 * @return all containers of layers
	 */
	@SuppressWarnings("unchecked")
	public Collection<LayerContainer> getLayerContainers() {
		if (layerContainerTable == null) {
			return Collections.EMPTY_LIST;
		}
		return new ArrayList<LayerContainer>(layerContainerTable.values());
	}

	/**
	 * Reacts to a creation of a new layer. The created layer will be automatically defined
	 * as the "currently edited layer" (what will fire a new <code>EditedLayerEvent</code>
	 * event.
	 * Needed by the <code>ElementCreationListener</code>
	 * @param ev an <code>ElementCreationEvent</code> object
	 */
	public void layerCreated(LayerContainer layerContainer) {
		layerContainer.setAbstractGraphHandler(this.handler);
		if (layerContainerTable == null) {
			layerContainerTable = new SimpleMap<String, LayerContainer>();
		}
		layerContainerTable.put(layerContainer.getKey(), layerContainer);
		handler.setEditedLayer(layerContainer.getKey());
	}

	/**
	 * Reacts to a creation of a new node. The created node will be added to the
	 * "currently edited layer".
	 * Needed by the <code>ElementCreationListener</code>
	 * @param ev an <code>ElementCreationEvent</code> object
	 */
	public void nodeCreated(NodeContainer nodeContainer, LayerContainer current) {
		if (current == null) {
			throw new IllegalStateException("Cannot create a node if no layer is selected to receive it");
		}
		for (LayerContainer layC : layerContainerTable.values()) {
			layC.addNodeInternal(nodeContainer, !layC.equals(current));
		}
		nodeContainer.setAbstractGraphHandler(this.handler);
	}
	/**
	 * Reacts to a creation of a new link. The created link will be added to the
	 * "currently edited layer".
	 * Needed by the <code>ElementCreationListener</code>
	 * @param e an <code>ElementCreationEvent</code> object
	 */
	public void linkCreated(LinkContainer linkContainer, LayerContainer current) {
		if (current == null) {
			throw new IllegalStateException("Cannot create a node if no layer is selected to receive it");
		}
		linkContainer.setAbstractGraphHandler(this.handler);
		current.addLinkInternal(linkContainer);
	}

	public void nodeSuppressed(NodeContainer nodeC) {
		Collection<LinkContainer> linksToRemove = nodeC.getAllConnectedLinks();
		for (LinkContainer linkToRemove : linksToRemove) {
			handler.removeElement(linkToRemove);
		}
		for (LayerContainer layC : this.layerContainerTable.values()) {
			layC.removeInternal(nodeC);
		}
	}

	public void linkSuppressed(LinkContainer linkC) {
		LayerContainer layerC = linkC.getContainingLayerContainer();
		layerC.removeInternal(linkC);
	}

	public void layerSuppressed(LayerContainer layerC) {
		Collection<LinkContainer> links = layerC.getLinkContainers();
		if (links != null) {
			for (LinkContainer linkC : links) {
				handler.removeElement(linkC);
			}
		}
		Collection<NodeContainer> nodes = layerC.getNodeContainers();
		if (nodes != null) {
			for (NodeContainer nodeC : nodes) {
				handler.removeElement(nodeC);
			}
		}
		layerC.setDisplayed(false);
		if (layerContainerTable != null) {
			layerContainerTable.remove(layerC.getKey());
		} else {
			assert (0==1);
		}
		if (layerC.equals(handler.getEditedLayer())) {
			if (layerContainerTable.size() > 0) {
				handler.setEditedLayer(layerContainerTable.keySet().iterator().next());
			}
		}
	}

	public void clearLayers() {
		layerContainerTable.clear();
	}
}
