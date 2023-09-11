package ch.heia.isc.ol.simulife.beans;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;

import java.awt.*;

public class TestElement extends CellElt {
    public TestElement(Board b) {
        super('t', 1, b);
        color = Color.red;
    }

    @Override
    public void action() {
        System.out.println("Action");
    }

    @Override
    public String toString() {
        return "#";
    }
}