package ch.epfl.javancox.topology_analysis.node_metrics;

import ch.epfl.general_libraries.path.ShortestPathAlgorithm;
import ch.epfl.javancox.topology_analysis.node_pair_metrics.AlgebraicalDistanceComputer;

public abstract class AbstractAlgebraicalClosenessComputer extends AbstractClosenessComputer {
	
	protected ShortestPathAlgorithm algPathSetResults;
	
	public AbstractAlgebraicalClosenessComputer(boolean directed) {
		super(directed);
	}
	
	@Override
	public void init() {	
		algPathSetResults = AlgebraicalDistanceComputer.getAlgebraicalShortestPathSet(rpi.getInput());
		
	}	
	
	
	
}
