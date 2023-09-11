package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;

import ch.epfl.javancox.experiments.builder.tree_model.BooleanChooseNode;

public class BooleanParameterGUIContainer extends ParameterGUIContainer {

	private static final long serialVersionUID = 1L;

	public BooleanParameterGUIContainer(final BooleanChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		final JCheckBox choice = new JCheckBox();
		choice.setSelected(false);
		choice.setPreferredSize(new Dimension(60, lineHeight));
		choice.setBackground(this.getBackground());
		choice.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.setValue(choice.isSelected());
			//	node.actionPerformed(BooleanChooseNode.CHECKBOX);
			}
		});
		choice.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
		//		System.out.println("uiehr");
		//		node.setValue(choice.isSelected());
			}
		});
		add(choice, new Placement(100, 130, false));
		add(getAddButton(), new Placement(0, 100, false));		
	}

}
