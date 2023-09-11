package ch.epfl.javancox.topology_analysis.node_pair_metrics;


import ch.epfl.general_libraries.path.ShortestPathAlgorithm;
import ch.epfl.javancox.inputs.compounds.AbstractGraphExperimentInput;

/**
 * This class computes shortest paths based on the "hops" attributes, which
 * should be also equal to 1. This gives the "hopDistance"
 */

public class AlgebraicalDistanceComputer extends GraphDistanceComputer {

	@Override
	public void init() {}

	@Override
	protected double[][] computeAllDistances() {
		return getAlgebraicalShortestPathSet(rpi.getInput()).getDistances();
	}
	
	@Override
	public String getResultName() {
		return "Minimum number of hops between each node pairs";
	}	
		
	public static ShortestPathAlgorithm getAlgebraicalShortestPathSet(AbstractGraphExperimentInput input) {
		ShortestPathAlgorithm sp = null;
		sp = new ShortestPathAlgorithm(input.getTopologyLayer().getUndirectedIncidenceMatrixDouble());
		sp.computeAll();
		
		return sp;	
	}


}
