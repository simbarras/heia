package ch.epfl.javancox.topology_analysis.node_metrics;


public class MeanAlgebraicalClosenessComputer extends AbstractAlgebraicalClosenessComputer {
	
	public MeanAlgebraicalClosenessComputer() {
		super(false);
	}
	
	@Override
	public String getResultName() {
		return "Mean closeness of each node (in hops)";
	}
	
	public boolean theGreaterTheCentraler() {
		return false;
	}		
	
	@Override
	public double computeNodeCentrality(int nodeIndex) {
		int sum = 0;
		int occ = 0;
		for (int i = 0 ; i <= rpi.getInput().getAgh().getHighestNodeIndex() ; i++) {
			if (nodeIndex != i) {
				double dou = algPathSetResults.getDistance(nodeIndex, i);
				if (dou != Double.POSITIVE_INFINITY && dou != Double.NEGATIVE_INFINITY) {
					int d = (int)dou;
					if (d >= 0) {
						sum += d;
						occ++;
					}
				}
			}
		}
		if (occ > 0) {
			return (double)sum/(double)occ;
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

	
}
