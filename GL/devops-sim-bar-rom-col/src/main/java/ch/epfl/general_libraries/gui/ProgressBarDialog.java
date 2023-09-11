package ch.epfl.general_libraries.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private int steps;
	private int current = 0;
	private JProgressBar progressBar;
	
	public ProgressBarDialog(int steps) {
		this.steps = steps;
		this.setSize(300, 100);
		this.setTitle("Progress");
		this.progressBar = new JProgressBar(0, steps);
		progressBar.setSize(220, 70);
		progressBar.setPreferredSize(new Dimension(220, 60));
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(progressBar);
		this.setContentPane(panel);
	}
	
	public void incrementProgression() {
		current++;
		progressBar.setValue(current);
		if (current == steps) {
			this.setVisible(false);
		}
	}
	
	public ProgressBarDialog setDialogVisible() {
		setLocationRelativeTo(null);
		super.setVisible(true);
		return this;
	}

}
