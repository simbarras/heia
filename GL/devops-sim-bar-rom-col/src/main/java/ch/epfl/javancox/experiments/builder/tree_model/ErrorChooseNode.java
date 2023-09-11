package ch.epfl.javancox.experiments.builder.tree_model;

public class ErrorChooseNode extends LeafChooseNode {


	private static final long serialVersionUID = 1L;

	public ErrorChooseNode(Object obj, ObjectConstuctionTreeModel tree) {
		super(obj, tree);
		checkConfigured();
	}
	
	public String getColor() {
		return "#FF0000";
	}
	
	@Override
	public boolean checkConfiguredRecursive() {
		this.setConfigured(false);
		return false;
	}	

}
