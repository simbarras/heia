package ch.epfl.javancox.topology_analysis.network_metrics;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.MethodFullDef;
import ch.epfl.general_libraries.path.HopsCalculator;
import ch.epfl.general_libraries.path.PathCalculator;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.path.JavancoPathCalculator;
import ch.epfl.javanco.path.JavancoShortestPathSet;

public class DiameterComputer extends NetworkWideMetricComputer {

	private PathCalculator calc;

	private boolean directed;

	@ConstructorDef(def="Computes the graph diameter. Bases its routing on the number of hops. Considers links are not directed")
	public DiameterComputer() {
		this.calc = new HopsCalculator();
		this.directed = false;
	}

	@ConstructorDef(def="<html>Diameter computer with <i>configurable</i> path calculator</html>")
	public DiameterComputer(
			@ParamName(name="A path calculator") PathCalculator calc,
			@ParamName(name="Tells wheter graph must be considered directed or not") boolean directed) {
		this.calc = calc;
		this.directed = directed;
	}

	@Override
	@MethodFullDef(name="Network Diameter")
	protected double computeNetworkWideMetric() {
		AbstractGraphHandler agh = rpi.getInput().getAgh();
		if (calc instanceof JavancoPathCalculator) {
			JavancoPathCalculator jCalc = ((JavancoPathCalculator)calc);
			jCalc.setAgh(agh);
			float[][] costs = jCalc.getRoutingCosts(rpi.getInput().getTopologyLayer(), directed);
			return Matrix.max(costs);
		} else {
			try {
				JavancoShortestPathSet shortestPaths = new JavancoShortestPathSet(agh, rpi.getInput().getTopologyLayer(), calc, directed);
				shortestPaths.setSymmetry();
				return Matrix.max(calc.getRoutingCost(shortestPaths));
			}
			catch (Exception e) {
				return Double.NaN;
			}
		}
	}
	
	@Override
	public String getResultName() {
		return "Graph diameter";
	}	

}
