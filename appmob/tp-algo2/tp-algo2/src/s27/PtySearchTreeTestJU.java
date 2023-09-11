package s27;
import java.awt.Point;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class PtySearchTreeTestJU {
  // ------------------------------------------------------------
  @Test public void testPtySearchTree() {
    System.out.println();
    Random r = new Random();
    int queryRepetitions=1;
    for(int i=1; i<100; i++) {
      int x1=r.nextInt(100);   
      int y1=r.nextInt(100);
      int x2=x1+r.nextInt(50); 
      testPtySearchTree(i, x1, y1, x2, queryRepetitions, true);
    }
  }
  // ------------------------------------------------------------
  @Test public void testPtySearchTreePerf() {
    int n=20000; 
    int x1=(int)(Math.sqrt(n)); int y1=x1; 
    int x2=5*x1; 
    int queryRepetitions=1000;
    testPtySearchTree(  n, x1, y1, x2, queryRepetitions, true);
    testPtySearchTree(2*n, x1, y1, x2, queryRepetitions, true);
  }
  // ------------------------------------------------------------
  static List<Point> naiveMatching(Point[] t, int xFrom, int xTo, int yTo) {
    Vector<Point> v= new Vector<>();
    for(Point p:t)
      if (p.y<=yTo && p.x>=xFrom && p.x<=xTo)
        v.add(p);
    return v;
  }
  // ------------------------------------------------------------
  public static void testPtySearchTree(int n, int x1, int y1, int x2,
                                   int queryRepetitions, boolean log) {
    long t1, t2; 
    // -------------------------------------- Random test 
    myPrint(log, "\nFind in range ["+x1+".."+x2+"][-inf.."+y1+"]");
    Random r = new Random();
    Point [] t = rndPointSet(r, n, n);
    myPrint(log, " in " +n+" points of coords [0.."+n+"]");
    myPrint(log, ", "+queryRepetitions+" queries");
    // -------------------------------------- Building
    t1=System.nanoTime();
    PtySearchTree pst = new PtySearchTree(t);
    t2=System.nanoTime();
    myPrint(log, "\n PtySearchTree:  build time [us]= "+(t2-t1)/1000);
    if (n<50)
      myPrint(log, ""+pst);
    // -------------------------------------- Querying
    List<Point> res=null;
    t1=System.nanoTime();
    for(int j=0; j<queryRepetitions; j++)
      res  = pst.search(x1, x2, y1);
    t2=System.nanoTime();
    myPrint(log, " query time [us]="+(t2-t1)/queryRepetitions/1000);
    // -------------------------------------- Comparing results
    int count=0;
    t1=System.nanoTime();
    for(int j=0; j<queryRepetitions; j++)
      count = naiveMatching(t, x1, x2, y1).size();
    t2=System.nanoTime();
    myPrint(log, ". Naive: [us]:"+(t2-t1)/queryRepetitions/1000);
    myPrint(log, ". Found points : "+res.size());
    if (count!=res.size()) System.out.println(count+" "+res.size());
    assertTrue(count==res.size());   
  }
  // ------------------------------------------------------------
  static void myPrint(boolean really, String s) {
    if (!really) return;
    System.out.print(s);
  }
  // ------------------------------------------------------------
  public static Point[] rndPointSet(Random r, int n, int maxCoord) {
    Point[] t = new Point[n];
    Point p;
    HashSet<Point> h = new HashSet<>();
    Iterator<Point> itr;
    while(h.size()<n) {
      p = new Point(r.nextInt(maxCoord), r.nextInt(maxCoord));
      h.add(p);
    }
    itr=h.iterator();
    for (int i=0; i<n; i++) {
      p = (Point)(itr.next());
      t[i] = p;
    }
    return t;
  }
}
