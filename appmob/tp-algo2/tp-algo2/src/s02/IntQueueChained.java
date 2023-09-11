package s02;

public class IntQueueChained {
    //======================================================================
    /* done: adapt using pseudo-pointers instead of queue node objects
     * "Memory management" code:
     * - define "memory" arrays, the NIL constant, and firstFreeCell
     * - define allocate/deallocate, with automatic array expansion
     * "User" code:
     * - modify enqueue/dequeue/..., keeping the same logic/algorithm
     * - test
     */
    //======================================================================
    static class Memory {
        static private final int NIL = -1;
        static private final int INITIAL_SIZE = 10;

        static private int[] elt;
        static private int[] next;
        static private int firstSpace;

        public Memory() {
            initialize(INITIAL_SIZE);
        }

        public Memory(int size) {
            initialize(size);
        }

        private void initialize(int initialSize) {
            try {
                if (next[next.length - 1] != NIL) throw new NullPointerException();
            } catch (NullPointerException e) {
                //initialize value
                firstSpace = 0;
                elt = new int[initialSize];
                next = new int[initialSize];

                //fill memory
                fillEmptyMemory(0, next);
            }
        }

        public int getElt(int index) {
            return index == NIL ? 0 : elt[index];
        }

        public int getNext(int index) {
            return next[index];
        }

        public void setNext(int index, int value) {
            next[index] = value;
        }

        public int allocate(int eltValue, int nextValue) {
            if (isMemoryFull()) resize();
            int index = firstSpace;
            firstSpace = next[firstSpace];
            elt[index] = eltValue;
            next[index] = nextValue;
            return index;
        }

        public int deallocate(int index) {
            int nextFirstSpace = next[index];
            next[index] = firstSpace;
            firstSpace = index;
            return nextFirstSpace;
        }

        static private void fillEmptyMemory(int start, int[] next) {
            for (int i = start; i < next.length - 1; i++) {
                next[i] = i + 1;
            }
            next[next.length - 1] = NIL;
        }

        private boolean isMemoryFull() {
            return firstSpace == NIL;
        }

        private void resize() {
            int[] newElt = new int[elt.length * 2];
            int[] newNext = new int[next.length * 2];
            for (int i = 0; i < elt.length; i++) {
                newElt[i] = elt[i];
                newNext[i] = next[i];
            }
            fillEmptyMemory(next.length, newNext);
            firstSpace = elt.length;
            elt = newElt;
            next = newNext;
        }
    }

    //======================================================================
    private int front = Memory.NIL;
    private int back = Memory.NIL;
    private final Memory memory = new Memory(2);

    public IntQueueChained() {
    }

    public void enqueue(int elt) {
        int aux = memory.allocate(elt, Memory.NIL);
        if (back == Memory.NIL) {
            back = aux;
            front = aux;
        } else {
            memory.setNext(back, aux);
            back = aux;
        }
    }

    public boolean isEmpty() {
        return back == Memory.NIL;
    }

    public int consult() {
        return memory.getElt(front);
    }

    public int dequeue() {
        int e = memory.getElt(front);
        int nextFront = memory.deallocate(front);
        if (front == back) {
            back = Memory.NIL;
            front = Memory.NIL;
        } else {
            front = nextFront;
        }
        return e;
    }
}
