package ch.epfl.javanco.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Christophe Trefois
 *
 */
public interface WrapperBearer extends Remote {
	/**
	 * Returns the RemoteGraphWrapper corresponding to this AbstractGraphHandler <br>
	 * #author Christophe Trefois
	 * @return The RemoteGraphWrapper created by AbstractGraphHandler
	 */
	public RemoteGraphWrapperImpl getRemoteGraphWrapper() throws RemoteException;
}
