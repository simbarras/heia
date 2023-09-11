package ch.epfl.javancox.topology_analysis.node_pair_metrics;

import java.awt.geom.Point2D;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * This class computes the direct distance separating two nodes, without
 * taking into account the links
 */

public class GeodesicalDistanceComputer extends GraphDistanceComputer {

	@Override
	public void init() {}

	@Override
	protected double[][] computeAllDistances() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		int max = agh.getHighestNodeIndex();
		double[][] distances = new double[max+1][max+1];
		int[][] positions = new int[2][max+1];
		for (int i = 0 ; i <= max ; i++) {
			NodeContainer nc = agh.getNodeContainer(i);
			positions[0][i] = nc.attribute(XMLTagKeywords.POS_X).intValue();
			positions[1][i] = nc.attribute(XMLTagKeywords.POS_Y).intValue();
		}
		for (int i = 0 ; i < max ; i++) {
			for (int j = i+1 ; j <= max ; j++) {
				distances[i][j] = Point2D.distance(positions[0][i], positions[1][i], positions[0][j], positions[1][j]);
			}
		}
		return distances;
	}
	
	@Override
	public String getResultName() {
		return "Bird eye distances";
	}	

}
