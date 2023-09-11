package ch.epfl.javanco.ui.swing;

import javax.swing.JMenuItem;

public interface InteractiveElement {

	public boolean hasContextualMenu();
	public JMenuItem getElementMenu();
}
