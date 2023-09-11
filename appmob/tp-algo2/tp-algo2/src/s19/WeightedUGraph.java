package s19;

import java.util.*;

// ======================================================================
public class WeightedUGraph {
  private final WeightedDiGraph graph;

  // --------------------
  public WeightedUGraph(int nbOfVertices) {
    graph=new WeightedDiGraph(nbOfVertices);
  }

  public WeightedUGraph(int nbOfVertices, int[] srcs, int[] tgts, int[] wgts) {
    this(nbOfVertices);
    for(int i=0; i<srcs.length; i++) 
      putEdge(srcs[i], tgts[i], wgts[i]);
    // cannot use: new WeightedDiGraph(nV, srcs, tgts, wgts);
  }
  // ------------------------------------------------------------
  
  public int nbOfVertices() {  // vertex IDs in [0..nbOfVertices-1]
    return graph.nbOfVertices();
  }

  public int nbOfEdges() {
    return graph.nbOfEdges();
  }

  public boolean isVertex(int vid) {
    return graph.isVertex(vid);
  }

  public boolean isEdge(int from, int to) {
    if (from>to) {int a=from; from=to; to=a;}
    return graph.isEdge(from, to);
  }

  public int degree(int toVid) {
    return graph.inDegree(toVid)+graph.outDegree(toVid);
  }

  public SortedSet<Integer> neighboursOf(int vid) {
    SortedSet<Integer> res=graph.neighboursFrom(vid);
    res.addAll(graph.neighboursTo(vid));
    return res;
  }

  public void putEdge(int from, int to, int weight) {
    if (from>to) {int a=from; from=to; to=a;}
    graph.putEdge(from, to, weight);
  }

  public void removeEdge(int from, int to) {
    if (from>to) {int a=from; from=to; to=a;}
    graph.removeEdge(from, to);
  }

  // --------------------
  public int edgeWeight(int from, int to) {
    if (from>to) {int a=from; from=to; to=a;}
    return graph.edgeWeight(from, to);
  }

  // --------------------
  public String toString() {
    return graph.toString();
  }
  // --------------------
  public List<Integer> pathBetween(int from, int to) {
    int n=nbOfVertices();
    boolean[] isVisited=new boolean[n];
    java.util.Queue<LinkedList<Integer>> fifo=new LinkedList<>();
    LinkedList<Integer> list= new LinkedList<>();
    list.add(from);
    fifo.add(list);
    while(!fifo.isEmpty()) {
      list=fifo.remove();
      int crt=list.getLast();
      if (isVisited[crt]) continue;
      isVisited[crt]=true;
      if (crt==to) return list;
      for(int next:neighboursOf(crt)) {
        list= new LinkedList<>(list);
        list.addLast(next);
        fifo.add(list);
      }
    }
    return null;
  }
  // --------------------
  public List<Edge> allEdges() {
    List<Edge> res=new ArrayList<>();
    for(WeightedDiGraph.Edge e:graph.allEdges())
      res.add(new Edge(e.from, e.to, e.weight));
    return res;
  }
  //======================================================================
  public static class Edge {
    public final int from, to, weight;
    public Edge(int from, int to, int weight) {
      this.from=from; this.to=to; this.weight=weight;
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
    int    [] wgts  = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 7, 3 };

    WeightedUGraph g = new WeightedUGraph(nbV, srcs, dsts, wgts);
    System.out.println("Input Graph:\n " + g);
    
    for(int i=0; i<nbV; i++)
      System.out.println("neighborsOf "+i+" :"+g.neighboursOf(i));
    
    int n=g.nbOfVertices();
    for(int u=0; u<n; u++)
      for(int v=0;v<n; v++) {
        if (u==v) continue;
        List<Integer> path = g.pathBetween(u, v);
        System.out.println("Path from "+u+" to "+v+": "+path);
      }
  }

}
