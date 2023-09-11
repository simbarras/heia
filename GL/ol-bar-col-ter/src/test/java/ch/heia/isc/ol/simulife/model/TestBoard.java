package ch.heia.isc.ol.simulife.model;

import org.junit.jupiter.api.Test;
import ressources.EmptyElt;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestBoard {

    private Board b;
    private List<EmptyElt> lst;
    private static final int SIZE = 10;

    @Test
    void testBoardCreation() {
        assertFalse(boardCreation(0, 0));
        assertFalse(boardCreation(-1, 0));
        assertFalse(boardCreation(0, -1));
        assertFalse(boardCreation(0, 1));
        assertFalse(boardCreation(1, 0));
        assertFalse(boardCreation(-1, 1));
        assertFalse(boardCreation(1, -1));
        assertTrue(boardCreation(1, 1));
        assertEquals(1, b.getWidth());
        assertEquals(1, b.getHeight());
        assertEquals(b, b.getICell(0, 0).getIUniverse());
        assertFalse(boardCreation(-1, 1));
        assertFalse(boardCreation(1, -1));
        assertTrue(boardCreation(1000, 1000));
        assertEquals(1000, b.getWidth());
        assertEquals(1000, b.getHeight());
        assertEquals(b, b.getICell(999, 999).getIUniverse());
    }

    private boolean boardCreation(int width, int height) {
        try {
            b = new Board(width, height);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    void testAddDelete() {
        b = new Board(SIZE, SIZE);
        lst = new ArrayList<>();
        testAdd();
        System.out.println(b);
        testMove();
        System.out.println(b);
        testDelete();
        System.out.println(b);
    }

    private void testAdd() {
        EmptyElt e = new EmptyElt(b);
        lst.add(e); //0
        assertTrue(add(e, 0, 0));

        e = new EmptyElt(b);
        lst.add(e); //1
        assertTrue(add(e, 0, 1));

        e = new EmptyElt(b);
        lst.add(e); //2
        assertTrue(add(e, 1, 0));

        e = new EmptyElt(b);
        lst.add(e); //3
        assertTrue(add(e, 1, 1));

        e = new EmptyElt(b);
        lst.add(e); //4
        assertTrue(add(e, 3, 3));

        e = new EmptyElt(b);
        lst.add(e); //5
        assertTrue(add(e, SIZE - 1, SIZE - 1));

        //--------------------------------------------

        e = new EmptyElt(b);
        lst.add(e); //6
        assertFalse(add(e, 0, -1));

        e = new EmptyElt(b);
        lst.add(e); //7
        assertFalse(add(e, -1, 0));

        e = new EmptyElt(b);
        lst.add(e); //8
        assertFalse(add(e, -1, -1));

        e = new EmptyElt(b);
        lst.add(e); //9
        assertFalse(add(e, SIZE, SIZE));

        e = new EmptyElt(b);
        lst.add(e); //10
        assertFalse(add(e, SIZE, SIZE + 1));

        e = new EmptyElt(b);
        lst.add(e); //11
        assertFalse(add(e, SIZE + 1, SIZE));

        e = new EmptyElt(b);
        lst.add(e); //12
        assertFalse(add(e, SIZE + 1, SIZE + 1));

        e = new EmptyElt(b);
        lst.add(e); //13
        assertFalse(add(e, SIZE, SIZE));

        e = new EmptyElt(b);
        lst.add(e); //14
        assertFalse(add(e, SIZE, SIZE - 1));

        e = new EmptyElt(b);
        lst.add(e); //15
        assertFalse(add(e, SIZE - 1, SIZE));

    }

    private boolean add(EmptyElt e, int x, int y) {
        try {
            b.addElement(e, x, y);
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    private void testMove() {
        assertTrue(move(lst.get(4), 1, 1));
        assertTrue(move(lst.get(4), 0, 1));
        assertTrue(move(lst.get(4), 1, 0));
        assertTrue(move(lst.get(4), SIZE - 1, SIZE - 1));

    }

    private boolean move(EmptyElt e, int x, int y) {
        try {
            b.moveElement(e, x, y);
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    private void testDelete() {
        for (int i = 0; i < 6; i++) {
            assertTrue(delete(lst.get(i)));
        }

        assertFalse(delete(lst.get(5)));

        for (int i = 6; i < lst.size(); i++) {
            assertFalse(delete(lst.get(i)));
        }
    }

    private boolean delete(EmptyElt e) {
        try {
            b.removeElement(e);
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    @Test
    void testGetCell() {
        b = new Board(SIZE, SIZE);
        EmptyElt e = new EmptyElt(b);
        b.addElement(e, 3, 3);
        assertEquals(b.getICell(3, 3).getTopElement(), e);
        assertNull(b.getICell(4, 4).getTopElement());

        assertFalse(getCell(0, -1));
        assertFalse(getCell(-1, 0));
        assertFalse(getCell(-1, -1));
        assertTrue(getCell(0, 0));
        assertTrue(getCell(0, 1));
        assertTrue(getCell(1, 0));
        assertTrue(getCell(1, 1));
        assertTrue(getCell(SIZE - 1, SIZE - 1));
        assertFalse(getCell(SIZE, SIZE));
        assertFalse(getCell(SIZE, SIZE - 1));
        assertFalse(getCell(SIZE - 1, SIZE));
        assertFalse(getCell(SIZE - 1, SIZE + 1));
        assertFalse(getCell(SIZE + 1, SIZE));
        assertFalse(getCell(SIZE + 1, SIZE + 1));
    }

    private boolean getCell(int x, int y) {
        try {
            b.getICell(x, y);
            return true;
        } catch (ArrayIndexOutOfBoundsException iae) {
            return false;
        }
    }
}
