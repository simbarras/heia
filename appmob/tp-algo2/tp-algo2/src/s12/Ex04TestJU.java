package s12;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class Ex04TestJU {

    @Test
    public void testMinMax(){
        Random rnd = new Random();
        Ex04 e = new Ex04();
        for (int i = 0; i <1000; i++) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            int[] tab = new int[10];
            for (int j = 0; j < tab.length; j++) {
                tab[j] = rnd.nextInt();
                min = min < tab[j] ? min : tab[j];
                max = max > tab[j] ? max : tab[j];
            }
            int[] r = e.maxMin(tab);
            assertTrue(r[0] == min);
            assertTrue(r[1] == max);
        }
    }
}
