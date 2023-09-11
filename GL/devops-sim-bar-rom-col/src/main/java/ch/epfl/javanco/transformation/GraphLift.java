package ch.epfl.javanco.transformation;

import java.util.ArrayList;
import java.util.Collection;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class GraphLift {
	
	public static void liftGraph(AbstractGraphHandler agh, int factor) {
		liftGraph(agh, factor, stream);
	}
		
		
	public static void liftGraph(AbstractGraphHandler agh, int factor, PRNStream s) {		
		factor = factor-1;
		int totalNodes = agh.getNumberOfNodes();
		if (totalNodes <= 0) return;
		NodeContainer nc0 = agh.getNodeContainer(0);
		boolean withPositions = (nc0.attribute(XMLTagKeywords.POS_X, false) != null);
		
		Collection<NodeContainer> list = agh.getNodeContainers();
		
		for (int i = 0 ; i < factor ; i++) {
			for (NodeContainer nc : list) {
				if (withPositions) {
					agh.newNode(nc.getX()+(20*i), nc.getY());
				} else {
					agh.newNode();
				}
			}
		}
		
		for (LinkContainer lc : agh.getLinkContainers()) {
			ArrayList<Integer> starts = new ArrayList<Integer>();
			ArrayList<Integer> ends = new ArrayList<Integer>();
			int start = lc.getStartNodeIndex();
			int end = lc.getEndNodeIndex();
			starts.add(start);
			ends.add(end);
			for (int i = 0 ; i < factor ; i++) {
				starts.add(start + totalNodes*(1+i));
				ends.add(end + totalNodes*(1+i));
			}
			
			remap(agh, lc, starts, ends, s);
		}
	}
	
	private static PRNStream stream = PRNStream.getDefaultStream(0);

	private static void remap(AbstractGraphHandler agh, LinkContainer lc,
			ArrayList<Integer> starts, ArrayList<Integer> ends, PRNStream s) {
		starts = s.shuffle(starts);
		agh.removeElement(lc);
		for (int i = 0 ; i < starts.size() ; i++) {
			agh.newLink(starts.get(i), ends.get(i));
		}
		
	}

}
