package ch.epfl.javancox.experiments.builder.tree_model;

import java.lang.reflect.Array;
import java.util.Map;

import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.TypeParser;

public class TypableChooseNode extends AbstractParameterChooseNode {

	private static final long serialVersionUID = 1L;
	
	private String textValue = "";
	
	public TypableChooseNode(Class<?> c, Map<String, String> annotationMap,
			ObjectConstuctionTreeModel<?> containingTree, boolean checkDef)
					throws ClassNotFoundException {
		super(c, containingTree, annotationMap);
		String def = annotationMap.get("ParamName.default_");
		if (def != null) {
			textValue = def;
			if (this.checkValues()) {
				this.addValue();
				this.setExpanded(false);
			}
		}
		checkConfigured();		
	}
	
	public void setTextValue(String t) {
		textValue = t;
	}
	
	private void addValue() {
		Pair<Object, Boolean> toAddPair = this.getAddedObject();
		Object toAdd = toAddPair.getFirst();
		boolean inArray = toAddPair.getSecond();
		if (inArray) {
			for (int i = 0; i < Array.getLength(toAdd); ++i) {
				addLeaf(Array.get(toAdd, i));
			}
		} else {
			addLeaf(toAdd);
		}
	}
	
	protected void addLeaf(Object toAdd) {
		LeafChooseNode newNode = new LeafChooseNode(toAdd, this.getContainingTreeModel());
		if (this.getChildCount() == 0 || !this.children.contains(newNode)) {
			this.add(newNode);
		}		
	}
	
	public boolean isInt() {
		return parameterType.equals("int") || parameterType.equals("java.lang.Int");
	}
	
	public boolean isLong() {
		return parameterType.equals("long")  || parameterType.equals("java.lang.Long");		
	}
	
	public boolean isShort() {
		return parameterType.equals("short")  || parameterType.equals("java.lang.Short");		
	}	
	
	public boolean isFloat() {
		return parameterType.equals("float")  || parameterType.equals("java.lang.Float");		
	}
	
	public boolean isDouble() {
		return parameterType.equals("double")  || parameterType.equals("java.lang.Double");		
	}	
	
	public boolean isBoolean() {
		return parameterType.equals("boolean")  || parameterType.equals("java.lang.Boolean");		
	}	
	
	public boolean isChar() {
		return parameterType.equals("char")  || parameterType.equals("java.lang.Char");		
	}
	
	public boolean isString() {
		return parameterType.equals("java.lang.String");		
	}
	
	public boolean isClass() {
		return parameterType.equals("java.lang.Class");		
	}

	private Pair<Object, Boolean> getAddedObject() {
		Object toAdd = null;
		boolean inArray = true;
		if (isInt()) {
			toAdd = TypeParser.parseInt(textValue);
		} else if (isLong()) {
			toAdd = TypeParser.parseLong(textValue);
		} else if (isFloat()) {
			toAdd = TypeParser.parseFloat(textValue);
		} else if (isShort()) {
			toAdd = TypeParser.parseShort(textValue);
		} else if (isDouble()) {
			toAdd = TypeParser.parseDouble(textValue);
		} else if (isChar()) {
			toAdd = textValue.charAt(0);
			inArray = false;
		} else if (isString()) {
			toAdd = textValue;
			inArray = false;
		} else if (isBoolean()) {
			toAdd = Boolean.parseBoolean(textValue);
			inArray = false;
		} else if (isClass()) {
			try {
				toAdd = Class.forName(textValue);
			} catch (Exception e) {
			}
			inArray = false;
		} else {
			try {
				toAdd = Class.forName(parameterType);
			} catch (ClassNotFoundException e) {
			}
			inArray = false;
		}
		return new Pair<Object, Boolean>(toAdd, inArray);
	}	

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void actionPerformed(String key) {
		if (key.equals(REMOVE_ALL)) {
			this.removeAllChildren();
		} else {
			if (textValue == null) return;
			if (this.checkValues()) {
				this.addValue();
			}
		}
		getContainingTreeModel().reloadTree();
	}
	
	public boolean checkValues() {
		try {
			if (this.parameterType.equals("int")) {
				TypeParser.parseInt(textValue);
			} else if (this.parameterType.equals("long")) {
				TypeParser.parseLong(textValue);
			} else if (this.parameterType.equals("short")) {
				TypeParser.parseShort(textValue);		
			}else if (this.parameterType.equals("float") || this.parameterType.equals("java.lang.Float")) {
				TypeParser.parseFloat(textValue);
			} else if (this.parameterType.equals("double") || this.parameterType.equals("java.lang.Double")) {
				TypeParser.parseDouble(textValue);
			} else if (this.parameterType.equals("char")) {
				if (textValue.length() == 1) {
				} else {
					textValue = "Invalid Input";
				}	
			} else if (this.parameterType.equals("java.lang.String")) {
			} else if (this.parameterType.equals("java.lang.Class")) {
				Class.forName(textValue);
			}
		}
		catch (Exception e) {
			textValue = "Invalid input";
			return false;
		}
		return true;
	}	

	@Override
	protected AbstractParameterChooseNode paremeterChooseNodeClone(
			Class<?> userObject, Map<String, String> annotationMap2,
			ObjectConstuctionTreeModel<?> containingTreeModel, boolean b)
			throws Exception {
		return new TypableChooseNode(userObject, annotationMap2, containingTreeModel, b);
	}



}
