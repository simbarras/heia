package s03;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.PriorityQueue;
import java.util.Random;

public class HeapTestJU {

    @Test
    public void testThreeAdds() {
        Heap<Integer> heap = new Heap<>();
        heap.add(-8);
        heap.add(-9);
        heap.add(-7);
        assertEquals(heap.removeMin(), Integer.valueOf(-9));
    }

    @Test
    public void testRandomOperations() {
        int nbOfOperations = 1_000_000;
        Heap<Integer> h = new Heap<>();
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
