package s10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Experiment {
    public static void main(String[] args) {
        // Create a new instance of the class
        // and call the method
        for (int i = 1; i < 5_000_000; i *= 2) {
            Treap<Integer> t = new Treap<>();
            List<Integer> buffer = fillTreap(t, i);
            int[][] stats = statistics(t, buffer, 1000);
            double[] averageAdd = new double[]{(double) stats[0][1] / stats[0][0], (double) stats[1][1] / stats[1][0]};
            double[] averageDel = new double[]{(double) stats[0][3] / stats[0][2], (double) stats[1][3] / stats[1][2]};
            System.out.printf("Treap size: %d\t\n" +
                    "\t When build:\t max depth fund %d\n" +
                    "\t\t add: %d\t rotate: %d\t average: %f\n" +
                    "\t\t del: %d\t rotate: %d\t average: %f\n" +
                    "\t When analysed:\t max depth fund %d\n" +
                    "\t\t add: %d\t rotate: %d\t average: %f\n" +
                    "\t\t del: %d\t rotate: %d\t average: %f\n\n"
                    , stats[0][4],
                    stats[0][5], stats[0][0], stats[0][1], averageAdd[0], stats[0][2], stats[0][3], averageDel[0],
                    stats[1][5], stats[1][0], stats[1][1], averageAdd[1], stats[1][2], stats[1][3], averageDel[1]);
        }
    }

    private static List<Integer> fillTreap(Treap<Integer> t, int size) {
        Random r = new Random();
        List<Integer> buffer = new ArrayList<>();


        while (t.size() < size) {
            int rndElt = r.nextInt();
            t.add(rndElt); // Add a random number
            buffer.add(rndElt);
            if (t.size() % 10 == 0) {
                int rndIdex = r.nextInt(buffer.size());
                rndElt = buffer.get(rndIdex);
                t.remove(rndElt); // remove a random element
                buffer.remove(rndIdex);

                rndElt = r.nextInt();
                t.add(rndElt); // Add a random number
                buffer.add(rndElt);
            }
        }
        return buffer;
    }

    private static int[][] statistics(Treap<Integer> t, List<Integer> buffer, int size) {
        Random r = new Random();
        int[][] stats = new int[2][];
        stats[0] = t.getStats();
        t.resetStats();
        for (int i = 0; i < size; i++) {
            int rndIndex = r.nextInt(buffer.size());
            int rndElt = buffer.get(rndIndex);
            t.remove(rndElt); // remove a random element
            buffer.remove(rndIndex);

            rndElt = r.nextInt();
            t.add(rndElt); // Add a random number
            buffer.add(rndElt);
        }
        stats[1] = t.getStats();
        return stats;
    }
}
