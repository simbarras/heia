package ch.epfl.general_libraries.path;


public class CalculatorBasedFilter extends PathFilter {

	private PathCalculator pc;
	private float limitPrefix;
	private float limitTotal;

	public CalculatorBasedFilter(PathCalculator pc, float limit1) {
		super(null);
		this.pc = pc;
		limitTotal = limit1;
		limitPrefix = Float.POSITIVE_INFINITY;
	}

	public CalculatorBasedFilter(PathCalculator pc, float limitTotal, float limitPrefix) {
		super(null);
		this.pc = pc;
		this.limitTotal = limitTotal;
		this.limitPrefix = limitPrefix;
	}
	
	public CalculatorBasedFilter(PathCalculator pc, float limitTotal, float limitPrefix, PathFilter nextfilter) {
		super(nextfilter);
		this.pc = pc;
		this.limitTotal = limitTotal;
		this.limitPrefix = limitPrefix;
	}	
	
	/**
	 * First boolean is if path is valid, second if this path
	 * is valid as prefix
	 */
	@Override
	public boolean[] filterPrefix(Path p) {
		float pathValue = pc.getPathValue(p);
		if (isHighPass()) {
			return new boolean[]{(pathValue >= limitTotal),(pathValue >= limitPrefix)};
		} else {
			return new boolean[]{(pathValue <= limitTotal),(pathValue <= limitPrefix)};
		}
	}

	@Override
	public boolean filter(Path p) {
		float pathValue = pc.getPathValue(p);
		if (isHighPass()) {
			return (pathValue >= limitTotal);
		} else {
			return (pathValue <= limitTotal);
		}
	}

	@Override
	public void setHighPass() {
		super.setHighPass();
		if (limitPrefix == Float.POSITIVE_INFINITY) {
			limitPrefix = Float.NEGATIVE_INFINITY;
		}
	}
	@Override
	public void setLowPass() {
		super.setLowPass();
		if (limitPrefix == Float.NEGATIVE_INFINITY) {
			limitPrefix = Float.POSITIVE_INFINITY;
		}
	}


}
