package ch.epfl.javancox.inputs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javancox.inputs.compounds.AbstractGraphExperimentInput;
import ch.epfl.javancox.inputs.topology.GridRandomGenerator;
import ch.epfl.javancox.topology_analysis.AbstractTopologyAnalyser;
import ch.epfl.javancox.topology_analysis.network_metrics.ComponentNumberComputer;
import ch.epfl.javancox.topology_analysis.node_pair_metrics.PathDistanceComputer;
import ch.epfl.javancox.topology_analysis.node_pair_metrics.TopologicalDistanceComputer;
import org.junit.jupiter.api.Test;



import ch.epfl.javancox.inputs.compounds.FromGeneratorGraphExperimentInput;
import ch.epfl.javancox.inputs.topology.AbstractRandomTopologyGenerator;
import ch.epfl.javancox.inputs.topology.ReyniErdosGenerator;
import ch.epfl.javancox.inputs.topology.ScaleFreeNetworkGenerator;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import ch.epfl.javancox.topology_analysis.CombinedTopologyAnalyser;
import ch.epfl.javancox.topology_analysis.TopologyAnalysis;
import ch.epfl.javancox.topology_analysis.network_metrics.GiantComponentSizeComputer;
import ch.epfl.javancox.topology_analysis.node_metrics.DegreeComputer;
import ch.epfl.javancox.topology_analysis.node_metrics.NodeBetweennessComputer;
import ch.epfl.javancox.topology_analysis.node_pair_metrics.AlgebraicalDistanceComputer;


public class TopologyGenerationAndAnalysisIntegrationTest {
	
	@Test
	public void testTreeLayouts() throws Exception {
		
	 //	NativeDB oldDb = new NativeDB();
		SmartDataPointCollector sebDb = new SmartDataPointCollector();
	//	OldDB oldDb = new OldDB(); 
		
		int[] nbNodesT = new int[]{2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
		int[] seedT = new int[]{1};
		for (int nbNodes : nbNodesT) {
			for (int seed : seedT) {
				
				List<AbstractRandomTopologyGenerator> generators = new ArrayList<AbstractRandomTopologyGenerator>();
			
				generators.add(new ReyniErdosGenerator(0.12f, nbNodes));
				generators.add(new ScaleFreeNetworkGenerator(nbNodes, 1));
				generators.add(new ScaleFreeNetworkGenerator(nbNodes, 2));
				generators.add(new ScaleFreeNetworkGenerator(nbNodes, 0.75f));
				
				
				CombinedTopologyAnalyser lyser = new CombinedTopologyAnalyser(
					new DegreeComputer(),
					new NodeBetweennessComputer(false),
					new GiantComponentSizeComputer(),
					new AlgebraicalDistanceComputer()
				);
				
				for (AbstractRandomTopologyGenerator gen : generators) {
					
					gen.setSeed(seed);
					
				
					TopologyAnalysis analysis = new TopologyAnalysis(
						new FromGeneratorGraphExperimentInput(gen),
						lyser
					);
					
					analysis.run(sebDb, null);

				}
			}
		}
		
		DefaultResultDisplayingGUI.displayDefault(sebDb, "Graph Analysis - SEB");		

	}

	public static final boolean display = true;

	@Test
	public void testGridLayout() throws Exception {

		SmartDataPointCollector sebDb = new SmartDataPointCollector();

		int[] nodes = GridRandomGenerator.getIntArray(20,21,2);
		int[] seeds =  GridRandomGenerator.getIntArray(1,5);
		float[] degs = GridRandomGenerator.getFloatArray(1.6f, 5f, 0.2f);
		float[] fracs = GridRandomGenerator.getFloatArray(0.0f, 0.4f, 0.2f);

		DegreeComputer comp = new DegreeComputer();

		for (int seed : seeds) {

			for (int node : nodes) {
				for (float f : degs) {
					for (float frac : fracs) {
						GridRandomGenerator gen = new GridRandomGenerator(node, f, frac, PRNStream.getDefaultStream(seed));

						TopologyAnalysis analysis = new TopologyAnalysis(
								new FromGeneratorGraphExperimentInput(gen),
								comp
						);

						//		analysis.getAgh().saveGraphImage("test"+id++, 600, 600);

						analysis.run(sebDb, null);

					}
				}
			}
		}

		if (display)
			DefaultResultDisplayingGUI.displayDefault(sebDb);

	}

	@Test
	public void testTopologyAnalysis() throws Exception {



		SmartDataPointCollector db = new SmartDataPointCollector();
		int[] nodes = new int[]{15,20,};
		int[] seeds = new int[]{1,2};
		float[] linkProbability = new float[]{0.2f, 0.3f, 0.4f, 0.5f, 0.7f, 0.9f, 1.1f, 1.3f, 1.5f, 1.7f, 1.9f, 2.1f, 2.3f, 2.5f, 2.75f, 3.0f, 3.25f, 3.5f, 3.75f, 4f};

		//Class[] classes = new Class[]{ReyniErdosGenerator.class/*CirclesPlanarGenerator.class, DivisionPlanarGenerator.class*/};
		AbstractTopologyAnalyser[] metrics = new AbstractTopologyAnalyser[]{
				new ComponentNumberComputer(),
				new GiantComponentSizeComputer.RelativeGiantComponentSizeComputer(),
				new TopologicalDistanceComputer(false), new PathDistanceComputer(false)
				//new AlgebraicalDistanceComputer(),
				//new GeodesicalDistanceComputer(), new NodeBetweennessComputer()
		};



		CombinedTopologyAnalyser comb = new CombinedTopologyAnalyser(Arrays.asList(metrics));

		for (int seed : seeds) {
			for (float linkProb : linkProbability) {
				for (int node : nodes) {
					AbstractGraphExperimentInput creator = new FromGeneratorGraphExperimentInput(
							new ReyniErdosGenerator(node, linkProb*2f, seed)
					);
					TopologyAnalysis topoAna = new TopologyAnalysis(creator, comb);

					topoAna.run(db, null);
				}
			}
		}
		DefaultResultDisplayingGUI.displayDefault(db,"Topology test");
	}
	
}
