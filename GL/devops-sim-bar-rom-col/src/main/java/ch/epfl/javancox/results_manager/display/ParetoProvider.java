package ch.epfl.javancox.results_manager.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jfree.chart.plot.XYPlot;

import ch.epfl.general_libraries.charts.ChartContainer;
import ch.epfl.general_libraries.charts.CustomDeviationRenderer;
import ch.epfl.general_libraries.charts.CustomNumberAxis;
import ch.epfl.general_libraries.charts.CustomXYIntervalSeries;
import ch.epfl.general_libraries.charts.CustomXYIntervalSeriesCollection;
import ch.epfl.general_libraries.charts.Problem;
import ch.epfl.general_libraries.charts.Problem.Severity;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.results.AdvancedDataRetriever;
import ch.epfl.general_libraries.results.DataRetrievalOptions;
import ch.epfl.general_libraries.results.DataSeries;
import ch.epfl.general_libraries.utils.Mapper;
import ch.epfl.general_libraries.utils.PairList;
import ch.epfl.general_libraries.utils.ParetoPoint;
import ch.epfl.general_libraries.utils.ParetoSet;

public class ParetoProvider extends XYChartProvider {
	
	public boolean xHigherBetter;
	public boolean yHigherBetter;
	
	class ParetoEntry extends ParetoPoint {
		
		private double x;
		private double y;
	//	private PairList<String, String> desc;
	//	private String alphaxValue;
		
		public ParetoEntry(double x, double y, PairList<String, String> desc, String alphaxValue) {
			this.x = x;
			this.y = y;
		//	this.desc = desc;
		//	this.alphaxValue = alphaxValue;
		}

		@Override
		public double getValueOfDimensionN(int n) {
			if (n == 0)
				return x;
			if (n == 1)
				return y;
			throw new IllegalStateException();
		}

		@Override
		public boolean isDimensionNtheHigherTheBetter(int n) {
			if (n == 0) // x
				return xHigherBetter;
			if (n == 1)
				return yHigherBetter;
			throw new IllegalStateException();
		}

		@Override
		public int getDimensions() {
			return 2;
		}
		
	}

	/*
	 * This is a copy-paste version of the same method in the upper class
	 * TODO: remove duplicated code
	 * (non-Javadoc)
	 * @see ch.epfl.javancox.results_manager.display.XYChartProvider#buildOneMethod(ch.epfl.general_libraries.charts.ChartContainer, ch.epfl.general_libraries.results.DataRetrievalOptions, java.awt.BasicStroke, int, ch.epfl.general_libraries.results.AdvancedDataRetriever)
	 */
	protected boolean buildOneMethod(ChartContainer chart, DataRetrievalOptions options, BasicStroke stroke, int idx, AdvancedDataRetriever retriever) {
		XYPlot plot = chart.getChart().getXYPlot();
		CustomXYIntervalSeriesCollection seriesCollection = new CustomXYIntervalSeriesCollection(options);
		boolean useLines = !(options.getCriteriumSet().get(0).size() == 0);
		useLines &= (isWithLines|| isWithLinesWA);
		CustomDeviationRenderer renderer = new CustomDeviationRenderer(useLines, true, chart); // chart is given for problem reporting
		renderer.useMultipointHighlight(!isWithoutIdentitical);
		renderer.setBaseStroke(stroke);
		renderer.setAlpha(0.25f);

		if( this.isNoScaleX )
			plot.getDomainAxis().setVisible(false);

		renderer.setBasePaint(Color.CYAN);
		renderer.setAutoPopulateSeriesFillPaint(true);
		if( idx == 0 )
			renderer.setBaseSeriesVisibleInLegend(true);
		else
			renderer.setBaseSeriesVisibleInLegend(false);
		plot.setRenderer(idx, renderer);
		plot.setDataset(idx, seriesCollection);

		// Values values = new Values(methodName, p);
		int noSerie = -1;

		/* clear all the lists */
		assosList.clear();
		xVals.clear();
		legends.clear();

//		expIDs.clear();

		min = MAX_VALUE;
		max = MIN_VALUE;

		DataSeries.DataSeriesSorter sorter = new DataSeries.DataSeriesSorter();
		boolean nonNumericXValues = false;

		TreeMap<Double, String> tickEquivalenceMap = new TreeMap<Double, String>();
		
		List<DataSeries> dataSeries = retriever.getChartValues(options, options.method[idx]);
	
		HashSet<String> toSortAndEvalFromZeroToOne = new HashSet<String>();
		
		yaxis = options.method[idx];
		xaxis = options.xAxisProperty;
	
		for( DataSeries cv : dataSeries) {
			if( cv instanceof DataSeries.EmptyChartValues )
				continue;
			toSortAndEvalFromZeroToOne.add(cv.getValueOfLastCrit());
		}
		
		Map<String, Float> colorMap = Mapper.getMap(toSortAndEvalFromZeroToOne, isLogColors);
		
		ArrayList<ParetoSet<ParetoEntry>> psets = new ArrayList<ParetoSet<ParetoEntry>>();
		
		
		for( DataSeries cv : dataSeries) {
			ParetoSet<ParetoEntry> paretoSet = new ParetoSet<ParetoEntry>(2);
			psets.add(paretoSet);
			if( cv instanceof DataSeries.EmptyChartValues )
				continue;
			Collection<DataSeries.DataSeriesSorter.XEntry> entries = sorter.getNumerisedList(cv);			
			for( DataSeries.DataSeriesSorter.XEntry xe : entries ) {
				float y;
				if (yHigherBetter) {
					y = MoreMaths.max(xe.values);
				} else {
					y = MoreMaths.min(xe.values);
				}
				ParetoEntry entry = new ParetoEntry(xe.numericXValue, y, cv.getSerieCriteria(), xe.alphaXValue);
				paretoSet.addCandidate(entry);

			}
		}
		
		int index = 0;
		for( DataSeries cv : dataSeries) {
			
			ParetoSet<ParetoEntry> paretoSet = psets.get(index);
			if( cv instanceof DataSeries.EmptyChartValues )
				continue;

			String legend = cv.getLegend();
			if( legend == null )
				continue;
			CustomXYIntervalSeries serie__ = new CustomXYIntervalSeries(legend);
			legends.add(cv.getSerieCriteria());
			noSerie++;
			float serieColor = colorMap.get(cv.getValueOfLastCrit());
			
			setSerieProperty(renderer, noSerie, cv.crit, serieColor);

			Collection<DataSeries.DataSeriesSorter.XEntry> entries;
			entries = sorter.getNumerisedList(cv);

			for( DataSeries.DataSeriesSorter.XEntry xe : entries ) {
				float x = xe.numericXValue;
				if( isLogX && x <= 0.0 )
					continue;

				for (Float y : xe.values) {
					// float y = xe.mean();
					// float x = xe.numericXValue;
					if (y.isInfinite() || y.isNaN()) {
						chart.addProblem(new Problem(Severity.INFORMATION,
								"Some infinite or NaN values for x="
										+ xe.alphaXValue, options.method[idx]));
						continue;
					}
					if (isLogY && y <= 0.0) {
						chart.addProblem(new Problem(Severity.INFORMATION,
								"Some 0 values (not allowed with log) for x="
										+ xe.alphaXValue, options.method[idx]));
						continue;
					}
					// try to find it again in list
					for (ParetoEntry ent : paretoSet) {
						if (ent.x == x && ent.y == y) {
							double[] vals = new double[] { y, y, y, y, y, y, y, y, y, x };
							serie__.add(vals);
							assosList.add(new Association(xe.alphaXValue, cv.getSerieCriteria(), y, x));
							break;
						}
					}

				}
				if (xe.nativeX == false) {
					if (nonNumericXValues == false) {
						chart.addProblem(new Problem(Severity.ERROR,
								"Cant cast x values to float",
								options.method[idx]));
						nonNumericXValues = true;
					}
					chart.addProblem(new Problem(Severity.INFORMATION,
							((int) (float) xe.numericXValue) + " = "
									+ xe.alphaXValue, options.method[idx]));
					tickEquivalenceMap.put((double) xe.numericXValue,
							xe.alphaXValue);
				}
				xVals.add(xe.alphaXValue);
			}
			seriesCollection.addSeries(serie__);
			index++;
		}
		if( nonNumericXValues ) {
			CustomNumberAxis axis = (CustomNumberAxis) plot.getDomainAxis();
			axis.setTickMap(tickEquivalenceMap);
			//	CategoryAxis axis = new CategoryAxis(label);
			//	chart.getChart().getXYPlot().setDomainAxis(axis);
		}
		return false;
	}	
	
}
