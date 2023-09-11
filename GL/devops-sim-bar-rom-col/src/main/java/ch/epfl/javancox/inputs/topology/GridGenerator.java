package ch.epfl.javancox.inputs.topology;

import java.util.Map;
import java.util.TreeMap;

import ch.epfl.JavancoGUI;
import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;

public class GridGenerator extends AbstractPlanarDeterministicGenerator {

	@Override
	public String getName() {
		return "grid_generator";
	}

	private int linkLength;
	private int width;
	private int nbNodes;

	@Override
	public Map<String, String> getGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("grid_width",width+"");
		map.put("grid_element_width", linkLength+"");
		return map;
	}

	@ConstructorDef(def = "Build a grid with variable link length, width, and number of nodes")
	public GridGenerator(@ParamName(name = "Link length") int linkLength,
			@ParamName(name = "Grid width") int width,
			@ParamName(name = "Number of nodes") int nbNodes) {
		this.linkLength = linkLength;
		this.width = width;
		this.nbNodes = nbNodes;
	}

	@ConstructorDef(def = "Build a standard grid of 3x4 nodes")
	public GridGenerator() {
		this.linkLength = 100;
		this.width = 4;
		this.nbNodes = 12;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		generate(agh, linkLength, width, nbNodes);
	}

	@Override
	public int getNumberOfNodes() {
		return nbNodes;
	}

	public static void generate(AbstractGraphHandler agh, int linkLength, int width, int nbNodes) {
		ensureLayerStatic(agh);
		createGrid(agh, nbNodes, width, linkLength);
	}

	public static void main(String[] args) throws Exception {
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		agh.activateMainDataHandler();
		agh.activateGraphicalDataHandler();
		GridGenerator.generate(agh, 100, 5, 43);
		JavancoGUI.displayGraph(agh, 800,800);
	}


}
