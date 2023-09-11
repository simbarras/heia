package ch.heia.isc.ol.simulife.strategies;

import ch.heia.isc.ol.simulife.model.CellElt;

import java.util.PriorityQueue;

/**
 * Strategy pattern for the moving elements
 */
public abstract class MovementStrategy {


    /**
     * Beans to store the possible moves
     */
    public static class PossiblePosition implements Comparable {
        public int x;
        public int y;
        public int distance;

        public int compareTo(Object o) {
            PossiblePosition p = (PossiblePosition) o;
            return this.distance - p.distance;
        }
    }

    private final int[][] directions; // The directions that the element can move
    private final Class[] avoidList; // List of elements to avoid

    protected MovementStrategy(int[][] directions, Class[] avoidList) {
        this.directions = directions;
        this.avoidList = avoidList;
    }

    /**
     * Get the possible movements for the element
     *
     * @param maxWidth  The width of the board
     * @param maxHeight The height of the board
     * @param elt       The element to move
     * @param target    The target of the element
     * @return A priority queue of the possible movements
     */
    public PriorityQueue<PossiblePosition> getPossibleMovements(int maxWidth, int maxHeight, CellElt elt, CellElt target) {
        PriorityQueue<PossiblePosition> possiblePositions = new PriorityQueue<>();
        for (int[] direction : directions) {
            int newX = elt.getX() + direction[0];
            if (newX < 0 || newX >= maxWidth) {
                continue;
            }
            int newY = elt.getY() + direction[1];
            if (newY < 0 || newY >= maxHeight) {
                continue;
            }
            int distance = (newX - target.getX()) * (newX - target.getX()) + (newY - target.getY()) * (newY - target.getY());
            PossiblePosition p = new PossiblePosition();
            p.x = newX;
            p.y = newY;
            p.distance = distance;
            possiblePositions.add(p);
        }
        return possiblePositions;
    }

    /**
     * Pick the best movement from the possible movements
     *
     * @param possiblePositions The possible movements
     * @param elt               The element to move
     * @return The best movement or null if no movement is possible
     */
    public int[] pickBestMovement(PriorityQueue<PossiblePosition> possiblePositions, CellElt elt) {
        while (!possiblePositions.isEmpty()) {
            PossiblePosition p = possiblePositions.poll();
            int nbOfElements = elt.getUniverse().getICell(p.x, p.y).getNumberOfElements();
            boolean free = true;
            for (int i = 0; i < nbOfElements; i++) {
                for (Class c : avoidList) {
                    if (elt.getUniverse().getICell(p.x, p.y).getElement(i).getClass().equals(c)) {
                        free = false;
                        break;
                    }
                }
            }
            if (free) {
                return new int[]{p.x, p.y};
            }
        }
        return null;
    }
}
