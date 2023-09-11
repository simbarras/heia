package ch.epfl.general_libraries.path;


public class ShortestPathSet extends PathSet {

	
	public ShortestPathSet(boolean[][] b) {
		super(b.length);
		double[][] d = new double[b.length][b.length];
		for (int i = 0 ; i < b.length ; i++) {
			for (int j = 0 ; j < b.length ; j++) {
				d[i][j] = (b[i][j] ? 1 : -1);
			}
		}
		compute(d, true);
	}
	
	public ShortestPathSet(double[][] d) {
		super(d.length);
		compute(d, false);
	}

	public ShortestPathSet(double[][] d, boolean directed) {
		super(d.length);
		compute(d, directed);
	}

	private void compute(double[][] d, boolean directed) {
		ShortestPathAlgorithm alg = new ShortestPathAlgorithm(d, directed);
		alg.computeAll();
		for (Path p : alg) {
			if (p != null && (directed || (p.getFirst() < p.getLast()))) {
				addPath(p);
			}
		}
	}



}
