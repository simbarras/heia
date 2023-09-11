package ch.epfl.javancox.experiments.builder.tree_model;

import java.util.Map;

public class EnumChooseNode extends ClassChooseNode {

	private static final long serialVersionUID = 1L;

	protected EnumChooseNode(Class<?> c, Map<String, String> annotationMap,
			ObjectConstuctionTreeModel<?> containingTree, boolean checkDef)
			throws Exception {
		super(c, annotationMap, containingTree, checkDef);
		checkConfigured();
	}


}
