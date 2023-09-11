package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.javanco.base.AbstractGraphHandler;

public class GraphLift extends JavancoTool {

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		ch.epfl.javanco.transformation.GraphLift.liftGraph(agh, 2);

	}

}
