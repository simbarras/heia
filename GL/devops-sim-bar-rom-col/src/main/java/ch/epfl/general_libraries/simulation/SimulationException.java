package ch.epfl.general_libraries.simulation;


public class SimulationException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimulationException(String s) {
		super(s);
	}

	public SimulationException(String s, Throwable t) {
		super(s,t);
	}

}
