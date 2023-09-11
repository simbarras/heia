package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;

public class TransformToDirected extends JavancoTool {

	public TransformToDirected() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void internalFrameClosing() {}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		for (LinkContainer lc : agh.getLinkContainers()) {
			LayerContainer lay = lc.getContainingLayerContainer();
			if (lay.getLinkContainer(lc.getEndNodeIndex(), lc.getStartNodeIndex()) == null) {
				LinkContainer c = agh.newLink(lc.getEndNodeIndex(), lc.getStartNodeIndex(), lay);
				c.attribute("directed").setValue("true");
				c.attribute("link_curve_start").setValue("13");
				c.attribute("link_curve_end").setValue("13");
				lc.attribute("link_curve_start").setValue("13");
				lc.attribute("link_curve_end").setValue("13");
				lc.attribute("directed").setValue("true");
			}
		}
	}

}
