package ch.epfl.javanco.event;

import java.util.EventObject;

import ch.epfl.javanco.base.AbstractGraphHandler;


public class GraphCreationEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GraphCreationEvent(AbstractGraphHandler handler) {
		super(handler);
	}
	public AbstractGraphHandler getGraphHandler() {
		return (AbstractGraphHandler)super.getSource();
	}
}
