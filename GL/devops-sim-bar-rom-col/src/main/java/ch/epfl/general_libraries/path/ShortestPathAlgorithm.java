package ch.epfl.general_libraries.path;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import ch.epfl.general_libraries.utils.BoxedPriorityQueue;
import ch.epfl.general_libraries.utils.NodePair;

public class ShortestPathAlgorithm implements Iterable<Path> {

//	private int[][] p;
	private int[][] n;
	private double[][] v;
	private int[][] h;
	protected boolean[][] done;

	private TreeMap<NodePair,Double> disabled;	

	private boolean initialised = false;

	protected double[][] incidenceMatrix;
	protected boolean directed = false;
	protected boolean isAllComputed = false;
	protected int maxNodeIndex;


	protected ShortestPathAlgorithm() {}

	public ShortestPathAlgorithm(boolean[][] matrix) {	
		this(matrix, false);
	}

	public ShortestPathAlgorithm(boolean[][] matrix, boolean directed) {
		this.directed = directed;
		initialise(matrix.length);
		for (int i = 0 ; i < maxNodeIndex ; i++) {			
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				incidenceMatrix[i][j] = matrix[i][j] ? 1 : -1;
				if (matrix[i][j] != matrix[j][i] && !directed) {
					throw new IllegalStateException("Undirected matrixes must be symmetric");
				}
			}
		}		
	}

	public ShortestPathAlgorithm(double[][] matrix) {
		this(matrix, false);
	}

	public ShortestPathAlgorithm(double[][] matrix, boolean directed) {
		this.directed = directed;
		initialise(matrix.length);
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				incidenceMatrix[i][j] = matrix[i][j];
				if (matrix[i][j] != matrix[j][i] && !directed) {
					throw new IllegalStateException("Undirected matrixes must be symmetric");
				}
			}
		}
	}	

	public ShortestPathAlgorithm(float[][] matrix) {
		this(matrix, false);
	}

	public ShortestPathAlgorithm(float[][] matrix, boolean directed) {
		this.directed = directed;
		initialise(matrix.length);
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				incidenceMatrix[i][j] = matrix[i][j];
				if (matrix[i][j] != matrix[j][i] && !directed) {
					throw new IllegalStateException("Undirected matrixes must be symmetric");
				}
			}
		}		
	}

	public ShortestPathAlgorithm(int[][] matrix) {
		this(matrix, false);
	}

	public ShortestPathAlgorithm(int[][] matrix, boolean directed) {
		this.directed = directed;
		initialise(matrix.length);
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				incidenceMatrix[i][j] = matrix[i][j];
				if (matrix[i][j] != matrix[j][i] && !directed) {
					throw new IllegalStateException("Undirected matrixes must be symmetric");
				}
			}
		}						
	}	

	protected void initialise(int maxNodeIndex) {
		this.disabled = new TreeMap<NodePair, Double>();	
		v = new double[maxNodeIndex][maxNodeIndex];
	//	p = new int[maxNodeIndex][maxNodeIndex];
		n = new int[maxNodeIndex][maxNodeIndex];
		h = new int[maxNodeIndex][maxNodeIndex];
		done = new boolean[maxNodeIndex][maxNodeIndex];
		incidenceMatrix = new double[maxNodeIndex][maxNodeIndex];

		// initialisation of incidence matrix and completion matric
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				done[i][j] = false;
				incidenceMatrix[i][j] = -1;
				v[i][j] = Float.POSITIVE_INFINITY;
				h[i][j] = -1;
			}
			incidenceMatrix[i][i] = 0;
		//	p[i][i] = i;
			n[i][i] = i;
			v[i][i] = 0;
			h[i][i] = 0;
			done[i][i] = true;
		}
		initialised = true;
		this.maxNodeIndex = maxNodeIndex;
	}

	private void checkInitialise() {
		if (!initialised) {
			throw new IllegalStateException("Use of shortest path algorithm without prior initialisation");
		}
	}

	private int[] getConnectedLinks(int ext) {
		int[] tab = new int[incidenceMatrix.length];
		int index = 0;
		for (int i = 0 ; i < incidenceMatrix.length ; i++) {
			if ((incidenceMatrix[ext][i] >= 0) || (incidenceMatrix[i][ext] >= 0)) {
				tab[index] = i;
				index++;
			}
		}
		int[] returned = new int[index];
		System.arraycopy(tab, 0, returned, 0, index);
		return returned;
	}

	private int[] getOutgoingLinks(int ext) {
		int[] tab = new int[incidenceMatrix.length];
		int index = 0;
		for (int i = 0 ; i < incidenceMatrix.length ; i++) {
			if ((incidenceMatrix[ext][i] >= 0)) {
				tab[index] = i;
				index++;
			}
		}
		int[] returned = new int[index];
		System.arraycopy(tab, 0, returned, 0, index);
		return returned;
	}

	public void setUseDirected(boolean b) {
		directed = b;
	}
	public boolean isUsingDirected() {
		return directed;
	}

	public void disableLink(int a, int b) {
		disabled.put(new NodePair(a, b), incidenceMatrix[a][b]);
		incidenceMatrix[a][b] = -1;
		if (!directed){
			disabled.put(new NodePair(b, a), incidenceMatrix[b][a]);
			incidenceMatrix[b][a] = -1;
		}
		isAllComputed = false;
		markAllUndone();
	}

	public void restore() {
		int s = disabled.size();
		for(int i = 0; i < s; i++){
			NodePair np = disabled.firstKey();
			double incidence = disabled.get(np);
			incidenceMatrix[np.getStartNode()][np.getEndNode()] = incidence;
			disabled.remove(np);
		}

		if (disabled.size()!=0){
			throw new IllegalStateException("The netwrok has not been restored correctly");
		}
		isAllComputed = false;
		markAllUndone();
	}	

	private void markAllDone() {
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			Arrays.fill(done[i], true);
		}
	}

	private void markAllUndone() {
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			Arrays.fill(done[i], false);
		}
	}	

	public void computeAll() {
		checkInitialise();
		// floyd algorithm
		if (isAllComputed == false) {
			// init
			for (int i = 0 ; i < maxNodeIndex ; i++) {
				for (int j = 0 ; j < maxNodeIndex ; j++) {
					if (i == j) {
						v[i][j] = 0;
					//	p[i][i] = i;
						n[i][i] = i;
						h[i][j] = 0;
					} else {
						v[i][j] = Double.POSITIVE_INFINITY;
					//	p[i][j] = -1;
						n[i][j] = -1;
						h[i][j] = -1;
					}

				}
			}
			//
			for (int i = 0 ; i < maxNodeIndex ; i++) {
				int[] ll;
				if (directed) {
					ll = getOutgoingLinks(i);
				} else {
					ll = getConnectedLinks(i);
				}
				if (ll != null) {
					for (int j : ll) {
						if ((incidenceMatrix[i][j] >= 0) && (i != j)) {
							v[i][j] = incidenceMatrix[i][j];
						//	p[i][j] = i;
							n[i][j] = j;
							h[i][j] = 1;
						}
					}
				}
			}
			//
			for (int k = 0 ; k < maxNodeIndex ; k++) {
				for (int i = 0 ; i < maxNodeIndex ; i++) {
					for (int j = 0 ; j < maxNodeIndex ; j++) {
						if ((v[i][k] < Double.POSITIVE_INFINITY) &&
								(v[k][j] < Double.POSITIVE_INFINITY) &&
								(v[i][k] + v[k][j] < v[i][j])) {
							v[i][j] = v[i][k] + v[k][j];
						//	p[i][j] = p[k][j];
							n[i][j] = n[i][k];
							h[i][j] = h[i][k] + h[k][j];
						}
					}
				}
			}
			isAllComputed = true;
			markAllDone();
		}
	}

	boolean maskUndone = true;

	public int[][] getSuccessors() {
		return n;
	}

	public double[][] getDistances() {
		return v;
	}
	
	public int[][] getHops() {
		return h;
	}
	/*
	public double[] getDistancesFrom(int startNode) {
		return v[startNode];
	}
	public double[] getDistancesTo(int destNode) {
		double[] array = new double[maxNodeIndex];
		for ( int i = 0 ; i < maxNodeIndex ; i++) {
			array[maxNodeIndex] = v[i][destNode];
		}
		return array;
	}
	*/
	public double getDistance(int startNode, int destNode) {
		return v[startNode][destNode];
	}
	/*
	public boolean[][] getDones() {
		return done;
	}*/

	public Iterator<Path> iterator() {
		return new Iterator<Path>() {
			private int i = 0;
			private int j = 1;

			public boolean hasNext() {
				return (i <maxNodeIndex && j < maxNodeIndex);
			}

			public void remove() {
				throw new IllegalStateException("Not implemented");
			}

			public Path next() {
				Path toReturn = null;
				do {
					toReturn = getPath(i,j);
					j++;
					if (j == i) j++;
					if (j >= maxNodeIndex) {
						i++;
						if (directed) {
							j = i+1;
						} else {
							j = 0;
						}
					}
				} while (toReturn == null && hasNext());
				return toReturn;
			}
		};
	}

	public Path getLongestPath() {
		Path max = null;
		float maxLen = -1;
		for (int i = 0 ; i < v.length ; i++) {
			for (int j = 0 ; j < v.length ; j++) {
				if (i != j) {
					Path pa = getPath(i, j);
					if (pa != null) {
						double length = v[i][j];
						if (length > maxLen) {
							max = pa;
						}
					}
				}
			}
		}
		return max;
	}

	public Path getPath(NodePair np) {
		return getPath(np.getStartNode(), np.getEndNode());
	}

	public PathSet getPathSet() {
		PathSet ps = new PathSet(v.length);
		for (int i = 0 ; i < v.length ; i++) {
			for (int j = 0 ; j < v.length ; j++) {
				Path p = getPath(i,j);
				if (p != null)
					ps.addPath(getPath(i,j));
			}
		}
		return ps;			
	}

	public Path getPath(int startNode, int destNode) {
		if (done[startNode][destNode] == false) {
			return singleDijkstra(startNode, destNode);
		}
		if (v[startNode][destNode] < 0) {
			throw new IllegalStateException("Path " + startNode +"-" + destNode + " has negative value");
		}
		Path p = new Path();
		int cursor = startNode;
		p.add(cursor);
		while (cursor != destNode) {
			int nloc = n[cursor][destNode];
			if(nloc!=-1){
				cursor = nloc;
			} else {
				return null;
			}
			p.add(cursor);
		}
		return p;
	}

	private Path singleDijkstra(int startNode, int destNode) {
		BoxedPriorityQueue<Integer> queue = new BoxedPriorityQueue<Integer>();
		double[] dist = new double[incidenceMatrix.length];
		int[] previous = new int[incidenceMatrix.length];
		Arrays.fill(dist, Double.MAX_VALUE);
		dist[startNode] = 0;
		queue.offer(startNode, 0);
		boolean stop = false;
		double distance;
		do {
			distance = queue.peekScore();
			int vertex = queue.pollElement();
			stop |= (vertex == destNode);
			addNeighbourhood(queue, vertex, dist, previous);
		} while (queue.isEmpty() == false && !stop);
		Path path = new Path();
		path.add(destNode);
		int cursor = destNode;
		do {
			if (path.size() > 1) {
				n[cursor][destNode] = path.getPenultimate();
			} else {
				n[cursor][destNode] = path.getLast();
			}
			done[cursor][destNode] = true;
			v[cursor][destNode] = distance - dist[cursor];
			path.add(previous[cursor]);
			cursor = previous[cursor];
		} while (cursor != startNode);
		if (path.size() > 1)
			n[cursor][destNode] = path.getPenultimate();
		else {
			n[cursor][destNode] = path.getLast();
		}	
		done[cursor][destNode] = true;
		v[cursor][destNode] = distance;
		path.reverse();
		return path;
	}
	
	private void addNeighbourhood(
			BoxedPriorityQueue<Integer> queue, 
			int from, 
			double[] distances,
			int[] previous) {
		for (int i = 0 ; i < distances.length ; i++) {
			if (i == from) continue;
			if (incidenceMatrix[from][i] < 0) continue;
			double newDist = distances[from] + incidenceMatrix[from][i];
			if (newDist < distances[i]) {
				distances[i] = newDist;
				previous[i] = from;
				queue.offer(i, newDist);
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\r\nIncidence Matrix: \r\n[");
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			if (i == 0) {
				sb.append("[");
			} else {
				sb.append("  ");
			}
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				sb.append((int)incidenceMatrix[i][j]);
				if (j+1 < maxNodeIndex) {sb.append(",");}
			}
			sb.append("]");
			if (i+1 < maxNodeIndex) {sb.append("\r\n");} else {
				sb.append("]");
			}
		}
	/*	sb.append("\r\nPredecessors : \r\n[");
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			if (i == 0) {
				sb.append("[");
			} else {
				sb.append("  ");
			}
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				if (done[i][j] == true || maskUndone == false) {
					sb.append(p[i][j]);
				} else {
					sb.append("*");
				}
				if (j+1 < maxNodeIndex) {sb.append(",");}
			}
			sb.append("]");
			if (i+1 < maxNodeIndex) {sb.append("\r\n");} else {
				sb.append("]");
			}
		}*/
		sb.append("\r\n");
		sb.append("Nexts : \r\n[");
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			if (i == 0) {
				sb.append("[");
			} else {
				sb.append("  ");
			}
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				if (done[i][j] == true || maskUndone == false) {
					sb.append(n[i][j]);
				} else {
					sb.append("*");
				}
				if (j+1 < maxNodeIndex) {sb.append(",");}
			}
			sb.append("]");
			if (i+1 < maxNodeIndex) {sb.append("\r\n");} else {
				sb.append("]");
			}
		}
		sb.append("\r\nDistances : \r\n[");
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			if (i == 0) {
				sb.append("[");
			} else {
				sb.append("  ");
			}
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				if (done[i][j] == true || maskUndone == false) {
					sb.append((int)v[i][j]);
				} else {
					sb.append("*");
				}
				if (j+1 < maxNodeIndex) {sb.append(",");}
			}
			sb.append("]");
			if (i+1 < maxNodeIndex) {sb.append("\r\n");} else {
				sb.append("]");
			}
		}
		return sb.toString();
	}

	public void printDistances() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < maxNodeIndex ; i++) {
			if (i == 0) {
				sb.append("[");
			} else {
				sb.append("  ");
			}
			for (int j = 0 ; j < maxNodeIndex ; j++) {
				if (done[i][j] == true || maskUndone == false) {
					sb.append((int)v[i][j]);
				} else {
					sb.append("*");
				}
				if (j+1 < maxNodeIndex) {sb.append(",");}
			}
			sb.append("]");
			if (i+1 < maxNodeIndex) {sb.append("\r\n");} else {
				sb.append("]");
			}
		}
		System.out.println(sb);
	}
}