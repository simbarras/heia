package s10;

import java.util.Random;

//--------------------------------------------------
public class Treap<E extends Comparable<E>> {
    //============================================================
    static class TreapElt<E extends Comparable<E>> implements Comparable<TreapElt<E>> {
        static Random rnd = new Random();
        // -----------------------
        private final E elt;
        private int pty;

        // -----------------------
        public TreapElt(E e) {
            elt = e;
            pty = rnd.nextInt();
        }

        public int pty() {
            return pty;
        }

        public E elt() {
            return elt;
        }

        public int compareTo(TreapElt<E> o) {
            return elt.compareTo(o.elt);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (this.getClass() != o.getClass()) return false;
            if (elt == null) return false;
            return elt.equals(((TreapElt<?>) o).elt);
        }

        @Override
        public String toString() {
            return "" + elt + "#" + pty;
        }

        @Override
        public int hashCode() {
            return elt.hashCode();
        }
    }

    //============================================================
    private final BST<TreapElt<E>> bst;

    private int nbRotationsAdd;
    private int nbAdd;
    private int nbRotationsDel;
    private int nbDel;
    private int maxDepth;

    // --------------------------
    public Treap() {
        bst = new BST<>();
    }

    public void add(E e) {
        if (contains(e)) return;
        nbAdd++;
        TreapElt<E> elt = new TreapElt<>(e);
        bst.add(elt);
        BTreeItr<TreapElt<E>> itr = bst.locate(elt);
        percolateUp(itr);
    }

    public void remove(E e) {
        if (!contains(e)) return;
        nbDel++;
        BTreeItr<TreapElt<E>> itr = bst.locate(new TreapElt<>(e));
        siftDownAndCut(itr);
    }

    public boolean contains(E e) {
        if (bst == null) return false;
        if (size() == 0) return false;
        return bst.contains(new TreapElt<E>(e));
    }

    public int size() {
        return bst.size();
    }

    public E minElt() {
        return bst.minElt().elt;
    }

    public E maxElt() {
        return bst.maxElt().elt;
    }

    public String toString() {
        return bst.toString();
    }

    // --------------------------------------------------
    // --- Non-public methods
    // --------------------------------------------------ds
    private void siftDownAndCut(BTreeItr<TreapElt<E>> ti) {
        int depth = 1;
        while (!ti.isLeafNode()) {
            depth++;
            nbRotationsDel++;
            if (!ti.hasLeft()) {
                ti = siftDownLeft(ti);
            } else if (!ti.hasRight()) {
                ti = siftDownRight(ti);
            } else {
                if (isLess(ti.left(), ti.right())) {
                    ti = siftDownLeft(ti);
                } else {
                    ti = siftDownRight(ti);
                }
            }
        }
        ti.cut();
        bst.crtSize--;
        maxDepth = maxDepth < depth ? depth : maxDepth;

    }

    private BTreeItr<TreapElt<E>> siftDownRight(BTreeItr<TreapElt<E>> ti) {
        ti.rotateRight();
        return ti.right();
    }

    private BTreeItr<TreapElt<E>> siftDownLeft(BTreeItr<TreapElt<E>> ti) {
        ti.rotateLeft();
        return ti.left();
    }

    private void percolateUp(BTreeItr<TreapElt<E>> ti) {
        int depth = 1;
        while ((!ti.isRoot()) && isLess(ti, ti.up())) {
            depth++;
            nbRotationsAdd++;
            if (ti.isLeftArc()) {
                ti = ti.up();
                ti.rotateRight();
            } else {
                ti = ti.up();
                ti.rotateLeft();
            }
        }
        maxDepth = maxDepth < depth ? depth : maxDepth;
    }

    private boolean isLess(BTreeItr<TreapElt<E>> a, BTreeItr<TreapElt<E>> b) {
        TreapElt<E> ca = a.consult();
        TreapElt<E> cb = b.consult();
        return ca.pty() < cb.pty();
    }


    public int[] getStats() {
        return new int[]{nbAdd, nbRotationsAdd, nbDel, nbRotationsDel, size(), maxDepth};
    }

    public void resetStats() {
        nbAdd = 0;
        nbRotationsAdd = 0;
        nbDel = 0;
        nbRotationsDel = 0;
        maxDepth = 0;
    }
}
