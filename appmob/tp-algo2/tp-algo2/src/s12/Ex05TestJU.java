package s12;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Ex05TestJU {

    @Test
    public void test_description() {
        String[] t = {"spot", "zut", "pots", "stop", "hello"};
        List<String> expected = new ArrayList<>();
        expected.add("spot");
        expected.add("pots");
        expected.add("stop");
        Ex05 dico = new Ex05();
        dico.add(t);
        test(dico.findAnagram("stop"), expected);
    }

    public void test(List<String> a, List<String> b) {
        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i) + ", ");
            assertEquals(a.get(i), b.get(i));
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
