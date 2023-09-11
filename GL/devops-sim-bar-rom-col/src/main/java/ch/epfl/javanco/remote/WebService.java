package ch.epfl.javanco.remote;

import java.util.Hashtable;

import org.dom4j.Element;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.web.MNDSoapMessage;

public class WebService extends AbstractJavancoService {
	/**
	 * Logger used for intelligent debugging.
	 */
	private static final Logger logger = new Logger(WebService.class);

	@Override
	public Logger getLogger() {
		return logger;
	}

	public WebService(Context context) {
		super(context);
	}

	/**
	 * WebService entry point
	 * #author S�bastien Rumley
	 */
	@Override
	public void process(Request req, Response resp) throws java.io.IOException {
		super.startProcess(req,resp);
		//	OutputStream s = null;
		//	OutputStreamWriter writer = null;
		try {
			logger.debug("--> \"WebService\" processer has been called");
			String sessionID = getSessionID(req);

			IRemoteGraphWrapper wrapper = super.newWrapper(sessionID);
			logger.debug("Retrieved wrapper is : " + wrapper);

			// THIS BLOCK CAN BE ACTIVATED FOR DEBUGGING AND TO SEE WHAT'S COMING OUT OF THE SOAP MESSAGE

			/*	BufferedReader inreader = new BufferedReader(new InputStreamReader(req.getInputStream()));
	    	String nextLine = "";
	    	StringBuilder sss = new StringBuilder();
	    	while ((nextLine = inreader.readLine()) != null) {
	    		sss.append(nextLine);
	    	}
	    	java.io.ByteArrayInputStream sr = new java.io.ByteArrayInputStream(sss.toString().getBytes());  */

			logger.debug("Constructing SOAP message");
			MNDSoapMessage msg = new MNDSoapMessage(req.getInputStream());

			logger.info("Opening network in Javanco server");
			wrapper.openNetwork(msg.getMND().asXML());

			String service = req.getURI();
			service = service.replace("/ws/","");

			Hashtable<String, String> table = msg.getParams();

			String response = wrapper.callTool(service, table);

			super.removeWrapper(sessionID);

			MNDSoapMessage respSoap = new MNDSoapMessage();

			Element el = wrapper.getNetworkDocument();

			respSoap.setMND(el);
			respSoap.setResp(response);


			logger.debug("Writing answer");
			resp.setText("HTTP/1.1 200 OK");
			resp.set("Content-Type", "text/xml"); // Important to set XML Content-Type !!
			resp.set("test-test", "test");
			respSoap.writeMessage(resp.getOutputStream());
			resp.commit();
			resp.getOutputStream().close();
		}
		catch (Throwable ex) {
			logger.error("Error happend in WebService",ex);
			resp.getOutputStream().close();
			//    	writer.close();
			//	    	s.close();
		}
		super.endProcess(req, resp);
	}

}

