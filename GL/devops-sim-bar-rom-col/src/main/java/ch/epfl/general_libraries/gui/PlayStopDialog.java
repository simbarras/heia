package ch.epfl.general_libraries.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

public class PlayStopDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton pause;
	JButton stop;
	boolean waiting = false;
	boolean arrived = false;
	boolean stopB = false;
	
	public PlayStopDialog(Frame owner) {
		super(owner, false);
		pause = new JButton("Pause");
		stop = new JButton("Stop");
		this.setLayout( new java.awt.GridLayout(2,2));
		this.add(pause);
		this.add(stop);
		this.setSize(400,120);
		this.setTitle("Play-Stop");
		this.setVisible(true);
		pause.addActionListener(this);
		stop.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(pause)) {
			if (waiting == false) {
				waiting = true;
				pause.setEnabled(false);
			}
			if (arrived == true) {
				synchronized (pause) {
					pause.notifyAll();
				}
				waiting = false;
				pause.setText("Pause");
			}				
		}
		if (e.getSource().equals(stop)) {
			stopB = true;
			if (arrived == true) {
				synchronized (pause) {
					pause.notifyAll();
				}
			}				
		}
	}
	
	public boolean waitFor() {
		pause.setEnabled(true);
		pause.setText("Continue");
		arrived = true;	
		waiting = true;	
		try {
			synchronized (pause) {
				pause.wait();
			}
		}
		catch (Exception e) {}
		arrived = false;
		return stopB;
	}		




}
