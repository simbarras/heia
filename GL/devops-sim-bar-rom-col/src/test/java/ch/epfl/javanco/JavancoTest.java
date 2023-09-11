package ch.epfl.javanco;

import ch.epfl.general_libraries.utils.Counter;
import ch.epfl.general_libraries.utils.Percentage;
import ch.epfl.javanco.graphics.GraphDisplayInformationSet;
import ch.epfl.javanco.graphics.NetworkPainter;
import org.junit.jupiter.api.Test;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.ui.AbstractGraphicalUI;

import java.awt.*;
import java.util.Random;

public class JavancoTest {

    @Test
    public void testNewLink() throws Exception {
        System.out.println(System.getProperty("user.dir"));
        System.out.println("Starting test");
        AbstractGraphHandler agh = getAGH();
        agh.newLayer("test");
        agh.newNode();
        agh.newNode();
        agh.newLink(0, 1);
    }

    @Test
    public void testNewNodesPositions() throws Exception {
        AbstractGraphHandler agh = getAGH();
        agh.activateMainDataHandler();
        agh.activateGraphicalDataHandler();
        agh.newLayer("test");
        agh.newNode(0, 100);
        agh.newNode(0, 200);
        agh.newLink(0, 1);
        LinkContainer link = agh.getLinkContainer(0, 1);
        link.setGeodesicLinkLength(null);
        assert (link.attribute("length").intValue() == 100);
    }

    @Test
    public void testOutputImage() throws Exception {
        AbstractGraphHandler agh = getAGH();
        agh.activateGraphicalDataHandler();
        agh.newLayer("test");

        GraphDisplayInformationSet infoSet = new GraphDisplayInformationSet(agh);
        infoSet.setDisplaySize(new Dimension(200, 200));
        NetworkPainter painter = AbstractGraphicalUI.getDefaultNetworkPainter();


        java.awt.image.BufferedImage o = painter.paintItToImage(infoSet);
        javax.imageio.ImageIO.write(o, "png", new java.io.File("output/test.png"));

        agh.newNode(0, 0);
        agh.newNode(0, 100);
        agh.newNode(0, 300);

        infoSet.setView(-10, -10, 350, 3500);

        o = painter.paintItToImage(infoSet);
        javax.imageio.ImageIO.write(o, "png", new java.io.File("output/test2.png"));
    }

    @Test
    public void testCounter() throws Exception {
        Random rnd = new Random();
        Counter c = new Counter();
        int check = 0;
        for (int i = 0; i < 1000; i++) {
            if (rnd.nextBoolean()) {
                c.increment();
                check++;
            } else {
                c.decrement();
                check--;
            }
            System.out.println(c.toString());
            assert (c.getValue() == check);
            String checkSize = check + "";
            int nbChar = 6 + checkSize.length();
            assert (c.toString().length() == nbChar);
        }
    }

    @Test
    public void testPercent() throws Exception {
        Random rnd = new Random();
        int percent = 0;
        for (int i = 0; i < 1000; i++) {
            percent = rnd.nextInt();
            Percentage  p = new Percentage (percent);
            percent = percent < 0 || percent > 100 ? 0 : percent;
            assert (p.getPercentage() == percent);
        }
    }

    private AbstractGraphHandler getAGH() throws Exception {
        return Javanco.getDefaultGraphHandler(false);
    }

}
