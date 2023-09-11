package ch.heia.isc.ol.simulife.strategies;

import ch.heia.isc.ol.simulife.beans.Cops;
import ch.heia.isc.ol.simulife.beans.SpikeStrip;

/**
 * Strategy for the movement of Homer
 */
public class MoveToBeer extends MovementStrategy {

    private static MoveToBeer instance = null; // Singleton

    private MoveToBeer() {
        super(DIRECTIONS, AVOID);
    }

    /**
     * Get the instance of the singleton
     *
     * @return the instance of the singleton
     */
    public static MoveToBeer getInstance() {
        if (instance == null) {
            instance = new MoveToBeer();
        }
        return instance;
    }

    private final static Class[] AVOID = {Cops.class, SpikeStrip.class};

    /**
     * The directions that Homer can move
     */
    private final static int[][] DIRECTIONS = {
            {-2, 2}, {0, 2}, {2, 2},
            {-1, 1}, {0, 1}, {1, 1},
            {-2, 0}, {-1, 0}, {1, 0}, {2, 0},
            {-1, -1}, {0, -1}, {1, -1},
            {-2, -2}, {0, -2}, {2, -2}
    };
}
