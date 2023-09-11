package ch.epfl.javancox.results_manager.display.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import ch.epfl.general_libraries.utils.PairList;

public class MatlabPlot {
//	public ArrayList<Double> xVector;
//	public ArrayList<Double> yVector;
//	public ArrayList<String> xLabelVector;
//	public ArrayList<Double> error;	
	public ArrayList<Entry> entries;
	public PairList<String, String> legend;
	public boolean xText;
	
	public boolean hasError;
	
	public static class Entry implements Comparable<Entry>{
		public Entry(double x, String xText, double y, double error) {
			this.x = x;
			this.xLabel = xText;
			this.y = y;
			this.error = error;
		}
		public double x;
		public String xLabel;
		public double y;
		public double error;
		@Override
		public int compareTo(Entry o) {
			return (int)Math.signum(this.x - o.x);
		}
	}
	
	
	public MatlabPlot(PairList<String, String> l, boolean xText) {
		entries = new ArrayList<Entry>();
		legend = l;
		this.xText = xText;
	}
	
	
	public void addxandyAndError(String xValOrString, double xVal, double yVal,
			double d) {
		Entry e;
		if (xText) {
			e = new Entry(xVal, xValOrString, yVal, d);
		} else {
			e = new Entry(Double.parseDouble(xValOrString), xValOrString, yVal, d);
		}
		entries.add(e);
	}	
	
	public static class MATLABPlotMap {

		public MATLABPlotMap() {
			plots = new ArrayList<MatlabPlot>();
		}

		public ArrayList<MatlabPlot> plots;

		public void add(MatlabPlot plot) {
			plots.add(plot);
		}

		public boolean containsPlot(PairList<String, String> other) {

			for( MatlabPlot p : plots ) {
				if( p.legend.equals(other) )
					return true;
			}

			return false;
		}

		public MatlabPlot getPlot(PairList<String, String> legend) {
			if( plots.size() == 0 || !containsPlot(legend) ) {
				return null;
			}

			for( MatlabPlot p : plots ) {
				if( p.legend.equals(legend) )
					return p;
			}

			return null;
		}

		public Iterator<MatlabPlot> getIterator() {
			return plots.iterator();
		}
	}


	public void sort() {
		Collections.sort(entries);
	}


}
