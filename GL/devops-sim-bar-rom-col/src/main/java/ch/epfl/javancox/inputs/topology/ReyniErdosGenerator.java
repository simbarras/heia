package ch.epfl.javancox.inputs.topology;

import java.util.Map;
import java.util.TreeMap;

import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.probdist.UniformIntDist;
import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.layout_reorganiser.UnequalTreeLayout;
import ch.epfl.javancox.inputs.topology.node_positionner.AbstractNodePositionner;
import ch.epfl.javancox.inputs.topology.node_positionner.DistributionBasedNodePositionner;
import ch.epfl.javancox.inputs.topology.node_positionner.LayoutPositionner;
import ch.epfl.javancox.inputs.topology.node_positionner.OrbitalPositionner;


public class ReyniErdosGenerator extends AbstractRandomAspatialTopologyGenerator {

	private float averageDegree;
	private float linkProbability;
	private PRNStream stream;

	public ReyniErdosGenerator(int nbNodes) {
		this(nbNodes, 1f);
	}
	
	public ReyniErdosGenerator(int nbNodes, int seed) {
		this(nbNodes, 1f, PRNStream.getDefaultStream(seed));
	}
	
	public ReyniErdosGenerator(int nbNodes, PRNStream stream) {
		this(nbNodes, 1f, stream);
	}
	
	public ReyniErdosGenerator(int nbNodes, float averageDegree) {
		this(nbNodes, averageDegree, PRNStream.getRandomStream());
	}
		
	public ReyniErdosGenerator(int nbNodes, float averageDegree, int seed) {
		this(nbNodes, averageDegree, PRNStream.getDefaultStream(seed));
	}					

	public ReyniErdosGenerator(int nbNodes, float averageDegree, PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		this.averageDegree = averageDegree;
		int nodeProduct = nbNodes*nbNodes-1;
		float desiredNbLinks = nbNodes*averageDegree;
		linkProbability = desiredNbLinks/nodeProduct;
	}
	
	public ReyniErdosGenerator(int nbNodes, int nbLinks, int seed) {
		this(nbNodes, nbLinks, PRNStream.getDefaultStream(seed));

	}		

	public ReyniErdosGenerator(int nbNodes, int nbLinks, PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		int nodeProduct = nbNodes*nbNodes-1;
		linkProbability = nbLinks/nodeProduct;
	}

	public ReyniErdosGenerator(float linkProbability, int nbNodes) {
		this(linkProbability, nbNodes, PRNStream.getRandomStream());
	}

	public ReyniErdosGenerator(float linkProbability, int nbNodes, int seed) {
		this(linkProbability, nbNodes, PRNStream.getDefaultStream(seed));
	}	
	

	public ReyniErdosGenerator(float linkProbability, int nbNodes, PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		this.linkProbability = linkProbability;
		
	}

	/**
	 * Method getRandomGeneratorParameters
	 *
	 *
	 * @return
	 *
	 */
	@Override
	public Map<String, String> getRandomGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("average_degree",averageDegree+"");
		map.put("number_of_nodes",nbNodes+"");
		return map;
	}
	/**
	 * Method generateRandomTopology
	 *
	 *
	 * @param agh
	 *
	 */
	@Override
	public void generateRandomTopology(AbstractGraphHandler agh) {
		ensureLayer(agh);
		for (int i = 0 ; i < nbNodes ; i++) {
			agh.newNode();
		}
		for (int i = 0 ; i < nbNodes-1 ; i++) {
			for (int j = i+1 ; j < nbNodes ; j++) {
				if (stream.nextDouble() < linkProbability) {
					agh.newLink(i, j);
				}
			}
		}

	}

	/**
	 * Method getName
	 *
	 *
	 * @return
	 *
	 */
	@Override
	public String getName() {
		return "Reyni-Erdos";
	}

	public static class ErdosReyni extends WebTopologyGeneratorStub {

		@MethodDef		
		public String probability(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int nbNodes,
				@ParameterDef (name="Link probability") float prob,
				@ParameterDef (name="Seed") int seed,
				@ForcedParameterDef (possibleValues={"NONE", "uniform", "gaussian", "circle", "tree"})
				@ParameterDef (name="Node placement") String placement)throws InstantiationException{
			return averageDegree(agh, nbNodes, ((float)(nbNodes-1))*prob, seed, placement);
		}
		
		
		@MethodDef
		public String averageDegree(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int nbNodes,
				@ParameterDef (name="Average degree") float degree,
				@ParameterDef (name="Seed") int seed,
				@ForcedParameterDef (possibleValues={"NONE", "uniform", "gaussian", "circle", "tree"})
				@ParameterDef (name="Node placement") String placement)throws InstantiationException{

			String s = test(nbNodes, 2000);	
			if (s != null) return s;

			ReyniErdosGenerator gen = new ReyniErdosGenerator(nbNodes, degree, PRNStream.getDefaultStream(seed));

			gen.generate(agh);

			AbstractNodePositionner pos = null;

			if (placement.equals("uniform")) {
				gen.stream.nextArrayOfInt(0,4,new int[5], 0 , 4);
				pos = unif;
			} else if (placement.equals("gaussian")) {
				pos = norm;
			} else if (placement.equals("circle")) {
				pos = orb;
			} else if (placement.equals("tree")) {
				pos = tree;
			}
			if (pos != null) {
				pos.setMoveNodesOnly(true);
				pos.placeNodes(agh,  gen.stream, nbNodes);
			}
			return null;
		}

	}

	private final static DistributionBasedNodePositionner unif =
		new DistributionBasedNodePositionner(new UniformIntDist(50, 750));

	private final static DistributionBasedNodePositionner norm =
		new DistributionBasedNodePositionner(new NormalDist(350, 120));
		
	private final static LayoutPositionner tree =
		new LayoutPositionner(new UnequalTreeLayout());
		

	private final static OrbitalPositionner orb =
		new OrbitalPositionner();

}
