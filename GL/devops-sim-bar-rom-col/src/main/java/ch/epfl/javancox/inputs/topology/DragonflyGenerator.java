package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class DragonflyGenerator extends AbstractDeterministicGenerator {
	
	private int nodesAtFirstLevel;
	private int levels;
//	private boolean graphics = false;
	
	private boolean isFlatBut = false;
	private int[] flatButDims;
	
	private int basicR = 100;
	private double additionalDist = 1.4;
	protected int extraIntergroup = 0;
	
	public DragonflyGenerator(@ParamName(name="nodes per level") int[] nodesPerLevelFlatbush, 
			@ParamName(name="Number of levels") int levels) {
		isFlatBut = true;
		flatButDims = nodesPerLevelFlatbush;
		this.nodesAtFirstLevel = (int)MoreMaths.product(nodesPerLevelFlatbush);
		this.levels = levels;
	}
	
	public DragonflyGenerator(@ParamName(name="nodes per level") int nodesPerLevel, 
			@ParamName(name="Number of levels") int levels) {
		this.nodesAtFirstLevel = nodesPerLevel;
		this.levels = levels;
	}
	
	public DragonflyGenerator(@ParamName(name="nodes per level") int nodesPerLevel, 
			@ParamName(name="Number of levels") int levels,
			@ParamName(name="Extra inter-group links") int extraIntergroup) {
		this.nodesAtFirstLevel = nodesPerLevel;
		this.levels = levels;
		this.extraIntergroup = extraIntergroup;
	}	
	
	public DragonflyGenerator(@ParamName(name="nodes per level") int nodesPerLevel, 
			@ParamName(name="Number of levels") int levels, 
			@ParamName(name="Extra inter-group links") int extraIntergroup,
			boolean graphics) {
		this.nodesAtFirstLevel = nodesPerLevel;
		this.levels = levels;
		this.extraIntergroup = extraIntergroup;
//		this.graphics = true;
	}	

	@Override
	public void generate(AbstractGraphHandler agh) {
		super.ensureLayer(agh);
		double finalR = basicR;
		for (int i = 1 ; i < levels ; i++) {
			finalR = Math.max(8, nodesAtFirstLevel+i)*finalR*additionalDist/Math.PI;
		}
		recursion(agh, 0, 0, 0, finalR, levels);
		wire(agh);
	}
	
	private int getNumberOfPenultianGroups(int level) {
		if (level == 0) return 0;
		if (level == 1) return nodesAtFirstLevel;
		int groups = 1;
		int nodePerGroup = nodesAtFirstLevel;
		for (int i = 0 ; i < level-1 ; i++) {
			nodePerGroup *= groups;
			groups = nodePerGroup+1;
		}
		return groups;
	}
	
	private int getNumberOfGroups(int level, int groupIndex) {
		if (groupIndex > level) throw new IllegalStateException();
		if (groupIndex == level) return 1;
		int product = 1;
		for (int i = level ; i > groupIndex ; i--) {
			product *= getNumberOfPenultianGroups(i);
		}
		return product;
	}

	private void recursion(AbstractGraphHandler agh, double anglePrev, double x, double y, double R, int level) {
		int items = getNumberOfPenultianGroups(level);
		double angle = 2*Math.PI/((double)items);
	//	anglePrev = anglePrev%(2*Math.PI/(nodesAtFirstLevel+level));
		for (int i = 0 ; i < items ; i++) {
			double addX = R*Math.sin(anglePrev + angle*(i+0.5));
			double addY = R*Math.cos(anglePrev + angle*(i+0.5));
			if (level == 1) {
				agh.newNode((int)(x + addX),(int)(y + addY));
			} else {
				recursion(agh, anglePrev + angle*(i+0.5), x + addX, y + addY, R*Math.PI/((double)Math.max(8, items)*additionalDist), level-1);
			}
		}
	}
	
	private void wire(AbstractGraphHandler agh) {
		int[][] matrix = new int[levels+1][levels+1];
		for (int i = 0 ; i <= levels ; i++) {
			for (int j = 0 ; j <= i ; j++) {
				matrix[i][j] = getNumberOfGroups(i, j);
			}
		}
		matrix[0][0] = 0;	

		for (int i = 1 ; i < levels+1 ; i++) {
			for (int j = 0 ; j < matrix[levels][i] ; j++) {
				for (int m = 0 ; m < matrix[i][i-1] - 1 ; m++) {
					for (int n = m+1 ; n < matrix[i][i-1] ; n++) {	
						if (i==1 && isFlatBut && notConnectedFlatBut(n, m)) continue;

					//	if ((n - m) % product[0] != 0 && n -m >= product[1]) continue;
						int start = j*matrix[i][0];
						int end = j*matrix[i][0];
						if (i == 1) {
							start += m*matrix[i-1][0];							
							start += m;
							end += n*matrix[i-1][0];							
							end += n;
						} else {
							start += m*matrix[i-1][0];
							start += n - 1 - m;
							end += n*matrix[i-1][0];
							end += (matrix[i-1][0] + m - n);
						}
						
						agh.newLink(start, end);
						if (i == 2) { // more inter-group added only at level 2 for now
							for (int h = 0 ; h < extraIntergroup; h++) {
								int increment = ((h+1)*matrix[i-1][0]);
								int dest = increment + end;
								if (dest >= matrix[i][0]) {
									dest = ((dest) % matrix[i][0]) + matrix[i-1][0];
								}
								LinkContainer l = agh.newLink(start, dest);
								l.attribute("link_color").setValue("255, 0, " + 50*h);
							}
						}
					}
				}
			}
		}	
		
		
/*		int nodes = agh.getNumberOfNodes();
		for (int i = 0 ; i < nodes ; i++) {
			int dest = i+1;
			while (dest % nodesAtFirstLevel != 0) {
				agh.newLink(i, dest);
				dest++;
			}
		}
		int nodesPerGroup = nodesAtFirstLevel;
		for (int j = 1 ; j < levels ; j++) {
			for (int m = 0 ; m < nodesAtFirstLevel + j -1 ; m++) {
				for (int n = m+1 ; n <nodesAtFirstLevel + j ; n++) {
					//connect group m to group n
					int start = nodesPerGroup*m + n - m - 1;
					int end = nodesPerGroup*n + (nodesAtFirstLevel + j - n + m - 1);
					agh.newLink(start, end);
				}
				
			}
			nodesPerGroup *= (nodesAtFirstLevel+j);
		}*/
	}

	private boolean notConnectedFlatBut(int n, int m) {
		int[] coordN = MoreMaths.coordinates(n, flatButDims);
		int[] coordM = MoreMaths.coordinates(m, flatButDims);
		
		for (int i = 0 ; i < coordN.length ; i++) {
			if (coordN[i] == coordM[i]) return false;
 		}
		return true;
	}

	@Override
	public String getName() {
		return "Dragonfly";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		Map<String, String> s = new SimpleMap<String, String>();
		s.put("nodes per level", nodesAtFirstLevel+"");
		s.put("dragonfly levels", levels+"");
		return s;
	}

	@Override
	public int getNumberOfNodes() {
		return getNumberOfGroups(levels, 0);
	}
	
	public static class Dragonfly_ extends WebTopologyGeneratorStub {

		/**
		 * Generate a dragonfly
		 * @param agh The AbstractGraphHandler
		 * @param layerName The layer
		 * @param torusLimit The torus dimension
		 * @param sideLength The length of the side
		 */
		@MethodDef
		public String generateDragonfly(AbstractGraphHandler agh,
				@ParameterDef (name="Nodes at first level") int n,
				@ParameterDef (name="Levels") int l) {

			DragonflyGenerator gen = new DragonflyGenerator(n, l, 0, true);
			gen.generate(agh);
			return null;
		}
		
		@MethodDef
		public String generateDragonfly(AbstractGraphHandler agh,
				@ParameterDef (name="nD dims") String n,
				@ParameterDef (name="Levels") int l) {

			DragonflyGenerator gen = new DragonflyGenerator(TypeParser.parseIntArray(n), l);
			gen.extraIntergroup = 0;
			gen.generate(agh);
			return null;
		}		
		

		@MethodDef
		public String generateDragonfly(AbstractGraphHandler agh,
				@ParameterDef (name="Nodes at first level") int n,
				@ParameterDef (name="Levels") int l,
				@ParameterDef (name="Extra links at level 2") int extra) {

			DragonflyGenerator gen = new DragonflyGenerator(n, l, extra, true);
			gen.generate(agh);
			return null;
		}		

	}	
		

}
