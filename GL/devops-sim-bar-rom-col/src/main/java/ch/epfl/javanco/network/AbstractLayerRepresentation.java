package ch.epfl.javanco.network;

import java.util.Collection;
import java.util.List;

public abstract class AbstractLayerRepresentation {

	private LayerContainer layerContainer = null;

	protected AbstractLayerRepresentation(LayerContainer ag) {
		layerContainer = ag;
	}

	protected LayerContainer getLayerContainer() {
		return layerContainer;
	}

	abstract List<NodeContainer> getAllNodeContainers();
	abstract List<LinkContainer> getAllLinkContainers();

	abstract Collection<LinkContainer> getLinkContainers(int start, int end);
	abstract List<LinkContainer> getOutgoingLinks(int start);
	abstract List<LinkContainer> getIncomingLinks(int end);
	
	public static class DuplicateLinkException extends IllegalStateException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int i,j;
		
		DuplicateLinkException(int i, int j) {
			super("Cannot define twice the same link (" + i + "-" + j + ") in the same layer");
			this.i = i;
			this.j = j;			
		}
	}

	/**
	 * VIrtual is used to mention if node is really contained on this layer or on another one
	 */
	abstract void addNode(NodeContainer nodeContainer, boolean virtual);
	abstract NodeContainer removeNode(NodeContainer nodeContainer);

	abstract void addLink(LinkContainer linkContainer);
	abstract LinkContainer removeLink(LinkContainer link);

	abstract void removeAllLinks();

	abstract AbstractElementContainer removeElement(AbstractElementContainer el);

	abstract NodeContainer nodeAt(int idx);

	protected boolean contains(AbstractElementContainer c) {
		if (c instanceof LinkContainer) {
			return contains((LinkContainer)c);
		} else if (c instanceof NodeContainer) {
			return contains((NodeContainer)c);
		} else {
			throw new IllegalStateException("A representation can only contain NodeContainers and LinkContainers and not " + c.getClass());
		}
	}

	protected boolean contains(NodeContainer c) {
		return this.getAllNodeContainers().contains(c);
	}

	protected boolean contains(LinkContainer c) {
		return this.getAllLinkContainers().contains(c);
	}

}
