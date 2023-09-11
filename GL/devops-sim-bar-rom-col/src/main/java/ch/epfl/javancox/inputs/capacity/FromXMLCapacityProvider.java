package ch.epfl.javancox.inputs.capacity;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.math.StatFunctions;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javancox.inputs.AbstractCapacityProvider;
import ch.epfl.javancox.inputs.compounds.NetworkExperimentInput;

public class FromXMLCapacityProvider extends AbstractCapacityProvider {
	
	private String layerName;
	private String attributeName;
	
	private double meanVal;
	private float factor;

	public FromXMLCapacityProvider() {
		layerName = DEFAULT_CAPACITY_LAYER_NAME;
		attributeName = DEFAULT_CAPACITY_ATTRIBUTE_NAME;
	}
	
	public FromXMLCapacityProvider(boolean createMissingLinks) {
		super(createMissingLinks);
		layerName = DEFAULT_CAPACITY_LAYER_NAME;
		attributeName = DEFAULT_CAPACITY_ATTRIBUTE_NAME;		
	}
	
	public FromXMLCapacityProvider(@ParamName(name="Layer name") String layerName,
									@ParamName(name="Attribute name") String attributeName) {
		this.layerName = layerName;
		this.attributeName = attributeName;	
		this.factor = 1;							
	}
	
	public FromXMLCapacityProvider(@ParamName(name="Layer name") String layerName,
									@ParamName(name="Attribute name") String attributeName,
									float factor) {
		this.layerName = layerName;
		this.attributeName = attributeName;	
		this.factor = factor;							
	}	

	@Override
	public float getReferenceOrMeanCapacity() {
		return (float)meanVal;
	}
	
	public Map<String, String> getCapacityProviderParameters() {
		Map<String, String> m = getNewMap(2);
		m.put("Cap_layer", layerName);
		m.put("cap_att", attributeName);
		return m;
	}	
	
	@Override
	public Double[][] generateCapacityImpl(NetworkExperimentInput input) {
		AbstractGraphHandler agh = input.getAgh();
		LayerContainer layC = agh.getLayerContainer(layerName);
		Double[][] d = layC.getMatrixLinkAttributeDoubleD(attributeName);
	
		for (int i = 0 ; i < d.length ; i++) {
			for (int j = 0 ; j < d.length ; j++) {
				if (d[i][j] != null) {
					d[i][j] = d[i][j] * factor;
				}
			}
		}	
		meanVal = StatFunctions.getMean(d);
		return 	d;
	}
	
	public String getCapacityAttributeName() {
		return attributeName;
	}

	public String getCapacityLayerName() {
		return layerName;
	}	
	
}
