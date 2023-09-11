package ch.epfl.javanco.graphics;

import java.awt.Polygon;


public class LinkPolygon extends Polygon implements ElementCoord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2511043795648995714L;

	public LinkPolygon(int[] xpoints, int[] ypoints, int npoints) {
		super(xpoints, ypoints, npoints);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Polygon with " + super.npoints + " : [");
		for (int i = 0 ; i < super.xpoints.length ; i++) {
			sb.append("(" + super.xpoints[i] + "," + super.ypoints[i] + ")");
			if (i < super.xpoints.length - 1) { sb.append(","); }
		}
		sb.append("]");
		return sb.toString();
	}

	public void reverseYCoord(int height) {
		for ( int i = 0 ; i < super.npoints ; i++) {
			super.ypoints[i] = height - super.ypoints[i];
		}
	}

	public int[] getCoords() {
		int[] coor = new int[super.npoints * 2];
		for (int i = 0 ; i < super.npoints ; i++) {
			coor[(2*i)] = super.xpoints[i];
			coor[(2*i)+1] = super.ypoints[i];
		}
		return coor;
	}
}
