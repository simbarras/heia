package ch.epfl.javanco.event;

import java.util.EventObject;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class ElementAdapter implements ElementListener {

	public void elementModified(ElementEvent event){}


	public void allElementsModified(CasualEvent e){}

	public void graphLoaded(EventObject ev){}

	public void nodeCreated(NodeContainer nc, ElementEvent e){}
	public void linkCreated(LinkContainer lc, ElementEvent e){}
	public void layerCreated(LayerContainer llc, ElementEvent e){}

	public void nodeSuppressed(NodeContainer nc, ElementEvent e){}
	public void linkSuppressed(LinkContainer lc, ElementEvent e){}
	public void layerSuppressed(LayerContainer llc, ElementEvent e){}

	public void beginBigChanges(CasualEvent ev){}
	public void endBigChanges(CasualEvent ev){}
}
