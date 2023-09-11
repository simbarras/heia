package ch.epfl.javancox.topology_analysis.node_pair_metrics;

import java.util.Arrays;

import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.general_libraries.utils.PairList;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.topology_analysis.MultiMetricComputer;

public abstract class GraphDistanceComputer extends MultiMetricComputer {

	protected abstract double[][] computeAllDistances();
	
	public PairList<Double, NodePair> computeDistancesPerNodePair() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		PairList<Double, NodePair> pairList = new PairList<Double, NodePair>();
		double[][] distances = computeAllDistances();

		for (int i = 0; i < agh.getHighestNodeIndex(); i++){
			for (int j = i+1; j <= agh.getHighestNodeIndex(); j++){
				double comp = distances[i][j];
				if (comp < 0 || comp == Double.POSITIVE_INFINITY || comp==Double.NEGATIVE_INFINITY) {
				} else {
					pairList.add(comp, new NodePair(i, j));
				}
			}
		}
		return pairList;
	}

	@Override
	protected double[] computeForAllElements() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		int var = agh.getHighestNodeIndex()+1;
		// on peut calculer le nombre de couples possible avec la formule de Gauss
		int res = (var*(var-1))/2;
		int pos = 0;
		int skipped = 0;

		double [] comp = new double[res];

		double[][] distances = computeAllDistances();

		for (int i = 0; i < agh.getHighestNodeIndex(); i++){
			for (int j = i+1; j <= agh.getHighestNodeIndex(); j++){
				comp[pos] = distances[i][j];
				if (comp[pos] < 0 || comp[pos] == Double.POSITIVE_INFINITY || comp[pos]==Double.NEGATIVE_INFINITY) {
					skipped++;
				} else {
					pos++;
				}
			}
		}
		comp = Arrays.copyOfRange(comp, 0, comp.length - skipped);
		return comp;
	}
}
