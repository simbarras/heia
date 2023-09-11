package s04;
import java.util.BitSet;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

public class BSTTestJU {

  // ------------------------------------------------------------
  @Test
  public void testAddRemoveContains() {
    testBST(1000);
  }
  // ------------------------------------------------------------
  @Test
  public void testConstructor() {
    BST<Integer> t;
    int maxSize=200;
    int offset=10;

    for (int n=0; n<maxSize; n++) {
      Integer[] tab = new Integer[n];
      for(int i=0; i<n; i++) tab[i] = Integer.valueOf(i-offset);
      t = new BST<>(tab);
      assertTrue("problem with size", t.size()==n);
      for(int i=0; i<n; i++) 
        assertTrue("missing elt", t.contains(i-offset));
      assertTrue("unexpected elt", !t.contains(n-offset));
    }
  }
  // ------------------------------------------------------------
  // ------------------------------------------------------------
  static private boolean shouldLog = true;
  // ------------------------------------------------------------
  static private void rndAddRm(Random r, BST<Integer> s, BitSet bs, int i) {
    String log="";
    if (r.nextBoolean()) { 
      s.add   (Integer.valueOf(i)); bs.set  (i);
      log += "\n--- add "+i+"\n";
    } else {
      s.remove(Integer.valueOf(i)); bs.clear(i);
      log += "\n--- remove "+i+"\n";
    }
    if (shouldLog) System.out.println(log);
  }
  // ------------------------------------------------------------
  static private boolean areSetEqual(BST<Integer> s, BitSet bs) {
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
  //     prm : n is the number of add/remove operations (typically 1000).
  
  private static void testBST(int n) {
    BST<Integer> s = new BST<>();
    BitSet bs = new BitSet();
    Random r = new Random();
    int a=1;
    for (a=0; a<n; a++) {
      rndAddRm(r, s, bs, r.nextInt(n));
      if (!shouldLog && a%(n/10)==0) System.out.print(".");
      System.out.println("set size: "+s.size());
      assertTrue( areSetEqual(s, bs) );
    }

  }
}
