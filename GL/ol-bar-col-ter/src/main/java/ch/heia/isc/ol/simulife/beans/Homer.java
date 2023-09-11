package ch.heia.isc.ol.simulife.beans;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;
import ch.heia.isc.ol.simulife.model.MobileElt;
import ch.heia.isc.ol.simulife.strategies.MoveToBeer;

public class Homer extends MobileElt {

    public Homer(Board b) {
        super('h', 4, b);
        setStrategy(MoveToBeer.getInstance());
    }

    /**
     * Move the element in the direction of the beer
     */
    @Override
    public void action() {
        CellElt target = searchTarget(Beer.class);
        if (!moveTo(target)) {
            System.out.println("Finir le jeu");
            needToFinish = true;
        }
    }

    @Override
    public String getIconPath() {
        return "src/ressources/homer.png";
    }
}
