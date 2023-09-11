package s02;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class IntQueueChainedTestJU {
  @Test
  public void testQueue() {
    int n = 100000;
    Random r = new Random();
    IntQueueChained q = new IntQueueChained();
    IntQueueChained q3 = new IntQueueChained();
    IntQueueChained q2 = new IntQueueChained();
    assertTrue(q.isEmpty());
    assertTrue(q3.isEmpty());
    int m=0; int k=0; int p = 0;
    for(int i=0; i<n; i++) {
      assertTrue(q2.isEmpty());
      boolean doAdd = r.nextBoolean();
      if (doAdd) {
        k++; 
        q.enqueue(k); 
        q3.enqueue(k);
        ok(!q.isEmpty(),  "should be non-empty "+m+" "+k+" "+p+"\n");
        ok(!q3.isEmpty(),  "should be non-empty "+m+" "+k+" "+p+"\n");
        m++;
        //System.out.print("a("+k+")");
      } else {
        if (m==0) {
          ok(q.isEmpty(),  "should be empty "+m+" "+k+" "+p+"\n");
          ok(q3.isEmpty(),  "should be empty "+m+" "+k+" "+p+"\n");
        } else {
          ok( !q.isEmpty(), "should be non-empty "+m+" "+k+" "+p+"\n");
          ok( !q3.isEmpty(), "should be non-empty "+m+" "+k+" "+p+"\n");
          int e = q.dequeue();
          int e3 = q3.dequeue();
          //System.out.print("r("+e+")");
          m--;
          ok( e == p+1, "not FIFO "+m+" "+k+" "+p+"\n");
          ok( e3 == p+1, "not FIFO "+m+" "+k+" "+p+"\n");
          p++;
        }
      }
    }
    System.out.println("Test passed successfully");
  }
  // ------------------------------------------------------------
  static void ok(boolean b, String s) {
    assertTrue(s,b);
  }
}
