package s19;

import org.apache.commons.math3.optim.nonlinear.vector.Weight;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Kruskal {

    /* RETURNS: the total weight of the Minimum Spanning Tree
     * PRE:     res has the same vertices as g, but no edge
     * POST:    res is the resulting sub graph
     */
    public static int kruskal(WeightedUGraph g, WeightedUGraph res) {
        // Hint: find the appropriate PriorityQueue constructor, and
        //       use a lambda expression (together with Integer.compare()
        //                                or Comparator.comparingInt()   ).
        PriorityQueue<WeightedUGraph.Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        pq.addAll(g.allEdges());
        DisjointSets ds = new DisjointSets(g.nbOfVertices());

        int result = 0;

        while (!ds.isUnique()) {
            WeightedUGraph.Edge e = pq.poll();
            if (ds.isInSame(e.from, e.to)) continue;
            res.putEdge(e.from, e.to, e.weight);
            ds.union(e.from, e.to);
            result += e.weight;
        }
        return result;
    }

    // ------------------------------------------------------------
    public static void main(String[] args) {
        int nVertices = 6; // int nEdges = 12;
        final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;
        int[] srcs = {A, A, A, B, B, D, D, D, D, E, F, F};
        int[] dsts = {B, C, F, F, C, A, B, C, E, A, D, E};
        int[] weights = {12, 6, 14, 1, 7, 9, 3, 2, 4, 11, 10, 5};

        WeightedUGraph g = new WeightedUGraph(nVertices, srcs, dsts, weights);
        System.out.println("Input Graph: " + g);

        WeightedUGraph res = new WeightedUGraph(g.nbOfVertices());
        int cost = kruskal(g, res);
        System.out.println("\nMinimal Spanning Tree of cost: " + cost);
        for (WeightedUGraph.Edge e : g.allEdges())
            System.out.println(e.from + " to " + e.to + " of cost " + e.weight);
    }
}
