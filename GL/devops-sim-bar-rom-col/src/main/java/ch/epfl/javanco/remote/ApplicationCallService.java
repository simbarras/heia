package ch.epfl.javanco.remote;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;

public class ApplicationCallService extends FileUploadService {

	private static final Logger logger = new Logger(FileUploadService.class);

	public ApplicationCallService(Context context) {
		super(context);
	}

	@Override
	public void process(Request req, Response resp)  {
		super.startProcess(req,resp);
		try {
			String sessionID = getSessionID(req);
			if(req != null) {
				if(parsePostedContent(req, "dataFile")) {
					IRemoteGraphWrapper wrapper = super.newWrapper(sessionID);
					wrapper.openNetwork(mndFile.getString());
					String name = super.arguments.get("applicationName");
					String method = super.arguments.get("methodName");
					wrapper.callTool(name + ":" + method, arguments);
					String toSend = wrapper.networkStats();
					super.removeWrapper(sessionID);
					super.sendBackFile(resp, toSend, mndFile.getName());
				} else {
					super.sendBackTextMessage(resp, "Wrong command");
				}
			}
		} catch (Exception e) {
			logger.error("Caught Exception -> ", e);
			Document documentToSend = DocumentHelper.createDocument();
			Element root2 = documentToSend.addElement("root");
			root2.addElement("errorMsg").addText(e.getMessage());
			root2.addElement("status").addText("2");
			try {
				super.sendBackXML(resp, documentToSend);
			}
			catch (Exception ex) {
				logger.error("Error when processing application call", ex);
			}
		}
	}
}
