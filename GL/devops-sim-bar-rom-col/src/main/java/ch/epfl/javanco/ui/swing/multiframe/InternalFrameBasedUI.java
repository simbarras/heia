package ch.epfl.javanco.ui.swing.multiframe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import ch.epfl.general_libraries.clazzes.ClassLister;
import ch.epfl.general_libraries.event.RunnerEventListener;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.event.EditedLayerEvent;
import ch.epfl.javanco.event.EditedLayerListener;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.io.JavancoClassesLoader;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.Link;
import ch.epfl.javanco.network.Node;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.pluggings.JavancoTool;
import ch.epfl.javanco.ui.GlobalInterface;
import ch.epfl.javanco.ui.swing.AbstractFullSwingUI;
import ch.epfl.javanco.ui.swing.LayerEditingToolBar;
import ch.epfl.javanco.ui.swing.NewLayerDialog;
import ch.epfl.javanco.ui.swing.PopupDisplayer;
import ch.epfl.javanco.ui.swing.ScriptControlPanel;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * Centralize the management of all network dependent GUI objects like toolbars,
 * menubar etc.
 */
public class InternalFrameBasedUI extends AbstractFullSwingUI {

	public final static String INTERNALFRAMEBASEDUI_PROPERTIES_FILE = "div/internalFrameBasedUI.properties.xml";

	protected GraphMenuBar                      menuBar;
	protected SwingEditorInternalFrame          associatedFrame;
	private   ScriptControlPanel                scriptControlPanel;

	/**
	 * Setup a new GUI Manager for the network handled by the handler given as
	 * parameter
	 * @param handler The handler of the edited or visualised network
	 * @param isEditable <code>true</code> if handler supports edition, <code>
	 * false</code> if not or if edition must be blocked (visualisation GUI).
	 * @param factory To suppress
	 */
	public InternalFrameBasedUI(NetworkPainter painter,
			AbstractGraphHandler agh,
			GlobalInterface superInterface) {
		super(painter, agh, superInterface);
	}

	@Override
	public RunnerEventListener getRunnerEventListener() {
		if (scriptControlPanel == null) {
			scriptControlPanel = new ScriptControlPanel(associatedGlobalInterface.getMainFrame());
		}
		return scriptControlPanel;
	}

	/**
	 * Provide the reference of the actual InternalFrame to the GUI delegate
	 */
	public SwingEditorInternalFrame getGraphInternalFrame() {
		if (associatedFrame == null && associatedGlobalInterface.getMainFrame() != null) {
			try {
				associatedFrame = new SwingEditorInternalFrame(handler);
				associatedFrame.setPreferredSize(associatedGlobalInterface.getMainFrame().getContentPane().getSize());
				associatedFrame.setTitle(handler.getHandledGraphName());
				menuBar = new GraphMenuBar(true, this);
				associatedFrame.setJMenuBar(menuBar);
				associatedFrame.setEditorComponent(editor);
				handler.addEditedLayerListener(associatedFrame);
			} catch (Exception e) {
				System.out.println("Catched expression when creating internalFrame");
				throw new IllegalStateException(e);
			}
		}
		return associatedFrame;
	}

	// ========================================== EDITOR RELATED

	@Override
	public void refreshAndRepaintDisplayAndUI() {
		super.refreshAndRepaintDisplayAndUI();
		if (getGraphInternalFrame() != null) {
			getGraphInternalFrame().repaint();
		}
	}


	public void newLayerUserCommand(EventObject e) {
		JFrame frame = associatedGlobalInterface.getMainFrame();
		NewLayerDialog dialog;
		JavancoClassesLoader addClassLoader = handler.getGraphHandlerFactory().getJavancoClassesLoader();
		dialog = new NewLayerDialog(frame, addClassLoader);
		dialog.setVisible(true);
		if (dialog.hasResult()) {
			try {
				handler.newLayer(dialog.getLayerName(), dialog.getLayerClass(), dialog.getNewLayerElement(), e);
			}
			catch (Exception ex) {
				PopupDisplayer.popupErrorMessage("Impossible to create new layer with the given properties.", ex, frame);
			}
		}
	}

	@Override
	public boolean askUserConfirmation(String s) {
		int ans = JOptionPane.showConfirmDialog(associatedGlobalInterface.getMainFrame(), s, "Javanco", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}
	
	public static interface SwingEditorInternalFrameClosingListener {
		public void internalFrameClosing();
	}

	static class SwingEditorInternalFrame extends JInternalFrame implements EditedLayerListener {

		public static final long serialVersionUID = 0;

		private JLabel footerLabel = new JLabel();

		private LayerEditingToolBar toolBar = null;
		private JPanel editorPanel = null;
		
		private ArrayList<SwingEditorInternalFrameClosingListener> closingListeners = new ArrayList<SwingEditorInternalFrameClosingListener>(0);

		private boolean showFooterLabel = true;
		private boolean showToolBar = true;

		private AbstractGraphHandler handler = null;

		public SwingEditorInternalFrame(AbstractGraphHandler agh) {
			this(true, true, agh);
		}

		public SwingEditorInternalFrame(boolean isEdited, AbstractGraphHandler agh) {
			this(isEdited, isEdited, agh);
		}

		public SwingEditorInternalFrame(boolean showFooterLabel, boolean showToolBar, AbstractGraphHandler agh) {
			super("", true, true, true, true);
			this.showFooterLabel = showFooterLabel;
			this.showToolBar = showToolBar;
			this.handler  = agh;
			initStaticComponents();
		}

		AbstractGraphHandler getAbstractGraphHandler() {
			return handler;
		}
		
		public void addClosingListener(SwingEditorInternalFrameClosingListener list) {
			closingListeners.add(list);
		}

		@Override
		public void setVisible(boolean b) {
			if (b) {
				this.setResizable(true);
				try {
					if (handler.getEditedLayer() != null) {
						setFooterLabelText(handler.getEditedLayer().getName());
						setToolBar();
					}
					setSelected(true);
				} catch (java.beans.PropertyVetoException e) {}
				pack();
			} else {
				if (closingListeners != null) {
					for (SwingEditorInternalFrameClosingListener l : closingListeners) {
						l.internalFrameClosing();
					}
					// clean up
					closingListeners.clear();
				}				
			}
			super.setVisible(b);
		}

		public void editedLayerChanged(EditedLayerEvent e) {
			LayerContainer editedLayer = e.getLayerEdited();
			setFooterLabelText(editedLayer.getName());
			setToolBar();
			this.setPreferredSize(getSize());
			this.pack();
		}

		void setFooterLabelText(String s) {
			footerLabel.setText("Currently edited layer : " + s);
		}

		void setToolBar() {
			if (this.showToolBar) {
				if (toolBar != null) {
					getContentPane().remove(toolBar);
				}

				LayerContainer editedLayer = handler.getEditedLayer();
				toolBar = LayerEditingToolBar.getEditingToolBar(editedLayer.getContainedLayerClass(), handler);
				getContentPane().add(toolBar, BorderLayout.WEST);

				try {
					String linkClassName = editedLayer.attribute(ch.epfl.javanco.xml.XMLTagKeywords.DEFAULT_LINK_CLASS).getValue();
					String nodeClassName = editedLayer.attribute(ch.epfl.javanco.xml.XMLTagKeywords.DEFAULT_NODE_CLASS).getValue();

					Class<? extends Link> linkClass = handler.getGraphHandlerFactory().getJavancoClassesLoader().getLinkClass(linkClassName);
					Class<? extends Node> nodeClass = handler.getGraphHandlerFactory().getJavancoClassesLoader().getNodeClass(nodeClassName);

					toolBar.setSelectedLinkType(linkClass.getSimpleName());
					toolBar.setSelectedNodeType(nodeClass.getSimpleName());
				}
				catch (NullPointerException e) {}
			}

		}

		void setEditorComponent(JComponent displayPane) {
			editorPanel.removeAll();
			editorPanel.add(displayPane);
		}

		private void initStaticComponents() {
			java.awt.Container mainPane = this.getContentPane();
			mainPane.setLayout(new java.awt.BorderLayout());
			if (showFooterLabel) {
				JPanel footerPanel = new JPanel();
				footerPanel.setLayout(new java.awt.BorderLayout());
				footerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
				footerPanel.setMaximumSize(new Dimension(32767, 24));
				footerPanel.setMinimumSize(new Dimension(10, 24));
				footerPanel.setPreferredSize(new Dimension(100, 24));
				footerLabel.setText("footerFrameLabel");
				footerPanel.add(footerLabel, java.awt.BorderLayout.WEST);
				mainPane.add(footerPanel, java.awt.BorderLayout.SOUTH);
			}
			/*
	        if (showToolBar && toolBar != null) {
		        mainPane.add(toolBar);
			}
			 */
			editorPanel = new JPanel();
			editorPanel.setLayout(new java.awt.GridLayout(1,1));
			mainPane.add(editorPanel, java.awt.BorderLayout.CENTER);

		}
	}

	class GraphMenuBar extends JMenuBar implements EditedLayerListener {

		public static final long serialVersionUID = 0;
		
		JMenu layers = new JMenu("Layers...");		
		JMenu visible = new JMenu("Visibility");
		JMenu enabled = new JMenu("Actually edited");
		JMenuItem menuItem = new JMenuItem("New Layer...");
		
		JMenu options = new JMenu("Options");		

		JMenu tools = new JMenu("Tools");		
		
		//	private ButtonGroup radioButtonGroup = null;
		private Hashtable<String, JMenu> layerGroupsVisible = null;
		private Hashtable<String, JMenu> layerGroupsEnabled = null;
		private JMenu currentGroupVisible = null;
		private JMenu currentGroupEnabled = null;
		JCheckBoxMenuItem view3d = null;

		private InternalFrameBasedUI sui;
		private boolean edited = true;

		public GraphMenuBar(boolean edited, InternalFrameBasedUI sui) {
			this.edited = edited;
			this.sui = sui;
			handler.addEditedLayerListener(this);
			create();
			refreshLayersMenu(edited);
		}
		
		private void create() {
			if (edited) {
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newLayerUserCommand(e);
					}
				});
				layers.add(menuItem);
				layers.add(new JSeparator());
				layers.add(enabled);		
			}			
			this.add(layers);
			this.add(options);			
			this.add(tools);
			layers.add(visible);
			initOptionsMenu();			
			initToolsMenu();
		}

		public void editedLayerChanged(EditedLayerEvent event) {
			refreshLayersMenu(edited);
		}

		private void refreshLayersMenu(boolean edited) {
			
			visible.removeAll();
			enabled.removeAll();

			Collection<LayerContainer> allLayers = handler.getLayerContainers();
			boolean useGroups = false;

			if (allLayers.size() > 15) {
				useGroups = true;
				layerGroupsVisible = new Hashtable<String, JMenu>();
				layerGroupsEnabled = new Hashtable<String, JMenu>();
				currentGroupVisible = null;
				currentGroupEnabled = null;
			}
			for (LayerContainer layerContainer : allLayers) {

				String layerName = layerContainer.getKey();
				JMenuItem menuItem = new JCheckBoxMenuItem(layerName, layerContainer.isDisplayed());
				menuItem.setActionCommand(layerName);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						JCheckBoxMenuItem it = (JCheckBoxMenuItem)ev.getSource();
						sui.getAssociatedAbstractGraphHandler().getLayerContainer(ev.getActionCommand()).setDisplayed(it.isSelected());
					}
				});
				if (useGroups) {
					addToGroup(layerContainer, currentGroupVisible, visible, menuItem, layerGroupsVisible);
				} else {
					visible.add(menuItem);
				}
				if (edited) {
					ButtonGroup radioButtonGroup = new ButtonGroup();
					menuItem = new JRadioButtonMenuItem(layerName, handler.getEditedLayer().equals(layerContainer));
					menuItem.setActionCommand(layerName);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							handler.setEditedLayer(ev.getActionCommand());
						}
					});
					radioButtonGroup.add(menuItem);
					if (useGroups) {
						addToGroup(layerContainer, currentGroupEnabled, enabled, menuItem, layerGroupsEnabled);
					} else {
						enabled.add(menuItem);
					}
				}
			}
		}

		private void initOptionsMenu() {
			final JMenuItem offsets = new JCheckBoxMenuItem("Use offsets");
			offsets.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					for (LayerContainer cont : handler.getLayerContainers()) {
						cont.attribute(XMLTagKeywords.USE_OFFSET).setValue(offsets.isSelected(), ev);
					}
				}
			});

			final JMenuItem anim = new JCheckBoxMenuItem("Use animations");
			anim.setSelected(sui.isAnimated());
			anim.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					setAnimated(anim.isSelected());
				}
			});
			
			final JMenuItem ids = new JCheckBoxMenuItem("Show node IDs");
			ids.setSelected(true);
			ids.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					for (NodeContainer cont : handler.getNodeContainers()) {
						cont.attribute(XMLTagKeywords.NODE_SEE_ID).setValue(ids.isSelected());
					}
				}
			});
				
			try {
		//		Class.forName("com.sun.opengl.impl.windows.WindowsGLDrawableFactory");
				if (sui instanceof InternalFrameBasedUIPlus3D) {
					view3d = new JCheckBoxMenuItem("Show 3d view");
					view3d.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
						//	if (sui instanceof InternalFrameBasedUIPlus3D) {
								((InternalFrameBasedUIPlus3D)sui).setView3dVisible(isSelected());
						//	}
						}
					});
					options.add(view3d);
				}
			}
			catch (Throwable t) {}
			
			options.add(offsets);
			options.add(anim);
			options.add(ids);
		}

		@SuppressWarnings("unchecked")
		private void initToolsMenu() {
			try {
				ClassLister<JavancoTool> lister = new ClassLister<JavancoTool>(
						Javanco.getProperty(Javanco.JAVANCO_DEFAULT_CLASSPATH_PREFIXES_PROPERTY).split(";"), JavancoTool.class);
	
				for (Class<? extends JavancoTool> c_ : lister.getSortedClasses()) {
					final Class c = c_;
					JMenuItem toolItem = new JMenuItem(c.getSimpleName());
					toolItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							try {
								JavancoTool t = ((JavancoTool)c.getConstructor(new Class[]{}).newInstance(new Object[]{}));
								sui.associatedFrame.addClosingListener(t);
								t.run(handler, associatedGlobalInterface.getMainFrame() );
							}
							catch (Exception e) {
								throw new IllegalStateException(e);
							}
						}
					});
					tools.add(toolItem);
				}
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		private void addToGroup(LayerContainer lc,
				JMenu currentGroup,
				JMenu menu,
				JMenuItem item,
				Hashtable<String, JMenu> layerGroups) {
			if (lc.attribute("layerGroup", false) != null) {
				String groupName = lc.attribute("layerGroup", false).getValue();
				JMenu group;
				if (layerGroups.get(groupName) == null) {
					group = new JMenu(groupName);
					layerGroups.put(groupName, group);
					menu.add(group);
				} else {
					group = layerGroups.get(groupName);
				}
				group.add(item);
			} else {
				if ((currentGroup == null) || (currentGroup.getItemCount() > 15)) {
					currentGroup = new JMenu("    -    ");
					menu.add(currentGroup);
				}
				currentGroup.add(item);
			}
		}


		/**
		 * Sets the state of the menu <code>view3d</code>.
		 * <BR>#author fmoulin
		 * @param checked true if the menu is selected, otherwise false
		 */
		public void setView3dChecked(boolean checked) {
			view3d.setSelected(checked);
		}

	}
}
