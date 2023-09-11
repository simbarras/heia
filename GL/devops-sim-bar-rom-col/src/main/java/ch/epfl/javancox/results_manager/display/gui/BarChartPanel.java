package ch.epfl.javancox.results_manager.display.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import ch.epfl.general_libraries.charts.ChartContainer;
import ch.epfl.general_libraries.results.AdvancedDataRetriever;
import ch.epfl.general_libraries.results.DataRetrievalOptions;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.javancox.results_manager.display.AbstractChartProvider;
import ch.epfl.javancox.results_manager.display.BarChartProvider;
import ch.epfl.javancox.results_manager.display.TreeLegend;
import ch.epfl.javancox.results_manager.display.AbstractChartProvider.Description;
import ch.epfl.javancox.results_manager.gui.ComplexDisplayPanel;

public class BarChartPanel extends AbstractChartProvider.AbstractChartPanel implements ActionListener {

	private JCheckBox logScaleY;
	private JCheckBox xOnScale;
	private JCheckBox scalarProduct;	
	private JPanel optionPanel;	
	
	public BarChartPanel(AdvancedDataRetriever retriever, ActionListener listener) {
		super(new BarChartProvider(), retriever);

		this.logScaleY = new JCheckBox("Log scale on Y");
		this.logScaleY.addActionListener(listener);
		this.xOnScale = new JCheckBox("X axis on Scale");
		this.xOnScale.addActionListener(listener);
		this.scalarProduct = new JCheckBox("Calculate scalar product");
		this.scalarProduct.addActionListener(listener);
		this.scalarProduct.addActionListener(this);
		this.listGraphs2 = new JComboBox();
		this.listGraphs2.addActionListener(listener);
		this.fillListGraphs2();
		this.createOptionPanel();
	}


	@Override
	public JPanel getConfigurationPanel(ComplexDisplayPanel panel) {
		return this.optionPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.scalarProduct) {
			this.listGraphs2.setEnabled(this.scalarProduct.isSelected());
		}
	}
	
	private void createOptionPanel() {
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 0;
		gb1.gridy = 0;
		gb1.anchor = GridBagConstraints.WEST;
		GridBagConstraints gb2 = new GridBagConstraints();
		gb2.gridx = 0;
		gb2.gridy = 1;
		gb2.anchor = GridBagConstraints.WEST;

		this.optionPanel = new JPanel(new GridBagLayout());
		JPanel pan2 = new JPanel(new FlowLayout());
		pan2.add(this.logScaleY);
		pan2.add(this.xOnScale);
		JPanel pan1 = new JPanel(new FlowLayout());
		pan1.add(this.listGraphs2);
		pan1.add(this.scalarProduct);

		this.optionPanel.add(pan1, gb1);
		this.optionPanel.add(pan2, gb2);
	}
	
	public Pair<ActionListener[], String[]> getDisplayerPossibleActions(ComplexDisplayPanel panel) {
		ActionListener excel = new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ev) {
				BarChartProvider disp = (BarChartProvider)displayer;
				try {
					disp.createExcelData(retriever);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(
							null,
							"Error during exportation of the file : \n"
							+ e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		return new Pair<ActionListener[], String[]>(
				new ActionListener[] { excel },
				new String[] { "Export displayed series as excel sheet" });
	}
	
	@Override
	public Object getLegend() {
		BarChartProvider disp = (BarChartProvider)displayer;
		Map<Pair<String, String>, Shape> seriesShape  = new HashMap<Pair<String, String>, Shape>();
		//seriesShape.put(new Pair<String, String>("", ""), new Rectangle(6, 6));
		JTree tl = TreeLegend.createTreeLegend(disp.seriesPaint, seriesShape,
				disp.seriesShapeBar, disp.legends);
		JScrollPane jp = new JScrollPane(tl);
		jp.setVisible(true);
		return jp;
	}	
	
	@Override
	public String getDecription() {
		return "Bar Chart";
	}


	@Override
	public String getSecondOption() {
		return "Texture";
	}
	
	public ChartContainer computeChart(DataRetrievalOptions options) {
		BarChartProvider disp = (BarChartProvider)displayer;
		disp.isLogY = logScaleY.isSelected();
		disp.isScalar = scalarProduct.isSelected();
		disp.isXOnScale = xOnScale.isSelected();
		disp.listGraph2 = ((Description) this.listGraphs2.getSelectedItem());
		return disp.computeChart(options, retriever);
	}



}
