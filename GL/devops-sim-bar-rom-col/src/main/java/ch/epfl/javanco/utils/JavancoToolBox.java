package ch.epfl.javanco.utils;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.GraphHandlerFactory;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;


public final class JavancoToolBox {

	public static AbstractGraphHandler copyLayer(AbstractGraphHandler agh, String layerName) {
		GraphHandlerFactory fac = new GraphHandlerFactory(agh.getClass());
		AbstractGraphHandler graphI = fac.getNewGraphHandler();
		try { graphI.newLayer(layerName); } catch (Exception e) {}
		for (int j = 0 ; j < agh.getHighestNodeIndex() +1 ; j++) {
			graphI.newNode();
		}

		LayerContainer lay = agh.getLayerContainer(layerName);
		for (LinkContainer lc :  lay.getLinkContainers()) {
			graphI.newLink(lc.getStartNodeIndex(), lc.getEndNodeIndex());
		}
		return graphI;
	}

	public static void copyLayer(AbstractGraphHandler src, AbstractGraphHandler dest, String layername) {
		if (dest.getLayerContainer(layername) == null) {
			dest.newLayer(layername);
		}
		dest.setEditedLayer(layername);
		for (NodeContainer nc : src.getNodeContainers()) {
			dest.newNode(nc.attribute(XMLTagKeywords.POS_X).intValue(), nc.attribute(XMLTagKeywords.POS_Y).intValue());
		}

		LayerContainer lay = src.getLayerContainer(layername);
		for (LinkContainer lc :  lay.getLinkContainers()) {
			dest.newLink(lc.getStartNodeIndex(), lc.getEndNodeIndex());
		}
	}

	public static void copyAgh(AbstractGraphHandler orig, AbstractGraphHandler copy) {
		copyAgh(orig, copy, "copy");
	}

	public static void copyAgh(AbstractGraphHandler orig, AbstractGraphHandler copy, String networkName) {
		if (copy.isCreated() == false) {
			copy.newNetwork(networkName);
		}
		for (LayerContainer lc : orig.getLayerContainers()) {
			copy.newLayer(lc.getName());
			for (NodeContainer node : lc.getNodeContainers()) {
				NodeContainer newC = new NodeContainer();
				newC.setIndex(node.getIndex());
				copy.newNode(newC);
			}
		}
		for (LayerContainer lc : orig.getLayerContainers()) {
			copy.setEditedLayer(lc.getName());
			for (LinkContainer phyLink : lc.getLinkContainers()) {
				LinkContainer linkCopy = copy.newLink(phyLink.getStartNodeIndex(), phyLink.getEndNodeIndex());
				// CONSIDER ADD FUNCTIONALITY TO COPY ALL ATTRIBUTES IN ABSTRACTELEMENTCONTAINER
				for (NetworkAttribute att : phyLink.attributes()) {
					linkCopy.attribute(att.getName()).setValue(att.getValue());
				}
			}
		}
	}




}



