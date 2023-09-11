package ch.epfl.javancox.inputs.topology;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.JavancoGUI;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.utils.ComponentCounter;

public abstract class AbstractRandomTopologyGenerator extends AbstractTopologyGenerator {

	private PRNStream privateStream = null;
	protected int nbNodes;

	private Map<String, String> map;

	public abstract Map<String, String> getRandomGeneratorParameters();
	
	public String getFullName() {
		return getName()+"_"+privateStream.getClass().getSimpleName()+"_seed_"+privateStream.getSeed();
	}
	
	public PRNStream getStream() {
		return privateStream;
	}
	
	public void setStream(PRNStream s) {
		privateStream = s;
	}

	@Override
	public final int getNumberOfNodes() {
		return nbNodes;
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		map = getRandomGeneratorParameters();
		map.put("topology_seed", privateStream.getInitialSeed()+"");
		return map;
	}

	public abstract void generateRandomTopology(AbstractGraphHandler agh);

	@Override
	public void generate(AbstractGraphHandler agh) {
		generateRandomTopology(agh);
		int nbLinks = agh.getEditedLayer().getLinkContainers().size();
		if (map == null) {
			map = getRandomGeneratorParameters();
		}
		map.put("nb_links", nbLinks+"");
		if (agh.getNumberOfNodes() > 0) {
			map.put("average_degree", 2*nbLinks/agh.getNodeContainers().size()+"");
		} else {
			map.put("average_degree", "NA");

		}
	}

	public AbstractRandomTopologyGenerator(int nbnodes, PRNStream stream) {
		this.privateStream = stream;
		this.nbNodes = nbnodes;
	}


	protected void selectLargestComponentOnly(AbstractGraphHandler agh) {
		ComponentCounter cc = new ComponentCounter(agh, agh.getEditedLayer().getName());
		if (cc.getNumberOfComponents() > 1) {
			ArrayList<NodeContainer> toRem = new ArrayList<NodeContainer>();
			for (Set<NodeContainer> minor : cc.getMinorComponents()) {
				for (NodeContainer nc : minor) {
					toRem.add(nc);
				}
			}
			for (NodeContainer nc : toRem) {
				agh.removeElement(nc);
			}
		}
	}

	public void display(PRNStream stream) {
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(true);
		agh.activateMainDataHandler();
		agh.activateGraphicalDataHandler();
		generate(agh);
		JavancoGUI.displayGraph(agh, 800,800);
	}
	
	public void resetStream() {
		privateStream.reset();
	}
	
	public void setSeed(int i) {
		try {
			privateStream.setSeed(i);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
