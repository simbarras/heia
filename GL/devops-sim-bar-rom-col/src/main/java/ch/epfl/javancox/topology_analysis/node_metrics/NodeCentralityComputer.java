package ch.epfl.javancox.topology_analysis.node_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.topology_analysis.CentralityComputer;
import ch.epfl.javancox.topology_analysis.MultiMetricComputer;


public abstract class NodeCentralityComputer extends MultiMetricComputer implements CentralityComputer {

	private NodeContainer centralNode = null;

	@Override
	protected double[] computeForAllElements() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		double[] tab = new double[agh.getHighestNodeIndex()+1];
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		NodeContainer minNode = null;
		NodeContainer maxNode = null;
		for (int i = 0 ; i < agh.getHighestNodeIndex()+1; i++) {
			NodeContainer nc = agh.getNodeContainer(i);
			if (nc != null) {
				tab[i] = computeNodeCentrality(i);
				if (tab[i] > max) {
					max = tab[i];
					maxNode = nc;
				}
				if (tab[i] < min) {
					min = tab[i];
					minNode = nc;
				}
			}
		}
		if (theGreaterTheCentraler()) {
			centralNode = maxNode;
		} else {
			centralNode = minNode;
		}		

		return tab;
	}
	
	public String getCentralElement() {
		return centralNode.toSimpleString();
	}

	protected abstract double computeNodeCentrality(int nodeIndex);


}

