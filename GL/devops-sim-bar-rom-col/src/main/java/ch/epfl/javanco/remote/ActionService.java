package ch.epfl.javanco.remote;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;

public class ActionService extends AbstractJavancoService {
	/**
	 * Logger used for intelligent debugging.
	 */
	private static final Logger logger = new Logger(ActionService.class);
	private String graphName = "";
	private String returnVal = "";

	@Override
	public Logger getLogger() {
		return logger;
	}

	public ActionService(Context context) {
		super(context);
	}

	private boolean checkIfSessionExist(Request req, Response resp) throws Exception {
		ActionToPerform action = ActionToPerform.toPerform(req.getParameter("DO"));
		if (req.getParameters().size() > 0) {
			if (action != ActionToPerform.NEWGRAPH) {
				if (action !=ActionToPerform.RECOVERSESSION) {
					IRemoteGraphWrapper currentWrapper = super.getCurrentWrapper(getSessionID(req), false);
					if (currentWrapper == null) {
						Document documentToSend = DocumentHelper.createDocument();
						Element root2 = documentToSend.addElement("root");
						root2.addElement("status").addText("2");
						root2.addElement("errorMsg").addText("SESSION EXPIRED");
						OutputStream out = resp.getOutputStream();
						XMLWriter writer = new XMLWriter(out, new OutputFormat("", false));
						logger.debug(documentToSend.asXML()) ;
						writer.write(documentToSend);
						writer.close();
						out.close();
						super.endProcess(req, resp);
						return false;
					}
				}
			}
		}
		return true;
	}

	public enum ActionToPerform {
		NEWNODE, NEWLINK, NEWGRAPH, RECOVERSESSION,
		DELNODE, DELLINK, EXECGROOVY, GETFILE,
		NOVALUE,ZOOM_IN,ZOOM_OUT,SIZE_INCREASE,
		SIZE_DECREASE,SCROLL_UP,SCROLL_DOWN,SCROLL_LEFT,
		SCROLL_RIGHT,SELECT,UNSELECT,SET_ATTRIBUTE,
		VIEW_NATIVE,VIEW_BEST_FIT, REM_ELEMENT;

		// It is useful to catch exceptions here...
		public static ActionToPerform toPerform(String myString) {
			try {
				return valueOf(myString);
			} catch (Exception e) {
				return NOVALUE;
			}
		}
	}

	/**
	 * Process the request and process the response<br>
	 * <br>
	 * #author Christophe Trefois
	 */
	@Override
	public synchronized void process(Request req, Response resp) {
		super.startProcess(req,resp);
		Document documentToSend = null;
		try {
			if(!checkIfSessionExist(req,resp)) {
				return;
			}
			if(req.getParameters().size() > 0) {
				String sessionID = getSessionID(req);
				switch(ActionToPerform.toPerform(req.getParameter("DO"))) {
				case RECOVERSESSION: // Recover the current Session
					documentToSend = DocumentHelper.createDocument();
					if (!super.checkFactory()) {
						Element noFacError = documentToSend.addElement("root");
						noFacError.addElement("status").addText("2");
						noFacError.addElement("errorMsg").addText("No RMI service available");
					} else if(super.hasWrapper(sessionID)) {
						super.getCurrentWrapper(sessionID, false).unselectElement();
						documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).networkStats());
						documentToSend.getRootElement().addElement("status").addText("1");
						documentToSend.getRootElement().addElement("sessionID").addText(sessionID);
					} else {
						Element root2 = documentToSend.addElement("root");
						root2.addElement("status").addText("2");
					}

					break;
				case NEWGRAPH: // New Graph

					//verify if can accept new session
					if(LoadCheck.loadCheckIsUsed){
						if(!LoadCheck.canSessionAdded(sessionID)) {
							return;
						}
					}
					graphName = req.getParameter("name");
					logger.debug("Command:  create new graph (name=" + graphName + ")");
					returnVal = super.newWrapper(sessionID).createNetwork(graphName);
					documentToSend = DocumentHelper.createDocument();
					Element root = documentToSend.addElement("root");
					Element graph = root.addElement("graph");
					graph.addElement("graphName").addText(returnVal);
					graph.addElement("sessionID").addText(sessionID);
					logger.debug(documentToSend.asXML());

					//add the new session in the LoadCheck calss.
					if(LoadCheck.loadCheckIsUsed) {
						LoadCheck.initNewSession(sessionID);
					}
					break;
				case NEWNODE: // Add New Node (take into account SessionID)
					try{
						if(LoadCheck.loadCheckIsUsed){ // check if a new node can be added in the System.
							if(!LoadCheck.nodeAdded(sessionID)) {
								return;
							}
						}
						int x = new Integer(req.getParameter("p1")).intValue();
						int y = new Integer(req.getParameter("p2")).intValue();
						IRemoteGraphWrapper wrap = super.getCurrentWrapper(sessionID, false);
						if (wrap != null) {
							returnVal = super.getCurrentWrapper(sessionID, false).addNewNode(x, y);
							documentToSend = DocumentHelper.parseText(returnVal);
							//check if the new node is added  for the  LoadCheck class.
							if(LoadCheck.loadCheckIsUsed) {
								LoadCheck.checkNodesCreation(returnVal,sessionID);
							}
						} else {
							//return status2
						}
					}
					catch(NumberFormatException ex){
						logger.warn("invalide input data while adding node");
					}
					break;
				case DELNODE: // Delete a Node
					int toDelete = new Integer(req.getParameter("p1")).intValue();
					returnVal = super.getCurrentWrapper(sessionID, false).removeNode(toDelete);
					documentToSend = DocumentHelper.parseText(returnVal);


					//check if a  node is delated  for the  LoadCheck class.
					if(LoadCheck.loadCheckIsUsed) {
						LoadCheck.checkNodesRemoval(returnVal,sessionID);
					}

					break;
				case DELLINK: // Delete a Link between 2 Nodes
					/*toDelete = new Integer(req.getParameter("p1")).intValue();
		    			returnVal = super.getCurrentWrapper(sessionID, false).removeLink(toDelete);
		    			documentToSend = DocumentHelper.parseText(returnVal);*/
					returnVal = super.getCurrentWrapper(sessionID, false).removeLink(new Integer(req.getParameter("p1")).intValue(), new Integer(req.getParameter("p2")).intValue(), req.getParameter("p3"));
					documentToSend = DocumentHelper.parseText(returnVal);


					//check if a  node is delated  for the  LoadCheck class.
					if(LoadCheck.loadCheckIsUsed) {
						LoadCheck.checkLinksRemoval(returnVal,sessionID);
					}

					break;
				case NEWLINK: // Create a New Link
					try{
						if(LoadCheck.loadCheckIsUsed){ // check if a new node can be added in the System.
							if(!LoadCheck.linksAdded(sessionID)) {
								return;
							}
						}
						int startingNode = new Integer(req.getParameter("p1")).intValue();
						int incidentNode = new Integer(req.getParameter("p2")).intValue();
						returnVal = super.getCurrentWrapper(sessionID, false).addNewLink(startingNode, incidentNode);
						documentToSend = DocumentHelper.parseText(returnVal);

						//check if the new link is added  for the  LoadCheck class.
						if(LoadCheck.loadCheckIsUsed) {
							LoadCheck.checkLinksCreation(returnVal,sessionID);
						}
					}
					catch(NumberFormatException ex){
						logger.warn("invalide input data while creating Link");
					}
					break;
				case EXECGROOVY: // Execute Groovy Statement
					String statementParam = req.getParameter("statement");
					IRemoteGraphWrapper wrap = super.getCurrentWrapper(sessionID, false);
					String response = wrap.executeGroovy(statementParam);
					documentToSend = DocumentHelper.parseText(response);
					logger.debug(documentToSend.asXML());
					break;
				case GETFILE:
					if(super.hasWrapper(sessionID)) {
						documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).networkStats());
					} else {
						documentToSend = DocumentHelper.createDocument();
						Element root2 = documentToSend.addElement("root");
						root2.addElement("status").addText("2");
					}
					break;
				case ZOOM_IN:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).zoom(1));
					break;
				case ZOOM_OUT:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).zoom(-1));
					break;
				case SIZE_INCREASE:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).modifySize(4));
					break;
				case SIZE_DECREASE:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).modifySize(-4));
					break;
				case SCROLL_UP:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).scrollVertical(-20));
					break;
				case SCROLL_DOWN:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).scrollVertical(20));
					break;
				case SCROLL_LEFT:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).scrollHorizontal(-20));
					break;
				case SCROLL_RIGHT:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).scrollHorizontal(20));
					break;
				case SELECT:
					logger.debug("%%%%%%%Calling select");
					String key = req.getParameter("id");
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).selectElement(key));
					break;
				case UNSELECT:
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).unselectElement());
					break;
				case SET_ATTRIBUTE:
					logger.debug("&&&&&&&&& Calling set");
					String keyid = req.getParameter("id");
					String attName = req.getParameter("attName");
					String attValue = req.getParameter("attValue");
					documentToSend = DocumentHelper.parseText(super.getCurrentWrapper(sessionID, false).setAttribute(keyid, attName, attValue));
					break;
				case VIEW_NATIVE:
					logger.debug("Called view native");
					break;
				case VIEW_BEST_FIT:
					logger.debug("Called view best fit");
					break;
				case REM_ELEMENT:
					keyid = req.getParameter("id");
					logger.debug("Called removed element with key " + keyid);
					break;
				default:
					documentToSend = DocumentHelper.createDocument();
					Element root2 = documentToSend.addElement("root");
					root2.addElement("status").addText("2");
					logger.debug(documentToSend.asXML());
					break;
				}
			}
		} catch (Exception e) {
			documentToSend = DocumentHelper.createDocument();
			Element root2 = documentToSend.addElement("root");
			root2.addElement("status").addText("2");
			root2.addElement("errorMsg").addText(e.getMessage());
		} finally { // Send the response back to the browser
			try {
				if(ActionToPerform.toPerform(req.getParameter("DO")) == ActionToPerform.GETFILE) {
					resp.set("Content-Type", "application/x-download");
					resp.set("Content-Disposition", "attachment; filename=Network_" + getSessionID(req) + ".xml");
				} else {
					resp.set("Content-Type", "text/xml"); // Important to set XML Content-Type !!
				}

				OutputStream out = resp.getOutputStream();
				XMLWriter writer = new XMLWriter(out, new OutputFormat("", false));
				if(documentToSend != null){
					logger.trace("Document sent : " + documentToSend.asXML()) ;
					writer.write(documentToSend);
				}
				writer.close();
				out.close();
			} catch (Exception e1) {
				logger.error("Exception Caught: ", e1);
			}
		}
		super.endProcess(req, resp);
	}
}
