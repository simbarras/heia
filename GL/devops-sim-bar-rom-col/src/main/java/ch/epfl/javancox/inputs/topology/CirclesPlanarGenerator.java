package ch.epfl.javancox.inputs.topology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.MersenneTwister;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.graphics.Util2DFunctions;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class CirclesPlanarGenerator extends AbstractRandomPlanarGenerator {

	private int radius;
	private double limitAngle;
	private double adjP;
	private double altP;
	protected PRNStream stream__;
	/**
	 * A number comprised between 0 and 1. 0 Means that distances can be
	 * null with previous node, and can extend until the double of the
	 * radius. 1 means a ridig honeynest structure
	 */
	private double radiusRange;
	/**
	 * This field is here for convenience and to avoid inverting
	 * the radiusRange each time
	 */
	private double invRadRange;

	@Override
	public Map<String, String> getRandomGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("radius",radius+"");
		map.put("limit_angle",limitAngle+"");
		map.put("adjacent_node_link_probability", adjP+"");
		map.put("inter_orbit_node_link_probability", altP+"");
		map.put("radius_range", radiusRange+"");
		map.put("PRG seed", stream__.getSeed()+"");
		return map;
	}

	@Override
	public String getName() {
		return "circle-based-generator";
	}

	public CirclesPlanarGenerator(int nbNodes) {
		this(nbNodes,60, 0.2, 1, 0.6, 0.6, PRNStream.getRandomStream());
	}
	
	public CirclesPlanarGenerator(@ParameterDef (name="Number of nodes") int nbNodes, 
								  @ParameterDef (name="Seed") int seed) {
		this(nbNodes,60, 0.2, 1, 0.6, 0.6, PRNStream.getDefaultStream(seed));
	}

	public CirclesPlanarGenerator(int nbNodes, PRNStream stream) {
		this(nbNodes,60, 0.2, 1, 0.6, 0.6, stream);
	}
	
	public static class DefaultCircle15 extends CirclesPlanarGenerator {
		public DefaultCircle15() {
			super(15, new MersenneTwister(1));
		}
		
		@Override
		public void generateRandomTopology(AbstractGraphHandler agh) {	
			resetStream();
			stream__.reset();
			super.generateRandomTopology(agh);
		}	
	}
	
	public CirclesPlanarGenerator(@ParamName(name = "Number of nodes") int nbNodes,
		    @ParamName(name = "Scale", default_="60") int scale,
			@ParamName(name = "Noise", default_="0.2") double radRange,
			@ParamName(name = "Circle angle", default_="1") double limitAngle,
			@ParamName(name = "Intra circle connectivity", default_="0.6") double adjP,
			@ParamName(name = "Inter circle connectivity", default_="0.6") double altP) {
		this(nbNodes, scale, radRange, limitAngle, adjP, altP, PRNStream.getRandomStream());
	}		

	public CirclesPlanarGenerator(@ParamName(name = "Number of nodes") int nbNodes,
		    @ParamName(name = "Scale", default_="60") int scale,
			@ParamName(name = "Noise", default_="0.2") double radRange,
			@ParamName(name = "Circle angle", default_="1") double limitAngle,
			@ParamName(name = "Intra circle connectivity", default_="0.6") double adjP,
			@ParamName(name = "Inter circle connectivity", default_="0.6") double altP,
			PRNStream stream) {
		super(nbNodes, stream);
		this.stream__ = stream;
		configure(scale, radRange, limitAngle, adjP, altP);
	}

	private void configure(int scale,
			double radRange,
			double limitAngle,
			double adjP,
			double altP) {
		if (adjP < 0.2) {
			System.out.println("WARNING:valors smaller than 0.2 for adjP not recommended");
		}
		this.limitAngle = limitAngle*2*Math.PI;
		this.adjP = adjP;
		this.altP = altP;
		this.radius = scale;
		this.radiusRange = radRange;
		this.invRadRange = 2-(radiusRange*2);
	}


	@Override
	public void generateRandomTopology(AbstractGraphHandler agh) {
		ensureLayer(agh);
		agh.activateMainDataHandler();
		agh.activateGraphicalDataHandler();
		placeNodesAndLinks(agh, nbNodes);
		super.reconnectComponents(agh);
	}

	/*	public void generate(AbstractGraphHandler agh, int seed, int nbNodes) {
		super.resetPRNStream(seed);
		generate(agh,nbNodes);
	}*/

	private void placeNodesAndLinks(AbstractGraphHandler agh, int nbNodes) {
		boolean stilToLink = false;
		ArrayList<NodeContainer> levelI = new ArrayList<NodeContainer>();
		ArrayList<NodeContainer> levelI2 = new ArrayList<NodeContainer>();
		ArrayList<NodeContainer> levelI3 = new ArrayList<NodeContainer>();


		if (nbNodes <= 0) {
			return;
		}
		NodeContainer nc = agh.newNode(200,200);
		levelI2.add(nc);
		int createdNode = 1;
		int actualOrbit = 1;
		double actualAngle = 0;
		nodes = new NodeContainer[nbNodes];
		nodes[0]= nc;
		while (createdNode < nbNodes) {
			double prod = (actualOrbit+((invRadRange*stream__.nextDouble())-(1-radiusRange))/2)*radius;
			double sin = Math.sin(actualAngle);
			double cos = Math.cos(actualAngle);
			int val_x = (int)(prod*sin);
			int val_y = (int)(prod*cos);
			nc = agh.newNode(val_x+200, val_y+200);
			nodes[createdNode] = nc;
			stilToLink = true;
			levelI.add(nc);
			createdNode++;
			double pi3 = ((Math.PI/3)/actualOrbit);
			if (actualAngle+pi3 > limitAngle+0.0000001) {
				actualOrbit++;
				actualAngle = 0;
				placeLinks(levelI, levelI2, levelI3, agh);
				stilToLink = false;
				levelI3 = levelI2;
				levelI2 = levelI;
				levelI = new ArrayList<NodeContainer>();
			} else {
				actualAngle += pi3;
			}
			double rand = stream__.nextDouble()/2;
			double d = pi3 * (((invRadRange*rand)-(1-radiusRange))/2);
			actualAngle += d;
		}
		if (stilToLink) {
			placeLinks(levelI, levelI2, levelI3, agh);
		}
	}

	private transient NodeContainer[] nodes;

	private void placeLinks(ArrayList<NodeContainer> li,
			ArrayList<NodeContainer> i2,
			ArrayList<NodeContainer> i3,
			AbstractGraphHandler agh) {
		ArrayList<NodeContainer> candidates = new ArrayList<NodeContainer>();
		ArrayList<LinkContainer> potentialConfLinks = new ArrayList<LinkContainer>();
		for (NodeContainer l2 : i2) {
			candidates.add(l2);
			potentialConfLinks.addAll(l2.getConnectedLinks());
		}
		for (int i = 0 ; i < li.size()-1 ; i++) {
			if (stream__.nextDouble() < adjP) {
				potentialConfLinks.add(agh.newLink(li.get(i).getIndex(), li.get(i+1).getIndex()));
			}
		}
		candidates.addAll(i3);
		Collections.shuffle(li, stream__.toRandom());

		Util2DFunctions dfunc = new Util2DFunctions(agh);
		for (int i = 0 ; i < li.size() ; i++) {
			NodeContainer nc = li.get(i);
			for (NodeContainer candi : candidates) {
				boolean b = dfunc.intersects_(nc, candi, potentialConfLinks);
				if (b == false) {
					if (testLink(nc, candi, potentialConfLinks)) {
						if (Util2DFunctions.distance(nc, candi) < this.radius*2) {
							if ((stream__.nextDouble() < adjP) || nc.getConnectedLinks().size() == 0) {
								potentialConfLinks.add(agh.newLink(nc.getIndex(), candi.getIndex()));
								//	System.out.println(nc.getIndex()+"-"+ candi.getIndex());
							}
						}
					} else {
						//	System.out.println(nc.getIndex()+"&&&&"+ candi.getIndex());
					}
				}
			}
		}
	}

	private boolean testLink(NodeContainer nc, NodeContainer nc2, ArrayList<LinkContainer> col) {
		for (LinkContainer lc : col) {
			double angle;

			NodeContainer lcS = nodes[lc.getStartNodeIndex()];
			NodeContainer lcE = nodes[lc.getEndNodeIndex()];

			if (lcS.equals(nc)) {
				angle = Util2DFunctions.angle(nc, nc2, lcE);
			} else if (lcS.equals(nc2)) {
				angle = Util2DFunctions.angle(nc2, nc, lcE);
			} else if (lcE.equals(nc)) {
				angle = Util2DFunctions.angle(nc, nc2, lcS);
			} else if (lcE.equals(nc2)) {
				angle = Util2DFunctions.angle(nc2, nc, lcS);
			} else {
				angle = 90;
			}
			if (Math.abs(angle) < 0.15) {
				return false;
			}
		}
		return true;
	}

	/*	public static void main(String args[]) {
		CirclesPlanarGenerator gen = new CirclesPlanarGenerator(60, 0.3, 0.5, 0.2, 0.1);
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler();
		gen.generate(agh, 24);
		GraphicalNetworkDisplayer.displayGraph(agh, 800,800, 1f, 0.6f);
		agh.saveNetwork("circular.xml");
	}*/

	public static void main(String args[]) throws java.io.IOException {
		double[] nodeLocRand = new double[]{0.1, 0.4, 0.7, 1};
		double[] angles = new double[]{0.5, 1};
		double[] adjP  = new double[]{0.5, 0.6, 0.8};
		double[] altP  = new double[]{0.1, 0.4, 0.7, 1};
		int[]  nodesSizes = new int[]{15,25,50,75};
		int[] seeds = new int[]{866,3544,232,4543};

		AbstractGraphHandler agh = null;
		for (double locR : nodeLocRand) {
			for (double angl : angles) {
				for (double adjp : adjP) {
					for (double alt : altP) {
						for (int size :nodesSizes) {
							for (int seed : seeds) {
								CirclesPlanarGenerator cirPlan = new CirclesPlanarGenerator(size,60, locR, angl, adjp, alt, PRNStream.getDefaultStream(seed));
								agh = Javanco.getDefaultGraphHandler(false);
								cirPlan.generate(agh);
								System.out.print(":");
							}

						}

					}
				}
			}
		}
		agh.saveNetwork("circularLast.xml");
	}

	public static class RandomCircles extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateRandomPolarGraph(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes) {
			
			String s = test(numberOfNodes);	
			if (s != null) return s;
					
			CirclesPlanarGenerator gen = new CirclesPlanarGenerator(numberOfNodes);
			gen.generate(agh);
			return null;
		}

		@MethodDef
		public String generateRandomPolarGraph(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Seed")int seed) {

			String s = test(numberOfNodes);	
			if (s != null) return s;
			
			CirclesPlanarGenerator gen = new CirclesPlanarGenerator(numberOfNodes, seed);
			gen.generate(agh);
			return null;
		}
		
		@MethodDef
		public String generateRandomPolarGraph(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Node arrangement (1= fixed, 0 = most noisy, default 0.2)")float radrange,
				@ParameterDef (name="Part of the circle to fill [0,1]")float q,
				@ParameterDef (name="Probability to connect neighbours of same circle [0,1] ")float a,
				@ParameterDef (name="Probability to connect neighbours of other circles [0,1]")float i) {

			return generateRandomPolarGraph(agh, numberOfNodes, radrange, q, a, i, PRNStream.getRandomStream().nextInt());
		}		

		@MethodDef
		public String generateRandomPolarGraph(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Node arrangement (1= fixed, 0 = most noisy, default 0.2)")float radrange,
				@ParameterDef (name="Part of the circle to fill [0,1]")float q,
				@ParameterDef (name="Probability to connect neighbours of same circle [0,1] ")float a,
				@ParameterDef (name="Probability to connect neighbours of other circles [0,1]")float i,
				@ParameterDef (name="The seed")int seed) {

			String s = test(numberOfNodes);	
			if (s != null) return s;

			CirclesPlanarGenerator gen = new CirclesPlanarGenerator(numberOfNodes, 200, radrange, q,a,i, PRNStream.getDefaultStream(seed));
			gen.generate(agh);
			return null;
		}
	}
}
