package ch.epfl.javancox.topology_analysis2;

import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javanco.base.AbstractGraphHandler;

public abstract class AbstractGraphAnalyser {

	public abstract void analyse(AbstractGraphHandler atg,
			AbstractResultsManager man, DataPoint dp, Execution e);

}
