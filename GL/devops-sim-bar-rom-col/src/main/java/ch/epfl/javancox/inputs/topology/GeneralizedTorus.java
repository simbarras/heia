package ch.epfl.javancox.inputs.topology;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.topology.layout_reorganiser.CircleLayout;

public class GeneralizedTorus extends AbstractDeterministicGenerator {
	
	protected int dimensions;
	private int[] width;
	private boolean skip = false;
	private String widthString;
	private boolean colors = false;

	@ConstructorDef(def = "Build a torus")
	public GeneralizedTorus(@ParamName(name="Dimensions") int dimensions, 
			@ParamName(name="Widths") int w) {
		this.dimensions = dimensions;
		this.width = new int[dimensions];
		Arrays.fill(this.width, w);
		widthString = width+"";
	}
	
	@ConstructorDef(def = "Build a torus with maxnode")
	public GeneralizedTorus(int dimensions, int w, int maxNode) {
		if (Math.pow(w, dimensions) > maxNode) skip = true;
		this.dimensions = dimensions;
		this.width = new int[dimensions];
		Arrays.fill(this.width, w);
		widthString = width+"";		
	}
	
	@ConstructorDef(def = "Build a torus with maxnode, specifiying the sizes of each dimension")
	public GeneralizedTorus(@ParamName(name="Sizes of dimensions") int[] width, 
			@ParamName(name="Node limit") int maxNode) {
		if (width.length > 1 && MoreArrays.hasThisValue(width, 1)) skip = true;		
		if (MoreMaths.product(width) > (long)maxNode) skip = true;
		this.dimensions = width.length;
		this.width = width;
		widthString = Arrays.toString(width);
	}
	
	@ConstructorDef(def = "Build the ith least irregular version of a n dimensionnal torus, if it creates less than maxnode")
	public GeneralizedTorus(int topologyIndex, short dimensions, int maxNode) {	
		int minWidth = (topologyIndex/dimensions) + 1;
		width = new int[dimensions];
		this.dimensions = dimensions;
		Arrays.fill(width, minWidth);
		for (int j = 0 ; j < topologyIndex % dimensions ; j++) {
			width[j]++;
		}
		long totalNode = MoreMaths.product(width);
		System.out.println(totalNode);
		if (totalNode > (long)maxNode) skip = true;		
		widthString = Arrays.toString(width);
	}
	
	public boolean willSkip() {
		return skip;
	}
	
	public static int[] getWidthVector(int dimensions, int desiredNumberOfNodes) {
		int max = (int)Math.ceil(Math.pow(desiredNumberOfNodes, 1/(double)dimensions));
		if (max == 1) {
		}
		int[] width = new int[dimensions];
		Arrays.fill(width, max);
		for (int i = 0 ; i < width.length ; i++) {
			width[i]--;
			if (MoreMaths.product(width) < desiredNumberOfNodes) {
				width[i]++;
				return width;
			}
		}
		return width;
	}	

	@Override
	public void generate(AbstractGraphHandler agh) {
		ensureLayer(agh);
		if (skip) return;
		generateFirstDimension(agh);
		for (int i = 0 ; i < dimensions - 1 ; i++) {
			generateNextDimension(agh, i+1);
		}
	}
	
	private void generateNextDimension(AbstractGraphHandler agh, int dimensionIndex) {		
		Collection<NodeContainer> existing = agh.getNodeContainers();
		List<LinkContainer> existingLinks = agh.getLinkContainers();
		ColorMap cmap = ColorMap.getDefaultMap();
		String ss = cmap.getColorAsString(dimensionIndex);
		int existingNb = existing.size();
		for (int j = 0 ; j < this.width[dimensionIndex]-1 ; j++) {
			for (int i = 0 ; i < existingNb ; i++) {
				NodeContainer nc = agh.newNode();
				int index = nc.getIndex();
				LinkContainer lc = agh.newLink(index - existingNb, index);
				if (colors)
					lc.attribute("link_color").setValue(ss);
				if (j == width[dimensionIndex] -2 && width[dimensionIndex] > 2) {
					lc = agh.newLink(index, i);
					if (colors)
						lc.attribute("link_color").setValue(ss);
				}
			}
			int offset = existingNb * (j+1);
			for (int i = 0 ; i < existingLinks.size(); i++) {
				LinkContainer lc = existingLinks.get(i);
				LinkContainer lcnew = agh.newLink(lc.getStartNodeIndex() + offset, lc.getEndNodeIndex() + offset);
				if (colors)
					lcnew.attribute("link_color").setValue(lc.attribute("link_color").getValue());
			}
		}
		
	}

	private void generateFirstDimension(AbstractGraphHandler agh) {
		NodeContainer prev = agh.newNode();
		NodeContainer init = prev;
		for (int i = 1 ; i < width[0] ; i++) {
			NodeContainer next = agh.newNode();
			agh.newLink(prev, next);
			prev = next;
		}
		if (width[0] > 2)
			agh.newLink(prev, init);
	}

	@Override
	public String getName() {
		return "generalized torus";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("dimensions", dimensions + "", "torus width", widthString);
	}

	@Override
	public int getNumberOfNodes() {
		return (int)MoreMaths.product(width);
	}
	
	public static class GeneralizedTorus_ extends WebTopologyGeneratorStub {

		/**
		 * Generate a torus
		 * @param agh The AbstractGraphHandler
		 * @param layerName The layer
		 * @param torusLimit The torus dimension
		 * @param sideLength The length of the side
		 */
		@MethodDef
		public String generateTorus(AbstractGraphHandler agh,
				@ParameterDef (name="Torus  size (number of nodes)") int torusSize,
				@ParameterDef (name="Dimensions") int dim,
				@ParameterDef (name="Link color per dimension") boolean linkCol, 
				@ParameterDef (name="Show p color") boolean pcol) throws InstantiationException{

			GeneralizedTorus torus = new GeneralizedTorus(dim, torusSize);
			torus.colors = linkCol;
			torus.generate(agh);
			
			CircleLayout l = new CircleLayout();
			
			l.assignNodesPosition(agh);
			
			if (pcol) {
				int clients = agh.getNumberOfNodes();
				double[][] dist = new double[clients][clients];
				for (int i = 0 ; i < clients ; i++) {
					Path[] p = BFS.getShortestPathsUndirectedFrom(agh, i);
					for (int j = 0 ; j < clients ; j++) {
						if (j != i && p[j] != null) {
							dist[i][j] = p[j].getNumberOfHops();
						}
					}
				}
				dist = Matrix.normalize(dist);
				PcolorGUI gui = new PcolorGUI(dist);
				gui.showInFrame();				
			}
			return null;
		}
		
		@MethodDef
		public String generateTorus2(AbstractGraphHandler agh,
				@ParameterDef (name="Torus  size (number of nodes)") String dimensions,
				@ParameterDef (name="Link color per dimension") boolean linkCol, 
				@ParameterDef (name="Show p color") boolean pcol) throws InstantiationException{

			int[] dim = TypeParser.parseIntArray(dimensions);
			
			GeneralizedTorus torus = new GeneralizedTorus(dim, 10000);
			torus.colors = linkCol;
			torus.generate(agh);
			
			CircleLayout l = new CircleLayout();
			
			l.assignNodesPosition(agh);
			
			if (pcol) {
				int clients = agh.getNumberOfNodes();
				double[][] dist = new double[clients][clients];
				for (int i = 0 ; i < clients ; i++) {
					Path[] p = BFS.getShortestPathsUndirectedFrom(agh, i);
					for (int j = 0 ; j < clients ; j++) {
						if (j != i && p[j] != null) {
							dist[i][j] = p[j].getNumberOfHops();
						}
					}
				}
				dist = Matrix.normalize(dist);
				PcolorGUI gui = new PcolorGUI(dist);
				gui.showInFrame();				
			}
			return null;
		}		

	}	
	

}
