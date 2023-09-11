package ch.epfl.javanco.ui.swing;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.ui.AbstractInteractiveGraphicalUI;
import ch.epfl.javanco.ui.GlobalInterface;

public abstract class AbstractSwingUI extends AbstractInteractiveGraphicalUI {

	public AbstractSwingUI(NetworkPainter painter, AbstractGraphHandler agh,
			GlobalInterface superInterface) {
		super(painter, agh, superInterface);
	}

	private GraphicalNetworkDisplayer graphicalNetworkDisplayer__ = null;	
	
	public GraphicalNetworkDisplayer getGraphicalNetworkDisplayer() {
	/*	if (graphicalNetworkDisplayer__ == null) {
			throw new NullPointerException("Network displayer is null which means that UI init() method has likely never been called");
		}*/
		return graphicalNetworkDisplayer__;
	}
	
	protected void setGraphicalNetworkDisplayer(GraphicalNetworkDisplayer disp) {
		graphicalNetworkDisplayer__ = disp;
	}
	
	public void repaintGraph() {
		graphicalNetworkDisplayer__.repaint();
	}
	
	/**
	 * Provides an access for non GUI component to the surface on the screen that
	 * is occupied by the display
	 */
	public java.awt.Rectangle getDisplayerBounds() {
		if (graphicalNetworkDisplayer__ != null) {
			java.awt.Point onScreen = graphicalNetworkDisplayer__.getLocationOnScreen();
			java.awt.Dimension size = graphicalNetworkDisplayer__.getSize();
			return new java.awt.Rectangle(onScreen.x, onScreen.y, size.width, size.height);
		} else {
			return null;
		}
	}	
	
}
