package ch.epfl.javanco.remote;

import java.lang.reflect.Method;
import java.util.Hashtable;

import org.dom4j.Element;

import ch.epfl.general_libraries.web.MNDWebServiceClient;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.xml.NetworkDocumentFactory;

public abstract class MNDService /*extends MNDBasedFunction*/ {

	public abstract boolean callService(AbstractGraphHandler agh) throws Exception;

	public abstract boolean callService(AbstractGraphHandler agh, Hashtable<String, String> table) throws Exception;

	public abstract boolean callServiceLocally(AbstractGraphHandler agh) throws Exception;

	public abstract boolean callServiceLocally(AbstractGraphHandler agh, Hashtable<String, String> table) throws Exception;

	public abstract String getServiceName();

	public static Element callRemoteMNDService(Element e, String address, Hashtable<String, String> table) throws Exception {
		try {
			MNDWebServiceClient client = new MNDWebServiceClient(address);
			NetworkDocumentFactory fact = NetworkDocumentFactory.getInstance();
			e = client.call(e, table, fact);
			return e;
		}
		catch (java.io.IOException ex) {
			throw new MNDServiceException("Impossible to call MNDservice[" + address + "]. " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public static Object callLocalMNDService(AbstractGraphHandler agh,
			String class_,
			boolean local,
			Hashtable<String, String> table) throws MNDServiceException {
		Object result = null;
		String method;
		try {
			Class c = Class.forName(class_);
			if (local) {
				method = "callServiceLocally";
			} else {
				method = "callService";
			}
			MNDService service = (MNDService)c.getConstructor(new Class[]{}).newInstance(new Object[]{});
			for (Method m : c.getDeclaredMethods()) {
				if (m.getName().equals(method)) {
					if (m.getParameterTypes().length == 2) {
						if (m.getParameterTypes()[0].equals(AbstractGraphHandler.class)) {
							if (m.getParameterTypes()[1].equals(Hashtable.class)) {
								return result = m.invoke(service, agh, table);
							}
						}
					}
				}
			}
		}
		catch (java.lang.InstantiationException e) {
			throw new MNDServiceException("Impossible to call MNDService named " + class_);
		}
		catch (java.lang.ClassNotFoundException e) {
			throw new MNDServiceException("Impossible to call MNDService named " + class_);
		}
		catch (java.lang.NoSuchMethodException e) {
			throw new MNDServiceException("Impossible to call MNDService named " + class_);
		}
		catch (java.lang.IllegalAccessException e) {
			throw new MNDServiceException("Impossible to call MNDService named " + class_);
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			if (e.getCause() instanceof MNDServiceException) {
				throw (MNDServiceException)e.getCause();
			} else {
				throw new MNDServiceException("Impossible to call MNDService named " + class_, e);
			}
		}
		return result;
	}
}
