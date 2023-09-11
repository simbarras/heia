package tsp;

//======================================================================
// Inlining + caching + Sortir les invariants + identité algébrique

public class TSP16 implements TSP {
    public void salesman(TSPPoint[] t, int[] path) {
        int i, j;
        int n = t.length;
        boolean[] visited = new boolean[n];
        int thisPt, closestPt = 0;
        thisPt = n - 1;
        if (thisPt < 0) return;
        visited[thisPt] = true;
        path[0] = n - 1;  // chose the starting city
        TSPPoint tJ;
        for (i = 1; i < n; i++) {
            double shortestDist = Double.MAX_VALUE;
            double thisPtX = t[thisPt].x;
            double thisPtY = t[thisPt].y;
            for (j = 0; j < n; j++) {
                if (visited[j])
                    continue;
                tJ = t[j];
                double eX = thisPtX - tJ.x;
                double eY = thisPtY - tJ.y;
                if ((eX * eX + eY * eY) < shortestDist) {
                    shortestDist = eX * eX + eY * eY;
                    closestPt = j;
                }
            }
            path[i] = closestPt;
            visited[closestPt] = true;
            thisPt = closestPt;
        }
    }
}