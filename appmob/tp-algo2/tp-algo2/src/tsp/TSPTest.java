package tsp;

import java.util.Random;

// ========================================================================
class TSPTest {

    static private double sqr(double a) {
        return a * a;
    }

    static private double distance(TSPPoint p1, TSPPoint p2) {
        return Math.sqrt(sqr(p1.x - p2.x) + sqr(p1.y - p2.y));
    }

    static private TSPPoint[] generatePoints(int n) {
        TSPPoint[] p = new TSPPoint[n];
        Random r = new Random(15);
        for (int i = 0; i < n; i++) {
            p[i] = new TSPPoint();
            p[i].x = 500 * r.nextFloat();
            p[i].y = 500 * r.nextFloat();
        }
        return p;
    }

    static private double pathLength(TSPPoint[] t, int[] path) {
        if (t.length != path.length) return -1.0;
        double res = 0.0;
        TSPPoint p = t[path[0]];
        boolean[] v = new boolean[t.length];
        v[path[0]] = true;
        TSPPoint a;
        for (int i = 1; i < path.length; i++) {
            if (v[path[i]]) return -1.0;
            a = t[path[i]];
            res += distance(a, p);
            p = a;
            v[path[i]] = true;
        }
        return res;
    }

    static TSP[] loadClasses(int[] algos) {
        TSP[] res = new TSP[algos.length];
        for (int i = 0; i < algos.length; i++) {
            try {
                res[i] = getTSP("tsp.TSP" + algos[i]);
            } catch (Exception e) {
                System.out.println("Problem loading TSP class: " + algos[i]);
            }
        }
        return res;
    }

    static TSP getTSP(String className) throws Exception {
        Class<?> c = Class.forName(className);
        return (TSP) c.getDeclaredConstructor().newInstance();
    }

    //------------------------------------------------------------
    public static void main(String[] args) {
        int n = 20000;
        if (args.length < 1) {
            System.out.println("Usage: TSPTest nbOfCities [algoID...]");
            System.out.println("       ok, let's try with " + n + " cities...");
        } else {
            n = Integer.parseInt(args[0]);
        }
        TSPPoint[] pts = generatePoints(n);
        int[] path = new int[n];
        double res;
        int[] algos = {0, 11, 12, 14, 15, 16, 17, 19, 20, 200, 201, 202};
        //int[] algos = {20, 200, 201, 202};
        TSP[] tspClasses;
        long t1, t2;

        if (args.length > 1) {
            algos = new int[args.length - 1];
            for (int i = 0; i < algos.length; i++)
                algos[i] = Integer.parseInt(args[1 + i]);
        }
        tspClasses = loadClasses(algos);
        System.out.println("n=" + n);
        double firstTime = Double.MAX_VALUE;
        double lowestTime = Double.MAX_VALUE;
        for (int i = 0; i < algos.length; i++) {
            if (tspClasses[i] == null) continue;
            java.util.Arrays.fill(path, 0);
            t1 = System.nanoTime();
            tspClasses[i].salesman(pts, path);   // <------ calling salesman
            t2 = System.nanoTime();
            res = pathLength(pts, path);
            double time = (t2 - t1) / 1000 / 1000;
            System.out.print("" + algos[i] + "\t- running time [ms] = " + time);
            System.out.print(" \t path length = " + res);
            if (i != 0) {
                System.out.print(" \t global progression = " + ((firstTime / time * 100) - 100) + "%");
                System.out.print(" \t step progression = " + ((lowestTime / time * 100) - 100) + "%");
            }
            if (i == 0) {
                firstTime = time;
                lowestTime = time;
            } else if (lowestTime > time) {
                lowestTime = time;
            }
            System.out.println();
      /*     System.out.print  ("path steps : " );
             for(int i=0; i<n; i++)
               System.out.print(" " + path[i]);
             System.out.println();
      */
        }
    }
    //------------------------------------------------------------
}
