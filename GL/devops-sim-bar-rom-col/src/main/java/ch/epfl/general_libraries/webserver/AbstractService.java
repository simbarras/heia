package ch.epfl.general_libraries.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;

import simple.http.GenericHeader;
import simple.http.Request;
import simple.http.Response;
import simple.http.State;
import simple.http.load.Service;
import simple.http.serve.Context;
import simple.http.session.Session;
import simple.util.net.Cookie;
import ch.epfl.general_libraries.logging.Logger;

public abstract class AbstractService extends Service {

	private static final Logger logger = new Logger(AbstractService.class);

	public abstract Logger getLogger();

	public AbstractService(Context context) {
		super(context);
	}

	/**
	 * Tries to isolate the sessionID from the current provided Request<br>
	 * #author Christophe Trefois
	 * @param req The processed Request
	 * @return A String representation of the sessionID
	 */
	public String getSessionID(Request req) {
		String returnSessionID = "";

		// Session handling
		Session currentSession = req.getSession();
		// Set Session Creation Date
		if(currentSession.isEmpty()) {
			Date currentTime = new Date();
			synchronized(currentTime) {
				currentTime = new Date();
				currentSession.put("date", currentTime);
			}
		}

		State myState = req.getState();
		for (int i = 0; i < myState.getCookies().length; i++) {
			Cookie myCookie = myState.getCookies()[i];
			logger.debug("Cookie: " + myCookie.getName() + " - " + myCookie.getValue() + " will expire: " + myCookie.getExpiry());
			returnSessionID = myCookie.getValue();
		}
		return returnSessionID;
	}

	protected void startProcess(Request req, Response resp) {
		Thread.currentThread().setName("Javanco WebServer service processor");
		//    	int count = req.headerCount();
		Logger serviceLogger = getLogger();
		serviceLogger.info(">>> "+ req.getMethod() + " : " + req.getURI());
		State myState = req.getState();
		if (myState.getCookies().length > 0) {
			serviceLogger.debug(">   Session : " + myState.getCookies()[0].getValue());
		}	
		if (serviceLogger.getEffectiveLevel().equals(Logger.TRACE)) {
			logHeaders(req);
		}
		serviceLogger.debug(">>>--------- ");
	}

	protected void endProcess(Request req, Response resp) {
		Logger serviceLogger = getLogger();
		serviceLogger.info("<<< Response to " + resp.getInetAddress());
		if (serviceLogger.getEffectiveLevel().equals(Logger.TRACE)) {
			logHeaders(resp);
		}
		serviceLogger.debug("<<<--------- ");
	}

	private void logHeaders(GenericHeader head) {
		int count = head.headerCount();
		for (int i = 0 ; i < count ; i++) {
			String value = head.getValue(i);
			String name  = head.getName(i);
			if (name.length() < 20) {
				name = name.concat("                     ");
			}
			if (value.length() < 20) {
				name = name.concat("                     ");
			}
			if (name.length() >= 20) {
				name = name.substring(0,19);
			}
			if (value.length() >= 50) {
				value = value.substring(0,49);
			}
			getLogger().trace("    " + name + ": " + value);
		}
	}

	protected void returnFile(String target, Response resp) {
		resp.set("Content-Type", context.getContentType(target));
		OutputStream out;
		try {
			out = resp.getOutputStream();
			context.getContent(target).write(out);
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			getLogger().error("Cannot found and send file " + target + " to the caller", e);
		}
	}

	protected void sendBackTextMessage(Response resp, String msg) throws IOException {
		resp.set("Content-Type", "text/plain"); // Important to set XML Content-Type !!
		PrintStream out = resp.getPrintStream();
		out.append(msg);
		out.close();
		logger.debug("Sending text back to browser: " + msg);
	}

	protected void sendBackFile(Response resp, String content, String fileName)  throws IOException {
		resp.set("Content-Type", "application/x-download");
		resp.set("Content-Disposition", "attachment; filename=" + fileName);
		PrintStream out = resp.getPrintStream();
		out.append(content);
		out.close();
		logger.debug("Sending file back to browser: " + fileName);
	}

	protected void sendBackXML(Response resp, Document toSend)  throws IOException  {
		resp.set("Content-Type", "text/xml"); // Important to set XML Content-Type !!
		OutputStream out = resp.getOutputStream();
		XMLWriter writer = new XMLWriter(out);
		writer.write(toSend);
		writer.close();
		out.close();
		logger.debug("Sending xml back to browser: ");
	}
}

