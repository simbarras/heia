package ch.heia.isc.ol.simulife.factory;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.MobileElt;

/**
 * Factory for the creation of MobileElt
 */
public abstract class MobileFactory extends CellEltFactory {

    @Override
    public abstract MobileElt createAndAddElement(Board board, int x, int y);
}
