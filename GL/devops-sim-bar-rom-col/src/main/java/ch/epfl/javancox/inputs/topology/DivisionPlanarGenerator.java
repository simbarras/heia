package ch.epfl.javancox.inputs.topology;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.graphics.Util2DFunctions;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class DivisionPlanarGenerator extends AbstractRandomPlanarGenerator {

	static class Edge {
		Edge(NodeContainer nc, Edge nextEdge, AbstractGraphHandler agh) {
			if (nc == null) {
				throw new NullPointerException("NodeContainer null");
			}
			startNode = nc;
			this.nextEdge = nextEdge;
		}

		Face a;
		float length;
		Edge nextEdge;
		Edge prevEdge;
		NodeContainer startNode;
		Edge inverse;
		double angle;

		private void setAngle() {
			angle = Util2DFunctions.angle(startNode, nextEdge.startNode, prevEdge.startNode);
		}

		@Override
		public String toString() {
			return startNode.getIndex()+"->"+nextEdge.startNode.getIndex();
		}

		public String toAllString() {
			return startNode.getIndex()+"-"+toStringRec(startNode.getIndex());
		}

		public String toExString() {
			return startNode.getIndex()+"->"+nextEdge.startNode.getIndex() + "  " + length;
		}

		private String toStringRec(int start) {
			if (nextEdge.startNode.getIndex() != start) {
				return nextEdge.startNode.getIndex()+ "-" + nextEdge.toStringRec(start);
			} else {
				return nextEdge.startNode.getIndex()+"";
			}
		}

		public Edge addOneNode(double prop) {
			Point p1 = startNode.getCoordinate();
			Point p2 = nextEdge.startNode.getCoordinate();

			double x = p1.getX();
			double  y = p1.getY();
			double  a = p2.getX();
			double  b = p2.getY();
			double X = x+((a-x)*prop);
			double Y = y+((b-y)*prop);
			return addOneNode((int)X,(int)Y);
		}
		public Edge addOneNode(int X, int Y) {
			NodeContainer nextNode = nextEdge.startNode;
			AbstractGraphHandler agh = this.a.agh;
			NodeContainer nc = agh.newNode(X,Y);

			LinkContainer toRemove = startNode.getLinkWith(nextNode.getIndex());
			agh.removeElement(toRemove);
			float dist2 = (float)Util2DFunctions.length(agh.newLink(nc, startNode));
			float dist1 = (float)Util2DFunctions.length(agh.newLink(nextNode, nc));
			Edge nEdge1 = new Edge(nc, nextEdge, agh);
			Edge next = nextEdge;
			nEdge1.prevEdge = this;
			nEdge1.length = dist1;
			nEdge1.a = this.a;
			nEdge1.a.firstEdge = nEdge1;
			nEdge1.a.sides++;
			nextEdge = nEdge1;
			next.prevEdge = nEdge1;
			length = dist2;
			if (inverse != null) {
				Edge nEdge2 = new Edge(nc, inverse.nextEdge, agh);
				next = inverse.nextEdge;
				nEdge2.prevEdge = inverse;
				nEdge2.length = dist2;
				nEdge2.a = inverse.a;
				nEdge2.a.firstEdge = nEdge2;
				nEdge2.a.sides++;
				inverse.nextEdge = nEdge2;
				next.prevEdge = nEdge2;
				nEdge1.inverse = inverse;
				nEdge2.inverse = nEdge1;
				inverse.length = dist1;
				inverse = nEdge2;
				inverse.inverse = this;
				nextEdge.inverse.inverse = nextEdge;
				nEdge2.angle = Math.PI;
			}
			nEdge1.angle = Math.PI;
			return nEdge1;
		}
	}

	static class Face {
		PRNStream stream;

		Face(Edge nEdge2, int level, AbstractGraphHandler agh, PRNStream stream) {
			super();
			this.stream = stream;
			firstEdge = nEdge2;
			firstEdge.a = this;
			this.level = level + 1;
			this.agh = agh;
			Edge cEdge = firstEdge;
			while (cEdge.equals(firstEdge) == false || sides==0) {
				cEdge.a = firstEdge.a;
				sides++;
				cEdge = cEdge.nextEdge;
			}
		}

		Face(AbstractGraphHandler agh, PRNStream stream) {
			super();
			this.stream = stream;
			this.agh = agh;
			level=0;
			// check for degree 2
			for (NodeContainer nc : agh.getNodeContainers()) {
				if (nc.getConnectedLinks().size() != 2) {
					throw new IllegalStateException("Must start with a cyclic graph");
				}
			}
			// check for convexity
			NodeContainer a = agh.getNodeContainers().iterator().next();
			int firstIndex = a.getIndex();
			NodeContainer b = a.getConnectedNodes().iterator().next();
			NodeContainer c = getNext(a, b);
			double angle = Util2DFunctions.angle(a,b,c);
			boolean clockwise = !(angle < 0);
			firstEdge = null;
			Edge prevEdge = null;
			while ((a.getIndex() != firstIndex) || (firstEdge == null)) {
				Edge edge = new Edge(a, null, agh);
				edge.angle = Util2DFunctions.angle(b,a,c);
				edge.a = this;
				edge.length = (float)Util2DFunctions.distance(a,b);
				if (prevEdge != null)  {
					prevEdge.nextEdge = edge;
					edge.prevEdge = prevEdge;
				}
				if (firstEdge == null) {
					firstEdge = edge;
				}
				a = b;
				b = c;
				c = getNext(a,b);
				if (!(Util2DFunctions.angle(a,b,c) < 0) != clockwise) {
					throw new IllegalStateException("Primary shape must be convex");
				}
				prevEdge = edge;
			}
			prevEdge.nextEdge = firstEdge;
			firstEdge.prevEdge = prevEdge;
			this.sides = agh.getNodeContainers().size();
		}

		public ArrayList<Edge> getEdges() {
			ArrayList<Edge> edges = new ArrayList<Edge>(sides);
			boolean start = true;
			Edge cEdge = firstEdge;
			while (cEdge.equals(firstEdge) == false || start) {
				start = false;
				edges.add(cEdge);
				cEdge = cEdge.nextEdge;
			}
			return edges;
		}

		@Override
		public String toString() {
			if (firstEdge.inverse != null) {
				return firstEdge.toAllString() + "  : " + surface() + "/ " + sides + "  |" + Integer.toHexString(this.hashCode()) +
				"         " + firstEdge.inverse.toAllString();
			} else {
				return firstEdge.toAllString() + "  : " + surface() + "/ " + sides + "  |" + Integer.toHexString(this.hashCode()) +
				"        no inverse";

			}
		}

		NodeContainer getNext(NodeContainer a, NodeContainer b) {
			Iterator<NodeContainer> bIte = b.getConnectedNodes().iterator();
			NodeContainer c = bIte.next();
			while (c.getIndex() == a.getIndex()) {
				c = bIte.next();
			}
			return c;
		}

		Point2D center() {
			double x = 0;
			double y = 0;
			int total = 0;
			Edge currentEdge = firstEdge;
			while (currentEdge.equals(firstEdge) == false || total == 0) {
				x += currentEdge.startNode.getCoordinate().getX();
				y += currentEdge.startNode.getCoordinate().getY();
				total++;
				currentEdge = currentEdge.nextEdge;
			}
			return new Point2D.Double(x/total,y/total);
		}

		double edges() {
			int total = 0;
			Edge currentEdge = firstEdge;
			while (currentEdge.equals(firstEdge) == false || total == 0) {
				total++;
				currentEdge = currentEdge.nextEdge;
			}
			return total;
		}

		double surface() {
			double total=0;
			boolean first = true;
			Point2D center = center();
			Edge currentEdge = firstEdge;
			while (currentEdge.equals(firstEdge) == false || first) {
				first = false;
				double angle = Util2DFunctions.angle(center, currentEdge.startNode, currentEdge.nextEdge.startNode);
				double surf = Util2DFunctions.distance(center, currentEdge.nextEdge.startNode);
				surf *= Math.sin(angle)*Util2DFunctions.distance(center, currentEdge.startNode)/2;
				total += Math.abs(surf);
				currentEdge = currentEdge.nextEdge;
			}

			return total;
		}

		Edge pickOneEdge() {
			if (sides < edges()) {
				throw new IllegalStateException("Faces with less than 4 sides cannot be split");
			}
			Edge currentEdge= firstEdge;
			double angleMax= 0;
			Edge maxAngleEdge = null;
			while (!currentEdge.equals(firstEdge) || maxAngleEdge == null) {
				if (Math.abs(currentEdge.angle) >= angleMax) {
					maxAngleEdge = currentEdge;
					angleMax = Math.abs(currentEdge.angle);
				}
				currentEdge = currentEdge.nextEdge;
			}
			return maxAngleEdge;
		}

		Object[] splitAdding() {
			Edge e = pickOneEdge();
			Edge e2 = e;
			int rep = (int)Math.floor(sides/2.0);
			for (int i = 0 ; i < rep ; i++) {
				e2 = e2.nextEdge;
			}
			Edge otherEdge = e2.addOneNode((1.0/3.0)+(stream.nextDouble()/3));
			return split(e, otherEdge);
		}

		Object[] split() {
			double minActAngle = Double.MAX_VALUE;
			Edge cursorEdge = firstEdge;
			Edge minEdge = firstEdge;
			while (!cursorEdge.equals(firstEdge) || minActAngle == Double.MAX_VALUE) {
				double an = cursorEdge.angle;
				if (Math.abs(an) < minActAngle) {
					minActAngle = Math.abs(an);
					minEdge = cursorEdge;
				}
				cursorEdge = cursorEdge.nextEdge;
			}
			if (minActAngle < 0.4) {
				return splitAlter(minEdge);
			} else {
				return splitRegular();
			}
		}

		Object[] splitAlter(Edge minEdge) {
			double angle = Util2DFunctions.angle(minEdge.nextEdge.startNode, minEdge.startNode, minEdge.nextEdge.nextEdge.startNode);
			if (Math.abs(angle) > Math.PI -0.01) {
				Edge n1 = minEdge.prevEdge.addOneNode(0.4+(0.2*stream.nextDouble()));
				Object[] tab = split(n1, minEdge.nextEdge);
				tab[2]= n1;
				return tab;
			}
			angle = Util2DFunctions.angle(minEdge.prevEdge.startNode, minEdge.startNode, minEdge.prevEdge.prevEdge.startNode);
			if (Math.abs(angle) > Math.PI -0.01) {
				Edge n1 = minEdge.nextEdge.addOneNode(0.4+(0.2*stream.nextDouble()));
				Object[] tab = split(n1, minEdge.prevEdge);
				tab[2]= n1;
				return tab;
			}
			Edge n1 = minEdge.prevEdge.addOneNode(0.4+(0.2*stream.nextDouble()));
			Edge n2 = minEdge.addOneNode(0.4+(0.2*stream.nextDouble()));
			Object[] tab = split(n1, n2);
			tab[2] = n1;
			tab[3] = n2;
			return tab;
		}

		Object[] splitRegular() {
			Edge currentEdge = pickOneEdge();
			Edge limitEdge = currentEdge.prevEdge;
			Edge otherEdge = currentEdge.nextEdge.nextEdge;
			LinkedList<Double> dList = new LinkedList<Double>();
			LinkedList<Edge> candidates = new LinkedList<Edge>();
			double sum = 0;
			while (!otherEdge.equals(limitEdge)) {
				double angle = Util2DFunctions.angle( currentEdge.startNode, currentEdge.nextEdge.startNode,otherEdge.startNode);
				double angle2 = Util2DFunctions.angle( currentEdge.startNode, currentEdge.prevEdge.startNode,otherEdge.startNode);
				if (((angle > 0.1 && angle < 3.04) || (angle < -0.1 && angle > -3.04)) &&
						((angle2 > 0.1 && angle2 < 3.04) || (angle2 < -0.1 && angle2 > -3.04))){
					double min = Math.min(Math.abs(angle), Math.abs(angle2));
					dList.add(min);
					sum += min;
					candidates.add(otherEdge);
				}
				otherEdge =otherEdge.nextEdge;
			}

			if (candidates.size() == 0) {
				return null;
			}

			double coeff = stream.nextDouble() * sum;
			sum = 0;
			Iterator<Edge> iteEdge = candidates.iterator();
			for (Double d : dList) {
				sum += d;
				Edge e = iteEdge.next();
				if (coeff < sum) {
					otherEdge = e;
					break;
				}
			}
			return split(currentEdge, otherEdge);
		}

		Object[] split(Edge currentEdge, Edge otherEdge) {
			try {
				agh.newLink(currentEdge.startNode.getIndex(), otherEdge.startNode.getIndex());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			Edge nEdge1= new Edge(currentEdge.startNode, otherEdge, agh);
			Edge tempEdge = otherEdge.prevEdge;
			otherEdge.prevEdge = nEdge1;
			nEdge1.prevEdge = currentEdge.prevEdge;
			nEdge1.prevEdge.nextEdge = nEdge1;
			nEdge1.length = (float)Util2DFunctions.distance(currentEdge.startNode, otherEdge.startNode);
			nEdge1.a = currentEdge.a;

			Edge nEdge2= new Edge(otherEdge.startNode, currentEdge, agh);
			currentEdge.prevEdge = nEdge2;
			nEdge2.prevEdge = tempEdge;
			nEdge2.prevEdge.nextEdge = nEdge2;
			nEdge2.length = (float)Util2DFunctions.distance(currentEdge.startNode, otherEdge.startNode);

			nEdge1.inverse = nEdge2;
			nEdge2.inverse = nEdge1;

			Face newFace = new Face(nEdge2, level, agh, stream);
			this.firstEdge = nEdge1;
			this.sides = sides+2-newFace.sides;
			nEdge1.a = this;
			nEdge1.setAngle();
			nEdge2.setAngle();
			currentEdge.setAngle();
			otherEdge.setAngle();
			return new Object[]{newFace, nEdge1,null, null};
		}

		Edge firstEdge;
		int sides;
		int level;
		AbstractGraphHandler agh;
	}

	private int nbNodesDep;
	private float targetDegree;
	private int ray;
	private float powerCoeff;
	private PRNStream stream;

	@Override
	public Map<String, String> getRandomGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("target_degree",targetDegree+"");
		map.put("divided_figure_radius", ray+"");
		map.put("divided_figure_size", nbNodesDep+"");
		map.put("centrality_splitting", powerCoeff+"");
		return map;
	}
	
	public static class RandomDivisions extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateDivisionBased(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes) {
			
			String s = test(numberOfNodes);	
			if (s != null) return s;
					
			DivisionPlanarGenerator gen = new DivisionPlanarGenerator(numberOfNodes);
			gen.generate(agh);
			return null;
		}

		@MethodDef
		public String generateDivisionBased(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Seed")int seed) {

			String s = test(numberOfNodes);	
			if (s != null) return s;
			
			DivisionPlanarGenerator gen = new DivisionPlanarGenerator(numberOfNodes, seed);
			gen.generate(agh);
			return null;
		}

		@MethodDef
		public String generateDivisionBased(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Base figure sides")int sides,
				@ParameterDef (name="Target degree")float q,
				@ParameterDef (name="Seed")int seed) {

			String s = test(numberOfNodes);	
			if (s != null) return s;

			DivisionPlanarGenerator gen = new DivisionPlanarGenerator(numberOfNodes, sides, q, 1000, 1.2f, seed);
			gen.generate(agh);
			return null;
		}
	}	
	
	

	public DivisionPlanarGenerator(int nbNodes) {
		this(nbNodes, 4, 3.5f, (int)(60*Math.sqrt(nbNodes)), 1.2f, PRNStream.getRandomStream());
	}
	
	public DivisionPlanarGenerator(int nbNodes, int seed) {
		this(nbNodes, 4, 3.5f, (int)(60*Math.sqrt(nbNodes)), 1.2f, seed);
	}	

	public DivisionPlanarGenerator(int nbNodes, int nbNodesDep, float targetDegree, int ray, float powerCoeff, int seed) {
		this(nbNodes, nbNodesDep, targetDegree, ray, powerCoeff, PRNStream.getDefaultStream(seed));
	}

	public DivisionPlanarGenerator(int nbNodes, int nbNodesDep, float targetDegree, int ray, float powerCoeff, PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		configure(nbNodesDep, targetDegree, ray, powerCoeff);
	}

	private void configure(int nbNodesDep, float targetDegree, int ray, float powerCoeff) {
		this.nbNodesDep = nbNodesDep;
		this.targetDegree = targetDegree;
		this.ray = ray;
		this.powerCoeff = powerCoeff;
	}

	@Override
	public void generateRandomTopology(AbstractGraphHandler agh) {
		if (targetDegree < 2.4 || targetDegree > 5.5) {
			throw new IllegalStateException("Given degree not supported");
		}
		ensureLayer(agh);
		agh.activateMainDataHandler();
		agh.activateGraphicalDataHandler();
		PolygonGenerator polygen = new PolygonGenerator(nbNodesDep, true, ray, (ray/2+20),(ray/2+20));

		polygen.generate(agh);

		ArrayList<Face> faces = new ArrayList<Face>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		int edgeNb = 0;
		int nodeNb = agh.getNodeContainers().size();
		edgeNb += agh.getLinkContainers().size();
		Face face = new Face(agh, stream);
		edges.addAll(face.getEdges());
		faces.add(face);

		while (nodeNb < nbNodes) {
			float actDegree = (float)(2*edgeNb)/(float)nodeNb;
			//	System.out.println("Degree " + actDegree);
			if (actDegree < targetDegree) {
				Edge[] e = pickFaceAndSplit(faces, stream);
				if (e == null) {
					e = pickFaceAndSplitWithNewNode(faces, stream);
					nodeNb++;
					edgeNb++;
				}
				edges.add(e[0]);
				if (e[1] != null) { edges.add(e[1]); edgeNb++; nodeNb++; }
				if (e[2] != null) { edges.add(e[2]); edgeNb++; nodeNb++; }
				edgeNb++;
			} else {
				Edge e = pickOneEdge(edges, faces, stream).addOneNode(computeProportion(stream));
				edges.add(e);
				edgeNb++;
				nodeNb++;
			}
		}
		/*
			System.out.println("Nodes : " + nodeNb + "  \tLinks : " + edgeNb + "  \tav.deg : " + (float)(2*edgeNb)/(float)nodeNb);
			System.out.println("links "  + agh.getLinkContainers().size());

			try {
				java.awt.image.BufferedImage buf = minUi.getGraphImage(false);
				javax.imageio.ImageIO.write(buf, "gif", new java.io.File(System.currentTimeMillis() + ".gif"));
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
		for (Edge e : edges) {
			System.out.println(e.toExString());
		}*/
	}

	public double computeProportion(PRNStream stream) {
		double prop = -1;
		while (prop < 0.2 || prop > 0.8) {
			prop = 0.45 + stream.nextGaussian()/5;
		}
		return prop;
	}

	public Edge pickOneEdge(ArrayList<Edge> edges, ArrayList<Face> faces, PRNStream stream) {
		Face f = pickFace(faces, false, 1, stream);
		double d = 0;
		Edge cursorEdge = f.firstEdge;
		while (!cursorEdge.equals(f.firstEdge) || d == 0) {
			d+=Math.pow(cursorEdge.length, powerCoeff);
			cursorEdge = cursorEdge.nextEdge;
		}
		double coeff = d*stream.nextDouble();
		d = 0;
		cursorEdge = f.firstEdge;
		while (!cursorEdge.equals(f.firstEdge) || d == 0) {
			d+=Math.pow(cursorEdge.length, powerCoeff);
			if (coeff < d) {
				return cursorEdge;
			}
			cursorEdge = cursorEdge.nextEdge;
		}
		throw new IllegalStateException("Should not be there");
	}

	public Edge[] pickFaceAndSplit(ArrayList<Face> faces, PRNStream stream) {
		Object[] tab =null;
		while (tab == null) {
			Face f = pickFace(faces, true, 4, stream);
			if (f == null)  {
				// debug
				pickFace(faces, true, 4, stream);
				return null;
			}
			tab = f.split();
		}
		faces.add((Face)tab[0]);
		Edge[] edgeTab = new Edge[]{(Edge)tab[1], (Edge)tab[2], (Edge)tab[3]};
		return edgeTab;
	}

	public Edge[] pickFaceAndSplitWithNewNode(ArrayList<Face> faces, PRNStream stream) {
		Face f = pickFace(faces, false, 3, stream);
		Object[] tab = f.splitAdding();
		faces.add((Face)tab[0]);
		Edge[] edgeTab = new Edge[]{(Edge)tab[1], (Edge)tab[2], (Edge)tab[3]};
		return edgeTab;
	}

	private Face pickFace(ArrayList<Face> faces, boolean check3Sides, int pow, PRNStream stream) {
		double d = 0;
		ArrayList<Face> candidates = new ArrayList<Face>(faces.size());
		for (Face f : faces) {
			if (!check3Sides || f.edges() > 3) {
				d+=Math.pow(f.surface(),pow);
				candidates.add(f);
			}
		}
		double coeff = d*stream.nextDouble();
		d = 0;
		for (Face f : candidates) {
			d += Math.pow(f.surface(),pow);
			if (coeff < d) {
				return f;
			}
		}
		return null;
	}



	@Override
	public String getName() {
		return "Division_based";
	}

	public static void main(String args[]) throws Exception {
		int[] startNode = new int[]{4,6,8,12};
		float[] degrees = new float[]{2.4f, 2.8f, 3.2f, 3.8f, 4.2f};
		int[] nodes = new int[]{15,25,50,75};
		int[] seeds = new int[]{0, 364, 24,568};

		AbstractGraphHandler agh = null;
		for (int start : startNode) {
			for (float deg : degrees) {
				for (int n : nodes) {
					for (int seed : seeds) {
						DivisionPlanarGenerator gen = new DivisionPlanarGenerator(n, start, deg, 200, 1.2f, seed);
						agh = Javanco.getDefaultGraphHandler(false);
						gen.generate(agh);
						System.out.print(":");
					}
				}
			}
		}
		agh.saveNetwork("divisionLast.xml");
	}


}
