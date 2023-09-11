package ch.epfl.javancox.inputs.topology.node_positionner;

import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public abstract class AbstractNodePositionner extends AbstractExperimentBlock {

	protected abstract int[][] getNodePosition(PRNStream stream, int nbNodes);

	private boolean moveNodesOnly = false;

	public void setMoveNodesOnly(boolean b) {
		moveNodesOnly = b;
	}

	public void placeNodes(AbstractGraphHandler agh, PRNStream stream, int numberOfNodes) {
		int[][] pos = getNodePosition(stream, numberOfNodes);
		if (moveNodesOnly) {
			int index = 0;
			for (NodeContainer nc : agh.getNodeContainers()) {
				nc.attribute(XMLTagKeywords.POS_X).setValue(pos[index][0]);
				nc.attribute(XMLTagKeywords.POS_Y).setValue(pos[index][1]);
				index++;
			}
		} else {
			int nbNodes = 0;
			while(nbNodes < numberOfNodes){
				agh.newNode(pos[nbNodes][0], pos[nbNodes][1]);
				nbNodes++;
			}
		}
	}



	public abstract Map<String, String> getPositionnerParameters();

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = getPositionnerParameters();
		map.put("node_linker", this.getClass().getSimpleName());
		return map;
	}
}
