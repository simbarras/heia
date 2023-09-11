package ch.epfl.javancox.experiments.builder.tree_model;

import java.util.Map;


public class BooleanChooseNode extends AbstractParameterChooseNode {
	
	private static final long serialVersionUID = 1L;

	public static final String CHECKBOX = "checkbox";
	
	private boolean current;

	public BooleanChooseNode(Class<?> c, Map<String, String> annotationMap,
			ObjectConstuctionTreeModel<?> containingTree, boolean checkDef)
					throws Exception {
		super(c, containingTree, annotationMap);
		String def = annotationMap.get("ParamName.default_");
		if (def != null) {
			addBoolean(Boolean.parseBoolean(def));		
		}	
		checkConfigured();
	}
		
	private void addBoolean(boolean bol) {
		LeafChooseNode newNode = new LeafChooseNode(bol, this.getContainingTreeModel());
		if (this.getChildCount() == 0 || !this.children.contains(newNode)) {
			this.add(newNode);
		}
	}
	
	public void setValue(boolean b) {
		current = b;
	}	

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void actionPerformed(String key) {
		if (key.equals(REMOVE_ALL)) {
			this.removeAllChildren();
		} else if (key == CHECKBOX) {
			current = !current;
		} else {
			addBoolean(current);

		//	this.getContainingTreeModel().getTreeModelUIManager().expandPath(new TreePath(this.getPath()));
		//	if (this.getChildCount() > 0) {
		//		for (Object c : this.children) {
		//			this.getContainingTreeModel().getTreeModelUIManager().expandPath(new TreePath(((AbstractChooseNode) c).getPath()));
		//		}
		//	}
		}
		getContainingTreeModel().reloadTree();
	}

	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject, Map<String, String> annotationMap2,
			ObjectConstuctionTreeModel<?> containingTreeModel, boolean b)
			throws Exception {
		return new BooleanChooseNode(userObject, annotationMap2, containingTreeModel, b);
	}



}
