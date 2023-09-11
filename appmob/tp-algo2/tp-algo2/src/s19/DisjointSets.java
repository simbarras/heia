package s19;
import java.util.Arrays;
// ------------------------------------------------------------
public class DisjointSets {
  private int[] parent ;
  private int   nbOfSets;
  // ------------------------------------------------------------
  public DisjointSets(int n) {
    nbOfSets = n;
    parent = new int[n];
    Arrays.fill(parent, -1);
  }

  public boolean isUnique() { 
    return nbOfSets==1;
  }

  public boolean isInSame(int i, int j) {
    return root(i)==root(j);
  }

  public void union(int i, int j) {
    mergeRoots(root(i), root(j));
  }

  @Override public String toString() {
    String res = "";
    int n = parent.length;
    boolean[] isVisited = new boolean[n];
    for (int i=0; i<n; i++) {
      if (isVisited[i]) continue;
      res += "{"+i;
      isVisited[i] = true;
      for (int j=0; j<n; j++) {
        if (isInSame(i,j) && !isVisited[j]) {
          res += ","+j;
          isVisited[j] = true;
        }
      }
      res += "}";
    }
    return res;
  }

  @Override public boolean equals(Object o) {
    if (o==null || this.getClass() != o.getClass()) return false;
    DisjointSets dj = (DisjointSets)o;
    if (parent.length != dj.parent.length) return false;
    for(int i=0; i<parent.length; i++)
      for(int j=0; j<parent.length; j++)
        if(isInSame(i,j) != dj.isInSame(i,j))
          return false;
    return true;
  }
  // ------------------------------------------------------------
  private void mergeRoots(int r1, int r2) {
    if (r1 == r2) return;
    nbOfSets--;
    if (parent[r2] < parent[r1]) {
      int aux=r1; r1=r2; r2=aux; // swap them
    }                            // now size(r1) >= size(r2)
    parent[r1] += parent[r2];    // update the size of r1
    parent[r2]  = r1;            // attach r2 to r1
  }

  private int root(int elt) {
    if (parent[elt]<0) return elt;
    parent[elt] = root(parent[elt]);
    return parent[elt];
  }
}
