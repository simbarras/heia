package ch.epfl.javancox.experiments.builder.web_gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.javancox.experiments.builder.tree_model.*;
import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Content;
import simple.http.serve.Context;
import ch.epfl.JavancoRemote;
import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.webserver.AbstractService;
import ch.epfl.javancox.experiments.builder.object_enum.AbstractEnumerator;
import ch.epfl.javancox.experiments.builder.object_enum.ExperimentExecutionManager;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode.ActionItem;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode.ActionStructure;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractChooseNode.SeparatorItem;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel.ObjectIterator;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel.TreeModelUIManager;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.web.ResultVisualisationService;

public class ConfigurationCockpitService extends AbstractService {
	
	private transient static ClassRepository globalLister;	
	private static final Logger logger = new Logger(ConfigurationCockpitService.class);
	private HashMap<String, SessionObject> sessionsTreesMap = new HashMap<String, SessionObject>();
	
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			JavancoRemote.startServer("web", Integer.parseInt(args[0]), "web/cockpit_online", "-");
		} else {
			JavancoRemote.startServer("web", 8080, "web/cockpit_online", "-");
		}
	}
	
	private static class SessionObject implements TreeModelUIManager {
		
	//	private String sessionKey;
		ObjectConstuctionTreeModel treeModel;
		HashMap<Integer, AbstractChooseNode> sessionNodes = new HashMap<Integer, AbstractChooseNode>();
	//	long lastAccessed;
		// Timer kill;
		
		public SessionObject(String sessionKey) throws Exception {
			super();
		//	this.sessionKey = sessionKey;
			treeModel = new ObjectConstuctionTreeModel<Experiment>(Experiment.class, getClassLister());
			treeModel.setTreeModelUIManager(this);
		}
		
	//	public void expandPath(TreePath treePath) {
		//	this_.expandPath(treePath);
	//	}

	/*	@Override
		public boolean isExpanded(TreePath treePath) {
			return true;
		}*/

		@Override
		public void showErrorMessage(String string) {
			//JOptionPane.showMessageDialog( null, string, "Error", JOptionPane.ERROR_MESSAGE);
		}

		@Override
		public void removeNode(AbstractChooseNode node) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void refresh() {
			// TODO Auto-generated method stub
			
		}		
	}

	public ConfigurationCockpitService(Context context) {
		super(context);
	}
	
	private static ClassRepository getClassLister() {
		logger.debug("Creating lister");
		if (globalLister == null) {
			ArrayList<String> list = new ArrayList<String>();
			list.add("ch");
			list.add("umontreal.iro");
			list.add("edu.columbia");
			globalLister = new ClassRepository(list);
		}
		return globalLister;
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
			logger.debug("parsing request");
			if (req.getParameters().size() > 0) {
				logger.debug("Returning user with parameters");
				if (req.getMethod().equals("POST")) {
					if (req.getParameter("action").equals("RUN")) {
						logger.debug("RUN command");
						redirectToGui(session, ow);
						return;
					} else if (req.getParameter("action").equals("CLEAR")) {
						logger.debug("CLEAR command");
						SmartDataPointCollector col = ResultVisualisationService.registeredDBs.get(session);
						col.clear();
					} else {
						logger.debug("Value addition");
						processAdd(req.getParameter("code"), req.getParameter("value"), session);
					}
				} else {
					logger.debug("Action command");
					processAction(req.getParameter("action"), req.getParameter("code"), session);
				}
			}
			Content cont = context.getContent("/menu.html");
			String st = cont.toString()
					.replace("#GOING_TO_BE_REPLACED#", getLinesAndMenu(getSessionObject(session)).toString())
					.replace("#COMMIT_CODE#", System.getProperty("ch.epfl.JavancoRemote.state"));
			ow.write(st);
			ow.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unhandled Problem", e);
		} finally { // Send the response back to the browser
			try { 
				out.close();	
			} catch (Exception e) {
			}
		}

		super.endProcess(req, resp);
	}

	private void redirectToGui(final String session, final OutputStreamWriter ow) {
		ExperimentExecutionManager enumerator = new ExperimentExecutionManager() {
			@Override
			public void afterIteration() {
				if (success) {
					ResultVisualisationService.registerDB(db, session);
					logger.info(this.i-1 + " experiments runned in : " + (System.currentTimeMillis() - this.start) + " ms");
					try {
						Content cont = context.getContent("/redirect.html");
						ow.write(cont.toString());
						ow.flush();						
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}

				}
			}			
		};

		
		@SuppressWarnings("unchecked")
		ObjectIterator<Experiment> ite = ((ObjectConstuctionTreeModel<Experiment>)getSessionObject(session).treeModel).getObjectIterator();
		
		enumerator.runInSameThread(null, ite, null);
		
	}

	private StringBuilder getLinesAndMenu(SessionObject sessionObject) {
		logger.debug("Getting root");
		AbstractChooseNode root = (AbstractChooseNode) sessionObject.treeModel.getRoot();
		StringBuilder sb = new StringBuilder();
		appendLine(root, sb, sessionObject);
		sb.append("<ul  class='nav-list' style='line-height: 8px;'>");		
		getLinesAndMenuRecursive(sessionObject, root, sb);
		sb.append("</ul>");
		if (root.isConfigured()) {
		    sb.append("<h1>2. Run your configurations...</h1>");
			sb.append("<form method='POST' action='cockpit'><input type='hidden' name='action' value='RUN'/><button type='Run' class='btn'>Run the " + root.getInstancesCount() + " instance(s)</button></form>");
			sb.append("<form method='POST' action='cockpit'><input type='hidden' name='action' value='CLEAR'/><button type='Run' class='btn'>Clear the database</button></form>");

		}
		return sb;
	}
	
	private void getLinesAndMenuRecursive(SessionObject sessionObject, AbstractChooseNode node, StringBuilder sb) {
		for (int i = 0 ; i < node.getChildCount() ; i++) {
			AbstractChooseNode child = (AbstractChooseNode) node.getChildAt(i);
			appendLine(child, sb, sessionObject);
			sb.append("<ul class='nav-list' style='line-height: 14px;'>");
			getLinesAndMenuRecursive(sessionObject, child, sb);
			sb.append("</ul>");
		}
		
	}
	
	private void appendLine(AbstractChooseNode node, StringBuilder sb, SessionObject session) {

		sb.append("<li style='line-height: 8px;'>");
		sb.append("<form class='form-inline' style='margin: 0;' action='cockpit' method='POST'>");	
		sb.append("<div class='btn-group' style='line-height: 14px;'><a class='btn dropdown-toggle' data-toggle='dropdown' href='#' style='line-height: 14px;'>");
		sb.append("<span style='color:" + node.getColor() + "'>");
		sb.append(node.getText());
		sb.append("</span>");
		sb.append("<span class='caret'></span></a>");
		sb.append("<ul class=\"dropdown-menu\">\r\n");
		appendRecursive(node, sb, node.getActions());		
		sb.append("</ul></div>");
		if (node instanceof AbstractParameterChooseNode) {
			AbstractParameterChooseNode cNode = (AbstractParameterChooseNode)node;
			if (cNode.isTypableType()) {
				sb.append("<input type='hidden' name='code' value='" + node.hashCode() + "'/>");
				sb.append("<input type='hidden' name='action' value='null'/>");				
				sb.append("<input type='text' name='value' class='input-small' placeholder='Enter value' style='line-height: 14px; height: 14px;'>");
				sb.append("<button type='Add' class='btn' style='line-height: 14px;'>Add</button>");
			} else if (cNode.isBooleanType()) {
				sb.append("<input type='hidden' name='code' value='" + node.hashCode() + "'/>");
				sb.append("<input type='hidden' name='action' value='null'/>");					
				sb.append("<input type='checkbox' name='value'/>");
				sb.append("<button type='Add' class='btn' style='line-height: 14px;'>Add</button>");				
			}
		}
		sb.append("</form></li>");
		

		// TODO check for conflicts
		session.sessionNodes.put(node.hashCode(), node);
	}
		
	private void appendRecursive(AbstractChooseNode node, StringBuilder sb, List<ActionItem> items) {
		for (ActionItem item : items) {
			if (item instanceof SeparatorItem) {
				sb.append("<li class=\"divider\"></li>\r\n");
			} else if (item instanceof ActionStructure) {
				sb.append("<li class=\"dropdown-submenu\">\r\n");
				sb.append("<a tabindex=\"-1\" ");				
				if (item.actionName != null) {
					sb.append("href=\"cockpit?action=" + getActionTag(item, node) +"\">" + item.text + "</a>\r\n");
				} else {
					sb.append("href=\"#\">" + item.text + "</a>\r\n");
				}
				sb.append("<ul class=\"dropdown-menu\">\r\n");
				
				appendRecursive(node, sb, ((ActionStructure) item).childs);
				
				sb.append("</ul>");
				
				sb.append("</li>\r\n");
			} else {
				sb.append("<li><a href=\"cockpit?action=" + getActionTag(item, node) +"\">" + item.text + "</a></li>\r\n");
			}
		}
	}
	
	private String getActionTag (ActionItem item, AbstractChooseNode node) {
		return item.actionName + "&code=" + node.hashCode();
	}
	
	private void processAction(String actionKey, String actionCode, String session) throws NumberFormatException, Exception {
		AbstractChooseNode node = getSessionObject(session).sessionNodes.get(Integer.parseInt(actionCode));
		node.actionPerformed(actionKey);
	}
	
	private void processAdd(String code, String value, String session) throws NumberFormatException, Exception {
		AbstractChooseNode node = getSessionObject(session).sessionNodes.get(Integer.parseInt(code));
		if (node instanceof TypableChooseNode) {
			((TypableChooseNode)node).setTextValue(value);
		}
		if (node instanceof BooleanChooseNode) {
			if (value != null && value.equals("on")) {
				((BooleanChooseNode)node).setValue(true);
			} else {
				((BooleanChooseNode)node).setValue(false);
			}	
		}
		node.actionPerformed(TypableChooseNode.ADD);
		
	}	

	private SessionObject getSessionObject(String session) /*throws Exception*/ {
		try {
			SessionObject obj = sessionsTreesMap.get(session);
			if (obj == null) {
				 obj = new SessionObject(session);
				 sessionsTreesMap.put(session, obj);
			}
			return obj;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
