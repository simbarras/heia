package ch.epfl.javancox.topology_analysis2.analysers;

import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.topology_analysis2.AbstractGraphAnalyser;

public class DiameterAnalyser extends AbstractGraphAnalyser {

	@Override
	public void analyse(AbstractGraphHandler agh, AbstractResultsManager man,
			DataPoint dp, Execution e) {
		int diam = computeDegree(agh);
		if (diam < Integer.MAX_VALUE) {
		//	DataPoint dp2 = dp.getDerivedDataPoint();
			dp.addResultProperty("Diameter", diam);
		}
	}
	
	public int computeDegree(AbstractGraphHandler agh) {
		int nbNodes = agh.getNumberOfNodes();
		int diam = 0;
		for (int i = 0 ; i < nbNodes ; i++) {
			int[] distances = BFS.getDistancesFromUndirected(agh, i);
			diam = Math.max(diam, MoreArrays.max(distances));
		}
		return diam;
	}

}
