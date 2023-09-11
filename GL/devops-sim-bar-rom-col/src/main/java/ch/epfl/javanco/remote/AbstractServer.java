package ch.epfl.javanco.remote;

import ch.epfl.general_libraries.logging.Logger;

public class AbstractServer {

	protected static Logger logger;

	protected static void logInit(Class<? extends AbstractServer> c, String s) {
		logger = new Logger(c);
		logger.info("");
		logger.info("=================================");
		logger.info("     STARTING "+ s);
		logger.info("=================================");
		logger.info("");
	}

}
