package ch.epfl.javancox.inputs.topology.node_positionner;

import java.util.Map;

import javancox.layout.AbstractTopologyLayout;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class LayoutPositionner extends AbstractNodePositionner {
	
	private AbstractTopologyLayout layout;
	
	public LayoutPositionner(AbstractTopologyLayout layout) {
		this.layout = layout;
	}
	
	@Override
	public void placeNodes(AbstractGraphHandler agh, PRNStream stream, int numberOfNodes) {
		layout.assignNodesPosition(agh);
	}	
		
	@Override
	public Map<String, String> getPositionnerParameters() {
		Map<String, String> map = getNewMap(1);
		map.put("layout", layout.getClass().getSimpleName());
		return map;
	}
	
	@Override	
	protected int[][] getNodePosition(PRNStream stream, int nbNodes) {
		return null; // never used since placeNodes overriden
	}		
	
}
