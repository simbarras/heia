package ch.epfl.general_libraries.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.general_libraries.utils.NodePair;


public class Path extends ArrayList<Integer> implements Comparable<Path>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int idCount;

	private int id;

	private int start;
	private int end;

	public Path(){
		super();
		id = idCount++;
	}

	public Path(int i) {
		add(i);
	}

	public Path(Path myPath){
		for(int i : myPath){
			add(i);
		}
	}
	
	public Path(NodePair np) {
		this(np.getStartNode(), np.getEndNode());
	}
	
	public Path(int start, int end) {
		this.start = start;
		this.end = end;
		add(start);
		add(end);
	}
	
	public Path(Path p, int suffix) {
		this.addAll(p);
		add(suffix);
		this.start = p.start;
		this.end = suffix;
	}

	public Path(int[] r) {
		for (int i = 0; i < r.length; i++) {
			add(r[i]);
		}
	}

	public Path(int[][] tab) {
		super(tab.length + 1);
		if (tab.length > 0) {
			add(0,tab[0][0]);
			for (int k = 0 ; k < tab.length ; k++) {
				add(k+1,tab[k][1]);
			}
		}
	}

	public Path(String s) {
		super(s.split("[^0-9]+").length);
		String[] vals = s.split("[^0-9]+");
		for (String num : vals) {
			if (num.length() > 0) {
				add(Integer.parseInt(num));
			}
		}
	}

	public Path(List<Integer> myList){
		for(int i : myList){
			add(i);
		}
	}

	/*	private void consistency() {
		if (this.size() == 0) return;
		if (start != this.get(0)) throw new IllegalStateException();
		if (end   != this.get(this.size()-1)) throw new IllegalStateException();
	}*/

	@Override
	public Integer set(int i, Integer j) {
		//	try {
		if (i == 0) {
			start = j;
		}
		if (i == this.size()-1) {
			end = j;
		}
		return super.set(i, j);
		/*	}
		finally {
			consistency();
		}*/
	}

	public void add(int i) {
		if (this.size() == 0) {
			start = i;
		}
		end = i;
		super.add(i);
	}

	@Override
	public boolean add(Integer i) {
		if (this.size() == 0) {
			start = i;
		}
		end = i;
		return super.add(i);
	}
	
	public int getSmaller() {
		if (start < end) return start;
		return end;
	}
	
	public int getGreater() {
		if (start > end) return start;
		return end;
	}

	public int getFirst(){
		/*	int test = super.get(0);
		if (test != start)
			throw new IllegalStateException();*/
		return start;
		//return super.get(0);
	}

	public int getLast(){
		/*	int test = super.get(this.size() -1);
		if (test != end)
			throw new IllegalStateException();*/
		//	return super.get(this.size()-1);
		return end;
	}
	
	public int getPenultimate() {
		return super.get(this.size() - 2);
	}
	
	public int removeFirst() {
		if (size() >= 2) {
			start = get(1);
			return super.remove(0);
		}
		if (size() == 1) {
			start = -1;
			end = -1;
			return super.remove(0);
		}
		throw new ArrayIndexOutOfBoundsException();		
	}

	public int removeLast() {
		if (size() >= 2) {
			end = get(this.size()-2);
			return super.remove(this.size()-1);
		}
		if (size() == 1) {
			start = -1;
			end = -1;
			return super.remove(this.size()-1);
		}
		throw new ArrayIndexOutOfBoundsException();
	}

	public ArrayList<Path> split(Collection<Integer> splittingPoints) {
		ArrayList<Path> splitted = new ArrayList<Path>(3);
		Path newPath = new Path();
		int size = this.size();
		int size1 = size-1;
		for (int i = 0 ; i < size ; i++) {
			Integer point = this.get(i);
			newPath.add(point);
			if (splittingPoints.contains(point) && (newPath.size() > 1))  {
				splitted.add(newPath);
				if (point != this.get(size1)) {
					newPath = new Path();
					newPath.add(point);
				}
			}
		}
		if (splitted.size() == 0) {
			splitted.add(this);
		} else {
			splitted.add(newPath);
		}
		return splitted;
	}

	public List<Path> split(int splittingPoint) {
		ArrayList<Path> splitted = new ArrayList<Path>(3);
		int size = this.size();
		int size1 = size-1;
		Path newPath = new Path();
		for (int i = 0 ; i < size ; i++) {
			Integer point = this.get(i);
			newPath.add(point);
			if ((splittingPoint == point) && (newPath.size() > 1))  {
				splitted.add(newPath);
				if (point != this.get(size1)) {
					newPath = new Path();
					newPath.add(point);
				}
			}
		}
		if (splitted.size() == 0) {
			splitted.add(this);
		} else {
			splitted.add(newPath);
		}
		return splitted;
	}

	public static Path concat(Collection<Path> paths) {
		if (paths.size() == 1) {
			return paths.iterator().next();
		}
		if (paths.size() == 0) {
			return null;
		}
		boolean first = true;
		Path current = null;
		for (Path p : paths) {
			if (first) {
				current = p;
				first = false;
			} else {
				current.concat(p);
			}
		}
		return current;
	}

	public Path concat(Path other) {
		if (this.end == other.start) {
			return concatPrivate(this, other);
		} else if (this.end == other.end) {
			return concatPrivate(this, other.getReversedPath());
		} else if (this.start == other.start) {
			return concatPrivate(this.getReversedPath(), other);
		} else if (this.start == other.end) {
			return concatPrivate(other, this);
		} else {
			throw new IllegalArgumentException("Cannot concat two paths p1 and p2 if last node of p1 differs from first node of p2");
		}

	}

	private Path concatPrivate(Path p1, Path p2) {
		Path p = (Path)p1.clone();
		p.removeLast();
		for (int i = 0 ; i < p2.size() ; i++) {
			p.add(p2.get(i));
		}
		return p;
	}

	public boolean isSharingOneSegment(Path other, boolean directed) {
		for (NodePair tnp : this.getPathSegments()) {
			for (NodePair onp : other.getPathSegments()) {
				if (tnp.equals(onp, directed)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasStartNodeGreaterThanEndNode() {
		return start > end;
	}

	public boolean isSharingOneExtremities(Path other) {
		int thisS = this.getFirst();
		int otherS = other.getFirst();
		if (thisS == otherS) {
			return true;
		}
		int thisL = this.getLast();
		if (thisL == otherS) {
			return true;
		}
		int otherL = other.getLast();
		return (thisS == otherL || thisL == otherL);
	}


	public ArrayList<NodePair> getPathSegments() {
		ArrayList<NodePair> myCollection = new ArrayList<NodePair>(0);
		Iterator<Integer> myIterator = this.iterator();
		if (myIterator.hasNext()) {
			int previous = myIterator.next();
			int current = -1;
			while (myIterator.hasNext()){
				current = myIterator.next();
				NodePair s = new NodePair(previous,current);
				myCollection.add(s);
				previous = current;
			}
		}
		return myCollection;
	}

	public Iterator<Float> pathValuesIterator(final PathCalculator pcalc) {
		return new Iterator<Float>() {
			Iterator<Integer> myIterator = iterator();
			int previous = (myIterator.hasNext()) ? myIterator.next() : -1 ;
			Float next = prepareNext();

			private Float prepareNext() {
				if (myIterator.hasNext() && previous != -1) {
					int current = myIterator.next();
					float toret = pcalc.getSegmentValue(previous, current);
					previous = current;
					return toret;
				}
				return null;
			}

			public boolean hasNext() {
				return (next != null);
			}
			public Float next() {
				float oldNext = next;
				next = prepareNext();
				return oldNext;
			}
			public void remove() {
			}
		};
	}

	public NodePair getExtremities() {
		return new NodePair(getFirst(), getLast());
	}
	
	public Path getInnerNodes() {
		Path p = new Path();
		for (int i = 1 ; i < this.size()-1 ; i++) {
			p.add(get(i));
		}
		return p;
	}

	public Iterator<NodePair> pathSegmentsIterator() {
		return new Iterator<NodePair>() {
			Iterator<Integer> myIterator = iterator();
			int previous = (myIterator.hasNext()) ? myIterator.next() : -1 ;
			NodePair next = prepareNext();

			private NodePair prepareNext() {
				if (myIterator.hasNext() && previous != -1) {
					int current = myIterator.next();
					NodePair toret = new NodePair(previous,current);
					previous = current;
					return toret;
				}
				return null;
			}

			public boolean hasNext() {
				return (next != null);
			}
			public NodePair next() {
				NodePair oldNext = next;
				next = prepareNext();
				return oldNext;
			}
			public void remove() {
			}
		};
	}

	public NodePair getCanonicalExtremitiesNodePair() {
		if (this.getFirst() < this.getLast()) {
			return new NodePair(getFirst(), getLast());
		} else {
			return new NodePair(getLast(), getFirst());
		}
	}

	public ArrayList<NodePair> getCanonicalPathSegments() {
		ArrayList<NodePair> myCollection = new ArrayList<NodePair>(0);
		Iterator<Integer> myIterator = this.iterator();
		if (myIterator.hasNext()) {
			int previous = myIterator.next();
			int current = -1;
			while (myIterator.hasNext()){
				current = myIterator.next();
				NodePair s;
				if (previous < current) {
					s = new NodePair(previous,current);
				} else {
					s = new NodePair(current, previous);
				}
				myCollection.add(s);
				previous = current;
			}
		}
		return myCollection;
	}

	public int[] internalNodesIndexes() {
		int[] ret = new int[size()-2];
		for (int i = 1 ; i < size()-1 ; i++) {
			ret[i] = get(i);
		}
		return ret;
	}

	public int[] allIndexesButFirst() {
		int[] ret = new int[size()-1];
		for (int i = 1 ; i < size() ; i++) {
			ret[i-1] = get(i);
		}
		return ret;
	}
	
	public int[] allIndexes() {
		int[] indexes = new int[size()];
		for (int i = 0 ; i < size() ; i++) {
			indexes[i] = get(i);
		}
		return indexes;		
	}

	public Iterable<Integer> iterateOnInternalNodeIndexes() {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					public int index = 1;
					public Path p = Path.this;
					public Integer next() {
						return p.get(index++);
					}
					public boolean hasNext() {
						return (index < p.size()-1);
					}
					public void remove() {
						throw new IllegalStateException("Not supported");
					}
				};
			}
		};
	}

	public int getPathID(){
		return id;
	}

	public void setPathID(int id_){
		this.id = id_;
	}

	public int compareTo(Path r) {
		//	if (r != null) {
		int tSize = this.size();
		int diff = tSize - r.size();
		if (diff != 0) {
			return diff;
		} else {
			for (int i = 0 ; i < tSize ; i++) {
				diff = this.get(i) - r.get(i);
				if (diff != 0) {
					return diff;
				}
			}
		}
		return 0;
		/*	} else {
    		return 1;
    	}*/
	}

	public void reverse() {

		Integer[] loc = new Integer[this.size()];
		toArray(loc);
		this.clear();
		for (int i = loc.length - 1 ; i >= 0 ; i--) {
			add(loc[i]);
		}
	}

	public Path getReversedPath() {
		Path p = new Path(this);
		p.reverse();
		return p;
	}

	public Set<Integer> getNodeIndexSet() {
		Set<Integer> set = new TreeSet<Integer>();
		for (Integer id : this) {
			set.add(id);
		}
		return set;
	}

	public boolean hasCycle() {
		return removeCyclePrivate(false);
	}

	public boolean removeCycles() {
		return removeCyclePrivate(true);
	}

	private boolean removeCyclePrivate(boolean remove) {
		boolean toReturn = false;
		Set<Integer> visitedSet = new TreeSet<Integer>();
		int i = 0;
		while (i < this.size()) {
			if (visitedSet.contains(this.get(i))) {
				int doublon = this.get(i);
				if (remove == false) {
					return true;
				} else {
					this.remove(i);
					toReturn = true;
					i--;
					// move backward
					while (this.get(i) != doublon) {
						this.remove(i);
						i--;
					}
				}
			} else {
				visitedSet.add(this.get(i));
			}
			i++;
		}
		return toReturn;
	}

	/*   public boolean equalsInMemory(Path r) {
    	return ((this.hashCode() - r.hashCode()) == 0);
    }*/

	public int getNumberOfHops() {
		return size() -1;
	}

	@Override
	public String toString() {
		return super.toString();
	}


}
