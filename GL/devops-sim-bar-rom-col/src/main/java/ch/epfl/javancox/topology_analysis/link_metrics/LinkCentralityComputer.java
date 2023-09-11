package ch.epfl.javancox.topology_analysis.link_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javancox.topology_analysis.CentralityComputer;
import ch.epfl.javancox.topology_analysis.MultiMetricComputer;

public abstract class LinkCentralityComputer extends MultiMetricComputer implements CentralityComputer {

	private LinkContainer centralLink = null;

	@Override
	/** Returns the centrality of each link contained in the AGH.
	 * The ordre respect the agh.getLinkContainers() order
	 */
	protected double[] computeForAllElements() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();

		int i = 0;
		double[] tab = new double[agh.getLinkContainers().size()];
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		LinkContainer minLink = null;
		LinkContainer maxLink = null;		
		for (LinkContainer lc: agh.getLinkContainers()) {
			if (lc != null) {
				tab[i] = computeLinkCentrality(lc);
				if (tab[i] > max) {
					max = tab[i];
					maxLink = lc;
				}
				if (tab[i] < min) {
					min = tab[i];
					minLink = lc;
				}
				i++;								
			}
		}
		if (theGreaterTheCentraler()) {
			centralLink = maxLink;
		} else {
			centralLink = minLink;
		}		
		return tab;
	}
	
	public String getCentralElement() {
		return centralLink.toSimpleString();
	}

	protected abstract double computeLinkCentrality(LinkContainer lc);


}
