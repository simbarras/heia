package s19;


import java.util.List;
import java.util.ArrayList;

public class BipartiteAnalysis {
    private final UGraph graph;
    private boolean isBipartite;
    private final boolean[] isVisited;
    private final boolean[] isWhite;  // meaningful if isBipartite
    private final int[] parent;   // the traversal tree structure
    private List<Integer> oddCycle; // meaningful if !isBipartite

    public BipartiteAnalysis(UGraph g) {
        int n = g.nbOfVertices();
        graph = g;
        isVisited = new boolean[n];
        isWhite = new boolean[n];
        parent = new int[n];
        oddCycle = new ArrayList<>();
        isBipartite = true;
        for (int i = 0; i < n; i++) {
            if (!isVisited[i] && isBipartite) dft(i);
        }
    }

    // Depth-first traversal
    // PRE: vid is not visited, but has been assigned a color
    // POST: either the whole subtree has been colored,
    //           or an odd cycle has been found via a back edge
    //              (meaning the graph is not bipartite).
    // Caution: exit the traversal once you find an "odd-cycle";
    //          a "back edge" is later met as a "forward" one...
    private void dft(int vid) {

        if (isVisited[vid]) return;
        isVisited[vid] = true;

        for (Integer neighbour : graph.neighboursOf(vid)) {
            if (!isBipartite) return;
            if (isVisited[neighbour]) {
                if (isWhite[neighbour] == isWhite[vid]) {
                    isBipartite = false;
                    rememberOddCycle(neighbour, vid);
                    return;
                }
                continue;
            }
            isWhite[neighbour] = !isWhite[vid];
            parent[neighbour] = vid;
            dft(neighbour);
        }
    }

    // build the oddCycle list with the tree path: <descendantId, ..., ancestorId>
    private void rememberOddCycle(int ancestorId, int descendantId) {
        oddCycle.add(descendantId);
        while (descendantId != ancestorId) {
            oddCycle.add(parent[descendantId]);
            descendantId = parent[descendantId];
        }
    }

    public boolean isBipartite() {
        return isBipartite;
    }

    public int colorOf(int vid) {
        assert isBipartite();
        return isWhite[vid] ? 0 : 1;
    }

    public List<Integer> anOddCycle() {
        assert !isBipartite();
        return new ArrayList<>(oddCycle);
    }
}
