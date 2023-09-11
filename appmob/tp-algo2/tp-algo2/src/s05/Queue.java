package s05;

public class Queue<E> {

    //======================================================================
    private static class QueueNode<F> {
        final F elt;
        QueueNode next = null;

        // ----------
        QueueNode(F elt) {
            this.elt = elt;
        }
    }

    // ======================================================================
    private QueueNode front;
    private QueueNode back;

    // ------------------------------
    public Queue() {
    }

    // --------------------------
    public void enqueue(E elt) {
        QueueNode<E> qn = new QueueNode(elt); // <-- Create a new tmp element
        if (isEmpty()) {
            front = qn; // <-- Set first element
        } else {
            back.next = qn; // <-- Change this element with the last back
        }
        back = qn; // <-- Replace the back
    }

    // --------------------------
    public boolean isEmpty() {
        return back == null;
    }

    // --------------------------
    // PRE : !isEmpty()
    public E consult() {
        return (E) front.elt;
    }

    // --------------------------
    // PRE : !isEmpty()
    public E dequeue() {
        E e = (E) front.elt;
        if (front == back) {
            back = null;
            front = null;
        } else {
            front = front.next;
        }
        return e;
    }

    // --------------------------
    public String toString() {
        String res = "";
        QueueNode c = front;
        while (c != null) {
            res += c.elt + " ";
            c = c.next;
        }
        return res;
    }
}
