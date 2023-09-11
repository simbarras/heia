package s18;

import java.util.*;

// ======================================================================
public class DiGraph {
  private final WeightedDiGraph graph;
  
  // --------------------
  public DiGraph(int nbOfVertices) {
    graph=new WeightedDiGraph(nbOfVertices);
  }

  public DiGraph(WeightedDiGraph dg) {
    graph=new WeightedDiGraph(dg.nbOfVertices(), dg.allEdges());
  }

  public DiGraph(int nbV, int[] srcs, int[] dsts) {
    graph=new WeightedDiGraph(nbV, srcs, dsts, new int[srcs.length]);
  }

  // ------------------------------------------------------------
  // vertex IDs in [0..nbOfVertices-1]
  public int nbOfVertices() {
    return graph.nbOfVertices();
  }

  public int nbOfEdges() {
    return graph.nbOfEdges();
  }

  public boolean isVertex(int vid) {
    return graph.isVertex(vid);
  }

  public boolean isEdge(int fromVid, int toVid) {
    return graph.isEdge(fromVid, toVid);
  }

  public int inDegree(int toVid) {
    return graph.inDegree(toVid);
  }

  public int outDegree(int fromVid) {
    return graph.outDegree(fromVid);
  }

  public SortedSet<Integer> neighboursFrom(int fromVid) {
    return graph.neighboursFrom(fromVid);
  }

  public SortedSet<Integer> neighboursTo(int toVid) {
    return graph.neighboursTo(toVid);
  }

  public List<Integer> pathBetween(int fromVid, int toVid) {
    return graph.pathBetween(fromVid, toVid);
  }

  public void putEdge(int fromVid, int toVid) {
    graph.putEdge(fromVid, toVid, 0);
  }

  public void removeEdge(int fromVid, int toVid) {
    graph.removeEdge(fromVid, toVid);
  }

  // --------------------
  public String toString() {
    return graph.toString(false); 
  }
  // --------------------
  public List<Edge> allEdges() {
    List<Edge> res=new ArrayList<>();
    for(WeightedDiGraph.Edge e:graph.allEdges())
      res.add(new Edge(e.from, e.to));
    return res;
  }
  //======================================================================
  public static class Edge {
    public final int from, to;
    public Edge(int from, int to) {
      this.from=from; this.to=to;
    }
  }
  //======================================================================

  // ----------------------------------------------------------------------
  // --------------------
  // Tiny demo...
  // --------------------

  public static void main(String [] args) {
    int nbV = 6; 
    final int A=0, B=1, C=2, D=3, E=4, F=5;
    int    [] srcs  = {A, A, A, B, B, D, D, D, D, E, F, F };
    int    [] dsts  = {B, C, F, F, C, A, B, C, E, A, D, E };

    DiGraph g = new DiGraph(nbV, srcs, dsts);
    System.out.println("Input Graph: " + g);
    
    int n=g.nbOfVertices();
    for(int u=0; u<n; u++)
      for(int v=0;v<n; v++) {
        if (u==v) continue;
        List<Integer> path = g.pathBetween(u, v);
        System.out.println("Path from "+u+" to "+v+": "+path);
      }
  }
}
