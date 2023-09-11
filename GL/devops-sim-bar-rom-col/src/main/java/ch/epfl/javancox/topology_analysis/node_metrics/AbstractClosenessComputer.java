package ch.epfl.javancox.topology_analysis.node_metrics;



public abstract class AbstractClosenessComputer extends NodeCentralityComputer {
	
	protected boolean directed;
	
	public AbstractClosenessComputer(boolean directed) {
		this.directed = directed;
	}	
	
}
