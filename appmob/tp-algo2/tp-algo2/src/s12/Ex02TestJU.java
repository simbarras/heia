package s12;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex02TestJU {

    private static final int NB_TESTS = 1000;
    private static final int SIZE_MAX = 10;

    @Test
    public void test_description() {
        int[] t = new int[]{0, 1, 2, 3, 4, 5, 6};
        int pivot = 3;
        int[] t1 = Ex02.rotateNaive1(t, pivot);
        int[] t2 = Ex02.rotate(t, pivot);
        for (int i = 0; i < t2.length; i++) {
            System.out.print(t2[i] + ", ");
        }
        test(t1, t2);
    }

    @Test
    public void test_specific() {
        int[] t = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        int pivot = 3;
        int[] t1 = Ex02.rotateNaive1(t, pivot);
        int[] t2 = Ex02.rotate(t, pivot);
        for (int i = 0; i < t2.length; i++) {
            System.out.print(t2[i] + ", ");
        }
        test(t1, t2);
    }

    @Test
    public void test_random() {
        for (int i = 0; i < NB_TESTS; i++) {
            int[] t = new int[(int) (Math.random() * SIZE_MAX)];
            int pivot = (int) (Math.random() * t.length);
            for (int j = 0; j < t.length; j++) {
                t[j] = j;
                System.out.print(j + ", ");
            }
            System.out.println(" Rotate: " + pivot + " size: " + t.length);
            test(Ex02.rotateNaive1(t, pivot), Ex02.rotate(t, pivot));
        }
    }

    public void test(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            try {
                assertEquals(a[i], b[i]);
            } catch (AssertionError e) {
                for (int j = 0; j < a.length; j++) {
                    System.out.print(a[j] + ", ");
                }
                throw e;
            }
        }
    }

    @Test
    public void checkAssert() {
        int ec = 0;
        assert (ec = 1) == 1;
        if (ec != 0) return;
        System.out.println("WARNING: assertions are disabled ('java -ea...')");
    }
}
