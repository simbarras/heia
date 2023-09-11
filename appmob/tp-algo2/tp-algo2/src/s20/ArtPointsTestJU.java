package s20;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;


public class ArtPointsTestJU {
  // ----------------------------------------------------------------------
  @Test
  public void testArtPointsOnExoGraph() {
    final int A=0,B=1,C=2,D=3,E=4,F=5,G=6,H=7,I=8,J=9,K=10,L=11,M=12;
    int nVertices=13;
    int[] srcs = {A,A,A,A, C, D,D, E,E, G,G,G, H, J,J,J, L};
    int[] dsts = {F,C,B,G, G, E,F, F,G, L,J,H, I, K,L,M, M};
    UGraph g = new UGraph(nVertices, srcs, dsts);
    Set<Integer> art = ArticulationPoints.articulationPoints(g, 2);
    assertEquals(art, new HashSet<>(Arrays.asList(A,G,H,J)) );
    // in Java 9:..., Set.of(A,G,H,J) ) 
  }
  // ----------------------------------------------------------------------
  @Test
  public void testArtPointsOnRandomGraphs() {
    int maxSize=30;
    double avgArt=0.0;
    for(int i=1; i<maxSize; i++) {
      System.out.print(i+" ");
      avgArt=testArtPoints(i);
    }
    System.out.println("\naverage nb of art points : "+avgArt);
  }
  // ----------------------------------------------------------------------
  public double testArtPoints(int n) {
    Random rnd=new Random();
    int totalNbArt=0;
    for(int z=0; z<100; z++) {
      int nEdges=rnd.nextInt(n*(n-1)/2 -(n-1) +1);
      UGraph g=rndAcyclicConnectedGraph(rnd, n);
      // now let's add "non-interesting" edges
      while(g.nbOfEdges()<nEdges) {
        int i=rnd.nextInt(n), j=rnd.nextInt(n);
        if (g.isEdge(i, j)||i==j) continue;
        g.putEdge(i, j);
      }
      //System.out.println(g);
      Set<Integer> res0=ArticulationPoints.articulationPoints(g, 0);
      int nbA=res0.size();
      totalNbArt+=nbA;
      //if (nbA>0) System.out.println(nbA);
      for (int j=0; j<n; j++) {
        //System.out.print("From "+j);
        Set<Integer> res=ArticulationPoints.articulationPoints(g, j);
        assertTrue(res0.equals(res)); // same results with another "root"...
      }
    }
    return totalNbArt/(double)n;
  }
  // ----------------------------------------------------------------------
  public static UGraph rndAcyclicConnectedGraph(Random r, int nVertices) {
    UGraph g= new UGraph(nVertices);
    while(g.nbOfEdges()<nVertices-1) {
      int from=r.nextInt(nVertices), to=r.nextInt(nVertices);
      if (from==to || g.isEdge(from, to)) continue;
      if (g.pathBetween(from, to)!=null) continue; // already connected
      //System.out.println(from+"??"+to);
      g.putEdge(from, to);
    }
    return g;
  }


}
