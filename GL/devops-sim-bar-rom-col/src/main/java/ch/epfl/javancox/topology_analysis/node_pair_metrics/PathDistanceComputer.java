package ch.epfl.javancox.topology_analysis.node_pair_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.path.JavancoShortestPath;
import ch.epfl.javanco.xml.XMLTagKeywords;


/**
 * This class computes the lengths of the paths based on the "length" attribute
 */

public class PathDistanceComputer extends GraphDistanceComputer {

	private String attributeForComputation;

	private boolean directed;
	
	public PathDistanceComputer() {
		this(false);	
	}

	/**
	 * THis default constructor setup the object to compute node betweeness
	 * based on the shortest paths, themselves based on the "length" attribute
	 */
	public PathDistanceComputer(boolean directed) {
		this(XMLTagKeywords.LENGTH.toString(), directed);
	}

	public PathDistanceComputer(String attributeToUse, boolean directed) {
		this.attributeForComputation = attributeToUse;
		this.directed = directed;
	}

	@Override
	protected double[][] computeAllDistances() {
		JavancoShortestPath sp = new JavancoShortestPath(
				rpi.getInput().getAgh(),
				rpi.getInput().getTopologyLayer(),
				attributeForComputation ,
				directed);
		sp.computeAll();
		return sp.getDistances();
	}
	
	@Override
	public String getResultName() {
		return "Distances of shortest paths (weight : link lengths)";
	}	

	@Override
	public void init() {}

	/**
	 * This method is placed here for conviniency. This method is not used by any of the other
	 * functions by this class. However, it eases the operation of defining lengths for each link
	 */
	public static void defineDistances(AbstractGraphHandler agh){
		for(int i=0; i<=agh.getHighestNodeIndex(); i++ ){
			for (int j=0; j<=agh.getHighestNodeIndex(); j++){
				for(LinkContainer lc : agh.getLinkContainers(i,j,false)){
					int orig_x = lc.getStartNodeContainer().attribute("pos_x").intValue();
					int orig_y = lc.getStartNodeContainer().attribute("pos_y").intValue();
					int dest_x = lc.getEndNodeContainer().attribute("pos_x").intValue();
					int dest_y = lc.getEndNodeContainer().attribute("pos_y").intValue();
					double length = java.awt.geom.Point2D.distance(orig_x, orig_y, dest_x, dest_y);
					length = Math.round(length);

					lc.attribute("length").setValue(length + "");
				}
			}
		}
	}

}
