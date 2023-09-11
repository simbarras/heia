package s24;

import s19.UGraph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

// =========================   // with facet "on the left"
public class HalfEdge implements Comparable<HalfEdge> {
    static final Comparator<Point> VERTICALLY =
            Comparator.<Point>comparingInt(a -> a.y).thenComparingInt(a -> a.x);
    static final Comparator<Point> HORIZONTALLY =
            Comparator.<Point>comparingInt(a -> a.x).thenComparingInt(a -> a.y);

    final Point origin;
    HalfEdge twin, next, prev;

    HalfEdge(Point p) {
        this.origin = p;
    }

    HalfEdge(Point from, Point to) {
        this(from);
        twin = new HalfEdge(to);
        twin.twin = this;
    }

    public void setNext(HalfEdge b) {
        assert (b != null);
        if (b == null) return;
        next = b;
        b.prev = this;
    }

    public List<HalfEdge> ring() {
        ArrayList<HalfEdge> v = new ArrayList<>();
        v.add(this);
        HalfEdge n = next;
        while (!v.contains(n)) {
            v.add(n);
            n = n.next;
        }
        v.add(n);
        return v;
    }

    public String facetString() {
        String res = "";
        List<HalfEdge> v = ring();
        for (HalfEdge he : v) {
            res += "(" + he.origin.x + "," + he.origin.y + ")";
        }
        return res;
    }

    public Point destination() {
        return twin.origin;
    }

    // finds in list a HalfEdge with origin p; null if none
    public HalfEdge reach(Point p) {
        HalfEdge edge = this;
        HalfEdge start = this;
        while (!edge.origin.equals(p)) {
            edge = edge.next;
            if (edge == start) return null;
        }
        return edge;
    }

    public Point[] facetPolygon() {
        List<HalfEdge> v = ring();
        int n = v.size();
        Point[] pts = new Point[n];
        for (int i = 0; i < n; i++) {
            HalfEdge he = v.get(i);
            pts[i] = he.origin;
        }
        return pts;
    }

    @Override
    public int compareTo(HalfEdge o) {
        if (o == null) return -1;
        Point a = origin;
        Point b = o.origin;
        int cmp = HORIZONTALLY.compare(a, b);
        if (cmp != 0) return cmp;
        a = destination();
        b = o.destination();
        cmp = HORIZONTALLY.compare(a, b);
        return cmp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HalfEdge)) return false;
        return compareTo((HalfEdge) o) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * origin.hashCode() + destination().hashCode();
    }

    @Override
    public String toString() {
        Point o = origin;
        Point d = o;
        if (twin != null)
            d = twin.origin;
        String res = "";
        res += "(" + o.x + "," + o.y + ")_(" + d.x + "," + d.y + ")-Facet ";
        res += facetString();
        return res;
    }
}


