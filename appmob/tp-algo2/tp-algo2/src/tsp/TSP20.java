package tsp;

//======================================================================
// Inlining + caching + Sortir les invariants + identité algébrique + optimisation du visited

public class TSP20 implements TSP {
    public void salesman(TSPPoint[] t, int[] path) {
        int i, j;
        int nbCities = t.length;
        int maxIndex = nbCities - 1;
        int[] indexes = new int[maxIndex];

        for (int k = 0; k < maxIndex; k++) {
            indexes[k] = k;
        }

        int thisPt, closestPt = 0;
        thisPt = maxIndex;
        if (thisPt < 0) return;
        path[0] = maxIndex;  // chose the starting city
        for (i = 1; i < nbCities; i++) {
            double shortestDist = Double.MAX_VALUE;
            double thisPtX = t[thisPt].x;
            double thisPtY = t[thisPt].y;
            for (j = 0; j < maxIndex; j++) {
                int tmpI = indexes[j];
                double eX = thisPtX - t[tmpI].x;
                if (((eX * eX) >= shortestDist)) continue;
                double eY = thisPtY - t[tmpI].y;
                if (((eY * eY) < shortestDist) && ((eX * eX + eY * eY) < shortestDist)) {
                    shortestDist = eX * eX + eY * eY;
                    closestPt = j;
                }
            }
            path[i] = indexes[closestPt];
            thisPt = indexes[closestPt];
            indexes[closestPt] = indexes[--maxIndex];
        }
    }
}
