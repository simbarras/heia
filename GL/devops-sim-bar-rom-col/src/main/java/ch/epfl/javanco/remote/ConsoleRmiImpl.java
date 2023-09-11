package ch.epfl.javanco.remote;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.GraphHandlerFactory;
import ch.epfl.javanco.ui.AbstractUI;

/**
 * @author Christophe Trefois
 *
 */
public class ConsoleRmiImpl extends RMIServerBaseObject implements IConsoleRMI {
	/**
	 * 
	 */
	private static final Logger logger = new Logger(ConsoleRmiImpl.class);
	/**
	 * 
	 */
	private GraphHandlerFactory graphHandlerFactory = null;

	private Hashtable<String, AbstractGraphHandler> publishedHandlers = new Hashtable<String, AbstractGraphHandler>();
	/**
	 * 
	 */
	private int port = -1;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * 
	 * @param fac
	 * @throws RemoteException
	 */
	public ConsoleRmiImpl(GraphHandlerFactory fac, int port) throws RemoteException {
		this.port = port;
		this.graphHandlerFactory = fac;
		startWebConsole();
	}

	public boolean createNewGraphHandler(String sessionID) throws RemoteException {
		try {
			//			this.sessionID = sessionID;
			// Create a new AbstractGraphHandler but cast it to WrapperBearer to prevent mis-access
			logger.trace("Creating a new AbstractGraphHandler");
			AbstractGraphHandler wrappedAgh = graphHandlerFactory.getNewGraphHandler();
			wrappedAgh.activateGraphicalDataHandler();

			RemoteGraphWrapperImpl wrapper = new RemoteGraphWrapperImpl(wrappedAgh);

			// Create a stub of the RemoteGraphWrapper wrapper
			IRemoteGraphWrapper stub = (IRemoteGraphWrapper)UnicastRemoteObject.exportObject(wrapper, 0);

			// Register the stub to the RMI Registry
			String url = getURL(sessionID);
			Naming.rebind(url, stub);

			publishedHandlers.put(url,wrappedAgh);

			logger.info("New agh published on RMI for session  " + sessionID + " !");
			return true;
		} catch (Exception e) {
			logAndThrowException("Error while creating and publishing a new AbstractGraphHandler", e);
			return false;
		}
	}

	public void deleteGraphHandler(String sessionID) throws RemoteException {
		try {
			logger.trace("Removing AbstractGraphHandler corresponding to rmi session " + sessionID);
			Naming.unbind(getURL(sessionID));
			AbstractGraphHandler agh = publishedHandlers.remove(getURL(sessionID));
			graphHandlerFactory.recycleGraphHandler(agh);
		}
		catch (Exception e) {
			logAndThrowException("Error while unbinding AbstractGraphHandler", e);
		}

	}

	@SuppressWarnings("unchecked")
	public void setUIManagerClassName(String s) throws RemoteException {
		try {
			Class<? extends AbstractUI> c = null;
			try {
				c = (Class<? extends AbstractUI>) Class.forName(s);
			}
			catch (Exception e) {}
			if (c == null) {
				c = ch.epfl.javanco.ui.console.ConsoleUI.class;
			}
			//			graphHandlerFactory.setUIManagerClass(c);
			logger.debug("UIManager to use has been set to " + c);
		}
		catch (Exception e) {
			logAndThrowException("Impossible to set UIManager class ", e);
		}
	}

	private String getURL(String sessionID) {
		return "rmi://localhost:"+ port +"/AGH" + sessionID;
	}

	private void startWebConsole() {
		Runnable r = new Runnable() {
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(8089);
					while(true) {
						try {
							Socket s = ss.accept();
							OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
							for (AbstractGraphHandler agh : publishedHandlers.values()) {
								osw.write(agh+"\r\n");
							}
							osw.close();
							s.close();
						}
						catch (Throwable t) {
							System.out.println("t" + t);
						}
					}
				}
				catch (Exception e) {
					System.out.println(e);
				}
			}
		};
		Thread t = new Thread(r);
		t.setName("JavancoRMIConsole_Console");
		t.start();
	}
}
