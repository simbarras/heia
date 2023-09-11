package ch.epfl.general_libraries.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ch.epfl.general_libraries.utils.Matrix;

public class MatrixViewer extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Object[][] data;
	private JTable jTable = null;

	public MatrixViewer(Object[][] data) {
		super();
		this.initialize();
		this.data = data;
		Integer[] cols = new Integer[data.length];
		((DefaultTableModel)getJTable().getModel()).setDataVector(data, cols);
	}

	public MatrixViewer(Matrix<?> matrix) {
		super();
		this.initialize();
		this.data = matrix.toArrayOfArray();
		Integer[] cols = new Integer[data.length];
		((DefaultTableModel)getJTable().getModel()).setDataVector(data, cols);
	}

	private void initialize() {
		this.setSize(300, 200);
		add(getJTable());

	}

	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 2, Color.black));
		}
		return jTable;
	}
}
