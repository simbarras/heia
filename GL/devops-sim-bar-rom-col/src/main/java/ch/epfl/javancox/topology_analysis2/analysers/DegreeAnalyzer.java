package ch.epfl.javancox.topology_analysis2.analysers;

import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.topology_analysis2.AbstractGraphAnalyser;

public class DegreeAnalyzer extends AbstractGraphAnalyser {

	@Override
	public void analyse(AbstractGraphHandler agh, AbstractResultsManager man,
			DataPoint dp, Execution e) {
		int nbNodes = agh.getNumberOfNodes();
		int maxDeg = 0;
		for (int i = 0 ; i < nbNodes ; i++) {
			maxDeg = Math.max(maxDeg, agh.getNodeContainer(i).getConnectedLinks().size());
		}
	//	DataPoint dp2 = dp.getDerivedDataPoint();
		dp.addResultProperty("Max degree", maxDeg);
		
	}

}
