package s08;
import java.util.BitSet;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

public class SplayTestJU {
  // ------------------------------------------------------------
  @Test
  public void testSplayTree() {
    int n=500;
    for(int i=0; i<100; i++)
      testSet(n);
    System.out.println("\nTest passed successfully");
  }
  // ------------------------------------------------------------
  static boolean log=false;
  // ------------------------------------------------------------
  static void rndAddRm(Random r, SplayTree<Integer> s, BitSet bs, int i) {
    if (r.nextBoolean()) { 
      if (log)
        System.out.println("--add   "+i+": "+s+" // "+bs);
      s.add   (Integer.valueOf(i)); bs.set  (i);
      if (log) {
        System.out.println(s);
        System.out.println("--added "+i+": "+s+" // "+bs);
        if (! areSetEqual(s, bs)) {
          System.out.println("error ! add "+i);
        }
      }
    } else {
      if (log)
        System.out.println("rm    "+i+": "+s+" // "+bs);
      s.remove(Integer.valueOf(i)); bs.clear(i);
      if (log) {
        System.out.println("rmed  "+i+": "+s+" // "+bs);
        if (! areSetEqual(s, bs)) {
          System.out.println("error ! rm "+i);
        }
      }
    }
  }
  // ------------------------------------------------------------
  static boolean areSetEqual(SplayTree<Integer> s, BitSet bs) {
    int l=0;
    for (int i=0; i<bs.length(); i++) {
      if(bs.get(i) != s.contains(Integer.valueOf(i))) {
        System.out.println("SetOf : "+s);
        System.out.println("BitSet: "+bs);
        System.out.println("Size: "+s.size());
        System.out.println("missing element : " +i);
        return false;
      }
      if (s.contains(Integer.valueOf(i)))
        l++;
    }
    if (l != s.size()) {
      System.out.println("SetOf : "+s);
      System.out.println("BitSet: "+bs);
      System.out.println("Size: "+s.size());
      System.out.println("too much elements...");
      return false;
    }
    return true;
  }
  // ------------------------------------------------------------
  // testSet : Simple test method for the Set specification. 
  //           It only verifies that an arbitrary sequence of add/remove
  //           results in a correct set. 
  //     prm : n is the maximum size of the tested set (typically 1000).
  
  public static void testSet(int n) {
    SplayTree<Integer> s = new SplayTree<>();
    int m=2*n;
    BitSet bs = new BitSet();
    Random r = new Random();
    for (int i=0; i<10; i++) {
      assertTrue(areSetEqual(s, bs));
      rndAddRm(r, s, bs, r.nextInt(m));
    }
    while(bs.cardinality()<n) {
      rndAddRm(r, s, bs, r.nextInt(m));
      if (bs.cardinality()%10==0)
      assertTrue(areSetEqual(s, bs));
      int j=r.nextInt(m); s.add(Integer.valueOf(j)); bs.set(j);
      //System.out.print(".");
    }
    assertTrue(areSetEqual(s, bs));
  }
}
