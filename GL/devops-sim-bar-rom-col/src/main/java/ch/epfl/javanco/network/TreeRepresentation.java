package ch.epfl.javanco.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

class TreeRepresentation extends AbstractLayerRepresentation {

	private static class SubStructure {
		private NodeContainer n;
		private boolean isOnLayer = false;
		private TreeMap<Integer, LinkContainer> outGo;
		private TreeMap<Integer, LinkContainer> inCo;
		private SubStructure(NodeContainer n, boolean isOnlayer) {
			this.isOnLayer = isOnlayer;
			this.n = n;
		}
		private void addOutgoingLink(int end, LinkContainer lc) {
			if (outGo == null) {
				outGo = new TreeMap<Integer, LinkContainer>();
			}
			if (outGo.get(end) != null) {
				throw new DuplicateLinkException(lc.getStartNodeIndex(), lc.getEndNodeIndex());
			}
			synchronized (outGo) {
				outGo.put(end, lc);
			}
		}
		private void addIncomingLink(int start, LinkContainer lc) {
			if (inCo == null) {
				inCo = new TreeMap<Integer, LinkContainer>();
			}
			if (inCo.get(start) != null) {
				throw new DuplicateLinkException(lc.getStartNodeIndex(), lc.getEndNodeIndex());
			}
			synchronized (inCo) {
				inCo.put(start, lc);
			}
		}

		@Override
		public String toString() {
			return "'" + n.getIndex() + "'";
		}
	}

//	public final static Class<TreeRepresentation> TREE_REPRESENTATION = TreeRepresentation.class;

	private Vector<SubStructure> internalArray = new Vector<SubStructure>(20,20);
	private ArrayList<SubStructure> internalList = new ArrayList<SubStructure>();

	TreeRepresentation(LayerContainer agr) {
		super(agr);

	}

	@Override
	List<NodeContainer> getAllNodeContainers() {
		if (internalList == null) {
			return new ArrayList<NodeContainer>(0);
		}
		List<NodeContainer> c = new ArrayList<NodeContainer>(internalList.size());
		synchronized (internalList) {
			for (SubStructure sb : internalList) {
				if (sb.isOnLayer) {
					c.add(sb.n);
				}
			}
		}
		return c;
	}

	@Override
	List<LinkContainer> getAllLinkContainers() {
		if (internalList == null) {
			return VOID_ARRAY;
		}
		List<LinkContainer> c = new LinkedList<LinkContainer>();
		synchronized (internalList) {
			for (SubStructure sb : internalList) {
				TreeMap<Integer, LinkContainer> linkMap = sb.outGo;
				if (linkMap != null) {
					c.addAll(linkMap.values());
				}
			}
		}
		return c;
	}

	private static final ArrayList<LinkContainer> VOID_ARRAY = new ArrayList<LinkContainer>(0);

	@Override
	protected List<LinkContainer> getOutgoingLinks(int start) {
		if (internalList == null) {
			return VOID_ARRAY;
		}
		if (internalArray.size() <= start) {
			return VOID_ARRAY;
		}
		SubStructure s = internalArray.get(start);
		if (s == null) {
			return VOID_ARRAY;
		}
		List<LinkContainer> c;
		if (s.outGo != null) {
			c = new ArrayList<LinkContainer>(s.outGo.size());
			synchronized (s.outGo) {
				c.addAll(s.outGo.values());
			}
		} else {
			c = new ArrayList<LinkContainer>(0);
		}
		return c;
	}
	@Override
	protected List<LinkContainer> getIncomingLinks(int end) {
		if (internalList == null) {
			return new ArrayList<LinkContainer>(0);
		}
		SubStructure s = internalArray.get(end);
		if (s == null) {
			return new ArrayList<LinkContainer>(0);
		}
		List<LinkContainer> c;
		if (s.inCo != null) {
			c = new ArrayList<LinkContainer>(s.inCo.size());
			synchronized (s.inCo) {
				c.addAll(s.inCo.values());
			}
		} else {
			c = new ArrayList<LinkContainer>(0);
		}
		return c;
	}

	@Override
	Collection<LinkContainer>	getLinkContainers(int start, int end){
		if (internalList == null) {
			return new ArrayList<LinkContainer>(0);
		}
		SubStructure s = internalArray.get(start);
		Collection<LinkContainer> c = new ArrayList<LinkContainer>(1);
		if (s != null && s.outGo != null) {
			synchronized (s.outGo) {
				LinkContainer link = s.outGo.get(end);
				if (link != null) {
					c.add(link);
				}
			}
		}
		return c;
	}

	/**
	 * VIrtual is used to mention if node is really contained on this layer or on another one
	 */
	@Override
	void addNode(NodeContainer nodeContainer, boolean virtual) {
		addNodeInternal(new SubStructure(nodeContainer, !virtual));
	/*	if (!virtual) {
			nodeContainer.setContainingLayerContainer(this.getLayerContainer());
		}*/
	}

	private void addNodeInternal(SubStructure s) {
		int index = s.n.getIndex();
		if (index < 0) {
			throw new IllegalStateException("Trying to add a node without index, index must be choosen first");
		}
		/*	if ((internalArray.size() > index) && (internalArray.get(index) != null)) {
			throw new IllegalStateException("Trying to add twice a node using the same index");
		}	*/
		if (internalArray.size() < (index+1)) {
			internalArray.setSize(index+1);
		}
		synchronized (internalArray) {
			internalArray.set(s.n.getIndex(),s);
		}
		synchronized (internalList) {
			internalList.add(s);
		}
	}

	@Override
	void addLink(LinkContainer linkContainer) {
		int start = linkContainer.getStartNodeIndex();
		int end   = linkContainer.getEndNodeIndex();
		int max = Math.max(start, end);
		if (internalArray.size() < max+1) {
			internalArray.setSize(max+1);
		}
		SubStructure startN = internalArray.get(start);
		SubStructure endN = internalArray.get(end);
		if (startN == null) {
			NodeContainer startC = linkContainer.getStartNodeContainer();
			startN = new SubStructure(startC, false);
			addNodeInternal(startN);
		}
		if (endN == null) {
			NodeContainer endC = linkContainer.getEndNodeContainer();
			endN = new SubStructure(endC, false);
			addNodeInternal(endN);
		}
		startN.addOutgoingLink(end, linkContainer);
		endN.addIncomingLink(start, linkContainer);
		linkContainer.setContainingLayerContainer(getLayerContainer());
	}
	@Override
	void removeAllLinks() {
		for (SubStructure sb : internalList) {
			if (sb.inCo != null) {
				synchronized (sb.inCo) {
					sb.inCo.clear();
				}
			}
			if (sb.outGo != null) {
				synchronized (sb.outGo) {
					sb.outGo.clear();
				}
			}
		}
	}

	@Override
	AbstractElementContainer removeElement(AbstractElementContainer cont){
		if (cont instanceof NodeContainer) {
			NodeContainer c = (NodeContainer)cont;
			return removeNode(c);
		} else if (cont instanceof LinkContainer) {
			LinkContainer lc = (LinkContainer)cont;
			return removeLink(lc);
		} else {
			throw new IllegalStateException("Cannot remove something else than node or link");
		}
	}

	@Override
	NodeContainer removeNode(NodeContainer node){
		SubStructure s = null;
		synchronized (internalList) {
			s = internalArray.set(node.getIndex(), null);
			internalList.remove(s);			
		}
		if (s == null) {
			assert (0==1) : "Impossible to remove an unexisting element";
			return null;
		}
		return s.n;
	}
	@Override
	LinkContainer removeLink(LinkContainer link){
		int startN = link.getStartNodeIndex();
		int endN   = link.getEndNodeIndex();
		SubStructure substart = internalArray.get(startN);
		SubStructure subend   = internalArray.get(endN);
		if ((substart == null) || (subend == null)) {
			throw new IllegalStateException("Cannot remove link if connecting node does not exist");
		}
		synchronized (substart.outGo) {
			substart.outGo.remove(endN);
		}
		synchronized (subend.inCo) {
			subend.inCo.remove(startN);
		}
		return link;
	}
	@Override
	NodeContainer nodeAt(int idx){
		if (internalArray == null) {
			return null;
		}
		synchronized (internalArray) {
			if (internalArray.size() < idx+1) {
				return null;
			}
			if (internalArray.get(idx) == null) {
				return null;
			}
			return internalArray.get(idx).n;
		}
	}
}