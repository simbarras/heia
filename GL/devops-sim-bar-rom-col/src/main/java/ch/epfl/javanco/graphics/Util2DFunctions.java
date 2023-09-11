package ch.epfl.javanco.graphics;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import ch.epfl.general_libraries.utils.Sparse2DList;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class Util2DFunctions {

	private int[] xs;
	private int[] ys;

	public Util2DFunctions(AbstractGraphHandler agh) {
		xs = new int[agh.getHighestNodeIndex()+1];
		ys = new int[agh.getHighestNodeIndex()+1];
		for (NodeContainer nc : agh.getNodeContainers()) {
			Point p = nc.getCoordinate();
			xs[nc.getIndex()] = p.x;
			ys[nc.getIndex()] = p.y;
		}
	}

	public static double distance(NodeContainer nc1, NodeContainer nc2) {
		return Point2D.distance(nc1.getCoordinate().getX(),
				nc1.getCoordinate().getY(),
				nc2.getCoordinate().getX(),
				nc2.getCoordinate().getY());

	}

	public static double distance(Point2D p, NodeContainer nc) {
		return Point2D.distance(p.getX(), p.getY(), nc.getCoordinate().getX(), nc.getCoordinate().getY());
	}

	public static double length(LinkContainer lc) {
		return distance(lc.getStartNodeContainer(), lc.getEndNodeContainer());
	}

	public static double angle(LinkContainer lc1, LinkContainer lc2) {
		int s1 = lc1.getStartNodeIndex();
		int s2 = lc2.getStartNodeIndex();
		int e1 = lc1.getEndNodeIndex();
		int e2 = lc2.getEndNodeIndex();
		NodeContainer common, alt1, alt2;
		if (s1== s2) {
			common = lc1.getStartNodeContainer();
			alt1 = lc1.getEndNodeContainer();
			alt2 = lc2.getEndNodeContainer();
		} else if (s1 == e2) {
			common = lc1.getStartNodeContainer();
			alt1 = lc1.getEndNodeContainer();
			alt2 = lc2.getStartNodeContainer();
		} else if (e1 == s2) {
			common = lc1.getEndNodeContainer();
			alt1 = lc1.getStartNodeContainer();
			alt2 = lc2.getEndNodeContainer();
		} else if (e1 == e2) {
			common = lc1.getEndNodeContainer();
			alt1 = lc1.getStartNodeContainer();
			alt2 = lc2.getStartNodeContainer();
		} else {
			throw new IllegalStateException("Can compute an angle only if two links share a common end point");
		}
		return angle(common, alt1, alt2);
	}

	public static double angle(NodeContainer nc, LinkContainer lc) {
		if (lc.hasForExtremity(nc.getIndex())) {
			throw new IllegalStateException("Three points are required to compute the angle");
		}
		return angle(lc.getStartNodeContainer(), nc, lc.getEndNodeContainer());
	}

	public static double angle(NodeContainer common, NodeContainer alt1, NodeContainer alt2) {
		double Xa = alt1.getCoordinate().getX();
		double Ya = alt1.getCoordinate().getY();
		double Xb = common.getCoordinate().getX();
		double Yb = common.getCoordinate().getY();
		double Xc = alt2.getCoordinate().getX();
		double Yc = alt2.getCoordinate().getY();
		return angleInternal(Xa,Ya,Xb,Yb,Xc,Yc);
	}

	public static double angle(Point2D p, NodeContainer alt1, NodeContainer alt2) {
		double Xa = alt1.getCoordinate().getX();
		double Ya = alt1.getCoordinate().getY();
		double Xb = p.getX();
		double Yb = p.getY();
		double Xc = alt2.getCoordinate().getX();
		double Yc = alt2.getCoordinate().getY();
		return angleInternal(Xa,Ya,Xb,Yb,Xc,Yc);
	}

	private static double angleInternal(double x1,double y1,double x0,double y0,double x2,double y2) {
		x1 = x1 - x0;
		y1 = y1 - y0;
		x2 = x2 - x0;
		y2 = y2 - y0;

		double hyp1 = Point2D.distance(x1,y1,0,0);
		double hyp2 = Point2D.distance(x2,y2,0,0);
		double alpha1, alpha2;

		if (y1 < 0) {
			alpha1 = -Math.acos(x1/hyp1) + Math.PI*2;
		} else {
			alpha1 = Math.acos(x1/hyp1);
		}
		if (y2 < 0) {
			alpha2 = -Math.acos(x2/hyp2) + Math.PI*2;
		} else {
			alpha2 = Math.acos(x2/hyp2);
		}
		double diff = alpha2 - alpha1;
		if (diff > Math.PI) {
			diff -= Math.PI*2;
		}
		if (diff > Math.PI) {
			diff -= Math.PI;
		}
		if (diff < -Math.PI) {
			diff +=Math.PI;
		}
		return diff;
	}

	public static Point2D vector(NodeContainer nc1, NodeContainer nc2) {
		double ax = nc1.getCoordinate().getX();
		double ay = nc1.getCoordinate().getY();
		double bx = nc2.getCoordinate().getX();
		double by = nc2.getCoordinate().getY();
		return new Point2D.Double(bx-ax, by-ay);
	}

	public static Point getIntersectionPoint(LinkContainer lc1, LinkContainer lc2) {
		Point a1 = lc1.getStartNodeContainer().getCoordinate();
		Point a2 = lc1.getEndNodeContainer().getCoordinate();
		Point b1 = lc2.getStartNodeContainer().getCoordinate();
		Point b2 = lc2.getEndNodeContainer().getCoordinate();
		return intersects(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y);
	}

	public static boolean intersects(LinkContainer lc1, LinkContainer lc2) {
		Point a1 = lc1.getStartNodeContainer().getCoordinate();
		Point a2 = lc1.getEndNodeContainer().getCoordinate();
		Point b1 = lc2.getStartNodeContainer().getCoordinate();
		Point b2 = lc2.getEndNodeContainer().getCoordinate();
		return intersects(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y) != null;
	}

	public static boolean intersects(NodeContainer n1, NodeContainer n2, LinkContainer lc) {
		Point a1 = n1.getCoordinate();
		Point a2 = n2.getCoordinate();
		Point b1 = lc.getStartNodeContainer().getCoordinate();
		Point b2 = lc.getEndNodeContainer().getCoordinate();
		return intersects(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y) != null;
	}

	public boolean intersects_(NodeContainer n1, NodeContainer n2, LinkContainer lc) {
		int i1 = n1.getIndex();
		int i2 = n2.getIndex();
		int i3 = lc.getStartNodeIndex();
		int i4 = lc.getEndNodeIndex();
		return intersects(xs[i1], ys[i1], xs[i2], ys[i2], xs[i3], ys[i3], xs[i4], ys[i4]) != null;
	}

	public static boolean intersects(NodeContainer n1, NodeContainer n2, Collection<LinkContainer> col) {
		for (LinkContainer lc : col) {
			if (intersects(n1,n2, lc)) {
				return true;
			}
		}
		return false;
	}

	public boolean intersects_(NodeContainer n1, NodeContainer n2, Collection<LinkContainer> col) {
		for (LinkContainer lc : col) {
			if (intersects_(n1,n2, lc)) {
				return true;
			}
		}
		return false;
	}

	public static Point intersects(int ax1, int ay1, int ax2, int ay2, int bx1, int by1, int bx2, int by2) {
		if (ax1==bx1 && ay1==by1 && (ax2 != bx2 || ay2 != by2)) {
			return null;
		}
		if (ax1==bx2 && ay1==by2 && (ax2 != bx1 || ay2 != by1)) {
			return null;
		}
		if (ax2==bx1 && ay2==by1 && (ax1 != bx2 || ay1 != by2)) {
			return null;
		}
		if (ax2==bx2 && ay2==by2 && (ax1 != bx1 || ay1 != by1)) {
			return null;
		}
		int prod = ((ax1-ax2)*(by1-by2)) - ((ay1-ay2)*(bx1-bx2));
		if (prod == 0) {
			return null;
		}
		int interx = ((bx1-bx2)*(ax1*ay2-ay1*ax2)-(ax1-ax2)*(bx1*by2-by1*bx2))/prod;
		int intery = ((by1-by2)*(ax1*ay2-ay1*ax2)-(ay1-ay2)*(bx1*by2-by1*bx2))/prod;

		if (interx < Math.min(ax1,ax2) || interx > Math.max(ax1,ax2)) {
			return null;
		}
		if (interx < Math.min(bx1, bx2) || interx > Math.max(bx1, bx2)) {
			return null;
		}
		if (intery < Math.min(ay1, ay2) || intery > Math.max(ay1, ay2)) {
			return null;
		}
		if (intery < Math.min(by1, by2) || intery > Math.max(by1, by2)) {
			return null;
		}
		return new Point(interx, intery);
	}
	
	public static Sparse2DList<LinkContainer> planarize(AbstractGraphHandler agh) {
		return planarize(agh, agh.getLinkContainers(), true);
	}
	
	public static Sparse2DList<LinkContainer> planarize(AbstractGraphHandler agh, boolean rem) {
		return planarize(agh, agh.getLinkContainers(), rem);
	}
	
	private static class CoeffPoint implements Comparable<CoeffPoint> {
		private NodeContainer nc;
		private double coeff;
		
		private CoeffPoint(NodeContainer nc, double c) {
			coeff = c;
			this.nc = nc;
		}
		@Override
		public int compareTo(CoeffPoint o) {
			return (int)Math.signum(this.coeff - o.coeff);
		}
	}
	
	
	public static Sparse2DList<LinkContainer> planarize(
			AbstractGraphHandler agh, 
			ArrayList<LinkContainer> can,
			boolean rem) {

		Sparse2DList<CoeffPoint> points = new Sparse2DList<CoeffPoint>();
		
		HashSet<LinkContainer> concerned = new HashSet<LinkContainer>();
		for (int i = 0 ; i < can.size()-1 ; i++) {
			for (int j = i + 1; j < can.size() ; j++) {
				Point p = getIntersectionPoint(can.get(i), can.get(j));
				if (p != null) {
					LinkContainer lc1 = can.get(i);
					LinkContainer lc2 = can.get(j);
					int start1 = lc1.getStartNodeContainer().getIndex();
					int start2 = lc2.getStartNodeContainer().getIndex();
					int end1 = lc1.getEndNodeContainer().getIndex();
					int end2 = lc2.getEndNodeContainer().getIndex();
					
					NodeContainer nc = agh.newNode(p.x, p.y);
					nc.attribute("node_color").setValue("255, 240, 220");
					nc.attribute("node_size").setValue("8");
					
					double percentage1 = lc1.getRelativePosition(p);
					double percentage2 = lc2.getRelativePosition(p);
					
					points.add(start1, end1, new CoeffPoint(nc, percentage1));
					points.add(start2, end2, new CoeffPoint(nc, percentage2));
					
					concerned.add(lc1);
					concerned.add(lc2);
				/*	
					int ncIndex = nc.getIndex();
					nc.attribute("node_color").setValue("255, 240, 220");
					nc.attribute("node_size").setValue("8");
					toRem.add(lc1);
					toRem.add(lc2);
					LinkContainer[] arr = new LinkContainer[4];
					arr[0] = agh.newLink(start1, ncIndex);
					arr[1] = agh.newLink(ncIndex, end1);
					list.add(start1, end1, arr[0]);
					list.add(start1, end1, arr[1]);					
					arr[2] = agh.newLink(start2, ncIndex);
					arr[3] = agh.newLink(ncIndex, end2);
					list.add(start2, end2, arr[2]);
					list.add(start2, end2, arr[3]);					
					if (lc1.booleanAttribute("directed")) {
						arr[0].attribute("directed").setValue("true");
						arr[1].attribute("directed").setValue("true");
					}
					if (lc2.booleanAttribute("directed")) {
						arr[2].attribute("directed").setValue("true");
						arr[3].attribute("directed").setValue("true");
					}	*/				
				}
			}
		}
		
		Sparse2DList<LinkContainer> list = new Sparse2DList<LinkContainer>();
		
		for (LinkContainer lc : concerned) {
			int start = lc.getStartNodeIndex();
			int end = lc.getEndNodeIndex();
			ArrayList<CoeffPoint> ps = points.get(start, end);
			if (ps.size() == 0) {
				throw new IllegalStateException("shouldn't be there");
			}
			Collections.sort(ps);
			ArrayList<LinkContainer> added = new ArrayList<LinkContainer>(4);
			added.add(agh.newLink(start, ps.get(0).nc.getIndex()));
			
			added.add(agh.newLink(ps.get(ps.size()-1).nc.getIndex(), end));
			for (int i = 0 ; i < ps.size()-1 ; i++) {
				added.add(agh.newLink(ps.get(i).nc.getIndex(), ps.get(i+1).nc.getIndex()));
			}
			if (lc.booleanAttribute("directed")) {
				for (LinkContainer add : added) {
					add.attribute("directed").setValue("true");
				}
			}
			
			list.addAll(start, end, added);
			
		}
		if (rem)
			agh.removeElements(concerned);
		return list;
	}

}
