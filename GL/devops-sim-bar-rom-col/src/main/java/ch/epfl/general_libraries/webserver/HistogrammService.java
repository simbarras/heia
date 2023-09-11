package ch.epfl.general_libraries.webserver;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.jfree.chart.JFreeChart;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import ch.epfl.general_libraries.charts.HistogramProvider;
import ch.epfl.general_libraries.logging.Logger;

public class HistogrammService extends AbstractService {

	private static final Logger logger = new Logger(HistogrammService.class);

	@Override
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Basic Constructor
	 * @param context The Context provided by SIMPLE
	 */
	public HistogrammService(Context context) {
		super(context);
	}

	@Override
	public void process(Request req, Response resp) {
		super.startProcess(req,resp);
		try {
		
			String val = req.getParameter("vals");
			
			String[] vals = val.split(", ");
			vals[0] = vals[0].replaceAll("\\[","");
			vals[vals.length-1] = vals[vals.length-1].replaceAll("\\]","");
			double[] rVals = new double[vals.length];
			for (int i = 0 ; i < vals.length ; i++) {
				rVals[i] = Double.parseDouble(vals[i]);
			}
			JFreeChart jFreeChart = HistogramProvider.getHistogram(rVals, "Histogram");
			
			BufferedImage im = jFreeChart.createBufferedImage(400,400);
			resp.set("Content-Type", "image/png");
			ImageIO.write(im, "png", resp.getOutputStream());
			
		//	String target = req.getURI(); // Retrive the URL of the target website
			try { resp.getOutputStream().close();	} catch (Exception e) {}
			super.endProcess(req, resp);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}	
	
	
}
