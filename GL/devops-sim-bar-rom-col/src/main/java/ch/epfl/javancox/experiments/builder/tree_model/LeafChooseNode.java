package ch.epfl.javancox.experiments.builder.tree_model;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import ch.epfl.general_libraries.clazzes.ObjectRecipe;
import ch.epfl.general_libraries.utils.Pair;


public class LeafChooseNode extends AbstractChooseNode {

	private static final long serialVersionUID = -5276221026077034218L;
	private transient boolean isNull = false;

	public LeafChooseNode(Object obj, ObjectConstuctionTreeModel tree) {
		super(tree);
		this.setUserObject(obj);
		if (obj == null) {
			this.isNull = true;
		}
		checkConfigured();		
	}
	
	public String getColor() {
		return "#00AA00";
	}
	
	public String getName() {
		return getUserObject().toString();
	}
	
	public String getText() {
		String value = "";
		if (getUserObject() instanceof Class) {
			value = ((Class)getUserObject()).getSimpleName();
		} else {
			value = isNull() ? "null" : getUserObject().toString();
		}	
		return value;
	}
	
	public boolean isNull() {
		return isNull;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof LeafChooseNode) {
			LeafChooseNode alt = (LeafChooseNode)anObject;
			return (this.getUserObject() == alt.getUserObject()) || this.getUserObject().equals(alt.getUserObject());
		} else {
			return false;
		}
	}
	
	@Override
	public void actionPerformed(String key) 	{
		LeafChooseNode instance = LeafChooseNode.this;
		DefaultTreeModel model = (DefaultTreeModel) instance.getContainingTreeModel();
		try {
			model.removeNodeFromParent(instance);
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			// TOFIX
		}
		instance.removeFromParent();
		instance.removeAllChildren();
		getContainingTreeModel().reloadTree();
	}
	
	public List<ActionItem> getActions() {
		List<ActionItem> l = new ArrayList<ActionItem>(1);	
		l.add(new ActionItem("Suppress value", "suppress"));
		return l;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean checkConfiguredRecursive() {
		this.setConfigured(true);
		return true;
	}
	
	@Override
	public boolean checkConfigured() {
		this.setConfigured(true);
		return true;
	}	

	@Override
	public Object getCurrentObject(){
		return getUserObject();
	}

	@Override
	public int getInstancesCount() {
		return 1;
	}

	@Override
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
				return new Pair<Object, ObjectRecipe>(getUserObject(), null);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return ret;
	}
	
	public DefinitionIterator getDefinitionIterator() {
		return new DefinitionIterator() {
			
			private boolean delivered;

			@Override
			public boolean hasNext() {
				return !delivered;
			}

			@Override
			public AbstractDefinition next() {
				delivered = false;
				return new StringDefinition(getUserObject().toString());
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();		
			}

			@Override
			void reset() {	
				delivered = true;
			}

			@Override
			AbstractDefinition current() {
				if (getUserObject() != null) {
					return new StringDefinition(getUserObject().toString());
				} else {
					return null;
				}
			}

			@Override
			boolean hasFutureNext() {
				return false;
			}
		};
	}		
	
	@Override
	public Object clone() {
		LeafChooseNode ret = new LeafChooseNode(this.getUserObject(), this.getContainingTreeModel());
		return ret;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(this.containingTreeModel);
		if (this.getUserObject() instanceof Serializable || getUserObject() == null) {
			out.writeBoolean(getUserObject() == null);
			out.writeBoolean(this.configured);
			if (getUserObject() != null)
			out.writeObject(this.getUserObject());			
		} else {
			System.err.println(this.getUserObject().getClass().getName() + " Not serializable.");
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.containingTreeModel = (ObjectConstuctionTreeModel<?>)in.readObject();
		boolean isNUll = in.readBoolean();
		this.isNull = isNUll;
		this.setConfigured(in.readBoolean());
		if (isNUll == false)
		this.setUserObject(in.readObject());		
	}

	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		throw new InvalidClassException("Uncompatible class");
	}

	@Override
	public void cleanUp() {

	}
}
