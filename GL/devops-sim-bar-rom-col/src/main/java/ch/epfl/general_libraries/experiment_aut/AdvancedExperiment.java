package ch.epfl.general_libraries.experiment_aut;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.DateAndTimeFormatter;


public abstract class AdvancedExperiment extends AbstractExperimentBlock implements Experiment {

	private static Logger logger;

	private DataPoint rootDataPoint;
	private Execution currentExecution;

	public abstract String getExperimentName();
	public abstract String getExperimentVersion();


	public AdvancedExperiment() {
		super();
		logger = new Logger(AdvancedExperiment.class);
	}

	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		long time = 0;
		long time2;
		try {
			time = System.currentTimeMillis();		
			conductExperiment(man, dis);
			time2 = System.currentTimeMillis() - time;
		}
		catch (TimeOutExpired e) {
			time2 = System.currentTimeMillis() - time;
			logger.warn("*** Experiment aborted : " + time2 + " ms");
			return;
		}
		if (time2 < 100) {
			time2 = 100;
		}
		logger.info("*** Experiment running time : " + time2 + " ms");
		rootDataPoint.addResultProperty("runningTime", time2);
	}
	
	protected abstract void conductExperiment(AbstractResultsManager man, AbstractResultsDisplayer dis);
	
	public DataPoint getRootDataPoint() {
		if (rootDataPoint == null) {
			rootDataPoint = new DataPoint(getAllParameters());
		}
		return rootDataPoint.getDerivedDataPoint();
	}
	
	public Execution getAssociatedExecution() {
		if (currentExecution == null) {
			currentExecution = new Execution(
					getExperimentName(),
					getExperimentVersion(),
					DateAndTimeFormatter.getDateAndTime(System.currentTimeMillis())
			);
		}
		return currentExecution;		
	}
}
