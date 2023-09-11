package ch.epfl.javancox.results_manager.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import simple.util.net.Parameters;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.results.DataRetrievalOptions;
import ch.epfl.general_libraries.results.Criterium;
import ch.epfl.general_libraries.results.CriteriumSet;
import ch.epfl.general_libraries.webserver.AbstractService;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.display.XYChartProvider;

public class ResultVisualisationImageService extends AbstractService {

	private static final Logger logger = new Logger(ResultVisualisationImageService.class);	
	
	private static BufferedImage voidImage;
	
	static {
		XYDataset set = new XYSeriesCollection();
		JFreeChart c = ChartFactory.createXYLineChart("no data", "no data", "no data", set, PlotOrientation.HORIZONTAL, false, false, false);
		voidImage = c.createBufferedImage(700, 500);
	}
	
	public ResultVisualisationImageService(Context context) {
		super(context);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected void process(Request req, Response resp) throws Exception {
		super.startProcess(req,resp);
		OutputStream s = null;
		try {
			BufferedImage image = getImage(req);
			logger.debug("Sending image...");
			s = resp.getOutputStream();

			resp.set("Content-Type", "image/png");
			resp.set("Cache-Control", "public, no-cache");
			ImageIO.write(image, "png", s);
			
			s.flush();
			s.close();
		}
		catch (Throwable t) {
			logger.error("cannot send image", t);
			try {
				s.close();
			}
			catch (Exception e) {}
		}
	}
	
	private BufferedImage getImage(Request req) throws IOException {
		String session = super.getSessionID(req);
		DataRetrievalOptions options = getOptionsFromRequest(req);
		if (options == null) return voidImage;
		SmartDataPointCollector col = ResultVisualisationService.registeredDBs.get(session);
		if (col == null) return voidImage;
		
		XYChartProvider displayer = new XYChartProvider();
		JFreeChart c = displayer.computeChart(options, col).getChart();
		
		return c.createBufferedImage(700,500);
	}
	
	private DataRetrievalOptions getOptionsFromRequest(Request req) throws IOException {
		DataRetrievalOptions options = new DataRetrievalOptions();
		Parameters params = req.getParameters();
	
		if (params.containsKey("method")) {
			options.method = new String[]{params.getParameter("method")};
		} else {
			return null;
		}
		if (params.containsKey("xAxis")) {
			options.xAxisProperty = params.getParameter("xAxis");
		} else {
			return null;
		}
		if (params.containsKey("criterium")) {
			CriteriumSet critSet = new CriteriumSet(1);	
			List<Criterium> selectedColors = new ArrayList<Criterium>(1);	
			Criterium c = new Criterium(params.getParameter("criterium"));
			selectedColors.add(c);
			critSet.add(selectedColors);
			options.criterias = critSet;
		} else {
			return null;
		}
		
		// be default, mean is on
		options.isMeanOrSum = true;
		options.isMeanInsteadOfSum = true;
		options.confInt = 95;
		options.filters = new HashMap<String, List<String>>();			
		
		if (params.containsKey("isMeanOrSum")) {
			options.isMeanOrSum = Boolean.parseBoolean(params.getParameter("isMeanOrSum"));
		}
		if (params.containsKey("isMedian")) {
			options.isMedian = Boolean.parseBoolean(params.getParameter("isMedian"));
		}
		if (params.containsKey("isFirst")) {
			options.isFirst = Boolean.parseBoolean(params.getParameter("isFirst"));
		}		
		if (params.containsKey("isMax")) {
			options.isMax = Boolean.parseBoolean(params.getParameter("isMax"));
		}		
		if (params.containsKey("isConfInt")) {
			options.isConfInt = Boolean.parseBoolean(params.getParameter("isConfInt"));
		}		
		if (params.containsKey("isQuartInt")) {
			options.isQuartInt = Boolean.parseBoolean(params.getParameter("isQuartInt"));
		}
		if (params.containsKey("is95")) {
			options.is95 = Boolean.parseBoolean(params.getParameter("is95"));
		}		
		if (params.containsKey("isMeanInsteadOfSum")) {
			options.isMeanInsteadOfSum = Boolean.parseBoolean(params.getParameter("isMeanInsteadOfSum"));
		}	
		
		return options;		
	}

}
