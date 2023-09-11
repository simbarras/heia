package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.javanco.algorithms.GraphColouring;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class Coloring extends JavancoTool {

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		GraphColouring c = new GraphColouring();
		
		c.solve(agh);
		
		
	}

}
