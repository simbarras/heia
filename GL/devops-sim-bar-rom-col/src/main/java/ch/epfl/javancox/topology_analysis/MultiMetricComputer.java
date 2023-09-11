package ch.epfl.javancox.topology_analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.math.StatFunctions;
import ch.epfl.general_libraries.path.BFSEnumeratedPathSet;
import ch.epfl.general_libraries.path.PathSet;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.path.JavancoShortestPathSet;

public abstract class MultiMetricComputer extends AbstractTopologyAnalyser {

	protected abstract double[] computeForAllElements();
	private List<Double> list;
	private boolean computed = false;
	
	public double computeMainResult() {
		return computeMean();
	}
	
	private void compute() {
		if (computed == false) {
			list = computeAll();
		}
	}
	
	public static PathSet getShortestPathSet(AbstractGraphHandler agh, boolean directed, boolean multiPath) {
		PathSet sp;
		if (!multiPath || agh.getNodeContainers().size() > 25) {
			sp = new JavancoShortestPathSet(agh, "length", directed);
		} else {
			double[][] d = agh.getEditedLayer().getIncidenceMatrixDouble(directed);
			
			BFSEnumeratedPathSet enumPset = new BFSEnumeratedPathSet(d, 10);

			enumPset.enumerateAndStoreAll();
			sp = enumPset;
		}
		return sp;
	}
				
	@MethodDef
	public List<Double> computeAll() {
		double[] comp = computeForAllElements();
		ArrayList<Double> list = new ArrayList<Double>(comp.length);

		for (int i = 0; i < comp.length; i++) {
			list.add(comp[i]);
		}

		return list;
	}
	
	@MethodDef
	public double computeMax() {
		compute();
		double max = Double.NEGATIVE_INFINITY;
		for (Double d : list) {
			if (d > max) {
				max = d;
			}
		}
		if (max == Double.NEGATIVE_INFINITY) return Double.NaN;
		return max;
	}
	@MethodDef
	public double computeMin() {
		compute();
		double min = Double.POSITIVE_INFINITY;
		for (Double d : list) {
			if (d < min) {
				min = d;
			}
		}
		if (min == Double.POSITIVE_INFINITY) return Double.NaN;
		return min;
	}
	@MethodDef
	public double computeMed() {
		compute();
		Collections.sort(list);
		return list.get(list.size()/2);
	}
	@MethodDef
	public double computeMean() {
		compute();
		if (list.size() == 0) {
			return 0;
		}
		double total = 0;
		for (Double d : list) {
			if (d.equals(Double.NaN)) return Double.NaN;
			total += d;
		}
		return total/list.size();
	}
	@MethodDef
	public double computeVar() {
		compute();
		return StatFunctions.getVarianceD(list);
	}
	
	public String getResultName() {
		return this.getClass().getSimpleName() + "(mean)";
	}	
	
}
