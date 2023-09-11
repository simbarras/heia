package ch.epfl.general_libraries.random;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;

public class MersenneTwister extends MantissaBasedPRNStream {
	
	@ConstructorDef(def="Default Mersenne Twister")
	public MersenneTwister(@ParamName(name="The seed", default_="0") int seed) {
		super(seed, MT19937);
	}
	
	@ConstructorDef(def="Seed 1")
	public MersenneTwister() {
		super(1, MT19937);
	}	
	
	@Override
	public PRNStream clone(int seed) {
		this.seed = seed;
		return new MersenneTwister(seed);
	}		
	
}
