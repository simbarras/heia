package s05;
public class GSTEx1 {
  // ------------------------------------------------------------
  public static void main(String [] args) {
    int [] c = {+1, +2, +3, +4, +5, +6, +7, +8, +9, -1, -2, -3, -4, -5, -6};
    GeneralSearchTreeMap<Integer, Integer> t;
    t = new GeneralSearchTreeMap<>(3);
    for(int i:c) {
      System.out.println("-------------------------  "+i +" :");
      if (i<0) t.remove(-i);
      else     t.put   ( i, i);
      System.out.println(t);
    }
  }
}
