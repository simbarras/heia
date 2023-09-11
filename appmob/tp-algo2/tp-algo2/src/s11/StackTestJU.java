package s11;

import org.junit.Test;
import org.junit.Before;
import s05.GeneralSearchTreeMap;

import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class StackTestJU {
    private IntStack s1, s2;

    @Before
    public void setUp() {
        s1 = new IntStack(10);
        s2 = new IntStack();
    }

    @Test
    public void testNewIsEmpty() {
        assertTrue(s1.isEmpty() && s2.isEmpty());
    }

    @Test
    public void testNotEmpty() {
        s1.push(21);
        s2.push(12);
        assertTrue(!s1.isEmpty() && !s2.isEmpty());
    }

    @Test
    public void testPushThenPop() {
        s1.push(4);
        assertEquals(4, s1.pop());
    }

    @Test
    public void testOverSizeAndTop() {
        for (int i = 0; i <= 50; i++) {
            s1.push(i);
        }
        assertEquals(50, s1.top());
    }

@Test
public void testRandom() {
    Random r = new Random();
    int[] arr = new int[100];
    int size = 0;
    int val;
    int step = 0;
    s1 = new IntStack();
    try {
        for (step = 0; step < 1000; step++) {
            if (s1.isEmpty()) {
                val = r.nextInt();
                s1.push(val);
                arr[size++] = val;
            }

            if (size == 100) {
                assertEquals(arr[size - 1], s1.top());
                assertEquals(arr[size - 1], s1.pop());
                size--;
            }

            if (r.nextInt(3) != 0) {
                val = r.nextInt();
                s1.push(val);
                arr[size++] = val;
            } else {
                assertEquals(arr[size - 1], s1.top());
                assertEquals(arr[size - 1], s1.pop());
                size--;
            }

            assertEquals(size == 0, s1.isEmpty());
        }
    } catch (AssertionError e) {
        System.out.printf("Step: %d\n", step);
        System.out.printf("Size: %d isEmpty -> %b\n", size, s1.isEmpty());
        for(int i = size; i >= 0; i--) {
            System.out.printf("%d <-> %d", arr[i], s1.pop());
        }
    }
}
}
