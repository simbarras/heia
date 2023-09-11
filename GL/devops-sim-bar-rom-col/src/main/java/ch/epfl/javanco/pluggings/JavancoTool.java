package ch.epfl.javanco.pluggings;

import java.awt.Frame;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.ui.swing.multiframe.InternalFrameBasedUI;



public abstract class JavancoTool implements InternalFrameBasedUI.SwingEditorInternalFrameClosingListener {

	public abstract void run(AbstractGraphHandler agh, Frame f);

	
}
