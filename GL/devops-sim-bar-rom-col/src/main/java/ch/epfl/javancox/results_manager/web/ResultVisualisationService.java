package ch.epfl.javancox.results_manager.web;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Content;
import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.webserver.AbstractService;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;

public class ResultVisualisationService extends AbstractService {
	
	private static final Logger logger = new Logger(ResultVisualisationService.class);
	
	public static final HashMap<String, SmartDataPointCollector> registeredDBs  = new HashMap<String, SmartDataPointCollector>();

	public ResultVisualisationService(Context context) {
		super(context); 
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected void process(Request req, Response resp) throws Exception {
		super.startProcess(req,resp);
		String session = super.getSessionID(req);
		resp.set("Content-Type", "text/html");
		OutputStream out = null;
		try {
			out = resp.getOutputStream();
			OutputStreamWriter ow = new OutputStreamWriter(out);			
			if (req.getParameters().size() > 0) {

			}
			Content cont = context.getContent("/gui.html");
			String st = cont.toString().replace("#METHOD_SELECTOR#", getMethodSelector(session));
			st = st.replace("#METHOD_SELECTOR#", getMethodSelector(session));	
			st = st.replace("#X_AXIS_SELECTOR#", getXAxisSelector(session));
			st = st.replace("#CRITERIUM_SELECTOR#", getCriteriumSelector(session));
			ow.write(st);
			ow.flush();
			out.close();
		} catch (Exception e) {
			logger.error("Unhandled Problem", e);
		} finally { // Send the response back to the browser
			try { 
				out.close();	
			} catch (Exception e) {
			}
		}

		super.endProcess(req, resp);		
		
	}

	private CharSequence getMethodSelector(String session) {
		StringBuilder sb = new StringBuilder();
		SmartDataPointCollector col = registeredDBs.get(session);
		sb.append("<select id='method' class='selectpicker'>");
		for (String s : col.getMetrics()) {
			if (!col.isInput(s)) {
				sb.append("<option>" + s + "</option>");
			}
		}
		sb.append("</select>");		
		return sb;
	}
	
	private CharSequence getXAxisSelector(String session) {
		StringBuilder sb = new StringBuilder();
		SmartDataPointCollector col = registeredDBs.get(session);
		sb.append("<select id='xAxis' class='selectpicker'>");
		for (String s : col.getParameters()) {
			if (col.isInput(s)) {
				sb.append("<option>" + s + "</option>");
			}
		}
		sb.append("</select>");		
		return sb;		
	}
	
	private CharSequence getCriteriumSelector(String session) {
		StringBuilder sb = new StringBuilder();
		SmartDataPointCollector col = registeredDBs.get(session);
		sb.append("<select id='criterium' class='selectpicker'>");
		for (String s : col.getParameters()) {
			if (col.isInput(s)) {
				sb.append("<option>" + s + "</option>");
			}
		}
		sb.append("<option>CONSTANT</option>");		
		sb.append("</select>");		
		return sb;		
	}	
/*
	private CharSequence getContent(String session) {
		registeredDBs.get(session);
		
		return "test";
	}*/

	public static void registerDB(SmartDataPointCollector db, String session) {
		registeredDBs.put(session, db);
		
	}

}
