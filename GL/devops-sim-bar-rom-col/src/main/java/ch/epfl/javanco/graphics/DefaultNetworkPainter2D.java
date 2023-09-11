package ch.epfl.javanco.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import ch.epfl.general_libraries.graphics.ToolBox;
import ch.epfl.javanco.network.AbstractElementContainer;

public class DefaultNetworkPainter2D implements NetworkPainter {
	public static final boolean HIGHLIGHT = true;

	//	public Rectangle graphicalSpace = null;

	public DefaultNetworkPainter2D() {
	}

	public Rectangle getGraphicalSpace(GraphDisplayInformationSet set) {
		Rectangle toReturn = null;

		for (PaintableNode node : set.getNodeHCopy()) {
			if (node != null) {
				Rectangle nodeNeighborhood = new Rectangle(node.getX() - node.size, node.getY() - node.size,node.size * 2,node.size * 2);
				if (toReturn == null) {
					toReturn = nodeNeighborhood;
				}
				if (toReturn.contains(nodeNeighborhood) == false) {
					toReturn = toReturn.union(nodeNeighborhood);
				}
			}
		}
		return toReturn;
	}

	public Pointable getElementAt(Point p, GraphDisplayInformationSet set) {
		return getElementAt(/*getAndCopyView(view, set)*/p.x, p.y, set);
	}

	protected Pointable getElementAt(int x, int y, GraphDisplayInformationSet set) {
		List<PaintableNode> nodes = set.getNodeHCopy();
		
		for (int i = nodes.size()-1; i!=-1;i--) {
			PaintableNode node = nodes.get(i);
			if (node != null) {
				Point nodeP = viewToGraphics(node.posX, node.posY, set);
				if (java.awt.geom.Point2D.distance(x, y, nodeP.x, nodeP.y) < modifySize(node.size,set)/2) {
					return node;
				}
			}
		}

		for (PaintableLink link : set.getLinksHCopy()) {
			Shape linkShape = computeLinkShape(link, set);

			if (computeDistanceToShape(linkShape, x, y) <= Math.max(link.width/2, 4) ) {
				return link;
			}

		}
		return null;
	}

	/**
	 * Note : need dim to know the space reference of the displayer, in order
	 * to perform the transformation from one space to the other one
	 */
	protected Point viewToGraphics(int sx, int sy, GraphDisplayInformationSet set) {
		Rectangle viewRectangle = set.getViewRectangle();
		if (viewRectangle != null) {
			int tx = sx - viewRectangle.x;
			int ty = sy - viewRectangle.y;

			Dimension size = set.getDisplaySize();

			float x = (float)(tx * size.width ) / (float)viewRectangle.width;
			float y = size.height - (float)(ty * size.height) / (float)viewRectangle.height;

			return new Point(Math.round(x), Math.round(y));
		}
		return new Point(0,0);
	}

	public Hashtable<AbstractElementContainer, ElementCoord> getDisplayedElementsPositions(GraphDisplayInformationSet set) {
		Hashtable<AbstractElementContainer, ElementCoord> ht = new Hashtable<AbstractElementContainer, ElementCoord>();
		Rectangle view = set.getViewRectangle();
		for (PaintableLink link : set.getLinksHCopy()) {
			if (link.intersects(view)) {
				ElementCoord poly =  computeLinkShape(link, set).toPolygon(link.width);//this.computeLinkCorners(/*getAndCopyView(view, set)*/view, link, 0,set);
				poly.reverseYCoord(set.getDisplaySize().height);
				ht.put(link.getElementContainer(), poly);
			}
		}

		for (PaintableNode node : set.getNodeHCopy()) {
			if (node != null) {
				Rectangle nodeNeighborhood = new Rectangle(node.getX() - node.size, node.getY() - node.size,node.size * 2,node.size * 2);
				if (nodeNeighborhood.intersects(view)) {
					ElementCoord nodeCoord = this.computeNodeCorners(node,set);
					nodeCoord.reverseYCoord(set.getDisplaySize().height);
					ht.put(node.getElementContainer(), nodeCoord);
				}
			}
		}
		return ht;
	}
	
	public BufferedImage paintItToImage(GraphDisplayInformationSet set) {
		BufferedImage img;
		
		Dimension d = set.getDisplaySize();

		img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
	//	Rectangle viewSaved = set.getViewRectangle();
	//	set.setView(view);
		paintItToGraphics(img.createGraphics(),  set);
	//	set.setView(viewSaved);
		return img;
	}	

	public void paintItToGraphics(Graphics g, GraphDisplayInformationSet set) {
		//		boolean updateScrollBars = updateNodeRectangle(set);
		synchronized (set) {
			List<PaintableNode> nodes = set.getNodeHCopy();
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
			g.setColor(set.backgroundColor);
			Dimension size = set.getDisplaySize();
			g.fillRect(0,0,size.width, size.height);
			paintBackGroundImage(g, set);
			g.setColor(Color.BLACK);
	
			if (set.newLink != null) {
				drawLink(nodes, set.newLink, g, set);
			}
		//	Pointable pointed = set.pointedObject;
		/*	if (pointed != null) {
				if (pointed instanceof PaintableLink) {
					PaintableLink link = (PaintableLink)pointed;
					
				}
			}
	
			System.out.println(set.getLinksHCopy());
	*/
	
			for (PaintableLink link : set.getLinksHCopy()) {
				if (link.pointed) {
					drawOutlinedLink(nodes, link, g,set);
				} else {
					drawLink(nodes, link, g, set);
				}
			}
			PaintableNode moved = set.movedNode;
			int movedId = -1;
			if (moved != null) {
				movedId = moved.getId();
			}
	
			for (PaintableNode node : nodes) {
				if (node != null) {
					if (node.getId() != movedId) {
						drawNodeLoc(g, set, node);
					}
				}
			}
			drawNodeLoc(g, set, moved);
		}
	}

	private void drawNodeLoc(Graphics g, GraphDisplayInformationSet set, PaintableNode node) {
		Rectangle view = set.getViewRectangle();
		if (node != null) {
				Rectangle nodeNeighborhood = new Rectangle(node.getX() - node.size, node.getY() - node.size,node.size * 2,node.size * 2);
				if (view.intersects(nodeNeighborhood)) {
					if (node.pointed) {
						drawOutlinedNode(node, g, set);
					} else {
						drawNode(node, g, set);
					}
				}
			}
	}

	protected void paintBackGroundImage(Graphics g, GraphDisplayInformationSet set) {
		if (set.backImage != null && set.displayBackgroundImage) {
			Graphics2D gr2d = (Graphics2D)g;

			Point graphLeftUp      = viewToGraphics(
					set.nodeSpaceImageOriginX,
					set.nodeSpaceImageOriginY + set.nodeSpaceImageHeight,
					set);
			Point graphRightDown   = viewToGraphics(set.nodeSpaceImageOriginX + set.nodeSpaceImageWidth,
					set.nodeSpaceImageOriginY, set
			);
		//	if (graphRightDown.x - graphLeftUp.x > 0 &&
		//			graphRightDown.y - graphLeftUp.y > 0) {
				gr2d.drawImage(set.backImage, graphLeftUp.x,graphLeftUp.y,
						Math.abs(graphRightDown.x - graphLeftUp.x),
						Math.abs(graphRightDown.y - graphLeftUp.y),
						null);	//Could be useful to specify an ImageObserver
		//	}
		}
	}

	private final LocColor BLACK_COLOR = new LocColor();
	private class LocColor extends Color {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private LocColor() {
			super(0,0,0);
		}
		private int alpha = 0;
		@Override
		public int getBlue() { return 0; }
		@Override
		public int getRed() { return 0; }
		@Override
		public int getGreen() { return 0; }
		public void setAlpha(int al) {
			alpha = al;
		}
		@Override
		public int getAlpha() {
			return alpha;
		}
	};

	/**
	 * Draws a node on the graphics g.
	 * 
	 * @param view Rectangle of the view.
	 * @param node Node to paint.
	 * @param g	Graphics where to paint the node.
	 * @param set Information about the way to draw the node.
	 * @param obs ImageObserver used to paint images.
	 */
	protected void drawNode(PaintableNode node, Graphics g, GraphDisplayInformationSet set) {
		NodeCoord nodePoints = computeNodeCorners(node,set);
		int x = nodePoints.x;
		int y = nodePoints.y;
		int width = nodePoints.width;
		int height = nodePoints.height;

		g.setColor(set.backgroundColor);
		// erase what is below
		g.fillOval(x, y, width, height);

		BLACK_COLOR.setAlpha(node.alpha);
		g.setColor(BLACK_COLOR);
		// display border
		g.fillOval(x, y, width, height);
		g.setColor(getColor(node.color, node.alpha));
		// display center
		g.fillOval(x+1, y+1, width-2, height-2);
		BLACK_COLOR.setAlpha(node.alpha);
		g.setColor(BLACK_COLOR);
	
	/*	float fontHeight = modifySize(node.labelFontSize, set);
		java.awt.Font f = new java.awt.Font("Arial", java.awt.Font.BOLD, (int)fontHeight);
		g.setFont(f);
		FontMetrics fm =  g.getFontMetrics(f);*/


		drawNodeId(node, set, g, nodePoints, 0f);
		g.setColor(node.labelColor);
		drawNodeLabel(node, set, g, nodePoints, 0f);

	}

	protected void drawNodeId(PaintableNode node, GraphDisplayInformationSet set, Graphics g, NodeCoord nodePoints, float decalage) {
		if (node.seeId) {
			int x = nodePoints.x;
			int y = nodePoints.y;
			int width = nodePoints.width;
			int height = nodePoints.height;
			float fontHeight = modifySize(node.labelFontSize, set);
			java.awt.Font f = new java.awt.Font("Arial", java.awt.Font.BOLD, (int)fontHeight);
			g.setFont(f);
			FontMetrics fm =  g.getFontMetrics(f);
			g.drawString("" + node.id, x + width/2-fm.stringWidth("" + node.id)/2, y +  (int)(height/2. + fontHeight/2. - decalage));		
		}
	}
	
	protected void drawNodeLabel(PaintableNode node, GraphDisplayInformationSet set, Graphics g, NodeCoord nodePoints, float decalage) {
		int x = nodePoints.x;
		int y = nodePoints.y;
		int width = nodePoints.width;
		int height = nodePoints.height;
		int fontHeight = (int)modifySize(node.labelFontSize, set);
		java.awt.Font f = new java.awt.Font("Arial", java.awt.Font.BOLD, fontHeight);
		g.setFont(f);
		FontMetrics fm =  g.getFontMetrics(f);
		int yLabPos;
		if (node.seeId) {
			yLabPos = y + height + fontHeight;
		} else {
			yLabPos = y +  (int)(height/2. + fontHeight/2. - decalage);	
		}
		g.drawString(node.label, x + width/2-fm.stringWidth(node.label)/2, yLabPos);
		
	}
		
	


	/**
	 * Draws an outlined node on the graphics g.
	 * 
	 * @param view Rectangle of the view.
	 * @param node Node to paint.
	 * @param g	Graphics where to paint the node.
	 * @param set Information about the way to draw the node.
	 * @param obs ImageObserver used to paint images.
	 */
	protected void drawOutlinedNode(PaintableNode node, Graphics g, GraphDisplayInformationSet set) {
		if (HIGHLIGHT) {
			NodeCoord nodePoints = computeNodeCorners(node,set);
			int x = nodePoints.x;
			int y = nodePoints.y;
			int width = nodePoints.width;
			int height = nodePoints.height;

			g.setColor(Color.BLACK);
			g.fillOval(x, y, width, height);
			g.setColor(ToolBox.getInverseColor(node.color));
			g.fillOval(x+1, y+1, width-2, height-2);
			g.setColor(new Color(0,0,0,node.alpha));
			
			drawNodeLabel(node, set, g, nodePoints, 0f);
			
			drawNodeId(node, set, g, nodePoints, 0f);
		}
		else {
			drawNode(node, g, set);
		}
	}

	/**
	 * Returns a new instance of image, scaled to fit in width*factor by height*factor.
	 * <BR>#author fmoulin
	 * 
	 * @param img image to scale.
	 * @param width width of the scaled image.
	 * @param height height of the scaled image.
	 * @param factor modifies the scaled width and height by factor.
	 * @param obs ImageObserver used to draw the image.
	 * @return a scaled image of img
	 */
	protected Image scaleImage(Image img, int width, int height, double factor, ImageObserver obs) {
		BufferedImage tmp = new BufferedImage((int)(width*factor), (int)(height*factor), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)tmp.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(img, 0, 0, (int)(width*factor), (int)(height*factor), obs);
		g2.dispose();
		return tmp;
	}

	protected NodeCoord computeNodeCorners(PaintableNode node, GraphDisplayInformationSet set) {
		Point center = viewToGraphics(node.posX, node.posY, set);
		float size = modifySize(node.size, set);
		NodeCoord coord = new NodeCoord();
		coord.x = Math.round(center.x - size/2);
		coord.y = Math.round(center.y - size/2);
		coord.width = Math.round(size);
		coord.height = Math.round(size);

		return coord;
	}

	/**
	 * Computes the distance from a point to a shape.
	 * <BR>#author fmoulin
	 * 
	 * @param s the shape.
	 * @param x
	 * @param y
	 * @return the distance from the point (x, y) to the shape s.
	 */
	protected double computeDistanceToShape(Shape s, int x, int y) {
		PathIterator pi = s.getPathIterator(null, 1);
		double[] coords = new double[6];
		double[] last = new double[2];
		double distance = -1;

		while (!pi.isDone()) {
			int type = pi.currentSegment(coords);
			if (type == PathIterator.SEG_MOVETO) {
				last[0] = coords[0];
				last[1] = coords[1];
			}
			if (type == PathIterator.SEG_LINETO) {
				if (distance == -1) {
					distance = Line2D.ptSegDist(coords[0], coords[1], last[0], last[1], x, y);
				} else {
					distance = Math.min(Line2D.ptSegDist(coords[0], coords[1], last[0], last[1], x, y), distance);
				}

				last[0] = coords[0];
				last[1] = coords[1];
			}
			pi.next();
		}

		return distance;
	}


	protected float modifySize(float size, GraphDisplayInformationSet set) {
		//	return (int)Math.rint((double)size * (Math.pow((double)2, (double)set.sizeModificator / (double)5)));
		return size * set.sizeModificator;
	}

	protected void drawLink(List<PaintableNode> nodes, PaintableLink link, Graphics screen, GraphDisplayInformationSet set) {
		drawLink(nodes, link, screen, set, false, 1);
	}

	/**
	 * Computes a shape from a link.
	 * <BR>#author fmoulin
	 * 
	 * @param view Rectangle of the view.
	 * @param link link that make the shape.
	 * @param set information about the graphic display.
	 * @return A <code>LinkShape</code> of the link
	 */
	protected LinkShape computeLinkShape(
			PaintableLink link,
			GraphDisplayInformationSet set) {
		Point start = viewToGraphics(link.startX,link.startY, set);
		Point end = viewToGraphics(link.endX,link.endY, set);

		//	double distance = Point2D.distance(start.x, start.y, end.x, end.y);


		if (link.sameNode) {
			double endx = end.x + Math.cos(Math.toRadians(link.curveEndAngle));
			double endy = end.y + Math.sin(Math.toRadians(link.curveEndAngle));
			double distance = Point2D.distance(start.x, start.y, endx, endy);

			double alpha = (end.y>start.y ? -1 : 1) * Math.acos((endx-start.x)/distance);
			double curveStart = link.curveStart+100;
			double curveEnd = link.curveEnd+100;

			Shape s;

			double startAngle = Math.toRadians(+link.curveStartAngle-45);
			double endAngle   = Math.toRadians(-link.curveStartAngle+45);
			// Point 1
			float p1x = start.x;
			float p1y = start.y;
			// Control point 1
			float cp1x = (float)(start.x + Math.sin(alpha + startAngle) * curveStart);
			float cp1y = (float)(start.y + Math.cos(alpha + startAngle) * curveStart);
			// Control point 2
			float cp2x = (float)(endx + Math.sin(alpha + endAngle) * curveEnd);
			float cp2y = (float)(endy + Math.cos(alpha + endAngle) * curveEnd);
			// Point 2
			float p2x = (float) endx;
			float p2y = (float) endy;
			s = new CubicCurve2D.Float(p1x, p1y, cp1x, cp1y, cp2x, cp2y, p2x, p2y);


			return new LinkShape(s);

		}
		/*	else if (start.x - end.x != 0 || start.y - end.y != 0)
			return new LinkShape(new Line2D.Float(start.x, start.y, end.x, end.y));*/

		double alpha = -1;
		double distance = -1;
		double curveStart = link.curveStart;
		double curveEnd = link.curveEnd;

		if (link.offset != 0 || curveStart != 0 || curveEnd != 0) {
			distance = Point2D.distance(start.x, start.y, end.x, end.y);
			alpha = (end.y>start.y ? -1 : 1) * Math.acos((end.x-start.x)/distance);
		}

		if (link.offset != 0) {
			double cosa = Math.cos(alpha);
			double sina = Math.sin(alpha);

			double f = link.offset * modifySize(5, set);
			start.y += cosa * f;
			start.x += sina * f;

			end.y   += cosa * f;
			end.x   += sina * f;
		}

		Shape s;
		if (curveStart == 0 && curveEnd == 0) {
			s = new Line2D.Float(start.x, start.y, end.x, end.y);
		} else {
			curveStart = distance * (curveStart / 100);
			curveEnd = distance * (curveEnd / 100);
			double startAngle = Math.toRadians(link.curveStartAngle);
			double endAngle   = Math.toRadians(link.curveEndAngle);
			// Point 1
			float p1x = start.x;
			float p1y = start.y;
			// Control point 1
			float cp1x = (float)(start.x + Math.sin(alpha + startAngle) * curveStart);
			float cp1y = (float)(start.y + Math.cos(alpha + startAngle) * curveStart);
			// Control point 2
			float cp2x = (float)(end.x + Math.sin(alpha + endAngle) * curveEnd);
			float cp2y = (float)(end.y + Math.cos(alpha + endAngle) * curveEnd);
			// Point 2
			float p2x = end.x;
			float p2y = end.y;
			s = new CubicCurve2D.Float(p1x, p1y, cp1x, cp1y, cp2x, cp2y, p2x, p2y);
		}

		return new LinkShape(s);
	}

	/**
	 * Solves an 2de order equation (x^2*a + x*b + c = 0).
	 * <BR>#author fmoulin
	 * 
	 * @param a coefficient of x^2 .
	 * @param b coefficient of x.
	 * @param c constant term.
	 * @return If there is no solution returns null.<br> If there is an solution returns an array with one value.<br> If there are two solutions returns an array with two values.
	 */
	protected double[] solveEquation(double a, double b, double c) {
		double[] ret = null;
		double delta = Math.pow(b, 2) - 4*a*c;

		if (delta > 0) {
			ret = new double[] {(-b - Math.sqrt(delta)) / (2*a), (-b + Math.sqrt(delta)) / (2*a)};
		} else if (delta == 0) {
			ret = new double[] {-b/(2*a)};
		}

		return ret;
	}

	/**
	 * Computes the intersection of a shape and a circle.
	 * <BR>#author fmoulin
	 * 
	 * @param s the shape.
	 * @param center center of the circle.
	 * @param radius radius of the circle.
	 * @param cutShape shape filled by the path of <code>s</code> from start of <code>s</code> to the intersection
	 * @return an array: first point is the intersection, second point can be used to compute the tangent on the intersection.<br> If the intersection can't be computed returns null.
	 */
	protected Point2D[] computeIntersectionWithCircle(Shape s, Point2D center, double radius, Path2D cutShape) {
		PathIterator pi = s.getPathIterator(null, 0.5);
		double[] coords = new double[6];
		double[] last = new double[2];
		double lastDist = 0;
		Point2D[] ret = null;
		if (cutShape == null) {
			cutShape = new Path2D.Double();
		}

		cutShape.reset();

		while (!pi.isDone()) {
			int type = pi.currentSegment(coords);
			if (type == PathIterator.SEG_MOVETO) {
				last[0] = coords[0];
				last[1] = coords[1];
				cutShape.moveTo(coords[0], coords[1]);
				lastDist = Point2D.distance(coords[0], coords[1], center.getX(), center.getY());
			}
			if (type == PathIterator.SEG_LINETO) {
				double dist = Point2D.distance(coords[0], coords[1], center.getX(), center.getY());

				boolean into = dist <= radius && lastDist > radius; //Going into the circle
				if (into /*|| out*/) {
					Point2D start = new Point2D.Double(last  [0], last  [1]);
					Point2D end   = new Point2D.Double(coords[0], coords[1]);


					double dx = end.getX() - start.getX();
					double dy = end.getY() - start.getY();
					double dxc = start.getX() - center.getX();
					double dyc = start.getY() - center.getY();

					double a = Math.pow(dx, 2) + Math.pow(dy, 2);
					double b = 2 * (dx*dxc + dy*dyc);
					double c = Math.pow(dxc, 2) + Math.pow(dyc, 2) - Math.pow(radius+3, 2);

					double[] t = solveEquation(a, b, c);
					Point2D p = null;

					if (t == null) {
						return null;
					} else if (t[0] >= 0 && t[0] <= 1) {
						p = new Point2D.Double(start.getX() + t[0]*dx, start.getY() + t[0]*dy);
					} else if (t.length == 2 && t[1] >= 0 && t[1] <= 1) {
						p = new Point2D.Double(start.getX() + t[1]*dx, start.getY() + t[1]*dy);
					} else {
						return null;
					}

					ret = new Point2D[] {p, (into ? start : end)};
					cutShape.lineTo(ret[0].getX(), ret[0].getY());
					break;
				}

				last[0] = coords[0];
				last[1] = coords[1];
				cutShape.lineTo(coords[0], coords[1]);
				lastDist = dist;
			}
			pi.next();
		}

		return ret;
	}

	/**
	 * Computes an arrow from a shape. The arrow tip is at intersection of a circle (define by center and radius) and the shape.
	 * <BR>#author fmoulin
	 * 
	 * @param s the shape.
	 * @param length length of the arrow.
	 * @param angle angle of the arrow.
	 * @param center center of the circle.
	 * @param radius radius of the circle.
	 * @return an array of 2 shape: the first shape is the arrow, the second is <code>s</code> cut to fit the arrow.
	 */
	protected Shape[] computeArrow(Shape s, double length, double angle, Point2D center, double radius) {
		Path2D cutShape = new Path2D.Double();
		Point2D[] p = computeIntersectionWithCircle(s, center, radius, cutShape);

		if (p == null) {
			return null;
		}

		Point2D start = p[1];
		double endx = p[0].getX();
		double endy = p[0].getY();

		double distance = start.distance(endx, endy);
		double alpha = (endy>start.getY() ? -1 : 1) * Math.acos((endx-start.getX())/distance);
		double beta = Math.toRadians(angle);

		endx += -Math.cos(alpha);
		endy +=  Math.sin(alpha);

		int[] px = new int[3];
		int[] py = new int[3];

		px[0] = (int)Math.round(endx + Math.sin(alpha-beta) * length);
		py[0] = (int)Math.round(endy + Math.cos(alpha-beta) * length);

		px[1] = (int)Math.round(endx);
		py[1] = (int)Math.round(endy);

		px[2] = (int)(endx + Math.sin(alpha+beta) * -length);
		py[2] = (int)(endy + Math.cos(alpha+beta) * -length);

		Path2D path = new Path2D.Double();
		path.moveTo(px[0], py[0]);
		path.lineTo(px[1], py[1]);
		path.lineTo(px[2], py[2]);
		path.closePath();

		Shape[] ret = {path, cutShape};
		return ret;
	}

	/**
	 * Gets the signed angle between two vectors
	 * @param v1x X component of first vector
	 * @param v1y Y component of first vector
	 * @param v2x X component of second vector
	 * @param v2y Y component of second vector
	 * @author pvogt
	 */
	private float getAngleFromVectors(float v1x, float v1y, float v2x, float v2y) {
		float dotproduct = v1x * v2x + v1y * v2y;
		float v1norm = (float)Math.sqrt(v1x * v1x + v1y * v1y);
		float v2norm = (float)Math.sqrt(v2x * v2x + v2y * v2y);
		float angle = (float)Math.acos( dotproduct / ( v1norm * v2norm ) );

		// We also take the dot product with a 90° rotated version of one of
		// the vectors, so that we know if the angle is negative or positive

		float dotproduct2 = v1x * v2y + v1y * (- v2x);

		if(dotproduct2 > 0) {
			angle *= -1;
		}
		return angle;
	}

	/**
	 * Computes the norm of a vector
	 * @param x The X component of the vector
	 * @param y The Y component of the vector
	 * @author pvogt
	 */
	private float getNorm(float x, float y) {
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Computes the <code>Shape</code>'s associated with the routing of a given link
	 * @param view The view
	 * @param nodes List of the paintable nodes
	 * @param link The link whose routing we are drawing
	 * @param set The <code>GraphDisplayInformationSet</code> containing all informations.
	 * @author pvogt
	 */
	public Shape[] computeRoutingShape(List<PaintableNode> nodes, PaintableLink link, GraphDisplayInformationSet set) {
		if(link.routing == null) {
			return null;
		}
		Shape[][] intermediate = new Shape[link.routing.length][];
		int totalLength = 0;
		for(int routeIndex = 0; routeIndex < link.routing.length; routeIndex++) {
			intermediate[routeIndex] = computeRoutingShape(nodes, link, routeIndex, set);
			totalLength += intermediate[routeIndex].length;
		}
		Shape[] result = new Shape[totalLength];
		int offset = 0;
		for(int routeIndex = 0; routeIndex < link.routing.length; routeIndex++) {
			int len = intermediate[routeIndex].length;
			System.arraycopy(intermediate[routeIndex], 0, result, offset, len);
			offset += len;
		}
		return result;
	}

	/**
	 * Tests wether we have to change the side of the routing. This test is based on the angle of the segments formed by the median control points and the center of the node. The change occurs between node one and two
	 * @param p1x X coordiate of the first node
	 * @param p1y Y coordiate of the first node
	 * @param p2x X coordiate of the second node
	 * @param p2y Y coordiate of the second node
	 * @param p3x X coordiate of the third node
	 * @param p3y Y coordiate of the third node
	 * @param distFromMedianPoint The distance between the median point and the link
	 * @param currentSide The current side: Either 1 or -1
	 * @author pvogt
	 */
	private boolean routingChangeSideTest(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y, float distFromMedianPoint, float currentSide) {
		float v1x = p1x - p2x;
		float v1y = p1y - p2y;
		float v2x = p3x - p2x;
		float v2y = p3y - p2y;
		float angle = currentSide * this.getAngleFromVectors(v1x, v1y, v2x, v2y);
		// A negative angle means there can be no collision at this point
		if(angle < 0) {
			return false;
		}
		else {
			float v1Norm = getNorm(v1x, v1y);
			float v2Norm = getNorm(v2x, v2y);
			float angle1 = (float) Math.atan(distFromMedianPoint * 2.0f / v1Norm);
			float angle2 = (float) Math.atan(distFromMedianPoint * 2.0f / v2Norm);
			return (angle1 * 2.0f > angle || angle2 * 2.0f > angle);
		}
	}

	/**
	 * Computes one component of the routing of a given link
	 * @param view Rectangle of the view.
	 * @param nodes List of the paintable nodes
	 * @param link The link whose routing we are drawing
	 * @param routeIndex The index of the routing component to be drawn
	 * @param set The <code>GraphDisplayInformationSet</code> containing all informations.
	 * @author pvogt
	 */
	public Shape[] computeRoutingShape(List<PaintableNode> nodes, PaintableLink link, int routeIndex, GraphDisplayInformationSet set) {
		final float sqrt2 = (float) Math.sqrt(2.0f);

		if(link.routing == null) {
			return null;
		}
		// Retrieve the position of the nodes we are routed through
		Point[] routing = new Point[link.routing[routeIndex].length];
		float[] nodeSizes = new float[routing.length];
		for(int i = 0; i < routing.length; i++) {
			PaintableNode pn = nodes.get(link.routing[routeIndex][i]);
			nodeSizes[i] = pn.size;
			routing[i] = viewToGraphics(pn.posX, pn.posY, set);
		}

		// Those are parameters with which one can tune the drawing
		// of the routing.
		float routingNodeDistanceFactor = 1.5f;
		float routingMedianDistanceFactor = 4.0f;
		float routingCurveSpeedIntermediateFactor = 2.0f;
		float routingCurveSpeedMedianFactor = 3.0f;
		float routingCurveSpeedEndsFactor = 2.0f;
		boolean routingDebug = false;

		// Compute all shapes
		Shape[] debugshapes = (routingDebug ? new Shape[(routing.length - 1) * 4] : null);
		Shape[] shapes = new Shape[(routing.length - 1) * 2];
		float oldp2x = 0.0f;
		float oldp2y = 0.0f;
		float oldside = 1.0f; // 1 == pass on the right, -1 == pass on the left
		float oldDirectionX = 0.0f;
		float oldDirectionY = 0.0f;
		for(int i = 0; i < routing.length - 1; i++) {
			float deltaXLink = routing[i + 1].x - routing[i + 0].x;
			float deltaYLink = routing[i + 1].y - routing[i + 0].y;
			float deltaNorm = getNorm(deltaXLink, deltaYLink);
			float nodeRadius = modifySize(nodeSizes[i +  + 0] / 2.0f, set);
			float nextNodeRadius = modifySize(nodeSizes[i + 1] / 2.0f, set);
			float side = oldside;
			// If the angle is too small, we will pass on the other side
			if(i != routing.length - 2) {
				if(routingChangeSideTest(routing[i + 0].x, routing[i + 0].y, routing[i + 1].x, routing[i + 1].y, routing[i + 2].x, routing[i + 2].y, nodeRadius * routingNodeDistanceFactor, oldside)) {
					side *= -1;
				}
			}
			// If we are still in the first part we can switch it's side too
			if(i == 0) {
				oldside = side;
			}
			// Point 1
			float p1x;
			float p1y;
			if(i == 0) {
				// Rotate to the right, normalize and scale
				p1x = routing[0].x - oldside * deltaYLink * nodeRadius / deltaNorm;
				p1y = routing[0].y + oldside * deltaXLink * nodeRadius / deltaNorm;
			}
			else {
				p1x = oldp2x;
				p1y = oldp2y;
			}
			// Control point 1
			float cp1x;
			float cp1y;
			if(i == 0) {
				float directionX = (deltaXLink - side * deltaYLink) * nodeRadius * (routingNodeDistanceFactor + routingCurveSpeedEndsFactor);
				float directionY = (deltaYLink + side * deltaXLink) * nodeRadius * (routingNodeDistanceFactor + routingCurveSpeedEndsFactor);
				float norm = deltaNorm * sqrt2;
				cp1x = routing[0].x + directionX / norm;
				cp1y = routing[0].y + directionY / norm;
			}
			else {
				// The real work is actually done only for
				// control point 2. Here we take advantage of
				// symetry to speed up computations.
				float directionX = (- oldDirectionX + oldside * oldDirectionY);
				float directionY = (- oldDirectionY - oldside * oldDirectionX);
				float norm = getNorm(directionX, directionY);
				cp1x = p1x - directionX * nodeRadius * routingCurveSpeedIntermediateFactor / norm;
				cp1y = p1y - directionY * nodeRadius * routingCurveSpeedIntermediateFactor / norm;
			}
			// Point 2
			float p2x;
			float p2y;
			if(i == routing.length - 2) {
				// Rotate to the right, normalize and scale
				p2x = routing[i + 1].x - side * deltaYLink * nextNodeRadius / deltaNorm;
				p2y = routing[i + 1].y + side * deltaXLink * nextNodeRadius / deltaNorm;
			}
			else {
				float normSegment1 = deltaNorm;
				float deltaXLink2 = routing[i + 2].x - routing[i + 1].x;
				float deltaYLink2 = routing[i + 2].y - routing[i + 1].y;
				float normSegment2 = getNorm(deltaXLink2, deltaYLink2);
				float directionX = (deltaXLink2 / normSegment2 + deltaXLink / normSegment1);
				float directionY = (deltaYLink2 / normSegment2 + deltaYLink / normSegment1);
				float norm = getNorm(directionX, directionY);
				p2x = routing[i + 1].x - side * directionY * nextNodeRadius * routingNodeDistanceFactor / norm;
				p2y = routing[i + 1].y + side * directionX * nextNodeRadius * routingNodeDistanceFactor / norm;
			}
			// Median point
			float mpx;
			float mpy;
			if(side == oldside) {
				mpx = routing[i].x + (deltaXLink / 2) - side * deltaYLink * ((nodeRadius + nextNodeRadius) * 0.5f) * routingMedianDistanceFactor / deltaNorm;
				mpy = routing[i].y + (deltaYLink / 2) + side * deltaXLink * ((nodeRadius + nextNodeRadius) * 0.5f) * routingMedianDistanceFactor / deltaNorm;
			}
			else {
				mpx = routing[i].x + (deltaXLink / 2);
				mpy = routing[i].y + (deltaYLink / 2);
			}
			// Median control point 1
			float mcp1x;
			float mcp1y;
			if(side == oldside) {
				mcp1x = mpx - deltaXLink * nodeRadius * routingCurveSpeedMedianFactor / deltaNorm;
				mcp1y = mpy - deltaYLink * nodeRadius * routingCurveSpeedMedianFactor / deltaNorm;
			}
			else {
				mcp1x = mpx - oldside * deltaYLink * nodeRadius * routingCurveSpeedMedianFactor / deltaNorm;
				mcp1y = mpy + oldside * deltaXLink * nodeRadius * routingCurveSpeedMedianFactor / deltaNorm;
			}
			// Median control point 2
			float mcp2x = mpx + (mpx - mcp1x);
			float mcp2y = mpy + (mpy - mcp1y);
			// Control point 2
			float cp2x;
			float cp2y;
			if(i == routing.length - 2) {
				float directionX = (- deltaXLink - side * deltaYLink) * nextNodeRadius * (routingNodeDistanceFactor + routingCurveSpeedEndsFactor);
				float directionY = (- deltaYLink + side * deltaXLink) * nextNodeRadius * (routingNodeDistanceFactor + routingCurveSpeedEndsFactor);
				float norm = deltaNorm * sqrt2;
				cp2x = routing[routing.length - 1].x + directionX / norm;
				cp2y = routing[routing.length - 1].y + directionY / norm;
			}
			else {
				float normSegment1 = deltaNorm;
				float deltaXLink2 = routing[i + 2].x - routing[i + 1].x;
				float deltaYLink2 = routing[i + 2].y - routing[i + 1].y;
				float normSegment2 = getNorm(deltaXLink2, deltaYLink2);
				float directionX = deltaXLink2 / normSegment2 + deltaXLink / normSegment1;
				float directionY = deltaYLink2 / normSegment2 + deltaYLink / normSegment1;
				oldDirectionX = directionX;
				oldDirectionY = directionY;
				directionX = (oldDirectionX + side * oldDirectionY);
				directionY = (oldDirectionY - side * oldDirectionX);
				float norm = getNorm(directionX, directionY);
				cp2x = p2x - directionX * nextNodeRadius * routingCurveSpeedIntermediateFactor / norm;
				cp2y = p2y - directionY * nextNodeRadius * routingCurveSpeedIntermediateFactor / norm;
			}
			Shape s1 = new CubicCurve2D.Float(p1x, p1y, cp1x, cp1y, mcp1x, mcp1y, mpx, mpy);
			Shape s2 = new CubicCurve2D.Float(mpx, mpy, mcp2x, mcp2y, cp2x, cp2y, p2x, p2y);
			if(routingDebug) {
				Shape d11 = new Line2D.Float(p1x, p1y, cp1x, cp1y);
				Shape d12 = new Line2D.Float(mcp1x, mcp1y, mpx, mpy);
				Shape d21 = new Line2D.Float(mpx, mpy, mcp2x, mcp2y);
				Shape d22 = new Line2D.Float(cp2x, cp2y, p2x, p2y);
				debugshapes[i * 4 + 0] = d11;
				debugshapes[i * 4 + 1] = d12;
				debugshapes[i * 4 + 2] = d21;
				debugshapes[i * 4 + 3] = d22;
			}
			shapes[i * 2 + 0] = s1;
			shapes[i * 2 + 1] = s2;
			oldp2x = p2x;
			oldp2y = p2y;
			oldside = side;
		}
		if(routingDebug) {
			Shape[] allShapes = new Shape[shapes.length + debugshapes.length];
			System.arraycopy(shapes,0,allShapes,0,shapes.length);
			System.arraycopy(debugshapes,0, allShapes, shapes.length, debugshapes.length);
			return allShapes;
		}
		else {
			return shapes;
		}
	}

	/**
	 * Draws a link.
	 * 
	 * @param view Rectangle of the view.
	 * @param nodes List of the paintable nodes
	 * @param link link to paint.
	 * @param screen Graphics where to paint the link.
	 * @param set Information about the way to draw the link.
	 * @param scale scale to be applied to the width
	 */ 
	protected void drawLink(List<PaintableNode> nodes, PaintableLink link, Graphics screen, GraphDisplayInformationSet set, boolean selected, float scale) {
		Rectangle view = set.getViewRectangle();
		if (view != null) {
			Rectangle tt = link.getRectangle();
			if (tt.intersects(view) == false) {
				return;
			}
		}
		
		
		Graphics2D g2 = (Graphics2D)screen;
		Color oldColor = g2.getColor();
		float width = modifySize(link.width, set);
		width = ((width<1) ? 1 : width);
		//Computes the shape of the link
		Shape linkShape = computeLinkShape(link, set);
		//Computes the stroke of the link
		Stroke linkStroke = computeLinkStroke(nodes, link, set, computeShapeLength(linkShape), width , scale);
		

		Shape arrow = null;
		//If directed, computes the arrow
		if (link.directed) {
			PaintableNode node;
			if (set.movedNode != null) {
				int movedId = set.movedNode.getId();
				if (movedId < link.dest) {
					node = nodes.get(link.dest-1);
				} else if (movedId == link.dest) {
					node = set.movedNode;
				} else {
					node = nodes.get(link.dest);
				}
			}
			else {
				node = nodes.get(link.dest);
			}
			Point center = viewToGraphics(link.endX, link.endY, set);
			double radius = 0;
			if (node != null) {
				radius = modifySize(node.size/2f, set);
			} else {
				radius = 30;
			}

			Shape[] ret = computeArrow(linkShape,modifySize(8, set), 77f , new Point2D.Double(center.x, center.y), radius*1.5f);
			if (ret != null) {
				arrow = ret[0];
				linkShape = ret[1];
			}
		}

		// If selected and routing information is available, display it
		if (selected && link.routing != null) {
			Shape[] routing = computeRoutingShape(nodes, link, set);
			for(Shape curve : routing) {
				g2.draw(curve);
			}
		}

		//If 2 colors, draws the second color before the first one
		if (link.color2 != null && link.dash.length != 0) {
			g2.setStroke(new BasicStroke(width*scale));
			g2.setColor(link.color2);
			g2.draw(linkShape);
		}

		//Draws the link
		g2.setStroke(linkStroke);
		g2.setColor(link.color);
		g2.draw(linkShape);

		//Draws the arrow
		if (arrow != null) {
			g2.setStroke(new BasicStroke(width*scale*2));
			g2.setColor(link.color);
			g2.fill(arrow);
			g2.draw(arrow);
		}

		// Draw Label
		if (!link.label.equals("")) {

			int i = 0;
			PathIterator ite = linkShape.getPathIterator(null, 1);
			while (ite.isDone() == false) {
				ite.next();
				i++;
			}
			ite = linkShape.getPathIterator(null, 1);
			float[] buf = new float[6];	
			int xlab = 0;
			int ylab = 0;
			if (i % 2 == 0) {
				for (int j = 0 ; j < (i/2)-1 ; j++) {
					ite.next();
				}
				// in this case, must average the middle points
				for (int j = 0 ; j < 2 ; j++) {
					if (ite.currentSegment(buf) == PathIterator.SEG_LINETO) {
						xlab += buf[0];
						ylab += buf[1];
						ite.next();	
					} else {
						xlab += buf[0];
						ylab += buf[1];
						ite.next();				
					}			
				}
				xlab /= 2;
				ylab /= 2;
			} else {
				for (int j = 0 ; j < (i/2) ; j++) {	
					ite.next();
				}
				ite.currentSegment(buf);
				xlab += buf[0];
				ylab += buf[1];					
			}

			float fontHeight = modifySize(11, set);
			java.awt.Font f = new java.awt.Font("Arial", java.awt.Font.BOLD, (int)fontHeight);			
			g2.setFont(f);
		//	FontMetrics fm =  g2.getFontMetrics(f);

			Rectangle2D rect = g2.getFont().getStringBounds(link.label, g2.getFontRenderContext());

			int corx = (int)(rect.getWidth()/2);
			int cory = (int)(rect.getHeight());
			
			ylab -= 3;

			g2.setColor(Color.LIGHT_GRAY);
			g2.drawRect(xlab-1-corx, ylab-1-cory, (int)rect.getWidth()+4, cory+2);
			g2.setColor(link.labelColor);
			g2.fillRect(xlab-corx, ylab-cory, (int)rect.getWidth()+3, cory+1);
			g2.setColor(Color.BLACK);
			g2.drawString(link.label, xlab+2-corx, ylab-1);
		}

		g2.setColor(oldColor);
	}

	/**
	 * Computes an approximation of the length of a shape.
	 * <BR>#author fmoulin
	 * 
	 * @param s
	 * @return approximation of the length
	 */
	protected int computeShapeLength(Shape s) {
		PathIterator pi = s.getPathIterator(null, 1);
		double[] coords = new double[6];
		double[] last = new double[2];

		double length = 0;
		while (!pi.isDone()) {
			int type = pi.currentSegment(coords);
			if (type == PathIterator.SEG_MOVETO) {
				last[0] = coords[0];
				last[1] = coords[1];
			}
			if (type == PathIterator.SEG_LINETO) {
				length += Point2D.distance(coords[0], coords[1], last[0], last[1]);
				last[0] = coords[0];
				last[1] = coords[1];
			}
			pi.next();
		}
		return (int)length;
	}

	/**
	 * Computes the stroke that should used to draw the link.
	 * <BR>#author fmoulin
	 * 
	 * @param nodes List of the paintable nodes
	 * @param link link.
	 * @param set Information about the way to draw a link.
	 * @param length length of the link
	 * @param width width of the link
	 * @param scale scale to be applied to the link
	 * @return stroke that could used to draw the link
	 */
	protected Stroke computeLinkStroke(List<PaintableNode> nodes, PaintableLink link, GraphDisplayInformationSet set, int length, float width, float scale) {
		if (link.dash != null && link.dash.length > 0) {
			float[] dash;

			//Computes the dash array
			if (link.isDashPerCent) {
				boolean odd = link.dash.length % 2 == 1;
				dash = new float[link.dash.length + (odd ? 1 : 0)];
				for (int i = 0; i < dash.length-(odd ? 1 : 0); i++) {
					dash[i] = (length*link.dash[i])/100f;
				}
				if (odd) {
					dash[dash.length-1] = 0;
				}
			}
			else {
				dash = new float[link.dash.length];
				for (int i = 0; i < dash.length; i++) {
					dash[i] = modifySize(link.dash[i], set);
				}
			}

			//Computes the dash length
			float lengthDash = 0;
			for (float f : dash) {
				lengthDash += f;
			}
			if (dash.length % 2 != 0 && !link.isDashPerCent) {
				lengthDash *= 2;
			}


			//Computes the speed
			float speed = 0;

			PaintableNode startNode = null;
			PaintableNode endNode   = null;
			try {
				startNode = nodes.get(link.orig);
				endNode   = nodes.get(link.dest);
			}
			catch (Exception e) {
				// to fix: some link may end in the moved node and be lost...
			}

			if (startNode == null || endNode == null || startNode.isMoving || endNode.isMoving) {
				speed = 0;
			} else if (set.useAnimation) {
				speed = link.speed * (1/set.sizeModificator);
			}

			//Computes the offset for animations
			float offset;
			if (speed == 0) {
				offset = lengthDash;
			} else {
				offset = ((new Date().getTime() % (int)(speed*lengthDash))/speed);
			}

			//Returns the right stroke
			return new BasicStroke(((width<1) ? 1f : width*scale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER , 10f, dash, lengthDash-offset);
		} else {
			return new BasicStroke(((width<1) ? 1f : width*scale));
		}
	}

	protected void drawOutlinedLink(List<PaintableNode> nodes, PaintableLink link, Graphics screen, GraphDisplayInformationSet set) {
		if (HIGHLIGHT) {
			drawLink(nodes, link, screen, set, true, 1.5f);
		} else {
			drawLink(nodes, link, screen, set);
		}
	}


	protected static Color getColor(Color color, int alpha) {
		if (color != null) {
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
		}
		return color;
	}

	protected static Color getColor(Color color) {
		return getColor(color, 255);
	}
}
