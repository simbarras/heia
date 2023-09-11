package ch.epfl.javanco.pluggings;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class HighlightPath extends JavancoTool {

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(final AbstractGraphHandler agh, Frame f) {
		JDialog d = new JDialog();
		d.setSize(400,100);
		final JTextField tf1 = new JTextField();
		final JTextField tf2 = new JTextField();
		tf1.setColumns(10);
		tf2.setColumns(10);
		final JButton show = new JButton("Highlight path");
		show.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				for (LinkContainer lc : agh.getLinkContainers()) {
					lc.attribute(XMLTagKeywords.LINK_COLOR).setValue("0,0,0");
				}
				
				Path p = BFS.getShortestPath(agh, Integer.parseInt(tf1.getText()), Integer.parseInt(tf2.getText()));
				if (p == null) return;
				for (LinkContainer lc : agh.getLinkContainers(p)) {
					lc.attribute(XMLTagKeywords.LINK_COLOR).setValue("255,0,0");
				}
			}
		});
		d.setLayout(new FlowLayout());
		d.add(tf1);
		d.add(tf2);
		d.add(show);
		d.setVisible(true);

	}

}
