package ch.epfl.javanco.pluggings;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;

import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class HPEPF extends JavancoTool {

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}
	
	int socketSpacing = 35;
	int chassisHeight = 100;
	
	int trayPerChassis = 4;	
	int socketPerTray = 4;
	int chassisPerSU = 4;
	int SUperRow = 4;
	int rowPerColumn = 1;
	int column = 1;

	@Override
	public void run(final AbstractGraphHandler agh, Frame f) {
		Thread t = new Thread() {
			public void run() {
				agh.clear();
				int yOffset = 0;
				int xOffset = 0;
				
				for (int col = 0 ; col < column ; col++) {
					for (int row = 0 ; row < rowPerColumn ; row++) {
						ArrayList<NodeContainer> routersOfrow = new ArrayList<NodeContainer>();
						row(agh, xOffset, yOffset, routersOfrow);
					}
				}
			}
		};

		t.start();
		// TODO Auto-generated method stub


		

		

	}
	
	private Dimension row(AbstractGraphHandler agh, int xOffset, int yOffset, ArrayList<NodeContainer> routers) {
		Dimension finalDim = new Dimension();
		ArrayList<ArrayList<NodeContainer>> routerArray = new ArrayList<ArrayList<NodeContainer>>();
		int localXOffset = xOffset;
		for (int su = 0 ; su < SUperRow ; su++) {					
			ArrayList<NodeContainer> routersOfSU = new ArrayList<NodeContainer>();
			Dimension dim = superUnit(agh, localXOffset, yOffset, routersOfSU);
			finalDim.height = dim.height;
			finalDim.width = (int)((double)SUperRow*(double)dim.width*1.2);
			localXOffset += (int)((double)dim.width * 1.2d);
			routerArray.add(routersOfSU);
		}
		for (int i = 0 ; i < routerArray.get(0).size() ; i++) {
			ArrayList<NodeContainer> group = new ArrayList<NodeContainer>();
			for (int j = 0 ; j < routerArray.size() ; j++) {
				group.add(routerArray.get(j).get(i));
				routers.add(routerArray.get(j).get(i));
			}
			ArrayList<LinkContainer> links = agh.connectAll(group);
			for (LinkContainer l : links) {
				l.attribute("link_curve_start").setValue("30");
				l.attribute("link_curve_end").setValue("30");
				l.attribute("link_color").setValue("0,255,0");
			}	
		}
		
		return finalDim;		
	}
	
	private Dimension superUnit(AbstractGraphHandler agh, int xOffset, int yOffset, ArrayList<NodeContainer> routers) {
		Dimension finalDim = new Dimension();
		ArrayList<ArrayList<NodeContainer>> routerArray = new ArrayList<ArrayList<NodeContainer>>();
		for (int cha = 0 ; cha < chassisPerSU ; cha++) {
			ArrayList<NodeContainer> routersOfChassis = new ArrayList<NodeContainer>();
			Dimension dim = chassis(agh, xOffset, yOffset+(cha*chassisHeight*2), routersOfChassis);
			finalDim.width = dim.width;
			routerArray.add(routersOfChassis);
		}	
		for (int i = 0 ; i < routerArray.get(0).size() ; i++) {
			ArrayList<NodeContainer> group = new ArrayList<NodeContainer>();
			for (int j = 0 ; j < routerArray.size() ; j++) {
				group.add(routerArray.get(j).get(i));
				routers.add(routerArray.get(j).get(i));
			}
			ArrayList<LinkContainer> links = agh.connectAll(group);
			for (LinkContainer l : links) {
				l.attribute("link_curve_start").setValue("-30");
				l.attribute("link_curve_end").setValue("-30");
				l.attribute("link_color").setValue("255,0,0");
			}	
		}

		finalDim.height = chassisPerSU*2*chassisHeight;
		return finalDim;
	}
	
	private Dimension chassis(AbstractGraphHandler agh, final int xOffset, int yOffset, ArrayList<NodeContainer> routers) {
		int localXoffset = xOffset;
		for (int tray = 0 ; tray < trayPerChassis ; tray++) {
			NodeContainer nc = agh.newNode(localXoffset, yOffset);	
			routers.add(nc);
			localXoffset += socketSpacing*(socketPerTray+2);
		}
		localXoffset = xOffset;
		for (int tray = 0 ; tray < trayPerChassis ; tray++) {
			ArrayList<NodeContainer> sockets = new ArrayList<NodeContainer>();
			for (int socket = 0; socket < socketPerTray ; socket++) {
				int xNeg = -socketSpacing*(socketPerTray-1)/2;
				NodeContainer nc2 = agh.newNode(localXoffset + xNeg + socketSpacing*socket, yOffset + chassisHeight);
				sockets.add(nc2);
				nc2.attribute("node_size").setValue("24");
				nc2.attribute("node_color").setValue("150, 150, 150");
				for (int k = 0 ; k < MoreMaths.ceilDiv(trayPerChassis, socketPerTray); k++) {
					LinkContainer lc = agh.newLink(nc2, routers.get((socket + (k*socketPerTray)) % (routers.size())));
					lc.attribute("link_color").setValue("0,0,200");
				}
			}
			ArrayList<LinkContainer> links = agh.connectAll(sockets);
			for (LinkContainer l : links) {
				l.attribute("link_curve_start").setValue("-50");
				l.attribute("link_curve_end").setValue("-50");
			}
			localXoffset += socketSpacing*(socketPerTray+2);
		}
		return new Dimension(localXoffset - xOffset, chassisHeight);
	}

}
