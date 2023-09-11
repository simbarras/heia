package ch.epfl.general_libraries.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.NodePair;

public class PathSet implements Iterable<Path> {

	private static class IJPathSet extends TreeSet<Path> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Path first;
	}

	private Matrix<IJPathSet> pathsMatrix;
	private PathFilter filter = null;

	public PathSet(int nodeNumber) {
		this(nodeNumber, null);
	}

	public PathSet(int nodeNumber, PathFilter myFilter) {
		pathsMatrix = new Matrix<IJPathSet>(nodeNumber);
		for (int i = 0 ; i < nodeNumber ; i++) {
			for (int j = 0 ; j < nodeNumber ; j++) {
				pathsMatrix.setMatrixElement(i,j,new IJPathSet());
			}
		}
		this.filter = myFilter;
	}
	
	public NodePair getMostConnectedNodePair() {
		int max = 0;
		int mI = -1;
		int mJ = -1;
		for (int i = 0 ; i < this.getMatrixSize() ; i++) {
			for (int j = 0 ; j < this.getMatrixSize() ; j++) {	
				if (i != j) {
					int m = getPaths(i,j).size();
					if (m > max) {
						max = m;
						mI = i;
						mJ = j;
					}
				}
			}		
		}
		return new NodePair(mI, mJ);
	}

	public void setFilter(PathFilter filter) {
		this.filter = filter;
	}

	@SuppressWarnings("unchecked")
	public Matrix<Set<Path>> getPathsMatrix(){
		return (Matrix<Set<Path>>)(Object)pathsMatrix;
	}

	public Matrix<Integer> getSizeMatrix() {
		int size = pathsMatrix.size();
		Matrix<Integer> sizes = new Matrix<Integer>(size);
		for (int i = 0 ; i < size ; i++) {
			for (int j = 0 ; j < size ; j++) {
				sizes.setMatrixElement(i,j,pathsMatrix.getMatrixElement(i,j).size());
			}
		}
		return sizes;
	}

	public int getMatrixSize() {
		return pathsMatrix.size();
	}

	@SuppressWarnings("unchecked")
	public Iterator<Path> iterator() {
		return pathsMatrix.iteratorOnContent();
	}

	/**
	 * Returns the number of paths contained into this set. WARNING: this do not returns the
	 * size of the matrix. Use getMatrixSize instead
	 * @return
	 */
	public int size() {
		int total = 0;
		for (int i = 0 ; i < getMatrixSize() ; i++) {
			for (int j = 0 ; j < getMatrixSize() ; j++) {
				total += pathsMatrix.getMatrixElement(i,j).size();
			}
		}
		return total;
	}
	
	public Path getPath(NodePair np) {
		return getPath(np.getStartNode(), np.getEndNode());
	}

	public Path getPath(int source, int destination) {
		IJPathSet element = pathsMatrix.getMatrixElement(source, destination);
		return element.first;
		/*		if (element.size() > 0) {
			return element.iterator().next();
		} else {
			return null;
		}*/
	}

	public Path getPathI(int source, int destination, int index) {
		Set<Path> element = pathsMatrix.getMatrixElement(source, destination);
		Path p = null;
		if (element.size() > index) {
			Iterator<Path> ite = element.iterator();
			for (int i = 0 ; i <= index ; i++) {
				p = ite.next();
			}
			return p;
		} else {
			return null;
		}
	}

	public Path getPathUndir(int ext1, int ext2) {
		Set<Path> element = pathsMatrix.getMatrixElementUndir(ext1, ext2);
		if (element.size() > 0) {
			return element.iterator().next();
		} else {
			return null;
		}
	}
	
	public Set<Path> getPaths(NodePair np) {
		return pathsMatrix.getMatrixElement(np.getStartNode(), np.getEndNode());
	}

	public Set<Path> getPaths(int source, int destination){
		return pathsMatrix.getMatrixElement(source,destination);
	}
	
	public List<Path> getPathsAsList(int source, int destination) {
		return new ArrayList<Path>(getPaths(source, destination));
	}

	/*	public Set<Path> getSortedPaths(int source, int destination, PathCalculator pc) {
		Set<Path> l = new TreeSet(pc.getComparator());
		l.addAll(pathsMatrix.getMatrixElement(source,destination));
		return l;
	}*/

	/*** place to simple path abstraction */
	public void setPath(int i, int j, Path p) {
		if (filter(p)) {
			IJPathSet ppp = pathsMatrix.getMatrixElement(i,j);
			ppp.clear();
			ppp.add(p);
			ppp.first = p;
		} else {
			throw new IllegalStateException("Cannot set path " + p + ". Does not match the filter requirements");
		}
	}


	private boolean addPath(int source, int destination, Path p) {
		IJPathSet localList = pathsMatrix.getMatrixElement(source,destination);
		if(filter != null){

			boolean[] ret = filter.filterPrefix(p);

			if(ret[0]){
				localList.add(p);
				if (localList.first == null) {
					localList.first = p;
				}
			}
			return ret[1];
		}
		else{
			localList.add(p);
			if (localList.first == null) {
				localList.first = p;
			}
			return true;
		}
	}

	/**
	 * This methods filters the path p, and depending whether the path
	 * passed the filter, it adds it to the PathSet. It also returns
	 * a boolean mentionning if the given path has been accepted.
	 */
	public boolean addPath(Path p) {
		return addPath(p.getFirst(), p.getLast(), p);
	}
	/**
	 * This methods filters and add (if not filtered) all the paths
	 * contained in the given collection
	 */
	public void addAllPaths(Collection<Path> col) {
		for (Path p : col) {
			addPath(p);
		}
	}

	/**
	 * This methods checks if the path is valid as prefix
	 */
	public boolean filter(Path p) {
		if (filter != null) {
			return filter.filterPrefix(p)[1];
		} else{
			return true;
		}
	}
	
	public boolean[] filterPrefix(Path p) {
		if (filter != null) {
			return filter.filterPrefix(p);
		} else{
			return new boolean[]{true, true};
		}		
	}

	@Override
	public String toString() {
		return pathsMatrix.toString();
	}

	public void setSymmetry() {
		for (int i = 0 ; i < getMatrixSize() ; i++) {
			for (int j = i+1; j < getMatrixSize() ; j++) {
				Path p = getPath(i,j);
				if (p != null) {
					addPath(p.getReversedPath());
				}
			}
		}
	}


}
