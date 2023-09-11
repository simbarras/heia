package s17;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class GraphUtilsTestJU {
  static Random rnd=new Random();

  @Test public void testDepthFirstForest() {
    testSeveralForests(12, true);
  }

  @Test public void testBestFirstForest() {
    testSeveralForests(12, false);
  }
  
  @Test public void testPathBetween() {
    int maxSize=20;
    System.out.println();
    for(int i=1; i<maxSize; i++) {
      System.out.print(i+" ");
      testPathBetween(i);
    }
  }
  
  private void testSeveralForests(int maxSize, boolean isDepth) {
    System.out.println();
    for(int i=1; i<maxSize; i++) {
      System.out.print(i+" ");
      testForest(i, isDepth);
    }
  }


  private void testForest(int n, boolean isDepth) {
    for(int i=0; i<50; i++) {
      int nEdges=rnd.nextInt(n*(n-1)+1);
      WeightedDiGraph wg=WeightedDiGraph.rndGraph(rnd, n, nEdges, 1,1);
      assignUniqueWeights(wg);
      //System.out.println(wg);
      int [][] shortest=allPairsShortestPath(wg);
      BTree<Integer> forest;
      for (int j=0; j<n; j++) {
        if (isDepth)
          forest=GraphUtils.depthFirstForest(new DiGraph(wg), j);
        else 
          forest=GraphUtils.bestFirstForest(wg, j);
        assertEquals(Integer.valueOf(j), forest.root().consult());
        //System.out.println(forest.toReadableString());
        checkForest(forest, wg, shortest, isDepth);
      }
    }
  }

  private static void checkForest(BTree<Integer> forest, WeightedDiGraph g, int[][] md, boolean isDepth) {
    int n=g.nbOfVertices();
    BTreeItr<Integer> ti=forest.root();
    boolean [] isV=new boolean[n];
    while(!ti.isBottom()) {
      int from=ti.consult();
      int minUnvisited=-1;
      for(int i=0; i<n; i++)
        if (!isV[i]) {
          minUnvisited=i; break;
        }
      if (!ti.isRoot())
        assertEquals(minUnvisited, from);
      if (isDepth)
        traverseTreeDepth(ti, isV, g);
      else
        traverseTreeBestFirst(ti, isV, g);
      ti=ti.right(); // next tree
      for(int i=0; i<n; i++) {
        if (i==from) continue;
        boolean isReachable= (md[from][i]!=Integer.MAX_VALUE);
        assertTrue(""+from+" "+i+" "+g, (!isReachable) || isV[i]); // isReachable => isV[i]
      }
    }
    for(boolean b:isV) assertTrue(b);  // ensures every vertex has been seen
  }
  
  private static void traverseTreeDepth(BTreeItr<Integer> ti, boolean[] isV, WeightedDiGraph g) {
    if (ti.isBottom()) return;
    int e=ti.consult();
    assertFalse(isV[e]);
    isV[e]=true;
    ti=ti.left();
    Iterator<Integer> neigh=g.neighboursFrom(e).iterator();
    int ee=e;
    while(!ti.isBottom()) {
      while(isV[ee]) ee=neigh.next();
      int f=ti.consult();
      assertEquals(f,ee);
      assertTrue(g.isEdge(e, f));
      traverseTreeDepth(ti, isV, g);
      ti=ti.right();
    }
  }
  
  private static void traverseTreeBestFirst(BTreeItr<Integer> ti, boolean[] isV, WeightedDiGraph g) {
    if (ti.isBottom()) return;
    int e=ti.consult();
    assertFalse(isV[e]);
    isV[e]=true;
    ti=ti.left();
    BTreeItr<Integer> itr=bestPos(isV, ti, g);
    while(!itr.isBottom()) {
      int f=itr.consult();
      int expMin=minWeightFromVisited(isV, g);
      int crtMin=weightFromParent(itr, g);
      assertEquals(expMin, crtMin);
      isV[f]=true;
      itr=bestPos(isV, ti, g);
    }
  }
  
  private static int minWeightFromVisited(boolean[] isV, WeightedDiGraph g) {
    int min=Integer.MAX_VALUE;
    int n=isV.length;
    for(int v=0; v<n; v++) {
      if (isV[v]) continue;
      for(int i=0; i<n; i++) {
        if (!isV[i]) continue;
        if (i==v) continue;
        if (! g.isEdge(i, v)) continue;
        min = Math.min(min, g.edgeWeight(i, v));
      }
    }
    return min;
  }
  
  private static BTreeItr<Integer> bestPos(boolean[] isV, BTreeItr<Integer> ti, WeightedDiGraph g) {
    if(ti.isBottom()) return ti;
    BTreeItr<Integer> l=ti, r=ti;
    int v=ti.consult();
    if(isV[v]) {
      l= bestPos(isV, ti.left(), g);
    }
    r=bestPos(isV, ti.right(), g);
    if (isBetter(l,r,g)) return l;
    return r;
  }
  
  private static boolean isBetter(BTreeItr<Integer> a, BTreeItr<Integer> b, WeightedDiGraph g) {
    if (b.isBottom()) return true;
    if (a.isBottom()) return false;
    return weightFromParent(a,g)<weightFromParent(b,g);
  }
  
  private static int weightFromParent(BTreeItr<Integer> ti, WeightedDiGraph g) {
    int to=ti.consult();
    while(!ti.isLeftArc()) ti=ti.up();
    ti=ti.up();
    int from=ti.consult();
    assertTrue(g.isEdge(from, to));
    return g.edgeWeight(from, to);
  }

  public void testPathBetween(int n) {
    for(int i=0; i<50; i++) {
      int nEdges=rnd.nextInt(n*(n-1)+1);
      WeightedDiGraph g=WeightedDiGraph.rndGraph(rnd, n, nEdges, 1,1);
      int [][] shortest=allPairsShortestPath(g);
      for (int j=0; j<n; j++) {
        checkPathBetween(g, shortest, j);
      }
    }
  }

  private void checkPathBetween(WeightedDiGraph wg, int[][] shortest, int from) {
    DiGraph g=new DiGraph(wg);
    int n=g.nbOfVertices();
    for (int k=0; k<n; k++) {
      if (k==from) continue;
      List<Integer> path=GraphUtils.pathBetween(g, from, k);
      int expectedLen=shortest[from][k];
      assertTrue((expectedLen==Integer.MAX_VALUE) == (path==null));
      if (path==null) continue;
      assertEquals(expectedLen, path.size()-1); // "-1" as both ends are in path
      for(int i=1; i<path.size(); i++)
        assert(g.isEdge(path.get(i-1), path.get(i)));
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

  private static void assignUniqueWeights(WeightedDiGraph g) {
    List<Integer> weights=new ArrayList<>();
    for(int i=0; i<g.nbOfEdges(); i++) weights.add(i);
    Collections.shuffle(weights);
    int i=0;
    for(WeightedDiGraph.Edge e:g.allEdges()) {
      g.putEdge(e.from, e.to, weights.get(i++));
    }
  }
  
}
