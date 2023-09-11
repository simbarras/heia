package ch.epfl.general_libraries.experiment_aut;

import java.util.Map;

import ch.epfl.general_libraries.results.AbstractResultsManager;

public abstract class Model extends AbstractExperimentBlock {

	 // Warning, Displayer can be null
	public abstract void conductExperiment(AdvancedExperiment exp, AbstractResultsManager man);

	@Override
	public abstract Map<String, String> getAllParameters();
}
