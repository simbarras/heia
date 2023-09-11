package ch.epfl.javanco.event;

import java.util.EventListener;
import java.util.EventObject;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public interface ElementListener extends EventListener {

	/**
	 * Call this to notify a modification on one element
	 */
	public void elementModified(ElementEvent event);

	/**
	 * Call this to notify modifications on all elements at the same thime
	 */
	public void allElementsModified(CasualEvent event);

	public void graphLoaded(EventObject e);

	public void nodeCreated(NodeContainer nc, ElementEvent ev);
	public void linkCreated(LinkContainer lc, ElementEvent ev);
	public void layerCreated(LayerContainer llc, ElementEvent ev);

	public void nodeSuppressed(NodeContainer nc, ElementEvent ev);
	public void linkSuppressed(LinkContainer lc, ElementEvent ev);
	public void layerSuppressed(LayerContainer llc, ElementEvent ev);

	public void beginBigChanges(CasualEvent ev);
	public void endBigChanges(CasualEvent ev);

	public static abstract class AbstractVoidListener implements ElementListener {
		public abstract void refresh(CasualEvent ev);
		public abstract void reset(CasualEvent ev);

		public void beginBigChanges(CasualEvent ev) {}
		public void endBigChanges(CasualEvent e) {
			refresh(e);
		}

		@Override
		public void allElementsModified(CasualEvent e) {
			reset(e);  //  @jve:decl-index=0:
		}

		@Override
		public void elementModified(ElementEvent e) {
			refresh(e);
		}

		@Override
		public void layerCreated(LayerContainer llc, ElementEvent e) {
			refresh(e);
		}

		@Override
		public void layerSuppressed(LayerContainer llc, ElementEvent e) {
			refresh(e);
		}

		@Override
		public void linkCreated(LinkContainer lc, ElementEvent e) {
			refresh(e);
		}

		@Override
		public void linkSuppressed(LinkContainer lc, ElementEvent e) {
			refresh(e);
		}

		@Override
		public void nodeCreated(NodeContainer nc, ElementEvent e) {
			refresh(e);
		}

		@Override
		public void nodeSuppressed(NodeContainer nc, ElementEvent e) {
			refresh(e);
		}
		@Override
		public void graphLoaded(EventObject e) {}
	}

}
