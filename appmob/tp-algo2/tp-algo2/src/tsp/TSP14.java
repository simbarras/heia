package tsp;

//======================================================================
// Inlining + caching + Sortir les invariants

public class TSP14 implements TSP {
    public void salesman(TSPPoint[] t, int[] path) {
        int i, j;
        int n = t.length;
        boolean[] visited = new boolean[n];
        int thisPt, closestPt = 0;
        double shortestDist;
        thisPt = n - 1;
        if (thisPt < 0) return;
        visited[thisPt] = true;
        path[0] = n - 1;  // chose the starting city
        for (i = 1; i < n; i++) {
            shortestDist = Double.MAX_VALUE;
            double thisPtX = t[thisPt].x;
            double thisPtY = t[thisPt].y;
            for (j = 0; j < n; j++) {
                if (visited[j])
                    continue;
                double eX = thisPtX - t[j].x;
                double eY = thisPtY - t[j].y;
                if (Math.sqrt(eX * eX + eY * eY) < shortestDist) {
                    shortestDist = Math.sqrt(eX * eX + eY * eY);
                    closestPt = j;
                }
            }
            path[i] = closestPt;
            visited[closestPt] = true;
            thisPt = closestPt;
        }
    }
}
