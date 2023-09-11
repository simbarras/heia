package ch.epfl.javanco.base;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.javanco.event.GraphCreationListener;
import ch.epfl.javanco.io.JavancoClassesLoader;

/**
 *
 */
public class GraphHandlerFactory {

	private JavancoClassesLoader acl = null;
	private Class<? extends AbstractGraphHandler> graphHandlerClass = null;
	private List<GraphCreationListener> graphCreationListeners = new ArrayList<GraphCreationListener>();

	public GraphHandlerFactory(Class<? extends AbstractGraphHandler> graphHandlerClass, JavancoClassesLoader acl) {
		this.graphHandlerClass = graphHandlerClass;
		this.acl = acl;
	}

	public GraphHandlerFactory(Class<? extends AbstractGraphHandler> graphHandlerClass) {
		this(graphHandlerClass, null);
	}

	public void recycleGraphHandler(AbstractGraphHandler agh) {
		fireGraphDeletedEvent(agh);
	}
	
	public void registerAgh(AbstractGraphHandler agh) {
		fireGraphRegisteredEvent(agh);
	}
	
	/**
	 * This method allows creating a graph WITHOUT notifying any gui about its creation.
	 * So obtained AGHs can later be notified to the gui using the <code>registerAgh()</code>
	 * method
	 * 
	 * @param notifyListeners
	 * @return
	 */
	public AbstractGraphHandler getNewGraphHandler(boolean notifyListeners) {
		return getNewGraphHandler(this.graphHandlerClass, notifyListeners);
	}

	/**
	 * Creates a new AGHs using the default implementation and registers it to any GUI
	 * @return
	 */
	public AbstractGraphHandler getNewGraphHandler() {
		return getNewGraphHandler(this.graphHandlerClass, true);
	}

	public AbstractGraphHandler getNewGraphHandler(Class<? extends AbstractGraphHandler> cl, boolean notify) {
		try {
			AbstractGraphHandler gh = cl.getConstructor(new Class[]{this.getClass()}).newInstance(new Object[]{this});
			if (notify)
				fireNewGraphCreatedEvent(gh);
			return gh;
		}
		catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
		catch (InstantiationException e) {
			throw new IllegalStateException(e);
		}
		catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}
	
	void fireGraphRegisteredEvent(AbstractGraphHandler agh) {
		for (GraphCreationListener listener : graphCreationListeners) {
			listener.graphRegistered(agh);
		}		
	}

	void fireGraphNameChangedEvent(AbstractGraphHandler agh) {
		for (GraphCreationListener listener : graphCreationListeners) {
			listener.graphRenamed(agh);
		}
	}

	private void fireNewGraphCreatedEvent(AbstractGraphHandler handler) {
		for (GraphCreationListener listener : graphCreationListeners) {
			listener.graphCreated(handler);
		}
	}

	private void fireGraphDeletedEvent(AbstractGraphHandler handler) {
		for (GraphCreationListener listener : graphCreationListeners) {
			listener.graphDeleted(handler);
		}
	}

	public void addGraphCreationListener(GraphCreationListener listener) {
		graphCreationListeners.add(listener);
	}

	public void removeGraphCreationListener(GraphCreationListener listener) {
		graphCreationListeners.remove(listener);
	}

	public JavancoClassesLoader getJavancoClassesLoader() {
		if (acl != null) {
			acl.setupLists();
		}
		return acl;
	}

}