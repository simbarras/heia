package ch.epfl.javanco.ui.swing;

import java.util.EventObject;

import javax.swing.JDialog;
import javax.swing.JTextArea;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.event.ElementListener;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class SimpleInfosDisplayer extends JDialog implements ElementListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static class EventProcessor {
		public String process(ElementEvent ev) {
			return ev.toString();
		}
	}

	private JTextArea text = new JTextArea();
	private EventProcessor processor = null;

	public SimpleInfosDisplayer(EventProcessor p) {
		super();
		text.setBackground(this.getBackground());
		text.setBorder(new javax.swing.border.EmptyBorder(0,0,0,0));
		this.add(text);
		this.setSize(300,200);
		this.processor = p;
	}

	@Override
	public void elementModified(ElementEvent event) {
		text.setText(processor.process(event));
	}

	/**
	 * Call this to notify modifications on all elements at the same thime
	 */
	public void allElementsModified(CasualEvent e) {}

	public void graphLoaded(EventObject o){}

	public void nodeCreated(NodeContainer nc, ElementEvent e){}
	public void linkCreated(LinkContainer lc, ElementEvent e){}
	public void layerCreated(LayerContainer llc, ElementEvent e){}

	public void nodeSuppressed(NodeContainer nc, ElementEvent e){}
	public void linkSuppressed(LinkContainer lc, ElementEvent e){}
	public void layerSuppressed(LayerContainer llc, ElementEvent e){}

	public void beginBigChanges(CasualEvent ev){}
	public void endBigChanges(CasualEvent ev){}


}
