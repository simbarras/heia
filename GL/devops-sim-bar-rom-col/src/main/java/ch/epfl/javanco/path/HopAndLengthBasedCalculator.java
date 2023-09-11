package ch.epfl.javanco.path;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.javanco.base.AbstractGraphHandler;


public class HopAndLengthBasedCalculator extends LengthBasedCalculator {

	private float hopLengthEq;

	public HopAndLengthBasedCalculator(AbstractGraphHandler agh,
			String onLayer,
			boolean directed,
			float hopLengthEquivalent){
		super(agh, onLayer, directed);
		hopLengthEq = hopLengthEquivalent;
	}

	@Override
	public float getPathValue(Path myPath) {
		return super.getPathValue(myPath) + ((myPath.getNumberOfHops()-1) * hopLengthEq);
	}

}
