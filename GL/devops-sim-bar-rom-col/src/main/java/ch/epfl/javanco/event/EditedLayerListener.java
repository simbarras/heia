package ch.epfl.javanco.event;

import java.util.EventListener;

public interface EditedLayerListener extends EventListener {

	public void editedLayerChanged(EditedLayerEvent e);

}