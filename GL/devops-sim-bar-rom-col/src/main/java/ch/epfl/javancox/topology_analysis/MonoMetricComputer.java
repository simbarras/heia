package ch.epfl.javancox.topology_analysis;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public abstract class MonoMetricComputer extends AbstractTopologyAnalyser {

//	private boolean computed = false;


	public abstract double computeMetric();

	@MethodDef	
	public double computeMainResult() {
		return computeMetric();
	}

	public static int getDegree(AbstractGraphHandler agh, int nodeIndex) {
		return agh.getNodeContainer(nodeIndex).getAllConnectedLinks().size();
	}

	public static int[] getDegrees (AbstractGraphHandler agh, String layerName){
		int[] degree = new int[agh.getHighestNodeIndex()+1];
		for (NodeContainer nc : agh.getNodeContainers()) {
			degree[nc.getIndex()] = nc.getConnectedLinks(layerName).size();
		}
		return degree;
	}

	public String getResultName() {
		return this.getClass().getSimpleName();
	}	
	
}
