package s19;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import org.junit.Test;

import static org.junit.Assert.*;


public class KruskalTestJU {
  @Test public void testKruskal() {
    int maxSize=30;
    for(int i=1; i<maxSize; i++) {
      System.out.print(i+" ");
      testKruskal(i);
    }
    System.out.println();
  }
  // ----------------------------------------------------------------------
  public void testKruskal(int n) {
    Random rnd=new Random();
    for(int z=0; z<100; z++) {
      int minWeight=1; 
      int maxWeight=20;
      WeightedUGraph g=rndConnectedGraph(rnd, n, minWeight, maxWeight);
      int mstWeight=prim(g,new int[n]);
      //System.out.println(g);
      for (int j=0; j<n; j++) {
        //System.out.print("From "+j);
        WeightedUGraph mst=new WeightedUGraph(n);
        int res=Kruskal.kruskal(g, mst);
        //System.out.println(Arrays.toString(parent));
        assertTrue(res==mstWeight);
        assertTrue(mst.nbOfEdges()==n-1);
      }
    }
  }
  // ----------------------------------------------------------------------
  public static WeightedUGraph rndConnectedGraph(Random r, int nVertices, 
                               int minWeight, int maxWeight) {
    WeightedUGraph g=rndAcyclicConnectedGraph(r, nVertices, minWeight, maxWeight);
    int n=nVertices;
    int nEdgesMax=n*(n-1)/2;
    int nEdges=g.nbOfEdges() + r.nextInt(1+nEdgesMax-g.nbOfEdges());
    while(g.nbOfEdges()<nEdges) {
      int i=r.nextInt(nVertices), j=r.nextInt(nVertices);
      if (g.isEdge(i, j)||i==j) continue;
      int w=r.nextInt(1+maxWeight-minWeight)+minWeight;
      g.putEdge(i, j, w);
    }
    return g;
  }
  
  public static WeightedUGraph rndAcyclicConnectedGraph(Random r, int nVertices, 
                               int minWeight, int maxWeight) {
    WeightedUGraph g= new WeightedUGraph(nVertices);
    while(g.nbOfEdges()<nVertices-1) {
      int from=r.nextInt(nVertices), to=r.nextInt(nVertices);
      if (from==to || g.isEdge(from, to)) continue;
      if (g.pathBetween(from, to)!=null) continue; // already connected
      int w=r.nextInt(1+maxWeight-minWeight)+minWeight;
      //System.out.println(from+"??"+to);
      g.putEdge(from, to, w);
    }
    return g;
  }
  // ------------------------------------------------------------
  private static int prim(WeightedUGraph g, int [] parent) {
    return prim(g, 0, parent);
  }

  private static int prim(WeightedUGraph g, int a, int [] parent) {
    int n = g.nbOfVertices();
    int totalCost = 0;
    boolean [] isVisited = new boolean[n];
    int     [] accessWeight = new int [n];
    Arrays.fill(accessWeight, Integer.MAX_VALUE);
    for (int i=0; i<n;i++) parent[i]=-1;
    PriorityQueue<Vertex> pq = new PriorityQueue<>();
    accessWeight[a]= 0; 
    pq.add(new Vertex(a,0));
    while (!pq.isEmpty()) {
      int k=pq.remove().vid;
      if (isVisited[k]) continue;
      isVisited[k]= true;
      totalCost += accessWeight[k];
      for (int j:g.neighboursOf(k)) {
        if (isVisited[j]) continue;
        int u = g.edgeWeight(k, j);
        if (u < accessWeight[j]) {
          parent[j]=k;
          accessWeight[j] = u;
          pq.add(new Vertex(j, u));
        }
      }
    }
    return totalCost;
  }
  // ============================================================
  private static class Vertex implements Comparable<Vertex> {
    public int vid, pty;
    public Vertex(int vid, int pty) {this.vid=vid; this.pty=pty;}
    public int compareTo(Vertex v) {
      return Integer.compare(pty, v.pty);
    }
  }
}
