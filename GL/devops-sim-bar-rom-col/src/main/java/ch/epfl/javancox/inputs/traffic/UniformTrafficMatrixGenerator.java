package ch.epfl.javancox.inputs.traffic;

import java.util.Map;
import java.util.TreeMap;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.AbstractTrafficProvider;

public class UniformTrafficMatrixGenerator extends AbstractTrafficProvider {

	private float intensity;


	public UniformTrafficMatrixGenerator(float intensity) {
		this.intensity = intensity;
	}
	
	public UniformTrafficMatrixGenerator() {
		this.intensity = 1;
	}
	
	public String getTrafficGeneratorName() {
		return "uniform";
	}	

	@Override
	public Map<String, String> getProviderParameters() {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("traffic_generator_type","uniform_traffic");
		map.put("traffic_intensity",intensity+"");
		return map;
	}


	@Override
	public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
		Double[][] d = new Double[agh.getHighestNodeIndex()+1][agh.getHighestNodeIndex()+1];
		for (int i = 0; i < d.length ; i++) {
			for (int j = 0 ; j < d.length ; j++) {
				d[i][j] = (double)intensity;
			}
		}
		return d;		
	}

}
