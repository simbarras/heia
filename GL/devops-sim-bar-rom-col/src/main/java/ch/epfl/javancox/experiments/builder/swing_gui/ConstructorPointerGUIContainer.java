package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import ch.epfl.javancox.experiments.builder.tree_model.ConstructorNodeChooserPointer;

public class ConstructorPointerGUIContainer extends ConstructorGUIContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConstructorPointerGUIContainer(final ConstructorNodeChooserPointer node, LayoutManager man, int prefix) {
		super(node.superInstance, man, prefix);
		JLabel lab = null;
		for (int i=0; i< getComponentCount(); ++i) {
			Component current = getComponent(i);
			if (current instanceof JLabel) {
				lab = (JLabel)current;
			}
		}
		if (lab != null) {
			String text = lab.getText();
			String newText = "";
			int index = text.indexOf('>', text.indexOf("font")) +1;
			newText += text.substring(0, index);
			newText += "@ ->" + text.substring(index);
			JLabel newLabel = new JLabel(newText);
			add(newLabel);
			final JCheckBox completeCartesian = new JCheckBox("Complete Cartesian Product");
			completeCartesian.setBackground(Color.WHITE);
			completeCartesian.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					node.isCartesianEnabled = completeCartesian.isSelected();
					node.getContainingTreeModel().reloadTree();
				}
			});
			add(completeCartesian);
		}
	}

}
