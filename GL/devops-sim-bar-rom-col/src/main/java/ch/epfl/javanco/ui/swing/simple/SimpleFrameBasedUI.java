package ch.epfl.javanco.ui.swing.simple;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.ui.swing.AbstractSwingUI;
import ch.epfl.javanco.ui.swing.GraphicalNetworkDisplayer;

public class SimpleFrameBasedUI extends AbstractSwingUI {

//	private Component disp;

	public SimpleFrameBasedUI(NetworkPainter painter,
			AbstractGraphHandler agh,
			GraphicalNetworkDisplayer disp) {
		super(painter, agh, null);
		super.setGraphicalNetworkDisplayer(disp);
	}

	@Override
	public void displayPropertiesImpl(AbstractElementContainer object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean askUserConfirmation(String s) {
		// TODO Auto-generated method stub
		return false;
	}
}
