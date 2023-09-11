package ch.epfl;

import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.ui.console.ConsoleEditor;

public class JavancoTUI {
	public static void main(String[] args) throws Exception {
		Javanco.initJavanco();
		new ConsoleEditor(Javanco.getDefaultGraphHandlerFactory());
	}
}
