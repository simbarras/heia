package s18;

import s03.PtyQueue;

import java.util.*;

public class Dijkstra {
    // ================================================
    static class Vertex implements Comparable<Vertex> {
        public final int vid;
        public final int pty;

        public Vertex(int vid, int pty) {
            this.vid = vid;
            this.pty = pty;
        }

        @Override
        public int compareTo(Vertex v) {
            return Integer.compare(this.pty, v.pty);
        }
    }
    // ================================================

    // POST : minDist[i] is the min distance from a to i
    //                      MAX_VALUE if i is not reachable, 0 for a
    //         parent[i] is the parent of i in the corresponding tree
    //                      -1 if i is not reachable, and for a
    public static void dijkstra(WeightedDiGraph g, int a,
                                int[] minDist, int[] parent) {
        boolean[] isVisited = new boolean[minDist.length];
        Arrays.fill(minDist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        minDist[a] = 0;
        PriorityQueue<Vertex> pq = new PriorityQueue<>();
        Vertex vt = new Vertex(a, minDist[a]);
        pq.add(vt);
        while (!pq.isEmpty()) {
            Vertex k = pq.remove();
            if (isVisited[k.vid]) continue;
            isVisited[k.vid] = true;
            for (int neighbor : g.neighboursFrom(k.vid)) {
                if (minDist[neighbor] >= (minDist[k.vid] + g.edgeWeight(k.vid, neighbor))) {
                    minDist[neighbor] = minDist[k.vid] + g.edgeWeight(k.vid, neighbor);
                    parent[neighbor] = k.vid;
                }
                Vertex vt2 = new Vertex(neighbor, minDist[neighbor]);
                pq.add(vt2);
            }
        }
    }

    /**
     * returns all vertices in vid's strongly connected component
     */
    static Set<Integer> strongComponentOf(WeightedDiGraph g, int vid) {
        Set<Integer> res = new HashSet<>();
        res.add(vid);
        WeightedDiGraph gInverted = new WeightedDiGraph(g.nbOfVertices());

        for (WeightedDiGraph.Edge e : g.allEdges()) {
            gInverted.putEdge(e.to, e.from, e.weight);
        }
        int[] parent = new int[g.nbOfVertices()];
        int[] invertedParent = new int[g.nbOfVertices()];
        dijkstra(g, vid, new int[g.nbOfVertices()], parent);
        dijkstra(gInverted, vid, new int[g.nbOfVertices()], invertedParent);

        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == -1) continue;
            if (invertedParent[i] == -1) continue;
            res.add(i);
        }
        return res;
    }

    // ------------------------------------------------------------
    public static void main(String[] args) {
        int nVertices = 6; // int nEdges = 12;
        final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;
        int[] srcs = {A, A, A, B, B, D, D, D, D, E, F, F};
        int[] dsts = {B, C, F, F, C, A, B, C, E, A, D, E};
        int[] costs = {12, 6, 14, 1, 7, 9, 3, 2, 4, 5, 10, 11};

        WeightedDiGraph g = new WeightedDiGraph(nVertices, srcs, dsts, costs);
        System.out.println("Input Graph: " + g);

        int n = g.nbOfVertices();
        int[] minCost = new int[n];
        int[] parent = new int[n];
        for (int a = 0; a < n; a++) {
            dijkstra(g, a, minCost, parent);
            System.out.println("\nMinimal distances from " + a);
            for (int i = 0; i < minCost.length; i++) {
                String s = "to " + i + ":";
                if (minCost[i] == Integer.MAX_VALUE) s += " unreachable";
                else s += " total " + minCost[i] + ", parent " + parent[i];
                System.out.println(s);
            }
        }
    }
}
