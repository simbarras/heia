package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.javanco.base.AbstractGraphHandler;

public class CreateAllLinksOnLayer extends JavancoTool {

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {

		for (int i = 0 ; i < agh.getHighestNodeIndex() ; i++) {
			for (int j = i+1 ; j <= agh.getHighestNodeIndex() ; j++) {
				if (agh.getEditedLayer().getLinkContainer(i,j) == null) {
					agh.newLink(i,j);
				}
			}
		}

	}

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub
		
	}

}
