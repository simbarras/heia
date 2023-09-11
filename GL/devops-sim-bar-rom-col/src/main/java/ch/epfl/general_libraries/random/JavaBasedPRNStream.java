package ch.epfl.general_libraries.random;

import java.util.Random;

public class JavaBasedPRNStream extends RandomClassBasedPRNStream {

	private int seed;

	public JavaBasedPRNStream() {
		super(convertDoubleToInt(Math.random()));
		seed = getInitialSeed();
		super.setRandom(new Random(seed));
	}

	public JavaBasedPRNStream(int seed) {
		super(seed, new Random(seed));
		this.seed = seed;
	}
	
	@Override
	public void resetInternal() {
		super.setRandom(new Random(seed));
	}

	@Override
	public PRNStream clone(int seed) {
		return new JavaBasedPRNStream(seed);
	}

	@Override
	public String getType() {
		return "java_Random_stream";
	}

	@Override
	public int getSeed() {
		return seed;
	}

}
