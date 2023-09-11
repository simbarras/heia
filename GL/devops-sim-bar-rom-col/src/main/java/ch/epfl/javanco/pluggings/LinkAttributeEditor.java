package ch.epfl.javanco.pluggings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.ElementListener;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.xml.NetworkAttribute;

public class LinkAttributeEditor extends JavancoTool {



	JTextField attributeName;
	JComboBox layerName;
	JTable jTable;
	JButton goPopuButton;
	private List<Integer> nodeIndexes;

	JTextField populatorData;

	AbstractGraphHandler agh;

	JDialog dial;

	@Override
	public void run(final AbstractGraphHandler agh, Frame f) {

		final ElementListener.AbstractVoidListener listener = new ElementListener.AbstractVoidListener() {
			@Override
			public void reset(CasualEvent o) {
				LinkAttributeEditor.this.updateValues();
			}

			@Override
			public void refresh(CasualEvent o) {
				LinkAttributeEditor.this.updateValues();
			}
		};

		agh.addGraphicalElementListener(listener);

		this.agh = agh;
		dial = new JDialog(f);
		dial.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2,1));
		topPanel.add(getAttributeNameField());
		topPanel.add(getLayerNameBox());
		dial.add(topPanel, BorderLayout.NORTH);

		JPanel table = new JPanel();

		table.add(getJTable());

		dial.add(table, BorderLayout.CENTER);

		JPanel down = new JPanel();
		down.setLayout(new GridLayout(1,3));

		down.add(new JLabel("Populate with : "));
		down.add(getPopulatorData());
		down.add(getPopulateButton());

		dial.add(down,BorderLayout.SOUTH);


		init();
		dial.pack();
		dial.setVisible(true);

		dial.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				agh.removeGraphicalElementListener(listener);
			}
		});
	}

	private JButton getPopulateButton() {
		if (goPopuButton == null) {
			goPopuButton = new JButton("Go");
			goPopuButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					LayerContainer lay = getActuallySelectedLayer();
					lay.setAttributeOnAllLinks(attributeName.getText(),populatorData.getText(), true);
					updateValues();
				}
			});
		}
		return goPopuButton;
	}

	private JTextField getAttributeNameField() {
		if (attributeName == null) {
			attributeName = new JTextField(30);
			attributeName.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					updateValues();
				}
				public void removeUpdate(DocumentEvent e) {
					updateValues();
				}
				public void changedUpdate(DocumentEvent e) {
					updateValues();
				}
			});

		}
		return attributeName;
	}

	private JTextField getPopulatorData() {
		if (populatorData == null) {
			populatorData = new JTextField(30);
		}
		return populatorData;
	}

	private JComboBox getLayerNameBox() {
		Object previouslySelected = null;
		if (layerName == null) {
			layerName = new JComboBox();
		} else {
			previouslySelected = layerName.getSelectedItem();
		}

		String[] comboData = new String[agh.getLayerContainers().size()];
		int i = 0;
		int toSelect = 0;
		for (LayerContainer lc : agh.getLayerContainers()) {
			comboData[i] = lc.getName();
			i++;
		}
		i = 0;
		for (LayerContainer lc : agh.getLayerContainers()) {
			if (lc.getName().equals(agh.getEditedLayer().getName())) {
				toSelect = i;
			}
			if (lc.getName().equals(previouslySelected)) {
				toSelect = i;
				break;
			}
			i++;
		}

		layerName.setModel(new DefaultComboBoxModel(comboData));

		//		if (layerName.getSelectedIndex() < 0)
		layerName.setSelectedIndex(toSelect);
		return layerName;
	}

	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 2, Color.black));
		}
		return jTable;
	}

	private void cellChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
			int ori = nodeIndexes.indexOf(e.getFirstRow());
			int dest = nodeIndexes.indexOf(e.getColumn());
			if (ori >= 0 && dest >= 0) {
				String v = (String)((TableModel)e.getSource()).getValueAt(ori, dest);

				LayerContainer lay = getActuallySelectedLayer();
				LinkContainer linkc = lay.getLinkContainer(nodeIndexes.get(ori), nodeIndexes.get(dest));
				linkc.attribute(attributeName.getText()).setValue(v);
				linkc.linkAttribute(attributeName.getText(),"main_description");
			}
		}
	}

	private void init() {
		getJTable().getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(final TableModelEvent e) {
				cellChanged(e);
			}
		});
		getJTable().setShowHorizontalLines(true);
		getJTable().setShowVerticalLines(true);
		getJTable().setColumnSelectionAllowed(false);
		getJTable().setRowSelectionAllowed(false);
		getJTable().setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				LayerContainer lay = getActuallySelectedLayer();
				LinkContainer linkc = lay.getLinkContainer(nodeIndexes.get(row), nodeIndexes.get(column));

				JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (linkc == null) {
					label.setBackground(Color.LIGHT_GRAY);
				} else {
					label.setBackground(Color.WHITE);
				}
				label.setHorizontalAlignment(SwingConstants.CENTER);
				return label;
			}
		});


		class GraphCellEditor extends DefaultCellEditor {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			JTextField ftf = new JTextField();
			public GraphCellEditor() {
				super(new JTextField());
				ftf = (JTextField)getComponent();

				//		        ftf.setFormatterFactory(new DefaultFormatterFactory(new DefaultFormatter()));
				//   ftf.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance())));

				ftf.setHorizontalAlignment(SwingConstants.CENTER);
				// ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);


				ftf.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent e) {}

					@Override
					public void focusGained(FocusEvent e) {
						ftf.selectAll();
					}
				});

			}
			@Override
			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {

				LayerContainer lay = getActuallySelectedLayer();
				LinkContainer linkc = lay.getLinkContainer(nodeIndexes.get(row), nodeIndexes.get(column));
				if (linkc != null) {
					delegate.setValue(value);
					ftf.selectAll();
					return ftf;
				} else {
					return null;
				}
				//return super.getTableCellEditorComponent(table, value, isSelected, row, column);
			}

			@Override
			public Object getCellEditorValue() {
				return ftf.getText();
			}
		}

		getJTable().setDefaultEditor(Object.class, new GraphCellEditor());
	}

	private void updateValues() {
		getLayerNameBox();
		nodeIndexes = agh.getNodeIndexes();
		int size = nodeIndexes.size();
		String[][] data = new String[size][size];
		Integer[] cols = new Integer[size];

		LayerContainer lay = getActuallySelectedLayer();

		for (int i = 0 ; i < size ; i++) {
			for (int j = 0 ; j < size ; j++) {
				LinkContainer lc = lay.getLinkContainer(i, j);
				if (lc != null) {
					NetworkAttribute att = lc.attribute(attributeName.getText(), false);
					if (att != null) {
						data[i][j] = att.getValue();
					}
				}
			}
		}

		((DefaultTableModel)getJTable().getModel()).setDataVector(data, cols);
		dial.pack();
	}

	private LayerContainer getActuallySelectedLayer() {
		return agh.getLayerContainer((String)layerName.getSelectedItem());
	}

	@Override
	public void internalFrameClosing() {
		dial.setVisible(false);
		
	}

}
