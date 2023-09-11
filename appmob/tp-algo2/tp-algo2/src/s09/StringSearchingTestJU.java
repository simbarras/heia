package s09;

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

// ------------------------------------------------------------
public class StringSearchingTestJU {

    // ------------------------------------------------------------
    @Test
    public void testRabinKarpHasher301Base256() {
        System.out.println(" --- Trying with original HASHER and BASE...");
        StringSearching.HASHER = 301;
        StringSearching.BASE = 256;
        int max = 400;
        test(max);
    }

    @Test
    public void testRabinKarpHasher7Base257() {
        System.out.println(" --- Trying with another HASHER and BASE...");
        StringSearching.HASHER = 7;
        StringSearching.BASE = 257;
        int max = 400;
        test(max);
    }

    // ------------------------------------------------------------
    public static String rndWord(int length, String alphabet, Random r) {
        String s = "";
        int x;
        for (int i = 0; i < length; i++) {
            x = r.nextInt(alphabet.length());
            s += alphabet.charAt(x);
        }
        return s;
    }

    // ------------------------------------------------------------
    private static void test(int n) {
        Random r = new Random();
        String t, p;
        int j;
        int occ = 0;
        String alphabet = "ab";
        assertTrue(0 == StringSearching.indexOf_rk("wxyz", "wxyz"));
        assertTrue(0 == StringSearching.indexOf_rk("wxyz0123456789wxya", "wxyz"));
        assertTrue(-1 == StringSearching.indexOf_rk("wxyz0123456789wxywxyz0123456789wxy", "a"));
        assertTrue(1 == StringSearching.indexOf_rk("0wxyz", "wxyz"));
        assertTrue(10 == StringSearching.indexOf_rk("0123456789wxyz", "wxyz"));
        t = rndWord(n, alphabet, r);
        for (int i = 0; i < n * n; i++) {
            int l = (int) Math.sqrt(n);
            p = rndWord(1 + r.nextInt(l), alphabet, r);
            j = t.indexOf(p);
            if (j != -1) occ++;
            assertTrue(j == StringSearching.indexOf_rk(t, p));
        }
        System.out.println("Nb of method calls : " + n * n);
        System.out.println("Nb of found occurrences : " + occ);
        System.out.println("Test passed successfully");
    }
    // ------------------------------------------------------------
}
