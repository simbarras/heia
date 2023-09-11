package ch.epfl.javancox.results_manager.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CustomWindowListener implements WindowListener {
	
	ComplexDisplayPanel main;
	
	
	public CustomWindowListener(ComplexDisplayPanel main) {
		this.main = main;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		main.disposeLegend();
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

}
