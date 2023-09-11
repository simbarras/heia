package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator;
import javancox.topogen.GeneratorStub;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.NormalDist;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javancox.inputs.topology.linker.AbstractNodeLinker;
import ch.epfl.javancox.inputs.topology.linker.ReyniErdosLinker;
import ch.epfl.javancox.inputs.topology.linker.RingNodeLinker;
import ch.epfl.javancox.inputs.topology.node_positionner.AbstractNodePositionner;
import ch.epfl.javancox.inputs.topology.node_positionner.DistributionBasedNodePositionner;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class PositionnerAndLinkerBasedGenerator extends AbstractRandomTopologyGenerator {

	private AbstractNodeLinker linker;
	private AbstractNodePositionner posi;
	
	private PRNStream stream;

	public PositionnerAndLinkerBasedGenerator(int nbNodes, AbstractNodeLinker linker, AbstractNodePositionner posi) {
		this(nbNodes, linker, posi, PRNStream.getRandomStream());
	}
	
	public PositionnerAndLinkerBasedGenerator(int nbNodes, 
											AbstractNodeLinker linker, 
											AbstractNodePositionner posi,
											PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		this.linker = linker;
		this.posi = posi;
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
		Map<String,String> map = linker.getAllParameters();
		map.putAll(posi.getAllParameters());
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
		posi.placeNodes(agh, stream, nbNodes);
		linker.linkNodes(agh, stream);
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
		return getClass().getSimpleName();
	}

	/**
	 * Method getGeneratorParameters
	 *
	 *
	 * @return
	 *
	 */
	@Override
	public Map<String, String> getGeneratorParameters() {
		Map<String, String> map = posi.getAllParameters();
		map.putAll(linker.getAllParameters());
		return map;
	}

	public static class PositionnerAndLinker extends GeneratorStub {

		@MethodDef
		public void generateGraph(AbstractGraphHandler agh,
				@ParameterDef (name="Number of nodes")int nbNodes,
				@ParameterDef (name="Node positionner")AbstractNodePositionner pos,
				@ParameterDef (name="Node linker")AbstractNodeLinker linker
		) {

			PositionnerAndLinkerBasedGenerator gen =
				new PositionnerAndLinkerBasedGenerator(nbNodes, linker, pos, PRNStream.getRandomStream());

			gen.generate(agh);
		}

	}


	public static void main (String args[]) throws Exception {
		AbstractNodeLinker[] linkers = new AbstractNodeLinker[]{
				new ReyniErdosLinker(0.1f), new RingNodeLinker()};
		AbstractNodePositionner[] pos = new AbstractNodePositionner[]{
				//			new DistributionBasedNodePositionner(1000),
				new DistributionBasedNodePositionner(new NormalDist(1000, 200)),
				new DistributionBasedNodePositionner(new ExponentialDist(0.001)),
		};
		int[] nodes = new int[]{5,10,20,50,100};

		SmartDataPointCollector db = new SmartDataPointCollector();

		int i = 0;

		for (int n : nodes) {
			for (AbstractNodeLinker l : linkers) {
				for (AbstractNodePositionner p : pos) {
					AbstractTopologyGenerator gen = new PositionnerAndLinkerBasedGenerator(n, l, p);

					AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);

					gen.generate(agh);

					agh.saveGraphImage(i+"", 800, 800);
					i++;
				}
			}
		}
		DefaultResultDisplayingGUI.displayDefault(db, "Positionner and linker");
	}
}
