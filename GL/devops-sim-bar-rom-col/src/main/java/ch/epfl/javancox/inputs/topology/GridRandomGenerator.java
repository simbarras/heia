package ch.epfl.javancox.inputs.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.topology.linker.DistanceAwarePlanarLinker;
import ch.epfl.javancox.inputs.topology.linker.PlanarLinker;

public class GridRandomGenerator extends AbstractRandomPlanarGenerator {

	int linkToRem = 0;
	int linkToAdd = 0;
	int nbPerRow = 0;
	int distance = 50;
	float targetDegree;
	float addiFraction = 0.3f;
	double rem, add;
	private PRNStream stream;

	int mode = 0;

	private AbstractGraphHandler agh;

	@Override
	public Map<String, String> getRandomGeneratorParameters() {
		Map<String, String> map = getNewMap(4);
		map.put("random_grid_width",nbPerRow+"");
		map.put("grid_element_width",distance+"");
		if (mode == 0) {
			map.put("target_degree", targetDegree+"");
			map.put("additional_removed_links", addiFraction+"");
			return map;
		} else {
			map.put("proportion_link_removed", rem+"");
			map.put("proportion_link_added", add+"");
			return map;
		}
	}

	@Override
	public String getName() {
		return "randomGrid";
	}


	public GridRandomGenerator(@ParamName(name = "Number of nodes") int nbNodes, 
		                       @ParamName(name = "Traget degree") float targetDegree) {
		this(nbNodes, targetDegree, 0.3f);
	}
	
	public GridRandomGenerator(@ParamName(name = "Number of nodes") int nbNodes, 
		                       @ParamName(name = "Traget degree") float targetDegree, 
		                       @ParamName(name = "Seed")	int seed) {
		this(nbNodes, targetDegree, 0.3f, PRNStream.getDefaultStream(seed));
	}
	
	public GridRandomGenerator(@ParamName(name = "Number of nodes") int nbNodes, 
		                       @ParamName(name = "Traget degree") float targetDegree, 
		                       PRNStream stream) {
		this(nbNodes, targetDegree, 0.3f, stream);
	}	
	
	public GridRandomGenerator(@ParamName(name = "Number of nodes") int nbNodes, 
		                       @ParamName(name = "Traget degree") float targetDegree, 
		                       @ParamName(name = "Removal factor") float frac) {
		this(nbNodes, targetDegree, frac, PRNStream.getRandomStream());
	}
	
	public GridRandomGenerator(@ParamName(name = "Number of nodes") int nbNodes, 
		                       @ParamName(name = "Traget degree") float targetDegree, 
		                       @ParamName(name = "Removal factor") float frac, 
		                       @ParamName(name = "Seed") int seed) {	
		this(nbNodes, targetDegree, frac, PRNStream.getDefaultStream(seed));
	}	

	public GridRandomGenerator(@ParamName(name = "Number of nodes") int nbNodes, 
		                       @ParamName(name = "Traget degree") float targetDegree, 
		                       @ParamName(name = "Removal factor") float frac, 
		                       PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		this.addiFraction = frac;
		this.targetDegree = targetDegree;
		mode = 0;
		nbPerRow = (int)Math.round(Math.sqrt(nbNodes));

		int nbLinks = computeNbLink();

		float actualDegree = (float)(2*nbLinks)/(float)nbNodes;

		float ratio = actualDegree / targetDegree;

		int finalLinkNumber = Math.round(nbLinks/ratio);
		if (ratio > 1) {

			linkToRem = Math.round(nbLinks - finalLinkNumber + (addiFraction * nbLinks));
			linkToAdd = Math.round((addiFraction * nbLinks));
		} else {
			linkToAdd = Math.round(finalLinkNumber - nbLinks + (addiFraction * nbLinks));
			linkToRem = Math.round((addiFraction * nbLinks));
		}

		check();
		
	/*	GraphHandlerFactory ghf = Javanco.getDefaultGraphHandlerFactory();
		gui = new JavancoDefaultGUI(ghf, true);	*/

	}

	private GridRandomGenerator(@ParamName(name = "Number of nodes") int nbNodes, int width, double rem, double add, PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		mode = 1;
		nbPerRow = width;

		this.rem = (float)((int)(rem*1000))/1000;
		this.add = (float)((int)(add*1000))/1000;


		int nbTotalLink = computeNbLink();

		int maxToRem = nbTotalLink - (nbNodes - 1);

		linkToRem = (int)Math.round(maxToRem*rem);

		linkToAdd = (int)Math.round(maxToRem*add);

		check();
		
		
	}

	private void check() {
		int link = computeNbLink();

		int finalNbLink = link + linkToAdd - linkToRem;
		if (finalNbLink > link + (getHoriLinks() - nbPerRow)) {
			finalNbLink = link + (getHoriLinks() - nbPerRow);
		}
	}

	private int computeNbLink() {
		int nbFullRow = nbNodes/nbPerRow;

		int nbVertLink = (nbFullRow -1) * nbPerRow;
		nbVertLink += nbNodes % nbPerRow;

		return getHoriLinks() + nbVertLink;
	}

	private int getHoriLinks() {
		int nbFullRow = nbNodes/nbPerRow;
		int nbHoriLink = nbFullRow * (nbPerRow-1);
		if (nbNodes % nbPerRow > 1) {
			nbHoriLink += nbNodes % nbPerRow - 1;
		}
		return nbHoriLink;
	}

	@Override
	public void generateRandomTopology(AbstractGraphHandler agh) {
		stream = stream.clone(linkToAdd*512 + linkToRem + stream.nextInt());
	//	gui.graphCreated(agh);

		this.agh = agh;
		GridGenerator gridGen = new GridGenerator(distance, nbPerRow, nbNodes);
		gridGen.generate(agh);

		int removed = removeNLinks(linkToRem, stream);

		PlanarLinker pl = new DistanceAwarePlanarLinker(linkToAdd - (linkToRem - removed));
		pl.setLinkFilter(new PlanarLinker.LinkFilter() {
			public boolean filter(NodeContainer nc1, NodeContainer nc2) {
				java.awt.Point p1 = nc1.getCoordinate();
				java.awt.Point p2 = nc2.getCoordinate();
				int indexDiff = Math.abs(nc1.getIndex() - nc2.getIndex());
				if ((p1.x == p2.x || p1.y == p2.y) && (indexDiff != 1 && indexDiff != nbPerRow)) return false;
				return true;
			}
		});
		pl.linkNodes(agh, stream);
		int i = reconnectComponents(agh);
		int joker = 4;
		while (joker > 0 && i > 0) {
			removeNLinks(i, stream);
			i = reconnectComponents(agh);
			joker--;
		}

	}

	private int removeNLinks(int n, PRNStream stream) {
		int removed = 0;
		ArrayList<AbstractElementContainer> toRem = new ArrayList<AbstractElementContainer>(linkToRem);
		List<LinkContainer> links = agh.getLinkContainers();
		for (int i = 0 ; i < n ; i++) {
			int nb = stream.nextInt(links.size()-1);
			if (nb < 0) {
				throw new IllegalStateException();
			}
			if (!links.isEmpty()) {
				toRem.add(links.remove(nb));
				removed++;				
			} else {
				break;
			}
		}
		agh.removeElements(toRem);
		return removed;
	}

	public class Filter extends PlanarLinker.LinkFilter {
		@Override
		public boolean filter(NodeContainer nc1, NodeContainer nc2) {
			int i1 = nc1.getIndex();
			int i2 = nc2.getIndex();
			if (i1 > i2) {
				i2 = i1;
				i1 = nc2.getIndex();
			}
			if (hasRowConflict(i1, i2)) {
				return false;
			}
			if (hasColumnConflict(i1, i2)) {
				return false;
			}
			return true;
		}

		public boolean hasRowConflict(int nc1, int nc2) {

			int a = nc1 / nbPerRow;
			int b = nc2 / nbPerRow;
			if (a != b) {
				return false;
			}
			a = nc1 % nbPerRow;
			b = nc2 % nbPerRow;
			for (int i = a ; i < b-1 ; i++) {
				for (int j = a+1 ; j < b ; j++) {
					if (agh.getLinkContainers(i,j, false).size() > 0) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean hasColumnConflict(int nc1, int nc2) {
			int ra = nc1 % nbPerRow;
			int rb = nc2 % nbPerRow;
			if (ra != rb) {
				return false;
			}
			int a = nc1 / nbPerRow;
			int b = nc2 / nbPerRow;
			for (int i = a ; i < b ; i++) {
				for (int j = a + 1 ; j <= b ; j++) {
					int c1 = i*nbPerRow + ra;
					int c2 = j*nbPerRow + ra;
					if (agh.getLinkContainers(c1,c2, false).size() > 0) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public static class RandomGrid extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateRandomGrid(AbstractGraphHandler agh,
				@ParameterDef (name="Number of nodes")int numberOfNodes,
				@ParameterDef (name="Target mean degree")float meanDegree) {
					
			String s = test(numberOfNodes);	
			if (s != null) return s;
			GridRandomGenerator gen = new GridRandomGenerator(numberOfNodes, meanDegree, PRNStream.getRandomStream());
			gen.generate(agh);
			return null;
		}

		@MethodDef
		public String generateRandomGrid(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Target mean degree")float meanDegree,
				@ParameterDef (name="Seed")int seed) {

			String s = test(numberOfNodes);	
			if (s != null) return s;
			GridRandomGenerator gen = new GridRandomGenerator(numberOfNodes, meanDegree, seed);
			gen.generate(agh);
			return null;		
		}

		@MethodDef
		public String generateRandomGrid(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int numberOfNodes,
				@ParameterDef (name="Target mean degree")float meanDegree,
				@ParameterDef (name="Removal factor") float var,
				@ParameterDef (name="Seed")int seed) {

			String s = test(numberOfNodes);	
			if (s != null) return s;
			GridRandomGenerator gen = new GridRandomGenerator(numberOfNodes, meanDegree, var, seed);
			gen.generate(agh);
			return null;
		}
	}
	
	private GridRandomGenerator() {
		super(0, PRNStream.getDefaultStream(9));
	}


}
