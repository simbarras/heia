package ch.epfl.javancox.topology_analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.AdvancedExperiment;
import ch.epfl.general_libraries.experiment_aut.Model;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class CombinedTopologyAnalyser extends Model {

	private List<AbstractTopologyAnalyser> analyserList;

	public CombinedTopologyAnalyser(List<AbstractTopologyAnalyser> list) {
		analyserList = list;
	}

	public CombinedTopologyAnalyser(AbstractTopologyAnalyser ... lyser) {
		analyserList = new ArrayList<AbstractTopologyAnalyser>(lyser.length);
		for (AbstractTopologyAnalyser a : lyser) {
			analyserList.add(a);
		}
	}

	/**
	 * This method does to conversion with the generic conductExperiment
	 * signature defined in the Model class
	 */
	@Override
	public void conductExperiment(AdvancedExperiment exp, AbstractResultsManager man) {
		conductExperiment((TopologyAnalysis)exp, man);
	}

	public void conductExperiment(TopologyAnalysis rpi) {
		for (AbstractTopologyAnalyser lyser : analyserList) {
			lyser.setExperiment(rpi);
			extractResults(rpi, lyser);
		}
	}

	protected void extractResults(AdvancedExperiment rpi, AbstractTopologyAnalyser lyser) {
		
		DataPoint cop = rpi.getRootDataPoint();
		
		if (lyser instanceof MonoMetricComputer) {
			double val = ((MonoMetricComputer)lyser).computeMetric();
			cop.addResultProperty(lyser.getResultName(), val);
			
		} else if (lyser instanceof MultiMetricComputer) {
			MultiMetricComputer mmc = ((MultiMetricComputer)lyser);
			double mean = mmc.computeMean();
			double max = mmc.computeMax();
			double var = mmc.computeVar();
			
			cop.addResultProperty("mean"+lyser.getResultName(), mean);
			cop.addResultProperty("max"+lyser.getResultName(), max);
			cop.addResultProperty("var"+lyser.getResultName(), var);	
		}
		
		rpi.getAssociatedExecution().addDataPoint(cop);
	}
	
	public Map<String, String> getAllParameters() {
		Map<String, String> m = getNewMap();
		for (AbstractTopologyAnalyser abt : analyserList) {
			m.putAll(abt.getAllParameters());
		}
		return m;
	}

}
