package s24;


import java.awt.Point;
import java.util.TreeSet;

// =========================
public class Dcel {
    final TreeSet<HalfEdge> edgesSet;  // all HalfEdges
    final TreeSet<Point> pointsSet; // all Points
    final TreeSet<HalfEdge> facetsSet; // subset of edgesSet, one per facet
    HalfEdge outer; // access to the external facet
    // facetsSet only stores "inner" facets

    public Dcel() {
        edgesSet = new TreeSet<>();
        pointsSet = new TreeSet<>(HalfEdge.HORIZONTALLY);
        facetsSet = new TreeSet<>();
    }

    // PRE: simple polygon, in clockwise order, explicitly closed,
    //      at least 3 points (so polyg.length >= 4)
    public Dcel(Point[] polyg) {
        this();
        addFirstPolygon(polyg);
    }

    // PRE : ropePoints forms a valid "ear" of the current Dcel :
    //       given clockwise,
    //       first and last points (no others) are already in the Dcel,
    //       intermediary points are in the outer facet,
    //       the rope does not generate any intersection,
    //       first and last points may be equal
    // POST: the Dcel is updated
    public void addEar(Point[] ropePoints) {
        if (pointsSet.isEmpty()) {
            addFirstPolygon(ropePoints);
            return;
        }
        HalfEdge first = buildRope(ropePoints);
        facetsSet.add(first.twin);
        updateLinks(outer.reach(ropePoints[0]), outer.reach(ropePoints[ropePoints.length - 1]), first);
        outer = first;
    }
    // ------------------------------------------------------------
    // public void     overLay     (Dcel d);
    // ------------------------------------------------------------

    // returns a half-edge with origin p, or null if p is not a vertex
    // of the Dcel
    public HalfEdge incidentEdge(Point p) {
        for (HalfEdge he : edgesSet) {
            if (he.origin.equals(p)) return he;
            //if (he.destination().equals(p)) return he.twin;
        }
        return null;
    }

    public HalfEdge locate(Point p) {
        if (pointsSet.isEmpty()) return null;
        if (pointsSet.contains(p)) return incidentEdge(p);
        for (HalfEdge he : facetsSet) {
            Point[] polyg = he.facetPolygon();
            if (Geom.isInPolygon(polyg, p)) // may or not report points on segments
                return he;
        }
        return outer;
    }

    @Override
    public String toString() {
        String s = "";
        for (HalfEdge he : facetsSet)
            s += "\n*" + he;
        s += "\n*outer:" + outer;
        return s;
    }

    // ------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------
    // creates a list of half-edges, correctly chained, including twins;
    // the last half-edge is connected back to the first (idem for twins);
    // edgesSet/pointsSet are updated
    private HalfEdge buildRope(Point[] pts) {
        HalfEdge first, prev;
        first = new HalfEdge(pts[0], pts[1]);
        prev = first;
        for (int i = 2; i < pts.length; i++) {
            HalfEdge newEdge = new HalfEdge(pts[i - 1], pts[i]);
            prev.setNext(newEdge);
            newEdge.twin.setNext(prev.twin);
            addEdge(prev);
            prev = newEdge;
        }
        if (!pts[0].equals(pts[pts.length - 1])) {
            HalfEdge newEdge = new HalfEdge(pts[pts.length - 1], pts[0]);
            prev.setNext(newEdge);
            newEdge.twin.setNext(prev.twin);
            newEdge.setNext(first);
            first.twin.setNext(newEdge.twin);
        } else {
            prev.setNext(first);
            first.twin.setNext(prev.twin);
        }
        addEdge(prev);
        return first;
    }

    // Connects the rope to the existing DCEL (cf course)
    private static void updateLinks(HalfEdge a, HalfEdge b, HalfEdge c) {
        // Add ear to the DCEL on one point
        HalfEdge cPrev = c.prev.prev;
        a.prev.setNext(c);
        // Add ear to the DCEL on one point
        if (a.equals(b)) {
            c.twin.next.twin.setNext(a);
        } else {
            // Add ear to the DCEL on two point
            c.twin.setNext(a);
            b.prev.setNext(cPrev.twin);
            cPrev.setNext(b);
        }
    }

    // adds the half-edge and its twin in edgesSet, and both extremities
    // in pointsSet
    private void addEdge(HalfEdge he) {
        Point p = he.origin;
        if (!edgesSet.contains(he)) edgesSet.add(he);
        if (!pointsSet.contains(p)) pointsSet.add(p);
        he = he.twin;
        p = he.origin;
        if (!edgesSet.contains(he)) edgesSet.add(he);
        if (!pointsSet.contains(p)) pointsSet.add(p);
    }

    private void addFirstPolygon(Point[] ropePoints) {
        outer = buildRope(ropePoints);
        facetsSet.add(outer.twin);
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    // tiny demo program
    // ------------------------------------------------------------
    static Point[] fromCoords(int[][] c) {
        Point[] p = new Point[c.length];
        for (int i = 0; i < c.length; i++)
            p[i] = new Point(c[i][0], c[i][1]);
        return p;
    }

    private static void testQuery(Point q, Dcel dc, String msg) {
        HalfEdge he = dc.locate(q);
        System.out.print("Query (" + q.x + "," + q.y + ")" + " is ");
        if (he.origin.equals(q)) System.out.println("a vertex");
        else if (he.equals(dc.outer)) System.out.println("outside");
        else System.out.println("inside " + he);
        if (msg.length() != 0) System.out.println("  should be  " + msg);
    }

    private static void mySleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static void okOuter(Dcel dc, int earID, int expectedNbOfEdges) {
        int n = dc.outer.ring().size() - 1;
        if (n == expectedNbOfEdges) return;
        System.out.println("After ear" + earID + ", bad number of edges in outer facet: " + n + ", should be " + expectedNbOfEdges);
        System.exit(-1);
    }

    // ------------------------------------------------------------
    public static void main(String[] args) {
        // should be a small test...
        int[][] ear0 = {{5, 5}, {5, 8}, {8, 8}, {8, 5}, {5, 5}};
        int[][] ear1 = {{5, 5}, {4, 2}, {2, 2}, {2, 4}, {5, 8}};
        int[][] ear2 = {{2, 4}, {0, 6}, {0, 8}, {3, 8}, {2, 4}};

        Point q0 = new Point(7, 6); // in ear0
        Point q1 = new Point(3, 3); // in ear1
        Point q2 = new Point(1, 7); // in ear2
        Point q3 = new Point(5, 1); // outside
        Point q4 = new Point(4, 2); // is a vertex

        Dcel dc = new Dcel(fromCoords(ear0));

        // GUI attempt (may be commented):
        new DcelDisplay(dc);
        System.out.println("With Ear 0 " + dc);
        mySleep(1000);

        okOuter(dc, 0, 4);
        dc.addEar(fromCoords(ear1));
        mySleep(1000);

        System.out.println("With Ear 1" + dc);
        okOuter(dc, 1, 7);
        dc.addEar(fromCoords(ear2));
        mySleep(1000);

        System.out.println("With Ear 2" + dc);
        okOuter(dc, 2, 11);
        mySleep(1000);

        testQuery(q0, dc, "...(5,8)(5,5)...");
        testQuery(q1, dc, "...(4,2)(5,5)...");
        testQuery(q2, dc, "...(0,6)(2,4)...");
        testQuery(q3, dc, "...outside...");
        testQuery(q4, dc, "...vertex...");

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                testQuery(new Point(i, j), dc, "");
    }
}
