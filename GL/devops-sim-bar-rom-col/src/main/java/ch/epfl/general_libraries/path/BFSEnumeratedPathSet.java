package ch.epfl.general_libraries.path;

import java.util.LinkedList;

public class BFSEnumeratedPathSet extends PathSet {

	private int maxPaths;
	private double[][] incidence;
	
	public BFSEnumeratedPathSet(double[][] incidence, int maxPaths, PathFilter myFilter) {
		super(incidence.length, myFilter);
		this.maxPaths = maxPaths;
		this.incidence = incidence;
	}
	
	public BFSEnumeratedPathSet(double[][] incidence, int maxPaths) {
		super(incidence.length);
		this.maxPaths = maxPaths;
		this.incidence = incidence;
	}
	
	public BFSEnumeratedPathSet(double[][] incidence) {
		this(incidence, -1);
	}
	
	public double[][] getIncidenceMatrix() {
		return incidence;
	}
	
	public void enumerateAndStoreAll() {
		int[][][] init = init();
		for (int i = 0 ; i < incidence.length ; i++) {
			enumerateFrom(i, init[0], init[1]);
		}
	}
	
	public void enumerateAndStoreFrom(int i) {
		int[][][] init = init();
		enumerateFrom(i, init[0], init[1]);
	}
	
	private int[][][] init() {
		int[][] neighbours = new int[incidence.length][];	
		int[][] numberFound = new int[incidence.length][incidence.length];
		for (int i = 0 ; i < incidence.length ; i++) {
			int nb = 0;
			for (int j = 0 ; j < incidence.length ; j++) {
				if (i != j) {
					if (incidence[i][j] >= 0) nb++;
				}
			}
			neighbours[i] = new int[nb];
			int index = 0;
			for (int j = 0 ; j < incidence.length ; j++) {
				if (i != j) {
					if (incidence[i][j] >= 0) {
						neighbours[i][index] = j;
						index++;
					}
				}
			}					
		}
		return new int[][][]{neighbours, numberFound};
	}
		
	protected void enumerateFrom(int i, int[][] neighbours, int[][] numberFound) {
		LinkedList<Path> q = new LinkedList<Path>();
		Path init = new Path(i);
		q.offer(init);
		while (!q.isEmpty()) {
			Path actual = q.poll();
			int actIndex = actual.getLast();
			int actualSize = actual.size();
			for (int j = 0 ; j < neighbours[actIndex].length ; j++) {
				int n = neighbours[actIndex][j];
				if (actualSize == 1 || !actual.contains(n)) {
					Path p = new Path(actual, n);
					boolean[] b = filterPrefix(p);
					if ((maxPaths < 0 || numberFound[i][n] < maxPaths) && b[0]) {
						addPath(p);
						numberFound[i][n]++;
					}
					if (b[1]) {
						q.offer(p);
					}
				}
			}				
		}						
	}	
	


}
