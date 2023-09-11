package ch.epfl.javancox.inputs.traffic;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javancox.inputs.AbstractTrafficProvider;

public class FromXMLTrafficProvider extends AbstractTrafficProvider {
	
	private String layerName;
	private String attributeName;

	public FromXMLTrafficProvider() {
		layerName = DEFAULT_TRAFFIC_LAYER_NAME;
		attributeName = DEFAULT_TRAFFIC_ATTRIBUTE_NAME;
	}
	
	public FromXMLTrafficProvider(@ParamName(name="Layer name") String layerName,
									@ParamName(name="Attribute name") String attributeName) {
		this.layerName = layerName;
		this.attributeName = attributeName;								
	}

	public String getTrafficGeneratorName() {
		return "customized_from_file";
	}
	
	@Override
	public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
		//Double[][] d = new Double[agh.getHighestNodeIndex()+1][agh.getHighestNodeIndex()+1];
		
		
		LayerContainer layC = agh.getLayerContainer(layerName);
		
		
		return layC.getMatrixLinkAttributeDoubleD(attributeName);		
	}	

}
