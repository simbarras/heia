package ch.epfl.javanco.graphics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

import org.dom4j.Element;

import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class PaintableLink extends PaintableObject implements Clickable, Curvable {


	public PaintableLink(LinkContainer link) {
		super(link);
	}
	
	private static final String useOffsetTAG = XMLTagKeywords.USE_OFFSET.toString();
	private static final String offsetTAG = XMLTagKeywords.OFFSET.toString();
	private static final String directedTAG = XMLTagKeywords.DIRECTED.toString();	

	public void init(LinkContainer link) {
		NetworkAttribute attt = link.attribute("visible", false);
		if (attt != null) {
			if (attt.booleanValue() == false) {
				valid = false;
				return;
			}
		}
		
		LayerContainer layerContainer = link.getContainingLayerContainer();

		layerZ = layerContainer.getZ();

		//	layerZ = layerContainer.attribute(XMLTagKeywords.POS_Z).positionValue();

		linkContainer = link;

		layer = layerContainer.getName();
		label = link.attribute(XMLTagKeywords.LABEL).getValue();

		try {
			orig = link.getStartNodeIndex(); //link.attribute(XMLTagKeywords.ORIG).intValue();
			dest = link.getEndNodeIndex(); //link.attribute(XMLTagKeywords.DEST).intValue();
		}
		catch (Exception e) {
			valid = false;
		}
		NetworkAttribute useOffset = layerContainer.attribute(useOffsetTAG, false);
		if ((useOffset != null) && (useOffset.booleanValue())) {
			NetworkAttribute offsetA = link.attribute(offsetTAG, false);
			if (offsetA != null) {
				offset = offsetA.intValue();
			}
		}

		NetworkAttribute directedA = link.attribute(directedTAG, false);
		if ((directedA != null) && (directedA.booleanValue())) {
			directed = true;
		}

		GraphicalPropertiesLoader loader = GraphicalPropertiesLoader.getGraphicalPropertiesLoader();
		width           = loader.getWidth(link);
		color           = loader.getColor(link);
		color2			= loader.getColor2(link);
		labelColor	    = loader.getLabelColor(link);
		speed			= loader.getSpeed(link);
		curveStart		= loader.getCurveStart(link);
		curveEnd		= loader.getCurveEnd(link);
		curveStartAngle = loader.getCurveStartAngle(link);
		curveEndAngle   = loader.getCurveEndAngle(link);
		routing = loader.getLinkRouting(link);
		endNodeSize     = loader.getNodeSize(link.getEndNodeContainer());
		startNodeSize   = loader.getNodeSize(link.getStartNodeContainer());

		loadDash(link, loader);

		key = orig + "-" + dest;

		sameNode = link.getStartNodeIndex() == link.getEndNodeIndex();
	}


	/**
	 * Fill the attributes dash and isDashPerCent from the property "dash".
	 * <BR>#author fmoulin
	 * 
	 * @param link
	 * @param loader
	 */
	private void loadDash(LinkContainer link, GraphicalPropertiesLoader loader) {
		String d = loader.getDash(link);

		if (d.isEmpty()) {
			isDashPerCent = false;
			dash = null;
		}

		LinkedList<Float> ll = new LinkedList<Float>();

		isDashPerCent = d.contains("%");
		if (isDashPerCent) {
			d = d.replace("%", " ");
		}

		for(String s : d.split(" ")) {
			if (s != null && !s.isEmpty()) {
				try {
					Float nb = Float.parseFloat(s);
					ll.add(nb);
				}
				catch(NumberFormatException e) {

				}
			}
		}

		dash = new float[ll.size()];
		for (int i = 0; i < ll.size(); i++) {
			dash[i] = ll.get(i);
		}
	}


	public PaintableLink() {
		super(null);
	}

	public boolean intersects(Rectangle r) {
		int upleftx = Math.min(startX,endX);
		int uplefty = Math.min(startY,endY);
		int downrightx = Math.max(startX,endX);
		int downrighty = Math.max(startY,endY);
		if (upleftx == downrightx) {
			upleftx--;
		}
		if (uplefty == downrighty) {
			uplefty--;
		}
		return (r.intersects(upleftx, uplefty, downrightx - upleftx, downrighty - uplefty));
	}

	@Override
	public String toString() {
		return super.toString() + "__orig = " + orig + " dest = " + dest;
	}

	public void setCurveA(int i) {
		curveStart = i;
	}

	public void setCurveB(int i) {
		curveEnd = i;
	}

	public void setCurveAngleA(int i) {
		curveStartAngle = i;
	}

	public void setCurveAngleB(int i) {
		curveEndAngle = i;
	}


	public Point getDirection() {
		return new Point(endX - startX, endY - startY);
	}

	public void saveCurve() {
		AbstractElementContainer cont = getElementContainer();
		cont.attribute(XMLTagKeywords.LINK_CURVE_START).setValue(""+curveStart);
		cont.attribute(XMLTagKeywords.LINK_CURVE_END).setValue(""+curveEnd);
		cont.attribute(XMLTagKeywords.LINK_CURVE_START_ANGLE).setValue(""+curveStartAngle);
		cont.attribute(XMLTagKeywords.LINK_CURVE_END_ANGLE).setValue(""+curveEndAngle);
		cont.linkAttribute(XMLTagKeywords.LINK_CURVE_START, "main_description");
		cont.linkAttribute(XMLTagKeywords.LINK_CURVE_END, "main_description");
		cont.linkAttribute(XMLTagKeywords.LINK_CURVE_START_ANGLE, "main_description");
		cont.linkAttribute(XMLTagKeywords.LINK_CURVE_END_ANGLE, "main_description");
	//	return new int[]{curveStart, curveEnd, curveStartAngle, curveEndAngle};
	}
	
	@Override
	public int[] getCurve() {
		AbstractElementContainer cont = getElementContainer();
		return new int[]{ cont.attribute(XMLTagKeywords.LINK_CURVE_START).intValue(),
				cont.attribute(XMLTagKeywords.LINK_CURVE_END).intValue(),
				cont.attribute(XMLTagKeywords.LINK_CURVE_START_ANGLE).intValue(),
				cont.attribute(XMLTagKeywords.LINK_CURVE_END_ANGLE).intValue()
		};
	}

	public int offset;
	public int startX;
	public int startY;
	public int endX;
	public int endY;
	public int layerZ;
	public int orig;
	public int dest;
	public int width;
	public int endNodeSize;
	public int startNodeSize;
	public boolean directed = false;
	public Color color2;
	public Color color 		= Color.DARK_GRAY;
	public int speed;
	public int curveStart;
	public int curveEnd;
	public int curveStartAngle;
	public int curveEndAngle;
	public int[] curvePrev;
	public String key;
	public String layer = "";
	public String label = "";
	public LinkContainer linkContainer;
	public Element associatedXMLDescription;
	public boolean valid = true;
	public boolean sameNode = false;
	public Color labelColor;

	public float[] dash;
	public boolean isDashPerCent = false;

	public int[][] routing = null;

	public Rectangle getRectangle() {
		int left;
		int down;
		int width;
		int height;
		if (startX < endX) {
			left = startX;
			width = endX - startX;
		} else {
			left = endX;
			width = startX - endX;
		}
		if (startY < endY) {
			down = startY;
			height = endY - startY;
		} else {
			down = endY;
			height = startY - endY;
		}
		return new Rectangle(left, down, width+1, height+1);
		
	}
}
