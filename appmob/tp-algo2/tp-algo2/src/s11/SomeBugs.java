package s11;
public class SomeBugs {
  private int attr, unused1;                 // unused attribute
  public static void main(String [] args) {
    int i=0;
    if (i!=0) i++;                           // always false
    for (i=0; i<args.length; i++); {         // bad ';'
      System.out.println(args[i]);           // ... so index-out-bounds
    }
    if (args.length>0 & args[0].length()>2)  // should be '&&'
      i=1;
    if (args[0] == "bad")                    // should be equals()
      i=2;
    while(i<10) {                            // infinite loop
      System.out.print("a");
    }
    System.out.println(idem(true, false));
    i=f(null);
    assert (i++)>0;                          // side-effect assertion
    System.out.println(i);
  }
  // ---------------------------------------
  public boolean equals(Object o) {          // should redefine hashCode()
    this.notify();                           // not owning the monitor 
    return ((SomeBugs)o).attr == attr;       // doesn't respect the specification 
  }
  // ---------------------------------------
  static boolean idem(boolean a, boolean b) {
    int unused2;                             // unused variable
    if (a=b) return true;                    // should be ==
    return false;
  }
  // ---------------------------------------
  private static int f(String s) {
    return s.length();                       // in the only call, s==null
  }
}
