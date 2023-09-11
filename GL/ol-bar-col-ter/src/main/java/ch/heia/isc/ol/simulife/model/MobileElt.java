package ch.heia.isc.ol.simulife.model;

import ch.heia.isc.gl1.simulife.interface_.ICell;
import ch.heia.isc.gl1.simulife.interface_.IElement;
import ch.heia.isc.ol.simulife.strategies.MovementStrategy;

import java.util.PriorityQueue;

/**
 * Element that use the strategy pattern to move
 */
public abstract class MobileElt extends CellElt {

    private MovementStrategy strat;

    public MobileElt(char code, int priority, Board b) {
        super(code, priority, b);
    }

    public void setStrategy(MovementStrategy strat) {
        this.strat = strat;
    }

    /**
     * Move the element to the nearest cell of the target
     *
     * @return true if the element has moved, false otherwise
     */
    protected boolean moveTo(CellElt target) {
        PriorityQueue<MovementStrategy.PossiblePosition> possiblePositions = strat.getPossibleMovements(board.getWidth(), board.getHeight(), this, target);
        int[] bestMovement = strat.pickBestMovement(possiblePositions, this);
        if (bestMovement != null) {
            this.board.moveElement(this, bestMovement[0], bestMovement[1]);
            return true;
        }
        return false;
    }

    /**
     * Search the cell of the target
     *
     * @return The cell of the target if it exists, null otherwise
     */
    protected CellElt searchTarget(Class targetClass) {
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                ICell c = board.getICell(i, j);
                for (int k = 0; k < c.getNumberOfElements(); k++) {
                    IElement e = c.getElement(k);
                    if(targetClass.isAssignableFrom(e.getClass())) return (CellElt) e;
                }
            }
        }
        return null;
    }
}
