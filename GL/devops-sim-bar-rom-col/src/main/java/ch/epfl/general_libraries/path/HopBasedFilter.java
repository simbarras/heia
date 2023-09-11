package ch.epfl.general_libraries.path;


public class HopBasedFilter extends PathFilter {

	private int maxHops;

	public HopBasedFilter(int maxHops) {
		super(null);
		this.maxHops = maxHops;
	}
	
	public HopBasedFilter(int maxHops, PathFilter filter) {
		super(filter);
		this.maxHops = maxHops;
	}	

	/**
	 * First boolean is if path is valid, seconds if this path
	 * is valid as prefix
	 */
	@Override
	public boolean[] filterPrefix(Path p) {
		if (isHighPass()) {
			if (p.size() >= maxHops+1) {
				return new boolean[]{true, true};
			} else {
				return new boolean[]{false, false};
			}
		} else {
			if (p.size() <= maxHops+1) {
				return new boolean[]{true, true};
			} else {
				return new boolean[]{false, false};
			}
		}
	}

	@Override
	public boolean filter(Path p) {
		if (isHighPass()) {
			return (p.size() >= maxHops+1);
		} else {
			return (p.size() <= maxHops+1);
		}
	}
}
