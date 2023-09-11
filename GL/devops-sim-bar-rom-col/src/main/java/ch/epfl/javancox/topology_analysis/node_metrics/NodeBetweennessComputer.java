package ch.epfl.javancox.topology_analysis.node_metrics;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.PathSet;
import ch.epfl.javanco.base.AbstractGraphHandler;


public class NodeBetweennessComputer extends NodeCentralityComputer {

	private PathSet sp;
	private int[] occ;	


	private boolean directed;

	/**
	 * THis default constructor setup the object to compute node betweeness
	 * based on the shortest paths, themselves based on the "length" attribute
	 */
	public NodeBetweennessComputer() {	 
		this(false);
	}
	 
	public NodeBetweennessComputer(boolean directed) {
		this.directed = directed;
	}
	
	public boolean theGreaterTheCentraler() {
		return true;
	}	
	
	@Override
	public String getResultName() {
		return "Node betweennesses";
	}	

	@Override
	public void init() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		if (sp == null) {
			sp = getShortestPathSet(agh, directed, false);
		}
		occ = new int[agh.getNumberOfNodes()];
		for (Path p : sp) {
			for (Integer i : p.getInnerNodes()) {
				occ[i]++;
			}
		}
	}

	@Override
	public double computeNodeCentrality(int nodeIndex) {
		if (directed) {
			int tot = sp.size();
			return (double)occ[nodeIndex]/(double)tot;
		} else {
			int tot = sp.size();
			int a = occ[nodeIndex];
			return (double)(a)/(double)tot;
		}
	}


/*
	public int getTimesNodeIsCrossed (AbstractGraphHandler agh, int node){
		int res = 0;
		Path p = new Path();
		for (NodeContainer nc : agh.getNodeContainers()){
			for (NodeContainer nc2 : agh.getNodeContainers()){
				if (nc.getIndex() != nc2.getIndex() && nc.getIndex() < nc2.getIndex()){
					p = sp.getResults().getPath(nc.getIndex(),nc2.getIndex());
					for ( int i = 0 ; i < p.size() ; i++){
						if ( p.get(i) == node ){
							res ++;
						}
					}
				}
			}
		}
		return res ;
	}

	public int getTotalCrossedNodes (AbstractGraphHandler agh){
		int total = 0;
		int var = 0;
		if (!hasOneComponent(agh)){
			return 0;
		}
		for ( int i = 0 ; i <= agh.getHighestNodeIndex() ; i++ ){
			var = getTimesNodeIsCrossed(agh,i);
			total += var;
		}
		return total;
	}*/

	/*	private float getNodeBetweenness (AbstractGraphHandler agh, int node){

		float res = 0;

		if (!hasOneComponent(agh)){
			return 0;
		}

		int var = getTimesNodeIsCrossed(agh, node);
		int tot = getTotalCrossedNodes(agh);

		res = (float)var/(float)tot;

		return res;
	}*/

}
