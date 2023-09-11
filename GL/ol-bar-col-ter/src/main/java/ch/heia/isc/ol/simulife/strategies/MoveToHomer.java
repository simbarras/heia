package ch.heia.isc.ol.simulife.strategies;

import ch.heia.isc.ol.simulife.beans.Beer;

/**
 * Strategy for the movement of Cops
 */
public class MoveToHomer extends MovementStrategy {

    private static MoveToHomer instance = null; // Singleton

    private MoveToHomer() {
        super(DIRECTIONS, AVOID);
    }

    /**
     * Get the instance of the singleton
     *
     * @return the instance of the singleton
     */
    public static MoveToHomer getInstance() {
        if (instance == null) {
            instance = new MoveToHomer();
        }
        return instance;
    }

    private final static Class[] AVOID = {Beer.class};

    /**
     * The directions that Cops can move
     */
    private final static int[][] DIRECTIONS = {
            {0, 1},
            {-1, 0}, {1, 0},
            {0, -1}
    };
}
