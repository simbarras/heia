package ch.epfl.javanco.ui.web;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.DefaultNetworkPainter2D;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.ui.AbstractInteractiveGraphicalUI;
import ch.epfl.javanco.ui.GlobalInterface;

public class WebUI extends AbstractInteractiveGraphicalUI {

	public WebUI(AbstractGraphHandler agh, GlobalInterface face) {
		super(new DefaultNetworkPainter2D(), agh, face);
	}

	public WebUI getGraphicalUIManager() {
		return this;
	}

	@Override
	public void displayPropertiesImpl(AbstractElementContainer aec) {}
	
	@Override
	public boolean askUserConfirmation(String s){
		return true;
	}

	@Override
	public void repaintGraph() {}

}
