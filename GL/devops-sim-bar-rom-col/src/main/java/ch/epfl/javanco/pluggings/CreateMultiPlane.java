package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.PolygonGenerator;

public class CreateMultiPlane extends JavancoTool {

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		int nbPlanes = 5;
		int zOff = 100;
		int nbSwitches = 8;
		PolygonGenerator pg = new PolygonGenerator(nbSwitches, "1,3");	
		
		for (int i = 0 ; i < nbPlanes ; i++) {
			pg.generate(agh);
			for (int j = i*nbSwitches ; j < (i+1)*nbSwitches ; j++) {
				agh.getNodeContainer(j).attribute("pos_z").setValue(i*zOff);
			}
		}

	}

}
