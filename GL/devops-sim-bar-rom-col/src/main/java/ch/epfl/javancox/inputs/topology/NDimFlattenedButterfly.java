package ch.epfl.javancox.inputs.topology;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import ch.epfl.javancox.inputs.topology.layout_reorganiser.CircleLayout;

public class NDimFlattenedButterfly extends AbstractDeterministicGenerator {
	
	private int dimensions;
	private int[] radixes;
	private boolean skip =  false;
	private boolean colors = false;
	
	public NDimFlattenedButterfly(int[] radixes) {
		this.dimensions = radixes.length;
		this.radixes = radixes;
		check();
	}

	public NDimFlattenedButterfly(@ParamName(name="Dimensions") int dimensions, 
			@ParamName(name="Radix") int radix) {
		this.dimensions = dimensions;
		this.radixes = new int[dimensions];
		Arrays.fill(radixes, radix);
		check();	
	}
	
	public NDimFlattenedButterfly(int dimensions, int radix, int maxNodes) {
		this.dimensions = dimensions;
		this.radixes = new int[dimensions];
		Arrays.fill(radixes, radix);
		check(maxNodes);
		
	}
	
	private void check() {
		check(Integer.MAX_VALUE);
	}
	
	private void check(int maxNodes) {
		if (radixes.length > 1 && MoreArrays.hasThisValue(radixes, 1)) skip = true;
		if (MoreMaths.product(radixes) > maxNodes) skip = true;
	}
	
	public static int[] getWidthVector(int dimensions, int desiredNumberOfNodes) {
		return GeneralizedTorus.getWidthVector(dimensions, desiredNumberOfNodes);
	}
	
	public boolean willSkip() {
		return skip;
	}	

	@Override
	public void generate(AbstractGraphHandler agh) {
		ensureLayer(agh);
		if (skip) return;
	/*	if (dimensions == 1) {
			// lay nodes in a senseful way
			super.createPolygonNodes(agh, radixes[0], 200, 150, 150);
			for (int i = 0 ; i < agh.getHighestNodeIndex() ; i++) {
				for (int j = i+1 ; j <= agh.getHighestNodeIndex() ; j++ ) {
					agh.newLink(i, j);
				}
			}
		} else {*/
			generateFirstDimension(agh);
			for (int i = 0 ; i < dimensions-1 ; i++) {
				addDimension(agh, i+1);
			}
	//	}

	}

	private void addDimension(AbstractGraphHandler agh, int dim) {
		int currentNodes = agh.getNumberOfNodes();
		List<LinkContainer> existingLinks = agh.getLinkContainers();
		ColorMap cmap = ColorMap.getDefaultMap();
		for (int i = 0 ; i < radixes[dim]-1 ; i++) {
			for (int j = 0 ; j < currentNodes ; j++) {
				agh.newNode();
			}
			for (int j = 0 ; j < existingLinks.size() ; j++) {
				LinkContainer existing = existingLinks.get(j);
				LinkContainer newL = agh.newLink(existing.getStartNodeIndex() + ((i+1)*currentNodes), existing.getEndNodeIndex() + ((i+1)*currentNodes));
				if (colors) {
					newL.attribute("link_color").setValue(existing.attribute("link_color").getValue());
				}
			}
		}
		for (int k = 0 ; k < currentNodes ; k++) {
			for (int i = 0 ; i < radixes[dim]-1 ; i++) {
				for (int j = i+1 ; j < radixes[dim] ; j++ ) {
					LinkContainer lc = agh.newLink((i*currentNodes) + k , (j*currentNodes) + k);
					if (colors) 
						lc.attribute("link_color").setValue(cmap.getColorAsInt(dim));
				}
			}
		}
	}

	private void generateFirstDimension(AbstractGraphHandler agh) {
		if (dimensions == 1) {
			super.createPolygonNodes(agh, radixes[0], 200, 150, 150);
		} else {
			for (int i = 0 ; i < radixes[0] ; i++) {
				agh.newNode();
			}
		}
		for (int i = 0 ; i < radixes[0]-1 ; i++) {
			for (int j = i+1 ; j < radixes[0] ; j++ ) {
				agh.newLink(i, j);
			}
		}		
	}
	
	

	@Override
	public String getName() {
		return "nDimFlattenedButterfly";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		String rad;
		if (radixes.length == 1) {
			rad = radixes[0] +"";
		} else {
			rad = Arrays.toString(radixes);
		}
		return SimpleMap.getMap("dimensions", dimensions+"", "radix", "" + rad);
	}

	@Override
	public int getNumberOfNodes() {
		return (int)MoreMaths.product(radixes);
	}
	
	public static class NDimFlattenedButterfly_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateFlattenedButterfly(AbstractGraphHandler agh,
				@ParameterDef (name="Dimensions") int dim,
				@ParameterDef (name="Radix") int rad,
				@ParameterDef (name="Pcolor?") boolean pcol) throws InstantiationException{

			NDimFlattenedButterfly torus = new NDimFlattenedButterfly(dim, rad);
			torus.colors = true;
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
		public String generateFlattenedButterfly(AbstractGraphHandler agh,
				@ParameterDef (name="Comma separated radixes") String rad,
				@ParameterDef (name="Pcolor?") boolean pcol) throws InstantiationException{
			
			int[] d = TypeParser.parseIntArray(rad);
			

			NDimFlattenedButterfly torus = new NDimFlattenedButterfly(d);
			torus.colors = true;			
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
