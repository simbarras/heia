package ch.heia.isc.ol.simulife.model;

import ch.heia.isc.gl1.simulife.interface_.IActionnableElement;
import ch.heia.isc.gl1.simulife.interface_.ICell;
import ch.heia.isc.gl1.simulife.interface_.IControllableUniverse;
import ch.heia.isc.gl1.simulife.interface_.IPositionnableElement;

import java.util.*;

/**
 * The board that contains the cells
 */
public class Board implements IControllableUniverse {
    private static final int SEED = 41;
    private final Random random;
    private final Cell[][] universeBoard;

    /* ====> Constructor <==== */

    /**
     * Create a new Board
     *
     * @param width  the width of the board
     * @param height the height of the board
     */
    public Board(int width, int height) {
        try {
            if (width <= 0 || height <= 0)
                throw new IllegalArgumentException("Invalid board size, must be positive integers");
            universeBoard = new Cell[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    universeBoard[y][x] = new Cell(this, x, y);
                }
            }
            random = new Random(SEED);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid board size, must be positive integers");
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not enough memory to create the board");
        }

    }

    /**
     * Check if the element's position is valid (inside the board)
     *
     * @param x the x coordinate of the element to check
     * @param y the y coordinate of the element to check
     */
    private void checkEltCoordinate(int x, int y) {
        if (x < 0 || x >= universeBoard[0].length || y < 0 || y >= universeBoard.length) {
            throw new IllegalArgumentException("Invalid coordinates. Must be positive integers and less than board size");
        }
    }

    @Override
    public int getWidth() {
        return universeBoard[0].length;
    }

    @Override
    public int getHeight() {
        return universeBoard.length;
    }

    @Override
    public ICell getICell(int x, int y) throws ArrayIndexOutOfBoundsException {
        return universeBoard[y][x];
    }

    /**
     * Use to show board in the console
     *
     * @return the board as string
     */
    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder("Board (" + getWidth() + "x" + getHeight() + ") {\n");
        for (ICell[] cells : universeBoard) {
            for (int j = 0; j < getHeight(); j++) {
                msg.append(cells[j].toString()).append("\t");
            }
            msg.append("\n");
        }
        return msg + "}";

    }

    /**
     * Remove and add element to move it
     *
     * @param iPositionnableElement the element to move
     * @param newX                  the new x coordinate
     * @param newY                  the new y coordinate
     * @throws ArrayIndexOutOfBoundsException if the new coordinates aren't valid
     */
    @Override
    public void moveElement(IPositionnableElement iPositionnableElement, int newX, int newY) throws ArrayIndexOutOfBoundsException {
        CellElt elt = (CellElt) iPositionnableElement;
        universeBoard[elt.getY()][elt.getX()].deleteElement(elt);
        universeBoard[newY][newX].addElement(elt);
    }

    /**
     * Place an element on the top of a cell in the board
     *
     * @param iActionnableElement the element to place
     * @param x                   the x coordinate of the cell
     * @param y                   the y coordinate of the cell
     * @throws ArrayIndexOutOfBoundsException if the new coordinates aren't valid
     */
    @Override
    public void addElement(IActionnableElement iActionnableElement, int x, int y) throws ArrayIndexOutOfBoundsException {
        checkEltCoordinate(x, y);
        universeBoard[y][x].addElement((CellElt) iActionnableElement);
    }

    /**
     * Remove an element from the board
     *
     * @param iPositionnableElement the element to remove. It contains his own coordinates
     * @throws IllegalArgumentException if the element isn't on the board
     */
    @Override
    public void removeElement(IPositionnableElement iPositionnableElement) throws IllegalArgumentException {
        CellElt elt = (CellElt) iPositionnableElement;
        checkEltCoordinate(elt.getX(), elt.getY());
        universeBoard[elt.getY()][elt.getX()].deleteElement(elt);
    }

    /**
     * Used by the simulation to activate the element in the right order
     *
     * @return a priority queue of the elements sorted by their priority
     */
    public PriorityQueue<CellElt> getAllElements() {
        PriorityQueue<CellElt> elements = new PriorityQueue<>();
        for (Cell[] cells : universeBoard) {
            for (Cell cell : cells) {
                for (int i = 0; i < cell.getNumberOfElements(); i++) {
                    elements.add((CellElt) cell.getElement(i));
                }
            }
        }
        return elements;
    }

    public int seedGenerator() {
        return random.nextInt();
    }

    /**
     * Remove all elements from the board
     */
    public void clearBoard() {
        for (Cell[] cells : universeBoard) {
            for (Cell cell : cells) {
                while (cell.getNumberOfElements() > 0) {
                    removeElement((IPositionnableElement) cell.getTopElement());
                }
            }
        }
    }
}
