package ch.epfl.javanco.pluggings;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTextField;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class PathColoring extends JavancoTool {
	
	JDialog dia;

	@Override
	public void internalFrameClosing() {
		dia.setVisible(false);
	}

	@Override
	public void run(final AbstractGraphHandler agh, Frame f) {
		if (dia != null) return;
		dia = new JDialog();
		dia.setSize(700,400);
		dia.setLayout(new FlowLayout());
		final JTextField tf1 = new JTextField(40);
		tf1.setColumns(10);
		final JColorChooser colChooser = new JColorChooser();
		dia.add(tf1);
		dia.add(colChooser);
		final JButton b = new JButton("go");
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color c = colChooser.getColor();
				Path path = new Path(tf1.getText());
				for (NodePair np : path.getPathSegments()) {
					LinkContainer lc = agh.getLinkContainer(np);
					if (lc != null) {
						lc.attribute("link_color").setValue(TypeParser.parseColor(c));
					}
				}
			}
		});
		dia.add(b);
		dia.setVisible(true);
	}

}
