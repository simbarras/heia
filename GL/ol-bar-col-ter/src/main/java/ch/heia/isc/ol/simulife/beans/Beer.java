package ch.heia.isc.ol.simulife.beans;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;

public class Beer extends CellElt {

    public Beer(Board b) {
        super('b', 1, b);
    }

    /**
     * Check if Homer is on the same cell and if he is, move in a random cell on the board
     */
    @Override
    public void action() {
        int nbOfElements = board.getICell(this.x, this.y).getNumberOfElements();
        if (nbOfElements > 1) {
            System.out.println("Homer drinks the beer");
            if (board.getICell(this.x, this.y).getTopElement() instanceof Homer) {
                int newX, newY;
                do {
                    newX = random.nextInt(board.getWidth());
                    newY = random.nextInt(board.getHeight());
                } while (board.getICell(newX, newY).getNumberOfElements() != 0);
                board.moveElement(this, newX, newY);
                // Add a cops
                board.addElement(new Cops(board), random.nextInt(board.getWidth()), random.nextInt(board.getHeight()));
            }
        }
    }

    @Override
    public String getIconPath() {
        return "src/ressources/beer.png";
    }
}
