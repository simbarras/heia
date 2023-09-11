package s12;

import org.junit.Test;
import s03.PtyQueue;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// ------------------------------------------------------------
public class Ex15TestJU {
    // ------------------------------------------------------------

    // ------------------------------------------------------------
    @Test
    public void testNoLevel() {
        System.out.println("Test with no level:");
        test(new float[]{1}, 1);
        test(new float[]{2, 2, 2, 2, 2, 2, 2}, 7);
    }

    @Test
    public void testExample() {
        System.out.println("Test from the example");
        test(new float[]{4, 2, 2, 8, 8, 3, 1, 5});
        test(new float[]{5, 1, 3, 8, 8, 2, 2, 4});
    }

    @Test
    public void testOneWay() {
        System.out.println("Test with array that grow or shrink of 1 every time");
        test(new float[]{4, 5, 6, 7, 8, 9, 10, 11});
        test(new float[]{11, 10, 9, 8, 7, 6, 5, 4});
    }

    @Test
    public void testBigBoss() {
        System.out.println("Big boss of the tests");
        test(new float[]{4, 2, 2, 8, 8, 3, 1, 5, -1, 12, 8, 6, 5, 2, 1, 0, -4, 0, 7, 8, 3, 2, 1, 4, 3, 2, 2, 1, 1, 0});
    }

    @Test
    public void testSample(){
        System.out.println("Test tricky sample");
        test(new float[]{1, 2, 3, 3, 4, 1, 1, 2});
        test(new float[]{2, 1, 1, 4, 3, 3, 2, 1});
        test(new float[]{4, 3, 2, 1, 2, 2, 3, 4});
        test(new float[]{2, 3, 4, 1, 1, 4, 3, 2});
        test(new float[]{1, 2, 3, 3, 4, 3, 2, 2});
        test(new float[]{2, 3, 4, 4, 4, 3, 2, 1});

    }

    @Test
    public void testRandom(){
        System.out.println("Test randomly");
        Random rnd = new Random();
        for (int i = 0; i < 1_000_000_000; i++) {
            float[] t = new float[rnd.nextInt(1000)];
            for (int j = 0; j < t.length; j++) {
                t[j] = rnd.nextInt();
            }
            System.out.printf("Test %d: ", i);
            test(t);
        }
    }

    private void test(float[] t){
        test(t, Ex15.checkMethod(t));
    }
    private void test(float[] t, int expected) {
        int receive = Ex15.nbOfStars(t);
        System.out.printf("\tExpected: %02d\t Actual: %02d\n", expected, receive);
        try {
            ok(expected, receive);
        } catch (AssertionError error){
            System.out.printf("[");
            for (int i = 0; i < t.length; i++) {
                System.out.printf("%f, ", t[i]);
            }
            System.out.printf("]\n");
            throw error;
        }
    }

    // ------------------------------------------------------------
    @Test
    public void checkAssert() {
        int ec = 0;
        assert (ec = 1) == 1;
        if (ec != 0) return;
        System.out.println("WARNING: assertions are disabled ('java -ea...')");
    }

    // ------------------------------------------------------------
    static void ok(int a, int b) {
        assertEquals(a, b);
    }
    // ------------------------------------------------------------
}
