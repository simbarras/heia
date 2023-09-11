package ch.epfl.general_libraries.random;

import umontreal.ssj.rng.BasicRandomStreamFactory;
import umontreal.ssj.rng.MT19937;
import umontreal.ssj.rng.RandomStream;

public class SSJBasedPRNStream extends PRNStream {

	public static final String MT19937 = "MT19937";
	public static final String F2NL607 = "F2NL607";
	public static final String GENF2W32 = "GenF2w32";
	public static final String LFSR113 = "LFSR113";
	public static final String LFSR258 = "LFSR258";
	public static final String MRG31k3p = "MRG31k3p";
	public static final String MRG32k3a = "MRG32k3a";
	public static final String MRG32k3aL = "MRG32k3aL";
	public static final String RandRijndael = "RandRijndael";
	public static final String WELL1024 = "WELL1024";
	public static final String WELL512 = "WELL512";
	public static final String WELL607 = "WELL607";

	public static PRNStream getMT19937(int seed) {
		try {
			return new SSJBasedPRNStream(seed, MT19937);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}

	public static PRNStream getF2NL607(int seed) {
		try {
			return new SSJBasedPRNStream(seed, F2NL607);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}

	public static PRNStream getGENF2W32(int seed) {
		try {
			return new SSJBasedPRNStream(seed, GENF2W32);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getLFSR113(int seed) {
		try {
			return new SSJBasedPRNStream(seed, LFSR113);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getLFSR258(int seed) {
		try {
			return new SSJBasedPRNStream(seed, LFSR258);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getMRG31k3p(int seed) {
		try {
			return new SSJBasedPRNStream(seed, MRG31k3p);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getMRG32k3a(int seed) {
		try {
			return new SSJBasedPRNStream(seed, MRG32k3a);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getMRG32k3aL(int seed) {
		try {
			return new SSJBasedPRNStream(seed, MRG32k3aL);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getRandRijndael(int seed) {
		try {
			return new SSJBasedPRNStream(seed, RandRijndael);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getWELL1024(int seed) {
		try {
			return new SSJBasedPRNStream(seed, WELL1024);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getWELL512(int seed) {
		try {
			return new SSJBasedPRNStream(seed, WELL512);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}
	public static PRNStream getWELL607(int seed) {
		try {
			return new SSJBasedPRNStream(seed, WELL607);
		}
		catch (ClassNotFoundException e) {
		}
		return null;
	}

	RandomStream ssjRandomStream;
	private String class__ = null;
	private int seed;

	public SSJBasedPRNStream(int seed, String class_) throws ClassNotFoundException {
		super(seed);
		internalConstructor(seed, class_);
		this.seed = seed;
	}

	public SSJBasedPRNStream(int seed) {
		super(seed);
		try {
			internalConstructor(seed, SSJBasedPRNStream.MT19937);
			this.seed = seed;
		}
		catch (Exception e) {}
	}
	
	public void resetInternal() {
		try {
			internalConstructor(seed, class__);
		}
		catch (Exception e) {}			
	}

	@Override
	public PRNStream clone(int seed) {
		try {
			return new SSJBasedPRNStream(seed, this.class__);
		}
		catch (ClassNotFoundException io) {
			throw new IllegalStateException(io);
		}
	}

	private void internalConstructor(int seed, String class_) throws ClassNotFoundException {
		class__ = class_;
		Class<?> c = Class.forName("umontreal.ssj.rng." + class_);
		if (c.equals(MT19937.class)) {
			SSJ_MT19937Initializer mtInit = new SSJ_MT19937Initializer(seed);
			ssjRandomStream = new MT19937(mtInit);
		} else {
			BasicRandomStreamFactory fac = new BasicRandomStreamFactory(c);
			ssjRandomStream = fac.newInstance();
		}
	}

	@Override
	public String getType() {
		return "SSJ_"+this.class__+"_stream";
	}

	@Override
	public int getSeed() {
		return this.seed;
	}
	
	public double state() {
		throw new IllegalStateException("NOT IMPL");
	}	

	@Override
	public void setStateInt(int[][] i) {
		throw new IllegalStateException("No state in SSJ BAsed");
	}

	@Override
	public void setSeedInternal(int i) {
		throw new IllegalStateException("No state in SSJ BAsed");
	}

	@Override
	public long nextUnsignedInt() {
		double d = ssjRandomStream.nextDouble();
		d = d * 0x100000000l;
		long l = (long)Math.ceil(d);
		return l;
	}

	@Override
	public int nextInt() {
		return ssjRandomStream.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Return an integer comprised betwen from (inclusive) and to (exclusive)
	 */
	@Override
	public int nextInt(int from, int to) {
		if (to > from) {
			return ssjRandomStream.nextInt(from, to-1);
		} else {
			return from;
		}
	}
	@Override
	public void nextArrayOfInt(int from, int to, int[] dest, int start, int n) {
		ssjRandomStream.nextArrayOfInt(from, to, dest, start, n);
	}

	@Override
	public double nextDouble() {
		return ssjRandomStream.nextDouble();
	}
	@Override
	public double nextRandomImpl() {
		return ssjRandomStream.nextDouble();
	}

	@Override
	public void nextArrayOfDouble(double[] dest, int start, int n) {
		ssjRandomStream.nextArrayOfDouble(dest, start, n);
	}

	@Override
	public int[][] getStateInt() {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public String getState() {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void resetNextSubstream() {
		ssjRandomStream.resetNextSubstream();
	}
	@Override
	public void resetStartStream() {
		ssjRandomStream.resetStartStream();
	}
	@Override
	public void resetStartSubstream() {
		ssjRandomStream.resetStartSubstream();
	}

}
