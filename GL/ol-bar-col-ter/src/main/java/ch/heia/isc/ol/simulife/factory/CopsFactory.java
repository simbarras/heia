package ch.heia.isc.ol.simulife.factory;

import ch.heia.isc.ol.simulife.beans.Cops;
import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.MobileElt;

/**
 * Factory for the creation of Cops
 */
public class CopsFactory extends MobileFactory {

    static {
        factories.add(new CopsFactory());
    }

    /**
     * Factory method for the creation of Cops
     *
     * @param board the board on which the cops will be crated
     * @param x     the x coordinate of the new cops
     * @param y     the y coordintate of the new cops
     * @return The new cops
     */
    @Override
    public MobileElt createAndAddElement(Board board, int x, int y) {
        Cops c = new Cops(board);
        board.addElement(c, x, y);
        return c;
    }

    /**
     * Used by the view
     *
     * @return the name of the factory
     */
    @Override
    public String getName() {
        return "Cops";
    }
}
