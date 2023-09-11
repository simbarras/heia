package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.general_libraries.utils.DynamicInstancier;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javancox.inputs.AbstractTopologyProvider;

public class GeneratorBasedTopologyProvider extends AbstractTopologyProvider {

	private AbstractTopologyGenerator generator;

	public GeneratorBasedTopologyProvider(AbstractTopologyGenerator gen) {
		this.generator = gen;
	}

	public GeneratorBasedTopologyProvider(int node, int seed, Class<AbstractTopologyGenerator> class_) {
		generator = (AbstractTopologyGenerator)DynamicInstancier.getInstance(class_, node, seed);
	}
	
	@Override
	public String getTopologyName() {
		return generator.getFullName();
	}

	@Override
	public void provideTopology(AbstractGraphHandler agh) {
		generator.generate(agh);
		for (LinkContainer lc : agh.getLinkContainers()) {
			lc.setGeodesicLinkLength();
		}
	}


	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = generator.getAllParameters();
		map.put("topology_provided_by", "generation");
		return map;
	}

	@Override
	public String getTopologyLayerName() {
		return generator.getTopologyLayerName();
	}
}
