package ch.epfl.general_libraries.path;

import java.util.Comparator;

import ch.epfl.general_libraries.utils.NodePair;

public abstract class PathCalculator {
	public abstract float getPathValue(Path p);
	public abstract float getSegmentValue(int i, int j);
	public abstract Comparator<NodePair> getNodePairComparator();

	public float getTotalValue(PathSet ps) {
		float total = 0;
		for (Path p : ps) {
			total += getPathValue(p);
		}
		return total;
	}
	/**
	 * Returns a comparator sorting the paths according to their length.
	 * The parameter <code>tolerateEqualLength</code> determines if
	 * only the length of the path is important, or if is composing node
	 * can be used to further classify paths. <br>
	 * With <code>tolerateEqualLength = true</code>, if two paths p1 and p2
	 * are or equal length, then the natural order of paths is used, thus
	 * the results of the comparison is <code>p1.compareTo(p2)</code>
	 * With <code>tolerateEqualLength = false</code>, if p1 and p2 are or equals
	 * length, the result of their comparison will be equal to 0
	 */
	public Comparator<Path> getComparator(final boolean tolerateEqualLength) {
		return new Comparator<Path>() {
			public int compare(Path p1, Path p2) {
				float  val1 = getPathValue(p1);
				float  val2 = getPathValue(p2);
				if (val1 < val2) {
					return -1;
				} else if (val1 == val2) {
					if (tolerateEqualLength) {
						return p1.compareTo(p2);
					} else {
						return 0;
					}
				} else {
					return 1;
				}
			}

			/*	public int compare(Object o1, Object o2) {
				return compare((Path)o1,(Path)o2);
			}*/
		};
	}

	public float[][] getRoutingCost(PathSet set) {
		float[][] cost = new float[set.getMatrixSize()][set.getMatrixSize()];
		for (Path p : set) {
			cost[p.getFirst()][p.getLast()] = this.getPathValue(p);
		}
		return cost;
	}

	/**
	 * Returns a comparator sorting the paths according to their length.
	 * The parameter <code>tolerateEqualLength</code> determines if
	 * only the length of the path is important, or if is composing node
	 * can be used to further classify paths. <br>
	 * With <code>tolerateEqualLength = true</code>, if two paths p1 and p2
	 * are or equal length, then the natural order of paths is used, thus
	 * the results of the comparison is <code>p1.compareTo(p2)</code>
	 * With <code>tolerateEqualLength = false</code>, if p1 and p2 are or equals
	 * length, the result of their comparison will be equal to 0
	 */
	public Comparator<Path> getInverseComparator(final boolean tolerateEqualLength) {
		return new Comparator<Path>() {
			public int compare(Path p1, Path p2) {
				float  val1 = getPathValue(p1);
				float  val2 = getPathValue(p2);
				if (val1 > val2) {
					return -1;
				} else if (val1 == val2) {
					if (tolerateEqualLength) {
						return p1.compareTo(p2);
					} else {
						return 0;
					}
				} else {
					return 1;
				}
			}

			/*	public int compare(Object o1, Object o2) {
				return compare((Path)o1,(Path)o2);
			}*/
		};
	}
}
