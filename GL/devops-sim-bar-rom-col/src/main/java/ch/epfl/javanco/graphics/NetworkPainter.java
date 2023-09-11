package ch.epfl.javanco.graphics;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import ch.epfl.javanco.network.AbstractElementContainer;

/**
 * Regroups all functions that a component dedicated to the painting of the
 * network representations should provide. Three methods are really about
 * painting. A painter that is dedicated for picture represenation may eventually
 * not implement the {@link #paintItToComponent(Graphics,Component,Rectangle,int,double)}
 * method.<p>
 *
 * Besides the painting methods, the management of the "user space" has been
 * delegated to the painter. It should thus updates permanently the bounds of the
 * editable space (which can be augmented when creating or moving a node, for
 * instance). This bounds are later usefull for scrolling purposes or indeed to
 * size the image that will receive the network representation.</p>
 *
 * Two methods provides "graphical access" to the elements. The painter may
 * place the node anywhere in its displayable zone, and thus differences may
 * appears with the "standard" coordinates (x,y). Thus, calling one of the
 * {@link #getDisplayedElementsPositions()} methods, the real positions of the
 * objects in the final 2Drepresentation of the network is accessible. For that,
 * two inner classes an one inner interfaces are included, providing a structure
 * for the coordinates of the displayed objects.
 */
public interface NetworkPainter  {


	public Pointable getElementAt(Point p, GraphDisplayInformationSet set);
	public Rectangle getGraphicalSpace(GraphDisplayInformationSet set);

	public BufferedImage paintItToImage(GraphDisplayInformationSet set);
	/**
	 * Paints the graph to the grahics g using the set s. The view (which part of the graph is painted) stored
	 * into the object set is used
	 * @param g
	 * @param set
	 */
	public void paintItToGraphics(Graphics g, GraphDisplayInformationSet set);	
	
	public Hashtable<AbstractElementContainer, ElementCoord> getDisplayedElementsPositions(GraphDisplayInformationSet set);

}
