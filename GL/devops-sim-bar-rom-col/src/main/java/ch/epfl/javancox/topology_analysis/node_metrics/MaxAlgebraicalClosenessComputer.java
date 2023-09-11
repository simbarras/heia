package ch.epfl.javancox.topology_analysis.node_metrics;


public class MaxAlgebraicalClosenessComputer extends AbstractAlgebraicalClosenessComputer {

	public MaxAlgebraicalClosenessComputer() {
		super(false);
	}
	
	@Override
	public String getResultName() {
		return "Max closeness of each node (in hops)";
	}
	
	public boolean theGreaterTheCentraler() {
		return false;
	}		
	
	@Override
	public double computeNodeCentrality(int nodeIndex) {
		double max= Double.NEGATIVE_INFINITY;
		for (int i = 0 ; i <= rpi.getInput().getAgh().getHighestNodeIndex() ; i++) {
			if (nodeIndex != i) {
				double dou = algPathSetResults.getDistance(nodeIndex, i);
				if (dou != Double.POSITIVE_INFINITY && dou != Double.NEGATIVE_INFINITY) {
					int d = (int)dou;
					if (d >= 0 && d > max) {
						max = d;
					}
				}
			}
		}	
		return max;
	}

}
