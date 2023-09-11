package s12;

import org.junit.Test;
import s03.PtyQueue;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// ------------------------------------------------------------
public class Ex17TestJU {
    // ------------------------------------------------------------

    // ------------------------------------------------------------
    @Test
    public void check() {
        long[] nums = new long[90];
        int[] draw = new int[90];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = i + 1;
        }
        Ex17.Loto loto = new Ex17.Loto(nums, new Random());
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < nums.length; j++) {
                int n = (int) loto.nextElement();
                assertTrue(draw[n - 1] == i);
                draw[n - 1]++;
            }
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
