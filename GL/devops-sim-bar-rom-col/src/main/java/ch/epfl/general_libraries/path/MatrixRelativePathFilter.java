package ch.epfl.general_libraries.path;


public class MatrixRelativePathFilter extends PathFilter {
	
	private double[][] limit;
	private double[][] distance;
	
	/**
	 * Accepts all paths shorter or equals than the given distances 
	 */
	public MatrixRelativePathFilter(double[][] distance) {
		this.distance = distance;
		limit = distance;
	}
	
	public MatrixRelativePathFilter(double[][] distance, double tolerance) {
		this.distance = distance;
		limit = new double[distance.length][distance.length];
		for (int i = 0 ; i < distance.length ; i++) {
			for (int j = 0 ; j < distance.length ; j++) {
				limit[i][j] = distance[i][j] * tolerance;
			}
		}
	}	
	
	
	/**
	 * Method filterPrefix
	 *
	 *
	 * @param p
	 *
	 * @return
	 *
	 */
	 
	private static boolean[] tru = new boolean[]{true, true};
	private static boolean[] fal = new boolean[]{false, false};
	 
	 
	public boolean[] filterPrefix(Path p) {
		int[] indexes = p.allIndexes();
		float sum = 0;
		for (int i = 0 ; i < indexes.length -1 ; i++) {
			sum += distance[indexes[i]][indexes[i+1]];
		}
		if (sum <= limit[indexes[0]][p.getLast()]) {
			return tru;
		}
		return fal;
	}

	public boolean filter(Path p) {
		return filterPrefix(p)[0];
	}	
}
