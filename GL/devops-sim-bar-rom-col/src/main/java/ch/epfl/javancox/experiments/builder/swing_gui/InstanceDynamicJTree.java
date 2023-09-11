package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractParameterChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ArrayChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.BooleanChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ClassChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ConstructorChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ConstructorNodeChooserPointer;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel;
import ch.epfl.javancox.experiments.builder.tree_model.LeafChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.TypableChooseNode;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel.TreeModelUIManager;

public class InstanceDynamicJTree extends JTree implements Serializable, KeyListener, TreeCellRenderer, TreeModelUIManager {
	
	private static Logger logger = new Logger(InstanceDynamicJTree.class);
	
	private static final long serialVersionUID = -235363405915261784L;

	
	private ArrayList<JComponent> focusable = new ArrayList<JComponent>();
	private JTextField focused;
	private int index = 0;
	
	private Hashtable<AbstractChooseNode, AbstractGUIContainer> guiComponentMap = new Hashtable<AbstractChooseNode, AbstractGUIContainer>();
	
	
	public InstanceDynamicJTree(ObjectConstuctionTreeModel<?> mod) {
		logger.trace("Initialisation of the JTree");
		this.addKeyListener(this);
		this.setFocusTraversalKeysEnabled(false);
		mod.setTreeModelUIManager(this);
		this.setModel(mod);		
		focusable.clear();
		index = 0;
		this.setEditable(true);
		this.setFocusable(true);
//		MouseListener ml = getMouseListener();
		this.setCellRenderer(this);
		this.setCellEditor(new MyEditor());
//		this.addMouseListener(ml);		
	}

	@Override
	public void showErrorMessage(String string) {
		JOptionPane.showMessageDialog( null, string, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void registerAsFocusable(JComponent comp) {
		focusable.add(comp);
	}

	@Override
	public boolean isPathEditable(TreePath treePath) {
		return ((AbstractChooseNode) treePath.getLastPathComponent()).isEditable();
	}
	
/*	private MouseListener getMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int selRow = InstanceDynamicJTree.this.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = InstanceDynamicJTree.this.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					AbstractChooseNode node = (AbstractChooseNode) selPath.getLastPathComponent();
					if (e.getButton() == 3) {
						JPopupMenu menu = buildMenu(node);
						//JPopupMenu menu = node.getOptions();
						menu.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
						menu.show((JComponent) e.getSource(), e.getX(),e.getY());
					}
					InstanceDynamicJTree.this.expandRow(selRow);
				}
			}
		};
	}*/

	public int getInstancesCount() {
		if (((ObjectConstuctionTreeModel<?>) getModel()).isReady()) {
			return ((AbstractChooseNode)getModel().getRoot()).getInstancesCount();
		} else {
			return 0;
		}
	}
	
	@Override
	public void removeNode(AbstractChooseNode node) {
		guiComponentMap.remove(node);
		
	}	
	
	protected Container getContainer(AbstractChooseNode node) {
		AbstractGUIContainer c = null;
		c = guiComponentMap.get(node);
		if (c == null) {
			if (node instanceof ClassChooseNode) {
				c = new ParameterGUIContainer((AbstractParameterChooseNode)node, null, 0);
			} else if (node instanceof BooleanChooseNode) {
				c = new BooleanParameterGUIContainer((BooleanChooseNode)node, null, 0);
			} else if (node instanceof ArrayChooseNode) {
				c = new ParameterGUIContainer((AbstractParameterChooseNode)node, null, 0);
			} else if (node instanceof TypableChooseNode) {
				c = new TypableParameterGUIContainer((TypableChooseNode)node, null, 0);
			} else if (node instanceof ConstructorChooseNode) {
				c = new ConstructorGUIContainer((ConstructorChooseNode)node, null, 0);
			} else if (node instanceof LeafChooseNode) {
				c = new LeafNodeGUIContainer((LeafChooseNode)node, null, 0);
			} else if (node instanceof ConstructorNodeChooserPointer) {
				c = new ConstructorPointerGUIContainer((ConstructorNodeChooserPointer)node, null, 0);
			} else {
				throw new IllegalStateException("Should not be here");
			}
			guiComponentMap.put(node, c);
		} else {
			c.refresh();
		}
		return c;
	}
	
	/**
	 *  This method is rendering the tree element. Implements TreeCellRenderer
	 * @param tree
	 * @param value
	 * @param sel
	 * @param expanded
	 * @param leaf
	 * @param row
	 * @param hasFocus
	 * @return
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
		if (value instanceof AbstractChooseNode == false) return new JPanel(); // may happend at object init
		AbstractChooseNode node = (AbstractChooseNode) value;
		try {
			Component c = getContainer(node);
			c.doLayout();
			return c;
		}
		catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	private class MyEditor implements TreeCellEditor, Serializable {

		private static final long serialVersionUID = 8980517165686533926L;
		private AbstractChooseNode current;

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		@Override
		public boolean stopCellEditing() {
			return false;
		}

		@Override
		public void cancelCellEditing() {
			if (this.current instanceof TypableChooseNode) 
				((TypableChooseNode)this.current).checkValues();
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {}		

		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			return getContainer((AbstractChooseNode) value);
		}

	}
	
	private void focusOn(JComponent c) {				
		if (c instanceof JTextField) {
			focused = (JTextField)c;
			focused.setText("");
		}
	}
	
	private void deFocus() {
		if (focused != null)
			focused.setText("Put value in here");
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}	
	
	private boolean ctrlPressed = false;

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrlPressed = true;
			return;
		}
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			return;
		}
		if (ctrlPressed && e.getKeyCode() == KeyEvent.VK_D) {
			Object root = this.getModel().getRoot();
			ClassChooseNode node = (ClassChooseNode)root;
			node.setAllExpanded();
		}
			
		if (focusable.size() == 0) return;
		char ch = e.getKeyChar();
		if (ch == '\t') {
			if (focused == null) {
				JComponent c = focusable.get(index);
				focusOn(c);
			} else {
				index++;
				if (index >= focusable.size()) index = 0;
				deFocus();
				JComponent c = focusable.get(index);
				focusOn(c);
			}
		} else if (ch != '\n') {
			if (focused != null) {
				focused.setText(focused.getText() + ch);
			}
		} else {
			for (ActionListener l : focused.getActionListeners()) {
				l.actionPerformed(new ActionEvent(focused, 0, ""));
			}
			focused.setText("");
		}
		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			ctrlPressed = false;		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}


}
