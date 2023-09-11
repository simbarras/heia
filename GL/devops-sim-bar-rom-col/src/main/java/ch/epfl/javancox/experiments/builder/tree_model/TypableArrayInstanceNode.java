package ch.epfl.javancox.experiments.builder.tree_model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ObjectRecipe;
import ch.epfl.general_libraries.utils.Pair;

public class TypableArrayInstanceNode extends TypableChooseNode {

	private static final long serialVersionUID = 1L;

	public TypableArrayInstanceNode(Class<?> c,
			Map<String, String> annotationMap,
			ObjectConstuctionTreeModel<?> containingTree, boolean checkDef)
			throws ClassNotFoundException {
		super(c, annotationMap, containingTree, checkDef);
		checkConfigured();
	}
	
	@Override
	public int getInstancesCount() {
		return 1;
	}
	
	@Override
	public String getColor() {
		return "#00DD00";
	}
	
	@Override
	public String getText() {
		return "Array of type " + parameterType + " with " + getChildCount() + " elements";
	}
	
	public Iterator<Pair<Object, ObjectRecipe>> iterator() {
		Iterator<Pair<Object, ObjectRecipe>> ret = new Iterator<Pair<Object, ObjectRecipe>>() {

			private boolean delivered = false;

			@Override
			public boolean hasNext() {
				return !delivered;
			}

			@Override
			public Pair<Object, ObjectRecipe> next() {
				this.delivered = true;
				return new Pair<Object, ObjectRecipe>(createArray(), null);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return ret;
	}
	
	protected Object createArray() {
		Object newArray = Array.newInstance((Class<?>)getUserObject(), getChildCount());
		int index = 0;
		for (AbstractChooseNode child : getChilds()) {
			Array.set(newArray, index, child.getUserObject());
			index++;
		}
		return newArray;
	}

	@Override
	public boolean checkConfiguredRecursive() {
		this.configured = true;
		return true;
	}
	
	@Override
	public List<ActionItem> getActions() {
		List<ActionItem> toReturn = new ArrayList<ActionItem>();
		toReturn.add(new ActionItem("Suppress this array", "suppress"));			
		toReturn.add(new ActionItem("Empty this array", REMOVE_ALL));
		return toReturn;
	}	
	
	@Override
	public void actionPerformed(String key) {
		if (key.equals("suppress")) {
			((AbstractChooseNode)this.getParent()).removeChild(this);
			containingTreeModel.reloadTree();			
		} else {
			super.actionPerformed(key);
		}
	}
	
	protected void addLeaf(Object toAdd) {
		LeafChooseNode newNode = new LeafChooseNode(toAdd, this.getContainingTreeModel());
		this.add(newNode);	
	}	
	
	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject, Map<String, String> annotationMap2,
			ObjectConstuctionTreeModel<?> containingTreeModel, boolean b)
			throws Exception {
		return new TypableArrayInstanceNode(userObject, annotationMap2, containingTreeModel, false);
	}	



}
