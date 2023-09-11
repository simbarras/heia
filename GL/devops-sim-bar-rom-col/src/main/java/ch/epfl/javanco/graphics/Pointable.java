package ch.epfl.javanco.graphics;

import ch.epfl.javanco.network.AbstractElementContainer;

public interface Pointable {
	public AbstractElementContainer getElementContainer();
	public void setPointed(boolean b);
}