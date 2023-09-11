package s27;

import java.awt.Point;

// The challenge is to reach the "hacked it!" line… 
public class ClosestPairBrainteaserV2 {
  public static Point[] naiveClosest(Point[] pts) {
    int n = pts.length;
    if (n < 2) throw new IllegalArgumentException();
    Point[] res = new Point[2];
    double d = Double.POSITIVE_INFINITY;
    for (int i=0; i<n; i++) {
      for (int j=i+1; j<n; j++) {
        Point pi = pts[i], pj = pts[j];
        double thisDist = pi.distance(pj);
        if (thisDist < d) {
          res[0] = pi; 
          res[1] = pj;
          d = thisDist;
        }
      }
    }
    if (res[0] == null)
      throw new IllegalStateException("Well, I hacked it!");
    return res;
  }
}
