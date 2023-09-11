package ch.epfl.javancox.topology_analysis.node_pair_metrics;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.ShortestPathAlgorithm;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.algorithms.Dijkstra;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javancox.topology_analysis.MonoMetricComputer;

public class SinglePairDistanceComputer extends MonoMetricComputer {

	public SinglePairDistanceComputer() {
	}

	@Override
	public double computeMetric() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		if (agh.getNodeContainers().size() == 0) return 0;
		double bfs = bfs(agh);
		return bfs;
	}
	
	public double shortestPathAlg(AbstractGraphHandler agh) {
		boolean[][] in = agh.getEditedLayer().getIncidenceMatrix();
		Matrix.symmetrise(in);
		ShortestPathAlgorithm sp = new ShortestPathAlgorithm(in);			
		int hops = 0;	
		for (int i = 1 ; i <= agh.getHighestNodeIndex() ; i++) {
			hops += sp.getPath(0, i).getNumberOfHops();
		}
	//	System.out.println("Temps pour SP " + t1/1000);			
		return (double)hops / (double)agh.getHighestNodeIndex();		
	}
	
	public double bfs(AbstractGraphHandler agh) {
		long t1 = System.nanoTime();
		Path[] paths = BFS.getShortestPathsFromUndirected(agh, 0);
		t1 = System.nanoTime() - t1;
	//	System.out.println("Temps pour BFS " + t1/1000);			
		int accum = 0;
		for (int i = 1 ; i < paths.length ; i++) {
			accum += paths[i].getNumberOfHops();
		}
		return (double)accum/((double)paths.length-1);
	}
	
	public double dijkstra(AbstractGraphHandler agh) {
		for (LinkContainer lc : agh.getLinkContainers()) {
			lc.attribute("length").setValue("1");
		}
		long t1 = System.nanoTime();
		Path[] paths = Dijkstra.getShortestPaths(agh, 0, "length", false);
		t1 = System.nanoTime() - t1;
		System.out.println("Temps pour Dijkstra " + t1/1000);		
		int accum = 0;
		for (int i = 1 ; i < paths.length ; i++) {
			accum += paths[i].getNumberOfHops();
		}

		return (double)accum/((double)paths.length-1);		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
