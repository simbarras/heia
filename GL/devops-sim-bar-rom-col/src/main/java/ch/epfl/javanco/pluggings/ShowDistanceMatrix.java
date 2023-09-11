package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class ShowDistanceMatrix extends JavancoTool {

	PcolorGUI gui;	
	
	@Override
	public void internalFrameClosing() {
		gui.close();
	}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		int[][] dist = BFS.getDistancesUndirected(agh);
		double[][] d = Matrix.normalize(dist);
		PcolorGUI gui = new PcolorGUI(d);
		gui.showInFrame();

	}

}
