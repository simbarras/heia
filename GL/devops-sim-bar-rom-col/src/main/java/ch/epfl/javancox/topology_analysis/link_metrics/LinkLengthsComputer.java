package ch.epfl.javancox.topology_analysis.link_metrics;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javancox.topology_analysis.MultiMetricComputer;

public class LinkLengthsComputer extends MultiMetricComputer {
	
	private String attName;
	
	public LinkLengthsComputer() {
		this("length");
	}
	
	public LinkLengthsComputer(String attributeName) {
		this.attName = attributeName;
	}
	
	public void init() {}
	
	@Override
	public String getResultName() {
		return "Link lengths";
	}		
	
	@Override
	protected double[] computeForAllElements() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();

		int i = 0;
		double[] tab = new double[agh.getLinkContainers().size()];
		for (LinkContainer lc: agh.getLinkContainers()) {
			if (lc != null) {
				tab[i] = lc.attribute(attName).intValue();
				i++;
			}
		}
		return tab;
	}	
	
}
