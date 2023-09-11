package ch.epfl.general_libraries.experiment_aut;

import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;

import java.io.IOException;

public interface Experiment {
	
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException, IOException;
	
	public static class globals {
		public static ClassRepository classRepo = null;
	}
	
	
	
}
