package ch.epfl.javancox.inputs.topology.node_positionner;

import java.util.Map;

import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.UniformIntDist;
import ch.epfl.general_libraries.random.PRNStream;

public class DistributionBasedNodePositionner extends AbstractNodePositionner {

	private Distribution xDist;
	private Distribution yDist;

	public DistributionBasedNodePositionner(int area) {
		xDist = new UniformIntDist(0, area);
		yDist = xDist;
	}

	public DistributionBasedNodePositionner(Distribution dist) {
		xDist = dist;
		yDist = dist;
	}

	public DistributionBasedNodePositionner(Distribution xDist, Distribution yDist) {
		this.xDist = xDist;
		this.yDist = yDist;
	}

	@Override
	protected int[][] getNodePosition(PRNStream stream, int nb) {
		int[][] vals = new int[nb][2];
		for (int i = 0 ; i < nb ; i++) {
			vals[i][0] = (int)xDist.inverseF(stream.nextDouble());
			vals[i][1] = (int)yDist.inverseF(stream.nextDouble());
		}
		return vals;
	}

	@Override
	public Map<String, String> getPositionnerParameters() {
		Map<String, String> map = getNewMap();
		if (xDist == yDist) {
			map.put("positionner_distribution", xDist.toString());
		} else {
			map.put("positionner_distribution_x", xDist.toString());
			map.put("positionner_distribution_y", yDist.toString());
		}
		return map;
	}


}
