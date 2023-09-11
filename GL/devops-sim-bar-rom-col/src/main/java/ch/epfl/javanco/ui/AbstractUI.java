package ch.epfl.javanco.ui;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.UIEventListener;


public abstract class AbstractUI implements UIEventListener {

	protected AbstractGraphHandler             handler                 = null;

	protected AbstractUI(AbstractGraphHandler handler) {
		this.handler = handler;
		handler.getUIDelegate().registerNewUI(this);
	}

	public void init() {
	}

	public AbstractGraphHandler getAssociatedAbstractGraphHandler() {
		return handler;
	}

	/**
	 * Shall be called by the UI delegate when a newNode must be added
	 * @param coord The point (in the node space reference) where to add the node
	 */
/*	public void newNode(int x, int y, String def, String def2, String icon) {
		handler.newNode(x,y);
		handler.newNode(x, y, def, def2, icon, handler.getUIDelegate().getTypeCreationAdapter().getSelectedNodeType());
	}*/

	/**
	 * Shall be called by the UI delegate when a new Link must be added
	 * @param orig The index of the origin node
	 * @param dest The index of the destination node
	 */
/*	public void newLink(int orig, int dest) {
		handler.newLink(orig, dest, handler.getUIDelegate().getTypeCreationAdapter().getSelectedLinkType());
	}*/

	/**
	 * Shall be called by the GUI delegate when an element must be removed
	 * @param The container of the element that must be removed
	 */
/*	public void removeElement(Pointable el) {
		handler.removeElement(el.getElementContainer());
	}

	public void removeAllElements() {
		handler.clearLayers(true);
	}

	public void setLayerDisplayed(String layerName, boolean displayed) {
		handler.getLayerContainer(layerName).setDisplayed(displayed);
		refreshAndRepaintAllDisplay();
	}*/

	/**
	 * Void implementation to override
	 */
	public void prepareForSave() {
	}

	/**
	 * Void implementation to override
	 */
	public void displayWarning(String s) {
	}
	/**
	 * Void implementation to override
	 */
	public void displayWarning(String s, Throwable e) {
	}

/*	public void beginBigChanges() {}
	public void endBigChanges() {}*/

	/**
	 * Void implementation to override. If only one object has been modified
	 */
/*	public void elementModified(ElementEvent event) {
	}*/

	/**
	 * Void implementation to override. If many objects have been modified
	 */
/*	public void allElementsModified() {
	}*/
	/**
	 * Void implementation to override. If graph has been recently loaded
	 */
/*	public void graphLoaded() {}*/

	/**
	 * Void implementation to override.
	 */
/*	public void nodeCreated(NodeContainer nc) {
	}*/
	/**
	 * Void implementation to override.
	 */
/*	public void linkCreated(LinkContainer lc) {
	}*/
	/**
	 * Void implementation to override.
	 */
/*	public void layerCreated(LayerContainer llc) {
	}*/
	/**
	 * Void implementation to override.
	 */
/*	public void nodeSuppressed(NodeContainer nc) {
	}*/
	/**
	 * Void implementation to override.
	 */
/*	public void linkSuppressed(LinkContainer lc) {
	}*/
	/**
	 * Void implementation to override.
	 */
/*	public void layerSuppressed(LayerContainer llc) {
	}*/

	/**
	 * Method that could be override to have different type of <code>UIManager</code> within the same implementation of <code>UIManager</code>.
	 * Used for example to have a 2D or 3D view in the <code>SwingUIManager</code>
	 * <BR>#author fmoulin
	 * @return
	 */
/*	public int getType() {
		return 0;
	}*/

}
