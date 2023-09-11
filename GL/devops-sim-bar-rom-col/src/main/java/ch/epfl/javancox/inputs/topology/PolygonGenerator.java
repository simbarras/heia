package ch.epfl.javancox.inputs.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class PolygonGenerator extends AbstractDeterministicGenerator {

	private int numberOfNodes;
	private boolean connected;
	private int ray;
	private int centerX;
	private int centerY;
	private int chordLength;
	private double angleOffset;
	private Integer[] offsetVector;
	private boolean invert;

	private int method;

	@Override
	public Map<String, String> getGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("polygon_connected",connected+"");
		switch(method) {
		case 0:
			map.put("polygon_type","simple_polygon");
			break;
		case 1:
			map.put("polygon_type","shortcutted_polygon");
			map.put("polygon_shortcuts_offset_vector",Arrays.toString(offsetVector));
			break;
		case 2:
			map.put("polygon_type","chordal_polygon");
			map.put("chord_length",chordLength+"");
			break;
		}
		return map;
	}
	

	public PolygonGenerator(int numberOfNodes, short chord1) {
		this(numberOfNodes, "1," + chord1);
	}	
	
	public PolygonGenerator(int numberOfNodes, short chord1, short chord2) {
		this(numberOfNodes, "1," + chord1 + "," + chord2);
	}	
	
	public PolygonGenerator(int numberOfNodes, short chord1, short chord2, short chord3) {
		this(numberOfNodes, "1," + chord1 + "," + chord2 + "," + chord3);
	}		
	

	/** Constructor to use for a simple polygonal topology
	 */
	public PolygonGenerator(int numberOfNodes, boolean connected,int ray,int centerX, int centerY, double angleOffset) {
		this.numberOfNodes = numberOfNodes;
		this.connected = connected;
		this.ray = ray;
		this.centerX = centerX;
		this.centerY = centerY;
		this.angleOffset = angleOffset;
		method = 0;
	}	
	

	/** Constructor to use for a simple polygonal topology
	 */
	public PolygonGenerator(int numberOfNodes, boolean connected,int ray,int centerX, int centerY) {
		this.numberOfNodes = numberOfNodes;
		this.connected = connected;
		this.ray = ray;
		this.centerX = centerX;
		this.centerY = centerY;
		method = 0;
	}
	/** Constructor to use for a polygonal with some "short-cuts"
	 */
	public PolygonGenerator(int numberOfNodes, int ray,int centerX, int centerY, Integer[] offsetVector, boolean invert) {
		this.numberOfNodes = numberOfNodes;
		this.ray = ray;
		this.centerX = centerX;
		this.centerY = centerY;
		this.offsetVector = offsetVector;
		this.invert = invert;
		method = 1;
	}
	
	/** Constructor to use for a polygonal with some "short-cuts"
	 */
	public PolygonGenerator(int numberOfNodes, int ray,int centerX, int centerY, String offsetVector, boolean invert) {
		this.numberOfNodes = numberOfNodes;
		this.ray = ray;
		this.centerX = centerX;
		this.centerY = centerY;
		this.offsetVector = TypeParser.parseIntegerArray(offsetVector);
		this.invert = invert;
		method = 1;
	}	
	
	
	/** Constructor to use for a polygonal with some "short-cuts"
	 */
	public PolygonGenerator(int numberOfNodes, String offsetVector) {
		this.numberOfNodes = numberOfNodes;
		this.ray = 200;
		this.centerX = 0;
		this.centerY = 0;
		this.offsetVector = TypeParser.parseIntegerArray(offsetVector);
		this.invert = false;
		method = 1;
	}		

	public PolygonGenerator(int numberOfNodes,int ray,int centerX,int centerY,int chordLength) {
		this.numberOfNodes = numberOfNodes;
		this.ray = ray;
		this.centerX = centerX;
		this.centerY = centerY;
		this.chordLength = chordLength;
		method = 2;
	}

	public PolygonGenerator(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
		this.ray = 300;
		this.centerX = 200;
		this.centerY = 200;
		method = 0;
		connected = true;
	}

	public void setRadius(int r) {
		this.ray = r;
	}


	@Override
	public void generate(AbstractGraphHandler agh) {
		switch (method) {
		case 0:
			generatePolygon(agh, numberOfNodes, connected, ray, centerX, centerY);
			break;
		case 1:
			generateSpecialPolygon(agh, numberOfNodes, ray, centerX, centerY, offsetVector);
			break;
		case 2:
			generateChordalRing(agh, numberOfNodes, ray, centerX, centerY, chordLength);
			break;
		}

	}

	@Override
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	@Override
	public String getName() {
		return "Polygon_based_generator";
	}

	/**
	 * Generate a Polygon topology
	 * @param agh The Abstract graph handler
	 * @param layerName The layer
	 * @param numberOfNodes The number of nodes of the topology
	 * @param connected If true, the nodes are connected  with the next neighbour
	 * @param ray The ray of the circle
	 * @param centerX The X coordinate of the topology center
	 * @param centerY The Y coordinate of the topology center
	 * 
	 */
	public void generatePolygon(AbstractGraphHandler agh,
			int numberOfNodes,
			boolean connected,
			int ray,
			int centerX,
			int centerY)  {
		ensureLayer(agh);
		createPolygonNodes(agh,numberOfNodes,ray,centerX,centerY, angleOffset);
		if (connected){
			fillUp(agh,numberOfNodes,1);
		}
	}

	/**
	 * Generate a specific Polygon form by connecting the nodes with the given neighbours in the offset vector
	 * @param agh The Abstract Graph Handler
	 * @param layerName The layer
	 * @param numberOfNodes The number of nodes of the topology
	 * @param ray The ray of the circle
	 * @param centerX The X coordinate of the topology center
	 * @param centerY The Y coordinate of the topology center
	 * @param offsetVector[] Each node has a link with the node situated to a certain distance from it. These distances
	 *						 are given in this offsetVector
	 */
	@SuppressWarnings("all")	
	public void generateSpecialPolygon(AbstractGraphHandler agh,
			int numberOfNodes,
			int ray,
			int centerX,
			int centerY,
			Integer[] offsetVector) {
		ensureLayer(agh);
		int[] indexes = createPolygonNodes(agh,numberOfNodes,ray,centerX,centerY);
		Integer[] arra;
		if (invert) {
		//	Integer[] rem = TypeParser.parseIntegerArray(offsetVector);
			ArrayList toRem = new ArrayList(Arrays.asList(offsetVector));
			Integer[] all = getIntegerArray(1,numberOfNodes/2);
			ArrayList add = new ArrayList(Arrays.asList(all));
			add.removeAll(toRem);
			arra = (Integer[])add.toArray(new Integer[add.size()]);
		} else {
			arra = offsetVector;//TypeParser.parseIntegerArray(offsetVector);
		}
		for(int off:arra){
			fillUp(agh,numberOfNodes,off, indexes);
		}
	}

	/**
	 * A chordal ring is a cicular graph. Each chord connects every pair of nodes that are at a distance <i>chordLength</i>
	 * In our case the sources nodes are the odd nodes
	 * @param agh The AbstractGraphHandler
	 * @param layerName The layer
	 * @param numberOfNodes The number of nodes of the topology
	 * @param ray The ray of the circle
	 * @param centerX The X coordinate of the topology center
	 * @param centerY The Y coordinate of the topology center
	 */
	public void generateChordalRing(AbstractGraphHandler agh,
			int numberOfNodes,
			int ray,
			int centerX,
			int centerY,
			int chordLength) {
		ensureLayer(agh);
		createPolygonNodes(agh,numberOfNodes,ray,centerX,centerY);
		fillUp(agh,numberOfNodes,1);

		for(int s=agh.getSmallestNodeIndex()+1;s<=agh.getHighestNodeIndex();s=s+2){
			int d=(s+chordLength)%numberOfNodes;
			if((agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) &&(s!=d)){
				agh.newLink(s,d);
			}
		}
	}

	public static class Ring extends WebTopologyGeneratorStub {
		/**
		 * Generate a Polygon topology
		 * @param agh The Abstract graph handler
		 * @param layerName The layer
		 * @param numberOfNodes The number of nodes of the topology
		 * @param connected If true, the nodes are connected  with the next neighbour
		 * @param ray The ray of the circle
		 * @param centerX The X coordinate of the topology center
		 * @param centerY The Y coordinate of the topology center
		 * 
		 */
		@MethodDef
		public String circleOrRing(AbstractGraphHandler agh,
				@ParameterDef (name="Graph order (number of vertices)")int numberOfNodes,
				@ForcedParameterDef(possibleValues={"true", "false"})
				@ParameterDef (name="Connected")boolean connected) {

			String s = test(numberOfNodes);	
			if (s != null) return s;
			
			PolygonGenerator backGen =
				new PolygonGenerator(numberOfNodes, connected, 300, 350, 350);
	
			backGen.generate(agh);
			return null;
		}
		
		/**
		 * A chordal ring is a cicular graph. Each chord connects every pair of nodes that are at a distance <i>chordLength</i>
		 * In our case the sources nodes are the odd nodes
		 * @param agh The AbstractGraphHandler
		 * @param layerName The layer
		 * @param numberOfNodes The number of nodes of the topology
		 * @param ray The ray of the circle
		 * @param centerX The X coordinate of the topology center
		 * @param centerY The Y coordinate of the topology center
		 */
		@Deprecated
		public String ringWithChord(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int numberOfNodes,
				@ParameterDef (name="Length of the chord") int chordLength) {

			String s = test(numberOfNodes);	
			if (s != null) return s;

			PolygonGenerator backGen =
				new PolygonGenerator(numberOfNodes, 300, 350, 350, chordLength);

			backGen.generate(agh);
			return null;
		}		

		/**
		 * Generate a specific Polygon form by connecting the nodes with the given neighbours in the offset vector
		 * @param agh The Abstract Graph Handler
		 * @param layerName The layer
		 * @param numberOfNodes The number of nodes of the topology
		 * @param ray The ray of the circle
		 * @param centerX The X coordinate of the topology center
		 * @param centerY The Y coordinate of the topology center
		 * @param offsetVector[] Each node has a link with the node situated to a certain distance from it. These distances
		 *						 are given in this offsetVector
		 */
		@MethodDef
		public String linkWithNDistances(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int numberOfNodes,
				@ParameterDef (name="Offset vector (e.g.\"1,2\")") String offsetVector) {

			String s = test(numberOfNodes);	
			if (s != null) return s;
			Integer[] rem = TypeParser.parseIntegerArray(offsetVector);
			PolygonGenerator backGen =
				new PolygonGenerator(numberOfNodes, 300, 350, 350, rem, false);

			backGen.generate(agh);
			return null;	
		}
		
		@MethodDef
		public String linkWithAllButNDistances(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int numberOfNodes,
				@ParameterDef (name="Inverse offset vector (e.g.\"3,5\")", maxArgLength=20) String offsetVector) {

			String s = test(numberOfNodes);	
			if (s != null) return s;
			Integer[] rem = TypeParser.parseIntegerArray(offsetVector);
			PolygonGenerator backGen =
				new PolygonGenerator(numberOfNodes, 300, 350, 350, rem, true);

			backGen.generate(agh);
			return null;	
		}		




	}

}
