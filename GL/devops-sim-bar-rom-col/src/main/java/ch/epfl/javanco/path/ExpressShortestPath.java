package ch.epfl.javanco.path;

public class ExpressShortestPath {
	
/*	public static Path shortestPath(AbstractGraphHandler agh, int start, int end) {
		NodeContainer nc = agh.getNodeContainer(start);
		while (nc != null) {
			nc.getAllOutgoingLinks();
		
	}*/
	
	
	
/*
	public static Path shortestPath(int startNode,
			int endNode,
			String layerName,
			String attribName,
			AbstractGraphHandler agh,
			boolean directed) {
		if (agh.getLayerContainer(layerName) != null) {
			return shortestPath(agh.getNodeContainer(startNode),
					agh.getNodeContainer(endNode),
					layerName,
					attribName,
					agh,
					directed);
		}
		return null;
	}

	public static Path shortestPath(NodeContainer startNode,
			NodeContainer endNode,
			String layerName,
			String attribName,
			AbstractGraphHandler agh,
			boolean directed) {


		SortedLinkedList<CoefficientComparableContainer<Pair<NodeContainer, Path>, Float>> candidates =
			new SortedLinkedList<CoefficientComparableContainer<Pair<NodeContainer, Path>, Float>>();

		NodeContainer     actualNode = startNode;
		int               actualNodeIndex = startNode.getIndex();
		Path              actualPath = new Path();
		float             actualValue = 0;

		boolean[] reached = new boolean[agh.getHighestNodeIndex() + 1];
		int endNodeIndex = endNode.getIndex();
		reached[startNode.getIndex()] = true;

		actualPath.add(startNode.getIndex());

		while (actualPath != null) {
			Collection<LinkContainer> col = null;
			if (directed) {
				col = actualNode.getOutgoingLinks(layerName);
			} else {
				col = actualNode.getConnectedLinks(layerName);
			}
			for (LinkContainer l : col) {
				Path p = (Path)actualPath.clone();
				NodeContainer nextNode = l.getOtherNodeContainer(actualNodeIndex);
				if (!reached[nextNode.getIndex()]) {
					p.add(nextNode.getIndex());
					if (nextNode.getIndex() == endNodeIndex) {
						return p;
					}
					if (l.attribute(attribName, false) == null) {
						throw new IllegalStateException("Cannot compute shortest path based on attribute " + attribName + " : it is missing");
					}
					float score = actualValue + l.attribute(attribName).floatValue();
					Pair<NodeContainer, Path> pair = new Pair<NodeContainer, Path>(nextNode, p);
					CoefficientComparableContainer<Pair<NodeContainer, Path>, Float> cont =
						new CoefficientComparableContainer<Pair<NodeContainer, Path>, Float>(pair, score);
					candidates.add(cont);
				}
			}
			CoefficientComparableContainer<Pair<NodeContainer, Path>, Float> cc = candidates.pollFirst();
			if (cc != null) {
				Pair<NodeContainer, Path> pp = cc.getContainedObject();
				actualNode = pp.getFirst();
				actualNodeIndex = actualNode.getIndex();
				reached[actualNodeIndex] = true;
				// removing alternatives
				actualPath = pp.getSecond();
				actualValue = cc.getCoefficient();
			} else {
				actualPath = null;
			}
		}
		return null;
	}

	public static ArrayList<Path> kShortestPath(int startNode, int endNode, AbstractGraphHandler agh, int k) {
		return kShortestPath(startNode, endNode, "physical", "length", agh, true, k);
	}

	public static ArrayList<Path> kShortestPath(int startNode,
			int endNode,
			String layerName,
			String attribName,
			AbstractGraphHandler agh,
			boolean directed,
			int k) {
		if (agh.getLayerContainer(layerName) != null) {
			return kShortestPath(agh.getNodeContainer(startNode),
					agh.getNodeContainer(endNode),
					layerName,
					attribName,
					agh,
					directed,
					k);
		}
		return null;
	}


	public static ArrayList<Path> kShortestPath(NodeContainer startNode,
			NodeContainer endNode,
			String layerName,
			String attribName,
			AbstractGraphHandler agh,
			boolean directed,
			int k) {
		SortedLinkedList<CoefficientComparableContainer<Pair<NodeContainer, Path>, Float>> candidates =
			new SortedLinkedList<CoefficientComparableContainer<Pair<NodeContainer, Path>, Float>>();

		NodeContainer     actualNode = startNode;
		int               actualNodeIndex = startNode.getIndex();
		Path              actualPath = new Path();
		float             actualValue = 0;
		ArrayList<Path>  set = new ArrayList<Path>(k);

		//	boolean[] reached = new boolean[agh.getHighestNodeIndex() + 1];
		int endNodeIndex = endNode.getIndex();
		//	reached[startNode.getIndex()] = true;

		actualPath.add(startNode.getIndex());
		do {
			Collection<LinkContainer> col = null;
			if (directed) {
				col = actualNode.getOutgoingLinks(layerName);
			} else {
				col = actualNode.getConnectedLinks(layerName);
			}
			for (LinkContainer l : col) {
				Path p = (Path)actualPath.clone();
				NodeContainer nextNode = l.getOtherNodeContainer(actualNodeIndex);
				//	if (!reached[nextNode.getIndex()]) {
				if (!p.contains(nextNode.getIndex())) {
					p.add(nextNode.getIndex());
					if (nextNode.getIndex() == endNodeIndex) {
						set.add(p);
						if (set.size() == k) {
							return set;
						}
					}
					float score;
					if (attribName == null) {
						score = actualValue + 1;
					} else {
						if (l.attribute(attribName, false) == null) {
							throw new IllegalStateException("Cannot compute shortest path based on attribute " + attribName + " : it is missing");
						}
						score = actualValue + l.attribute(attribName).floatValue();
					}
					Pair<NodeContainer, Path> pair = new Pair<NodeContainer, Path>(nextNode, p);
					CoefficientComparableContainer<Pair<NodeContainer, Path>, Float> cont =
						new CoefficientComparableContainer<Pair<NodeContainer, Path>, Float>(pair, score);
					candidates.add(cont);
				}
			}
			CoefficientComparableContainer<Pair<NodeContainer, Path>, Float> cc = candidates.pollFirst();
			Pair<NodeContainer, Path> pp = cc.getContainedObject();
			actualNode = pp.getFirst();
			actualNodeIndex = actualNode.getIndex();
			//	reached[actualNodeIndex] = true;
			// removing alternatives
			actualPath = pp.getSecond();
			actualValue = cc.getCoefficient();
		}
		while ((candidates.size() > 0));
		return set;
	}

*/
}


