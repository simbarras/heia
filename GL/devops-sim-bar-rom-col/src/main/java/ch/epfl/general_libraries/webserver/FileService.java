package ch.epfl.general_libraries.webserver;

import java.io.IOException;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;

/**
 * Provide basic web-server functionality. <br>
 * Just outputs an asked filename to the browser. <br>
 * i.e. /index.html will be fetched and just printed to the browser.
 * @author Christophe Trefois
 *
 */
public class FileService extends AbstractService {
	/**
	 * Logger used for intelligent debugging.
	 */
	private static final Logger logger = new Logger(FileService.class);

	@Override
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Basic Constructor
	 * @param context The Context provided by SIMPLE
	 */
	public FileService(Context context) {
		super(context);
	}
	/**
	 * 
	 * <br><br>
	 * #author Christophe Trefois
	 */
	@Override
	public void process(Request req, Response resp) {
		super.startProcess(req,resp);
		String target = req.getURI(); // Retrive the URL of the target website

		if(target.endsWith("/")) {
			target += "/index.html";
		}
		try {
			if(req.getParameters().size() > 0) {
				target += "/index.html";
			}
		} catch (IOException e) {
			logger.error("Cannot access to request parameters", e);
		}
		super.returnFile(target, resp);
		super.endProcess(req, resp);
	}
}
