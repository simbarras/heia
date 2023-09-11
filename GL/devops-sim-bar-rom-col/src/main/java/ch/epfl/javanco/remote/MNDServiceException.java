package ch.epfl.javanco.remote;

public class MNDServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MNDServiceException(String s) {
		super(s);
	}

	public MNDServiceException(String s, Throwable t) {
		super(s,t);
	}
}
