package ch.epfl.javancox.inputs.compounds;

import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class FromGeneratorGraphExperimentInput extends AbstractGraphExperimentInput {
	private AbstractTopologyGenerator topoGen;

	public FromGeneratorGraphExperimentInput(@ParamName(name = "Topology_generator") AbstractTopologyGenerator topoGen) {
		this.topoGen = topoGen;
	}		

	@Override
	public void createImpl(AbstractGraphHandler agh) {
		topoGen.generate(agh);
		setLinksLengths(agh);
	}

	@Override
	public Map<String, String> getAllParameters() {
		return topoGen.getAllParameters();
	}

	@Override
	public String getPhysicalLayerName() {
		return topoGen.getTopologyLayerName();
	}
	@Override
	public String getLengthAttributeName() {
		return XMLTagKeywords.LENGTH.toString();
	}
}