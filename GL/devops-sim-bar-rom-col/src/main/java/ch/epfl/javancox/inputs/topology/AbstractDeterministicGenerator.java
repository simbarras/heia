package ch.epfl.javancox.inputs.topology;

import java.util.ArrayList;
import java.util.List;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public abstract class AbstractDeterministicGenerator extends AbstractTopologyGenerator {
	
	protected boolean vertical = true;

	@Override
	public abstract void generate(AbstractGraphHandler agh);
	
	public String getFullName() {
		return getName();
	}
	
	protected List<NodeContainer> placeOneColumn(int column, int numberOfNodes, int maxNodePerColumn, AbstractGraphHandler agh) {
		ArrayList<NodeContainer> list = new ArrayList<NodeContainer>();
		int x = column*300;
		double yIncr;
		double y;
		if (numberOfNodes == maxNodePerColumn) {
			yIncr = (double)2000/(double)(numberOfNodes-1);
			y = 0;
		} else {
			yIncr = 2000d/(double)numberOfNodes;
			y = yIncr/2d;
		}
		for (int i = 0 ; i < numberOfNodes ; i++) {
			if (vertical) {
				list.add(agh.newNode(x,(int)Math.round(y)));
			} else {
				list.add(agh.newNode((int)Math.round(y),x));
			}
			y += yIncr;
		}
		return list;
	}	
}
