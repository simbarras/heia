package ch.epfl.general_libraries.experiment_aut;

public class WrongExperimentException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongExperimentException() {
		
	}
	
	public WrongExperimentException(String m) {
		super(m);
	}
	
	public WrongExperimentException(Throwable t) {
		super(t);
	}
}
