package s12;

import org.junit.Test;

import java.util.Random;

public class Ex03TestJU {
    private static final int NB_TESTS = 1000;
    private static final int NB_FCT = 4;

    @Test
    public void test_Random() {
        Random random = new Random();
        Ex03 s = new Ex03();
        for (int i = 0; i < NB_TESTS; i++) {
            int t = random.nextInt(NB_FCT);
            switch (t) {
                case 0:
                    s.push((int) Math.random() * NB_TESTS);
                    break;
                case 1:
                    s.pop();
                    break;
                case 2:
                    s.consultMin();
                    break;
                case 3:
                    s.consultMax();
                    break;
            }
        }
    }

    @Test
    public void test_notRandom() {
        Ex03 s = new Ex03();
        s.push(3);
        s.push(5);
        System.out.println("MIN " + s.consultMin());
        System.out.println("MAX " + s.consultMax());
        s.push(2);
        s.push(-1);
        s.push(8);
        s.push(3);
        System.out.println("MIN " + s.consultMin());
        System.out.println("MAX " + s.consultMax());
        s.pop();
        s.pop();
        s.pop();
        System.out.println("MIN " + s.consultMin());
        System.out.println("MAX " + s.consultMax());
        s.pop();
        System.out.println("MIN " + s.consultMin());
        System.out.println("MAX " + s.consultMax());
    }

}
