package ch.epfl.javanco.ui;

import java.util.EventObject;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.event.ElementListener;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public abstract class AbstractDynamicGraphicalUI extends AbstractGraphicalUI implements ElementListener {

	private boolean eventEnabled = true;
	
	public abstract void repaintGraph();

	public AbstractDynamicGraphicalUI(NetworkPainter painter,
			AbstractGraphHandler agh,
			GlobalInterface superInterface) {
		super(painter, agh, superInterface);
	}	

	@Override
	public void init() {
		super.init();
		handler.addGraphicalElementListener(this);
	}
	
	public void refreshAndRepaint() {
		graphInfoSet.refreshElementsGraphicalInfos(handler);
		repaintGraph();
	}
	
	protected void refreshAndRepaintDisplayAndUI() {
		getGraphDisplayInformationSet().fullRefresh(handler, handler.getUIDelegate().getGraphicalDataHandler());	
		repaintGraph();
	}
	
	@Override
	protected void setInfoSetView_(int x, int y, int width, int height) {
		super.setInfoSetView_(x,y,width, height);
		refreshAndRepaint();
	}	
	
	private void refreshAndRepaint(EventObject ev) {
	//	if (ev.equals(lastEvent)) return;
		if (eventEnabled) {
			refreshAndRepaint();
		}		
	}
	
	private void refreshAndRepaintForced(EventObject ev) {
		refreshAndRepaint();
	}	
	
	private void refreshAndRepaintAllDisplay(EventObject ev) {
		if (eventEnabled) {
			refreshAndRepaintDisplayAndUI();
		}		
	}	

	@Override
	public void setDisplaySize(int width, int height) {
		super.setDisplaySize(width, height);
	/*	boolean t = (width != graphInfoSet.getDisplaySize().width);
		if (t) {
			graphInfoSet.refreshElementsGraphicalInfos(handler);
		}*/
	}

	@Override
	public void elementModified(ElementEvent event) {
		refreshAndRepaint(event);
	}

	@Override
	public void beginBigChanges(CasualEvent ev) {
		eventEnabled = false;
	}
	@Override
	public void endBigChanges(CasualEvent ev) {
		refreshAndRepaint();
		eventEnabled = true;
	}
	

	@Override
	public void graphLoaded(EventObject e) {
		refreshAndRepaintAllDisplay(e);
	}

	@Override
	public void allElementsModified(CasualEvent e) {
		refreshAndRepaintForced(e);
	}

	@Override
	public void nodeCreated(NodeContainer nc, ElementEvent e) {
		refreshAndRepaint(e);
	}
	@Override
	public void linkCreated(LinkContainer lc, ElementEvent e) {
		refreshAndRepaint(e);
	}
	@Override
	public void nodeSuppressed(NodeContainer nc, ElementEvent e) {
		refreshAndRepaint(e);
	}
	@Override
	public void linkSuppressed(LinkContainer lc, ElementEvent e) {
		refreshAndRepaint(e);
	}
	
	/*
	 *	Layers required to reload the interface (and not only the
	 *  graph painter data, thus all display is called*/
	@Override
	public void layerCreated(LayerContainer llc, ElementEvent e) {
		refreshAndRepaintAllDisplay(e);
	}	
	
	@Override
	public void layerSuppressed(LayerContainer llc, ElementEvent e) {
		refreshAndRepaintAllDisplay(e);
	}



}
