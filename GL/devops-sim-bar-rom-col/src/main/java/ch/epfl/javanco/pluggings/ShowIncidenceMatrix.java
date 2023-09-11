package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class ShowIncidenceMatrix extends JavancoTool {
	
	PcolorGUI gui;

	public ShowIncidenceMatrix() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void internalFrameClosing() {
		gui.close();

	}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		gui = new PcolorGUI(agh.getEditedLayer().getIncidenceMatrix(true));
		gui.showInDialog(f);
	}

}
