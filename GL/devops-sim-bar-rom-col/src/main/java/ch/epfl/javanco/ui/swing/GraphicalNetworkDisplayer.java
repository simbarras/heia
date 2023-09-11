package ch.epfl.javanco.ui.swing;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;

import ch.epfl.javanco.ui.AbstractGraphicalUI;


public class GraphicalNetworkDisplayer extends JComponent implements ComponentListener {

	private static final long serialVersionUID = 1L;

	protected AbstractGraphicalUI aGui;

	float zoomFactor;

	boolean viewImposed;

	public GraphicalNetworkDisplayer() {}

	public GraphicalNetworkDisplayer(float f, boolean view) {
		this.viewImposed = view;
		zoomFactor = f;
	}
	
	public GraphicalNetworkDisplayer(AbstractGraphicalUI ui) {
		this.aGui = ui;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		aGui.paintItToComponent(g, this);
	}	

	public void updateViewSize(ComponentEvent e) {
		java.awt.Rectangle rect = (e.getComponent()).getBounds();
		aGui.setBestFit(rect.getSize());
		this.setPreferredSize(rect.getSize());
	}

	public void setUI(AbstractGraphicalUI ui) {
		this.aGui = ui;
	}

	public void componentHidden(ComponentEvent e) {	}
	public void componentMoved(ComponentEvent e){	}
	public void componentResized(ComponentEvent e){
		updateViewSize(e);
	}
	public void componentShown(ComponentEvent e){
		//	updateViewSize(e);
	}

}
