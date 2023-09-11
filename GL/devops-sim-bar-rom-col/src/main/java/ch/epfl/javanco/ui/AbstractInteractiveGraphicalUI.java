package ch.epfl.javanco.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;
import java.util.List;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.EditedLayerEvent;
import ch.epfl.javanco.event.EditedLayerListener;
import ch.epfl.javanco.event.SelectedTypeChangedEvent;
import ch.epfl.javanco.event.SelectedTypeChangedListener;
import ch.epfl.javanco.graphics.Clickable;
import ch.epfl.javanco.graphics.Movable;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.graphics.PaintableLink;
import ch.epfl.javanco.graphics.PaintableNode;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public abstract class AbstractInteractiveGraphicalUI extends AbstractDynamicGraphicalUI
implements EditedLayerListener, SelectedTypeChangedListener {

	private float					 			 zoom				 	 = 100f;
	private boolean isNodeSelected = false;
	private boolean isLinkSelected = false;

	public AbstractInteractiveGraphicalUI(NetworkPainter painter,
			AbstractGraphHandler agh,
			GlobalInterface superInterface) {
		super(painter, agh, superInterface);
	}

	public abstract void displayPropertiesImpl(AbstractElementContainer object);
	public abstract boolean askUserConfirmation(String s);

	public void displayProperties(Clickable object) {
		if (object != null) {
			displayPropertiesImpl(object.getElementContainer());
		} else {
			displayPropertiesImpl(getAssociatedAbstractGraphHandler().getEditedLayer());
		}
	}	

	public void editedLayerChanged(EditedLayerEvent e) {
		refreshAndRepaint();
	}

	public void selectedTypeChanged(SelectedTypeChangedEvent e) {
		isLinkSelected = e.isLinkSelected();
		isNodeSelected = e.isNodeSelected();
	}

	public boolean isNodeSelected() {
		return isNodeSelected;
	}
	public boolean isLinkSelected() {
		return isLinkSelected;
	}

	public Movable getMovedNode() {
		return graphInfoSet.movedNode;
	}

	public void setMovedNode(PaintableNode m) {
		graphInfoSet.setMovedNode(m);
	}

	public void setSizeModificator(float size) {
		if (size != 0) {
			graphInfoSet.sizeModificator = size;
		}
		super.refreshAndRepaint();
	}

	public float getSizeModificator() {
		return graphInfoSet.sizeModificator;
	}

	public PaintableLink getBeeingConstructedLink() {
		return graphInfoSet.newLink;
	}

	public void setBeeingConstructedLink(PaintableLink link) {
		graphInfoSet.newLink = link;
		repaintGraph();
	}

	public void resizeInfoSetViewHorizontal(int incr_x) {
		graphInfoSet.incrementWidth(incr_x);
		prepareForSave();
		repaintGraph();
	}

	public void resizeInfoSetViewVertical(int incr_y) {
		graphInfoSet.incrementHeight(incr_y);
		repaintGraph();
	}

	public void moveInfoSetViewHorizontal(int coeff) {
		int deplx = Math.round((graphInfoSet.getViewWidth()/100f)*coeff);
		graphInfoSet.incrementViewOriginX(deplx);
		repaintGraph();
	}

	public void moveInfoSetViewVertical(int coeff) {
		int deply = Math.round((graphInfoSet.getViewHeight()/100f)*coeff);
		graphInfoSet.incrementViewOriginY(deply);
		//	refreshAllDisplay();
		repaintGraph();
	}

	public void updateBackgroundImagePosition(int axis, int incr) {
		if (axis == 0) {
			graphInfoSet.nodeSpaceImageOriginX += incr;
		} else if (axis == 1) {
			graphInfoSet.nodeSpaceImageOriginY += incr;
		}
		repaintGraph();
	}

	public void updateBackgroundImageSize(int axis, int incr) {
		if (axis == 0) {
			graphInfoSet.nodeSpaceImageWidth += incr;
		} else if (axis == 1) {
			graphInfoSet.nodeSpaceImageHeight += incr;
		}
		repaintGraph();
	}

	public void setZoom(float zoom) {
		float fact = (this.zoom/zoom);


		this.modifyDisplayedElementSize(1/fact);
		
		int h = graphInfoSet.getViewHeight();
		int w = graphInfoSet.getViewWidth();

		int newHeight = Math.round((h)*fact);
		int newWidth  = Math.round((w)*fact);
		int x = Math.round((float)(w  - newWidth)  / (float)2) + graphInfoSet.getViewX();
		int y = Math.round((float)(h - newHeight) / (float)2) + graphInfoSet.getViewY();
		
		graphInfoSet.setView(x, y, newWidth, newHeight);

		this.zoom = zoom;
		repaintGraph();
	}

	public int getZoom() {
		return (int)zoom;
	}

	public void zoom(int dir) {
		float fact;
		if (dir > 0) {
			fact = 3f/4f;
		} else if (dir < 0) {
			fact = 4f/3f;
		} else {
			fact = 1;
		}

		setZoom((zoom*fact));
	}

	public void resetDisplayedElementSize() {
		super.resetDisplayedElementSize();
		repaintGraph();
	}
	
	public void modifyDisplayedElementSize(float factor) {
		super.modifyDisplayedElementSize(factor);
		repaintGraph();
	}	
	

	public void modifyDisplayedElementSize(int increment) {
		float factor = (float)Math.pow(.95, increment);
		modifyDisplayedElementSize(factor);
	}

	public void setBackgroundColor(Color c) {
		graphInfoSet.backgroundColor = c;
		repaintGraph();
	}

	public Color getBackgroundColor() {
		return graphInfoSet.backgroundColor;
	}

	public boolean setBackgroundImage(JavancoFile file) {
		graphInfoSet.setBackImage(file.getAbsolutePath());
		graphInfoSet.setImageAsView();
		return true;
	}

	/**
	 * Updates all coordinates in such a way that this is equivalent to a zoom
	 * centered in the middle of the view.
	 * @author pvogt
	 */
	protected void refit(float factor, boolean saveToXML, EventObject e) {
		Rectangle2D.Float from = graphInfoSet.getView2DRectangle();

		float centerx = from.x + from.width / 2.0f;
		float centery = from.y + from.height / 2.0f;

		Rectangle2D.Float to = new Rectangle2D.Float(
				centerx + (from.x - centerx) * factor,
				centery + (from.y - centery) * factor,
				from.width * factor,
				from.height * factor
		);

		refit(from, to, saveToXML, e);
	}

	/**
	 * Updates all coordinates in such a way that is equivalent to the
	 * linear transformation transforming <code>from</code> into <code>to</code>
	 */
	protected void refit(Rectangle2D.Float from, Rectangle2D.Float to, boolean saveToXML, EventObject e) {

		List<PaintableNode> paintableNodes = getGraphDisplayInformationSet().getNodeHCopy();

		for (NodeContainer n : handler.getNodeContainers()) {
			int posX = n.getCoordinate().x;
			int posY = -n.getCoordinate().y;

			float relativeX = (posX - from.x) / from.width;
			float relativeY = (posY - from.y) / from.height;

			posX = (int) (to.x + relativeX * to.width);
			posY = (int) (to.y + relativeY * to.height);

			if (saveToXML) {
				n.attribute(XMLTagKeywords.POS_X).setValue(posX, e);
				n.attribute(XMLTagKeywords.POS_Y).setValue(-posY, e);
			}
			else {
				paintableNodes.get(n.getIndex()).posX = posX;
				paintableNodes.get(n.getIndex()).posY = posY;
				for(PaintableLink pl :getGraphDisplayInformationSet().getLinksHCopy()) {
					if (pl.linkContainer.getStartNodeIndex() == n.getIndex()) {
						pl.startX = posX;
						pl.startY = posY;
					}
					if (pl.linkContainer.getEndNodeIndex() == n.getIndex()) {
						pl.endX = posX;
						pl.endY = posY;
					}
				}
			}
		}
		repaintGraph();
	}

	private float refitFactor = 1;

	public void reinitRefitFactor() {
		refitFactor = 1;
		repaintGraph();
	}

	/**
	 * Transforms coordinates in such a way that it corresponds to a zoom around the center
	 * of the view.
	 * @param factor The zoom factor
	 */
	public void refit(float factor, EventObject e) {
		float savRefitFactor = refitFactor;

		refitFactor *= factor;

		if (refitFactor == 0) {
			refitFactor = savRefitFactor;
		}

		refit(refitFactor, false, e);
	}

	public void saveRefit() {
		refit(refitFactor, true, null);
	}

	@Override
	public void setInfoSetView_(int x, int y, int width, int height) {
		super.setInfoSetView_(x,y,width, height);
		repaintGraph();
	}

}
