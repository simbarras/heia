package s02;

import org.junit.Test;
import s02.BTree;
import s02.BTreeItr;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class BTreeTestJU {

  @Test
  public void testBTree() {
    checkAssert();
    int nbOfNodes = 20;
    int nbOfOps = 50000;
    int nRepetitions = 10;
    for(int i=0; i<nRepetitions; i++)
      testBTree(nbOfNodes, nbOfOps);
    System.out.println("Test passed successfully...");
  }

  // ------------------------------------------------------------
  private static Random rnd = new Random();

  static s02.BTree t1, t2;
  static btree.BTree t5, t6;
  static s02.BTreeItr ti;
  static btree.BTreeItr tj;
  static boolean toStrFlag;
  // ------------------------------------------------------------
  static void compareTrees() {
    ok(t1.isEmpty()==t5.isEmpty(), "isEmpty should be "+t5.isEmpty());
    ok(t2.isEmpty()==t6.isEmpty(), "isEmpty should be "+t6.isEmpty());
    ok(ti.hasLeft()==tj.hasLeft(), "hasLeft should be "+tj.hasLeft());
    ok(ti.hasRight()==tj.hasRight(), "hasRight should be "+tj.hasRight());
    ok(ti.isBottom()==tj.isBottom(), "isBottom should be "+tj.isBottom());
    ok(ti.isLeafNode()==tj.isLeafNode(),"isLeafNode should be "+tj.isLeafNode());
    if (!tj.isBottom())
      ok(ti.consult()==tj.consult(), "consult should be "+tj.consult());
    if (!tj.isRoot())
      ok(ti.isLeftArc()==tj.isLeftArc(), "isLeftArc should be "+tj.isLeftArc());
    toStrFlag &= t1.toString().equals(t5.toString());
    //System.out.println(t1.toString() + " <-> " + t5.toString());
  }

  static void testBTree(int nbOfNodes, int nbOfOps) {
    toStrFlag=true;
    Integer elt;
    t1 = new s02.BTree(); t5 = new btree.BTree();
    ti = t1.root();   tj = t5.root();
    buildRndTrees(nbOfNodes);
    while (nbOfOps-- > 0) {
      int r = rnd.nextInt(9);
      //System.out.println(r);
      switch (r) {
        case 0: // up
          if (tj.isRoot()) continue;
          ti=ti.up(); tj=tj.up();
          break;
        case 1: // left
          if (tj.isBottom()) continue;
          ti=ti.left(); tj=tj.left();
          break;
        case 2: // right
          if (tj.isBottom()) continue;
          ti=ti.right(); tj=tj.right();
          break;
        case 3: // update
          if (tj.isBottom()) continue;
          elt = nbOfOps;
          ti.update(elt); tj.update(elt);
          break;
        case 4: // insert
          elt = nbOfOps;
          ti.insert(elt); tj.insert(elt);
          break;
        case 5: // cut
          t2 = ti.cut(); t6 = tj.cut();
          break;
        case 6: // paste
          ti.paste(t2); tj.paste(t6);
          break;
        case 7: // new trees for t2/t6
          buildRndTrees(nbOfNodes);
          break;
        case 8: // root()
          ti = ti.whole().root(); tj = tj.whole().root();
      }
      compareTrees();
    }
    assertTrue("minor problem with toString", toStrFlag);
  }

  private static void shuffle(int[] t, Random r) {
    for(int i=1; i<t.length; i++) {
      int j = r.nextInt(i);
      int a=t[i]; t[i]=t[j]; t[j]=a;
    }
  }

  // Assigns t2 and t6 the same rnd tree
  public static void buildRndTrees(int size) {
    int i=0;
    s02.BTree t = new BTree();
    btree.BTree u = new btree.BTree();
    BTreeItr a;
    btree.BTreeItr b;
    int [] tab = new int [size];
    for(i=0; i<size; i++) tab[i]=i;
    shuffle(tab, rnd);
    for(i=0; i<size; i++) {
      a=t.root(); b=u.root();
      while(!a.isBottom()) {
        if (rnd.nextBoolean()) { a=a.left();  b=b.left(); }
        else                   { a=a.right(); b=b.right();}
      }
      Integer elt = tab[i];
      a.insert(elt); b.insert(elt);
    }
    t2=t; t6=u;
  }

  public static void ok(boolean b, String msg) {
    assertTrue(msg, b);
  }

  public static void checkAssert() {
    int ec=0; assert (ec=1)==1;
    if (ec!=0) return; 
    System.out.println("WARNING: assertions are disabled ('java -ea...')"); 
  }
}

