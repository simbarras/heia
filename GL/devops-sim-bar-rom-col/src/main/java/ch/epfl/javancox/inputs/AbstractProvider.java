package ch.epfl.javancox.inputs;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;

public abstract class AbstractProvider extends AbstractExperimentBlock {

	public double[][] getMatrix(AbstractGraphHandler agh, String layerName, String attName) {
		LayerContainer layC = agh.getLayerContainer(layerName);
		return layC.getMatrixLinkAttributeD(attName);
	}

	protected void ensureLayer(AbstractGraphHandler agh, String layerName) {
		if (agh.getLayerContainer(layerName) == null) {
			agh.newLayer(layerName);
		} else {
			agh.setEditedLayer(agh.getLayerContainer(layerName));
		}
	}



}
