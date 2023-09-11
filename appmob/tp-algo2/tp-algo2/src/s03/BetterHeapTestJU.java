package s03;

import org.junit.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class BetterHeapTestJU {

    private int value;
    final private static int MAX_NUMBER = 100;

    @Test
    public void testThreeAdds() {
        BetterHeap<Integer> heap = new BetterHeap<>();
        heap.add(-8);
        heap.add(-9);
        heap.add(-7);
        assertEquals(heap.removeMin(), Integer.valueOf(-9));
    }

    @Test
    public void testThreeRemoveMax() {
        BetterHeap<Integer> heap = new BetterHeap<>();
        heap.add(-8);
        heap.add(-9);
        heap.add(-7);
        assertEquals(heap.removeMax(), Integer.valueOf(-7));
    }

    @Test
    public void randomRemoveMax() {
        Random r = new Random();
        BetterHeap<Integer> heap;
        int maxTemp;
        for (int i = 0; i < 100; i++) {
            maxTemp = Integer.MIN_VALUE;
            heap = new BetterHeap<>();
            for (int j = 0; j < r.nextInt(MAX_NUMBER) + 1; j++) {
                value = r.nextInt(MAX_NUMBER);
                heap.add(value);
                if (value > maxTemp) maxTemp = value;
            }
            System.out.println(heap.toReadableString(0));
            assertEquals(Integer.valueOf(maxTemp), heap.removeMax());
        }
    }


    @Test
    public void testRandomOperations() {
        int nbOfOperations = 1_000_000;
        BetterHeap<Integer> h = new BetterHeap<>();
        PriorityQueue<Integer> p = new PriorityQueue<Integer>();
        Random r = new Random();
        for (int i = 0; i < nbOfOperations; i++) {
            if (r.nextBoolean()) {
                int e = r.nextInt();
                h.add(e);
                p.add(e);
                //  System.out.println("Add: " + e);
            } else {
                if (!p.isEmpty()) {
                    //   System.out.println("Remove");
                    assertEquals(p.remove(), h.removeMin());
                }
            }
            // System.out.println("is empty: " + h.isEmpty() +" <-> "+p.isEmpty());
            assertEquals(p.isEmpty(), h.isEmpty());
            if (!h.isEmpty()) {
                // System.out.println("Value: " + h.min() +" <-> "+p.peek());
                assertEquals(p.peek(), h.min());
                // System.out.println(h.toReadableString(0));
            }
            // System.out.println();
        }
        // System.out.println("Test passed with success");
    }
}
