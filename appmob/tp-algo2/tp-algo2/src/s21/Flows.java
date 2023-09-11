package s21;

import java.util.*;

public class Flows {
    //==================================================
    public static class MaxFlowResult {
        public WeightedDiGraph flow;
        public int throughput;
        public boolean[] isInSourceGroup; // gives the Min-Cut solution
    }

    //==================================================
    public static MaxFlowResult maxFlow(WeightedDiGraph capacity,
                                        int source, int sink) {
        WeightedDiGraph resid = initialResid(capacity);
        WeightedDiGraph flow = initialFlow(capacity);
        List<Integer> path = resid.pathBetween(source, sink);
        MaxFlowResult result = new MaxFlowResult();
        while (path != null) {
            int minStep = minStep(resid, path);
            if(minStep <= 0) {
                System.out.println("Minstep <= 0");
                System.out.println(resid);
                break;
            }
            updateFlow(flow, resid, path, minStep);
            suppressEmptyEdges(resid);
            path = resid.pathBetween(source, sink);
            result.throughput += minStep;
        }
        suppressEmptyEdges(flow);
        result.flow = flow;
        result.isInSourceGroup = new boolean[capacity.nbOfVertices()];
        for (int i = 0; i < result.isInSourceGroup.length; i++) {
            result.isInSourceGroup[i] = resid.pathBetween(source, i) != null;
        }
        return result;
    }

    public static void suppressEmptyEdges(WeightedDiGraph g) {
        for (WeightedDiGraph.Edge e : g.allEdges())
            if (e.weight == 0)
                g.removeEdge(e.from, e.to);
    }

    public static int minStep(WeightedDiGraph g, List<Integer> path) {
        int minStep = Integer.MAX_VALUE;
        for (int i = 1; i < path.size(); i++) {
            int from = path.get(i - 1);
            int to = path.get(i);
            minStep = Math.min(minStep, g.edgeWeight(from, to));
        }
        return minStep;
    }

    public static WeightedDiGraph initialResid(WeightedDiGraph cap) {
        WeightedDiGraph result = new WeightedDiGraph(cap.nbOfVertices());
        for (WeightedDiGraph.Edge e : cap.allEdges()) {
            result.putEdge(e.from, e.to, e.weight);
        }
        return result;
    }

    public static WeightedDiGraph initialFlow(WeightedDiGraph cap) {
        WeightedDiGraph result = new WeightedDiGraph(cap.nbOfVertices());
        for (WeightedDiGraph.Edge e : cap.allEdges()) {
            result.putEdge(e.from, e.to, 0);
            result.putEdge(e.to, e.from, 0);
        }
        return result;
    }

    public static void updateFlow(WeightedDiGraph flow,
                                  WeightedDiGraph resid,
                                  List<Integer> path,
                                  int benefit) {
        for (int i = 1; i < path.size(); i++) {
            int from = path.get(i - 1);
            int to = path.get(i);
            int wDesc = flow.edgeWeight(from, to);
            int wAsc = flow.edgeWeight(to, from);
            flow.removeEdge(from, to);
            flow.removeEdge(to, from);
            flow.putEdge(from, to, wDesc + benefit);
            flow.putEdge(to, from, wAsc - benefit);
            addCost(resid, from, to, benefit);
            addCost(resid, to, from, -benefit);
        }
    }

    private static void addCost(WeightedDiGraph g, int from, int to, int delta) {
        int w = 0;
        if (g.isEdge(from, to)) {
            w = g.edgeWeight(from, to);
            g.removeEdge(from, to);
        }
        g.putEdge(from, to, w - delta);
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    // ------------------------------------------------------------
    public static void main(String[] args) {
        int nVertices = 6;  //int nEdges = 12;
        final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5;
        int[] srcs = {A, A, A, B, B, D, D, D, D, E, F, F};
        int[] dsts = {B, C, F, F, C, A, B, C, E, A, D, E};
        int[] costs = {12, 6, 14, 1, 7, 9, 3, 2, 4, 5, 10, 11};

        WeightedDiGraph g = new WeightedDiGraph(nVertices, srcs, dsts, costs);
        System.out.println("                Input Graph: " + g);

        int source = 0, sink = 2;
        System.out.println("              Source vertex: " + source);
        System.out.println("                Sink vertex: " + sink);
        MaxFlowResult result = maxFlow(g, source, sink);
        System.out.println("               Maximal flow: " + result.flow);
        System.out.println("           Total throughput: " + result.throughput);
        System.out.println("Source-group in the min-cut: " + groupFromArray(result.isInSourceGroup));
    }

    static List<Integer> groupFromArray(boolean[] t) {
        List<Integer> res = new LinkedList<>();
        for (int i = 0; i < t.length; i++)
            if (t[i]) res.add(i);
        return res;
    }

}
