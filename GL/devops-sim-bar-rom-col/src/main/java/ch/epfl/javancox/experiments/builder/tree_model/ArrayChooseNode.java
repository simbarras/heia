package ch.epfl.javancox.experiments.builder.tree_model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ArrayChooseNode extends AbstractParameterChooseNode {
	
	private static final long serialVersionUID = 1L;
	private Class t;

	public ArrayChooseNode(Class<?> c, Map<String, String> annotationMap,
			ObjectConstuctionTreeModel<?> containingTree, boolean checkDef)
					throws Exception {
		super(c, containingTree, annotationMap);
		this.annotationMap = annotationMap;	
		parameterType = parameterType.substring(1);
		if (parameterType.length() == 1) {
			switch (parameterType.charAt(0)) {
			case 'I':
				parameterType = "int";
				t = Integer.TYPE;
				break;
			case 'J':
				parameterType = "long";
				t = Long.TYPE;
				break;
			case 'F':
				parameterType = "float";
				t = Float.TYPE;
				break;
			case 'D':
				parameterType = "double";
				t = Double.TYPE;
				break;
			case 'S':
				parameterType = "short";
				t = Short.TYPE;
				break;	
			case 'C':
				parameterType = "char";
				t = Character.TYPE;
				break;
			case 'Z':
				parameterType = "boolean";
				t = Boolean.TYPE;
				break;
			}
		} else {
			parameterType = parameterType.substring(1, parameterType.length() - 1);
			t = Class.forName(parameterType);
		}
		addLeaf(checkDef);
		checkConfigured();
	}
	
	private void addLeaf(boolean checkDef) throws Exception {
		if (isTypableType()) {
			this.add(new TypableArrayInstanceNode(t, annotationMap, containingTreeModel, checkDef));			
		} else {
			this.add(new UntypableArrayInstanceNode(Class.forName(parameterType), containingTreeModel, annotationMap, checkDef));
		}
	}
	
	public String getText() {
		return "Array of " + super.getText();
	}

	@Override
	public List<ActionItem> getActions() {
		List<ActionItem> toReturn = new ArrayList<ActionItem>();
		ActionItem addNew = new ActionItem("Create new array", "new");
		ActionItem addNull = new ActionItem("Use null", "null");		
		ActionItem remove = new ActionItem("Remove all", ArrayChooseNode.REMOVE_ALL);
		toReturn.add(addNew);
		toReturn.add(addNull);		
		toReturn.add(new SeparatorItem());
		toReturn.add(remove);
		return toReturn;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void actionPerformed(String key) {
		if (key.equals("new")) {
			try {
				addLeaf(false);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} else if (key.equals("null")) {
			add(new LeafChooseNode(null, getContainingTreeModel()));
		} else {
			removeAllChildren();
		}
		getContainingTreeModel().reloadTree();
	}

	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject, Map<String, String> annotationMap2,
			ObjectConstuctionTreeModel<?> containingTreeModel, boolean b) throws Exception {
		// TODO Auto-generated method stub
		return new ArrayChooseNode(userObject, annotationMap2, containingTreeModel, b);
	}
	
	/*	private LeafChooseNode newArrayObjectLeaf(Object toAdd, boolean inArray) {
	LeafChooseNode newNode;
	if (inArray) {
		newNode = new LeafChooseNode(toAdd, this.getContainingTreeModel());
	} else {
		try {
			if (this.getChildCount() > 0) {
				Object array = ((LeafChooseNode) this.children.get(0)).getUserObject();
				int arrayLength = Array.getLength(array);					
				Object newArray = Array.newInstance(Class.forName(parameterType),arrayLength + 1);
				for (int i = 0; i < arrayLength; ++i) {
					Array.set(newArray, i, Array.get(array, i));
				}
				Array.set(newArray, arrayLength, toAdd);
				this.remove((LeafChooseNode) this.children.get(0));
				newNode = new LeafChooseNode(newArray, this.getContainingTreeModel());
			} else {
				Object array = Array.newInstance(Class.forName(parameterType), 1);
				Array.set(array, 0, toAdd);
				newNode = new LeafChooseNode(array, this.getContainingTreeModel());
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	if (this.getChildCount() == 0 || !this.children.contains(newNode)) {
		this.add(newNode);
	}
	return newNode;
}*/

/*	private LeafChooseNode newArrayConstructor(Constructor<?> c) {	
LeafChooseNode newNode;
if (this.getChildCount() > 0) {
	Object array = ((LeafChooseNode) this.children.get(0)).getUserObject();
	int arrayLength = Array.getLength(array);					
	Object newArray = Array.newInstance(Constructor.class,arrayLength + 1);
	for (int i = 0; i < arrayLength; ++i) {
		Array.set(newArray, i, Array.get(array, i));
	}
	Array.set(newArray, arrayLength, c);
	this.remove((LeafChooseNode) this.children.get(0));
	newNode = new LeafChooseNode(newArray, this.getContainingTreeModel());
} else {
	Object array = Array.newInstance(Constructor.class, 1);
	Array.set(array, 0, c);
	newNode = new LeafChooseNode(array, this.getContainingTreeModel());
}
if (this.getChildCount() == 0 || !this.children.contains(newNode)) {
	this.add(newNode);
}
return newNode;		
}*/	

}
