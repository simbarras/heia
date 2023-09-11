package ch.epfl.general_libraries.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.RandomStream;

public abstract class PRNStream implements RandomStream, RandomSource {
	
	public PRNStream(int seed) {
		this.initialSeed = seed;
	}
	/**
	 * Returns one integer selected randomly from the range <code>[from, to]
	 * </code>.
	 */
	public abstract int nextInt(int from, int to);
	public abstract int nextInt();
	public abstract void nextArrayOfInt(int from, int to, int[] dest, int start, int n);

	public abstract long nextUnsignedInt();

	public abstract double nextDouble();
	public abstract void nextArrayOfDouble(double[] dest, int start, int n);

	public abstract String getState();

	public abstract int[][] getStateInt();

	public abstract double nextRandomImpl();

	public abstract PRNStream clone(int seed);

	public abstract int getSeed();
	public abstract String getType();
	
	private double next = 0;
	private boolean hasNext = false;
	private int initialSeed;
	
	public int getInitialSeed() {
		return initialSeed;
	}
	
	/** Compatibility with RandomSource but without effect
	 * 
	 * @return
	 */
	public int getIndex() {
		return 0;
	}
	
	public int generationStep() {
		return 0;
	}
	
	public void incGenStep() {
		
	}
	
	public double nextRandom() {
		if (!hasNext)
			return nextRandomImpl();
		hasNext = false;
		return next;
	}
	
	public double peekNextRandom() {
		if (hasNext == false) {
			next = nextRandomImpl();
			hasNext = true;
		}
		return next;
	}

	/**
	 * Returns one integer selected randomly from the range <code>[from, to]
	 * </code>.
	 */
	protected static int nextInt(int from, int to, double ran) {
		int range = to - from + 1;
		return (int)Math.round(Math.floor(ran * range)) + from;
	}

	public static long convertDouble(double d) {
		return (long)(d * 4294967296d);
	}

	public static int convertDoubleToInt(double d) {
		return (int) (d * 4294967296d);
	}

	public double nextGaussian() {
		return NormalDist.inverseF01(this.nextDouble());
	}

	/** Takes an int in the [0, to] interval
	 */
	public int nextInt(int n) {
		return nextInt(0, n);
	}
	
	public static <T> Collection<T> pickNin(Collection<T> col, int n , RandomSource src) {
		Collection<T> newCol = new ArrayList<T>(n);
		for (int i = 0 ; i < n ; i++) {
			T t = pickIn(col, src);
			newCol.add(t);
			col.remove(t);
		}
		return newCol;
	}
	
	public <T> Collection<T> pickNin(Collection<T> col, int n) {
		Collection<T> newCol = new ArrayList<T>(n);
		for (int i = 0 ; i < n ; i++) {
			T t = pickIn(col);
			newCol.add(t);
			col.remove(t);
		}
		return newCol;
	}	
	
	public static <T> T pickIn(Collection<T> col, RandomSource src) {
		int i = col.size();
		int r = nextInt(0,i-1, src.nextRandom());
		int iter = 0;
		for (T t : col) {
			if (iter == r) return t;
			iter++;
		}
		throw new IllegalStateException();		
	}
	
	public <T> T pickIn(Collection<T> col) {
		int i = col.size();
		int r = nextInt(0,i-1);
		int iter = 0;
		for (T t : col) {
			if (iter == r) return t;
			iter++;
		}
		throw new IllegalStateException();
	}
	
	public <T> T pickIn(T[] tab) {
		int i = tab.length;
		int r = nextInt(0,i-1);
		int iter = 0;
		for (T t : tab) {
			if (iter == r) return t;
			iter++;
		}
		throw new IllegalStateException();		
	}
	
	public int pickIn(int[] tab) {
		int i = tab.length;
		int r = nextInt(0,i-1);
		int iter = 0;
		for (int j = 0 ; j < tab.length; j++) {
			int t = tab[j];
			if (iter == r) return t;
			iter++;
		}
		throw new IllegalStateException();			
	}
	
	
	public <T> T pickInAndRemove(Collection<T> col) {
		int i = col.size();
		int r = nextInt(0,i-1);
		int iter = 0;
		for (T t : col) {
			if (iter == r) {
				col.remove(t);
				return t;
			}
			iter++;
		}
		throw new IllegalStateException();
	}	

	public <T> ArrayList<T> shuffle(Collection<T> col) {
		ArrayList<T> list = new ArrayList<T>(col.size());
		while (col.size() > 0) {
			list.add(pickInAndRemove(col));
		}
		return list;
	}
	
	public int[] shuffle(int[] array) {
		ArrayList<Integer> lis = new ArrayList<Integer>(array.length);
		for (int i = 0 ; i < array.length ; i++) {
			lis.add(array[i]);
		}
		int[] a2 = new int[array.length];
		for (int i = 0 ; i < array.length ; i++) {
			a2[i] = pickInAndRemove(lis);
		}
		return a2;
	}
	

	public <T> ArrayList<T> shuffleAndKeep(Collection<T> col) {
		ArrayList<T> list = new ArrayList<T>(col.size());
		ArrayList<T> copy = new ArrayList<T>(col);
		while (copy.size() > 0) {
			list.add(pickInAndRemove(copy));
		}
		return list;
	}
	
	public void setSeed(int i) {
		initialSeed = i;
		setSeedInternal(i);
	}
	
	public void reset() {
		setSeedInternal(initialSeed);
	}

	public abstract void setSeedInternal(int i);

	public abstract void setStateInt(int[][] i);
	
	public abstract void resetInternal();

	public void resetNextSubstream() {
		throw new IllegalStateException("By default not implemented, must be overriden");
	}

	public void resetStartStream() {
		throw new IllegalStateException("By default not implemented, must be overriden");
	}

	public void resetStartSubstream() {
		throw new IllegalStateException("By default not implemented, must be overriden");
	}

	public static PRNStream getDefaultStream(int seed) {
		return new MersenneTwister(seed);
	}

	public static PRNStream getRandomStream() {
		int i = (new Random()).nextInt();
		return new MersenneTwister(i);
	}

	public int getRandomIndex(double[] weights) {
		double tot = 0;
		for (int i = 0 ; i < weights.length ; i++) {
			tot += weights[i];
		}
		double rand = this.nextDouble() * tot;
		double sinceThen = 0;
		for (int i = 0 ; i < weights.length ; i++) {
			sinceThen += weights[i];
			if (rand < sinceThen) {
				return i;
			}
		}
		throw new IllegalStateException();
	}

	private Random r;

	public Random toRandom() {
		if (r == null) {
			r = new Random() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				@Override
				public boolean nextBoolean() {
					return (PRNStream.this.nextDouble() > 0.5);
				}
				@Override
				public void nextBytes(byte[] bytes) {
					for (int i = 0 ; i < bytes.length ; i++) {
						bytes[i] = (byte)PRNStream.this.nextInt(0, 255);
					}
				}
				@Override
				public double nextDouble() {
					return PRNStream.this.nextDouble();
				}
				@Override
				public float nextFloat() {
					return (float)PRNStream.this.nextDouble();
				}
				@Override
				public double nextGaussian() {
					return NormalDist.inverseF01(PRNStream.this.nextDouble());
				}
				@Override
				public int nextInt() {
					return PRNStream.this.nextInt();
				}
				@Override
				public int nextInt(int n) {
					return PRNStream.this.nextInt(0, n-1);
				}
				@Override
				public long nextLong() {
					return (PRNStream.this.nextInt() << 32) + PRNStream.this.nextInt();
				}
			};
		}
		return r;
	}

}
