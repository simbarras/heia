package ch.epfl.general_libraries.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class StatFunctionsTest {

    @Test
    public void testStats() {
        int numberOfSamples = 20;
        float[] tab = new float[numberOfSamples];
        for (int i = 0 ; i < tab.length ; i++) {
            tab[i] = 0.0001f * i;
        }
        Assertions.assertEquals(StatFunctions.getMean(tab), 9.4999996E-4f);
        Assertions.assertEquals(StatFunctions.getVariance(tab), 3.499999365885742E-7);
        float[] confi = StatFunctions.getConfidenceInterval(tab, 0.95f);
        Assertions.assertEquals(confi[0], 6.731189205311239E-4f);
        Assertions.assertEquals(confi[1], 0.00122688093688339f);
    }
}
