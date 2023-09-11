package ch.epfl.javancox.inputs;

import java.util.Map;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javancox.inputs.compounds.NetworkExperimentInput;

public abstract class AbstractCapacityProvider extends AbstractProvider {


	private AbstractGraphHandler agh;
	private boolean createMLinks = false;
	
	public AbstractCapacityProvider() {
	}
	
	public AbstractCapacityProvider(boolean createMissingLinks) {
		createMLinks = createMissingLinks;
		
	}

	public void provideCapacity(NetworkExperimentInput input) {
		this.agh = input.getAgh();
		generateCapacity(input);
	}

	public abstract float getReferenceOrMeanCapacity();
	public abstract Double[][] generateCapacityImpl(NetworkExperimentInput input);
	public abstract Map<String, String> getCapacityProviderParameters();
	
	public Map<String, String> getAllParameters() {
		Map<String, String> m = getCapacityProviderParameters();
		m.put("capacity_provider", this.getClass().getSimpleName());
		return m;
	}	

	public double[][] getProvidedCapacities() {
		return getMatrix(agh, getCapacityLayerName(), getCapacityAttributeName());
	}	
	
	public void generateCapacity(NetworkExperimentInput input) {
		Double[][] rates = generateCapacityImpl(input);		
		ensureLayer(agh, getCapacityLayerName());
		LayerContainer layC = agh.getLayerContainer(getCapacityLayerName());
		for (int i = agh.getSmallestNodeIndex() ; i <= agh.getHighestNodeIndex() ; i++) {
			for (int j = agh.getSmallestNodeIndex(); j <= agh.getHighestNodeIndex() ; j++) {
				if (i == j) {
					continue;
				}
				if (agh.getNodeContainer(i) != null) {
					if (agh.getNodeContainer(j) != null) {
						LinkContainer lc = layC.getLinkContainer(i,j);
						if (lc != null || createMLinks) {
							if (lc == null) {
								lc = layC.newLink(i,j);
							}
							lc.attribute(getCapacityAttributeName()).setValue(rates[i][j]);
							lc.linkAttribute(getCapacityAttributeName());
						}
					}
				}
			}
		}	
	}	


	public static final String DEFAULT_CAPACITY_ATTRIBUTE_NAME = "cap";

	public static final String DEFAULT_CAPACITY_LAYER_NAME = "physical";

	/**
	 * Returns the DEFAULT_CAPACITY_ATTRIBUTE_NAME = physical if not overriden
	 */
	public String getCapacityAttributeName() {
		return DEFAULT_CAPACITY_ATTRIBUTE_NAME;
	}

	public String getCapacityLayerName() {
		return DEFAULT_CAPACITY_LAYER_NAME;
	}



}
