package ch.epfl.javanco.graphics;

import ch.epfl.javanco.network.AbstractElementContainer;

public class PaintableObject implements Pointable {
	AbstractElementContainer elC;
	boolean pointed;

	protected PaintableObject (AbstractElementContainer cont) {
		elC = cont;
	}

	public AbstractElementContainer getElementContainer() {
		return elC;
	}
	
	public void setPointed(boolean b) {
		this.pointed = b;
	}	
}