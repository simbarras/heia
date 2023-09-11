package ch.epfl.javancox.topology_analysis;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.AdvancedExperiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.compounds.AbstractGraphExperimentInput;
import ch.epfl.javancox.inputs.compounds.DefaultGraphExperimentInput;

public class TopologyAnalysis extends AdvancedExperiment {
	
	private CombinedTopologyAnalyser cta;
	private AbstractGraphExperimentInput input;

	public TopologyAnalysis(@ParamName(name = "Topology_Creator") AbstractGraphExperimentInput input,
			@ParamName(name = "Analysers_list") CombinedTopologyAnalyser analyser) {
		input.create();
		this.input = input;
		this.cta = analyser;
	}

	public TopologyAnalysis(@ParamName(name = "Topology_Creator") AbstractGraphExperimentInput input,
			@ParamName(name = "Single_analyser") AbstractTopologyAnalyser analyser) {
		input.create();
		this.input = input;		
		this.cta = new CombinedTopologyAnalyser(analyser);
	}

	@ConstructorDef(ignore=true)
	public TopologyAnalysis(AbstractGraphHandler agh, AbstractTopologyAnalyser topoAl) {
		input = new DefaultGraphExperimentInput(agh, "physical", "length");
		input.create();
		agh.activateMainDataHandler();
		agh.activateGraphicalDataHandler();
		this.cta = new CombinedTopologyAnalyser(topoAl);
	}



	@Override
	public String getExperimentName() {
		return "Topology analysis";
	}

	@Override
	public String getExperimentVersion() {
		return "1.0";
	}

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> m = cta.getAllParameters();
		m.putAll(getInput().getAllParameters());
		return m;
	}

	@Override
	protected void conductExperiment(AbstractResultsManager man,
			AbstractResultsDisplayer dis) {
		cta.conductExperiment(this);
		
	}

	public AbstractGraphExperimentInput getInput() {
		return input;
	}



}
