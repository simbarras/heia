package s18;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
// ======================================================================
public class WeightedDiGraph {
  //------------------------------------------------------------
  private final int                size;
  private final List<Map<Integer,Integer>> outNeighbors;
  private final List<Map<Integer,Integer>> inNeighbors;
  private int  nbOfEdges;
  // --------------------
  public WeightedDiGraph(int nbOfVertices){
    size=nbOfVertices;
    outNeighbors=new ArrayList<>();
    inNeighbors=new ArrayList<>();
    for (int i=0; i < size; i++) {
      outNeighbors.add(new HashMap<>());
      inNeighbors.add (new HashMap<>());
    }
  }
  // --------------------
  public WeightedDiGraph(WeightedDiGraph wdg) {
    this(wdg.nbOfVertices(), wdg.allEdges());    
  }
  // -------------------- 
  public WeightedDiGraph(int nbOfVertices, List<Edge> edges){
    this(nbOfVertices);
    for(Edge e:edges) {
      putEdge(e.from, e.to, e.weight);
    }
  }
  // ------------------------------------------------------------
  public WeightedDiGraph(int nbOfVertices, int[] srcs, int[] tgts, int[] wgts){
    this(nbOfVertices);
    if (srcs.length!=tgts.length || srcs.length!=wgts.length)
      throw new RuntimeException("bad parallel arrays");
    for(int i=0; i<srcs.length; i++)
      putEdge(srcs[i], tgts[i], wgts[i]);
  }
  // ------------------------------------------------------------
  public int nbOfVertices() { // vertex IDs in [0..nbOfVertices-1]
    return (int) size;
  }
  // ------------------------------------------------------------
  public int nbOfEdges() {
    return nbOfEdges;
  }
  // --------------------
  public boolean isVertex(int vid) {
    return (vid >= 0) && (vid < size);
  }
  // --------------------
  public void putEdge(int fromVid, int toVid, int weight) {
    if (!(isVertex(fromVid) && isVertex(toVid)) || fromVid == toVid)
      throw new RuntimeException("bad vertex id: "+fromVid+"-"+toVid);
    if(!isEdge(fromVid,toVid)) nbOfEdges++;
    outNeighbors.get(fromVid).put(toVid, weight);
    inNeighbors.get(toVid).put(fromVid, weight);
  }
  // --------------------
  public void removeEdge(int fromVid, int toVid) {
    if (!(isVertex(fromVid) && isVertex(toVid)) || fromVid == toVid)
      throw new RuntimeException("bad vertex id");
    if(isEdge(fromVid,toVid)) nbOfEdges--;
    outNeighbors.get(fromVid).remove(toVid);
    inNeighbors.get(toVid).remove(fromVid);
  }
  // --------------------
  public boolean isEdge(int fromVid, int toVid) {
    return outNeighbors.get(fromVid).containsKey(toVid);
  }
  // --------------------
  public int edgeWeight(int from, int to) {
    if (!isEdge(from, to))
      throw new IllegalArgumentException("edge " + from + "-" + to
          + " doesn't exist");
    return outNeighbors.get(from).get(to);
  }
  // --------------------
  public int inDegree(int toVid) {
    return inNeighbors.get(toVid).size();
  }
  // --------------------
  public int outDegree(int fromVid) {
    return outNeighbors.get(fromVid).size();
  }
  // --------------------
  public SortedSet<Integer> neighboursFrom(int fromVid) {
    return new TreeSet<>(outNeighbors.get(fromVid).keySet());
  }
  // --------------------
  public SortedSet<Integer> neighboursTo(int toVid) {
    return new TreeSet<>(inNeighbors.get(toVid).keySet());
  }
  //======================================================================
  public static class Edge {
    public final int from, to, weight;
    public Edge(int from, int to, int weight) {
      this.from=from; this.to=to; this.weight=weight;
    }
  }
  //======================================================================
  public List<Edge> allEdges() {
    List<Edge> res= new ArrayList<>();
    for(int i=0; i < size; i++) {
      Map<Integer,Integer> neighbors=outNeighbors.get(i);
      for(Map.Entry<Integer,Integer> e:neighbors.entrySet()) {
        res.add(new Edge(i, e.getKey(), e.getValue()));
      }
    }
    return res;
  }
  // --------------------
  public List<Integer> pathBetween(int from, int to) {
    int n=nbOfVertices();
    boolean[] isVisited=new boolean[n];
    Queue<LinkedList<Integer>> fifo = new LinkedList<>();
    LinkedList<Integer> list= new LinkedList<>();
    list.add(from);
    fifo.add(list);
    while(!fifo.isEmpty()) {
      list=fifo.remove();
      int crt=list.getLast();
      if (isVisited[crt]) continue;
      isVisited[crt]=true;
      if (crt==to) return list;
      for(int next:neighboursFrom(crt)) {
        LinkedList<Integer> augmList= new LinkedList<>(list);
        augmList.addLast(next);
        fifo.add(augmList);
      }
    }
    return null;
  }
  // --------------------
  public String toString() {
    return toString(true);
  }

  // ------------------------------------------------------------
  // --------------------
  // static methods
  // --------------------
  
  public static WeightedDiGraph rndGraph(Random r, int nVertices, int nEdges,
              int minWeight, int maxWeight) {
    assert nEdges <= nVertices*(nVertices-1);
    WeightedDiGraph g= new WeightedDiGraph(nVertices);
    while(nEdges>0) {
      int from=r.nextInt(nVertices), to=r.nextInt(nVertices);
      if (from==to || g.isEdge(from, to)) continue;
      nEdges--;
      int w=r.nextInt(1+maxWeight-minWeight)+minWeight;
      //System.out.println(from+"??"+to);
      g.putEdge(from, to, w);
    }
    return g;
  }
  
  // ------------------------------------------------------------
  // --------------------
  // non public methods
  // --------------------
  protected String toString(boolean withWeights) {
    String res="Vertices: 0.."+  (nbOfVertices()-1)+" Edges: ";
    List<Edge> edges=allEdges();
    for (Edge e:edges) {
      int a=e.from, b=e.to;
      res+="("+a+"-"+b;
      if(withWeights) res+="/"+edgeWeight(a,b);
      res+=")";
    }
    return res;
  }

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

    WeightedDiGraph g = new WeightedDiGraph(nbV, srcs, dsts, wgts);
    System.out.println("Input Graph: " + g);
    
    int n=g.nbOfVertices();
    for(int u=0; u<n; u++)
      for(int v=0;v<n; v++) {
        if (u==v) continue;
        List<Integer> path = g.pathBetween(u, v);
        System.out.println("Path from "+u+" to "+v+": "+path);
      }
    
    System.out.println("Random");
    System.out.println(rndGraph(new Random(), 5, 10, 0, 9));
  }
}
