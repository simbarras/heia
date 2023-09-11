package ch.epfl.javanco.algorithms;

import java.util.Arrays;
import java.util.List;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.BoxedPriorityQueue;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class Dijkstra {
	
	/**
	 * Find path using all links on all layers, with distance given by the att attribute per link
	 * @param agh
	 * @param startNode
	 * @param endNode
	 * @param dir
	 * @param att 
	 * @return
	 */	
	public static Pair<Path, Double> getShortestPath(AbstractGraphHandler agh, int startNode, int endNode, String att, boolean dir) {
		double[] distances = new double[agh.getHighestNodeIndex() +1];
		Arrays.fill(distances, Double.POSITIVE_INFINITY);
		int[] prev = new int[distances.length];
		boolean[] done = new boolean[distances.length];
		Arrays.fill(done, false);
		BoxedPriorityQueue<Integer> queue = new BoxedPriorityQueue<Integer>();
		double lastDistance = internal(agh, startNode, endNode, distances, prev, queue, dir, att, done);

		return new Pair<Path, Double>(buildPath(startNode, endNode, prev), lastDistance);	
	}
	
	public static Path[] getShortestPaths(AbstractGraphHandler agh, int startNode, String att, boolean dir) {	
		double[] distances = new double[agh.getHighestNodeIndex() +1];
		int[] prev = new int[distances.length];
		boolean[] done = new boolean[distances.length];
		Arrays.fill(done, false);		
		Arrays.fill(prev, -1);
		BoxedPriorityQueue<Integer> queue = new BoxedPriorityQueue<Integer>();
		for (int i = 0 ; i <= agh.getHighestNodeIndex() ; i++) {
			if (done[i] == false) {
				queue = new BoxedPriorityQueue<Integer>();
				Arrays.fill(distances, Double.POSITIVE_INFINITY);
				internal(agh, startNode, i, distances, prev, queue, dir, att, done);
			}
		}
		Path[] paths = new Path[agh.getHighestNodeIndex()+1];
		for (int i = 0 ; i <= agh.getHighestNodeIndex() ; i++) {
			paths[i] = buildPath(startNode, i, prev);
		}
		return paths;
	}
	
	private static Path buildPath(int startNode, int endNode, int[] prev) {
		Path path = new Path();
		path.add(endNode);
		int cursor = endNode;
		while (cursor != startNode) {
			path.add(prev[cursor]);
			cursor = prev[cursor];
		}
		path.reverse();
		return path;
	}
	
	private static double internal(
			AbstractGraphHandler agh, 
			int startNode, 
			int endNode, 
			double[] distances, 
			int[] prev, 
			BoxedPriorityQueue<Integer> queue,
			boolean dir,
			String att,
			boolean[] done) {
		prev[startNode] = startNode;
		distances[startNode] = 0;
		queue.offer(startNode, 0);
		double lastDistance = Double.POSITIVE_INFINITY;
		while (!queue.isEmpty()) {
			lastDistance = queue.peekScore();
			int vertex = queue.pollElement();
			done[vertex] = true;
			
			List<LinkContainer> links;
			if (dir) {
				links = agh.getNodeContainer(vertex).getOutgoingLinks();
			} else {
				links = agh.getNodeContainer(vertex).getConnectedLinks();
			}
			for (int i = 0 ; i < links.size() ; i++) {
				LinkContainer lc = links.get(i);
				int altVertex = lc.getOtherNodeIndex(vertex);
				double dist = distances[vertex] + lc.attribute(att).doubleValue();
				if (dist < distances[altVertex]) {
					distances[altVertex] = dist;
					prev[altVertex] = vertex;
					queue.offer(altVertex, dist);
				}
			}
			if (vertex == endNode) {
				break;
			}
		}
		return lastDistance;
	}

}
