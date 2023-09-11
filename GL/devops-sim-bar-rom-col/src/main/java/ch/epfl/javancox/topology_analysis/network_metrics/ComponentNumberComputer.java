package ch.epfl.javancox.topology_analysis.network_metrics;


public class ComponentNumberComputer extends NetworkWideMetricComputer {

	@Override
	protected double computeNetworkWideMetric() {
		return super.getNumberOfComponents(rpi.getInput().getAgh());
	}

	@Override
	public String getResultName() {
		return "Number of components";
	}	
	
}
