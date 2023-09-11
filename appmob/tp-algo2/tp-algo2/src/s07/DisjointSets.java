package s07;


// ------------------------------------------------------------
public class DisjointSets {
    private int[] elts;
    private int nbRoots;

    public DisjointSets(int nbOfElements) {
        elts = new int[nbOfElements];
        nbRoots = nbOfElements - 1;
        for (int i = 0; i < nbOfElements; i++) {
            elts[i] = -1;
        }
    }

    private int root(int index) {
        if (elts[index] < 0) return index;
        int r = root(elts[index]);
        elts[index] = r;
        return r;
    }

    private int sizeOfTree(int index) {
        return -elts[root(index)];
    }

    public boolean isInSame(int i, int j) {
        return root(i) == root(j);
    }

    public void union(int i, int j) {
        if (i == j) return;
        if (isInSame(i, j)) return;
        int rootI = root(i);
        int sizeI = sizeOfTree(i);
        int rootJ = root(j);
        int sizeJ = sizeOfTree(j);
        if (sizeI < sizeJ) {
            elts[rootI] = rootJ;
            elts[rootJ] = -(sizeI + sizeJ);
        } else {
            elts[rootJ] = rootI;
            elts[rootI] = -(sizeI + sizeJ);
        }
        nbRoots--;
    }

  public int nbOfElements() {  // as given in the constructor
    return 0;
  }
  
  public int minInSame(int i) {
    return 0;
  }

    public boolean isUnique() {
        //return nbOfElements() == sizeOfTree(0); // not O(1)
        return nbRoots == 0;
    }

    @Override
    public String toString() {
        String res = "";
        int n = nbOfElements();
        boolean[] isVisited = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (isVisited[i]) continue;
            res += "{" + i;
            isVisited[i] = true;
            for (int j = i + 1; j < n; j++) {
                if (isInSame(i, j) && !isVisited[j]) {
                    res += "," + j;
                    isVisited[j] = true;
                }
            }
            res += "}";
        }
        return res;
    }

    @Override
    public boolean equals(Object otherDisjSets) {
        for (int i = 0; i < nbOfElements(); i++) {
            int myRoot = root(i);
            int otherRoot = ((DisjointSets) otherDisjSets).root(i);
            if (!(isInSame(i, otherRoot) && ((DisjointSets) otherDisjSets).isInSame(myRoot, i))) return false;
        }
        return true;
    }
}
