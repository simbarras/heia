package s08;
import java.util.HashSet;
import java.util.TreeSet;


public class SplayTreePerfTest {
  //=======================================================================
  static interface SimpleSet<E extends Comparable<E>> {
    void myAdd(E e);
    void myRemove(E e);
    boolean contains(E e);
    int size();
  }
  static class MySplayTree<E extends Comparable<E>> extends SplayTree<E> implements SimpleSet<E> {
    @Override public void myAdd(E e)       { super.add(e); }
    @Override public void myRemove(E e)    { super.remove(e); }
  }
  static class  StdTreeSet<E extends Comparable<E>> extends TreeSet<E> implements SimpleSet<E> {
    @Override public void myAdd(E e)       { super.add(e); }
    @Override public void myRemove(E e)    { super.remove(e); }
    @Override public boolean contains(E e) { return super.contains(e); }
  }
  static class  StdHashSet<E extends Comparable<E>> extends HashSet<E> implements SimpleSet<E> {
    @Override public void myAdd(E e)       { super.add(e); }
    @Override public void myRemove(E e)    { super.remove(e); }
    @Override public boolean contains(E e) { return super.contains(e); }
  }

  //=======================================================================

  public static void perf() {
    int n=20000;
    perfExperiment(new StdTreeSet<>(), n);
    perfExperiment(new StdTreeSet<>(), 2*n);
    System.out.println();
    perfExperiment(new StdHashSet<>(), n);
    perfExperiment(new StdHashSet<>(), 2*n);
    System.out.println();
    perfExperiment(new MySplayTree<>(), n);
    perfExperiment(new MySplayTree<>(), 2*n);
  }
  
  private static void perfExperiment(SimpleSet<Integer> s, int n) {
    for(int i=0; i<2*n; i+=2)
      s.myAdd(i);
    System.out.print(s.getClass().getSimpleName());
    System.out.println(" of size: "+s.size());
    callManyTimesContains(s, false);
    callManyTimesContains(s, true);
    callManyTimesContains(s, false);
    callManyTimesContains(s, true);
  }
  
  private static void callManyTimesContains(SimpleSet<Integer> s, boolean use2080) {
      int nRepetitions=100;
      int n= s.size();
      String use=use2080 ? "    20/80 use":"Non-20/80 use";
      int divider=use2080 ? 10 : n;

      long t0, t1;
      boolean dummy=true;
      double nCalls;
      t0=System.nanoTime();
      for(int j=0; j<nRepetitions/2; j++)
        for(int i=0; i<n; i++) 
            dummy ^=s.contains(i%divider);
      t1=System.nanoTime();
      nCalls=nRepetitions*n;
      System.out.println("  "+use+", time per call to contains() [ns]: "+(t1-t0)/nCalls);
      if(dummy) System.out.flush();  //ensures that the VM optimizer is not too aggressive...
  }

  //--------------------------------------------------------------------
  public static void main(String[] args) {
    perf();
  }
}
