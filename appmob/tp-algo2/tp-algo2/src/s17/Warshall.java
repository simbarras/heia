package s17;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class Warshall {
  @FunctionalInterface interface TransitiveClosure {
    boolean[][] apply(DiGraph g);
  }
  // ----------------------------------------------------------------------
  public static boolean[][] transitiveClosure(DiGraph g) {
    int n = g.nbOfVertices();
    boolean[][] res = new boolean[n][n];
    int i, j, k;
    for(i=0; i<n; i++) 
      for(j=0; j<n; j++)
        res[i][j] = g.isEdge(i,j); 
    for(k=0; k<n; k++) 
      for(i=0; i<n; i++) 
        for(j=0; j<n; j++)
          res[i][j] |= res[i][k] && res[k][j];
    return res;
  }
  // ----------------------------------------------------------------------
  public static boolean[][] transitiveClosureOpt(DiGraph g) {
    // todo in S18... 
    return transitiveClosure(g);
  }
  // ----------------------------------------------------------------------
  @Test
  public void testWarshall() {
    int maxSize=30;
    for(int i=1; i<maxSize; i++) {
      testWarshall(i);
    }
  }
  @Test
  public void testPerfWarshall() {
    int n = 300;
    testPerfWarshall(n, Warshall::transitiveClosure);
    testPerfWarshall(n, Warshall::transitiveClosureOpt);
    n = 2*n;
    testPerfWarshall(n, Warshall::transitiveClosure);
    testPerfWarshall(n, Warshall::transitiveClosureOpt);
  }
  // ----------------------------------------------------------------------
  private void testWarshall(int n) {
    Random rnd=new Random(76);
    for(int i=0; i<100; i++) {
      int nEdges=rnd.nextInt(n*(n-1)+1);
      WeightedDiGraph wg=WeightedDiGraph.rndGraph(rnd, n, nEdges, 0,1);
      DiGraph g=new DiGraph(wg);
      boolean [][] res0=transitiveClosure(g);
      boolean [][] res1=transitiveClosureOpt(g);      
      for (int j=0; j<n; j++) {
        assertTrue(Arrays.equals(res0[j], res1[j]));
      }
    }
  }
  // ----------------------------------------------------------------------
  protected static boolean[][] res;
  private static long warshallDuration(DiGraph g, TransitiveClosure tc) {
    long dt0=0, t0;
    t0=System.nanoTime();
      res=tc.apply(g);
    dt0 +=System.nanoTime()-t0;
    if(res[0][0]) System.out.flush();  // so that the result is used...
    return dt0;
  }
  // ----------------------------------------------------------------------
  private void testPerfWarshall(int n, TransitiveClosure tc) {
    Random rnd=new Random(76);
    int nSamples=20;
    long duration=0L;
    for(int t=0; t<nSamples; t++) {
      int nEdges=rnd.nextInt(n*(n-1)+1);
      WeightedDiGraph wg=WeightedDiGraph.rndGraph(rnd, n, nEdges, 0,1);
      DiGraph g=new DiGraph(wg);
      duration += warshallDuration(g, tc);
    }
    String s=""+n+" vertices, ";
    System.out.println(s + "[us]: " + (duration/nSamples/1000));
  }
}
