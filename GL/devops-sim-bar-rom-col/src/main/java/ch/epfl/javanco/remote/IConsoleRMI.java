package ch.epfl.javanco.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Christophe Trefois
 *
 */
public interface IConsoleRMI extends Remote {
	/**
	 * Create a new AbstractGraphHandler and register the Handler as AGH + sessionID in the RMI Registry
	 * @param sessionID the sessionID provided by the web session
	 * @return true on successful creation and registration, false otherwise
	 * @throws RemoteException
	 */
	public boolean createNewGraphHandler(String sessionID) throws RemoteException;

	public void deleteGraphHandler(String sessionID) throws RemoteException;

	public void setUIManagerClassName(String s) throws RemoteException;
}
