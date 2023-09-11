
package s01;

import btree.BTree;
import btree.BTreeItr;

public class BreadthFirst {

  public static void visitLevel(BTree t, int level) {
    visitLevel(t.root(), level);
  }
  
  private static void visitLevel(BTreeItr ti, int level) {
    if (ti.isBottom ()) return;
    if (level==0) {
      System.out.print (" " + ti.consult());
      return;
    }
    visitLevel(ti.left(),  level-1);
    visitLevel(ti.right(), level-1);
    int a = 1;
  }

  public static void breadthFirstR(BTree t) {
    int d = ExoBTree.height(t);
    for (int l=0; l<=d; l++) { 
      visitLevel(t, l);
    }
  }
  
  public static void breadthFirstQ(BTree t) {
    BTreeItr crt; 
    Queue q = new Queue();
    q.enqueue(t.root());
    while(! q.isEmpty()) {
      crt = (BTreeItr) (q.dequeue()); 
      if (crt.isBottom ()) continue;
      System.out.print(" "+crt.consult()); 
      q.enqueue(crt.left ());
      q.enqueue(crt.right());
    }
  }
  
  public static void main(String[] args) {
    BTree t=ExoBTree.rndTree(10);
    breadthFirstQ(t);
    System.out.println();
    breadthFirstR(t);
    System.out.println();
  }
  
  //===========================================================
  static class Queue {
    static class QueueNode {
      Object e;
      QueueNode prev = null;
      QueueNode(Object elt) {e=elt;}
    }
    //==============================
    QueueNode front = null;
    QueueNode back = null;

    public Queue() {};
    public void enqueue (Object elt) {
      QueueNode aux = new QueueNode(elt);
      if (back==null) {
        back = aux; front = aux;
      } else {
        back.prev = aux;
        back = aux;
      } 
    }

    public boolean isEmpty() { return back==null; }

    public Object consult() { return front.e; }

    public Object dequeue() {
      Object e = front.e;
      if (front == back) {
        back = null; front = null;
      } else {
        front = front.prev;
      }
      return e;
    }
  }
}
