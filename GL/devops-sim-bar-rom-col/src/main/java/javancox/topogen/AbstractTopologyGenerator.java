package javancox.topogen;


import java.awt.Point;
import java.util.Collection;
import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.NodeContainer;

public abstract class AbstractTopologyGenerator extends AbstractExperimentBlock {

	/**
	 * The default topology layer name is "physical"
	 */
	public static final String DEFAULT_TOPOLOGY_LAYER_NAME = "physical";

	public abstract void generate(AbstractGraphHandler agh);
	public abstract String getName();
	public abstract String getFullName();
	public abstract Map<String, String> getGeneratorParameters();
	/**
	 * Common field to all generators : the number of nodes
	 */
	public abstract int getNumberOfNodes();

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = getGeneratorParameters();
		map.put("topology_generator_name",getName());
		map.put("number_of_nodes",getNumberOfNodes()+"");
		return map;
	}
	
	public AbstractGraphHandler generate() {
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		agh.newLayer("physical");
		generate(agh);
		return agh;
	}
	
	/**
	 * This method can be overriden to prevent generation of extremely large topologies
	 * @return
	 */
	public boolean willSkip() {
		return false;
	}

	/**
	 * Returns the DEFAULT_TOPOLOGY_LAYER_NAME = physical if not overriden
	 */
	public String getTopologyLayerName() {
		return DEFAULT_TOPOLOGY_LAYER_NAME;
	}

	public static int getClosestNode(AbstractGraphHandler agh, int currentNode,Collection<NodeContainer> nodes ){
		float min = Float.POSITIVE_INFINITY;
		int toReturn = 0;
		Point thisPoint = agh.getNodeContainer(currentNode).getCoordinate();
		for(NodeContainer nc : nodes){
			Point otherPoint = nc.getCoordinate();
			float currentDistance = (float) thisPoint.distance(otherPoint);
			if (currentDistance<min) {
				min = currentDistance;
				toReturn = nc.getIndex();
			}
		}
		return toReturn;
	}

	/**
	 * Create a link from every node to the node situated at a <i>offset</i> distance from it
	 * @param agh The AbstractGrapgHandler
	 * @param numberOfNodes The number of nodes of the topology
	 * @param offset The location of the destination from the source of the link
	 */
	protected void fillUp(AbstractGraphHandler agh,int numberOfNodes,int offset){
		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			int d=(s+offset)%numberOfNodes;
			if((agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null)&&(s!=d)){
				agh.newLink(s,d);
				//agh.newLink(d,s);
			}
		}
	}
	
	/**
	 * Create a link from every node to the node situated at a <i>offset</i> distance from it
	 * @param agh The AbstractGrapgHandler
	 * @param numberOfNodes The number of nodes of the topology
	 * @param offset The location of the destination from the source of the link
	 */
	protected void fillUp(AbstractGraphHandler agh,int numberOfNodes,int offset, int[] indexes){
		for (int i = 0 ; i < indexes.length ; i++) {
			int s = indexes[i];
			int d = indexes[(i+offset)%numberOfNodes];
		//for (int s: indexes) {
		//	int d=(s+offset)%numberOfNodes;
			if((agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null)&&(s!=d)){
				agh.newLink(s,d);
				//agh.newLink(d,s);
			}
		}
	}	

	protected void ensureLayer(AbstractGraphHandler agh) {
		if (agh.getLayerContainer(getTopologyLayerName()) == null) {
			agh.newLayer(getTopologyLayerName());
		} else {
			agh.setEditedLayer(agh.getLayerContainer(getTopologyLayerName()));
		}
	}

	protected static void ensureLayerStatic(AbstractGraphHandler agh) {
		if (agh.getLayerContainer(DEFAULT_TOPOLOGY_LAYER_NAME) == null) {
			agh.newLayer(DEFAULT_TOPOLOGY_LAYER_NAME);
		} else {
			agh.setEditedLayer(agh.getLayerContainer(DEFAULT_TOPOLOGY_LAYER_NAME));
		}
	}
	
	/**
	 * Creates the nodes of the polygon
	 * @param agh The AbstractGraphHandler
	 * @param numberOfNodes The number of nodes of the topology
	 * @param ray The ray of the circle
	 * @param centerX The X coordinate of the topology center
	 * @param centerY The Y coordinate of the topology center
	 */
	public static int[] createPolygonNodes(
			AbstractGraphHandler agh, 
			int numberOfNodes,
			int ray, 
			int centerX, 
			int centerY,
			double angleOffset) {
		int[] indexes = new int[numberOfNodes];
		double pisur2 = (Math.PI/2d);
		double circlePart = (2*Math.PI/numberOfNodes);
		for (int i=0;i<numberOfNodes;i++){
			double x = centerX + ray * Math.sin(i*circlePart-pisur2 + angleOffset);
			double y = centerY + ray * Math.cos(i*circlePart-pisur2 + angleOffset);
			NodeContainer nc = agh.newNode((int)x,(int)y);
			indexes[i] = nc.getIndex();
		}
		return indexes;
	}
	
	/**
	 * Creates the nodes of the polygon
	 * @param agh The AbstractGraphHandler
	 * @param numberOfNodes The number of nodes of the topology
	 * @param ray The ray of the circle
	 * @param centerX The X coordinate of the topology center
	 * @param centerY The Y coordinate of the topology center
	 */
	public static int[] createPolygonNodes(AbstractGraphHandler agh, int numberOfNodes,int ray, int centerX, int centerY) {
		return createPolygonNodes(agh, numberOfNodes, ray, centerX, centerY, 0);
	}	
	
	public static void createGrid(AbstractGraphHandler agh, int nbNodes, int width, int linkLength) {
		int x = 0;
		int y = 0;
		int created = 0;
		while (created < nbNodes) {
			agh.newNode(x,y);
			x += linkLength;
			if (x >= width*linkLength) {
				x = 0;
				y += linkLength;
			}
			created++;
		}
		for (int i = 1 ; i < nbNodes ; i++) {
			if (i%width != 0) {
				agh.newLink(i-1, i);
			}
			if (i >= width) {
				agh.newLink(i-(width), i);
			}
		}
	}	
	
	public static abstract class WebTopologyGeneratorStub extends PrimitiveTypeGeneratorStub {
		
		protected String test(int nbNodes) {
			return test(nbNodes, 500);
		}
		
		protected String test(int nbNodes, int limit) {		
			if (nbNodes > limit || nbNodes < 1) {
				return "Number of nodes must be comprised between 1 and " + limit;
			} else return null;		
		}
	}

}
