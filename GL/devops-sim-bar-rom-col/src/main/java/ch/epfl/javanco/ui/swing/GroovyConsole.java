package ch.epfl.javanco.ui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.ui.GlobalInterface;


public class GroovyConsole implements KeyListener {

	private GlobalInterface assosInterface = null;
	private JFrame actuallyOpen = null;
	private JTextPane actualPane = null;
	private JTextPane outputPane = null;

	public GroovyConsole(GlobalInterface assosInterface) {
		this.assosInterface = assosInterface;
	}

	public void setVisible(boolean vol) {
		if (vol) {
			actuallyOpen = new JFrame("GroovyEditor");
			actuallyOpen.setSize(600,400);

			DefaultStyledDocument doc = new DefaultStyledDocument();
			// FIX-ME (issue #1) : needed to disable this filtering because GroovyFilter not available. Investigate why
			//doc.setDocumentFilter(new GroovyFilter(doc));

			actualPane = new JTextPane(doc);
			outputPane = new JTextPane();

			actualPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
			actualPane.setEditable(true);
			actualPane.addKeyListener(this);

			outputPane.setEditable(false);
			outputPane.setBackground(Color.LIGHT_GRAY);
			outputPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

			JSplitPane sPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, actualPane, new JScrollPane(outputPane));

			actuallyOpen.setContentPane(sPane);
			actuallyOpen.setVisible(true);
		} else {
			actuallyOpen.setVisible(false);
			actualPane.removeKeyListener(this);
			actuallyOpen = null;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
		try {
			if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0) {
				if (e.getKeyCode() == KeyEvent.VK_R) {
					AbstractGraphHandler agh = assosInterface.getCurrentlyActiveAgh();
					if (agh != null) {
						outputPane.setText(agh.getGroovyScriptManager().executeStatement(actualPane.getText()));

					}
				}
			}
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(actuallyOpen, ex);
		}
	}
	public void keyPressed(KeyEvent e) {

	}
}
