package ch.epfl.javanco.ui.console;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.GraphHandlerFactory;
import ch.epfl.javanco.graphics.DefaultNetworkPainter2D;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.ui.AbstractInteractiveGraphicalUI;

/**
 * @author Christophe Trefois
 *
 */
public class ConsoleUI extends AbstractInteractiveGraphicalUI {
	/**
	 * 
	 */
	public GraphHandlerFactory graphHandlerFactory= null;

	/**
	 * 
	 * @param handler
	 * @param factory
	 */
	public ConsoleUI(AbstractGraphHandler agh, ConsoleEditor editor) {

		super(new DefaultNetworkPainter2D(), agh, editor);

		//		agh.registerXMLHandler(super.getGraphicalDataHandler());
		super.setInfoSetView_(-200,0,200,200);
	}

	public ConsoleUI getGraphicalUI() {
		return this;
	}

	@Override
	public void displayPropertiesImpl(AbstractElementContainer aec) {}
	
	@Override
	public boolean askUserConfirmation(String s){
		return true;
	}

	@Override
	public void repaintGraph() {
		// TODO Auto-generated method stub
		
	}	
}
