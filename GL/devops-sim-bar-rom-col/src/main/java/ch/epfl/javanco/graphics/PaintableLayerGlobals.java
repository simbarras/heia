package ch.epfl.javanco.graphics;

import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class PaintableLayerGlobals {
	public boolean layersInit = false;
	public int layersMinX = 0;
	public int layersMaxX = 0;
	public int layersMinY = 0;
	public int layersMaxY = 0;
	
	private static final String posxTAG = XMLTagKeywords.POS_X.toString();	
	private static final String posyTAG = XMLTagKeywords.POS_Y.toString();		

	public void updateLevelsSize(NodeContainer nc) {
		GraphicalPropertiesLoader loader = GraphicalPropertiesLoader.getGraphicalPropertiesLoader();
		int radius = loader.getNodeSize(nc) / 2;
		try {
			int x = nc.attribute(posxTAG, false).intValue();
			if(layersInit || x - radius < layersMinX) {
				layersMinX = x - radius;
			}
			if(layersInit || x + radius > layersMaxX) {
				layersMaxX = x + radius;
			}
		}
		catch(Exception e) {}
		try {
			int y = nc.attribute(posyTAG, false).intValue();
			if(layersInit || y - radius < layersMinY) {
				layersMinY = y - radius;
			}
			if(layersInit || y + radius > layersMaxY) {
				layersMaxY = y + radius;
			}
		}
		catch(Exception e) {}
		layersInit = false;
	}

	public void initLevels() {
		layersInit = true;
		// In principle this should not be necessary but in case we
		// have a bug it's better if it is as deterministic as possible.
		layersMinX = 0;
		layersMaxX = 0;
		layersMinY = 0;
		layersMaxY = 0;
	}
}
