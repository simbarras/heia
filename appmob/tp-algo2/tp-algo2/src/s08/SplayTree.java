package s08;

public class SplayTree<K extends Comparable<K>> {
    BTree<K> tree = new BTree<>();
    int crtSize = 0;

    // --------------------------------------------------
    public SplayTree() {
        super();
    }

    // --------------------------------------------------
    public void add(K e) {
        if (contains(e)) return;  // This "splays" the tree!
        BTreeItr<K> ti = tree.root();

        if (isEmpty()) {
            ti.insert(e);
        } else {
            BTree<K> oldRight;
            BTree<K> oldLeft;
            if (ti.consult().compareTo(e) < 0) {
                oldRight = ti.hasRight() ? ti.right().cut() : null;
                oldLeft = ti.cut();
            } else {
                oldLeft = ti.hasLeft() ? ti.left().cut() : null;
                oldRight = ti.cut();
            }
            ti.insert(e);
            if (oldRight != null) ti.right().paste(oldRight);
            if (oldLeft != null) ti.left().paste(oldLeft);
        }
        crtSize++;

    }

    // --------------------------------------------------
    public void remove(K e) {
        if (!contains(e)) return; // This "splays" the tree!
        crtSize--;
        if (tree.root().hasLeft() && tree.root().hasRight()) {
            BTree<K> oldRight = tree.root().right().cut();
            tree = tree.root().left().cut();
            BTreeItr<K> maxInLeft = tree.root().rightMost().up();
            BTreeItr<K> ti = splayToRoot(maxInLeft); // now tree has no right subtree!
            ti.right().paste(oldRight);
        } else {  // the tree has only one child
            if (tree.root().hasLeft()) tree = tree.root().left().cut();
            else tree = tree.root().right().cut();
        }
    }

    // --------------------------------------------------
    public boolean contains(K e) {
        if (isEmpty()) return false;
        BTreeItr<K> ti = locate(e);
        boolean absent = ti.isBottom();
        if (absent) ti = ti.up();
        ti = splayToRoot(ti);
        return !absent;
    }

    // --------------------------------------------------
    protected BTreeItr<K> locate(K e) {
        BTreeItr<K> ti = tree.root();
        while (!ti.isBottom()) {
            K c = ti.consult();
            if (e.compareTo(c) == 0) break;
            if (e.compareTo(c) < 0) ti = ti.left();
            else ti = ti.right();
        }
        return ti;
    }

    // --------------------------------------------------
    public int size() {
        return crtSize;
    }

    // --------------------------------------------------
    public boolean isEmpty() {
        return size() == 0;
    }

    // --------------------------------------------------
    public K minElt() {
        BTreeItr<K> ti = tree.root();
        while (!ti.isBottom()) ti = ti.left();
        return ti.consult();
    }

    // --------------------------------------------------
    public K maxElt() {
        BTreeItr<K> ti = tree.root();
        while (!ti.isBottom()) ti = ti.right();
        return ti.consult();
    }

    // --------------------------------------------------
    public String toString() {
        return "" + tree.toReadableString() + "SIZE:" + size();
    }

    // --------------------------------------------------
    // --- Non-public methods
    // --------------------------------------------------
    // PRE:     ! ti.isBottom()
    // RETURNS: root position
    // WARNING: ti is no more valid afterwards
    private BTreeItr<K> splayToRoot(BTreeItr<K> ti) {
        while (!ti.isRoot()) {
            if (ti.up().isRoot()) return applyZig(ti);
            ti = (ti.isLeftArc() == ti.up().isLeftArc()) ? applyZigZig(ti) : applyZigZag(ti);
        }
        return ti;
    }

    // --------------------------------------------------
    // PRE / RETURNS : Zig situation (see schemas)
    // WARNING: ti is no more valid afterwards
    private BTreeItr<K> applyZig(BTreeItr<K> ti) {
        boolean leftZig = ti.isLeftArc();
        ti = ti.up();
        if (leftZig) ti.rotateRight();
        else ti.rotateLeft();
        return ti;
    }

    // --------------------------------------------------
    // PRE / RETURNS : ZigZig situation (see schemas)
    // WARNING: ti is no more valid
    private BTreeItr<K> applyZigZig(BTreeItr<K> ti) {
        BTreeItr<K> tiParent = ti.up();
        applyZig(tiParent);
        return applyZig(ti);
    }

    // --------------------------------------------------
    // PRE / RETURNS : ZigZag situation (see schemas)
    // WARNING: ti is no more valid
    private BTreeItr<K> applyZigZag(BTreeItr<K> ti) {
        BTreeItr<K> tiParent = ti.up();
        applyZig(ti);
        return applyZig(tiParent);
    }
    // --------------------------------------------------
}
