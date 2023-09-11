package s22;

import java.awt.Point;

public class Geom {

    public static int signedArea(Point p1, Point p2, Point p3) {
        return ((p2.x - p1.x) * (p3.y - p1.y) - (p3.x - p1.x) * (p2.y - p1.y))/2;
        // negative if clockwise; twice the area of the triangle p1-p2-p3
    }

    public static int ccw(Point p1, Point p2, Point p3) {
        int a = signedArea(p1, p2, p3);
        if (a < 0) return -1;
        if (a > 0) return +1;
        return 0;
    }

    public static boolean intersect(Segm s0, Segm s1) {
        if (((ccw(s0.from(), s0.to(), s1.from())
                * ccw(s0.from(), s0.to(), s1.to())) < 0)
                && ((ccw(s1.from(), s1.to(), s0.from())
                * ccw(s1.from(), s1.to(), s0.to())) < 0)) {
            return true;
        }
        return isOnSegment(s0.from(), s1) || isOnSegment(s0.to(), s1) || isOnSegment(s1.to(), s0) || isOnSegment(s1.from(), s0);
    }

    public static boolean isInLeftAngle(Point query, Point a, Point b, Point c) {
        if (query == a || query == b || query == c) return false;
        if (ccw(a, b, c) > 0) {
            return ccw(b, a, query) < 0 && ccw(b, c, query) > 0;
        } else {
            return ccw(b, a, query) < 0 || ccw(b, c, query) > 0;
        }
    }

    public static boolean isInTriangle(Point query, Point a, Point b, Point c) {
        if (query == a || query == b || query == c) return false;
        if (isInCcwOrder(new Point[]{a, b, c})) {
            return isInLeftAngle(query, a, b, c) && isInLeftAngle(query, b, c, a) && isInLeftAngle(query, c, a, b);
        } else {
            return isInLeftAngle(query, c, b, a) && isInLeftAngle(query, b, a, c) && isInLeftAngle(query, a, c, b);
        }
    }

    public static boolean isOnSegment(Point p, Segm s) {

        if(p == s.from() || p == s.to()) return true;
        // Check if the point is inside the "Square" of the segment
        if (p.x >= Math.min(s.from().x, s.to().x) && p.x <= Math.max(s.from().x, s.to().x) &&
                p.y >= Math.min(s.from().y, s.to().y) && p.y <= Math.max(s.from().y, s.to().y)) {
            return ccw(s.from(), s.to(), p) == 0;
        }
        return false;
    }

    public static boolean isInCcwOrder(Point[] simplePolygon) {
        int max = Integer.MIN_VALUE;
        int indexMax = 0;

        for (int i = 0; i < simplePolygon.length; i++) {
            if (simplePolygon[i].y > max) {
                max = simplePolygon[i].y;
                indexMax = i;
            }
        }

        indexMax += simplePolygon.length;

        return ccw(simplePolygon[(indexMax - 1) % simplePolygon.length], simplePolygon[(indexMax) % simplePolygon.length],
                simplePolygon[(indexMax + 1) % simplePolygon.length]) > 0;
    }

//    public static boolean isInPolygon(Point[] polyg, Point p) {
//        int minY = Integer.MAX_VALUE;
//        for (int i = 0; i < polyg.length; i++) {
//            if (polyg[i].y < minY) {
//                minY = polyg[i].y;
//            }
//        }
//        Segm fil = new Segm(p, new Point(p.x, minY - 1));
//        int counter = 0;
//
//        for (int i = 0, j = polyg.length - 1; i < polyg.length; j = i++) {
//            if (intersect(fil, new Segm(polyg[i], polyg[j]))) {
//                counter++;
//            } else if (p.x == polyg[i].x || p.x == polyg[j].x) {
//                Segm newFil = new Segm(p, new Point(p.x - 1, minY - 1));
//                if (intersect(newFil, new Segm(polyg[i], polyg[j]))) {
//                    counter++;
//                }
//            }
//        }
//        return counter % 2 == 1;
//    }

    public static boolean isInPolygon(Point[] polyg, Point p) {
        int minY = Integer.MAX_VALUE;
        for (int i = 0; i < polyg.length; i++) {
            if (polyg[i].y < minY) {
                minY = polyg[i].y;
            }
        }
        Segm fil = new Segm(p, new Point(p.x, minY - 1));
        int counter = 0;

        for (int i = 0, j = polyg.length - 1; i < polyg.length; j = i++) {
            if (intersect(fil, new Segm(polyg[i], polyg[j]))) {
                counter++;

            }
        }
        return counter % 2 == 1;
    }


    public static boolean isInPolygonPersonal(Point[] polyg, Point p) {

        int i, j = 0;
        int counter = 0;
        // Inizialise j with the last point and i with the first point then j=i and i++
        for (i = 0, j = polyg.length - 1; i < polyg.length; j = i++) {
            if ((polyg[i].x < p.x) != (polyg[j].x < p.x)) {
                if (polyg[i].y >= p.y && polyg[j].y >= p.y) {
                    continue;
                }
                if (polyg[i].y < p.y && polyg[j].y < p.y) {
                    counter++;
                    continue;
                }

                int y = polyg[i].y > polyg[j].y ? polyg[i].y : polyg[j].y;
                int x = polyg[i].y > polyg[j].y ? polyg[j].x : polyg[i].x;
                if (isInTriangle(p, polyg[i], polyg[j], new Point(x, y))) {
                    counter++;
                    continue;
                }

            }
        }


        return counter % 2 == 1;
    }

    public static void main(String[] args) {
        System.out.println("A");
        s22.CG.main(args);
    }

}
