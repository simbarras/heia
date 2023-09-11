package ch.epfl.javancox.topology_analysis.network_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.topology_analysis.MonoMetricComputer;

public class ClusteringCoefficientComputer extends NetworkWideMetricComputer {

	@Override
	protected double computeNetworkWideMetric() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		int[] tri = new int [agh.getHighestNodeIndex()+1];
		int[] lam = new int [agh.getHighestNodeIndex()+1];
		float sum = 0;
		float res = 0;

		if (!hasOneComponent(agh)){
			return 0;
		}

		for ( int i = 0 ; i < agh.getHighestNodeIndex()+1 ; i++ ){
			tri[i] = getTrianglesOfNode(agh, i);
			lam[i] = getLambdasOfNode(agh, i);

			if ((lam[i] > 0)&&(tri[i]  > 0)){

				sum += (float)tri[i]/(float)lam[i];
			}
		}

		res = sum / agh.getNodeContainers().size();
		//	System.out.println("" + res);
		return res;
	}


	private static int getLambdasOfNode (AbstractGraphHandler agh, int node){
		int var = MonoMetricComputer.getDegree(agh, node);

		int res = (var*var - var)/2;

		return res;
	}
	
	@Override
	public String getResultName() {
		return toString();
	}

	@Override
	public String toString() {
		return "Graph clustering coefficient";
	}
}
