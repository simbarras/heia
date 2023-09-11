package s10;

import java.util.BitSet;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class TreapTestJU {
  // ------------------------------------------------------------
  @Test
  public void testTreap() {
    int n=2000;
    testSet(n);
  }
  // ------------------------------------------------------------
  static void rndAddRm(Random r, Treap<Integer> s, BitSet bs, int i) {
    if (r.nextBoolean()) { 
      s.add(i); bs.set(i);
      //System.out.println("add "+i+": "+s+" // "+bs);
      //if (! areSetEqual(s, bs)) {
      //System.out.println("oh ! add "+i);
      //}
    } else {
      s.remove(i); bs.clear(i);
      //System.out.println("rm  "+i+": "+s+" // "+bs);
      //if (! areSetEqual(s, bs)) {
      //System.out.println("oh ! rm "+i);
      //}
    }
  }
  // ------------------------------------------------------------
  static boolean areSetEqual(Treap<Integer> s, BitSet bs) {
    int l=0;
    for (int i=0; i<bs.length(); i++) {
      if(bs.get(i) != s.contains(Integer.valueOf(i))) {
        System.out.println("SetOf : "+s);
        System.out.println("BitSet: "+bs);
        System.out.println("Size: "+s.size());
        System.out.println("missing element : " +i);
        return false;
      }
      if (s.contains(i))
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
    Treap<Integer> s = new Treap<>();
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
      int j=r.nextInt(m); 
      s.add(j); 
      bs.set(j);
      //System.out.print(".");
    }
    assertTrue(areSetEqual(s, bs));
  }
  // ------------------------------------------------------------
}
