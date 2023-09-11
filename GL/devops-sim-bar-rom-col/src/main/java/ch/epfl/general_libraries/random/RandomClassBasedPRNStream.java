package ch.epfl.general_libraries.random;

import java.util.Random;

public abstract class RandomClassBasedPRNStream  extends PRNStream {

	Random source;

	protected RandomClassBasedPRNStream(int seed) {
		super(seed);
	}

	public RandomClassBasedPRNStream(int seed, Random r) {
		super(seed);
		source = r;
	}

	public void setRandom(Random r) {
		source = r;
	}

	@Override
	public void setSeedInternal(int seed) {
		source.setSeed(seed);
	}
	
	public double state() {
		throw new IllegalStateException("NOT IMPL");
	}	

	@Override
	public int[][] getStateInt() {
		return null;
	}

	@Override
	public void setStateInt(int[][] state) {
		throw new IllegalStateException("THis class does not support set state");
	}

	/**
	 * Returns one integer selected randomly from the range <code>[from, to]
	 * </code>.
	 */
	@Override
	public long nextUnsignedInt() {
		int i = source.nextInt();
		long l;
		if ( i < 0) {
			l = (i) ^ 0xFFFFFFFF00000000l;
		} else {
			l = i;
		}
		return l;
	}

	/** Takes an int in the [from, to] interval
	 */
	@Override
	public int nextInt(int from, int to) {
		int r = super.nextInt(from, to, source.nextDouble());
		return r;
	}

	@Override
	public int nextInt() {
		return source.nextInt();
	}

	@Override
	public void nextArrayOfInt(int from, int to, int[] dest, int start, int n) {
		if (dest.length < n) {
			throw new ArrayIndexOutOfBoundsException("Exceed size of array, fill greater than capacity");
		}
		for (int i = 0 ; i < n ; i++) {
			dest[start] = nextInt(from, to);
			start++;
			if (start >= dest.length) {
				start = 0;
			}
		}

	}

	@Override
	public double nextDouble() {
		return source.nextDouble();
	}
	@Override
	public double nextRandomImpl() {
		return source.nextDouble();
	}

	@Override
	public void nextArrayOfDouble(double[] dest, int start, int n) {
		if (dest.length < n) {
			throw new ArrayIndexOutOfBoundsException("Exceed size of array, fill greater than capacity");
		}
		for (int i = 0 ; i < n ; i++) {
			dest[start] = nextDouble();
			start++;
			if (start >= dest.length) {
				start = 0;
			}
		}
	}

	@Override
	public String getState() {
		return "state not implemented yet";
	}
}
