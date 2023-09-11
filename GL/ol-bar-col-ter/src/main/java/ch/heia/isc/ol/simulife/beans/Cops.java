package ch.heia.isc.ol.simulife.beans;

import ch.heia.isc.ol.simulife.factory.SpikeStripFactory;
import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;
import ch.heia.isc.ol.simulife.model.MobileElt;
import ch.heia.isc.ol.simulife.strategies.MoveToHomer;

import java.awt.*;

public class Cops extends MobileElt {

    public static final int SPIKE_STRIP_CHANCE = 10;
    private static final SpikeStripFactory SPIKE_STRIP_FACTORY = new SpikeStripFactory();

    public Cops(Board b) {
        super('c', 3, b);
        this.setColor(Color.blue);
        setStrategy(MoveToHomer.getInstance());
    }

    /**
     * Move the cops in the direction of Homer or add a spike strip (10% chance)
     */
    @Override
    public void action() {
        // 10% de chance de créer une herse
        if (random() == 0) {
            SPIKE_STRIP_FACTORY.createAndAddElement(board, this.x, this.y);
            System.out.println("Spikes added at " + this.x + ", " + this.y);
        }
        CellElt target = searchTarget(Homer.class);

        if (moveTo(target)) {
            // CHeck if the cop is on the same cell as homer
            if (board.getICell(this.x, this.y).getElement(0) instanceof Homer) {
                System.out.println("Homer est mort: Finir le jeu");
                needToFinish = true;

            }
            System.out.println("Cops: C'est tout bon, je me déplace");
        }
    }

    private int random() {
        return random.nextInt(SPIKE_STRIP_CHANCE);
    }

    @Override
    public String getIconPath() {
        return "src/ressources/cops.png";
    }
}
