package s18;

import java.util.Random;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;


public class DijkstraTestJU {
  @Test
  public void testDijkstra() {
    testIt(true, 30);
  }

  @Test
  public void testStrongComponent() {
    testIt(false, 30);
  }
  
  public void testIt(boolean isDijkstra, int maxSize) {
    for(int i=1; i<maxSize; i++) {
      System.out.print(i+" ");
      testOnRndGraph(i, isDijkstra);
    }
    System.out.println();
  }

  public void testOnRndGraph(int n, boolean isDijkstra) {
    Random rnd=new Random();
    for(int i=0; i<100; i++) {
      int nEdges=rnd.nextInt(n*(n-1)+1);
      int minWeight=1; 
      int maxWeight=100;
      WeightedDiGraph g=WeightedDiGraph.rndGraph(rnd, n, nEdges, minWeight, maxWeight);
      int [][] shortest=allPairsShortestPath(g);
      for (int j=0; j<n; j++) {
        if (isDijkstra)
          checkDijkstra(g, shortest, j);
        else 
          checkStrongComponent(g, shortest, j);
      }
    }
  }

  private void checkStrongComponent(WeightedDiGraph g, int[][] shortest, int from) {
    int n=g.nbOfVertices();
    Set<Integer> sc = Dijkstra.strongComponentOf(g, from);
    int count=0;
    for (int k=0; k<n; k++) {
      if (k==from ||
          shortest[k][from] < Integer.MAX_VALUE &&
          shortest[from][k] < Integer.MAX_VALUE) {
        assertTrue(sc.contains(k));
        count++;
      }
    }
    assertEquals(sc.size(), count);
  }

  private void checkDijkstra(WeightedDiGraph g, int[][] shortest, int from) {
    int n=g.nbOfVertices();
    int [] minDist=new int[n];
    int [] parent=new int[n];
    Dijkstra.dijkstra(g, from, minDist, parent);
    //System.out.println(g);
    //System.out.println(Arrays.toString(minDist));
    //System.out.println(Arrays.toString(parent));
    for (int k=0; k<n; k++) {
      if (k==from) {
        assertEquals(0, minDist[k]);
        assertEquals(-1, parent[k]);
        continue;
      }
      assertEquals(shortest[from][k], minDist[k]);
      if (minDist[k]==Integer.MAX_VALUE) {
        assertEquals(-1, parent[k]);
      }else {
        assertTrue(g.isEdge(parent[k], k));
        assertEquals(minDist[parent[k]]+g.edgeWeight(parent[k],k), minDist[k]);
      }
    }
  }

  public static int [][] allPairsShortestPath(WeightedDiGraph g) {
    int n = g.nbOfVertices();
    boolean [][] reachable = new boolean[n][n];
    int [][] shortest=new int[n][n];
    int i, j, k;
    for(i=0; i<n; i++) 
      for(j=0; j<n; j++) {
        reachable[i][j] = g.isEdge(i,j); 
        if (g.isEdge(i,j)) {
          reachable[i][j] = true; 
          shortest[i][j]=g.edgeWeight(i,j);
        } else {
          shortest[i][j]=Integer.MAX_VALUE;
        }
      }
    for(k=0; k<n; k++) 
      for(i=0; i<n; i++) 
        for(j=0; j<n; j++)
          if (reachable[i][k] && reachable[k][j]) {
            reachable[i][j]= true;
            int c=shortest[i][k] + shortest[k][j];
            shortest[i][j]=Math.min(shortest[i][j], c);
          }
    return shortest;
  }

}
