package ch.epfl.general_libraries.path;

public class PathRelativeFilter extends PathFilter {

	protected PathSet referencePathSet;
	protected PathCalculator pc;
	protected float limitPath;
	protected float limitPrefix;
	protected int operation;
	protected boolean inclusive;

	public static final int PLUS = 0;
	public static final int MULTIPLY = 1;
	public static final int POWER = 2;
	
	

	public PathRelativeFilter(PathSet referencePathSet, PathCalculator pc, float pathLimit) {
		this (referencePathSet, pc, null, pathLimit, Float.MAX_VALUE, 1, true);
	}

	public PathRelativeFilter(PathSet referencePathSet, PathCalculator pc, float pathLimit, int operator) {
		this (referencePathSet, pc, null, pathLimit, Float.MAX_VALUE, operator, true);
	}

	public PathRelativeFilter(PathSet referencePathSet, PathCalculator pc, float limitPath, float limitPrefix, int operation) {
		this(referencePathSet, pc, null, limitPath, limitPrefix, operation, true);
	}

	public PathRelativeFilter(PathSet referencePathSet,
			PathCalculator pc,
			PathFilter nextFilter,
			float limitPath,
			float limitPrefix,
			int operation,
			boolean inclusive) {
		super(nextFilter);
		this.referencePathSet = referencePathSet;
		this.pc = pc;
		this.limitPath = limitPath;
		this.limitPrefix = limitPrefix;
		this.operation = operation;
		this.inclusive = inclusive;
		checkInit();
	}
	
	protected void checkInit() {
		if (limitPath > limitPrefix) {
			throw new IllegalArgumentException("Limit path cannot be greater as limit prefix");
		}
		if (operation > 2 || operation < 0) {
			throw new IllegalArgumentException("Parameter operation can only have values 0, 1 or 2 (corresponding to PLUS, MULIPLY and POWER");
		}
		
	}
	
	protected PathRelativeFilter(PathFilter filter) {
		super(filter);
	}

	public boolean isInclusive() {
		return inclusive;
	}

	public void setInclusive(boolean inclusive) {
		this.inclusive = inclusive;
	}

	public String getOperation() {
		switch (operation) {
		case 0:
			return "PLUS";
		case 1:
			return "MULTIPLY";
		case 2:
			return "POWER";
		default:
			return "error";
		}
	}

	/**
	 * First boolean is if path is valid, seconds if this path
	 * is valid as prefix
	 */
	@Override
	public boolean[] filterPrefix(Path p) {
		//	System.out.println(p);
		Path shortest = referencePathSet.getPath(p.getFirst(), p.getLast());
		if (shortest == null) {
			throw new IllegalStateException("Given reference set has not path from " +
					p.getFirst() + " and " + p.getLast() +
			" and thus cannot accept a path from these two extremities. Check if reference PathSet is symmetric");
		}
		if (p.size() >= 3) {
			if (pc != null) {
				float min = pc.getPathValue(shortest);
				float act = pc.getPathValue(p);
				float effectivePLim, effectivePrefixLim;
				switch (operation) {
				case 0:		default:
					effectivePLim = min + limitPath;
					effectivePrefixLim = min + limitPrefix;
					break;
				case 1:
					effectivePLim = min * limitPath;
					effectivePrefixLim = min * limitPrefix;
					break;
				case 2:
					effectivePLim = (float)Math.pow(min, limitPath);
					effectivePrefixLim = (float)Math.pow(min, limitPrefix);
					break;

				}
				if (inclusive) {
					if (act >= effectivePrefixLim) {
						return new boolean[]{false, false};
					} else {
						if (act <= effectivePLim) {
							return new boolean[]{true, true};
						} else {
							return new boolean[]{false, true};
						}
					}
				} else {
					if (act > effectivePrefixLim) {
						return new boolean[]{false, false};
					} else {
						if (act < effectivePLim) {
							return new boolean[]{true, true};
						} else {
							return new boolean[]{false, true};
						}
					}
				}
			}
		}
		return new boolean[]{true, true};
	}

	@Override
	public boolean filter(Path p) {
		return filterPrefix(p)[0];
	}
}
