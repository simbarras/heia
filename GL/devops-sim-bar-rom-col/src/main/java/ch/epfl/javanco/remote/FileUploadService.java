package ch.epfl.javanco.remote;


import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import simple.http.upload.DiskFileUpload;
import simple.http.upload.FileItem;
import simple.http.upload.FileUploadBase;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.io.JavancoFile;


/**
 * Basic File Upload Service to handle the upload of files.
 * @author Christophe Trefois
 *
 */
public class FileUploadService extends AbstractJavancoService {
	/**
	 * Logger used for intelligent debugging.
	 */
	private static final Logger logger = new Logger(FileUploadService.class);
	private String sessionID = "";
	private String toSend = "";
	private Document documentToSend = null;

	protected FileItem mndFile;
	protected Hashtable<String,String> arguments;

	@Override
	public Logger getLogger() {
		return logger;
	}
	/**
	 * Basic Constructor
	 * @param context The context provided by SIMPLE
	 */
	public FileUploadService(Context context) {
		super(context);
	}
	/**
	 * @param fileFieldName Name of the field which contains the path in the HTML form
	 */
	protected boolean parsePostedContent(Request req, String fileFieldName) throws Exception {
		boolean response = false;
		if(FileUploadBase.isMultipartContent(req)) {
			DiskFileUpload diskUpload = new DiskFileUpload();
			diskUpload.setRepositoryPath(new File(JavancoFile.getDefaultOutputDir()).getAbsolutePath());
			List<?> multiContentItemList = diskUpload.parseRequest(req);
			arguments = new Hashtable<String,String>();
			for (Object o : multiContentItemList) {
				FileItem fi = (FileItem)o;
				if (fi.getFieldName().equals(fileFieldName)) {
					logger.debug("Filename: " + fi.getName() + " - Size: " + fi.getSize());
					mndFile = fi;
					response = true;
				} else {
					arguments.put(fi.getFieldName(), fi.getString());
				}
			}
		}
		return response;
	}

	/**
	 * Process the HTTP request / response. <br>
	 * Checks for a Multi-Part Content Flag. If it is set, it will read the file into an FileItem object.
	 * The uploaded file should be an XML file. After the file is read it, this method tries to open a new Network using the file provided.
	 * <br>
	 * <br>#author Trefex
	 */
	@Override
	public void process(Request req, Response resp)  {
		super.startProcess(req,resp);
		try {
			sessionID = getSessionID(req);
			if(req != null) {
				if(parsePostedContent(req, "fileToUpload")) {
					// Load to Javanco
					IRemoteGraphWrapper wrapper = super.getCurrentWrapper(sessionID, true);
					wrapper.openNetwork(mndFile.getString());
					resp.set("Content-Type", "text/xml"); // Important to set XML Content-Type !!
					toSend = "<root><file><filename>" + mndFile.getName() + "</filename>" + "<filesize>" + mndFile.getSize() + "</filesize>" + "</file></root>";
					PrintStream out = resp.getPrintStream();
					out.append(toSend);
					out.close();
					logger.debug("Sending back to browser: " + toSend);
				}
			}
		} catch (Exception e) {
			logger.error("Caught Exception -> ", e);
			documentToSend = DocumentHelper.createDocument();
			Element root2 = documentToSend.addElement("root");
			root2.addElement("errorMsg").addText(e.getMessage());
			root2.addElement("status").addText("2");
			// Send the response back to the browser
			resp.set("Content-Type", "text/xml"); // Important to set XML Content-Type !!
			try {
				OutputStream out = resp.getOutputStream();
				XMLWriter writer = new XMLWriter(out);
				writer.write(documentToSend);
				writer.close();
				out.close();
			} catch (Exception e1) {
				logger.error("Exception Caught: ", e1);
			}
		}
		super.endProcess(req, resp);
	}


	/**
	 * Writes the content from the fileItem to a file on the Hard-Disk.
	 * <br><br>
	 * #author Christophe Trefois
	 * @param fileItem the FileItem to save to the disk
	 * @param cookieValue
	 */
	/*	public String writeFile(FileItem fileItem, String cookieValue) {
		String[] filePathElements;
		String returnName = "";
		try {
			if(fileItem.getName().contains("\\")) {
				filePathElements = fileItem.getName().split("\\\\");
			} else {
				filePathElements = fileItem.getName().split("/");
			}
			String fileName = filePathElements[filePathElements.length-1];
			File fileToSave = new File(cookieValue + "_" + fileName);

			logger.debug("Saved to Path: " + fileToSave.getCanonicalPath());

			returnName = cookieValue + "_" + fileName;
			fileItem.write(fileToSave);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return returnName;
	}*/
}
