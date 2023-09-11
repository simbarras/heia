package ch.epfl.general_libraries.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;

public class ReturnKeyListener implements KeyListener {
	
	private JButton button;

	public ReturnKeyListener(JButton ok) {
		button = ok;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\n') {
			button.doClick();
		}	
	}

	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}

}
