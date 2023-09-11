package ch.epfl.javanco.scripting;

import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScriptExecutionException extends Exception {

	public static final long serialVersionUID = 0;
	private String message = null;
	private String lineInfo = null;

	public ScriptExecutionException(Throwable t) {
		super(t);
		message = t.getMessage();
		StackTraceElement[] stack = t.getStackTrace();
		//	t.printStackTrace();
		int stopIndex = 0;
		for (int i = 0 ; i < stack.length ; i++) {
			if (stack[i].getFileName() != null) {
				if (stack[i].getFileName().matches(".*groovy")) {
					lineInfo = "Script encountered an " + t.getClass().getSimpleName() + " at line " + stack[i].getLineNumber();
					stopIndex = i;
					break;
				}
			}
		}
		StackTraceElement[] localStackTrace = new StackTraceElement[stack.length - stopIndex];
		for (int i = stopIndex ; i < stack.length ; i++) {
			localStackTrace[i-stopIndex] = stack[i];
		}
		this.setStackTrace(localStackTrace);
	}

	@Override
	public String getMessage() {
		return lineInfo + " : " + message;
	}

	public void displayInfoDialog() {
		displayInfoDialog(null);
	}

	private int nbOfLines = 0;

	private void addTextLabels(String s, JPanel upPane) {
		if (s != null) {
			String[] words = s.split(" ");
			int cursor = 0;
			while (cursor < words.length) {
				StringBuffer sb = new StringBuffer();
				if (words[cursor].length() >= 90) {
					sb.append(words[cursor]);
				} else {
					while (sb.length() + words[cursor].length() < 90) {
						sb.append(words[cursor] + " ");
						cursor++;
						if (cursor >= words.length) {
							break;
						}
					}
				}
				JLabel lab = new JLabel();
				lab.setText(sb.toString());
				upPane.add(lab);
				nbOfLines++;
			}
		}
	}

	public void displayInfoDialog(Frame f) {
		final JDialog diag = new JDialog(f);
		diag.setSize(600,150);
		diag.setLayout(new java.awt.BorderLayout());
		JPanel upPane = new JPanel();
		JPanel downPane = new JPanel();

		diag.add(upPane, java.awt.BorderLayout.CENTER);
		diag.add(downPane, java.awt.BorderLayout.SOUTH);

		addTextLabels(lineInfo, upPane);
		addTextLabels(message, upPane);
		if (nbOfLines > 5) {
			diag.setSize(600, 150 + 25 * (nbOfLines - 4));
		}
		JButton okButton = new JButton("ok");
		JButton moreButton = new JButton("view full trace");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				diag.setVisible(false);
			}
		});
		moreButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				displayStackDialog(diag);
			}
		});
		downPane.add(okButton);
		downPane.add(moreButton);

		diag.setVisible(true);
	}

	public void displayStackDialog(JDialog diag) {
		StringBuffer sb = new StringBuffer();
		for (StackTraceElement el : this.getCause().getStackTrace()) {
			sb.append(el.toString() + "\r\n");
		}
		final javax.swing.JDialog trace = new javax.swing.JDialog(diag, "Trace");
		trace.setLayout(new java.awt.BorderLayout());
		javax.swing.JTextArea text = new javax.swing.JTextArea(sb.toString());
		javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(text);
		trace.add(scroll, java.awt.BorderLayout.CENTER);

		javax.swing.JButton button = new javax.swing.JButton("ok");
		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				trace.setVisible(false);
			}
		});
		trace.add(button, java.awt.BorderLayout.SOUTH);
		trace.pack();
		trace.setVisible(true);
		//	javax.swing.JOptionPane.showMessageDialog(null, sb.toString());
	}

}