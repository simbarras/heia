package ch.epfl.javanco.tree;

import ch.epfl.javanco.base.AbstractGraphHandler;



public abstract class AbstractSpanningTreeFinder {

	public abstract int createSpanningTree(AbstractGraphHandler agh, String topologyLayer, String destinationLayer);
}
