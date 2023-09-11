package s10;

public class BST<E extends Comparable<E>> {
  protected BTree<E> tree;
  protected int   crtSize;

  public BST() {
    tree = new BTree<>();
    crtSize = 0;
  }

  public BST(E[] tab) {  // PRE sorted, no duplicate
    tree = optimalBST(tab, 0, tab.length-1);
    crtSize = tab.length;
  }

  public void add(E e) {
    BTreeItr<E> ti = locate(e);
    if (!ti.isBottom()) return;
    ti.insert(e);
    crtSize++;
  }

  public void remove(E e) {
    BTreeItr<E> ti = locate(e);
    if(ti.isBottom()) return;
    crtSize--;
    // Alternative 1
    while(ti.hasRight()) {
      ti.rotateLeft();
      ti = ti.left();
    }
    BTree<E> l = ti.left().cut();
    ti.cut();
    ti.paste(l);
    // Alternative 2
    // remplacer avec le plus grand dans le ss-arbre gauche...
    //     locate(e);
    //     if (t.isLeafArc()) return;
    //     crtSize--;
    //     if (!t.hasRight())  {t.paste(t.left ().cut()); return;}
    //     if (!t.hasLeft())   {t.paste(t.right().cut()); return;}
    //     t.update(t.right().leftMost().up().consult());
    //     t.goToRight().goToLeftMost().goToUp();
    //     t.paste(t.right().cut());
  }

  public boolean contains(E e) {
    BTreeItr<E> ti = locate(e);
    return ! ti.isBottom();
  }

  public int size() {
    return crtSize;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public E minElt() {
    return (tree.root().leftMost().up().consult());
  }

  public E maxElt() {
    return (tree.root().rightMost().up().consult());
  }

  @Override public String toString() {
    return ""+tree;
  }
  
  public String toReadableString() {
    String s = tree.toReadableString();
    s += "size=="+crtSize+"\n";
    return s;
  }
  
  // --------------------------------------------------
  // --- Non-public methods
  // --------------------------------------------------
  
  /** returns where e is, or where it should be inserted as a leaf */
  protected  BTreeItr<E> locate(E e) {
    BTreeItr<E> ti = tree.root();
    while(!ti.isBottom()) {
      E c = ti.consult();
      if (e.compareTo(c)==0) break;
      if (e.compareTo(c)< 0) ti = ti.left();
      else                   ti = ti.right();
    }
    return ti;
  }

  private BTree<E> optimalBST(E[] sorted, int left, int right) {
    BTree<E> r = new BTree<>();
    BTreeItr<E> ri = r.root();
    if (left>right) return r;
    int mid = (left + right) /2;
    ri.insert(sorted[mid]);
    ri.left( ).paste(optimalBST(sorted, left, mid-1));
    ri.right().paste(optimalBST(sorted, mid+1, right));
    return r;
  }
}
