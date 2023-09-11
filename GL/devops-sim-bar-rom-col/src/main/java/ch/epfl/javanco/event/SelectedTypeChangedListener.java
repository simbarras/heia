package ch.epfl.javanco.event;

import java.util.EventListener;

public interface SelectedTypeChangedListener extends EventListener {

	public void selectedTypeChanged(SelectedTypeChangedEvent e);

}