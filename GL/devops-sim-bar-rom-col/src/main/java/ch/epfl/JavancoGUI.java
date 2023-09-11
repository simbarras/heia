package ch.epfl;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.ui.AbstractGraphicalUI;
import ch.epfl.javanco.ui.swing.GraphicalNetworkDisplayer;
import ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI;
import ch.epfl.javanco.ui.swing.simple.SimpleFrameBasedUI;

public class JavancoGUI {

	public static void main(String[] args) throws Exception {
		startJavancoGUI();
	}
	
	public static void startJavancoGUI() {
		try {
			Javanco.initJavanco();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JavancoDefaultGUI.getAndShowDefaultGUI(true);
	}
	
	public static void displayGraph(AbstractGraphHandler agh) {
		displayGraph(agh, 600, 600);
	}
	
	public static void displayGraph(AbstractGraphHandler agh, int wid, int height) {
		JFrame frame = new JFrame("graph");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(wid,height);

		GraphicalNetworkDisplayer disp = new GraphicalNetworkDisplayer(1, true);

		SimpleFrameBasedUI ui = new SimpleFrameBasedUI(AbstractGraphicalUI.getDefaultNetworkPainter(), agh,disp);
		agh.getUIDelegate().registerNewUI(ui);
		disp.setUI(ui);
		ui.setBestFit(new java.awt.Dimension(800,800));
		ui.setDisplaySize(800,800);
		ui.setElementSize(0.6f);
		frame.getContentPane().addComponentListener(disp);
		frame.getContentPane().add(disp);
		frame.setVisible(true);
	}	
	
	
}
