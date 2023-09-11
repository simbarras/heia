package s02;
import s02.BTree.BTNode;

public class BTreeItr<T> {
    private final BTree<T> whole;

    private BTNode<T> up = null;
    private BTNode<T> down = null;
    private boolean isLeftArc = false; // not relevant at the root
    // ------------------------------------------------------------

    /**
     * Position on root.
     */
    public BTreeItr(BTree<T> t) {
        whole = t;
        down = whole.root;
    }
    // ------------------------------------------------------------
    // ------ Accessors
    // ------------------------------------------------------------

    /**
     * Terminal position: no node above.
     */
    public boolean isRoot() {
        return up == null;
    }

    /**
     * Position on a node with a left child.
     */
    public boolean hasLeft() {
        if (isBottom()) return false;
        return down.left != null;
    }

    /**
     * Position on a node with a right child.
     */
    public boolean hasRight() {
        if (isBottom()) return false;
        return down.right != null;
    }

    /**
     * Terminal position: no node below.
     */
    public boolean isBottom() {
        return down == null;
    }

    /**
     * Position on a node with no child.
     */
    public boolean isLeafNode() {
        return !(isBottom() || hasLeft() || hasRight());
    }

    /**
     * Position on a left child. Not relevant if isRoot().
     */
    public boolean isLeftArc() {
        return isLeftArc;
    }

    /**
     * PRE: !isBottom(). Returns the element stored at that position.
     */
    public T consult() {
        assert (!isBottom());
        assert (isConsistent());
        return down.elt;
    }

    // ------------------------------------------------------------
    // ------ Movements
    // ------------------------------------------------------------

    /**
     * The tree containing that position
     */
    public BTree<T> whole() {
        return whole;
    }

    /**
     * PRE: !isRoot(). Does not move this itr but creates a new one.
     */
    public BTreeItr<T> up() {
        assert (!isRoot());
        BTreeItr<T> nbt = new BTreeItr(this.whole);
        nbt.down = this.up;
        nbt.up = this.up.parent;
        nbt.isLeftArc = (nbt.up != null) && (nbt.down == nbt.up.left);
        return nbt;
    }

    /**
     * PRE: !isBottom(). Does not move this itr but creates a new one.
     */
    public BTreeItr<T> left() {
        assert (!isBottom());
        BTreeItr<T> r = new BTreeItr(whole);
        r.up = down;
        r.down = down.left;
        r.isLeftArc = true;
        return r;
    }

    /**
     * PRE: !isBottom(). Does not move this itr but creates a new one.
     */
    public BTreeItr<T> right() {
        assert (!isBottom());
        BTreeItr<T> r = new BTreeItr(whole);
        r.up = down;
        r.down = down.right;
        r.isLeftArc = false;
        return r;
    }

    /**
     * Goes down to the left as far as possible, from here. Always returns
     * a bottom arc. Does not move this itr but creates a new one.
     */
    public BTreeItr<T> leftMost() {
        BTreeItr<T> r = new BTreeItr(whole);
        r.up = up;
        r.down = down;
        r.isLeftArc = isLeftArc;
        while (!r.isBottom()) {
            r.up = r.down;
            r.down = r.down.left;
            r.isLeftArc = true;
        }
        return r;
    }

    /**
     * Goes down to the right as far as possible, from here. Always returns
     * a bottom arc. Does not move this itr but creates a new one.
     */
    public BTreeItr<T> rightMost() {
        BTreeItr<T> r = new BTreeItr(whole);
        r.up = up;
        r.down = down;
        r.isLeftArc = isLeftArc;
        while (!r.isBottom()) {
            r.up = r.down;
            r.down = r.down.right;
            r.isLeftArc = false;
        }
        return r;
    }

    /**
     * Returns a new iterator on the same position.
     */
    public BTreeItr<T> alias() {
        BTreeItr<T> t = new BTreeItr(whole);
        t.up = up;
        t.down = down;
        t.isLeftArc = isLeftArc;
        return t;
    }

    /**
     * Each position belongs to exactly one tree.
     */
    public boolean isInside(BTree<T> t) {
        return whole == t;
    }

    // ------------------------------------------------------------
    // ------ Modifiers
    // ------------------------------------------------------------

    /**
     * PRE: !isBottom(). Replaces the element stored at that position.
     */
    public void update(T elt) {
        assert (!isBottom());
        down.elt = elt;
    }

    /**
     * Replaces the subtree with a single node containing that element.
     */
    public void insert(T elt) {
        //create the node
        BTNode<T> nbtn = new BTNode(elt, null, null, up);
        //add reference to the tree
        if (up != null) {
            if (isLeftArc) up.left = nbtn;
            else up.right = nbtn;
        } else {
            whole.root = nbtn;
        }

        //add reference to the itr
        down = nbtn;
    }

    /**
     * Replaces the subtree with that whole tree.
     * PRE: !this.isInside(t). POST: t is now empty.
     */
    public void paste(BTree<T> t) {
        if (isInside(t)) throw new IllegalArgumentException();
        cut();
        if (t.isEmpty()) return;
        BTNode n = t.root;
        n.parent = up;
        if (isRoot())
            whole.root = n;
        else {
            if (isLeftArc) up.left = n;
            else up.right = n;
        }
        down = n;
        t.root = null;
    }

    /**
     * Removes the subtree and returns it as a new tree.
     */
    public BTree<T> cut() {
        BTree<T> nbt = new BTree();
        nbt.root = this.down;
        if (this.up != null) {
            if (isLeftArc) this.up.left = null;
            else this.up.right = null;
        } else {
            whole.root = null;
        }
        this.down = null;

        return nbt;
    }

    @Override
    public String toString() {
        return whole.toString();
    }

    /**
     * A nice representation of the whole tree
     */
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
    private boolean isConsistent() {
        BTNode<T> u = up, d = down;
        if (u != null && d != ((isLeftArc) ? (u.left) : (u.right))) return false;
        while (u != null) {
            if (d != null && d.parent != u) return false;
            if (d != u.left && d != u.right) return false;
            d = u;
            u = u.parent;
        }
        return (d == whole.root);
    }

}
