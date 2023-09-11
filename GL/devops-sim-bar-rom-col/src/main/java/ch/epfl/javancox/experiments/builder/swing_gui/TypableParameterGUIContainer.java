package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ch.epfl.javancox.experiments.builder.tree_model.TypableChooseNode;

public class TypableParameterGUIContainer extends ParameterGUIContainer {

	private static final long serialVersionUID = 1L;
	JTextField text = null;	
	
	public TypableParameterGUIContainer(final TypableChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		if (text == null) {
			text = new JTextField("Put your value here", 30);
		}
		text.setFont(defaultFont);
		text.setSize(150, 30);
		text.setPreferredSize(new Dimension(150, lineHeight));
		text.setMaximumSize(new Dimension(150, lineHeight));
		text.setBackground(Color.WHITE);
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {}
		});
		text.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.actionPerformed(TypableChooseNode.ADD);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						text.requestFocusInWindow();
					}
					
				});
			}
		});	
		text.getDocument().addDocumentListener(new DocumentListener() {			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				node.setTextValue(text.getText());
			}			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				node.setTextValue(text.getText());			
			}			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				node.setTextValue(text.getText());
			}
		});
		JButton addButton = getAddButton();
		this.add(text, new Placement(100, 250, false));
		this.add(addButton, new Placement(0, 100, false));	
	}
	
}
