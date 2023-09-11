package ch.epfl.general_libraries.random;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;

public class FourTapRandom extends MantissaBasedPRNStream {

	@ConstructorDef(def="Default four tap random PRNG (seed = 0)")
	public FourTapRandom(@ParamName(name="The seed", default_="0") int seed) {
		super(seed, FOUR_TAP_RANDOM);
	}
	
	@Override
	public PRNStream clone(int seed) {
		this.seed = seed;
		return new FourTapRandom(seed);
	}	
	
}
