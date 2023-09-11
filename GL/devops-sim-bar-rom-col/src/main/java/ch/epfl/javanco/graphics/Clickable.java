package ch.epfl.javanco.graphics;

import ch.epfl.javanco.network.AbstractElementContainer;

public interface Clickable extends Pointable {
	public AbstractElementContainer getElementContainer();
}