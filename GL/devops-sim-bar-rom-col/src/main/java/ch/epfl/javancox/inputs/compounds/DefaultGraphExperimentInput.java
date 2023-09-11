package ch.epfl.javancox.inputs.compounds;

import java.io.Serializable;
import java.util.Map;

import ch.epfl.javanco.base.AbstractGraphHandler;

public class DefaultGraphExperimentInput extends AbstractGraphExperimentInput implements Serializable {

	private static final long serialVersionUID = 1L;

	public DefaultGraphExperimentInput(AbstractGraphHandler localAgh, String phyLayerName, String lengthAttName) {
		this.phyLayerName = phyLayerName;
		this.lengthAttName = lengthAttName;
		this.localAgh = localAgh;
	}
	
	String phyLayerName;
	String lengthAttName;
	AbstractGraphHandler localAgh;
	
	public void createImpl(AbstractGraphHandler agh) {}
	public String getPhysicalLayerName() {
		return phyLayerName;
	}			
	public String getLengthAttributeName() {
		return lengthAttName;	
	}
	public AbstractGraphHandler getAgh() {
		return localAgh;
	}
	public Map<String, String> getAllParameters() {
		return getNewMap();
	}		
}