package ch.epfl.javanco.algorithms;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.NodeContainer;

public class BFS {
	
	public static Path getShortestPath(AbstractGraphHandler agh, int startNode, int endNode) {
		/**
		 * We should make this more efficient and have only one path created
		 */
		return getShortestPathsFrom(agh, startNode)[endNode];
	}	
	
	public static Path[] getShortestPathsFromUndirected(AbstractGraphHandler agh, int startNode) {	
		return getShortestPaths(agh, startNode, 2, null);
	}
	
	public static Path[] getShortestPathsFrom(AbstractGraphHandler agh, int startNode) {
		return getShortestPathsFrom(agh, startNode, null);
	}
	
	public static Path[] getShortestPathsFrom(AbstractGraphHandler agh, int startNode,  LayerContainer layer) {
		return getShortestPaths(agh, startNode, 0, layer);
	}
	
	public static Path[] getShortestPathsTo(AbstractGraphHandler agh, int endNode) {
		return getShortestPathsTo(agh, endNode,  null);
	}	
	
	public static Path[] getShortestPathsTo(AbstractGraphHandler agh, int endNode, LayerContainer layer) {
		return getShortestPaths(agh, endNode, 1, layer);
	}
	
	public static Path[] getShortestPathsUndirectedFrom(AbstractGraphHandler agh, int startNode) {
		return getShortestPathsUndirectedFrom(agh, startNode, null);
	}
	
	public static Path[] getShortestPathsUndirectedFrom(AbstractGraphHandler agh, int startNode, LayerContainer layer) {
		return getShortestPaths(agh, startNode, 2, layer);
	}
	
	public static Path[] getShortestPathsUndirectedTo(AbstractGraphHandler agh, int endNode) {
		return getShortestPathsUndirectedFrom(agh, endNode, null);
	}
	
	public static Path[] getShortestPathsUndirectedTo(AbstractGraphHandler agh, int endNode, LayerContainer layer) {
		return getShortestPaths(agh, endNode, 3, layer);
	}	

	private static Path[] getShortestPaths(AbstractGraphHandler agh, int startNode, int mode, LayerContainer layer) {
		Path[] paths = new Path[agh.getHighestNodeIndex()+1];
		int[] pred = getBFSTree(agh, startNode, mode, layer);
		for (int i = 0 ; i < paths.length ; i++) {
			if (i == startNode) continue;
			paths[i] = new Path(i);
			int current = i;
			while (current != startNode && current >= 0) {
				current = pred[current];
				paths[i].add(current);
			}
			if (current == -1) {
				paths[i] = null;
			} else {
				if (mode % 2 == 0)
					paths[i].reverse();
			}
		}
		
		return paths;
	}
	
	public static int[] getDistancesFromUndirected(AbstractGraphHandler agh, int startNode) {
		return getDistances(agh, startNode, 2, null);
	}
	
	public static int[][] getDistancesUndirected(AbstractGraphHandler agh) {
		int maxId = agh.getHighestNodeIndex();
		int[][] dist = new int[maxId+1][maxId+1];
		for (int i = 0 ; i < dist.length ; i++) {
			dist[i] = getDistancesFromUndirected(agh, i);
		}
		return dist;
	}
	
	public static double getAverageDistanceFromUndirected(AbstractGraphHandler agh, int startNode) {
		int[] dist = getDistancesFromUndirected(agh, startNode);
		int total = 0;
		for (int i = 0 ; i < dist.length ; i++) {
			if (dist[i] == Integer.MAX_VALUE) {
				return Integer.MAX_VALUE;
			}
			total += dist[i];
		}
		return (double)total/(double)(dist.length-1);
	}
	
	public static double getAverageDistanceUndirected(AbstractGraphHandler agh) {
		int maxId = agh.getHighestNodeIndex();
		int total = 0;
		int pairs = 0;
		for (int i = 0 ; i <= maxId ; i++) {
			int[] distances = getDistancesFromUndirected(agh, i);
			for (int j = 0 ; j < distances.length ; j++) {
				if (j == i) break;
				if (distances[j] == Integer.MAX_VALUE) {
					return Integer.MAX_VALUE;
				}
				total += distances[j];
				pairs++;
			}
		}
		return (double)total/(double)pairs;
	}
	
	public static int[] getDistances(AbstractGraphHandler agh, int startNode, int mode, LayerContainer layer) {
		int[] pred = getBFSTree(agh, startNode, mode, layer);
		int[] distances = new int[pred.length];
		for (int i = 0 ; i < distances.length ; i++) {
			if (i == startNode) distances[i] = 0;	
			int counter = 0;
			int current = i;
			while (current != startNode && current >= 0) {
				current = pred[current];
				counter++;
			}
			if (current < 0) {
				distances[i] = Integer.MAX_VALUE;
			} else {
				distances[i] = counter;
			}
		}
		return distances;
	}
	
	// TODO : implement it by layer and for undirected case
	private static int[] getBFSTree(AbstractGraphHandler agh, int node, int mode, LayerContainer layer) {
		if (agh.getNodeContainer(node) == null) return new int[0];
		if (layer != null) mode = mode + 4;
		int[] predecessors = new int[agh.getHighestNodeIndex()+1];
		Arrays.fill(predecessors, -1);
		
		LinkedList<NodeContainer> lc = new LinkedList<NodeContainer>();	
		// init
		lc.add(agh.getNodeContainer(node));
		predecessors[node] = node;
		while (lc.size() > 0) {
			NodeContainer n = lc.pollFirst();
			int index = n.getIndex();
			List<NodeContainer> nexts;
			switch(mode) {
			case 0:
				nexts = n.getAllOutgoingLinksExtremities();
				break;
			case 1:
				nexts = n.getAllIncomingLinksExtremities();
				break;
			case 2: case 3:
				nexts = n.getAllConnectedLinkExtremities();
				break;
			case 4:
				nexts = n.getOutgoingLinksExtremities(layer);
				break;
			case 5:
				nexts = n.getIncomingLinksExtremities(layer);
				break;
			case 6: case 7:
				nexts = n.getConnectedNodes(layer);
				break;
			default:
				nexts = n.getAllConnectedLinkExtremities();					
			} 
			Collections.sort(nexts);
			for (int i = 0 ; i < nexts.size() ; i++) {
				NodeContainer nc = nexts.get(i);
				int itIndex = nc.getIndex();
				if (predecessors[itIndex] < 0) {
					predecessors[itIndex] = index;
					lc.addLast(nc);
				}
			}
		}
		return predecessors;
	}
	
	public static int[] getBFSPredecessors(AbstractGraphHandler agh, int start, String layerName, String attName, boolean directed) {
		throw new IllegalStateException("Not implemented yet");
	}
	


}
