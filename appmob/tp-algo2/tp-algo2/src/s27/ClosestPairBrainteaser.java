package s27;

import java.awt.Point;

// The challenge is to reach the "hacked it!" line… 
public class ClosestPairBrainteaser {
  public static Point[] naiveClosest(Point[] pts) {
    int n = pts.length;
    if (n < 2) throw new IllegalArgumentException();
    Point[] res = new Point[2];
    double d = Double.POSITIVE_INFINITY;
    for (int i=0; i<n; i++) {
      for (int j=i+1; j<n; j++) {
        double thisDist = pts[i].distance(pts[j]);
        if (thisDist < d) {
          res[0] = pts[i]; 
          res[1] = pts[j];
          d = thisDist;
        }
      }
    }
    if (res[0] == null)
      throw new IllegalStateException("Well, I hacked it!");
    return res;
  }
}
