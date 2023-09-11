package s21;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class FlowsTestJU {
    // ----------------------------------------------------------------------
    @Test
    public void testMaxFlow() {
        testIt(9, false);
    }

    @Test
    public void testMinCutAndThroughput() {
        testIt(9, true);
    }

    public void testIt(int maxSize, boolean testOtherFields) {
        for (int i = 1; i < maxSize; i++) {
            System.out.print(i + " ");
            testMaxFlow(i, testOtherFields);
        }
        System.out.println();
    }

    public void testMaxFlow(int n, boolean testOtherFields) {
        Random rnd = new Random();
        final int nbOfRepetitions = 100;
        for (int i = 0; i < nbOfRepetitions; i++) {
            int nEdges = rnd.nextInt(n * (n - 1) + 1);
            int minWeight = 1;
            int maxWeight = 100;
            WeightedDiGraph g = WeightedDiGraph.rndGraph(rnd, n, nEdges, minWeight, maxWeight);
            for (int s = 0; s < n; s++) {
                List<Integer> sReachable = reachable(g, s);
                for (int t = 0; t < n; t++) {
                    if (s == t) continue;
                    Flows.MaxFlowResult ford = Flows.maxFlow(g, s, t);
                    assertTrue(isFlow(g, ford.flow, s, t));
                    int max = throughput(ford.flow, s);
                    if (!sReachable.contains(t))
                        assertTrue(max == 0);
                    else
                        assertTrue(max == naiveMinCut(g, s, t, sReachable));
                    if (testOtherFields) {
                        assertEquals(max, ford.throughput);
                        assertEquals(max, cutCost(g, groupFromArray(ford.isInSourceGroup)));
                    }
                }
            }
        }
    }

    static List<Integer> groupFromArray(boolean[] t) {
        List<Integer> res = new LinkedList<>();
        for (int i = 0; i < t.length; i++)
            if (t[i]) res.add(i);
        return res;
    }

    static boolean isFlow(WeightedDiGraph g, WeightedDiGraph f, int s, int t) {
        final int n = g.nbOfVertices();
        for (int v = 0; v < n; v++) {
            int input = 0, output = 0;
            for (int w : f.neighboursFrom(v)) {
                int vw = f.edgeWeight(v, w);
                int cap = 0;
                if (g.isEdge(v, w)) cap = g.edgeWeight(v, w);
                assertTrue(vw <= cap);
                assertTrue(f.isEdge(w, v));
                assertTrue(vw == -f.edgeWeight(w, v));
                if (vw > 0) output += vw;
                else input += -vw;
            }
            if (v == s || v == t) continue;
            assertTrue(input == output);
        }
        return true;
    }

    static int naiveMinCut(WeightedDiGraph g, int s, int t, List<Integer> sReach0) {
        List<Integer> sReach = new LinkedList<>(sReach0);
        if (!sReach.contains(t)) return Integer.MAX_VALUE;
        List<Integer> allVert = new LinkedList<>();
        for (int i = 0; i < g.nbOfVertices(); i++) allVert.add(i);
        sReach.remove(Integer.valueOf(s));
        sReach.remove(Integer.valueOf(t));
        List<List<Integer>> possibleSubsets = selectAtMost(sReach, sReach.size());
        possibleSubsets.add(new LinkedList<>()); // for s alone
        int min = Integer.MAX_VALUE;
        for (List<Integer> crt : possibleSubsets) {
            crt.add(s);
            int cost = cutCost(g, crt);
            if (cost < min) min = cost;
        }
        return min;
    }

    static int cutCost(WeightedDiGraph g, List<Integer> s) {
        int res = 0;
        for (WeightedDiGraph.Edge e : g.allEdges())
            if (s.contains(e.from) && !s.contains(e.to))
                res += e.weight;
        return res;
    }

    static int throughput(WeightedDiGraph g, int s) {
        int res = 0;
        for (int v : g.neighboursFrom(s))
            res += g.edgeWeight(s, v);
        return res;
    }

    static List<Integer> reachable(WeightedDiGraph g, int from) {
        boolean[] isVisited = new boolean[g.nbOfVertices()];
        List<Integer> res = new LinkedList<>();
        Queue<Integer> q = new LinkedList<>();
        q.add(Integer.valueOf(from));
        isVisited[from] = true;
        while (!q.isEmpty()) {
            int c = q.remove();
            for (int v = 0; v < g.nbOfVertices(); v++)
                if (g.isEdge(c, v) && !isVisited[v]) {
                    q.add(Integer.valueOf(v));
                    isVisited[v] = true;
                    res.add(v);
                }
        }
        return res;
    }

    static List<List<Integer>> selectAtMost(List<Integer> t, int m) {
        List<List<Integer>> res = selectExactly(t, 0);
        for (int i = 1; i <= m; i++)
            res.addAll(selectExactly(t, i));
        return res;
    }

    static List<List<Integer>> selectExactly(List<Integer> t, int m) {
        List<List<Integer>> res = new LinkedList<>();
        if (m <= 0) return res;
        if (m > t.size()) return res;
        int[] sol = new int[m];
        int[] cand = new int[t.size()];
        for (int i = 0; i < cand.length; i++)
            cand[i] = t.get(i);
        select(cand, sol, m, 0, res);
        return res;
    }

    static void select(int[] t, int[] sol, int m, int from, List<List<Integer>> res) {
        if (m == 0) {
            List<Integer> crt = new LinkedList<>();
            for (int i : sol) crt.add(i);
            res.add(crt);
            return;
        }
        int nCandidates = t.length - from;
        if (m > nCandidates) return;
        select(t, sol, m, from + 1, res); // without t[from]
        sol[sol.length - m] = t[from];
        select(t, sol, m - 1, from + 1, res); // with    t[from]
    }
}
