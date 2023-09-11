package ch.epfl.javancox.inputs.topology;

import java.util.Set;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.Util2DFunctions;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.utils.ComponentCounter;



public abstract class AbstractRandomPlanarGenerator extends AbstractRandomSpatialTopologyGenerator {

	protected AbstractRandomPlanarGenerator(int nbNodes, PRNStream stream) {
		super(nbNodes, stream);
	}

	public static int reconnectComponents(AbstractGraphHandler agh) {
		int add = 0;
		boolean result = false;
		while (result == false) {
			ComponentCounter cc = new ComponentCounter(agh, agh.getEditedLayer().getName());
			if (cc.getNumberOfComponents() > 1) {
				Set<NodeContainer> largest = cc.getLargestComponent();
				for (Set<NodeContainer> minor : cc.getMinorComponents()) {
					if (connect(largest, minor, agh)) {
						add++;
					}
				}
				result = (add >= cc.getNumberOfComponents()-1);
			} else {
				result = true;
			}
		}
		return add;
	}
	/** Debug */
/*	private static AbstractGraphHandler agh_;
	static JavancoDefaultGUI gui;*/
	

	private static boolean connect(Set<NodeContainer> largest, Set<NodeContainer> minor, AbstractGraphHandler agh) {
/*		agh_ = agh;
		OO o = new OO();*/
		double minDist = Float.MAX_VALUE;
		NodeContainer couple1 = null;
		NodeContainer couple2 = null;
		for (NodeContainer nc1 : largest) {
			for (NodeContainer nc2 : minor) {
				double d = Util2DFunctions.distance(nc1, nc2);
				if (d < minDist) {
					if (!Util2DFunctions.intersects(nc1, nc2, agh.getLinkContainers())) {
						minDist = d;
						couple1 = nc1;
						couple2 = nc2;
					}
				}
			}
		}
		if (couple1 == null || couple2 == null) {
//			javax.swing.JOptionPane.showConfirmDialog(gui.getMainFrame(), "eui");
		//	agh.newLink(largest.iterator().next(), minor.iterator().next());
			return false;
		} else {
			agh.newLink(couple1, couple2);
			return true;
		}
	}
/*	private static class OO {
		private boolean test(int i, int j) {
			NodeContainer nc1 = agh_.getNodeContainer(i);
			NodeContainer nc2 = agh_.getNodeContainer(j);
			
			return (!Util2DFunctions.intersects(nc1, nc2, agh_.getLinkContainers()));
		}
	}*/

}
