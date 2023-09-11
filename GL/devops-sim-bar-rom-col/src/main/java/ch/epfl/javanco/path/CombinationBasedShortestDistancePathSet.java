package ch.epfl.javanco.path;

import java.util.ArrayList;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.PathSet;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;



public class CombinationBasedShortestDistancePathSet extends PathSet {
	
	class WeigthedPath extends Path {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		double weigth;
		
		WeigthedPath(int i, int j) {
			super(i,j);
		}
	}
	
	public CombinationBasedShortestDistancePathSet(AbstractGraphHandler agh, String layerName, String attribName, boolean directed){
		super(agh.getNodeContainers().size(),null);
		compute(agh, layerName, attribName, directed);
	}

	private void compute(AbstractGraphHandler agh, String layerName, String attribName, boolean directed){
		// init
		ArrayList<WeigthedPath> news = new ArrayList<WeigthedPath>(0);
		ArrayList<WeigthedPath> olds = new ArrayList<WeigthedPath>(0);
		JavancoShortestPathSet refSet = new JavancoShortestPathSet(agh, layerName, attribName, directed, null);
		double[][] costs = refSet.getCosts();
		LayerContainer layC = agh.getLayerContainer(layerName);
		
		for (LinkContainer lc : layC.getLinkContainers()) {
			double val = lc.attribute(attribName).doubleValue();
			int start = lc.getStartNodeIndex();
			int end = lc.getEndNodeIndex();
			if (costs[start][end] == val) {
				WeigthedPath p = new WeigthedPath(start, end);
				WeigthedPath p2 = new WeigthedPath(end, start);
				news.add(p);
				news.add(p2);
				olds.add(p);
				olds.add(p2);
				addPath(p);
				p.weigth = costs[start][end];
			}
		}
		
		
		// loop
		while (news.size() > 0) {
			ArrayList<WeigthedPath> newNews = new ArrayList<WeigthedPath>();
			for (WeigthedPath lc : news) {
				for (WeigthedPath lc2 : olds) {
					if (lc.getLast() == lc2.getFirst()) {
						double tot = lc.weigth + lc2.weigth;
						if (tot == costs[lc.getFirst()][lc2.getLast()]) {
							WeigthedPath con = (WeigthedPath)lc.concat(lc2);
							con.weigth = tot;
							newNews.add(con);
							addPath(con);
						}
					}
				}
			}
		/*	for (WeigthedPath wp : news) {
				olds.add(wp);
			}*/
			news = newNews;
		}	
	}
	
		
}
