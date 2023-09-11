package ch.epfl.javancox.results_manager.gui;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AdvancedDataRetriever;
import ch.epfl.general_libraries.utils.DateAndTimeFormatter;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.display.AbstractChartProvider;
import ch.epfl.javancox.results_manager.display.AbstractChartProvider.AbstractChartPanel;
import ch.epfl.javancox.results_manager.display.gui.BarChartPanel;
import ch.epfl.javancox.results_manager.display.gui.HistChartPanel;
import ch.epfl.javancox.results_manager.display.gui.ParetoPanel;
import ch.epfl.javancox.results_manager.display.gui.XYLineChartPanel;



public class DefaultResultDisplayingGUI extends AbstractResultsDisplayer {

	JFrame frame = null;

	private List<Class<? extends AbstractChartPanel>> displayerClasses;
	private ComplexDisplayPanel cmp;


	private DefaultResultDisplayingGUI(AdvancedDataRetriever retriever) {
		super(retriever);
		this.displayerClasses = getDefaultDisplayers(retriever);
	}
	
	public static AbstractResultsDisplayer displayDefault_(SmartDataPointCollector a) {	
		return displayDefault(a);
	}

	public static AbstractResultsDisplayer displayDefault(AdvancedDataRetriever a) {
		DefaultResultDisplayingGUI gui = new DefaultResultDisplayingGUI(a);
		gui.showFrame("", null, null, null, (String)null);
		return gui;
	}


	public static AbstractResultsDisplayer displayDefault(AdvancedDataRetriever a, String frameTitle) {
		DefaultResultDisplayingGUI gui = new DefaultResultDisplayingGUI(a);
		gui.showFrame(frameTitle, null, null, null, (String)null);
		return gui;
	}
	
	public static AbstractResultsDisplayer displayDefault(AdvancedDataRetriever a, String frameTitle,
			String yAxis, String xAxis, String shape, String color) {
		DefaultResultDisplayingGUI gui = new DefaultResultDisplayingGUI(a);
		gui.showFrame(frameTitle, yAxis, xAxis, shape, color);
		return gui;		
	}
	
	public void refresh() {
		cmp.resetAFC();	
	}
	
	public JFrame showFrame() {
		return this.showFrame("", null, null, null, (String)null);
	}		

	public JFrame showFrame(String title, String yAxis, String xAxis, String shape, String color) {
		return showFrame(title, yAxis, xAxis, shape, new String[]{color});
	}

	public JFrame showFrame(String title, String yAxis, String xAxis, String shape, String[] colors) {
		frame = new JFrame();
		cmp = new ComplexDisplayPanel(this.retriever, this.displayerClasses, title, frame);
		if (yAxis != null && xAxis != null && shape != null && colors != null) {
			cmp.setDefault(yAxis, xAxis, shape, colors);
		}
		frame.setTitle(title + " - " + DateAndTimeFormatter.getDateAndTime(System.currentTimeMillis()));
		frame.setJMenuBar(cmp.getMenubar());
		frame.setContentPane(cmp);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setSize(new java.awt.Dimension(1000, 750));
		frame.setVisible(true);
		//	frame.setExtendedState(frame.MAXIMIZED_BOTH);

		return frame;
	}
	
	public void setCocFile(File f){
		if (this.cmp != null) this.cmp.loadCocFile(f);
	}
	
	public void getNewFrame() {
		showFrame();
	}
	
	private static List<Class<? extends AbstractChartPanel>> getDefaultDisplayers(AdvancedDataRetriever a) {
		List<Class<? extends AbstractChartProvider.AbstractChartPanel>> list = new ArrayList<Class<? extends AbstractChartProvider.AbstractChartPanel>>();
		list.add(XYLineChartPanel.class);
		list.add(BarChartPanel.class);
		list.add(HistChartPanel.class);
		list.add(ParetoPanel.class);
		return list;
	}

	public static void main(String[] args) {
		DefaultResultDisplayingGUI gui = new DefaultResultDisplayingGUI(new SmartDataPointCollector());
		gui.showFrame();
		if (args.length > 0)
			gui.setCocFile(new File(args[0]));
	}


}
