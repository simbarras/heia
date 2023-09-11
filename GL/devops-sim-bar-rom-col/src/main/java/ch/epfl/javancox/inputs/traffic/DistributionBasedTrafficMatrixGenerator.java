package ch.epfl.javancox.inputs.traffic;

import java.util.Map;
import java.util.TreeMap;

import umontreal.ssj.probdist.Distribution;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.AbstractTrafficProvider;

public class DistributionBasedTrafficMatrixGenerator extends AbstractTrafficProvider {

	private Distribution dist;
	private PRNStream stream;
	private float normalisationFactor = 1;

	public DistributionBasedTrafficMatrixGenerator(Distribution dist) {
		this(dist, 1, 0);
	}
	
	public DistributionBasedTrafficMatrixGenerator(Distribution dist, PRNStream stream) {
		this(dist, 1, stream);
	}
	
	public DistributionBasedTrafficMatrixGenerator(Distribution dist, int seed) {
		this(dist, 1, seed);
	}
	
	public DistributionBasedTrafficMatrixGenerator(Distribution dist, float normalisationFactor) {
		this(dist, normalisationFactor, 0);
	}	
	
	public DistributionBasedTrafficMatrixGenerator(Distribution dist, float normalisationFactor, int seed) {	
		this(dist, normalisationFactor, PRNStream.getDefaultStream(seed));
	}
	
	public DistributionBasedTrafficMatrixGenerator(Distribution dist, float normalisationFactor, PRNStream stream) {	
		this.normalisationFactor = normalisationFactor;
		this.dist = dist;		
		this.stream = stream;		
	}
	
	public String getTrafficGeneratorName() {
		return "distribution_based";
	}

	@Override
	public Map<String, String> getProviderParameters() {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("traffic_generator_type","distribution_based_traffic");
		map.put("traffic_distribution", dist.toString());
		if (stream != null) {
			map.put("traffic_gen_stream", stream.getClass().getSimpleName());
			map.put("traffic_gen_seed", stream.getSeed()+"");
		}
		//	map.put("distribution_mean", dist.getMean()+"");
		//	map.put("distribution_variance", dist.getVariance()+"");
		return map;
	}

	@Override
	public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
		Double[][] rates = new Double[agh.getHighestNodeIndex()+1][agh.getHighestNodeIndex()+1];
		for (int i = 0 ; i <= agh.getHighestNodeIndex() ; i++) {
			for (int j = 0; j <= agh.getHighestNodeIndex() ; j++) {
				if (i != j){
					if (agh.getNodeContainer(i) != null) {
						if (agh.getNodeContainer(j) != null) {
							double r = stream.nextDouble();
							double var = dist.inverseF(r);
							int trials = 1000;
							while (var < 0 && trials > 0){
								r = stream.nextDouble();
								var = (int)dist.inverseF(r);
								trials--;
							}
							if (trials == 0) throw new IllegalStateException("Distribution seems to generate only negative numbers");
							rates[i][j] = var*normalisationFactor;
						}
					}
				}
			}
		}
		return rates;		
	}
}
