package ch.epfl.javanco.path;

import java.util.Collection;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.ShortestPathAlgorithm;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.NetworkAttribute;

public class JavancoShortestPath extends ShortestPathAlgorithm {

	public JavancoShortestPath(Collection<NodeContainer> nodes,
			Collection<LinkContainer> links,
			String attributeName,
			boolean directed) {
		initializeGen(directed);
		initializeNodes(nodes);
		initializeLinks(links, attributeName);
	}

	public JavancoShortestPath(Collection<NodeContainer> nodes,
			Collection<LinkContainer> links) {
		this(nodes, links, "length", false);
	}
	
	public JavancoShortestPath(LayerContainer lc, String att, boolean directed) {
		this(lc.getAbstractGraphHandler().getNodeContainers(), lc.getLinkContainers(), att, directed);
	}

	public JavancoShortestPath(AbstractGraphHandler agh,
			String layerName) {
		this(agh, layerName, "length", false);
	}
	public JavancoShortestPath(AbstractGraphHandler agh,
			String layerName,
			String attributeName,
			boolean directed) {
		this(agh, agh.getLayerContainer(layerName), attributeName, directed);
	}

	/**
	 * Construct a JavancoShortestPath object. The shortest path will be
	 * computed on the layer given in parameter. If layerName is <code>
	 * null</code>, method will try to access the layer "physical". If no
	 * such layer is founded, a <code>NullPointerException</code> is thrown.
	 */
	public JavancoShortestPath(AbstractGraphHandler agh, LayerContainer lc, String attN, boolean dir) {
		if (lc == null) {
			throw new NullPointerException("Cannot apply shortest path, null layer ");
		}
		initializeGen(directed);
		initializeNodes(agh.getNodeContainers());
		initializeLinks(lc.getLinkContainers(), attN);
	}

	public static Path getPath(int startNode,
			int destNode,
			Collection<NodeContainer> nodes,
			Collection<LinkContainer> links,
			String attribName,
			boolean directed) {
		JavancoShortestPath jsp = new JavancoShortestPath(nodes, links, attribName, directed);
		return jsp.getPath(startNode, destNode);
	}


	private void initializeNodes(Collection<NodeContainer> nodes) {
		// compute maximal node index
		int max = -1;
		if (nodes != null) {
			for (NodeContainer c : nodes) {
				if (c.getIndex() > maxNodeIndex) {
					max = c.getIndex();
				}
			}
		}
		super.initialise(max +1);
	}

	private void initializeGen(boolean directed) {
		super.directed = directed;		
	}

	private void initializeLinks(Collection<LinkContainer> links,
			String attribute) {
		// completion of the incidence matrix
		for (LinkContainer lc : links) {
			if (lc != null) {
				int start = lc.getStartNodeIndex();
				int end = lc.getEndNodeIndex();
				if (attribute == null) {
					attribute = "length";
				}
				float length;
				if (lc.attribute(attribute, false) == null) {
					length = 1;
				} else {
					NetworkAttribute nt = lc.attribute(attribute);
					if (nt.getValue().equals("")) {
						length = 1;
					} else {
						length = nt.floatValue();
					}
				}

				super.incidenceMatrix[start][end] = length;
				if (!directed) {
					super.incidenceMatrix[end][start] = length;
				}
			}
		}
	}
}