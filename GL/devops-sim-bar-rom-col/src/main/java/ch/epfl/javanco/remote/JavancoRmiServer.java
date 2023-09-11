package ch.epfl.javanco.remote;


import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import ch.epfl.javanco.base.GraphHandlerFactory;

/**
 * This class starts the RMI Registry and is the basis for RMI functionality.
 * @author Christophe Trefois
 *
 */
public class JavancoRmiServer extends AbstractServer {
	
	public static final String RMI_PORT_PROPERTY = "javanco.rmi.port";

	public static void startJavancoRMIServer(GraphHandlerFactory fac, int port, int range) {
		try {
			logInit(JavancoRmiServer.class, "RMI SERVER");
			RemoteDeamon.launchDeamon();
			String previousName = Thread.currentThread().getName();
			Thread.currentThread().setName("Javanco RMIServer launcher");
			port = startRMIRegistry(port, range);
			logger.debug("Trying to register the JAVANCO RMI service...");
			registerJavancoRMIService(fac, port);
			Thread.currentThread().setName(previousName);
		}
		catch (Throwable e) {
			logger.error("Error", e);
		}
	}

	private static int startRMIRegistry(int port, int trials) {
		int counter = 0;
		boolean ok = false;
		try {
			logger.info("Starting RMI registry - Looking for a free port");
			while (!ok || counter > trials) {
				try {
					// Try to start the registry
					java.rmi.registry.LocateRegistry.createRegistry(port + counter);
					logger.info("      trying " + (port + counter) + "   OK !");
					ok = true;
					System.setProperty(RMI_PORT_PROPERTY, ""+(port + counter));
					return port + counter;
				}
				catch (java.rmi.RemoteException e) {
					logger.info("      trying " + (port + counter));
				}

				counter++;
			}
		}
		catch (Exception e) {
			logger.error("Problem while starting RMI registry : " + e.getMessage());
		}
		if (ok == false) {
			System.out.println("Cannot continue, exiting");
			System.exit(0);
		}
		return 0;
	}

	private static void registerJavancoRMIService(GraphHandlerFactory fac, int port) {
		try {
			// Create the Object
			logger.debug("Creating the console object");
			ConsoleRmiImpl obj = new ConsoleRmiImpl(fac, port);
			// Create the Stub
			logger.debug("Exportation of the objet");
			Remote stub = UnicastRemoteObject.exportObject(obj, 16777);
			String url = "rmi://localhost:" + port + "/JavancoRMIService";

			logger.debug("Binding of the object at : " + url);
			Naming.rebind(url, stub);
			logger.info("Service \"JavancoRMIService\" registered successfully under url " + url + " !");
		} catch (Exception e) {
			logger.debug("Impossible to register Javanco service into the RMI registry", e);
			synchronized(System.out) {
				e.printStackTrace();
			}
		}
	}
}
