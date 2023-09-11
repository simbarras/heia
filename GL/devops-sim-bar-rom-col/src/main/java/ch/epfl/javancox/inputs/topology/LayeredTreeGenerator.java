package ch.epfl.javancox.inputs.topology;

import java.util.Map;
import java.util.Arrays;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javancox.inputs.topology.layout_reorganiser.UnequalTreeLayout;

public class LayeredTreeGenerator extends AbstractDeterministicGenerator
		implements TreeGenerator {
	
	private int layers;
	private int[] siblings;
	
	LayeredTreeGenerator(int layers, int siblingsPerLayer) {
		this.layers = layers;
		this.siblings = new int[layers];
		Arrays.fill(siblings, siblingsPerLayer);
	}
	
	public LayeredTreeGenerator(int siblingsAtTheUniqueLayer) {
		this(siblingsAtTheUniqueLayer+"");
	}
	
	public LayeredTreeGenerator(String siblingsPerLayer) {
		this.siblings = TypeParser.parseInt(siblingsPerLayer);
		this.layers = siblings.length;
	}

	@Override
	public void generateTree(AbstractGraphHandler agh) {
		generate(agh);
	}
	
	@Override
	public void generateTree(AbstractGraphHandler agh, PRNStream stream) {
		generate(agh);
	}	

	@Override
	public void generate(AbstractGraphHandler agh) {
		NodeContainer current = agh.newNode(0, 0);
		addLayer(agh, current, 0);
		lay.assignNodesPosition(agh);
	}
	
	private void addLayer(AbstractGraphHandler agh, NodeContainer nc, int layer) {
		for (int i = 0 ; i < siblings[layer] ; i++) {
			NodeContainer na = agh.newNode();
			agh.newLink(nc, na);
			if (layer < layers-1) {
				addLayer(agh, na, layer+1);
			}
		}
	}

	@Override
	public String getName() {
		return "layered tree generator";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("Layers in tree", layers +"", "siblings per layer", Arrays.toString(siblings).replaceAll("\\[", "").replaceAll("\\]", ""));
	}

	@Override
	public int getNumberOfNodes() {
		int result = 1;
		int accum = 0;
		for (int i = 0 ; i < layers ; i++) {
			result *= siblings[i];
			accum += result;
		}
		return accum+1; // the root
	}
	
	@Override
	public double getAverageNumberOfLeaves() {
		int val = 1;
		for (int i = 0 ; i < siblings.length ; i++) {
			val *= siblings[i];
		}
		return val;
	}

	@Override
	public double getAverageHubNumber() {
		int d = 1;
		for (int i = 0 ; i < siblings.length-1 ; i++) {
			if (siblings[i] > 1) d += siblings[i];
		}
		return d;
	}	
	
	private static UnequalTreeLayout lay = new UnequalTreeLayout();	
	
	public static class LayeredTreeGenerator_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String generate(AbstractGraphHandler agh,
				@ParameterDef (name="Number of layers")int nbNodes,
				@ParameterDef (name="Vertices by layers (int)")int s) throws InstantiationException{
			
			LayeredTreeGenerator gen = new LayeredTreeGenerator(nbNodes, s);
			gen.generate(agh);
			
			return null;
		}
		
		@MethodDef
		public String generateWithVarious(AbstractGraphHandler agh,
				@ParameterDef (name="Vertices by layers array (String)")String s) throws InstantiationException{
			
			LayeredTreeGenerator gen = new LayeredTreeGenerator(s);
			gen.generate(agh);
			
			return null;
		}		
	}
}
