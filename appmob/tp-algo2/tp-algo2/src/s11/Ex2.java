package s11;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

public class Ex2 {
  public static void main(String[] args) {
    testAddRemoveGet(2000, 3);
    testAddRemoveGet(2000, 7);
  }
  // ------------------------------------------------------------
  static void rndAddRm(Random r, GSTMap<Integer,Double> s, 
                       TreeMap<Integer,Double> u, int i) {
    if (r.nextBoolean()) { 
      s.put(Integer.valueOf(i), Double.valueOf(i));
      u.put(Integer.valueOf(i), Double.valueOf(i));
    } else {
      s.remove(Integer.valueOf(i));
      u.remove(Integer.valueOf(i));
    }
  }
  // ------------------------------------------------------------
  static boolean areSetEqual(GSTMap<Integer,Double>    s, 
                             TreeMap<Integer,Double> u) {
    int lastKey=10;
    if (!u.isEmpty()) lastKey=u.lastKey();
    for (int i=-5; i<lastKey+5; i++) {
      Double du=u.get(i), ds=(Double)(s.get(i));
      if (ds==null && du==null) continue;
      if (ds==null || du==null) {
        System.out.println("conflicting element : " +i);
        System.out.println(" should be "+ (du==null?"absent":"present"));
        return false;
      }
      if(!u.get(i).equals(s.get(i))) {
        System.out.println("conflicting element : " +i);
        System.out.println(u.get(i));
        System.out.println(s.get(i));
        System.out.println(" (bad key-val pair) ");
        return false;
      }
    }
    return true;
  }
  // ------------------------------------------------------------
  // testAddRemoveGet : Simple test method for the Set specification. 
  //           It only verifies that an arbitrary sequence of add/remove
  //           results in a correct set. 
  //     prm : n is the number of add/remove operations (typically 1000).
  // ------------------------------------------------------------
  public static void testAddRemoveGet(int n, int m) {
    GSTMap<Integer,Double> s;
    TreeMap <Integer,Double> u;
    s = new GSTMap<>(m);
    u = new TreeMap<>();
    Random r = new Random();
    int a=1;
    long t1=System.currentTimeMillis();
    while(System.currentTimeMillis()-t1 < n) {
      rndAddRm(r, s, u, r.nextInt(n));
      if (a%(1+n/10)==0) System.out.print(".");
      if(!areSetEqual(s,u)) System.out.println("bad news");
    }
    System.out.println("set size: "+s.size());
    if (u.size()<10) System.out.println(s);
  }

  
  //==============================================
  static class GSTMap<K extends Comparable<K>, V> {
    final int maxNodeSize;      // should be odd
    private GSTNode<K,V> root;  // root node can be empty
    // ------------------------------------------------------------
    public GSTMap(int maxNbOfChildren) {
      if (maxNbOfChildren%2==1) maxNbOfChildren++;
      maxNodeSize=maxNbOfChildren;
      root= new GSTNode<>();
    }
    // ------------------------------------------------------------
    // returns null if key is not present
    public V get(K key) {
      GSTNode<K,V> n=root;
      int i=0;
      while(!n.isLeaf()) {
        for (i=0; i<n.nbOfKeys(); i++)
          if (n.getKey(i).compareTo(key)>0) break;
        n=n.getSon(i);
      }
      for (i=0; i<n.nbOfKeys(); i++) 
        if (n.getKey(i).equals(key)) return n.getVal(i);
      return null;
    }
    // ------------------------------------------------------------
    // count elements e in [min..max[  : min <= e < max
    public int nbOfKeysInRange(K min, K max) {
      return nbOfKeysInRange(root, min, max);
    }
    // ------------------------------------------------------------
    private int nbOfKeysInRange(GSTNode<K,V> n, K min, K max) {
      int r=0, i=0;
      if (n.isLeaf()) {
        for (i=0; i<n.nbOfKeys(); i++) {
          K k=n.getKey(i);
          if (k.compareTo(min)>=0 && k.compareTo(max)<0) r++;
        }
        return r;
      }
      for (i=0; i<n.nbOfKeys(); i++) {
        K k=n.getKey(i);
        if (k.compareTo(min)<0) continue;
        if (k.compareTo(max)>=0) break;
        r += nbOfKeysInRange(n.getSon(i),min,max);
      }
      r += nbOfKeysInRange(n.getSon(i),min,max);
      return r;
    }
    public void put(K key, V val) {
      root.putFromRoot(key,val); 
    }
    public void remove(K key)  {
      root.removeFromRoot(key);  
    }
    public boolean isEmpty() {
      return root.isEmpty();     
    }
    public int size() {
      return root.size();        
    }
    // ------------------------------------------------------------
    public String toString() { return ""+root;  }
    // =====================================================================
    class GSTNode <K1 extends Comparable<K1>, V1> {
      // ------------------------------------------------------------
      private boolean              isLeaf;
      private Vector<K1>             keys;  //only for leaves
      private Vector<V1>             vals;  //only for leaves
      private Vector<GSTNode<K1,V1>> sons;  //only for non-leaves
      // ------------------------------------------------------------
      public GSTNode() {
        isLeaf=true;
        keys= new Vector<>();
        vals= new Vector<>();
        sons= new Vector<>();
      }
      // ------------------------------------------------------------
      public GSTNode(GSTNode<K1,V1> n, int from, int to) {
        this(); isLeaf=n.isLeaf;
        if (isLeaf) {
          for(int i=from; i<to; i++) {
            keys.add(n.getKey(i)); vals.add(n.getVal(i));
          }
        } else {
          for(int i=from; i<to; i++) // one more son than key !
            sons.add(n.getSon(i));
        }
      }
      // ------------------------------------------------------------
      public GSTNode(K1 key, V1 val) {
        this();
        isLeaf=true;
        keys.add(key); vals.add(val);
      }
      // ------------------------------------------------------------
      public boolean      isLeaf()        { return isLeaf;}
      public V1            getVal(int i)  { return vals.get(i);}
      public GSTNode<K1,V1> getSon(int i) { return sons.get(i);}
      public boolean      isEmpty()       { return size()==0;}
      public int          nbOfSons()      { return isLeaf ? 0:nbOfKeys()+1;}
      // ------------------------------------------------------------
      public int        nbOfKeys()    {
        return isLeaf ? keys.size() : sons.size()-1;
      }
      // ------------------------------------------------------------
      public K1 getKey(int i) {
        if (isLeaf) return keys.get(i);
        return getSon(i+1).minKey();
      }
      // ------------------------------------------------------------
      public boolean containsKey(K1 key) {
        return keys.contains(key);
      }
      // ------------------------------------------------------------ 
      public int size() {
        if (isLeaf()) return keys.size();
        int res=0;
        for (GSTNode<K1,V1> s:sons) res+=s.size();
        return res;
      }
      // ------------------------------------------------------------ 
      private int indexOfFGK(K1 key) {
        int i;
        for (i=0; i<nbOfKeys(); i++){
          if (getKey(i).compareTo(key)>0) break;
        }
        return i;
      }
      // ------------------------------------------------------------  
      private K1 minKey() {
        if (isEmpty()) return null;
        if (isLeaf) return keys.firstElement();
        return sons.firstElement().minKey();
      }
      // ------------------------------------------------------------  
      private K1 maxKey() {
        if (isEmpty()) return null;
        if (isLeaf) return keys.lastElement();
        return sons.lastElement().maxKey();
      }
      // ------------------------------------------------------------  
      private int nbOfKeysOrSons() { return isLeaf?nbOfKeys():nbOfSons();}
      private boolean isFull()     { return nbOfKeysOrSons() >  maxNodeSize;    }
      private boolean isLight()    { return nbOfKeysOrSons() <= maxNodeSize/2;  }
      private boolean canOfferOne(int i) {
        if (i<0 || i>=nbOfSons()) return false;
        return sons.get(i).nbOfKeysOrSons() > maxNodeSize/2 +1;   
      }
      // ------------------------------------------------------------  
      private void putInLeaf(K1 key, V1 val) {
        assert isLeaf;
        if (keys.contains(key)) return;
        int i=0;
        for (i=0; i<keys.size(); i++) {
          int cmp=getKey(i).compareTo(key);
          if (cmp==0) {vals.set(i, val); return; }
          if (cmp>0) break;
        }
        keys.add(i,key); vals.add(i,val);
      }
      // ------------------------------------------------------------  
      private void mergeWithSibling(int i) {
        if (i!=0) i--;
        GSTNode<K1,V1> a=getSon(i);
        GSTNode<K1,V1> b=getSon(i+1);
        assert(a.isLeaf()==b.isLeaf());
        if (a.isLeaf()) {
          a.keys.addAll(b.keys); 
          a.vals.addAll(b.vals);
        } else
          a.sons.addAll(b.sons);
        sons.remove(i+1);
      }
      // ------------------------------------------------------------  
      private void borrowFromLeft(GSTNode<K1,V1> crt, GSTNode<K1,V1> left) {
        if (crt.isLeaf()) {
          int last=left.keys.size()-1;
          K1 k=left.keys.remove(last);
          V1 v=left.vals.remove(last);
          crt.keys.add(0,k); crt.vals.add(0,v);
        } else {
          GSTNode<K1,V1> e= left.sons.remove(left.sons.size()-1);
          crt.sons.add(0,e);
        }
      }
      // ------------------------------------------------------------  
      private void borrowFromRight(GSTNode<K1,V1> crt, GSTNode<K1,V1> right) {
        if (crt.isLeaf()) {
          K1 k=right.keys.remove(0);
          V1 v=right.vals.remove(0);
          crt.keys.add(k); crt.vals.add(v);
        } else {
          GSTNode<K1,V1> e= right.sons.remove(0);
          crt.sons.add(e);
        }
      }
      // ------------------------------------------------------------  
      private boolean isBorrowing(int i) {
        if (canOfferOne(i-1)) {
          borrowFromLeft(sons.get(i), sons.get(i-1));
          return true;
        }
        if (canOfferOne(i+1)) {
          borrowFromRight(sons.get(i), sons.get(i+1));
          return true;
        }
        return false;
      }
      // ------------------------------------------------------------  
      private void removeFromLeaf(K1 key) {
        int i=keys.indexOf(key);
        if (i==-1) return;
        keys.remove(i); vals.remove(i);
      }
      // ------------------------------------------------------------  
      void removeFromRoot(K1 key) {
        remove(key);
        if (isLeaf())     return;
        if (nbOfSons()>1) return;
        GSTNode<K1,V1> us=sons.remove(0);  // skip unique son : height is decreased
        if (us.isLeaf()) {
          isLeaf=true;
          keys.addAll(us.keys);
          vals.addAll(us.vals);
        } else {
          sons.addAll(us.sons);
        }
      }
      // ------------------------------------------------------------  
      private void remove(K1 key) {
        if (isEmpty()) return;
        if (isLeaf()) { removeFromLeaf(key); return; }
        int i=indexOfFGK(key);
        GSTNode<K1,V1> s=sons.get(i);
        s.remove(key);
        if (!s.isLight())    return;
        if (isBorrowing(i))  return; 
        mergeWithSibling(i);
      }
      // ------------------------------------------------------------  
      void putFromRoot(K1 key, V1 val) {
        put(key,val);
        if (isFull()) split();  // height increased !
      }
      // ------------------------------------------------------------  
      private void put(K1 key, V1 val) {
        if (isLeaf) { putInLeaf(key, val); return;}
        assert minKey()!=null && maxKey() !=null;
        //if (key.compareTo(minKey())<0) sons.add(0,new GSTNode<K,V>(key,val));
        //if (key.compareTo(maxKey())>0) sons.add(  new GSTNode<K,V>(key,val));
        int i=indexOfFGK(key);
        GSTNode<K1,V1> s=sons.get(i);
        s.put(key, val);
        if (s.isFull()) {
          s.split();
          sons.remove(i);
          sons.add(i,  s.getSon(0));
          sons.add(i+1,s.getSon(1));
        }
      }
      // ------------------------------------------------------------  
      // GSTNode<K,V> upperHalf() { return null; }
      // ------------------------------------------------------------  
      private void split() {
        int n=nbOfKeys();
        if (!isLeaf) n++;  //one more son...
        int mid=n/2;
        GSTNode<K1,V1> a= new GSTNode<>(this, 0, mid);
        GSTNode<K1,V1> b= new GSTNode<>(this, mid, n);
        keys.clear(); vals.clear();
        isLeaf=false;
        sons.clear();
        sons.add(a); sons.add(b);
      }
      // ------------------------------------------------------------ 
      public String toString() { return toString("");}
      private String toString(String prefix) {
        String s="";
        if (isLeaf) {
          s+=prefix+"LEAF: ";
          for(K1 c:keys) s+=" "+ c;
          s+="\n";
        } else {
          GSTNode<K1,V1> f=sons.firstElement();
          for(GSTNode<K1,V1> g:sons)
            s+= prefix+(g==f?".":"."+g.minKey())+"\n"+g.toString(prefix+"  "); 
          s+= prefix+".\n";
        }
        return s;
      }
      // ------------------------------------------------------------
    }
  }
}
