package ch.epfl.javanco.event;

import java.util.EventObject;

import ch.epfl.javanco.network.LayerContainer;

public class EditedLayerEvent extends EventObject {
	public static final long serialVersionUID = 0;
	public EditedLayerEvent(LayerContainer con) {
		super(con);
	}

	public LayerContainer getLayerEdited() {
		return (LayerContainer)getSource();
	}
}