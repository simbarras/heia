package s03;

import java.sql.SQLOutput;

public class PtyQueue<E, P extends Comparable<P>> {
    private final Heap<HeapElt> heap;
    private long count;

    public PtyQueue() {
        heap = new Heap<>();
        count = Long.MIN_VALUE;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Adds an element elt with priority pty
     */
    public void enqueue(E elt, P pty) {
        heap.add(new HeapElt(pty, elt, count++));
    }

    /**
     * Returns the element with highest priority. PRE: !isEmpty()
     */
    public E consult() {
        return heap.min().elt;
    }

    /**
     * Returns the priority of the element with highest priority.
     * PRE: !isEmpty()
     */
    public P consultPty() {
        return heap.min().pty;
    }

    /**
     * Removes and returns the element with highest priority.
     * PRE: !isEmpty()
     */
    public E dequeue() {
        return heap.removeMin().elt;
    }

    @Override
    public String toString() {
        return heap.toString();
    }

    //=============================================================
    class HeapElt implements Comparable<HeapElt> {
        private E elt;
        private P pty;
        private long id;

        public HeapElt(P thePty, E theElt, long theId) {
            elt = theElt;
            pty = thePty;
            id = theId;
        }

        @Override
        public int compareTo(HeapElt arg0) {
            int compare = pty.compareTo(arg0.pty);
            if (compare == 0) compare = (int) (id - arg0.id);
            return compare;
        }

        @Override
        public String toString() {
            return pty + " -> " + elt;
        }
    }
}

