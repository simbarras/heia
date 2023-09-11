package ch.epfl.javanco.remote;

import java.rmi.RemoteException;

import ch.epfl.general_libraries.logging.Logger;

abstract class RMIServerBaseObject {

	abstract Logger getLogger();

	protected void logAndThrowException(String msg, Throwable t) throws RemoteException {
		getLogger().error(msg, t);
		throw new RemoteException(msg, t);
	}
}

