package ch.epfl.javanco.event;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class ElementEvent extends CasualEvent {

	private static final long serialVersionUID = 0;

	public enum EVENT_TARGET {
		NODE,
		LINK,
		LAYER,
		ALL
		/*,NONE*/ // for possible future use
	}

	public enum EVENT_TYPE {
		CREATION,
		SUPPRESSION,
		MODIFICATION,
	}

	private final EVENT_TARGET target;
	private final EVENT_TYPE type;
	private final String property;
	
	public static ElementEvent ALL_MODIFIED = new ElementEvent(EVENT_TARGET.ALL);

	public static ElementEvent createCreationEvent(AbstractElementContainer con) {
		return new ElementEvent(con, EVENT_TYPE.CREATION, null);
	}

	public static ElementEvent creationSuppressionEvent(AbstractElementContainer con) {
		return new ElementEvent(con, EVENT_TYPE.SUPPRESSION, null);
	}

	public static ElementEvent creationModificationEvent(AbstractElementContainer con, String prop) {
		return new ElementEvent(con, EVENT_TYPE.MODIFICATION, prop);
	}
	
	public static ElementEvent getAllElementEvent() {
		return new ElementEvent(ElementEvent.EVENT_TARGET.ALL);
	}

	private ElementEvent(EVENT_TARGET ta) {
		super("");
		if (!ta.equals(EVENT_TARGET.ALL)) {
			throw new IllegalArgumentException("Constructor with AbstractElementContainer should be used");
		}
		target = ta;
		type = EVENT_TYPE.MODIFICATION;
		property = null;
	}

	private ElementEvent(AbstractElementContainer con, EVENT_TYPE ty, String property) {
		super(con);
		if (con instanceof LinkContainer) {
			target = EVENT_TARGET.LINK;
		} else if (con instanceof NodeContainer) {
			target = EVENT_TARGET.NODE;
		} else if (con instanceof LayerContainer) {
			target = EVENT_TARGET.LAYER;
		} else {
			throw new IllegalStateException("Exotic type of container !!");
		}
		type = ty;
		this.property = property;
	}

	public AbstractElementContainer getModifiedElement() {
		return (AbstractElementContainer)getSource();
	}

	public boolean concernsNode() {
		return (target == EVENT_TARGET.NODE) || (target == EVENT_TARGET.ALL);
	}
	public boolean concernsLink() {
		return (target == EVENT_TARGET.LINK) || (target == EVENT_TARGET.ALL);
	}
	public boolean concernsLayer() {
		return (target == EVENT_TARGET.LAYER) || (target == EVENT_TARGET.ALL);
	}
	public boolean concernsAll() {
		return (target == EVENT_TARGET.ALL);
	}
	public boolean isCreation() {
		return (type == EVENT_TYPE.CREATION);
	}
	public boolean isSuppression() {
		return (type == EVENT_TYPE.SUPPRESSION);
	}
	public boolean isModification() {
		return (type == EVENT_TYPE.MODIFICATION);
	}
	public boolean concernsProperty(String property) {
		if (this.property == null) return false;
		if (type == EVENT_TYPE.MODIFICATION && this.property.equals(property)) {
			return true;
		}
		return false;
	}

	public NodeContainer getNodeContainer() {
		if (concernsNode()) {
			return (NodeContainer)getSource();
		} else {
			throw new IllegalStateException("Event does not concerns a node");
		}
	}
	public LinkContainer getLinkContainer() {
		if (concernsLink()) {
			return (LinkContainer)getSource();
		} else {
			throw new IllegalStateException("Event does not concerns a link");
		}
	}
	public LayerContainer getLayerContainer() {
		if (concernsLayer()) {
			return (LayerContainer)getSource();
		} else {
			throw new IllegalStateException("Event does not concerns a layer");
		}
	}

	public AbstractElementContainer getContainer() {
		return (AbstractElementContainer)getSource();
	}

	@Override
	public String toString() {
		return type.toString() + "_" + target.toString() + 	"_" + property;
	}


}
