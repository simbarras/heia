package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import javancox.layout.AbstractTopologyLayout;
import javancox.topogen.GeneratorStub;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;


public class AspatialAndLayoutBasedGenerator extends AbstractRandomTopologyGenerator {

	private AbstractRandomAspatialTopologyGenerator aspa;
	private AbstractTopologyLayout lay;

	public AspatialAndLayoutBasedGenerator(AbstractRandomAspatialTopologyGenerator aspa, AbstractTopologyLayout lay, PRNStream stream) {
		super(-1, stream);
		this.aspa = aspa;
		this.lay = lay;
	}

	@Override
	public void generateRandomTopology(AbstractGraphHandler agh) {
		aspa.generate(agh);
		lay.assignNodesPosition(agh);
	}

	@Override
	public Map<String, String> getRandomGeneratorParameters() {
		Map<String,String> map = aspa.getAllParameters();
		map.putAll(lay.getAllParameters());
		return map;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public static class AspatialPlusLayout extends GeneratorStub {

		@MethodDef
		public void generateGraph(AbstractGraphHandler agh,
				@ParameterDef (name="Aspatial generator")AbstractRandomAspatialTopologyGenerator aspa,
				@ParameterDef (name="Layout mode")AbstractTopologyLayout lay,
				@ParameterDef (name="seed")int seed					
		) {

			AspatialAndLayoutBasedGenerator gen =
				new AspatialAndLayoutBasedGenerator(aspa, lay, PRNStream.getDefaultStream(seed));

			gen.generate(agh);
		}
	}

}
