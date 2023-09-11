package ch.epfl.javancox.inputs.topology.linker;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.network.NodeContainer;

public class DistanceAwarePlanarLinker extends PlanarLinker {


	public DistanceAwarePlanarLinker(int nbDesiredLinks) {
		super(nbDesiredLinks);
	}	
	
	protected List<NodeContainer> arrangeList(NodeContainer start, List<NodeContainer> candidates, PRNStream s) {
		List<NodeContainer> newList = new ArrayList<NodeContainer>(candidates.size());
		
		double[] weight = new double[candidates.size()];
		double total = 0;
		for (int i = 0 ; i < weight.length ; i++) {
			NodeContainer alt = candidates.get(i);
			double dist = alt.getDistanceTo(start);
			if (dist > 0) {
				weight[i] = 1f/dist;
			} else {
				weight[i] = 1;
			}
			total += weight[i];
		}
		NodeContainer[] nodes = candidates.toArray(new NodeContainer[candidates.size()]);
		while (candidates.size() > newList.size()) {
			double totSoFar = 0;
			double rand = s.nextDouble() * total;
			for (int i = 0 ; i < weight.length ; i++) {
				if (weight[i] > 0) {
					if (rand < totSoFar + weight[i]) {
						newList.add(nodes[i]);
						total -= weight[i];
						totSoFar += weight[i];
						weight[i] = -1;
						break;
					} else {
						totSoFar += weight[i];
					}
				}
			}
		}
		
		return newList;
	}	
	
	
	
}
