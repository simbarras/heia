package s27;

import java.awt.Point;
import java.util.*;

public class PtySearchTree {
    private static final Comparator<Point> VERTICALLY =
            Comparator.<Point>comparingInt(a -> a.y).thenComparingInt(a -> a.x);
    private static final Comparator<Point> HORIZONTALLY =
            Comparator.<Point>comparingInt(a -> a.x).thenComparingInt(a -> a.y);

    //============================================================
    static class PtySearchElt {
        final Point ptWithXmedian;
        final Point ptWithYmin;

        public PtySearchElt(Point xMed, Point point) {
            ptWithXmedian = xMed;
            ptWithYmin = point;
        }

        @Override
        public String toString() {
            return "" + ptWithXmedian.x + "-" + ptWithYmin.x + "-" + ptWithYmin.y;
        }
    }

    //============================================================
    private final BTree<PtySearchElt> tree;

    public PtySearchTree(Point[] points) {
        List<Point> v = new ArrayList<>();
        v.addAll(Arrays.asList(points));
        v.sort(VERTICALLY);
        tree = build(v);
    }

    public List<Point> search(int xFrom, int xTo, int yTo) {
        List<Point> v = new ArrayList<>();
        search(tree.root(), xFrom, xTo, yTo, v);
        return v;
    }

    public String toString() {
        return tree.toReadableString();
    }

    private static List<Point> pointsBefore(List<Point> xP, Point xMid) {
        List<Point> v = new ArrayList<>();
        for (Point p : xP) {
            if (HORIZONTALLY.compare(p, xMid) <= 0)
                v.add(p);
        }
        return v;
    }

    private static void search(BTreeItr<PtySearchElt> z, int xFrom, int xTo, int yTo,
                               List<Point> result) {
        if (z.isBottom()) return;
        PtySearchElt e = z.consult();

        if (e.ptWithYmin.y > yTo) return;

        if (xFrom <= e.ptWithYmin.x && e.ptWithYmin.x <= xTo) {
            result.add(e.ptWithYmin);
        }

        if (xFrom <= e.ptWithXmedian.x) {
            search(z.left(), xFrom, xTo, yTo, result);
        }

        if (e.ptWithXmedian.x <= xTo) {
            search(z.right(), xFrom, xTo, yTo, result);
        }

    }


    private static BTree<PtySearchElt> build(List<Point> vertSorted) {
        assert (isMonoton(vertSorted, VERTICALLY));
        BTree<PtySearchElt> r = new BTree<>();
        BTreeItr<PtySearchElt> ri = r.root();
        if (vertSorted.size() == 0) return r;
        if (vertSorted.size() == 1) {
            ri.insert(new PtySearchElt(vertSorted.get(0), vertSorted.get(0)));
            return r;
        }

        Point yMin = vertSorted.remove(0);
        vertSorted.sort(HORIZONTALLY);
        Point xMedP = vertSorted.get((vertSorted.size() - 1) / 2);
        int xMed = xMedP.x;
        ri.insert(new PtySearchElt(xMedP, yMin));

        // Split the vertSortedX into two parts
        List<Point> listLeft = new ArrayList<>();
        List<Point> listRight = new ArrayList<>();
        for (Point p : vertSorted) {
            if (p.x <= xMed) {
                listLeft.add(p);
            } else {
                listRight.add(p);
            }
        }

        listLeft.sort(VERTICALLY);
        listRight.sort(VERTICALLY);
        ri.left().paste(build(listLeft));
        ri.right().paste(build(listRight));

        return r;
    }


    private static boolean isMonoton(List<Point> v, Comparator<Point> c) {
        boolean ok = true;
        for (int i = 1; i < v.size(); i++) {
            ok = ok && c.compare(v.get(i - 1), v.get(i)) <= 0;
        }
        return ok;
    }

}
