package ch.heia.isc.ol.simulife.factory;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for the creation of elements
 */
public abstract class CellEltFactory {
    public static List<CellEltFactory> factories = new ArrayList<>();

    public abstract CellElt createAndAddElement(Board board, int x, int y);

    public abstract String getName();
}
