package ch.epfl.general_libraries.experiment_aut;

import ch.epfl.general_libraries.results.AbstractResultsManager;

public abstract class TimedOutModel extends Model {
	private int timeOut;
	protected long start;

	public abstract void conductTimedExperiment(AdvancedExperiment exp, AbstractResultsManager man);

	@Override
	public void conductExperiment(AdvancedExperiment exp, AbstractResultsManager man) {
		startTimer();
		conductTimedExperiment(exp, man);
	}

	public void setTimeOut(int millisSeconds){
		timeOut = millisSeconds;
	}

	public void startTimer() {
		start = System.currentTimeMillis();
	}

	public void checkTime() throws TimeOutExpired {
		if (timeOut > 100) {
			if (System.currentTimeMillis() - start > timeOut) {
				throw TimeOutExpired.inst;
			}
		}
	}
}

class TimeOutExpired extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static TimeOutExpired inst = new TimeOutExpired();
}	

