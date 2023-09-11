package ch.epfl.javancox.topology_analysis;

import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.path.PathSet;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.compounds.AbstractGraphExperimentInput;


public abstract class AbstractTopologyAnalyser extends AbstractExperimentBlock {
	
	public Map<String, String> getAllParameters() {
		return getEmptyMap();
	}

	protected TopologyAnalysis rpi;
	protected PathSet sp;
	protected AbstractGraphExperimentInput input;

	public abstract void init();
	public abstract String getResultName();
	public abstract double computeMainResult();
	
/*	public double getMainResult(GraphBasedExperiment rpi) {
		this.rpi = rpi;
		init();
		return computeMainResult();
	}*/

	public void setAGH(AbstractGraphHandler agh) {
		rpi = new TopologyAnalysis(agh, this);
		this.input = rpi.getInput();
		init();
	}
	
	public void setAGH(AbstractGraphHandler agh, PathSet shortestPathSet) {
		rpi = new TopologyAnalysis(agh, this);
		this.input = rpi.getInput();		
		this.sp = shortestPathSet;
		init();
	}
	
	public void setExperiment(TopologyAnalysis rpi) {
		this.rpi = rpi;
		init();
	}
	
	public static boolean hasOneComponent ( AbstractGraphHandler agh ) {
		int comp = getNumberOfComponents( agh );

		if (comp == 1) {
			return true;
		}
		return false;
	}
	
	public static int getNumberOfComponents(AbstractGraphHandler agh) {
		class LocalIteration {
			void colorNodeNeighbours(NodeContainer nc, int color, int[] colors,AbstractGraphHandler agh) {
				colors[nc.getIndex()]=color;
				for(NodeContainer nc2 : agh.getNodeContainer(nc.getIndex()).getConnectedNodes()){
					if (colors[nc2.getIndex()]==-1){
						colorNodeNeighbours(nc2,color,colors,agh);
					}
				}
			}
		}
		LocalIteration ite = new LocalIteration();

		int[] colors = new int[agh.getHighestNodeIndex()+1];
		for (int i = 0 ; i < colors.length ; i++) {
			colors[i] = -1;
		}
		int startColor = 0;

		for (int i = 0 ; i <= agh.getHighestNodeIndex() ; i++) {
			if (colors[i] < 0) {
				NodeContainer nc = agh.getNodeContainer(i);
				if (nc == null) continue;
				ite.colorNodeNeighbours(nc, startColor, colors, agh);
				startColor++;
			}
		}
		return (startColor);
	}		
}
