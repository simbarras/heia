package ch.heia.isc.ol.simulife;

import ch.heia.isc.ol.simulife.model.Board;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestSize {

    private static final int LOWER_BOUND = 1;
    private static final int UPPER_BOUND = 10000;

    private int low, high, mid; // Set here to put them in the heap


    @Test
    void testBoardCreation() {
        long memoryMb = Long.parseLong(System.getProperty("xmx"));
        long memoryB = memoryMb * 1024 * 1024;
        System.out.println("Memory expected: " + memoryMb + "MB ("+memoryB+")");
        System.out.println("Memory available: " + Runtime.getRuntime().maxMemory());

        low = LOWER_BOUND;
        high = UPPER_BOUND;
        while (high - low > 1) {
            mid = (high - low) / 2 + low;
            System.out.println("Trying to create a board with size " + mid + "x" + mid + " (Range: " + low +" - " + high + ")");
            try {
                System.gc();
                Board b = new Board(mid, mid);
                System.out.println("Board created successfully with size " + mid + "x" + mid);
                b = null;
                low = mid;
            } catch (OutOfMemoryError e) {
                System.out.println("Board creation failed with size " + mid + "x" + mid);
                high = mid;
            }
        }

        System.out.println("Maximum board side size: " + low );
        long expected = Long.parseLong(System.getProperty("maxSize"));
        long precision = Long.parseLong(System.getProperty("precision"));
        System.out.println("Expected board side size: " + expected + " (+/- "+precision+")");

        Assertions.assertTrue( expected - precision <= low && low <= expected + precision);
    }
}


