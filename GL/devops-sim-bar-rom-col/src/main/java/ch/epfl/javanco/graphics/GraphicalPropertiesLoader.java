package ch.epfl.javanco.graphics;

import java.awt.Color;

import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;


public class GraphicalPropertiesLoader {

	/** SINGLETON */
	private static GraphicalPropertiesLoader singleton = null;

	public static GraphicalPropertiesLoader getGraphicalPropertiesLoader() {
		if (singleton == null) {
			singleton = new GraphicalPropertiesLoader();
		}
		return singleton;
	}
	/** END SINGLETON */

	private int maxNodeSize = 96;
	private int	minNodeSize = 4;
	private float defaultNodeSize = 32;
	private String defaultNodeIcon = "";
	private int defaultLinkWidth = 2;
	private int maxLinkWidth = 10;
	private int defaultLabelFontSize = 10;
	private java.awt.Color defaultLinkColor = Color.BLACK;
	private java.awt.Color defaultNodeColor = Color.WHITE;
	private boolean coloredIcons = false;

	private GraphicalPropertiesLoader() {
		initStaticVariables();
	}

	private void initStaticVariables() {
		String prop = Javanco.getProperty("ch.epfl.javanco.graphics.maxNodeSize");
		if (prop != null) {
			maxNodeSize = Integer.parseInt(prop);
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.minNodeSize");
		if (prop != null) {
			minNodeSize = Integer.parseInt(prop);
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.defaultNodeSize");
		if (prop != null) {
			defaultNodeSize = Integer.parseInt(prop);
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.defaultLinkWidth");
		if (prop != null) {
			defaultLinkWidth = Integer.parseInt(prop);
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.maxLinkWidth");
		if (prop != null) {
			maxLinkWidth = Integer.parseInt(prop);
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.defaultLinkColor");
		if (prop != null) {
			defaultLinkColor = TypeParser.parseColor(prop);
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.defaultNodeColor");
		if (prop != null) {
			defaultNodeColor = TypeParser.parseColor(prop);
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.defaultNodeIcon");
		if (prop != null) {
			defaultNodeIcon = prop;
		}
		prop = Javanco.getProperty("ch.epfl.javanco.graphics.defaultLabelFontSize");
		if (prop != null) {
			defaultLabelFontSize = Integer.parseInt(prop);
		}

		prop = Javanco.getProperty("ch.epfl.javanco.graphics.coloredIcons");
		if (prop != null) {
			coloredIcons = Boolean.parseBoolean(prop);
		}

	}

	public int getDefaultLinkWidth() {
		return defaultLinkWidth;
	}

	public int getMaxLinkWidth() {
		return maxLinkWidth;
	}

	public Color getDefaultLinkColor() {
		return defaultLinkColor;
	}

	public String getDefaultNodeIcon() {
		return defaultNodeIcon;
	}

	public Color getDefaultNodeColor() {
		return defaultNodeColor;
	}
	
	public Color getDefaultNodeLabelColor() {
		return Color.BLACK;
	}
	
	public Color getDefaultLinkLabelColor() {
		return Color.WHITE;
	}

	public boolean getColoredIcons() {
		return coloredIcons;
	}


	/*------------------------------------
	 * LinkContainer
	 * -----------------------------------
	 */

	public int getWidth(LinkContainer cont) {
		int width = applyDefaultWidth(cont.getContainingLayerContainer());
		NetworkAttribute widthA = cont.attribute(XMLTagKeywords.LINK_WIDTH, false);
		if (widthA != null) {
			if (!(widthA.getValue().equals("default"))) {
				return Math.max(1,Math.min(widthA.intValue(), maxLinkWidth));
			}
		}
		return width;
	}

	public java.awt.Color getColor(LinkContainer cont) {
		java.awt.Color color = applyDefaultLinkColor(cont.getContainingLayerContainer());
		NetworkAttribute attColor = cont.attribute(XMLTagKeywords.LINK_COLOR, false);
		if (attColor != null) {
			if (!(attColor.getValue().equals("default"))) {
				return  attColor.colorValue();
			}
		}
		return color;
	}

	public java.awt.Color getColor2(LinkContainer cont) {
		java.awt.Color color = null;
		NetworkAttribute attColor = cont.attribute(XMLTagKeywords.LINK_COLOR2, false);
		if (attColor != null) {
			if (!(attColor.getValue().equals("default"))) {
				return  attColor.colorValue();
			}
		}
		return color;
	}
	
	public java.awt.Color getLabelColor(AbstractElementContainer cont) {
		java.awt.Color color = applyDefaultLabelColor(cont);
		NetworkAttribute attColor = cont.attribute(XMLTagKeywords.LABEL_COLOR, false);
		if (attColor != null) {
			if (!(attColor.getValue().equals("default"))) {
				return  attColor.colorValue();
			}
		}
		return color;
	}	

	public int getSpeed(LinkContainer cont) {
		NetworkAttribute speedA = cont.attribute(XMLTagKeywords.LINK_SPEED, false);
		if (speedA != null) {
			if (!(speedA.getValue().equals("default"))) {
				return speedA.intValue();
			}
		}
		return 0;
	}

	public int getCurveStartAngle(LinkContainer cont) {
		NetworkAttribute curveA = cont.getCurveStartAngle();
		if (curveA != null) {
			if (!(curveA.getValue().equals("default"))) {
				return curveA.intValue();
			}
		}
		return 0;
	}

	public int getCurveEndAngle(LinkContainer cont) {
		NetworkAttribute curveA = cont.getCurveEndAngle();
		if (curveA != null) {
			if (!(curveA.getValue().equals("default"))) {
				return curveA.intValue();
			}
		}
		return -getCurveStartAngle(cont);
	}

	public int getCurveStart(LinkContainer cont) {
		NetworkAttribute curveA = cont.getCurveStart();
		if (curveA != null) {
			if (!(curveA.getValue().equals("default"))) {
				return curveA.intValue();
			}
		}
		return 0;
	}

	public int getCurveEnd(LinkContainer cont) {
		NetworkAttribute curveA = cont.getCurveEnd();
		if (curveA != null) {
			if (!(curveA.getValue().equals("default"))) {
				return curveA.intValue();
			}
		}
		return getCurveStart(cont);
	}

	public String getDash(LinkContainer cont) {
		NetworkAttribute dash = cont.attribute(XMLTagKeywords.LINK_DASH, false);
		if (dash != null) {
			if (dash.getValue().equals("default")) {
				return "";
			} else {
				return dash.getValue();
			}
		} else {
			return "";
		}
	}

	private int[][] parseRoutingArray(String str) {
		// Sting.split(...) takes a string representing
		// a regexp as argument, that's why we need to escape
		// the pipe character
		String[] temp = str.split("\\|");
		int[][] result = new int[temp.length][];
		for(int i = 0; i < temp.length; i++) {
			result[i] = parseIntArray(temp[i]);
		}
		return result;
	}

	private int[] parseIntArray(String str) {
		String[] temp = str.split(",");
		int[] result = new int[temp.length];
		for(int i = 0; i < temp.length; i++) {
			try {
				result[i] = Integer.parseInt(temp[i]);
			}
			catch(java.lang.NumberFormatException e) {
				System.out.println("parseIntArray in GraphicalPropertiesLoader: Failed to pars '" + temp[i] + "' as an integer");
			}
		}
		return result;
	}

	public int[][] getLinkRouting(LinkContainer cont) {
		NetworkAttribute att = cont.attribute(XMLTagKeywords.LINK_ROUTING, false);
		if(att != null) {
			return this.parseRoutingArray(att.getValue());
		}
		return null;
	}

	/*------------------------------------
	 * NodeContainer
	 * -----------------------------------
	 */

	public int getNodeSize(NodeContainer cont) {
		int size = (int)defaultNodeSize;
		NetworkAttribute att = cont.getNodeSizeAttribute();
		if (att != null) {
			if (!(att.getValue().equals("default"))) {
				size = Math.max(minNodeSize, Math.min(att.intValue(), maxNodeSize));
			}
		}
		return size;
	}

	public String getNodeIcon(NodeContainer cont) {
		String icon = applyDefaultNodeIcon(cont.getContainingLayerContainer());
		NetworkAttribute att = cont.getNodeIconAttribute();
		if (att != null) {
			if (!(att.getValue().equals("default"))) {
				icon = att.getStringValue();
			}
		}
		return icon;
	}
	
	public boolean getVisibleId(NodeContainer cont) {
		NetworkAttribute att = cont.getVisibleAttribute();
		if (att != null) {
			return att.booleanValue();
		}
		return true;	
	}

	public java.awt.Color getColor(NodeContainer cont) {
		java.awt.Color color = applyDefaultNodeColor(cont.getContainingLayerContainer());
		NetworkAttribute attColor = cont.getNodeColorAttribute();
		if (attColor != null) {
			if (!(attColor.getValue().equals("default"))) {
				return  attColor.colorValue();
			}
		}
		return color;
	}

	public int getLabelFontSize(NodeContainer cont) {
		NetworkAttribute attSize = cont.getLabelFontSizeAttribute();
		if (attSize != null) {
			if (!(attSize.getValue().equals("default"))) {
				return attSize.intValue();
			}
		}
		return defaultLabelFontSize;
	}


	/*------------------------------------
	 * LayerContainer
	 * -----------------------------------
	 */



	private int applyDefaultWidth(LayerContainer layerContainer) {
		NetworkAttribute widthD = layerContainer.getDefaultLinkWidth();
		if (widthD != null) {
			return Math.max(1,Math.min(widthD.intValue(), getMaxLinkWidth()));
		}	else {
			return getDefaultLinkWidth();
		}
	}

	private String applyDefaultNodeIcon(LayerContainer layerContainer) {
		NetworkAttribute icon = layerContainer.getDefaultNodeIcon();
		if (icon != null) {
			return icon.getStringValue();
		}	else {
			return getDefaultNodeIcon();
		}
	}

	private java.awt.Color applyDefaultLinkColor(LayerContainer layerContainer) {
		NetworkAttribute colorD = layerContainer.getDefaultLinkColor();
		if (colorD != null) {
			java.awt.Color defAttColor = colorD.colorValue();
			if (defAttColor != null) {
				return defAttColor;
			}
		}
		return getDefaultLinkColor();
	}

	private java.awt.Color applyDefaultNodeColor(LayerContainer layerContainer) {
		NetworkAttribute colorD = layerContainer.getDefaultNodeColor();
		if (colorD != null) {
			java.awt.Color defAttColor = colorD.colorValue();
			if (defAttColor != null) {
				return defAttColor;
			}
		}
		return getDefaultNodeColor();
	}
	
	private java.awt.Color applyDefaultLabelColor(AbstractElementContainer aec) {
		LayerContainer layerContainer = aec.getContainingLayerContainer();
		NetworkAttribute colorD = layerContainer.getDefaultLabelColor();
		if (colorD != null) {
			java.awt.Color defAttColor = colorD.colorValue();
			if (defAttColor != null) {
				return defAttColor;
			}
		}
		if (aec instanceof LinkContainer) {
			return getDefaultLinkLabelColor();
		} else {
			return getDefaultNodeLabelColor();
		}
	}	
	
}
