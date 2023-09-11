package ch.epfl.javancox.topology_analysis2.analysers;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.statistics.StatisticalDistribution;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.topology_analysis2.AbstractGraphAnalyser;

public class AlgebraicalDistanceAnalyser extends AbstractGraphAnalyser {
	
	private int[] fromOriginal;
	private boolean store = true;
	private boolean storeAll = false;
	
	
	
	public AlgebraicalDistanceAnalyser() {
		fromOriginal = null;
	}
	
	public AlgebraicalDistanceAnalyser(@ParamName(name="List of indexes to compute distance from") int ... from) {
		this.fromOriginal = from;
	}
	
	public AlgebraicalDistanceAnalyser(@ParamName(name="Store distances from each node?") boolean store) {
		this.store = store;
	}
	
	public AlgebraicalDistanceAnalyser(@ParamName(name="Store everything (enter a dummy double)")  double dummy) {
		this.storeAll = true;
	}

	@Override
	public void analyse(AbstractGraphHandler agh,
			AbstractResultsManager man, DataPoint orig, Execution e) {
		int highest = agh.getHighestNodeIndex();
		int zeros = (int)Math.max(Math.ceil(Math.log10(highest)),1);
		int[] from = fromOriginal;
		if (from == null) {
			from = MoreArrays.range(0, highest);
		}
		int diam = 0;
		double globalAccum = 0;
		int globalCounter = 0;
		for (int i = 0 ; i < from.length ; i++) {

			if (agh.getNodeContainer(from[i]) == null) continue;
			int[] distances = BFS.getDistancesFromUndirected(agh, from[i]);
			StatisticalDistribution<Integer> dist = new StatisticalDistribution<Integer>();
			for (int ii = 0 ; ii < distances.length ; ii++) {
				dist.add(distances[ii]);
			}
			Object[][] histogram = dist.getDistribution();
			int accum = MoreArrays.sum(distances);
			globalAccum += accum;
			globalCounter += distances.length - 1;
			int largest = MoreArrays.max(distances);
			diam = Math.max(diam, largest );
			if (store) {
				DataPoint dp = orig.getDerivedDataPoint();				
				if (storeAll) {
					for (int j = 0 ; j < distances.length ; j++) {
						DataPoint dp2 = dp.getDerivedDataPoint();
						dp2.addProperty("from - to", String.format("%0"+zeros+"d", from[i])  + "-" + String.format("%0"+zeros+"d", j) );
						dp2.addResultProperty("distance from-to", distances[j]);
						e.addDataPoint(dp2);
					}
					for (int j = 0 ; j < histogram[0].length ; j++) {
						DataPoint dp2 = dp.getDerivedDataPoint();
						dp2.addProperty("distance__", histogram[0][j]+"");
						dp2.addResultProperty("number of occurrences", histogram[1][j]+"");
						e.addDataPoint(dp2);
						
					}
				}
				dp.addProperty("from", String.format("%0"+zeros+"d", from[i]));
				dp.addResultProperty("Average distance (from i)", (double)accum / ((double)distances.length - 1));			
				dp.addResultProperty("Largest (from i)", largest);
				e.addDataPoint(dp);

			}
		}
		
		DataPoint glo = orig.getDerivedDataPoint();	
		glo.addResultProperty("Global diam", diam);
		glo.addResultProperty("Average distance", globalAccum/(double)globalCounter);
		e.addDataPoint(glo);
	}

}
