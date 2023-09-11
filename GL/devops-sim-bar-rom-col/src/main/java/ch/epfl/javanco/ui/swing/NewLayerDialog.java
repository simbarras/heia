package ch.epfl.javanco.ui.swing;

import java.awt.Dimension;
import java.util.List;

import org.dom4j.Attribute;

import ch.epfl.javanco.io.JavancoClassesLoader;
import ch.epfl.javanco.network.DefaultGraphImpl;
import ch.epfl.javanco.network.DefaultLinkImpl;
import ch.epfl.javanco.network.DefaultNodeImpl;
import ch.epfl.javanco.network.Element;
import ch.epfl.javanco.network.Layer;
import ch.epfl.javanco.network.Link;
import ch.epfl.javanco.network.Node;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * Provides a Dialog permitting the entry of the basic parameters of a new graph or a new layer.
 * Using the <code>newGraph</code> boolean variable, either the dialog contains the fields for
 * the creation of a layer only, or also includes fields for a full new network creation
 */
public class NewLayerDialog extends javax.swing.JDialog {

	public static final long serialVersionUID = 0;
	private JavancoClassesLoader cl = null;

	private String layerName = null;
	private Class<? extends Layer> layerClass = null;

	private Class<? extends Node> defaultNodeClass = null;
	private String defaultNodeColor = null;
	private String defaultNodeIcon = null;

	private Class<? extends Link> defaultLinkClass = null;
	private String defaultLinkColor = null;
	private String defaultLinkWidth = null;

	private boolean result = false;

	private boolean newGraph = false;

	private String graphName = null;
	/**
	 * Returns the content of the "graph name" field
	 * @return the content of the "graph name" field
	 */
	public String getGraphName() {
		return (this.graphName);
	}
	/**
	 * Returns the content of the "layer name" field
	 * @return the content of the "layer name" field
	 */
	public String getLayerName() {
		return (this.layerName);
	}
	/**
	 * Returns the content of the "layer class" field
	 * @return the content of the "layer class" field
	 */
	public Class<? extends Layer> getLayerClass() {
		return (this.layerClass);
	}

	private Class<? extends Node> getDefaultNodeClass() {
		return (this.defaultNodeClass);
	}

	private String getDefaultNodeColor() {
		return (this.defaultNodeColor);
	}

	private  String getDefaultNodeIcon() {
		return (this.defaultNodeIcon);
	}

	private Class<? extends Link> getDefaultLinkClass() {
		return (this.defaultLinkClass);
	}

	private  String getDefaultLinkColor() {
		return (this.defaultLinkColor);
	}

	private  String getDefaultLinkWidth() {
		return (this.defaultLinkWidth);
	}

	/**
	 * Returns <code>true</code> if user clicked on the "OK" button and if all field
	 * are correctly filled, <code>false</code> otherwise
	 * @return <code>true</code> if user interrogation succeed
	 */
	public boolean hasResult() {
		return result;
	}

	/**
	 * Returns all the parameters introduced by the user, as a <code>List</code>
	 * of XML attributes
	 * @return all the parameters introduced by the user, as a <code>List</code>
	 * of XML attributes
	 */

	public List<Attribute> getNewLayerElement() {
		List<Attribute> attributes = new java.util.ArrayList<Attribute>();
		attributes.add(new NetworkAttribute(XMLTagKeywords.DEFAULT_NODE_CLASS, getDefaultNodeClass().getSimpleName(), null));
		attributes.add(new NetworkAttribute(XMLTagKeywords.DEFAULT_NODE_COLOR, getDefaultNodeColor(), null));
		attributes.add(new NetworkAttribute(XMLTagKeywords.DEFAULT_NODE_ICON, getDefaultNodeIcon(), null));
		attributes.add(new NetworkAttribute(XMLTagKeywords.DEFAULT_LINK_CLASS, getDefaultLinkClass().getSimpleName(), null));
		attributes.add(new NetworkAttribute(XMLTagKeywords.DEFAULT_LINK_COLOR, getDefaultLinkColor(), null));
		attributes.add(new NetworkAttribute(XMLTagKeywords.DEFAULT_LINK_WIDTH, getDefaultLinkWidth(), null));
		return attributes;
	}

	/**
	 * Display the dialog, associating it with the <code>parent</code> frame.
	 * @param parent the parent frame of this dialog
	 * @param cl The classLoader that knows about network element's classes
	 */
	public NewLayerDialog(java.awt.Frame parent, JavancoClassesLoader cl) {
		this(parent, false, cl);
	}
	/**
	 * Display the dialog, associating it with the <code>parent</code> frame. The boolean
	 * <code>newGraph</code> determines if the dialog shall present the fields for a new
	 * graph (including a graph name field) or just for a new layer
	 * @param parent the parent frame of this dialog
	 * @param newGraph true when creating a new Graph, false otherwise
	 * @param cl The classLoader that knows about network element's classes
	 */
	public NewLayerDialog(java.awt.Frame parent, boolean newGraph, JavancoClassesLoader cl) {
		super(parent, true);
		this.cl = cl;
		this.newGraph = newGraph;
		initComponents();
		setComboBoxes();
	}
	
	public NewLayerDialog(java.awt.Frame parent) {
		this(parent, false, null);
	}

	private Class<? extends Element> actualClass = null;


	private void setComboBoxes() {
		if (cl != null) {
			Class<? extends Element> newClass = cl.getNetworkClass(classSelector.getSelectedItem().toString());
			if (!(newClass.equals(actualClass))) {
				defaultLinkClassSelector.setModel(
						new javax.swing.DefaultComboBoxModel(
								cl.getAllowedLinkClasses(newClass).keySet().toArray(new String[]{})
						)
				);
				defaultNodeClassSelector.setModel(
						new javax.swing.DefaultComboBoxModel(
								cl.getAllowedNodeClasses(newClass).keySet().toArray(new String[]{})
						)
				);
			}
		}
	}


	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	private void reservedButtonActionPerformed(java.awt.event.ActionEvent evt) { }

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {

		// A FAIRE : v�rifier les valeurs surtout les couleurs

		graphName = nameTextField.getText();

		layerName = layerNameTextField.getText();
		if (cl != null) {
			layerClass = cl.getNetworkClass(classSelector.getSelectedItem().toString());
			defaultNodeClass = cl.getNodeClass(defaultNodeClassSelector.getSelectedItem().toString());
			defaultLinkClass = cl.getLinkClass(defaultLinkClassSelector.getSelectedItem().toString());
		} else {
			layerClass = DefaultGraphImpl.class;
			defaultNodeClass = DefaultNodeImpl.class;
			defaultLinkClass = DefaultLinkImpl.class;	
		}
		defaultNodeColor = defaultNodeColorTextField.getText();
		defaultNodeIcon = defaultNodeIconTextField.getText();

		defaultLinkColor = defaultLinkColorTextField.getText();
		defaultLinkWidth = defaultLinkWidthTextField.getText();

		result = true;
		this.setVisible(false);
	}


	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		buttonGroup1 = new javax.swing.ButtonGroup();
		buttonPanel = new javax.swing.JPanel();
		okButton = new javax.swing.JButton();
		reservedButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		tabbedPane = new javax.swing.JTabbedPane();
		mainSettingsPane = new javax.swing.JPanel();
		nameLabel = new javax.swing.JLabel();
		nameTextField = new javax.swing.JTextField();
		classLabel = new javax.swing.JLabel();
		layerNameTextField = new javax.swing.JTextField();
		layerNameLabel = new javax.swing.JLabel();
		classSelector = new javax.swing.JComboBox();
		mainLayerSettingsPane = new javax.swing.JPanel();
		nodeLayerPanel = new javax.swing.JPanel();
		defaultNodeClassLabel = new javax.swing.JLabel();
		defaultNodeColorLabel = new javax.swing.JLabel();
		defaultNodeIconLabel = new javax.swing.JLabel();
		defaultNodeClassSelector = new javax.swing.JComboBox();
		defaultNodeColorTextField = new javax.swing.JTextField();
		defaultNodeIconTextField = new javax.swing.JTextField();
		linkLayerPanel = new javax.swing.JPanel();
		defaultLinkClassLabel = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		defaultLinkClassSelector = new javax.swing.JComboBox();
		defaultLinkColorTextField = new javax.swing.JTextField();
		defaultLinkWidthTextField = new javax.swing.JTextField();

		setPreferredSize(new Dimension(370,290));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

		buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 15, 1));
		buttonPanel.setMaximumSize(new Dimension(32767, 100));
		buttonPanel.setMinimumSize(new Dimension(10, 30));
		buttonPanel.setPreferredSize(new Dimension(100, 40));
		okButton.setText("OK");
		okButton.setMaximumSize(new Dimension(80, 23));
		okButton.setMinimumSize(new Dimension(80, 23));
		okButton.setPreferredSize(new Dimension(80, 23));
		okButton.setSelected(true);
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(okButton);

		reservedButton.setText("reserved");
		reservedButton.setEnabled(false);
		reservedButton.setMaximumSize(new Dimension(80, 23));
		reservedButton.setMinimumSize(new Dimension(80, 23));
		reservedButton.setOpaque(false);
		reservedButton.setPreferredSize(new Dimension(80, 23));
		reservedButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				reservedButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(reservedButton);

		cancelButton.setText("Cancel");
		cancelButton.setMaximumSize(new Dimension(80, 23));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setPreferredSize(new Dimension(80, 23));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(cancelButton);

		getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

		tabbedPane.setMinimumSize(new Dimension(40, 40));
		tabbedPane.setPreferredSize(new Dimension(40, 100));
		mainSettingsPane.setLayout(new java.awt.GridBagLayout());
		mainSettingsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Layer"));
		mainSettingsPane.setMinimumSize(new Dimension(40, 40));
		mainSettingsPane.setPreferredSize(new Dimension(40, 40));

		if (newGraph) {
			mainSettingsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Graph"));
			mainSettingsPane.setMinimumSize(new Dimension(40, 40));
			mainSettingsPane.setPreferredSize(new Dimension(40, 40));
			nameLabel.setText("Name : ");
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipady = 4;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			mainSettingsPane.add(nameLabel, gridBagConstraints);

			nameTextField.setMinimumSize(new Dimension(6, 22));
			nameTextField.setPreferredSize(new Dimension(57, 22));
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 84;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
			mainSettingsPane.add(nameTextField, gridBagConstraints);
		}

		layerNameLabel.setText("Layer name : ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainSettingsPane.add(layerNameLabel, gridBagConstraints);
		if (newGraph) {
			layerNameTextField.setText("physical");
		} else {
			layerNameTextField.setText("layer_name");
		}
		layerNameTextField.setMaximumSize(new Dimension(2147483647, 22));
		layerNameTextField.setMinimumSize(new Dimension(6, 22));
		layerNameTextField.setPreferredSize(new Dimension(57, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 90;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		mainSettingsPane.add(layerNameTextField, gridBagConstraints);

		classLabel.setText("Class : ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		mainSettingsPane.add(classLabel, gridBagConstraints);
		
		if (cl != null) {
			classSelector.setModel(new javax.swing.DefaultComboBoxModel(cl.getNetworkClassesNames()));
		} else {
			// check, might be wrong
			classSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"DefaultGraphImpl"}));
		}
		classSelector.setMinimumSize(new Dimension(65, 22));
		classSelector.setMaximumSize(new Dimension(65,22));
		classSelector.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setComboBoxes();
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 84;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		mainSettingsPane.add(classSelector, gridBagConstraints);

		tabbedPane.addTab("General", mainSettingsPane);

		mainLayerSettingsPane.setLayout(new javax.swing.BoxLayout(mainLayerSettingsPane, javax.swing.BoxLayout.Y_AXIS));

		nodeLayerPanel.setLayout(new java.awt.GridBagLayout());

		nodeLayerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Nodes"));
		defaultNodeClassLabel.setText("Default Node Class : ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		nodeLayerPanel.add(defaultNodeClassLabel, gridBagConstraints);

		defaultNodeColorLabel.setText("Default Node Color : ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		nodeLayerPanel.add(defaultNodeColorLabel, gridBagConstraints);

		defaultNodeIconLabel.setText("Default Node Icon :");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		nodeLayerPanel.add(defaultNodeIconLabel, gridBagConstraints);

		defaultNodeClassSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
		defaultNodeClassSelector.setMinimumSize(new Dimension(65,22));
		defaultNodeClassSelector.setPreferredSize(new Dimension(65,22));
		defaultNodeClassSelector.setMaximumSize(new Dimension(65,22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipadx = 84;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		nodeLayerPanel.add(defaultNodeClassSelector, gridBagConstraints);

		defaultNodeColorTextField.setText("#FFFFFF");
		defaultNodeColorTextField.setMinimumSize(new Dimension(6, 22));
		defaultNodeColorTextField.setPreferredSize(new Dimension(57, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipadx = 84;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		nodeLayerPanel.add(defaultNodeColorTextField, gridBagConstraints);

		defaultNodeIconTextField.setText("node.png");
		defaultNodeIconTextField.setMinimumSize(new Dimension(6, 22));
		defaultNodeIconTextField.setPreferredSize(new Dimension(57, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipadx = 84;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		nodeLayerPanel.add(defaultNodeIconTextField, gridBagConstraints);

		mainLayerSettingsPane.add(nodeLayerPanel);

		linkLayerPanel.setLayout(new java.awt.GridBagLayout());

		linkLayerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Links"));
		defaultLinkClassLabel.setText("Default Link Class : ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		linkLayerPanel.add(defaultLinkClassLabel, gridBagConstraints);

		jLabel2.setText("Default Link Color : ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		linkLayerPanel.add(jLabel2, gridBagConstraints);

		jLabel3.setText("Default Link Width : ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		linkLayerPanel.add(jLabel3, gridBagConstraints);

		defaultLinkClassSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
		defaultLinkClassSelector.setMinimumSize(new Dimension(65, 22));
		defaultLinkClassSelector.setPreferredSize(new Dimension(65, 22));
		defaultLinkClassSelector.setMaximumSize(new Dimension(65, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.ipadx = 84;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		linkLayerPanel.add(defaultLinkClassSelector, gridBagConstraints);

		defaultLinkColorTextField.setText("#000000");
		defaultLinkColorTextField.setMinimumSize(new Dimension(6, 22));
		defaultLinkColorTextField.setPreferredSize(new Dimension(57, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipadx = 84;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		linkLayerPanel.add(defaultLinkColorTextField, gridBagConstraints);

		defaultLinkWidthTextField.setText("2");
		defaultLinkWidthTextField.setMinimumSize(new Dimension(6, 22));
		defaultLinkWidthTextField.setPreferredSize(new Dimension(57, 22));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipadx = 84;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		linkLayerPanel.add(defaultLinkWidthTextField, gridBagConstraints);

		mainLayerSettingsPane.add(linkLayerPanel);

		tabbedPane.addTab("Other settings", mainLayerSettingsPane);

		getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

		pack();
	}

	// Variables declaration - do not modify
	protected javax.swing.ButtonGroup buttonGroup1;
	protected javax.swing.JPanel buttonPanel;
	protected javax.swing.JButton cancelButton;
	protected javax.swing.JLabel classLabel;
	protected javax.swing.JComboBox classSelector;
	protected javax.swing.JLabel defaultLinkClassLabel;
	protected javax.swing.JComboBox defaultLinkClassSelector;
	protected javax.swing.JTextField defaultLinkColorTextField;
	protected javax.swing.JPanel linkLayerPanel;
	protected javax.swing.JTextField defaultLinkWidthTextField;
	protected javax.swing.JLabel defaultNodeClassLabel;
	protected javax.swing.JComboBox defaultNodeClassSelector;
	protected javax.swing.JLabel defaultNodeColorLabel;
	protected javax.swing.JTextField defaultNodeColorTextField;
	protected javax.swing.JLabel defaultNodeIconLabel;
	protected javax.swing.JTextField defaultNodeIconTextField;
	protected javax.swing.JLabel jLabel2;
	protected javax.swing.JLabel jLabel3;
	protected javax.swing.JPanel nodeLayerPanel;
	protected javax.swing.JLabel layerNameLabel;
	protected javax.swing.JTextField layerNameTextField;
	protected javax.swing.JPanel mainLayerSettingsPane;
	protected javax.swing.JPanel mainSettingsPane;
	protected javax.swing.JLabel nameLabel;
	protected javax.swing.JTextField nameTextField;
	protected javax.swing.JButton okButton;
	protected javax.swing.JButton reservedButton;
	protected javax.swing.JTabbedPane tabbedPane;
	// End of variables declaration

}
