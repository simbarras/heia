package ch.epfl.javanco.tree;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.general_libraries.utils.CoefficientComparableContainer;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.utils.NodeDegreeComparator;

public class DegreeBasedSpanningTreeFinder extends AbstractSpanningTreeFinder {


	@Override
	public int createSpanningTree(AbstractGraphHandler agh,
			String topologyLayer,
			String destinationLayer) {
		if (topologyLayer == null) {
			topologyLayer = "physical";
		}

		agh.newLayer(destinationLayer);
		Set<Integer> nodesInTree = new TreeSet<Integer>();

		CoefficientComparableContainer<LinkContainer, NodeContainer> required
		= new CoefficientComparableContainer<LinkContainer, NodeContainer>(null, null);

		TreeSet<CoefficientComparableContainer<LinkContainer, NodeContainer>> candidates
		= new TreeSet<CoefficientComparableContainer<LinkContainer, NodeContainer>>(
				required.getParticularComparator(new NodeDegreeComparator()));

		LayerContainer top = agh.getLayerContainer(topologyLayer);
		if (top.containsConvexGraph() == false) {
			throw new IllegalStateException("Cannot build a spanning tree on a non-convex graph");
		}

		NodeContainer start = findHighestDegree(agh.getNodeContainers());

		nodesInTree.add(start.getIndex());

		for (LinkContainer lc : start.getConnectedLinks()) {
			CoefficientComparableContainer<LinkContainer, NodeContainer> co
			= new CoefficientComparableContainer<LinkContainer, NodeContainer>(lc, lc.getOtherNodeContainer(start.getIndex()));
			candidates.add(co);
		}

		while (nodesInTree.size() < agh.getNumberOfNodes()) {
			CoefficientComparableContainer<LinkContainer, NodeContainer> nc = candidates.pollLast();
			NodeContainer toAdd = nc.getCoefficient();
			nodesInTree.add(toAdd.getIndex());
			LinkContainer lc = nc.getContainedObject();
			agh.newLink(lc.getStartNodeIndex(), lc.getEndNodeIndex());
			for (LinkContainer lic : toAdd.getConnectedLinks()) {
				NodeContainer otherNode = lic.getOtherNodeContainer(toAdd.getIndex());
				if (nodesInTree.contains(otherNode.getIndex()) == false) {
					CoefficientComparableContainer<LinkContainer, NodeContainer> co
					= new CoefficientComparableContainer<LinkContainer, NodeContainer>(lic, otherNode);
					candidates.add(co);
				}
			}
		}

		return start.getIndex();
	}

	private NodeContainer findHighestDegree(Collection<NodeContainer> list) {
		int max_degree = 0;
		NodeContainer hi = null;
		for (NodeContainer nc : list) {
			int deg = nc.getConnectedLinks().size();
			if (deg > max_degree) {
				hi = nc;
				max_degree = deg;
			}
		}
		return hi;
	}
}
