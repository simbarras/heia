package ch.epfl.javancox.topology_analysis.network_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.utils.ComponentCounter;

public class GiantComponentSizeComputer extends NetworkWideMetricComputer {

	public static class RelativeGiantComponentSizeComputer extends GiantComponentSizeComputer {
		@Override
		protected double computeNetworkWideMetric() {
			AbstractGraphHandler agh = rpi.getInput().getAgh();
			double largestSize = super.computeNetworkWideMetric();
			return (largestSize-1)/(agh.getNodeContainers().size()-1);
		}
		
		@Override
		public String getResultName() {
			return "Proporition of nodes in the largest component";
		}		
	}

	@Override
	protected double computeNetworkWideMetric() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		ComponentCounter counter = new ComponentCounter(agh, rpi.getInput().getTopologyLayer());
		return counter.getLargestComponentSize();
	}
	
	@Override
	public String getResultName() {
		return "Nodes in the largest component";
	}	
	
}
