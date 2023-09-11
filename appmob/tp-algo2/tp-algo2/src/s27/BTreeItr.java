package s27;
import s27.BTree.BTNode;

public class BTreeItr<E> {
  private BTree<E> whole;
  // ------------------------------------------------------------
  private BTNode<E> up   = null;  
  private BTNode<E> down = null; 
  private boolean isLeftArc = false; // not relevant at the root
  // ------------------------------------------------------------
  /** Position on root. */
  public BTreeItr(BTree<E> t) {
    whole=t;down = whole.root;
  }
  
  // ------------------------------------------------------------
  // ------ Accessors
  // ------------------------------------------------------------
  /** Terminal position: no node above. */
  public boolean isRoot() {
    return up==null;
  }
  /** Position on a node with a left child. */
  public boolean hasLeft() { 
    return down!=null && down.left !=null;
  }
  /** Position on a node with a right child. */
  public boolean hasRight() {
    return down!=null && down.right!=null;
  }
  /** Terminal position: no node below. */
  public boolean isBottom() {
    return down==null; 
  }
  /** Position on a node with no child. */
  public boolean isLeafNode() { 
    return !(isBottom()||hasLeft()||hasRight());
  }
  /** Position on a left child. Not relevant if isRoot(). */
  public boolean isLeftArc() {
    return isLeftArc;
  }
  /** PRE: !isBottom(). Returns the element stored at that position. */
  public E consult() {
    assert (!isBottom());
    assert (isConsistent());
    return down.elt;
  }

  // ------------------------------------------------------------
  // ------ Movements
  // ------------------------------------------------------------

  /** The tree containing that position */
  public BTree<E> whole() {
    return whole;
  }
  
  /** PRE: !isRoot(). Does not move this itr but creates a new one.*/
  public BTreeItr<E> up() { 
    assert (!isRoot());
    BTreeItr<E> r = new BTreeItr<>(whole);
    r.down=up; r.up=up.parent; r.isLeftArc = (r.up!=null && r.up.left==r.down);
    return r;
  }
  
  /** PRE: !isBottom(). Does not move this itr but creates a new one. */
  public BTreeItr<E> left()      {
    assert (!isBottom());
    BTreeItr<E> r = new BTreeItr<>(whole);
    r.up=down; r.down=down.left; r.isLeftArc = true;
    return r;
  }
  
  /** PRE: !isBottom(). Does not move this itr but creates a new one.  */
  public BTreeItr<E> right()     { 
    assert (!isBottom());
    BTreeItr<E> r = new BTreeItr<>(whole);
    r.up=down; r.down=down.right; r.isLeftArc = false;
    return r;
  }
  
  /** Goes down to the left as far as possible, from here. Always returns
   *  a bottom arc. Does not move this itr but creates a new one.  */
  public BTreeItr<E> leftMost()  { 
    BTreeItr<E> r = new BTreeItr<>(whole);
    r.up=up; r.down=down; r.isLeftArc = isLeftArc;
    while (!r.isBottom()) {
      r.up=r.down; r.down=r.down.left; r.isLeftArc = true;
    } 
    return r;
  }

  /** Goes down to the right as far as possible, from here. Always returns
   *  a bottom arc. Does not move this itr but creates a new one.  */
  public BTreeItr<E> rightMost() {
    BTreeItr<E> r = new BTreeItr<>(whole);
    r.up=up; r.down=down; r.isLeftArc = isLeftArc;
    while (!r.isBottom()) {
      r.up=r.down; r.down=r.down.right; r.isLeftArc = false;
    } 
    return r;    
  }

  /** Returns a new itr on the same position. */
  public BTreeItr<E> alias() {
    BTreeItr<E> t = new BTreeItr<>(whole);
    t.whole = whole; t.up=up; t.down=down; t.isLeftArc=isLeftArc;
    return t;
  }

  /** Each position belongs to exactly one tree. */
  public boolean isInside(BTree<E> t) { 
    return whole==t;
  }
  // ------------------------------------------------------------
  // ------ Modifiers
  // ------------------------------------------------------------

  /** PRE: !isBottom(). Replaces the element stored at that position. */
  public void  update(E elt) {
    assert (!isBottom());  assert (isConsistent());
    down.elt = elt; 
  }

  /** Replaces the subtree with a single node containing that element. */
  public void insert(E elt) {
    cut();
    BTNode<E> n = new BTNode<>(elt, null, null, up);
    if (isRoot())
      whole.root=n;
    else {
      if (isLeftArc) up.left = n;
      else           up.right= n;
    }
    down = n;
  }

  /** Replaces the subtree with that whole tree. 
   * PRE: !this.isInside(t). POST: t is now empty.*/
  public void paste(BTree<E> t) {
    if (isInside(t)) throw new IllegalArgumentException();
    cut();
    if (t.isEmpty()) return;
    BTNode<E> n = t.root; 
    n.parent=up;
    if (isRoot())
      whole.root=n;
    else {
      if (isLeftArc) up.left = n;
      else           up.right= n;
    }
    down = n;  
    t.root = null; 
  }

  /** Removes the subtree and returns it as a new tree. */
  public BTree<E> cut() {
    assert (isConsistent());
    BTree<E> t = new BTree<>();
    if (isBottom()) return t;
    t.root = down; t.root.parent = null;
    if (isRoot())
      whole.root=null;
    else {
      if (isLeftArc) up.left = null;
      else           up.right= null;
    }
    down = null;
    return t;
  }
  //------------------------------------------------------------
  //------ "rotation" methods
  //------------------------------------------------------------
  
  /** Moves the neighboring nodes according to a left rotation. 
   *  PRE: hasRight() */
  public void rotateLeft() {
    assert (hasRight());
    BTreeItr<E> x = cut().root();
    BTree<E> b = x.right().left().cut();
    BTreeItr<E> y = x.right().cut().root();
    x.right().paste(b);
    y.left().paste(x.whole());
    paste(y.whole());
  }

  /** Moves the neighboring nodes according to a right rotation.
  *  PRE: hasLeft() */
  public void rotateRight() {
    assert (hasLeft());
    BTreeItr<E> y = cut().root();
    BTree<E> b = y.left().right().cut();
    BTreeItr<E> x = y.left().cut().root();
    y.left().paste(b);
    x.right().paste(y.whole());
    paste(x.whole());
  }

  @Override public String toString() {
    return whole.toString();
  }
  
  /** A nice representation of the whole tree */
  public String toReadableString() {
    return whole.toReadableString();
  }
  //------------------------------------------------------------
  //------ Non-public methods
  //------------------------------------------------------------

  // isConsistent() : tracks tree inconsistency (due to recent 
  //       PRE-conditions violations for cut/paste/insert) :
  //       - local bidirectional chaining
  //       - recursively, upwards, till the root arc
  //       This slows a lot, but may help detect bugs 
  // ------------------------------------------------------------
  private boolean isConsistent() {
    BTNode<E> u=up, d=down;
    if (u!=null && d!=((isLeftArc)?(u.left):(u.right))) return false;
    while (u!=null) {
      if (d!=null && d.parent!=u) return false;
      if (d!=u.left && d!=u.right) return false;
      d=u; u=u.parent;
    }
    return (d==whole.root);
  }
  //------------------------------------------------------------

}
