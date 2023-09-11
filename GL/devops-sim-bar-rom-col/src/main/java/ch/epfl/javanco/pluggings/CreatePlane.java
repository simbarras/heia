package ch.epfl.javanco.pluggings;

import java.awt.Frame;

import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.topology.PolygonGenerator;

public class CreatePlane extends JavancoTool {

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(AbstractGraphHandler agh, Frame f) {
		
		agh.clearLayers(true);
	
		int nbPlanes = 5;
		int zOff = 20;
		int nbSwitches = 30;
		int clientPerNode = 2;
		int ray1 = 400;
		int ray2 = 490;
		
		PolygonGenerator gen = new PolygonGenerator(nbSwitches, "1, 5,9, 16");
		gen.setRadius(ray1);
		
		for (int i = 0 ; i < nbPlanes ; i++) {
			gen.generate(agh);
			for (int j = i*nbSwitches ; j < (i+1)*nbSwitches ; j++) {
				agh.getNodeContainer(j).attribute("pos_z").setValue(zOff*i);
			}
			if (i == nbPlanes - 2) {
				for (LinkContainer lc : agh.getLinkContainers()) {
					lc.attribute("link_color").setValue("200,200,200");
				}
			}
		}
		int numNodesBase = nbSwitches;
		double angle1 = 2*Math.PI / (double)numNodesBase;
		double angle2 = angle1/(double)clientPerNode;
		double startAngle2;
		if (clientPerNode % 2 == 0) {
			startAngle2 = -((double)(clientPerNode-1)/2d)*angle2;
		} else {
			startAngle2 = -((double)(clientPerNode-1)/2d)*angle2;
		}		
		int index = 0;
		int[] positions = TypeParser.parseIntArray("0:" + (numNodesBase-1));
		for (int i = 0 ; i < numNodesBase ; i++) {
			for (int j = 0 ; j < clientPerNode ; j++) {
				double x = -Math.cos(startAngle2 + (angle2*index))*ray2;
				double y = Math.sin(startAngle2 + (angle2*index))*ray2;
				NodeContainer nn = agh.newNode((int)x, (int)y);
				nn.attribute("node_color").setValue("220,100,200");
				nn.attribute("pos_z").setValue(nbPlanes*zOff/2);
		//		nn.attribute("node_size").setValue(30);
				index++;
				for (int k = 0 ; k < nbPlanes ; k++) {
					LinkContainer l = agh.newLink(positions[i]+(k*nbSwitches), nn.getIndex());
					if (k < nbPlanes-1) {
						l.attribute("link_color").setValue("200,200,200");
					}
					
				}
			}
		}		

	}

}
