package ch.epfl.general_libraries.random;

import org.spaceroots.mantissa.random.FourTapRandom;
import org.spaceroots.mantissa.random.MersenneTwister;

import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;

public abstract class MantissaBasedPRNStream extends RandomClassBasedPRNStream {

	public static final String MT19937 = "mt19937";
	public static final String FOUR_TAP_RANDOM = "four_tap_random";
	protected String met;
	protected int seed;

	protected MantissaBasedPRNStream(int seed, @ForcedParameterDef(possibleValues={MT19937, FOUR_TAP_RANDOM }) String method) {
		super(seed);
		this.seed = seed;
		met = method;
		if (method.equals(MT19937)) {
			super.setRandom(new MersenneTwister(seed));
		} else if (method.equals(FOUR_TAP_RANDOM)) {
			super.setRandom(new FourTapRandom(seed));
		}
		reset();
	}
	
	public void resetInternal() {
		if (met.equals(MT19937)) {
			super.setRandom(new MersenneTwister(seed));
		} else if (met.equals(FOUR_TAP_RANDOM)) {
			super.setRandom(new FourTapRandom(seed));
		}
	}

	@Override
	public String getType() {
		return "mantissa_"+met+"_stream";
	}

	@Override
	public int getSeed() {
		return seed;
	}

	@Override
	public int[][] getStateInt() {
		if (source instanceof ModFourTapRandom) {
			return ((ModFourTapRandom)source).getStateInt();
		} else {
			return null;
		}
	}

	@Override
	public void setStateInt(int[][] state) {
		if (source instanceof ModFourTapRandom) {
			((ModFourTapRandom)source).setStateInt(state);
		}
	}




}
