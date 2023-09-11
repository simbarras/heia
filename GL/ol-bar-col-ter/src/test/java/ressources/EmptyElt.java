package ressources;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;

public class EmptyElt extends CellElt {
    public EmptyElt(Board b) {
        super(' ', 1, b);
        code = '#';
    }

    @Override
    public void action() {
        System.out.print("EmptyElt action\n");
    }
}
