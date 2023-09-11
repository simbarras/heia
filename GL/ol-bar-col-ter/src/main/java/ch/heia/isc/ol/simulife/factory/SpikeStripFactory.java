package ch.heia.isc.ol.simulife.factory;

import ch.heia.isc.ol.simulife.beans.SpikeStrip;
import ch.heia.isc.ol.simulife.model.Board;

/**
 * Factory for the creation of SpikeStrips
 */
public class SpikeStripFactory extends CellEltFactory {
    static {
        factories.add(new SpikeStripFactory());
    }

    /**
     * Factory method for the creation of spike strips
     *
     * @param board the board on wich the spile strip will be created
     * @param x     the x coordinate of the new element
     * @param y     the y coordinate of the new element
     * @return the new element
     */
    @Override
    public SpikeStrip createAndAddElement(Board board, int x, int y) {
        SpikeStrip s = new SpikeStrip(board);
        board.addElement(s, x, y);
        return s;
    }

    /**
     * Used by the default view to display the menu
     *
     * @return
     */
    @Override
    public String getName() {
        return "SpikeStrip";
    }
}
