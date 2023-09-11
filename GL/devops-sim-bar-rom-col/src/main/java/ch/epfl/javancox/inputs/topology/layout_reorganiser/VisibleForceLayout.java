package ch.epfl.javancox.inputs.topology.layout_reorganiser;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class VisibleForceLayout extends ForceLayout {

	protected void animate(AbstractGraphHandler agh, double[] posx, double[] posy, int nbNodes) {
		for (int j = 0 ; j < nbNodes ; j++) {	
			NodeContainer node = agh.getNodeContainer(j);	
			node.attribute("pos_x").setValue(posx[j]);
			node.attribute("pos_y").setValue(posy[j]);	
		}		
		agh.fireAllElementsModificationEvent(new CasualEvent(this));	
	}
}
