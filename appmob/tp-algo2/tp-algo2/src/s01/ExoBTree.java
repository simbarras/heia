package s01;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import btree.BTree;
import btree.BTreeItr;

public class ExoBTree {

  public  static int size(BTree t) {
    return size(t.root());
  }
  
  private static int size(BTreeItr ti) {
    if (ti.isBottom()) return 0;
    return 1+size(ti.left()) +size(ti.right());
  }

  public static int height(BTree t) {
    return height(t.root());
  }

  private static int height(BTreeItr bt){
    if (bt == null) return 0;
    if (bt.isBottom()) return 0;
    return 1 + max(height(bt.left()), height(bt.right()));
  }

  private static int max(int a, int b){
    return a < b ? b : a;
  }
  
  //-----------------------------------------------------------------
  // --- Small test program
  //-----------------------------------------------------------------

  private static Random rnd = new Random();

  public static BTree rndTree(int size) {
    int i=0;
    BTree t = new BTree();
    BTreeItr ti;
    List<Integer> list=new ArrayList<>();
    for(i=0; i<size; i++) list.add(i);
    Collections.shuffle(list, rnd);
    for(int e:list) {
      ti=t.root();
      while(!ti.isBottom()) {
        if (rnd.nextInt(2) == 0) ti=ti.left(); else ti=ti.right();
      }
      ti.insert(e);
    }
    return t;
  }

  public static void checkAssert() {
    int ec=0; assert (ec=1)==1;
    if (ec!=0) return; 
    System.out.println("WARNING: assertions are disabled ('java -ea...')"); 
  }

  public static void main(String [] args) {
    checkAssert();
    int nbOfNodes = 10;
    if (args.length > 0)
      nbOfNodes = Integer.parseInt(args[0]);
    BTree t = rndTree(nbOfNodes);
    System.out.println("Tree:" +t);
    System.out.println(t.toReadableString());
    System.out.println("  Size: " + size(t));
    System.out.println("Height: " + height(t));
  }

}

