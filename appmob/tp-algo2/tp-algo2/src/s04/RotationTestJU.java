package s04;
import java.util.Random;
import java.util.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

public class RotationTestJU {
  // ------------------------------------------------------------
  @Test
  public void testRotation() {
    int nbOfRotations=10000;
    for(int i=0; i<nbOfRotations; i++) {
      BTree<Integer> t;
      int size=rnd.nextInt(10)+6;
      do t = rndTree(size); while(itrWithRights.isEmpty());
      int ri = rnd.nextInt(itrWithRights.size());
      BTreeItr<Integer> ti = ((itrWithRights.get(ri)));
      String s0 = ti.toReadableString();
      if (i==0) System.out.println("original expected" +t.toReadableString());
      if (i==0) System.out.println("original result" +s0);
      if (i==0) System.out.println("rotated left on "+ti.consult());
      ti.rotateLeft();
      String s1 = ti.toReadableString();
      if (i==0) System.out.println("Expected" + t.toReadableString());
      if (i==0) System.out.println("Result" + s1);
      ti.rotateRight();
      String s2 = ti.toReadableString();
      if (i==0) System.out.println("then right again expected" +t.toReadableString());
      if (i==0) System.out.println("then right again result" +s2);
      assertTrue(s0.equals(s2));
      assertFalse(s0.equals(s1));
    }
  }
  // ------------------------------------------------------------
  private Random rnd = new Random();
  private Vector<BTreeItr<Integer>> itrWithRights;
  // ------------------------------------------------------------
  private void shuffle(int[] t, Random r) {
    for(int i=1; i<t.length; i++) {
      int j = r.nextInt(i);
      int a=t[i]; t[i]=t[j]; t[j]=a;
    }
  }
  // ------------------------------------------------------------
  public BTree<Integer> rndTree(int size) {
    itrWithRights = new Vector<>();
    int i=0;
    BTree<Integer> t = new BTree<>();
    BTreeItr<Integer> ti;
    int [] tab = new int [size];
    for(i=0; i<size; i++) tab[i]=i;
    shuffle(tab, rnd);
    for(i=0; i<size; i++) {
      ti=t.root();
      while(!ti.isBottom()) {
        if (rnd.nextInt(2) == 0) ti=ti.left(); else ti=ti.right();
      }
      ti.insert(Integer.valueOf(tab[i]));
      if (!ti.isRoot() && !ti.isLeftArc()) itrWithRights.add(ti.up());
    }
    return t;
  }
  // ------------------------------------------------------------
}

