package ch.epfl.javancox.topology_analysis.node_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;

public class DegreeComputer extends NodeCentralityComputer {

	private int mode = 0;

	public static int UNDIRECTED = 0;
	public static int INCOMING = 1;
	public static int OUTGOING = 2;

	public DegreeComputer() {
		mode = 0;
	}
	
	public boolean theGreaterTheCentraler() {
		return true;
	}

	public DegreeComputer(int mode) {
		if (mode != 0 && mode != 2 && mode != 3) {
			throw new IllegalArgumentException("Use UNDIRECTED, INCOMING or OUTGONIG as paramter");
		}
		this.mode = mode;
	}
	
	@Override
	public String getResultName() {
		return "Node degrees";
	}	

	@Override
	public void init() {}

	@Override
	public double computeNodeCentrality(int nodeIndex) {
		AbstractGraphHandler agh = rpi.getInput().getAgh();

		int deg;
		if (mode == 0) {
			deg = agh.getNodeContainer(nodeIndex).getConnectedLinks(rpi.getInput().getTopologyLayer()).size();
		} else if (mode == 2) {
			deg = agh.getNodeContainer(nodeIndex).getOutgoingLinks(rpi.getInput().getTopologyLayer()).size();
		} else {
			deg = agh.getNodeContainer(nodeIndex).getIncomingLinks(rpi.getInput().getTopologyLayer()).size();

		}
		return deg;
	}

}
