package ch.epfl.javanco.graphics;

import java.awt.Point;

public interface Curvable {
	

	public void setCurveA(int i);
	public void setCurveB(int i);
	public void setCurveAngleA(int i);
	public void setCurveAngleB(int i);
	public void saveCurve();
	public int[] getCurve();
	public Point getDirection();
}
