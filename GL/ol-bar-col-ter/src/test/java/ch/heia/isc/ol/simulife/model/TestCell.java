package ch.heia.isc.ol.simulife.model;

import org.junit.jupiter.api.Test;
import ressources.EmptyElt;

import static org.junit.jupiter.api.Assertions.*;

class TestCell {

    Cell cell;
    Board b = new Board(10, 10);

    @Test
    void testGetElement() {
        cell = new Cell(null, 1, 1);
        assertFalse(getElement(-1));
        assertFalse(getElement(0));
        assertFalse(getElement(1));
        assertEquals(0, cell.getNumberOfElements());

        cell.addElement(new EmptyElt(b));
        assertFalse(getElement(-1));
        assertTrue(getElement(0));
        assertFalse(getElement(1));
        assertEquals(1, cell.getNumberOfElements());

        cell.addElement(new EmptyElt(b));
        assertFalse(getElement(-1));
        assertTrue(getElement(0));
        assertTrue(getElement(1));
        assertEquals(2, cell.getNumberOfElements());
    }

    private boolean getElement(int i) {
        try {
            cell.getElement(i);
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Test
    void testDelete() {
        cell = new Cell(null, 1, 1);
        EmptyElt e = new EmptyElt(b);
        assertFalse(delete(e));
        cell.addElement(e);
        assertFalse(delete(new EmptyElt(b)));
        assertTrue(delete(e));
        assertFalse(delete(e));

    }

    private boolean delete(EmptyElt e) {
        try {
            cell.deleteElement(e);
            return true;
        } catch (IllegalArgumentException e1) {
            return false;
        }
    }
}
