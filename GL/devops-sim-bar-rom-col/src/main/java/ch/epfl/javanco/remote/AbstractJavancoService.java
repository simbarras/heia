package ch.epfl.javanco.remote;


import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.webserver.AbstractService;

public abstract class AbstractJavancoService extends AbstractService {

	private static final Logger logger = new Logger(AbstractJavancoService.class);

	@Override
	public abstract Logger getLogger();
	

	public AbstractJavancoService(Context context) {
		super(context);
	}


	/**
	 * Tries to retrieve the current IRemoteGraphWrapper
	 * @param sessionID
	 * @return
	 */
	protected IRemoteGraphWrapper getCurrentWrapper(String sessionID, boolean createNewWrapperIfNoExists) throws Exception {
		IRemoteGraphWrapper toReturn = null;
		//	try {
		logger.debug("Recovering wrapper corresponding to session " + sessionID);
		if(!WrapperFactory.getWrapperFactory().hasWrapper(sessionID)) {
			if (createNewWrapperIfNoExists) {
				WrapperFactory.getWrapperFactory().newHandler(sessionID);
			} else {
				return toReturn;
			}
		}
		toReturn =WrapperFactory.getWrapperFactory().getCurrentWrapper(sessionID);
		logger.debug("Recovered wrapper is " + toReturn.rmiToString());
		/*	}
		catch (RemoteException e) {
			logger.error("Impossible to access wrapper handler",e);
		}*/
		return toReturn;
	}

	/**
	 *
	 */
	protected IRemoteGraphWrapper newWrapper(String sessionID) throws Exception {
		IRemoteGraphWrapper toReturn = null;
		//	try {
		logger.debug("Force to create new wrapper for session " + sessionID);
		WrapperFactory.getWrapperFactory().newHandler(sessionID);
		toReturn = WrapperFactory.getWrapperFactory().getCurrentWrapper(sessionID);
		logger.debug("Created wrapper is " + toReturn.rmiToString());
		/*	}
		catch (RemoteException e) {
			logger.error("Impossible to access wrapper handler",e);
		}*/
		return toReturn;
	}

	protected void removeWrapper(String sessionID) throws Exception {
		WrapperFactory.getWrapperFactory().killHandler(sessionID);
	}

	protected boolean hasWrapper(String sessionID) throws Exception {
		return WrapperFactory.getWrapperFactory().hasWrapper(sessionID);
	}

	protected boolean checkFactory() throws Exception {
		return (WrapperFactory.getWrapperFactory() != null);
	}


}
