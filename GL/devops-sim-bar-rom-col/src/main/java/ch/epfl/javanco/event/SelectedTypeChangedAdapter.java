package ch.epfl.javanco.event;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.Element;
import ch.epfl.javanco.network.Layer;
import ch.epfl.javanco.network.Link;
import ch.epfl.javanco.network.Node;


public class SelectedTypeChangedAdapter implements SelectedTypeChangedListener {

	private Class<? extends Node> selectedTypeForNodeCreation = ch.epfl.javanco.network.DefaultNodeImpl.class;
	private Class<? extends Link> selectedTypeForLinkCreation = ch.epfl.javanco.network.DefaultLinkImpl.class;
	private Class<? extends Layer> selectedTypeForLayerCreation = ch.epfl.javanco.network.DefaultGraphImpl.class;

	private Class<? extends Element> previousClass = Element.class;
	private boolean previousIsNode = false;
	private boolean previousIsLink= false;

	private AbstractGraphHandler agh = null;

	private static Logger logger = new ch.epfl.general_libraries.logging.Logger(SelectedTypeChangedAdapter.class);

	public SelectedTypeChangedAdapter(AbstractGraphHandler agh) {
		this.agh = agh;
	}

	/**
	 * Called by the UI if then type selected for creation of new objects changes.
	 * By implementing the <code>SelectedTypeChangedListener</code> interface,
	 * this class can be added to a set of listeners that are informed when the
	 * type of the selected editor tool is changed.
	 * @param e A <code>SelectedTypeChangeEvent<code> object that contains the new
	 * type.
	 */
	@SuppressWarnings("unchecked")
	public void selectedTypeChanged(SelectedTypeChangedEvent e) {
		Class<?> selection = e.getSelectedClass();
		if (e.isLinkSelected()) {
			if ((selection != null) && (Link.class.isAssignableFrom(selection))) {
				selectedTypeForLinkCreation = (Class<? extends Link>) selection;
			} else {
				logger.error("event says link tool is selected, but received class is not AbstractLink compatible");
			}
		} else if (e.isNodeSelected()) {
			if ((selection != null) && (Node.class.isAssignableFrom(selection))) {
				selectedTypeForNodeCreation = (Class<? extends Node>) selection;
			} else {
				logger.error("event says node tool is selected, but received class is not AbstractNode compatible");
			}
		} else if (e.isLayerSelected()) {
			if ((selection != null) && (Layer.class.isAssignableFrom(selection))) {
				selectedTypeForLayerCreation = (Class<? extends Layer>) selection;
			} else {
				logger.error("event says node tool is selected, but received class is not AbstractLayer compatible");
			}
		}
	}

	public Class<? extends Node> getSelectedNodeType() {
		return selectedTypeForNodeCreation;
	}

	public Class<? extends Link> getSelectedLinkType() {
		return selectedTypeForLinkCreation;
	}

	public Class<? extends Layer> getSelectedLayerType() {
		return selectedTypeForLayerCreation;
	}
	public void changeSelectedType(Class<? extends Element> c, boolean isNode, boolean isLink, boolean isLayer) {
		if ((isNode == previousIsNode) && (isLink == previousIsLink)) {
			if ((c != null) && (previousClass != null)) {
				if (c.equals(previousClass)) {
					return;
				}
			} else {
				return;
			}
		}
		agh.fireSelectedTypeChangedEvent(c, isNode, isLink, isLayer);
		previousClass = c;
		previousIsNode = isNode;
		previousIsLink = isLink;
	}


}
