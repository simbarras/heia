package ch.epfl.javancox.inputs.topology.node_positionner;

import java.util.Map;

import ch.epfl.general_libraries.random.PRNStream;

public class OrbitalPositionner  extends AbstractNodePositionner {

	public OrbitalPositionner() {
	}

	@Override
	protected int[][] getNodePosition(PRNStream stream, int nb) {
		int[][] vals = new int[nb][2];
		double circlePart = 2*Math.PI/nb;
		for (int i=0;i<nb;i++){
			vals[i][0] = (int)(350 + 350 * Math.sin(i*circlePart));
			vals[i][1] = (int)(350 + 350 * Math.cos(i*circlePart));
		}
		return vals;
	}

	@Override
	public Map<String, String> getPositionnerParameters() {
		Map<String, String> map = getNewMap();
		return map;
	}
}
