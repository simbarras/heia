package ch.heia.isc.ol.simulife.model;

import ch.heia.isc.gl1.simulife.interface_.ICell;
import ch.heia.isc.gl1.simulife.interface_.IElement;
import ch.heia.isc.gl1.simulife.interface_.IUniverse;

import java.util.ArrayList;
import java.util.List;

/**
 * The Cell that contains 0, 1 or more elements
 */
public class Cell implements ICell {   // immutable coordinates
    private final List<CellElt> lst;
    private final int x;
    private final int y;
    private final Board universe;

    /* ====> Constructor <====*/

    /**
     * Create a new Cell
     *
     * @param universe the universe where the cell is
     * @param x        the x coordinate of the cell
     * @param y        the y coordinate of the cell
     */
    public Cell(Board universe, int x, int y) {
        this.universe = universe;
        this.x = x;
        this.y = y;
        lst = new ArrayList<>();
    }

    public void addElement(CellElt elt) {
        elt.setPosition(x, y);
        lst.add(elt);
    }

    /**
     * Remove elements from the list and put his coordinates to -1
     *
     * @param elt the removed element
     */
    public void deleteElement(CellElt elt) {
        int i = lst.indexOf(elt);
        if (i == -1) throw new IllegalArgumentException("Element not found");
        elt.setPosition(-1, -1);
        lst.remove(i);
    }

    @Override
    public IUniverse getIUniverse() {
        return universe;
    }

    @Override
    public IElement getTopElement() {
        return !lst.isEmpty() ? lst.get(lst.size() - 1) : null;
    }

    @Override
    public IElement getElement(int i) throws ArrayIndexOutOfBoundsException {
        return lst.get(i);
    }

    @Override
    public int getNumberOfElements() {
        return lst.size();
    }

    /* ====> Vue <====*/
    @Override
    public String toString() {
        if (lst.isEmpty()) return "-";
        StringBuilder res = new StringBuilder();
        for (CellElt e : lst) res.append(e);
        return res.toString();
    }
}