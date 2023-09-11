package ch.epfl.general_libraries.random;


import umontreal.ssj.rng.CloneableRandomStream;
import ch.epfl.general_libraries.logging.Logger;

public class SSJ_MT19937Initializer implements CloneableRandomStream {

	private static Logger logger = new Logger(SSJ_MT19937Initializer.class);

	private int[] seedTab = new int[624];
	private double[] doub = new double[624];
	private int originalSeed;
	private int pointer = 0;

	@Override
	public CloneableRandomStream clone() {
		return new SSJ_MT19937Initializer(originalSeed);
	}

	public SSJ_MT19937Initializer(int seed) {
		originalSeed = seed;
		seedTab[0] = seed;
		doub[0] = seed / 4294967296d;
		for (int i = 1 ; i < 624 ; i++) {
			seedTab[i] = (1812433253 * (seedTab[i-1] ^ (seedTab[i-1] >>> 30)) + i);
			seedTab[i] &= 0xffffffff;
			doub[i] = (seedTab[i] / 4294967296d);
		}
	}

	public SSJ_MT19937Initializer(int[] state) {
		if (state.length == seedTab.length) {
			//	logger.debug("State length match perfeclty !");
			seedTab = state;
		} else if (state.length > seedTab.length) {
			logger.info("WARNING : state given for initialisation (MT19937) is too long (requied : "+seedTab.length+", found : " + state.length + ")");
			for (int i = 0 ; i < seedTab.length ; i++) {
				seedTab[i] = state[i];
			}
		} else {
			logger.error("MT19937 should be initialised with at least " + seedTab.length + " integers");
			throw new IllegalArgumentException("Too short array for state");
		}
	}

	public int[] getState() {
		return seedTab;
	}

	public void nextArrayOfDouble(double[] u, int start, int n) {
		throw new IllegalStateException("This stream is dedicated for MT19937 initalisation. Only nextDougle() is available");
	}

	public void nextArrayOfInt(int i, int j, int[] u, int start, int n) {
		throw new IllegalStateException("This stream is dedicated for MT19937 initalisation. Only nextDougle() is available");
	}

	public double nextDouble() {
		pointer++;
		double toReturn = (seedTab[pointer - 1] / 4294967296d);
		/*	if (pointer < 10 || (seedTab.length - pointer < 10)) {
			logger.trace("Initialiser " + pointer + " : " + toReturn);
		}*/
		return toReturn;
	}

	public int nextInt(int i, int j) {
		throw new IllegalStateException("This stream is dedicated for MT19937 initalisation. Only nextDougle() is available");
	}

	public void resetNextSubstream() {
	}
	public void resetStartStream() {
	}
	public void resetStartSubstream() {
	}
	@Override
	public String toString() {
		return "0";
	}
}
