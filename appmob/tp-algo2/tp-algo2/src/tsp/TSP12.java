package tsp;
//======================================================================
// Caching

public class TSP12 implements TSP {
  public void salesman(TSPPoint [] t, int [] path) {
    int i, j;
    int n = t.length;
    boolean[] visited = new boolean[n];
    int thisPt, closestPt = 0;
    double shortestDist;

    thisPt = n-1;
    if (thisPt < 0) return;
    visited[thisPt] = true;
    path[0] = n-1;  // chose the starting city
    for(i=1; i<n; i++) {
      shortestDist = Double.MAX_VALUE;
      for(j=0; j<n; j++) {
        if (visited[j])
          continue;
        double d = distance(t[thisPt], t[j]);
        if (d < shortestDist ) {
          shortestDist = d;
          closestPt = j;
        }
      }
      path[i] = closestPt;
      visited[closestPt] = true;
      thisPt = closestPt;
    }
  }
  //----------------------------------
  static private double sqr(double a) {
    return a*a;
  }
  //---------------------------------
  static private double distance(TSPPoint p1, TSPPoint p2) {
    return Math.sqrt(sqr(p1.x-p2.x) + sqr(p1.y-p2.y));
  }
}
