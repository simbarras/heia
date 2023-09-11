package ch.epfl.javanco.tree;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;

public class AttributeBasedSpanningTreeFinder extends AbstractSpanningTreeFinder {

	private String attribute;

	public AttributeBasedSpanningTreeFinder(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public int createSpanningTree(AbstractGraphHandler agh,
			String topologyLayer,
			String destinationLayer) {
		if (topologyLayer == null) {
			topologyLayer = "physical";
		}

		agh.newLayer(destinationLayer);

		LayerContainer phy = agh.getLayerContainer(topologyLayer);
		LayerContainer tree = agh.getLayerContainer(destinationLayer);

		for (LinkContainer lc : phy.getLinkContainers()) {
			if (lc.attribute(attribute, false) != null) {
				tree.newLink(lc.getStartNodeIndex(), lc.getEndNodeIndex());
			}
		}
		return agh.getSmallestNodeIndex();

	}



}
