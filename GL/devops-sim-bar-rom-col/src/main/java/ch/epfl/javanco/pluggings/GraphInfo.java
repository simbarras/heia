package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import javax.swing.JDialog;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.topology_analysis.GraphPropertiesFormatter;

public class GraphInfo extends JavancoTool {
	
	JDialog diag;

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		
		GraphPropertiesFormatter format = new GraphPropertiesFormatter(false);
		
		diag = new JDialog(f, true);
		diag.add(format.getPanel(agh, f));
		diag.setSize(600, 600);
		diag.setVisible(true);

	//	int i = agh.getNodeContainers().size();
	//	int j = agh.getLinkContainers().size();

	//	JOptionPane.showMessageDialog(f, "Graph has " + i + " nodes and " + j + " links");


	}

	@Override
	public void internalFrameClosing() {
		diag.setVisible(false);
	}

}