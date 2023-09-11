package ch.epfl.javanco.event;

import java.util.EventListener;

import ch.epfl.javanco.base.AbstractGraphHandler;

public interface GraphCreationListener extends EventListener {

	public void graphCreated(AbstractGraphHandler agh);
	public void graphDeleted(AbstractGraphHandler agh);
	public void graphRenamed(AbstractGraphHandler agh);
	public void graphRegistered(AbstractGraphHandler agh);
}
