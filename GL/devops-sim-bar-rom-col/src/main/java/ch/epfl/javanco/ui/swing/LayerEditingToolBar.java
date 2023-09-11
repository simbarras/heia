package ch.epfl.javanco.ui.swing;

import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.io.JavancoClassesLoader;
import ch.epfl.javanco.network.DefaultLinkImpl;
import ch.epfl.javanco.network.DefaultNodeImpl;
import ch.epfl.javanco.network.Element;
import ch.epfl.javanco.network.Layer;
import ch.epfl.javanco.network.Link;
import ch.epfl.javanco.network.Node;
import ch.epfl.javanco.ui.UIDelegate;

public class LayerEditingToolBar extends JToolBar {

	public static final long serialVersionUID = 0;

	private JToggleButton jNodeToggleButton = null;
	private JToggleButton jLinkToggleButton = null;

	/*
    private JPopupMenu jNodeToggleButtonPopup = null;
    private JPopupMenu jLinkToggleButtonPopup = null;
	 */

	private Hashtable<String,Class<? extends Node>> nodeTypes = null;
	private Hashtable<String,Class<? extends Link>> linkTypes = null;

	//private AbstractGraphHandler agh = null;
	private UIDelegate uidel;

	public LayerEditingToolBar(Hashtable<String,Class<? extends Node>> nodeTypes,
			Hashtable<String,Class<? extends Link>> linkTypes,
			UIDelegate uidel,
			boolean linkToBar) {
		this.uidel = uidel;
		this.nodeTypes = nodeTypes;
		this.linkTypes = linkTypes;
		initComponents(linkToBar);
		//initPopupMenus();
	}
	


	public static LayerEditingToolBar getEditingToolBar(Class<? extends Layer> layerClass, AbstractGraphHandler agh) {
		LayerEditingToolBar toolBar;
		
		if (agh.getGraphHandlerFactory().getJavancoClassesLoader() == null) {
			Hashtable<String, Class<? extends Link>> links = new Hashtable<String, Class<? extends Link>>();
			links.put("DefaultLink", DefaultLinkImpl.class);
			Hashtable<String, Class<? extends Node>> nodes = new Hashtable<String,Class<? extends Node>>();
			nodes.put("DefaultNode", DefaultNodeImpl.class);
			toolBar = new LayerEditingToolBar(nodes, links, agh.getUIDelegate(), true);
		} else {
			JavancoClassesLoader l = agh.getGraphHandlerFactory().getJavancoClassesLoader();
			toolBar = new LayerEditingToolBar(l.getAllowedNodeClasses(layerClass),
					l.getAllowedLinkClasses(layerClass),
					agh.getUIDelegate(), true);
		}
		return toolBar;
	}
	
	public static JToggleButton[] getButtonPair(AbstractGraphHandler agh,
			Hashtable<String, Class<? extends Node>> nHash,
			Hashtable<String, Class<? extends Link>> lHash) {
		LayerEditingToolBar tb = new LayerEditingToolBar(nHash, lHash, agh.getUIDelegate(), false);
		return new JToggleButton[]{tb.jNodeToggleButton, tb.jLinkToggleButton};
	}	

	public static LayerEditingToolBar getEditingToolBar(AbstractGraphHandler agh,
			Hashtable<String, Class<? extends Node>> nHash,
			Hashtable<String, Class<? extends Link>> lHash) {
		return new LayerEditingToolBar(nHash, lHash, agh.getUIDelegate(), true);
	}

	private Class<? extends Link> cachedLinkClass = null;
	private Class<? extends Node> cachedNodeClass = null;

	private void notifyChange() {
		Class<? extends Element> c = null;
		if (jNodeToggleButton.isSelected()) {
			c = cachedNodeClass;
		} else if (jLinkToggleButton.isSelected()) {
			c = cachedLinkClass;
		} else {
			c = null;
		}
		uidel.getTypeCreationAdapter().changeSelectedType(c, jNodeToggleButton.isSelected(), jLinkToggleButton.isSelected(), false);
		//	agh.fireSelectedTypeChangedEvent(c, jNodeToggleButton.isSelected(), jLinkToggleButton.isSelected());
		/*	.changeSelectedType(c, jNodeToggleButton.isSelected(), jLinkToggleButton.isSelected());*/
	}

	public void setLinkbuttonVisible(boolean b) {
		jLinkToggleButton.setVisible(b);
	}

	public void setNodebuttonVisible(boolean b) {
		jNodeToggleButton.setVisible(b);
	}

	public void setSelectedNodeType(String s) {
		jNodeToggleButton.setText(s);
		jNodeToggleButton.setToolTipText(s);
		cachedNodeClass = nodeTypes.get(s);
		notifyChange();
	}

	public void setSelectedLinkType(String s) {
		jLinkToggleButton.setText(s);
		jLinkToggleButton.setToolTipText(s);
		cachedLinkClass = linkTypes.get(s);
		notifyChange();
	}

	private void nodeToggleButtonChanged(javax.swing.event.ChangeEvent e) {
		if (jNodeToggleButton.isSelected()) {
			jLinkToggleButton.setSelected(false);
		}
		notifyChange();
	}

	private void linkToggleButtonChanged(javax.swing.event.ChangeEvent e) {
		if (jLinkToggleButton.isSelected()) {
			jNodeToggleButton.setSelected(false);
		}
		notifyChange();
	}



	public void deselectAll() {
		jNodeToggleButton.setSelected(false);
		jLinkToggleButton.setSelected(false);
	}

	/*	private int buttonWidth = 96;
	private int buttonHeight = 32;  */

	private void initComponents(boolean linkToBar) {

		this.setOrientation(SwingConstants.VERTICAL);

		jNodeToggleButton = new javax.swing.JToggleButton();
		jLinkToggleButton = new javax.swing.JToggleButton();

		AbstractButton[] buttons = new AbstractButton[]{jNodeToggleButton, jLinkToggleButton};

		for (AbstractButton b : buttons) {
			b.setFocusable(false);
			b.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
			// 	b.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
			//	b.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
			// 	b.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		}

		setSelectedNodeType(nodeTypes.keys().nextElement().toString());
		setSelectedLinkType(linkTypes.keys().nextElement().toString());

		jNodeToggleButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				nodeToggleButtonChanged(e);
			}
		});

		jLinkToggleButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				linkToggleButtonChanged(e);
			}
		});
		if (linkToBar) {
			this.add(jLinkToggleButton);
			this.add(jNodeToggleButton);
		}
	}
}