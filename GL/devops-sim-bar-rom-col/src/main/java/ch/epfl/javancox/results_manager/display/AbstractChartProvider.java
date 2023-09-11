package ch.epfl.javancox.results_manager.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.renderer.AbstractRenderer;

import ch.epfl.general_libraries.charts.ChartContainer;
import ch.epfl.general_libraries.results.AdvancedDataRetriever;
import ch.epfl.general_libraries.results.DataRetrievalOptions;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.general_libraries.utils.PairList;
import ch.epfl.javancox.results_manager.gui.ComplexDisplayPanel;

public abstract class AbstractChartProvider {
	
	protected static final Shape DEFAULT_SHAPE;

	static {
		DEFAULT_SHAPE = (new DefaultDrawingSupplier()).getNextShape();
	}
	
	public static final int MAX_VALUE = Integer.MAX_VALUE;
	public static final int MIN_VALUE = Integer.MIN_VALUE;
	
	public static int min = MAX_VALUE;
	public static int max = MIN_VALUE;
	
	public HashSet<String> xVals = new HashSet<String>();
	protected DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier();
	public Map<PairList<String,String>, Paint> seriesPaint = new HashMap<PairList<String,String>, Paint>();

	public AbstractChartProvider() {}

	public abstract ChartContainer  computeChart(DataRetrievalOptions options, AdvancedDataRetriever retriever);
	
	private static final int REF_YELLOW_RGB = new Color(255, 255, 85).getRGB();
	
	protected Paint setSerieProperty(AbstractRenderer renderer, int noSerie, PairList<String,String> color, boolean contColors, float serieColor) {
		Paint paint = seriesPaint.get(color);
		if (paint == null) {
			if (!contColors) {
				paint = drawingSupplier.getNextPaint();
				Color c = (Color) paint;
				if (c.getRGB() == REF_YELLOW_RGB) {
					paint = drawingSupplier.getNextPaint();
				}
			} else {
				float frac = serieColor;
				if (frac < 0.5) {
					paint = new Color((int)(frac*2f*255f), 0, 0);
				} else {
					int green = (int)((frac-0.5f)*1.5f*255f);
					paint = new Color(255, green, 0);
				}
				
			}

			seriesPaint.put(color, paint);
		}
		renderer.setSeriesPaint(noSerie, paint);
		return paint;
	}	

	/**
	 * For remote compatibility
	 */
	public ArrayList<String> getProperties() {
		return new ArrayList<String>();
	}
	
	protected Collection<String> sort(Collection<String> sort) {
		
		// try first to sort numerically
		ArrayList<Double> d = new ArrayList<Double>(sort.size());
		boolean alpha = false;
		for (String s : sort) {
			try {
				double test = Double.parseDouble(s);
				d.add(test);
			} catch (Exception e) {
				alpha = true;
				break;
			}
		} 
		if (alpha) {
			ArrayList<String> d2 = new ArrayList<String>(sort.size());
			for (String s : sort) {
				d2.add(s);
			}
			Collections.sort(d2);
			return d2;
		} else {
			Collections.sort(d);
			ArrayList<String> finalA = new ArrayList<String>();
			for (Double sortedD : d) {
				finalA.add(sortedD + "");
			}
			return finalA;
		}
	}	
	
	public static class Description {
		public String nom;
		public String description;
		public boolean isDistribution;

		public Description(String nom, String description,
				boolean isDistribution) {
			this.nom = nom;
			this.description = description;
			this.isDistribution = isDistribution;
		}

		public Description(String nom, String description) {
			this(nom, description, false);
		}
	}

	public static class Association {

		public Association(String xValOrString, PairList<String, String> legend, float y, double x) {
			this.xValOrString = xValOrString;
			this.legend = legend;
			stats = new float[] {y, y, y, y, y, y, y, y,y };
			xNumeric = x;
		//	first = y;
		}

		public Association(String xV, PairList<String, String> legend, float[] y, double x) {
			xValOrString = xV;
			this.legend = legend;
			stats = y;
			xNumeric = x;
		//	first = f[8];
		}

		public String xValOrString;
		public PairList<String, String> legend;
		public float[] stats;
		public double xNumeric;
	//	public double first;
	}
	
	public static abstract class AbstractChartPanel {
		


		protected JComboBox listGraphs2;

		protected AdvancedDataRetriever retriever;
		protected AbstractChartProvider displayer;

		protected List<Pair<String, TreeSet<String>>> variableProperties;

		protected AbstractChartPanel(AbstractChartProvider displayer, AdvancedDataRetriever retriever) {
			this.retriever = retriever;
			this.displayer = displayer;	
		}

		public abstract JPanel getConfigurationPanel(ComplexDisplayPanel panel);
		public abstract String getDecription();
		public abstract String getSecondOption();
		public abstract Pair<ActionListener[], String[]> getDisplayerPossibleActions(ComplexDisplayPanel panel);
		public abstract ChartContainer computeChart(DataRetrievalOptions options);

		public abstract Object getLegend();/* {
			Object tl = this.getLegend();
			JScrollPane jp;
			if (tl instanceof JComponent) {
				jp = new JScrollPane((JComponent)tl);
			} else {
				jp = new JScrollPane();
			}
			//jp.setPreferredSize(new Dimension(150, container.getHeight() - 10));
			jp.setVisible(true);
			return jp;
		}*/

		public List<String> getMetrics() {
			return retriever.getMetrics();
		}

		public Set<String> getParameters() {
			return retriever.getParameters();
		}

		public Set<String> getPossibleValuesOfGivenProperty(String property) {
			return retriever.getPossibleValuesOfGivenProperty(property);
		}

		public List<Pair<String, TreeSet<String>>> getVariableProperties() {
			return this.variableProperties;
		}

		protected void fillListGraphs2() {
			if (listGraphs2 == null) {
				return;
			}
			Vector<Description> list = new Vector<Description>();
			Collection<String> menuItems;
			menuItems = this.getMetrics();

			for(String s: menuItems) {
				list.add(new Description(s, s));
			}

			ListCellRenderer lcr = new DefaultListCellRenderer() {
				private static final long serialVersionUID = 3743493336564211493L;
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Description description = ((Description) value);
					String s = (value == null ? "" : description.description);
					Component comp = super.getListCellRendererComponent(list, s,
							index, isSelected, cellHasFocus);
					if (description != null && description.isDistribution) {
						comp.setForeground(Color.BLUE);
					}
					return comp;
				}
			};

			this.listGraphs2.setMaximumRowCount(list.size());
			this.listGraphs2.setModel(new DefaultComboBoxModel(list));
			this.listGraphs2.setRenderer(lcr);
		}

		protected String getOutputMessage() {
			String outputMessage = "";
			if (AbstractChartProvider.min != AbstractChartProvider.max) {
				outputMessage += " Between " + AbstractChartProvider.min + " and " + AbstractChartProvider.max
				+ " values of Y on the same X, means used";
			} else {
				if (AbstractChartProvider.min > 1) {
					outputMessage += " " + AbstractChartProvider.min
					+ " values of Y on the same X, means used";
				}
			}
			return outputMessage;
		}
	}	
	
}
