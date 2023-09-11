package ch.epfl.javancox.inputs.capacity;

import java.util.Map;
import java.util.TreeMap;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.AbstractCapacityProvider;
import ch.epfl.javancox.inputs.compounds.NetworkExperimentInput;


public class UniformCapacityGenerator extends AbstractCapacityProvider {

	private int capacity;

	public UniformCapacityGenerator(int capacity) {
		this.capacity = capacity;
	}	
	
	public UniformCapacityGenerator(int capacity, boolean addMissingLinks) {
		super(addMissingLinks);
		this.capacity = capacity;
	}

	@Override
	public Map<String, String> getCapacityProviderParameters() {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("capacity",capacity+"");
		return map;
	}

	@Override
	public float getReferenceOrMeanCapacity() {
		return capacity;
	}

	@Override
	public Double[][] generateCapacityImpl(NetworkExperimentInput input) {
		AbstractGraphHandler agh = input.getAgh();
		Double[][] d = new Double[agh.getHighestNodeIndex()+1][agh.getHighestNodeIndex()+1];
		for (int i = 0; i < d.length ; i++) {
			for (int j = 0 ; j < d.length ; j++) {
				d[i][j] = (double)capacity;
			}
		}
		return d;		
	}

//	@Override
	// TODO place capacity only where link exists
/*	public void createCapacity(AbstractGraphHandler agh, PRNStream stream) {
		for (LinkContainer lc : agh.getLayerContainer(getCapacityLayerName()).getLinkContainers()) {
			lc.attribute(getCapacityAttributeName()).setValue(capacity);
			lc.linkAttribute(getCapacityAttributeName());
		}
	}*/



}
