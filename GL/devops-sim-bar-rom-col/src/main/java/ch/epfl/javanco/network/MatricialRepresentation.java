package ch.epfl.javanco.network;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

class MatricialRepresentation extends AbstractLayerRepresentation {

	MatricialRepresentation(LayerContainer agr) {
		super(agr);
	}

	private Vector<Vector<Vector<LinkContainer>>> incidenceMatrix = null;
	private Vector<NodeContainer> nodes  = null;

	@Override
	protected List<NodeContainer> getAllNodeContainers() {
		if (nodes == null) {
			return new Vector<NodeContainer>(0);
		}
		Vector<NodeContainer> c = new Vector<NodeContainer>();
		for (NodeContainer nodeContainer : nodes) {
			if (nodeContainer != null) {
				c.add(nodeContainer);
			}
		}
		return c;
	}

	protected List<Node> getAllNodes(){
		if (nodes == null) {
			return new Vector<Node>(0);
		}
		Vector<Node> c = new Vector<Node>();
		for (NodeContainer nodeContainer : nodes) {
			if (nodeContainer != null) {
				c.add(nodeContainer.getNode());
			}
		}
		return c;
	}

	@Override
	protected List<LinkContainer> getAllLinkContainers(){
		if (incidenceMatrix == null) {
			return new Vector<LinkContainer>(0);
		}
		Vector<LinkContainer> c = new Vector<LinkContainer>();
		for (Vector<Vector<LinkContainer>> v : incidenceMatrix) {
			if (v != null) {
				for (Vector<LinkContainer> v2 : v) {
					if (v2 != null) {
						c.addAll(v2);
					}
				}
			}
		}
		return c;
	}

	@Override
	protected List<LinkContainer> getOutgoingLinks(int start) {
		if (incidenceMatrix == null) {
			return new Vector<LinkContainer>(0);
		}
		if (incidenceMatrix.size() <= start) {
			return new Vector<LinkContainer>(0);
		}
		if (incidenceMatrix.get(start) == null) {
			return new Vector<LinkContainer>(0);
		}
		Vector<LinkContainer> c = new Vector<LinkContainer>();
		for (Vector<LinkContainer> cell : incidenceMatrix.get(start)) {
			if (cell != null) {
				c.addAll(cell);
			}
		}
		return c;
	}

	@Override
	protected List<LinkContainer> getIncomingLinks(int end) {
		if (incidenceMatrix == null) {
			return new Vector<LinkContainer>(0);
		}
		Vector<LinkContainer> c = new Vector<LinkContainer>();
		for (Vector<Vector<LinkContainer>> column : incidenceMatrix) {
			if (column != null) {
				if (column.size() > end) {
					Vector<LinkContainer> cell = column.get(end);
					if (cell != null) {
						c.addAll(cell);
					}
				}
			}
		}
		return c;
	}

	@Override
	protected Collection<LinkContainer>	getLinkContainers(int start, int end){
		if (incidenceMatrix == null) {
			return new Vector<LinkContainer>(0);
		}
		if (incidenceMatrix.size() <= start) {
			return new Vector<LinkContainer>(0);
		}
		if (incidenceMatrix.elementAt(start) == null) {
			return new Vector<LinkContainer>(0);
		}
		Vector<Vector<LinkContainer>> column = incidenceMatrix.elementAt(start);
		if (column.size() <= end) {
			return new Vector<LinkContainer>(0);
		}
		if (column.elementAt(end) == null) {
			return new Vector<LinkContainer>(0);
		}
		Vector<LinkContainer> c = new Vector<LinkContainer>();
		for (LinkContainer container : column.elementAt(end)) {
			c.add(container);
		}
		return c;
	}

	@Override
	protected void addNode(NodeContainer nodeContainer, boolean virt) {
		if (virt == false) {
			int index = nodeContainer.getIndex();
			if (index < 0) {
				throw new IllegalStateException("Trying to add a node without index, index must be choosen first");
			}
			if (nodes == null) {
				nodes = new Vector<NodeContainer>();
			}
			if (nodes.size() <= index) {
				nodes.setSize(index + 1);
			}
			if (nodes.elementAt(index) != null) {
				throw new IllegalStateException("Trying to add twice a node using the same index");
			}
			nodeContainer.setContainingLayerContainer(this.getLayerContainer());
			nodes.set(nodeContainer.getIndex(),nodeContainer);
		}
	}

	@Override
	protected void addLink(LinkContainer linkContainer) throws IllegalStateException {
		int start = linkContainer.getStartNodeIndex();
		int end   = linkContainer.getEndNodeIndex();

		if (incidenceMatrix == null) {
			incidenceMatrix = new Vector<Vector<Vector<LinkContainer>>>();
		}
		if (incidenceMatrix.size() <= start) {
			incidenceMatrix.setSize(start + 1);
		}
		if (incidenceMatrix.elementAt(start) == null) {
			incidenceMatrix.setElementAt(new Vector<Vector<LinkContainer>>(end +1), start);
		}

		Vector<Vector<LinkContainer>> startColumn = incidenceMatrix.elementAt(start);

		if (startColumn.size() < end+1) {
			startColumn.setSize(end +1);
		}

		if (startColumn.elementAt(end) == null) {
			startColumn.set(end, new Vector<LinkContainer>(1));
		}

		Vector<LinkContainer> cell = startColumn.elementAt(end);

		cell.add(linkContainer);

		linkContainer.setContainingLayerContainer(getLayerContainer());
	}

	@Override
	protected AbstractElementContainer removeElement(AbstractElementContainer el){
		if (el instanceof NodeContainer) {
			return removeNode((NodeContainer)el);
		} else if (el instanceof LinkContainer) {
			return removeLink((LinkContainer)el);
		} else {
			assert(1 == 0) : "type of element not recognized";
			return null;
		}
	}

	@Override
	protected NodeContainer removeNode(NodeContainer nodeContainer){
		if (nodes == null) {
			return null;
		}
		if (nodes.size() == 0) {
			return null;
		}
		int idx = nodes.indexOf(nodeContainer);

		NodeContainer toReturn = null;
		if (idx >= 0) {
			toReturn = nodes.get(idx);
			nodes.set(idx, null);
		}
		if (incidenceMatrix != null) {
			for (int i = 0 ; i < nodes.size() ; i++) {
				if (incidenceMatrix.size() > i) {
					Vector<Vector<LinkContainer>> column = incidenceMatrix.get(i);
					if (column != null) {
						if (column.size() > idx) {
							column.set(idx, null);
							Vector<LinkContainer> cell = column.get(idx);
							if (cell != null) {
								cell.clear();
							}
						}
					}
				}
			}
			if (incidenceMatrix.size() > idx) {
				Vector<Vector<LinkContainer>> column = incidenceMatrix.get(idx);
				if (column != null) {
					column.clear();
				}
				incidenceMatrix.set(idx, null);
			}
			trimMatrix();
		}
		return toReturn;
	}



	@Override
	protected LinkContainer removeLink(LinkContainer linkContainer){
		int start = linkContainer.getStartNodeIndex();
		int end   = linkContainer.getEndNodeIndex();
		boolean result = incidenceMatrix.get(start).get(end).remove(linkContainer);
		assert (result) : "removing not existing link";
		return linkContainer;
	}

	@Override
	protected void removeAllLinks() {
		incidenceMatrix.removeAllElements();
	}

	@Override
	protected NodeContainer nodeAt(int idx) {
		if (nodes == null) {
			return null;
		}
		if (nodes.size() <= idx) {
			return null;
		}
		if (nodes.elementAt(idx) == null) {
			return null;
		}
		return nodes.elementAt(idx);
	}

	protected int indexOfNode(Node node) {
		if (nodes == null) {
			return -1;
		}
		if (nodes.size() <= 0) {
			return -1;
		}
		return nodes.indexOf(node);
	}

	private void trimMatrix() {
		for (int i = nodes.size() - 1 ; i <= 0 ; i--) {
			if (nodes.get(i) == null) {
				nodes.setSize(nodes.size() - 1);
				break;
			}
		}
		for (int i = incidenceMatrix.size() -1; i <= 0 ; i--) {
			if (incidenceMatrix.get(i) == null) {
				incidenceMatrix.setSize(incidenceMatrix.size() -1);
				break;
			}
		}
		for (int i = incidenceMatrix.size() -1; i <= 0 ; i--) {
			for (int j = incidenceMatrix.get(i).size() - 1;	i <= 0 ; i--) {
				if (incidenceMatrix.get(i).get(j) == null) {
					incidenceMatrix.get(i).setSize(incidenceMatrix.get(i).size() - 1);
				}
			}
		}
		nodes.trimToSize();
		incidenceMatrix.trimToSize();
	}

}
