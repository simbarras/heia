package ch.heia.isc.ol.simulife;

import ch.heia.isc.ol.simulife.beans.TestElement;
import ch.heia.isc.ol.simulife.model.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestMain {
    @Test
    void testDefaultSimulation() {
        // Load simulation
        System.out.println("Load the simulator");
        Board board = new Board(10, 10);

        System.out.println(board);

        // Add element
        System.out.println("Add element");
        TestElement testElement = new TestElement(board);
        board.addElement(testElement, 0, 0);
        System.out.println(board);

        // Move element
        System.out.println("Move element");
        board.moveElement(testElement, 1, 1);
        System.out.println(board);

        // Delete element
        System.out.println("Delete element");
        board.removeElement(testElement);
        System.out.println(board);

        assertTrue(true);
    }
}
