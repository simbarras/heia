package s04;

// =======================
public class BST<E extends Comparable<E>> {
    protected BTree<E> tree;
    protected int crtSize;

    public BST() {
        tree = new BTree<>();
        crtSize = 0;

    }

    public BST(E[] tab) {  // PRE sorted, no duplicate
        this.tree = optimalBST(tab, 0, tab.length - 1);
    }

    /**
     * returns where e is, or where it should be inserted as a leaf
     */
    protected BTreeItr<E> locate(E e) {
        BTreeItr<E> itr;
        if (tree == null) {
            tree = new BTree<>();
            itr = new BTreeItr<>(tree);
        } else {
            itr = tree.root();
        }
        while (true) {
            if (itr.isBottom()) break;
            if (e.compareTo(itr.consult()) < 0) {
                itr = itr.left();
            } else if (e.compareTo(itr.consult()) > 0) {
                itr = itr.right();
            } else {
                break;
            }
        }
        return itr;
    }

    public void add(E e) {
        BTreeItr<E> itr = locate(e);
        if (itr.isBottom()) {
            itr.insert(e);
            crtSize++;
        }
    }

    public void remove(E e) {
        BTreeItr<E> itr = locate(e);
        if (itr.isBottom()) return;
        while (itr.hasRight()) {
            itr.rotateLeft();
            itr = itr.left();
        }
        if (itr.isBottom()) {
            itr.cut();
        } else {
            itr.paste(itr.left().cut());
        }
        crtSize--;
    }

    public boolean contains(E e) {
        BTreeItr<E> ti = locate(e);
        return !ti.isBottom();
    }

    public int size() {
        return crtSize;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public E minElt() {
        if (tree == null) return null;
        BTreeItr<E> itr = new BTreeItr<>(tree);
        return itr.leftMost().consult();
    }

    public E maxElt() {
        if (tree == null) return null;
        BTreeItr<E> itr = new BTreeItr<>(tree);
        return itr.rightMost().consult();
    }

    @Override
    public String toString() {
        return "" + tree;
    }

    public String toReadableString() {
        String s = tree.toReadableString();
        s += "size==" + crtSize + "\n";
        return s;
    }

    // --------------------------------------------------
    // --- Non-public methods
    // --------------------------------------------------

    private BTree<E> optimalBST(E[] sorted, int left, int right) {
        BTree<E> r = new BTree<>();
        BTreeItr<E> ri = r.root();
        if (left > right) return r;
        int mid = (left + right) / 2;
        ri.insert(sorted[mid]);
        ri.left().paste(optimalBST(sorted, left, mid - 1));
        ri.right().paste(optimalBST(sorted, mid + 1, right));
        crtSize++;
        return r;
    }
}
