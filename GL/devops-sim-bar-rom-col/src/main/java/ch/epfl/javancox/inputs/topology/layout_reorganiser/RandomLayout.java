package ch.epfl.javancox.inputs.topology.layout_reorganiser;

import java.util.Map;

import javancox.layout.AbstractTopologyLayout;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;


public class RandomLayout extends AbstractTopologyLayout {


	public RandomLayout() {
	}

	@Override
	public Map<String, String> getLayoutParameters() {
		return getEmptyMap();
	}

	@Override
	public void assignNodesPosition(int max_screen_x, int max_screen_y, AbstractGraphHandler agh){
		int nbNodes = agh.getHighestNodeIndex()+1;

		int rand_x, rand_y;
		for(int m = 0; m < nbNodes; m++){
			NodeContainer node = agh.getNodeContainer(m);
			rand_x = (int)(Math.random()* max_screen_x);
			rand_y = (int) (Math.random()* max_screen_y);
			node.attribute("pos_x").setValue(rand_x);
			node.attribute("pos_y").setValue(rand_y);
		}
	}
}
