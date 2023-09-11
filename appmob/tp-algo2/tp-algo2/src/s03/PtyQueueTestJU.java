package s03;

import java.util.Random;
import java.util.PriorityQueue;
import org.junit.Test;

import static org.junit.Assert.*;

// ------------------------------------------------------------
public class PtyQueueTestJU {
  // ------------------------------------------------------------
  static class PQElt implements Comparable<PQElt>{
    Integer elt;
    int pty;
    PQElt(int e, int p) { elt=e; pty=p;}
    public int compareTo(PQElt o) {
      if (pty>o.pty) return +1;
      if (pty<o.pty) return -1;
      return 0;
    }
  }
  // ------------------------------------------------------------
  @Test
  public void testPtyQueue() {
    int n=500000;
    checkAssert();
    Random r = new Random();
    int p;
    PriorityQueue<PQElt>      pq1 = new PriorityQueue<PQElt>();
    PtyQueue<Integer,Integer> pq2 = new PtyQueue<>();
    for(int i=0; i<n; i++) {
      if(i%(n/10 +1)==0) System.out.print(".");
      assertTrue(pq1.isEmpty()==pq2.isEmpty());
      if (r.nextBoolean() && !pq1.isEmpty()) {
        assertTrue(pq1.peek().elt.equals(pq2.consult()));     
        assertTrue(pq1.peek().pty==pq2.consultPty());
        Object a = pq1.remove().elt;
        Object b = pq2.dequeue();
        // System.out.println("Dequeued "+a+" and "+b);
        ok (a.equals(b));
      } else {
        p = r.nextInt(n);
        Integer o = Integer.valueOf(-p);
        pq1.add(new PQElt(o, p));
        pq2.enqueue(o, p);
        //System.out.println("Added "+p+" gives "+pq2);
      }
    }
    while(!pq1.isEmpty())
      ok (pq1.remove().elt.equals(pq2.dequeue()));
    System.out.println("Test passed successfully");
  }
  // ------------------------------------------------------------
  public static void checkAssert() {
    int ec=0; assert (ec=1)==1;
    if (ec!=0) return; 
    System.out.println("WARNING: assertions are disabled ('java -ea...')"); 
  }
  // ------------------------------------------------------------
  static void ok(boolean b) {
    assertTrue(b);
  }
  // ------------------------------------------------------------
}
