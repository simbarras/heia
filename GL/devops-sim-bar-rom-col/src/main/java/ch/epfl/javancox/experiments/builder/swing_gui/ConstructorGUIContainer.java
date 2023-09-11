package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Color;
import java.awt.LayoutManager;
import ch.epfl.javancox.experiments.builder.tree_model.ConstructorChooseNode;

public class ConstructorGUIContainer extends AbstractGUIContainer {

	private static Color lightYellow = new Color(1f, 1f, 0.8f);

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	


	public ConstructorGUIContainer(ConstructorChooseNode node, LayoutManager man, int prefix) {
		super(node, man, prefix);
		this.setBackground(lightYellow);		
		refresh();
	}

	@Override
	public void refreshImpl() {
		super.setIcon("constructoricon.png");
	}
}
