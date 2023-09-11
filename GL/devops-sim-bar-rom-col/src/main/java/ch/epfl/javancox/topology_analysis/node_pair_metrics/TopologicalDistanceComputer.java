package ch.epfl.javancox.topology_analysis.node_pair_metrics;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.path.JavancoShortestPath;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * This class computes shortest paths based on the "length" attribute.
 * On this basis, it counts the paths lengths in terms of hops
 */


public class TopologicalDistanceComputer extends GraphDistanceComputer{

	private String attributeForComputation;

	private boolean directed;

	/**
	 * THis default constructor setup the object to compute node betweeness
	 * based on the shortest paths, themselves based on the "length" attribute
	 */
	public TopologicalDistanceComputer() {	 
		this(false);
	}
	
	@Override
	public String getResultName() {
		return "Hops of shortest paths (weight : link lengths)";
	}	
	 
	public TopologicalDistanceComputer(boolean directed) {
		this(XMLTagKeywords.LENGTH.toString(), directed);
	}

	public TopologicalDistanceComputer(String attributeToUse, boolean directed) {
		this.attributeForComputation = attributeToUse;
		this.directed = directed;
	}

	@Override
	public void init() {}

	@Override
	public double[][] computeAllDistances() {

		AbstractGraphHandler agh = rpi.getInput().getAgh();

		JavancoShortestPath sp = new JavancoShortestPath(
				rpi.getInput().getAgh(),
				rpi.getInput().getTopologyLayer(),
				attributeForComputation ,
				directed);
		sp.computeAll();

		/* DEBUG
		 */
		//	JavancoShortestPath sp2 = new JavancoShortestPath(agh, getLayerName(agh), "hops" , false);
		//	sp2.computeAll();

		int max = agh.getHighestNodeIndex();
		double[][] distances = new double[max+1][max+1];

		for (int i = 0 ; i < max ; i++) {
			for (int j = i+1 ; j <= max ; j++) {
				Path p = sp.getPath(i,j);
				if (p != null) {
					distances[i][j] = p.getNumberOfHops();
				} else {
					distances[i][j] = -1;
				}
			}
		}

		return distances;
	}
}
