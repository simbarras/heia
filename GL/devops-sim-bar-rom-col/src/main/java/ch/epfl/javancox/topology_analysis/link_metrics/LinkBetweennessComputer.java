package ch.epfl.javancox.topology_analysis.link_metrics;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.PathSet;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class LinkBetweennessComputer extends LinkCentralityComputer {

	private PathSet sp;
	private int[][] occ;

	private boolean directed;
	
	public LinkBetweennessComputer() {	
		this(false);
	}

	public LinkBetweennessComputer(boolean directed) {
		this.directed = directed;
	}
	
	public boolean theGreaterTheCentraler() {
		return true;
	}	
	
	@Override
	public String getResultName() {
		return "Link betweennesses";
	}	

	@Override
	protected double computeLinkCentrality(LinkContainer lc) {
		
		if (directed) {
			int tot = sp.size();
			return (double)occ[lc.getStartNodeIndex()][lc.getEndNodeIndex()]/(double)tot;
		} else {
			int tot = sp.size();
			int a = occ[lc.getStartNodeIndex()][lc.getEndNodeIndex()];
			int b = occ[lc.getEndNodeIndex()][lc.getStartNodeIndex()];
			return (double)(a+b)/(double)tot;
		}
	}

	@Override
	public void init() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		if (sp == null) {
			sp = getShortestPathSet(agh, directed, false);
		}
		occ = new int[agh.getNumberOfNodes()][agh.getNumberOfNodes()];
		try {
			for (Path p : sp) {
				for (NodePair np : p.getPathSegments()) {
					occ[np.getStartNode()][np.getEndNode()]++;
				}
			}
		}
		catch (Exception e) {
		}
	}
}
