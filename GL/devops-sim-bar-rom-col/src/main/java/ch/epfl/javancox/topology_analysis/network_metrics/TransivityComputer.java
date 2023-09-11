package ch.epfl.javancox.topology_analysis.network_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.NodeContainer;

public class TransivityComputer extends NetworkWideMetricComputer {

	@Override
	protected double computeNetworkWideMetric(){
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		double res = 0;

		int tri = getNumberOfTriangles(agh);

		double lam = getNumberOfLambdas(agh, rpi.getInput().getTopologyLayer());

		if (!hasOneComponent(agh)){
			return 0;
		}

		res = (3 * (double)tri)/lam ;

		return res;
	}

	private static int getNumberOfTriangles (AbstractGraphHandler agh){

		int res = 0;

		for (NodeContainer nc : agh.getNodeContainers()){
			res += getTrianglesOfNode(agh, nc.getIndex());
		}

		res = res/6 ;

		return res;
	}

	private static double getNumberOfLambdas (AbstractGraphHandler agh, LayerContainer lc){
		double lambdas = 0;
		//	int[] degrees = getDegrees(agh);
		double parcial;
		double var;
		for ( int i = 0 ; i <= agh.getHighestNodeIndex() ; i++ ){
			NodeContainer nc = agh.getNodeContainer(i);
			if (nc == null) continue;
			
			var = nc.getConnectedLinks(lc).size();
			parcial = 0;
			for ( int k = 1 ; k < var ; k++ ){
				parcial += var-k;
			}
			lambdas += parcial;
		}
		return lambdas;
	}

	@Override
	public String getResultName() {
		return "Graph transitivity";
	}

}
