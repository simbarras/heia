package ch.epfl.javanco.pluggings;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class AddClientsAndGetDistances extends JavancoTool implements ActionListener {
	
	private JDialog diag;
	private JTextField tf;
	private JTextArea ta;
	private JButton ok;
	private JButton revert;
	private JButton done;
	
	private JCheckBox graphics;
	
	private ArrayList<Integer> previousNodes;
	
	private AbstractGraphHandler agh;
	private Frame callingFrame;

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		this.agh = agh;
		this.callingFrame = f;
		previousNodes = new ArrayList<Integer>();
		for (NodeContainer n : agh.getNodeContainers()) {
			previousNodes.add(n.getIndex());
		}
		
		
		diag = new JDialog(f, false);
		diag.add(getPanel(agh, f));
		diag.setSize(600, 600);
		diag.setVisible(true);
	}

	private Component getPanel(AbstractGraphHandler agh, Frame f) {
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		JLabel nbClients = new JLabel("Number of clients");
		pane.add(nbClients);
		tf = new JTextField(2);
		pane.add(tf);
		JLabel gra = new JLabel("Change agh?");
		pane.add(gra);
		graphics = new JCheckBox();
		pane.add(graphics);		
		ok = new JButton("ok");
		ok.addActionListener(this);
		pane.add(ok);
		revert = new JButton("revert");
		revert.addActionListener(this);
		pane.add(revert);
		done = new JButton("done");
		done.addActionListener(this);
		pane.add(done);
		
		ta = new JTextArea(30, 10);
		ta.setEditable(true);
		pane.add(ta);
		return pane;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(ok)) {
			addClients();
		} else if (ae.getSource().equals(revert)) {
			revert();
		} else {
			agh = null;
			diag.setVisible(false);
		}
	}

	private void revert() {
		agh.removeAllNodesButSome(previousNodes);
	}

	private void addClients() {
		int clientPerNode = Integer.parseInt(tf.getText());
		if (graphics.isSelected()) {
			int ray1 = 250;
			int ray2 = 330;
			int numNodesBase = agh.getNumberOfNodes();
			double angle1 = 2*Math.PI / (double)numNodesBase;
			double angle2 = angle1/(double)clientPerNode;
			double startAngle2;
			if (clientPerNode % 2 == 0) {
				startAngle2 = -((double)(clientPerNode-1)/2d)*angle2;
			} else {
				startAngle2 = -((double)(clientPerNode-1)/2d)*angle2;
			}
			int index = 0;
		//	int[] positions = TypeParser.parseIntArray(AskQuestion.askString("sequence"));
			int[] positions = TypeParser.parseIntArray("0:" + (numNodesBase-1));
			for (int i = 0 ; i < positions.length ; i++) {
				NodeContainer nc = agh.getNodeContainer(positions[i]);
				nc.setX((int)(Math.cos(angle1*index)*ray1));
				nc.setY((int)(Math.sin(angle1*index)*ray1));
				nc.attribute("node_color").setValue("#ffcccc");
			//	nc.attribute("node_size").setValue(40);				
				index++;
			}
			index = 0;
			for (int i = 0 ; i < numNodesBase ; i++) {
				for (int j = 0 ; j < clientPerNode ; j++) {
					double x = Math.cos(startAngle2 + (angle2*index))*ray2;
					double y = Math.sin(startAngle2 + (angle2*index))*ray2;
					NodeContainer nn = agh.newNode((int)x, (int)y);
					nn.attribute("node_color").setValue("220,100,200");
			//		nn.attribute("node_size").setValue(30);
					index++;
					agh.newLink(positions[i], nn.getIndex());
				}
			}
			agh.setBestFit();
		} else {
			ta.setText("");
			int[][] dist = BFS.getDistancesUndirected(agh);
			int[][] expanded = new int[dist.length*clientPerNode][dist.length*clientPerNode];
			for (int i = 0 ; i < dist.length ; i++) {
				for (int k = 0 ; k < clientPerNode ; k++) {
					for (int l = 0 ; l < clientPerNode ; l++) {
						if (k != l) {
							expanded[i*clientPerNode+k][i*clientPerNode+l] = 2;
						}
					}
				}
			}
			for (int i = 0 ; i < expanded.length ; i++) {
				for (int j = 0 ; j < expanded.length ; j++) {
					if (i == j) {
						expanded[i][j] = 0;
					} else if (expanded[i][j] == 0) {
						expanded[i][j] = 2 + dist[i/clientPerNode][j/clientPerNode];
					}
					ta.append(i + "-" + j + "\t" + expanded[i][j] + "\r\n");
				}
			}
			double[][] d = Matrix.normalize(expanded);
			PcolorGUI gui = new PcolorGUI(d);
			gui.showInDialog(callingFrame);
		}
	}
	
	

}
