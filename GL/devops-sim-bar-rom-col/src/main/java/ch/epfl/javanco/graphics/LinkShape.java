package ch.epfl.javanco.graphics;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

/**
 * Represents a link as a <code>Shape</code>.
 * 
 * @author fmoulin
 *
 */
public class LinkShape implements Shape {
	private LinkPolygon poly = null;
	private Shape shape;


	/**
	 * Constructs the <code>LinkShape</code> from a <code>Shape</code>.
	 * <BR>#author fmoulin
	 * 
	 * @param shape
	 */
	public LinkShape(Shape shape) {
		this.shape = shape;
	}

	/*
	public LinkPolygon toPolygon2() {
		if (poly == null) {
			PathIterator pi = getPathIterator(null, 1);
			LinkedList<Integer> xpoints = new LinkedList<Integer>();
			LinkedList<Integer> ypoints = new LinkedList<Integer>();

			double[] coords = new double[6];

			while (!pi.isDone()) {
				int type = pi.currentSegment(coords);
				if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
					xpoints.add((int)Math.round(coords[0]));
					ypoints.add((int)Math.round(coords[1]));
				}
				else
					assert false;
				pi.next();
			}
			int[] xp = new int[xpoints.size()];
			int[] yp = new int[xp.length];

			for (int i = 0; i < xp.length; i++) {
				xp[i] = xpoints.get(i);
				yp[i] = ypoints.get(i);
			}

			poly = new LinkPolygon(xp, yp, xp.length);
		}
		return poly;
	}*/

	/**
	 * Converts the <code>LinkShape</code> to a <code>LinkePolygon</code>.
	 * <BR>#author fmoulin
	 * 
	 * @param width Width of the link
	 * @return An approximation of the shape by a <code>Polygon</code>.
	 */
	public LinkPolygon toPolygon(float width) {
		if (poly == null) {
			PathIterator pi = getPathIterator(null, 1);
			LinkedList<Integer> xpoints = new LinkedList<Integer>();
			LinkedList<Integer> ypoints = new LinkedList<Integer>();
			LinkedList<Integer> xpointsret = new LinkedList<Integer>();
			LinkedList<Integer> ypointsret = new LinkedList<Integer>();

			double[] coords = new double[6];
			double[] last = new double[2];
			Point2D p;

			while (!pi.isDone()) {
				last[0] = coords[0];
				last[1] = coords[1];
				int type = pi.currentSegment(coords);
				if (type == PathIterator.SEG_MOVETO) {

				}
				if (type == PathIterator.SEG_LINETO) {
					p = decaler(last[0], last[1], coords[0], coords[1], width/2);
					xpoints.add((int)p.getX());
					ypoints.add((int)p.getY());

					p = decaler(last[0], last[1], coords[0], coords[1], -width/2);
					xpointsret.add((int)p.getX());
					ypointsret.add((int)p.getY());
				}
				pi.next();
			}

			p = decaler(coords[0], coords[1], last[0], last[1], -width/2);
			xpoints.add((int)p.getX());
			ypoints.add((int)p.getY());

			p = decaler(coords[0], coords[1], last[0], last[1], width/2);
			xpointsret.add((int)p.getX());
			ypointsret.add((int)p.getY());

			int[] xp = new int[xpoints.size() + xpointsret.size()];
			int[] yp = new int[xp.length];

			for (int i = 0; i < xpoints.size(); i++) {
				xp[i] = xpoints.get(i);
				yp[i] = ypoints.get(i);
			}

			for (int i = 0; i < xpointsret.size(); i++) {
				xp[xpoints.size() + i] = xpointsret.get(xpointsret.size() - 1 - i);
				yp[xpoints.size() + i] = ypointsret.get(xpointsret.size() - 1 - i);
			}

			poly = new LinkPolygon(xp, yp, xp.length);
		}
		return poly;
	}

	private Point2D decaler(double x1, double y1, double x2, double y2, float modif) {
		Point2D ret = new Point2D.Double();
		double distance = Point2D.distance(x1, y1, x2, y2);
		double alpha = (y2>y1 ? -1 : 1) * Math.acos((x2-x1)/distance);
		ret.setLocation( x1 + Math.sin(alpha) * modif, y1 + Math.cos(alpha) * modif);
		return ret;
	}


	/*
	@Override
	public String toString() {
		return toPolygon().toString();
	}

	@Override
	public int[] getCoords() {
		return toPolygon().getCoords();
	}

	@Override
	public void reverseYCoord(int height) {
		toPolygon().reverseYCoord(height);
	}
	 */

	@Override
	public boolean contains(Point2D p) {
		return shape.contains(p);
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return shape.contains(r);
	}

	@Override
	public boolean contains(double x, double y) {
		return shape.contains(x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return shape.contains(x, y, w, h);
	}

	@Override
	public Rectangle getBounds() {
		return shape.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return shape.getBounds2D();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return shape.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return shape.getPathIterator(at, flatness);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return shape.intersects(r);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return shape.intersects(x, y, w, h);
	}

}