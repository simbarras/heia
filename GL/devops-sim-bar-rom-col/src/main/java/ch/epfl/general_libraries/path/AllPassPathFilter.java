package ch.epfl.general_libraries.path;


public class AllPassPathFilter extends PathFilter {
	
	public AllPassPathFilter() {
		super(null);
	}

	@Override
	public boolean[] filterPrefix(Path p) {
		return new boolean[]{true, true};
	}

	@Override
	public boolean filter(Path p) {
		return true;
	}

}
