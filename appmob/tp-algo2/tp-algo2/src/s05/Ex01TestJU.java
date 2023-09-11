package s05;

import org.junit.Test;
import s12.Ex02;

import static org.junit.Assert.assertEquals;

public class Ex01TestJU {
    public final static int NB_TESTS = 1000;
    public final static int CHAR_MAX = 125;
    public final static int CHAR_MIN = 33;


    @Test
    public void test_random() {
        CharStack charStack = new CharStack();
        Queue<Character> queue = new s05.Queue<Character>();

        try {
            for (int i = 0; i < NB_TESTS; i++) {
                char rChar = (char) (CHAR_MIN + (Math.random() * (CHAR_MAX - CHAR_MIN)));
                int r = (int) (Math.random() * 2);
                if (r == 1) {
                    charStack.push(rChar);
                    queue.enqueue(rChar);
                } else {
                    assertEquals(queue.isEmpty(), charStack.isEmpty());
                    if (!charStack.isEmpty()) {
                        assertEquals((int) queue.dequeue(), (int) charStack.pop());
                    }
                }
            }
        } catch (AssertionError e) {

            System.out.println("Error");

            while (!queue.isEmpty()) {
                System.out.print(queue.dequeue() + ", ");
            }
            System.out.println();
            while (!charStack.isEmpty()) {
                System.out.print(charStack.pop() + ", ");
            }
            System.out.println();
            throw e;
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
