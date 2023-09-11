package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Color;
import java.awt.LayoutManager;

import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ArrayChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.BooleanChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ClassChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.EnumChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ErrorChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.LeafChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.TypableChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.UntypableArrayInstanceNode;

public class LeafNodeGUIContainer extends AbstractGUIContainer {
	
	private static Color lightGreen = new Color(0.8f, 1f, 0.8f);
	private static Color error = new Color(1f, 0.9f, 0.7f);	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public LeafNodeGUIContainer(LeafChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		this.setBackground(lightGreen);			
		refresh();
	}

	@Override
	public void refreshImpl() {
		AbstractChooseNode parent = (AbstractChooseNode)absNode.getParent();
		if (parent instanceof BooleanChooseNode) {
			setIcon("boolean.png");
		} else if (parent instanceof EnumChooseNode) {
			setIcon("class.png");
		} else if (parent instanceof UntypableArrayInstanceNode) {
			setIcon("class.png");
		} else if (parent instanceof ArrayChooseNode){
			setIcon("other.png");
		} else if (parent instanceof ClassChooseNode) {
			setIcon("other.png");
			// sanity check
			if (!(absNode instanceof LeafChooseNode)) {
				throw new IllegalStateException();
			} 
			if (!((LeafChooseNode)absNode).isNull()) {
				if (!(absNode instanceof ErrorChooseNode))
					throw new IllegalStateException();		
				else
					this.setBackground(error);	
			}
		} else {
			TypableChooseNode p = (TypableChooseNode)absNode.getParent();
			if (p.isBoolean()) {
				setIcon("boolean.png");
			} else if (p.isInt()) {
				setIcon("integer.png");
			} else if (p.isClass()) {
				setIcon("class.png");
			} else if (p.isDouble()) {
				setIcon("double.png");
			} else if (p.isLong()) {
				setIcon("long.png");
			} else {
				setIcon("other.png");
			}
		}
	}
	

}

