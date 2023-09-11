package ch.epfl.javancox.inputs.traffic;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.Util2DFunctions;
import ch.epfl.javancox.inputs.AbstractTrafficProvider;

public class DistanceBasedTrafficGenerator extends AbstractTrafficProvider {
	
	PRNStream stream;
	
	public DistanceBasedTrafficGenerator(PRNStream stream) {
		this.stream = stream;
	}
	
	public DistanceBasedTrafficGenerator(int seed) {
		this(PRNStream.getDefaultStream(seed));
	}
	
	public DistanceBasedTrafficGenerator() {
		this(0);
	}	
	
	public String getTrafficGeneratorName() {
		return "distance based";
	}
		
	public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
		Double[][] rates = new Double[agh.getHighestNodeIndex()+1][agh.getHighestNodeIndex()+1];
		for (int i = 0 ; i <= agh.getHighestNodeIndex() ; i++) {
			for (int j = 0; j <= agh.getHighestNodeIndex() ; j++) {
				if (i != j){
					if (agh.getNodeContainer(i) != null) {
						if (agh.getNodeContainer(j) != null) {
							double val = Util2DFunctions.distance(agh.getNodeContainer(i), agh.getNodeContainer(j));
							rates[i][j] = 1.0/val;
						}
					}
				}
			}			
		}
		return rates;
	}		
}
