package ch.heia.isc.ol.simulife.beans;

import ch.heia.isc.gl1.simulife.interface_.IPositionnableElement;
import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;

import java.awt.*;

/**
 * SpikeStrip is a CellElt that can be placed on the board
 */
public class SpikeStrip extends CellElt {
    public SpikeStrip(Board b) {
        super('s', 2, b);
        this.setColor(Color.GRAY);
    }

    /**
     * Remove the other element if another element is on the same cell
     */
    @Override
    public void action() {
        // Check if there is more than one element in the cell
        while (board.getICell(this.x, this.y).getNumberOfElements() > 1) {
            if (board.getICell(this.x, this.y).getTopElement() == this) return;
            board.removeElement((IPositionnableElement) board.getICell(this.x, this.y).getTopElement());
        }
    }
    @Override
    public String getIconPath() {
        return "src/ressources/nails.png";
    }
}
