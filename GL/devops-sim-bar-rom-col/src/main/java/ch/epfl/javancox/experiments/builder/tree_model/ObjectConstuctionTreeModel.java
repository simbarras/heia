package ch.epfl.javancox.experiments.builder.tree_model;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.clazzes.ClassUtils;
import ch.epfl.general_libraries.clazzes.ObjectRecipe;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.javancox.experiments.builder.swing_gui.InstanceDynamicTreeListener;

public class ObjectConstuctionTreeModel<X> extends DefaultTreeModel implements Serializable {

	private static transient Logger logger = new Logger(ObjectConstuctionTreeModel.class);
	
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_FILE_NAME = "tree.conf";

	private transient ClassRepository classRepo;
	private transient List<InstanceDynamicTreeListener> listeners = new ArrayList<InstanceDynamicTreeListener>(1);
	private transient Map<ConstructorChooseNode, Integer> configuredConstructors = new HashMap<ConstructorChooseNode, Integer>();
	private transient int nextConstructorIndex = 0;
	private transient boolean ready = false;
	
	private transient TreeModelUIManager toUI;
	
	public static interface TreeModelUIManager {
//		void expandPath(TreePath treePath);
//		boolean isExpanded(TreePath treePath);
		void showErrorMessage(String string);
		void removeNode(AbstractChooseNode node);
		void refresh();
	}

	public static <W> ObjectConstuctionTreeModel<W> loadFromFile(ClassRepository classRepo, Class<? extends W> template) throws Exception {
		return loadFromFile(classRepo, template, DEFAULT_FILE_NAME);
	}
	
	public static <W> ObjectConstuctionTreeModel<W> loadFromFile(ClassRepository classRepo, Class<? extends W> template, String s) throws Exception {
		if (s == null) s = DEFAULT_FILE_NAME;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(s));
			Object or = in.readObject();
			if (or instanceof ObjectConstuctionTreeModel<?>) {
				@SuppressWarnings("unchecked")
				ObjectConstuctionTreeModel<W> read = (ObjectConstuctionTreeModel<W>)or;
				in.close();
				read.classRepo = classRepo;				
				read.check();
				return read;	
			}
			in.close();
			throw new Exception("Wrong format");
		}
		catch (FileNotFoundException e) {
			System.out.println("Impossible to read file " + new File(s).getAbsolutePath());
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}	
	}		
	
	public ObjectConstuctionTreeModel(Class<? extends X> newClass, ClassRepository classRepo) throws Exception {
		super(new DefaultMutableTreeNode());
		logger.trace("Initialisation of the configuration tree");
		configuredConstructors = new HashMap<ConstructorChooseNode, Integer>();
		this.classRepo = classRepo;
		ClassChooseNode ccn = new ClassChooseNode(newClass, AbstractChooseNode.parseAnnotations(newClass.getAnnotations()), this, false);		
		this.setRoot(ccn);
	}
	
	@Override
    public void removeNodeFromParent(MutableTreeNode node) {
		super.removeNodeFromParent(node);
		toUI.removeNode((AbstractChooseNode)node);
	}
	
	private void check() {
		getRootChooseNode().removeInvalidsRecursive();
		getRootChooseNode().checkConfiguredRecursive();
	}
	
	public void setTreeModelUIManager(TreeModelUIManager ui) {
		this.toUI = ui;
	}
	
	public TreeModelUIManager getTreeModelUIManager() {
		return toUI;
	}
	
	public void saveFile() {
		saveFile(DEFAULT_FILE_NAME);
	}

	public void saveFile(String file) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(this);
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			toUI.showErrorMessage("File error!\nUnable to save file.");
		}
	}	
	
	public boolean isReady() {
		return ready;
	}
	
	private AbstractChooseNode getRootChooseNode() {
		return (AbstractChooseNode) getRoot();
	}
	
	protected boolean containsNode(AbstractChooseNode node) {
		return getRootChooseNode().containsNode(node);
	}

	public void addInstanceDynamicTreeListener(InstanceDynamicTreeListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<InstanceDynamicTreeListener>(1);
		}
		this.listeners.add(listener);
		getRootChooseNode().checkConfiguredRecursive();
	}	
	
	public void reloadTree() {
		try{
			getRootChooseNode().checkConfiguredRecursive();
	//		Collection<AbstractChooseNode> temp = this.getExpandedNodes();
			reload();
			toUI.refresh();
		} catch (NullPointerException e) {
			throw new IllegalStateException(e);
		}		
	}
	
	protected Collection<AbstractChooseNode> getExpandedNodes() {
	//	Set<AbstractChooseNode> ret = new HashSet<AbstractChooseNode>();
		return getRootChooseNode().getAllExpandedChildren();
	}	

	protected void setExpandedNodes(Collection<AbstractChooseNode> nodes) {
		for (AbstractChooseNode node : getRootChooseNode().getAllChildren()) {
			if (nodes.contains(node)) {
				node.setExpanded(true, false);
			} else {
				node.setExpanded(false, false);
			}
		}
	}	
	
	protected void readyStateChanged() {
		if (listeners == null) return;
		for (InstanceDynamicTreeListener listener : this.listeners) {
			if (getRootChooseNode().isConfigured()) {
				ready = true;
				listener.treeReadyAction(new ActionEvent(this, (int) System.currentTimeMillis(), "Tree ready"));
			} else if (isReady()) {
				listener.treeNotReadyAction(new ActionEvent(this, (int) System.currentTimeMillis(), "Tree not ready"));
				ready = false;
			}
		}
	}	
	
	protected int addConfiguredConstructor(ConstructorChooseNode toAdd) {
		++this.nextConstructorIndex;
		if (configuredConstructors == null) {
			// can be null after deserialization
			configuredConstructors = new HashMap<ConstructorChooseNode, Integer>();
		}
		this.configuredConstructors.put(toAdd, this.nextConstructorIndex);
		return nextConstructorIndex;
	}

	protected void removeConsrtuctor(ConstructorChooseNode toRemove) {
		if (configuredConstructors == null) {
			// can be null after deserialization
			configuredConstructors = new HashMap<ConstructorChooseNode, Integer>();
		}		
		this.configuredConstructors.remove(toRemove);
	}

	protected Map<ConstructorChooseNode, Integer> getConfiguredConstructors() {
		if (configuredConstructors == null) {
			// can be null after deserialization
			configuredConstructors = new HashMap<ConstructorChooseNode, Integer>();
		}		
		return this.configuredConstructors;
	}

	protected List<Class<?>> getHeritedClasses(Class<?> c) throws Exception {
		logger.debug("Looking for classes extending " + c.getSimpleName() + "...");
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(c);
		for (Class<?> cl : classRepo.getClasses(c)) {
			if (ClassUtils.isHeritingFrom(cl, c)) {
				classes.addAll(getHeritedClasses(cl));
			}
		}
		logger.trace("Found " + classes.size() + " ones");
		return new ArrayList<Class<?>>(classes);
	}
	
	public DefinitionIterator getObjectDefinitionIterator() {
		return ((ClassChooseNode)getRoot()).getObjectDefinitionIterator();
	}


	public ObjectIterator<X> getObjectIterator() {
		ObjectIterator<X> ret = new ObjectIterator<X>(getRootChooseNode());
		return ret;
	}
	
	public static class ObjectIterator<X> implements Iterator<X> {

		Iterator<Pair<Object, ObjectRecipe>> rootIterator;
		AbstractChooseNode rootClone;
		
		public ObjectIterator(AbstractChooseNode root) {
			rootClone = (AbstractChooseNode)root.clone();
			rootClone.resetIterators();
			rootIterator = rootClone.iterator();
		}
		@Override
		public boolean hasNext() {
			return rootClone.isConfigured() && this.rootIterator.hasNext();
		}

		@SuppressWarnings("unchecked")
		@Override
		public X next() {
			if (!rootClone.isConfigured()) {
				throw new NoSuchElementException();
			}
			Pair<Object, ObjectRecipe> pair = this.rootIterator.next();
			if (pair != null) {
				return (X) pair.getFirst();
			} else {
				return null; // item is with error (WrongExperimentException)
			}
			
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public void cleanUp() {
			rootClone.cleanUp();
		}
	}
	
}
