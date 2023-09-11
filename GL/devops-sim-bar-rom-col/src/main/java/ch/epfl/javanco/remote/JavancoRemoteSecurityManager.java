package ch.epfl.javanco.remote;


public class JavancoRemoteSecurityManager extends SecurityManager {

	/**
	 * Always throws {@link SecurityException}.
	 */
	@Override
	public void checkExit(final int code) {
		if (System.getProperty("JAVANCO.exit") == null) {
			throw new SecurityException("Use of System.exit() is forbidden!");
		}
	}

}

