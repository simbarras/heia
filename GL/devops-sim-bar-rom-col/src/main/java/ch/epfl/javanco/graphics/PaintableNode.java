package ch.epfl.javanco.graphics;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.EventObject;

import org.dom4j.Element;

import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class PaintableNode extends PaintableObject implements Movable, Linkable {

	public PaintableNode(NodeContainer node) {
		super(node);
	}
	@SuppressWarnings("all")
	public void init(NodeContainer node) {
		LayerContainer layerContainer = node.getContainingLayerContainer();
		if (node == null) {
			valid = false;
			return;
		}

		nodeContainer = node;

		layer = layerContainer.getName();

		try {
			Point p = node.getCoordinate();

			posX = p.x;
			posY = p.y;

			id   = node.getIndex();//node.attribute(XMLTagKeywords.ID).intValue();
		}
		catch (Exception e) {
			valid = false;
		}

		if (node.isZDefined()) {
			posZ = node.getZ();
		}else {

			/*	NetworkAttribute attr = node.attribute(XMLTagKeywords.POS_Z, false);
		if (attr != null && attr.getValue() != null) {
			posZ = attr.positionValue();
		}
		else {*/
			LayerContainer lc = node.getContainingLayerContainer();
			posZ = lc.getZ();
			//	posZ = lc.attribute(XMLTagKeywords.POS_Z).positionValue();
		}

		GraphicalPropertiesLoader loader = GraphicalPropertiesLoader.getGraphicalPropertiesLoader();

		seeId = loader.getVisibleId(node);
		size = loader.getNodeSize(node);
		icon = loader.getNodeIcon(node);
		color = loader.getColor(node);
		labelColor = loader.getLabelColor(node);
		labelFontSize = loader.getLabelFontSize(node);

		if (icon != "") {
			URL url = getClass().getResource("/images/" + icon);
			if (url != null) {
				iconImage = Toolkit.getDefaultToolkit().getImage(url);
				icon = url.getPath();
			} else {
				File file = new File(Javanco.getProperty(Javanco.JAVANCO_DIR_IMAGES_PROPERTY) + "/" + icon);
				if (file.exists()) {
					iconImage = Toolkit.getDefaultToolkit().getImage(file.getPath());
					icon = file.getPath();
				}
				else {
					file = new File(icon);
					if (file.exists()) {
						iconImage = Toolkit.getDefaultToolkit().getImage(file.getPath());
						icon = file.getPath();
					}
					else {
						iconImage = null;
						icon = "";
					}
				}
			}
		}

		String labelA = node.attribute(XMLTagKeywords.LABEL).getValue();
		if (labelA != null) {
			label = labelA;
		}
	}

	public boolean intersects(Rectangle r) {
		return (r.intersects(posX,posY,1,1));
	}

	public void saveNewPosition(EventObject e) {
		AbstractElementContainer cont = getElementContainer();
		boolean eventEnabled = cont.getAbstractGraphHandler().isEventEnabled();
		if (eventEnabled) {
			cont.getAbstractGraphHandler().setModificationEventEnabledWithoutCallingBigChanges(false);
		}
		cont.attribute(XMLTagKeywords.POS_X).setValue(""+getX(), e);
		cont.attribute(XMLTagKeywords.POS_Y).setValue(""+(getY()), e);
		if (eventEnabled) {
			cont.getAbstractGraphHandler().setModificationEventEnabledWithoutCallingBigChanges(true);
		}
		cont.fireElementEvent();  //explicit
		
	}

	/*	private Color extractColor(LayerContainer layer, NodeContainer node) {
		NetworkAttribute nodeColorAtt = node.attribute(XMLTagKeywords.NODE_COLOR);
		if (nodeColorAtt == null) {
			return Color.WHITE;
		} else {
			Color nodeColor = nodeColorAtt.colorValue();
			if (nodeColor != null) {
				return nodeColor;
			} else {
				if (nodeColorAtt.toString().equals("default")) {
					Color defAttColor = layer.attribute(XMLTagKeywords.DEFAULT_NODE_COLOR).colorValue();
					if (defAttColor != null) {
						return defAttColor;
					} else {
						return Color.WHITE;
					}
				} else {
					return Color.WHITE;
				}
			}
		}
	}*/

	@Override
	public String toString() {
		return super.toString() + "__id = " + id + " size = " + size;
	}

	private class PaintableNodeClone extends PaintableNode {
		private int alternateZCoord = 0;

		private PaintableNodeClone(NodeContainer nc, int alternateZCoord) {
			super(nc);
			this.alternateZCoord = alternateZCoord;
		}

		@Override
		public void init(NodeContainer node) {
			super.init(node);
			this.posZ = this.alternateZCoord;
		}

		@Override
		public String toString() {
			return super.toString() + " (clone)";
		}
	}

	/**
	 * Clones a given PaintableNode to another layer or returns null if it is allready on the given layer. This can be used to display a node at the end of a link if the link is on another layer as the node.
	 * @param lc The layer where the node should appear
	 * @author pvogt
	 */
	public PaintableNode cloneToLayer(PaintableLayer pl) {
		if (layer.equals(pl.getName())) {
			return null;
		} else {
			PaintableNodeClone pnc = new PaintableNodeClone(nodeContainer, pl.getLayerZ());
			pnc.init(nodeContainer);
			return pnc;
		}
	}

	public void setX(int x) { posX = x; }
	public void setY(int y) { posY = y; }
	public void setZ(int z) { posZ = z; }
	public int getX() { return posX; }
	public int getY() { return posY; }
	public int getZ() { return posZ; }
	public int getId() { return id; }
	public int posX = -1;
	public int posY = -1;
	public int posZ = -1;
	public int id   = -1;
	public int size = -1;
	public String icon = "";
	public int labelFontSize = -1;
	public int alpha = 255;
	public Color color = Color.WHITE;
	public Color labelColor;
	public String label = "";
	public String layer = "";
	public int labelRelativePositionX = 0;
	public int labelRelativePositionY = 35;
	public NodeContainer nodeContainer = null;
	public Element associatedXMLDescription = null;
	public boolean valid = true;
	public boolean seeId = true;

	private Image iconImage = null;
	public Image getIconImage() {
		return iconImage;
	}

	public boolean isMoving = false;
}
