package ch.epfl.javanco.utils;

import java.util.Collection;

import ch.epfl.javanco.network.NodeContainer;

public class JavancoCollections {

	public static int maxIndex(Collection<NodeContainer> col) {
		int max = 0;
		for (NodeContainer nc : col) {
			if (nc.getIndex() > max) {
				max = nc.getIndex();
			}
		}
		return max;
	}
}
