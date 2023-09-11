package ch.epfl.general_libraries.path;

import java.util.Collection;

public abstract class PathFilter {

	private boolean highPass = false;
	
	private PathFilter nextFilter;
	/**
	 * First boolean is if path is valid, seconds if this path
	 * is valid as prefix
	 */
	public abstract boolean[] filterPrefix(Path p);

	public abstract boolean filter(Path p);
	
	private boolean filterLoc(Path p) {
		boolean thisF = filter(p);
		if (thisF == true && nextFilter != null) {
			return nextFilter.filter(p);
		} else {
			return thisF;
		}
	}
	
	public PathFilter() {
	}
	
	public PathFilter(PathFilter nextFilter) {
		this.nextFilter = nextFilter;
	}

	public boolean filter(Collection<Path> col) {
		for (Path p : col) {
			if (!filterLoc(p)) {
				return false;
			}
		}
		return true;
	}

	public void setHighPass() {
		highPass = true;
	}
	public void setLowPass() {
		highPass = false;
	}

	public boolean isHighPass() {
		return highPass;
	}

}
