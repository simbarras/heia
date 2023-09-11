package s19;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class BipartiteAnalysisTestJU {
    private Random rnd = new Random();
    private int nbOfBipartiteGraphs = 0;
    private int nbOfNonBipartiteGraphs = 0;

    @Test
    public void testBipartiteOnRandomGraphs() {
        int maxSize = 30;
        for (int i = 1; i < maxSize; i++) {
            System.out.print(i + " ");
            testBipartite(i);
        }
        System.out.println("\n    Bipartite: " + nbOfBipartiteGraphs);
        System.out.println("non Bipartite: " + nbOfNonBipartiteGraphs);
    }

    // ----------------------------------------------------------------------
    public void testBipartite(int nVertices) {
        for (int z = 0; z < 100; z++) {
            int nEdges = rnd.nextInt(nVertices * (nVertices - 1) / 2 + 1);
            UGraph g = rndUGraph(rnd, nVertices, nEdges);
            BipartiteAnalysis ba = new BipartiteAnalysis(g);
            verify(g, ba);
        }
    }

    // ----------------------------------------------------------------------
    private void verify(UGraph g, BipartiteAnalysis ba) {
        int n = g.nbOfVertices();
        if (ba.isBipartite()) {
            //System.out.println("Check if really bipartite");
            nbOfBipartiteGraphs++;
            for (int i = 0; i < n; i++) {
                for (int j : g.neighboursOf(i)) {
                    //System.out.println("Check color");
                    System.out.println(g);
                    assertTrue(ba.colorOf(i) != ba.colorOf(j));
                }
            }
        } else {
            //System.out.println("Check if isn't bipartite");
            nbOfNonBipartiteGraphs++;
            List<Integer> cycle = ba.anOddCycle();
            assertTrue(cycle.size() % 2 == 1);
            int first = cycle.get(0);
            int last = cycle.get(cycle.size() - 1);
            assertTrue(g.isEdge(first, last));
            for (int i = 1; i < cycle.size(); i++)
                assertTrue(g.isEdge(cycle.get(i - 1), cycle.get(i)));
        }
    }

    // ----------------------------------------------------------------------
    public static UGraph rndUGraph(Random rnd, int nVertices, int nEdges) {
        UGraph g = new UGraph(nVertices);
        while (g.nbOfEdges() < nEdges) {
            int u = rnd.nextInt(nVertices), v = rnd.nextInt(nVertices);
            if (u == v) continue;
            g.putEdge(u, v);
        }
        return g;
    }
}
