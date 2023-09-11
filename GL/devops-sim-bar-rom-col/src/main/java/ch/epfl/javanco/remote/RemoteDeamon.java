package ch.epfl.javanco.remote;

import java.net.ServerSocket;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.Javanco;

public class RemoteDeamon {

	private static final Logger logger = new Logger(RemoteDeamon.class);

	private static ServerSocket ss;

	private static Thread t;

	public static void launchDeamon() throws Exception {
		if (Javanco.hasBeenInitialised() == false) {
			throw new IllegalStateException("Initialise Javanco prior to start the Deamon");
		}
		logger.info("Start killer deamon");
		if (t == null) {
			t = new Thread() {
				@Override
				public void run() {
					doIt();
				}
			};
			t.start();
			while (ss == null || ss.isBound() == false) {
				Thread.sleep(100);
			}
			logger.info("      started on port " + getDeamonPort());
			logger.info("");
		} else {
			logger.info("      already running");
		}

	}
	
	private static int getDeamonPort() {
		int ret = 0;
		try {
			String p = Javanco.getProperty("deamon_port");
			if (p == null) {
				ret = 15555;
			} else {
				ret = Integer.parseInt(p);
			}
			return ret;
		}
		finally {
			logger.info("Port for deamon " + ret);
		}
	}

	private static void doIt() {
		try {
			Thread.currentThread().setName("JavancoServerKiller");
			ss = new ServerSocket(getDeamonPort());
			ss.setReuseAddress(true);
			ss.accept();
			logger.info("Server killed via listening port");
			System.setProperty("JAVANCO.exit","true");
			try {
				ss.close();
			}
			catch (Exception e) {}
			System.exit(0);
		}
		catch (java.net.BindException e) {
			logger.error("Cannot start deamon because there's already one running",e);
			logger.error("EXITING");
			System.exit(0);
		}
		catch (java.io.IOException e) {
			logger.error("Cannot start deamon for unkfown reason, exiting");
			System.exit(0);
		}
	}
}


