
package ch.epfl.javancox.inputs.topology.linker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.Util2DFunctions;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class PlanarLinker extends AbstractNodeLinker {

	public abstract static class LinkFilter {
		public abstract boolean filter(NodeContainer nc1, NodeContainer nc2);
	}

	private int Nlinks;
	private LinkFilter filter = null;

	public PlanarLinker(int nbDesiredLinks) {
		this.Nlinks = nbDesiredLinks;
	}

	public void setLinkFilter(LinkFilter filter) {
		this.filter = filter;
	}

	@Override
	public void linkNodes(AbstractGraphHandler agh, PRNStream stream) {
		int aux= agh.getNumberOfNodes();

		//	Vector<NodeContainer> vec1 = new Vector<NodeContainer>(aux);
		Vector<NodeContainer> vec2 = new Vector<NodeContainer>(aux);
		vec2.setSize(aux);
		List<LinkContainer> links = agh.getLinkContainers();

		Set<Integer> col = new TreeSet<Integer>();

		for (NodeContainer nc : agh.getNodeContainers()) {
			//		vec1.set(nc.getIndex(), nc);
			vec2.set(nc.getIndex(), nc);
			col.add(nc.getIndex());
		}



		while (Nlinks > 0 && !col.isEmpty()) {
			Integer start = stream.nextInt(aux-1);
			if (col.contains(start) == false) {
				continue;
			}
			NodeContainer startN = agh.getNodeContainer(start);
			List<NodeContainer> newList = arrangeList(startN, vec2, stream);
			NodeContainer nc = trySuccessively(agh, startN, newList, links);
			if (nc != null && filter.filter(startN, nc)) {
				LinkContainer lc = agh.newLink(startN, nc);
				links.add(lc);
				Nlinks--;
			} else {
				col.remove(start);
			}
		}
	}


	protected List<NodeContainer> arrangeList(NodeContainer start, List<NodeContainer> candidates, PRNStream s) {
		Collections.shuffle(candidates, s.toRandom());
		return candidates;
	}

	private NodeContainer trySuccessively(AbstractGraphHandler agh, 
										  NodeContainer start, 
										  List<NodeContainer> candidates, 
										  List<LinkContainer> others) {
		for (NodeContainer nc : candidates) {
			if (nc == null) {
				continue;
			}
			if (nc == start) {
				continue;
			}
			if (agh.getLinkContainers(start.getIndex(), nc.getIndex(), false).size() > 0) {
				continue;
			}
			if (Util2DFunctions.intersects(start, nc, others) == false) {
				if (filter == null || filter.filter(start, nc)) {
					return nc;
				}
			}
		}
		return null;
	}



	@Override
	public Map<String, String> getLinkerParameters() {
		Map<String, String> map = getNewMap(1);
		return map;
	}

}
