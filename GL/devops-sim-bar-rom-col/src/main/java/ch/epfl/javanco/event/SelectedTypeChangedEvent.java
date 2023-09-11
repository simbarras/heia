package ch.epfl.javanco.event;

import java.util.EventObject;


public class SelectedTypeChangedEvent extends EventObject {

	public static final long serialVersionUID = 0;
	private final boolean isNode;
	private final boolean isLink;
	private Class<?> class_;
	private final boolean isLayer;

	public SelectedTypeChangedEvent(Object o, Class<?> c, boolean isNode, boolean isLink, boolean isLayer) {
		super(o);
		this.isNode = isNode;
		this.isLink = isLink;
		this.isLayer = isLayer;
		this.class_ = c;
	}

	public boolean isNodeSelected() {
		return isNode;
	}

	public boolean isLinkSelected() {
		return isLink;
	}

	public boolean isLayerSelected() {
		return isLayer;
	}

	public Class<?> getSelectedClass() {
		return class_;
	}
}
