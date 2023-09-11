package ch.epfl.javancox.topology_analysis.network_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.topology_analysis.MonoMetricComputer;


public abstract class NetworkWideMetricComputer extends MonoMetricComputer {

	private AbstractGraphHandler agh = null;

	@Override
	public void init() {}

	protected abstract double computeNetworkWideMetric();

	public double computeMetric() {
		AbstractGraphHandler rpiAgh = rpi.getInput().getAgh();
		if ((agh == null) || (rpiAgh.equals(this.agh) == false)) {
			this.agh = rpiAgh;
			return computeNetworkWideMetric();
		} else {
			AbstractGraphHandler temp = agh;
			agh = rpiAgh;
			double d = computeNetworkWideMetric();
			agh = temp;
			return d;
		}
	}

	/**
	 * This method is provided for the classes extending this class
	 */
	protected static int getTrianglesOfNode (AbstractGraphHandler agh , int node){
		int res = 0;
		NodeContainer nc = agh.getNodeContainer(node);

		for ( LinkContainer lc1 : nc.getAllConnectedLinks()){
			for ( LinkContainer lc2 : nc.getAllConnectedLinks()){
				if (lc1.getOtherNodeIndex(nc.getIndex()) != lc2.getOtherNodeIndex(nc.getIndex())){
					for (LinkContainer lc3 : lc1.getOtherNodeContainer(nc.getIndex()).getAllConnectedLinks()){
						if (lc2.getOtherNodeIndex(nc.getIndex()) == lc3.getOtherNodeIndex(lc1.getOtherNodeIndex(nc.getIndex()))){
							res++;
						}
					}
				}
			}
		}


		res = res / 2;

		return res;
	}


}
