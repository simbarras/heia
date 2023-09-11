package ch.epfl.general_libraries.path;

public class ShortestPathRelativePathSet extends BFSEnumeratedPathSet {
	
	private double tolerance;
	
	public ShortestPathRelativePathSet(double[][] incidence, int maxPaths) {
		this(incidence, maxPaths, 1);
	}

	public ShortestPathRelativePathSet(double[][] incidence) {
		this(incidence, -1, 1);
		// TODO Auto-generated constructor stub
	}	
	
	public ShortestPathRelativePathSet(double[][] incidence, double tolerance) {
		this(incidence, -1, tolerance);
	}
	
	public ShortestPathRelativePathSet(double[][] incidence, int maxPaths, double tolerance) {
		super(incidence, maxPaths);
		this.tolerance = tolerance;
	}
	
	@Override
	public void enumerateAndStoreAll() {
		ShortestPathAlgorithm alg = new ShortestPathAlgorithm(getIncidenceMatrix());
		alg.computeAll();
		MatrixRelativePathFilter m = new MatrixRelativePathFilter(alg.getDistances(), tolerance);
		
		setFilter(m);
		super.enumerateAndStoreAll();		
	}





}
