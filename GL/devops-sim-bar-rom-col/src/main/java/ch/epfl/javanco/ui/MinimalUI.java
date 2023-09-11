package ch.epfl.javanco.ui;

import ch.epfl.javanco.base.AbstractGraphHandler;

public class MinimalUI  extends AbstractUI {

	public MinimalUI(AbstractGraphHandler handler) {
		super(handler);
		handler.addUIRefreshEventListener(this);
	}

	protected void repaintElements() {}

	protected void refreshAndRepaintElements() {}

	public void refreshAndRepaintAllDisplay() {}

	/**
	 * 
	 */
	@Override
	public void displayWarning(String s) {
		System.out.println(s);
	}
	/**
	 * Void implementation to override
	 */
	@Override
	public void displayWarning(String s, Throwable e) {
		System.out.println(s);
		e.printStackTrace(System.out);
	}

}