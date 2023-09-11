package ch.heia.isc.ol.simulife.model;

import ch.heia.isc.gl1.simulife.interface_.IActionnableElement;
import ch.heia.isc.gl1.simulife.interface_.IControllableUniverse;

import java.awt.*;
import java.util.Map;
import java.util.Random;


/**
 * Elements that can be added to a cell
 */
public abstract class CellElt implements IActionnableElement, Comparable<CellElt>, Cloneable {
    protected int x;
    protected int y;
    protected char code;
    protected Color color;
    protected String iconPath;
    protected Board board;
    protected int priority;
    protected Map<String, String> state;
    protected boolean needToFinish = false;
    protected Random random;


    /* ====> Constructor <==== */

    /**
     * Create a new CellElt
     *
     * @param code     the code to determine the type of the element
     * @param priority the priority of the element
     * @param board    the board where the element is
     */
    protected CellElt(char code, int priority, Board board) {
        this.x = -1;
        this.y = -1;
        this.board = board;
        this.code = code;
        this.priority = priority;
        this.random = new Random(board.seedGenerator());
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setUniverse(IControllableUniverse iControllableUniverse) {
        board = (Board) iControllableUniverse;
    }

    @Override
    public int[] getPosition() {
        return new int[]{x, y};
    }

    @Override
    public IControllableUniverse getUniverse() {
        return board;
    }

    @Override
    public Map<String, String> getState() {
        return state;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getCode() {
        return code;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getIconPath() {
        return iconPath;
    }

    /**
     * Used by the simulation to activate the elements int the right order
     *
     * @param o the object to be compared.
     * @return the difference between the priority
     */
    @Override
    public int compareTo(CellElt o) {
        return o.priority - this.priority;
    }

    /**
     * Used by the command pattern to freeze an element
     *
     * @return the frozen element
     */
    @Override
    public CellElt clone() {
        try {
            return (CellElt) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * This method is used to know if the simulation is finished. Only Cops and Homer can finish the simulation.
     * After every action, this state is fetched to continue or not the simulation.
     *
     * @return true if the simulation has to be finished, false otherwise
     */
    public boolean needToFinish() {
        return needToFinish;
    }

    /* ====> Vue <==== */
    public String toString() {
        return code + "";
    }
}
