package ch.epfl.javanco.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.Javanco;

/**
 * @author Christophe Trefois
 *
 */
public class WrapperFactory {
	private static Logger logger;
	private HashMap<String, IRemoteGraphWrapper> wrapperTable = null;
	private IConsoleRMI myConsole = null;
	private String rmiURL = null;


	private static WrapperFactory singleton;


	public static WrapperFactory getWrapperFactory() {
		logger = new Logger(WrapperFactory.class);
		if (singleton == null) {
			try {
				String ip = "127.0.0.1";
				int range = 1;
				singleton = new WrapperFactory(ip, Integer.parseInt(Javanco.getProperty(JavancoRmiServer.RMI_PORT_PROPERTY)), range);
			} catch (MalformedURLException e) {
				logger.error("URL Form Problem", e);
				singleton = null;
			} catch (RemoteException e) {
				logger.error("Remote Problem Exception - possibly RMI service not found", e);
				singleton = null;
			} catch (NotBoundException e) {
				logger.error("Service Not Found", e);
				singleton = null;
			} catch (Exception e) {
				logger.error("Other exception", e);
				singleton = null;
			}
		}
		return singleton;
	}
	/**
	 * Initialize the WrapperFactory by reading the configuration file javancoServer.properties
	 * and retrieving te ConsoleService registered in the remote RMI Registry.
	 *
	 */
	private WrapperFactory(String ip, int port, int range) throws Exception {
		String rmiIP = ip;
		int rmiPort = port;
		int trials = range;
		int counter = 0;
		boolean ok = false;

		while (counter < trials && !ok) {
			try {
				rmiURL = "rmi://" + rmiIP + ":" + (rmiPort+counter) + "/";
				if(myConsole == null) {
					myConsole = (IConsoleRMI)Naming.lookup(rmiURL + "JavancoRMIService");
					if(wrapperTable == null) {
						wrapperTable = new HashMap<String, IRemoteGraphWrapper>();
					}
				}
				ok = true;
			}
			catch (java.rmi.ConnectIOException e) {
				counter++;
			}
			catch (java.rmi.ConnectException e) {
				counter++;
			}
		}



		myConsole.setUIManagerClassName("ch.epfl.javanco.ui.web.WebUIManager");
	}

/*	private void quit() {
		logger.fatal("Impossible to connect to RMI server, check if it is alive");
		logger.fatal("exiting...");
		System.exit(-1);
	}*/

	private void addHTMLMapAttributes(IRemoteGraphWrapper wrapper) {
		try {
			wrapper.addAreaAttribute("onmouseover=\"return overlib('<div align=center>Node ID: %id%</div>', OFFSETX, 20, WIDTH, 90);\" ");
			wrapper.addAreaAttribute("onmouseover=\"return overlib('<div align=center>Link: %orig% -> %dest%</div>', OFFSETX, 20, WIDTH, 90);\" ");
			wrapper.addAreaAttribute("onmouseout=\"return nd();\"");
			wrapper.addAreaAttribute("class=\"AbstractElementContainer\"");
			wrapper.addAreaAttribute("id=\"%element_key%\"");
		}
		catch (Exception e){}
	}

	/**
	 * Retrieve the current IRemoteGraphWrapper stored in a HashMap.
	 * @param sessionID The sessionID is used to retrieved the Wrapper.
	 * @return the wrapper
	 */
	public IRemoteGraphWrapper getCurrentWrapper(String sessionID) {
		// think if check liveness of JavancoConsole
		IRemoteGraphWrapper remoteWrapper = null;
		synchronized (wrapperTable) {
			remoteWrapper = wrapperTable.get(sessionID);
			if(remoteWrapper == null) {
				remoteWrapper = createWrapperInternal(sessionID);
			}
		}
		return remoteWrapper;
	}

	/**
	 * Create a new AbstractGraphHandler via the RMI system and erase the old one
	 * @param sessionID The Session ID
	 */
	public void newHandler(String sessionID) {
		logger.debug("Requesting a new and empty AbstractGraphHandler (and its wrapper)");
		if (wrapperTable.get(sessionID) != null) {
			deleteHandler(sessionID);
		}
		createWrapperInternal(sessionID);
	}

	public void killHandler(String sessionID) {
		logger.debug("Requesting to delete AbstractGraphHandler corresponding to the session (and its wrapper");
		if (wrapperTable.get(sessionID) != null) {
			deleteHandler(sessionID);
		}
	}

	/**
	 * Determines wether or not there already exists a wrapper for the given Session
	 * @param sessionID the Session ID
	 * @return true if such a wrapper exists, false otherwise
	 */
	public boolean hasWrapper(String sessionID) {
		IRemoteGraphWrapper wrapper = wrapperTable.get(sessionID);
		if(wrapper == null) {
			return false;
		} else {
			//update the access time in the sessionCurrentTime HashMap
			//***********************************************************************************
			SessionTimer.updateSessionTime(sessionID);
			return true;
		}
	}

	private IRemoteGraphWrapper createWrapperInternal(String sessionID) {
		IRemoteGraphWrapper remoteWrapper = null;
		synchronized (wrapperTable) {
			try {
				myConsole.createNewGraphHandler(sessionID);
				logger.trace("AbstractGraphHandler successfully remotely created");
			} catch (RemoteException e) {
				logger.error("Impossible to create remotely a new AbstractGraphHandler", e);
			}
			remoteWrapper = getWrapper(sessionID);
			addHTMLMapAttributes(remoteWrapper);
			logger.debug("Registering wrapper in local session manager under session number " + sessionID);
			wrapperTable.put(sessionID, remoteWrapper);

			//Start the timer for the specified session
			//***********************************************************************************
			SessionTimer.addSessionTimer(sessionID);
		}
		return remoteWrapper;
	}

	/**
	 * 
	 * @param sessionID
	 * @return
	 */
	private IRemoteGraphWrapper getWrapper(String sessionID) {
		logger.debug("Retrieving wrapper for AbstractGraphHandler corresponding to session " + sessionID);
		IRemoteGraphWrapper toReturn = null;
		try {
			toReturn = (IRemoteGraphWrapper)Naming.lookup(rmiURL + "AGH" + sessionID);
		} catch (Exception e) {
			logger.error("Impossible to retrieve wrapper for remote AbstractGraphHandler corresponsing to session " + sessionID);
		}
		try {
			logger.trace("Retrieved wrapper is " + toReturn.rmiToString());
		} catch (RemoteException e) {
			logger.error("Impossible to access returned wrapper", e);
		}
		return toReturn;
	}

	private  void deleteHandler(String sessionID) {
		logger.debug("Removing AbstractGraphHandler (and its wrapper) corresponding to session " + sessionID);
		try {
			synchronized(wrapperTable) {
				myConsole.deleteGraphHandler(sessionID);
				logger.trace("AbstractGraphHandler successfully remotely removed");
				wrapperTable.remove(sessionID);
				logger.trace("Wrapper locally removed, no more objects correspond to session " + sessionID);
				//kill the Timer object associeted to the  killed session
				//***********************************************************************************
				SessionTimer.removeSessionTimer(sessionID);
			}
		}
		catch (Exception e) {
			logger.error("Impossible to remove remotely AbstractGraphHandler corresponding to session " + sessionID, e);
		}
	}
}



