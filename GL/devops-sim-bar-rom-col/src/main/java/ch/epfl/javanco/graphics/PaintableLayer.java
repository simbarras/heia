package ch.epfl.javanco.graphics;

import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class PaintableLayer extends PaintableObject implements Clickable {
	private String name = null;
	public String getName() { return name; }
	private int layerZ = 0;
	public int getLayerZ() { return layerZ; }

	public PaintableLayer(LayerContainer lc) {
		super(lc);
	}

	public void init(LayerContainer lc) {
		name = lc.getKey();
		layerZ = 0;
		try {
			layerZ = lc.attribute(XMLTagKeywords.POS_Z, false).intValue();
		}
		catch(Exception e) {}
	}
	
	public void setPointed(boolean b) {
	}

}
