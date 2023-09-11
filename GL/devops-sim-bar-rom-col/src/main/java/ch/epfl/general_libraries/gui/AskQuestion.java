package ch.epfl.general_libraries.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class AskQuestion {
	
	public static String askString(String s) {
		return JOptionPane.showInputDialog(s); 
	}

	public static boolean askQuestion(String s) {
		return (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, s, s, JOptionPane.YES_NO_OPTION));
	}

	public static class LDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LDialog(JDialog d, String s) {
			super(d,s);
		}

		boolean returnVal;
	}

	public static boolean askQuestion(String s1, String s2) {
		final LDialog d = new LDialog(null, "Question");

		d.setLayout(new java.awt.FlowLayout());

		JButton b1 = new JButton(s1);
		b1.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				synchronized (d) {
					d.returnVal = true;
					d.setVisible(false);
					d.notifyAll();
				}
			}
		});

		JButton b2 = new JButton(s2);
		b2.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent ef) {
				synchronized (d) {
					d.returnVal = false;
					d.setVisible(false);
					d.notifyAll();
				}
			}
		});

		d.add(b1);
		d.add(b2);

		d.setSize(200,80);
		d.setLocationByPlatform(true);
		d.setVisible(true);
		try {
			synchronized (d) {
				d.wait();
			}
		}
		catch (InterruptedException e) {
		}

		return d.returnVal;

	}



}
