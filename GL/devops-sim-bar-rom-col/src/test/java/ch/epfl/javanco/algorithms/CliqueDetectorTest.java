package ch.epfl.javanco.algorithms;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import org.junit.jupiter.api.Test;

public class CliqueDetectorTest {

    @Test
    public void cliqueTest() throws Exception {
        AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
        agh.newLayer("physical");
        for (int i = 0 ; i < 6 ; i++) {
            agh.newNode();
        }
        agh.newLink(0,1);
        agh.newLink(0,2);
        agh.newLink(1,2);
        agh.newLink(2,3);
        agh.newLink(3,4);
        agh.newLink(2,4);
        agh.newLink(2,5);
        agh.newLink(3,5);
        agh.newLink(4,5);
        System.out.println(CliqueDetector.getClique(agh));
    }
}
