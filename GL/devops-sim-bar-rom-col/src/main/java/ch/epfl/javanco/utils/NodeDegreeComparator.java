package ch.epfl.javanco.utils;

import java.util.Comparator;

import ch.epfl.javanco.network.NodeContainer;

public class NodeDegreeComparator implements Comparator<NodeContainer> {

	/*	public int compare(Object o1, Object o2) {
		return compare((NodeContainer)o1, (NodeContainer)o2);
	}*/

	public int compare(NodeContainer nc1, NodeContainer nc2) {
		int degreeDifference = nc1.getConnectedLinks().size() - nc2.getConnectedLinks().size();
		if (degreeDifference != 0) {
			return degreeDifference;
		}
		return nc1.getIndex() - nc2.getIndex();
	}
}
