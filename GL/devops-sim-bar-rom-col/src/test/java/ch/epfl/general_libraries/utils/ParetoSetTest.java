package ch.epfl.general_libraries.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class ParetoSetTest {

    public static class ParetoPoint3DTest extends ParetoPoint {

        private int[] val;

        public ParetoPoint3DTest(int a, int b, int c) {
            val = new int[]{a,b,c};
        }

        @Override
        public double getValueOfDimensionN(int n) {
            return val[n];
        }

        @Override
        public boolean isDimensionNtheHigherTheBetter(int n) {
            return true;
        }

        @Override
        public int getDimensions() {
            return 3;
        }

        public String toString() {
            return Arrays.toString(val);
        }
    }

    @Test
    public void testPareto() {
        ParetoSet<ParetoPoint3DTest> ps = new ParetoSet<ParetoPoint3DTest>(3);
        ps.addCandidate(new ParetoPoint3DTest(1,4,8));
        ps.addCandidate(new ParetoPoint3DTest(1,3,8));
        ps.addCandidate(new ParetoPoint3DTest(1,2,8));
        ps.addCandidate(new ParetoPoint3DTest(2,4,8));
        ps.addCandidate(new ParetoPoint3DTest(2,3,8));
        ps.addCandidate(new ParetoPoint3DTest(2,2,8));
        ps.addCandidate(new ParetoPoint3DTest(2,1,8));
        ps.addCandidate(new ParetoPoint3DTest(3,4,8));
        ps.addCandidate(new ParetoPoint3DTest(3,3,8));
        ps.addCandidate(new ParetoPoint3DTest(3,2,8));
        ps.addCandidate(new ParetoPoint3DTest(3,1,8));
        ps.addCandidate(new ParetoPoint3DTest(4,4,8));
        ps.addCandidate(new ParetoPoint3DTest(4,3,8));
        ps.addCandidate(new ParetoPoint3DTest(4,2,8));
        ps.addCandidate(new ParetoPoint3DTest(4,1,8));
        ps.addCandidate(new ParetoPoint3DTest(8,4,7));
        ps.addCandidate(new ParetoPoint3DTest(1,5,7));
        ps.addCandidate(new ParetoPoint3DTest(7,1,6));
        ParetoPoint3DTest dominant = new ParetoPoint3DTest(10,8,8);
        ps.addCandidate(dominant);


        ArrayList<ParetoPoint3DTest> expected = new ArrayList<>();
        expected.add(dominant);

        Assertions.assertArrayEquals(expected.toArray(), ps.getParetoPoints().toArray());

    }


}
