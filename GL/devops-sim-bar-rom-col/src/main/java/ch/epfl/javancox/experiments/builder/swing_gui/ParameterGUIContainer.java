package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractParameterChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.TypableChooseNode;

public class ParameterGUIContainer extends AbstractGUIContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Color lightBlue = new Color(0.92f, 0.92f, 0.97f);
	

	public ParameterGUIContainer(final AbstractParameterChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);

		refresh();
		this.setBackground(lightBlue);		
		if (node.isRoot()) {
			JButton save = new JButton("Save");
			ActionListener a = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					node.getContainingTreeModel().saveFile();
				}
			};
			save.setFocusable(false);
			save.addActionListener(a);
			
			JButton saveAs = new JButton("Save as...");
			a = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String s = JOptionPane.showInputDialog("Give a file name", "tree");
					node.getContainingTreeModel().saveFile(s);
				}
			};
			saveAs.setFocusable(false);
			saveAs.addActionListener(a);	
			
			JButton deploy = new JButton("Deploy tree");
			a = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					node.setAllExpanded();
				}
			};
			deploy.setFocusable(false);
			deploy.addActionListener(a);
			
			add(save,new Placement(200, 300, false));
			add(saveAs,new Placement(100, 200, false));
			add(deploy,new Placement(0, 100, false));
		}
		setMaximumSize(new Dimension(3000,lineHeight));
	}
	
	protected JButton getAddButton() {
		JButton add = new JButton("Add");
		add.setPreferredSize(new Dimension(60, 20));
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				absNode.actionPerformed(TypableChooseNode.ADD);
			}
		});
		add.setFocusable(false);
		return add;
	}

	@Override
	public void refreshImpl() {
		if (absNode.getChildCount() > 0) {
			if (absNode.isExpanded()) {
				super.setIcon("openfolder.png");
			} else {
				super.setIcon("closedfolder.png");
			}
		} else {
			super.setIcon("void.png");
		}
	}
}
