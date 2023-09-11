package ch.epfl.general_libraries.ch.epfl.general_libraries.random;

import ch.epfl.general_libraries.random.PRNStream;
import org.junit.jupiter.api.Test;

public class RandomTest {

    private final static int NUMBER_OF_SAMPLE = 1000;

    @Test
    public void testFilterPlus() throws Exception {
        PRNStream st;

        double[] weights = new double[]{0.10, 0.20, 0.20, 0.10, .1, .1, .2};

        for (int s = 0 ; s < 5 ; s++) {
            st = PRNStream.getDefaultStream(s);

            int[] res = new int[weights.length];
            for (int i = 0 ; i < NUMBER_OF_SAMPLE ;i++) {
                res[st.getRandomIndex(weights)]++;
            }

            int totSam = 0;
            for (int i = 0 ; i < res.length ; i++) {
                totSam += res[i];
            }
            if (totSam != NUMBER_OF_SAMPLE) throw new IllegalStateException();
        }


    }
}
