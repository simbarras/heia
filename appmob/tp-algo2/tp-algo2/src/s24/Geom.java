package s24;
import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
// =========================
public class Geom {

  public static int signedArea(Point p1, Point p2, Point p3) {
    return (p2.x-p1.x)*(p3.y-p1.y) - (p3.x-p1.x)*(p2.y-p1.y);
    // negative if clockwise; twice the area of the triangle p1-p2-p3
  }

  public static int ccw(Point p1, Point p2, Point p3) {
    int a = signedArea(p1, p2, p3);
    if (a < 0) return -1;
    if (a > 0) return +1;
    return 0;
  }

  public static Point[] rndPointSet(Random r, int n, int maxCoord) {
    Point[] t = new Point[n];
    Point p;
    HashSet<Point> h = new HashSet<>();
    Iterator<Point> itr;
    while(h.size()<n) {
      p = new Point(r.nextInt(maxCoord), r.nextInt(maxCoord));
      h.add(p);
    }
    itr=h.iterator();
    for (int i=0; i<n; i++) {
      p = itr.next();
      t[i] = p;
    }
    return t;
  }
  // ------------------------------------------------------------
  // Adapted from W.R. Franklin code. This algo may report points on
  // a boundary segment, but not always (the effect is yet systematic,
  // but not intuitive)
  public static boolean isInPolygon(Point [] polyg, Point p) {
    boolean c = false;
    int i, j;
    int npol = polyg.length;
    for (i = 0, j = npol-1; i < npol; j = i++) {
      int dxji = (polyg[j].x - polyg[i].x);
      int dyji = (polyg[j].y - polyg[i].y);
      int dxpi = (p.x - polyg[i].x);
      int dypi = (p.y - polyg[i].y);
      if ((((polyg[i].y<=p.y) && (p.y<polyg[j].y)) ||
           ((polyg[j].y<=p.y) && (p.y<polyg[i].y))) &&
            dxpi < dxji * dypi / (float) dyji )
        c = !c;
    }
    return c;
  }

}
