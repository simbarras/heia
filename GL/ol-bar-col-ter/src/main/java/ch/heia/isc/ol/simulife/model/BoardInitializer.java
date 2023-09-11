package ch.heia.isc.ol.simulife.model;

import ch.heia.isc.ol.simulife.beans.Beer;
import ch.heia.isc.ol.simulife.beans.Homer;
import ch.heia.isc.ol.simulife.factory.CopsFactory;
import ch.heia.isc.ol.simulife.factory.SpikeStripFactory;

import java.awt.*;
import java.util.Random;

/**
 * Initialize the board with the base elements
 */
public class BoardInitializer {
    private static final int WIDTH = 22;
    private static final int HEIGHT = 22;

    private static final CopsFactory COPS_FACTORY = new CopsFactory();
    // Used to force adding this factory to the list of factories
    private static final SpikeStripFactory SPIKE_STRIP_FACTORY = new SpikeStripFactory();

    public Board initializeBoard(int width, int height) {
        Board board = new Board(width, height);
        Random rnd = new Random(board.getHeight());

        Beer b = new Beer(board);
        b.setColor(Color.red);
        board.addElement(b, rnd.nextInt(board.getWidth()), rnd.nextInt(board.getHeight()));
        Homer h = new Homer(board);
        h.setColor(Color.yellow);
        board.addElement(h, rnd.nextInt(board.getWidth()), rnd.nextInt(board.getHeight()));

        COPS_FACTORY.createAndAddElement(board, rnd.nextInt(board.getWidth()), rnd.nextInt(board.getHeight()));

        return board;
    }

    public Board initializeBoard(){
        return initializeBoard(WIDTH, HEIGHT);
    }
}
