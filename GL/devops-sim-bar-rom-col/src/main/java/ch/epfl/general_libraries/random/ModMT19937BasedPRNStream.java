package ch.epfl.general_libraries.random;

import ch.epfl.general_libraries.logging.Logger;

public class ModMT19937BasedPRNStream  extends SSJBasedPRNStream {

	private static Logger logger = new Logger(ModMT19937BasedPRNStream.class);

	ModMT19937 source = null;

	public ModMT19937BasedPRNStream() {
		super(0);
	}

	@Override
	public void setSeed(int arg1) {
		SSJ_MT19937Initializer mtInit = new SSJ_MT19937Initializer(arg1);
		source = new ModMT19937(mtInit);
		ssjRandomStream = source;
	}

	@Override
	public void setStateInt(int[][] arg) {
		source = new ModMT19937();
		source.setStateInt(arg);
		ssjRandomStream = source;
	}


	@Override
	public int[][] getStateInt() {
		//	logger.trace("Getting internal state from PRNG");
		int[][] toReturn = null;
		try {
			toReturn = source.getStateInt();
		}
		catch (Exception e) {
			logger.error("Error",e);
		}
		//	logger.trace("Sucessful");
		return toReturn;
	}


}
