package tsp;

//======================================================================
// Inlining

public class TSP11 implements TSP {
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
            for (j = 0; j < n; j++) {
                if (visited[j])
                    continue;
                if (Math.sqrt((t[thisPt].x - t[j].x) * (t[thisPt].x - t[j].x) + ((t[thisPt].y - t[j].y) * (t[thisPt].y - t[j].y))) < shortestDist) {
                    shortestDist = Math.sqrt((t[thisPt].x - t[j].x) * (t[thisPt].x - t[j].x) + (t[thisPt].y - t[j].y) * (t[thisPt].y - t[j].y));
                    closestPt = j;
                }
            }
            path[i] = closestPt;
            visited[closestPt] = true;
            thisPt = closestPt;
        }
    }
}
