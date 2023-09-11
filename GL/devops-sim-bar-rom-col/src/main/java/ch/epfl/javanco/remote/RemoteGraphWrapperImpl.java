package ch.epfl.javanco.remote;


import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.ElementCoord;
import ch.epfl.javanco.graphics.LinkPolygon;
import ch.epfl.javanco.graphics.NodeCoord;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.scripting.ScriptExecutionException;
import ch.epfl.javanco.ui.web.WebUI;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * @author Christophe Trefois
 *
 */
public class RemoteGraphWrapperImpl extends RMIServerBaseObject implements Remote, IRemoteGraphWrapper {

	private static final Logger logger = new Logger(RemoteGraphWrapperImpl.class);
	/**
	 * 
	 */
	private AbstractGraphHandler handler = null;
	private WebUI ui = null;

	protected Logger getLogger() {
		return logger;
	}



	/*
	 * =========================================
	 *===========================================
	 * TO MOVE TO ACTION SERVICE.
	 */
	private static String xmlEncode(String s) {
		s = s.replace("<","&lt;");
		s = s.replace(">","&gt;");
		return s;
	}

	/**
	 * Creates an instance of RemoteGraphWrapper.
	 * @param abstractHandler
	 */
	public RemoteGraphWrapperImpl(AbstractGraphHandler agh) throws RemoteException {
		this.handler = agh;
		this.handler.activateMainDataHandler();
		this.ui = new WebUI(agh, null);
	}

	public String getGraphName() throws RemoteException {
		return this.handler.getHandledGraphName();
	}

	public String createNetwork(String graphName) throws RemoteException {
		String returnValue = "";
		this.handler.newNetwork(graphName);
		returnValue = this.handler.getHandledGraphName();
		logger.debug("Network \"" + returnValue + "\" created");
		return returnValue;
	}

	public String openNetworkLocally(JavancoFile fileToOpen) throws RemoteException {
		try {
			this.handler.openNetworkFile(fileToOpen);
			logger.debug("Network \"" + handler.getHandledGraphName() + "\" opened (from local repository)");
		} catch (Exception e) {
			logAndThrowException("Impossible to open network: ", e);
			return "<root><status>2</status><errorMsg>" + e.getMessage() + "</errorMsg></root>";
		} catch (Error ex) {

			ex.printStackTrace();
		}
		return "<root><status>1</status></root>";
	}

	public String openNetwork(String fileContent) throws RemoteException {
		try {
			this.handler.openNetworkFromString(fileContent.toString());
			logger.debug("Network \"" + handler.getHandledGraphName() + "\" opened (remotely)");
		} catch (Exception e) {
			logAndThrowException("Impossible to open network: ", e);
			return "<root><status>2</status><errorMsg>" + e.getMessage() + "</errorMsg></root>";
		}
		return "<root><status>1</status></root>";
	}

	public String openNetworkFromFile(String fileToOpen) throws RemoteException {
		try {
			this.handler.openNetworkFile(fileToOpen);
			logger.debug("Network \"" + handler.getHandledGraphName() + "\" opened (remotely)");
		} catch (Exception e) {
			logAndThrowException("Impossible to open network: ", e);
			return "<root><status>2</status><errorMsg>" + e.getMessage() + "</errorMsg></root>";
		}
		return "<root><status>1</status></root>";
	}

	public String getXML() throws RemoteException  {
		try {
			return this.handler.getXML().asXML();
		}
		catch (Exception e) {}
		return null;
	}

	public String addNewNode(int x, int y) throws RemoteException {
		Point nodeCoord = null;
		try {
			NodeContainer nodecont = this.handler.newNode(x, y);
			nodeCoord = nodecont.getCoordinate();
			logger.debug("Node id=" + nodecont.getIndex() + " [" + x + "," + y +"] created");
		} catch (Exception e) {
			logAndThrowException("Node instantiation problem: ", e);
			return "<root><status>2</status><errorMsg>" + e.getMessage() + "</errorMsg></root>";
		}
		return "<root><point><x>" + nodeCoord.x + "</x><y>" + nodeCoord.y + "</y></point><status>1</status></root>";
	}

	public String addNewLink(int orig, int dest) throws RemoteException {
		try {
			this.handler.newLink(orig, dest);
			logger.debug("Link orig=" + orig + " dest=" + dest + " created");
		} catch (Exception e) {
			logAndThrowException("Problem: ", e);
			return "<root><status>2</status><errorMsg>" + xmlEncode(e.getMessage()) + "</errorMsg></root>";
		}
		return "<root><link><origin>" + orig + "</origin><destination>" + dest + "</destination></link><status>1</status></root>";
	}

	public String executeGroovy(String statementToExec) throws RemoteException {
		try {
			this.handler.getGroovyScriptManager().executeStatement(statementToExec);
			logger.debug("Groovy Script Executed");
		}
		catch (ScriptExecutionException e) {
			if ((e.getCause() instanceof java.security.AccessControlException) ||
					(e.getCause() instanceof java.lang.SecurityException)) {
				logger.info("Security exception while executing script");
			} else {
				logAndThrowException("Problem while executing groovy statement", e);
			}
			return "<root><status>2</status><errorMsg>" + xmlEncode(e.getMessage()) + "</errorMsg></root>";
		}
		return "<root><status>1</status></root>";
	}

	public String networkStats() throws RemoteException {
		logger.debug("Sending XML structure to Web Server");
		return handler.getXML().getBackedElement().asXML();
	}

	public Element getNetworkDocument() throws RemoteException {
		logger.debug("Sending XML structure to Web Server");
		Element el = handler.getXML().getBackedElement();
		return el;
	}

	public String removeLink(int orig, int dest, String layer) throws RemoteException {
		try {
			LinkContainer linkCont = this.handler.getLinkContainer(orig, dest, layer);
			this.handler.removeElement(linkCont);
			logger.debug("Link " + linkCont.getStartNodeIndex() + "-" +
					linkCont.getEndNodeIndex() + " on layer " +
					linkCont.getContainingLayerContainer().getName() + "has been removed");
		} catch (Exception e) {
			logAndThrowException("Problem removing link " + orig + "-" + dest +
					" on layer " + layer, e);
			return "<root><status>2</status><errorMsg>" + e.getMessage() + "</errorMsg></root>";
		}
		return "<root><status>1</status></root>";
	}

	public String removeNode(int nodeNum) throws RemoteException {
		try {
			NodeContainer cont = this.handler.getNodeContainer(nodeNum);
			handler.removeElement(cont);
			logger.debug("Node id=" + cont.getIndex() + " has been removed");
		} catch (Exception e) {
			logAndThrowException("Problem removing node id=" + nodeNum, e);
			return "<root><status>2</status><errorMsg>" + e.getMessage() + "</errorMsg></root>";
		}
		return "<root><status>1</status></root>";
	}

	public String callTool(String name, Hashtable<String,String> arguments) throws RemoteException {
		String[] vals = name.split(":");
		String toolName = vals[0];
		boolean local = false;
		if (vals.length > 1) {
			local = Boolean.parseBoolean(vals[1]);
		}
		Object result = null;
		try {
			result = handler.callLocalMNDService(toolName,local, arguments);
		}
		catch (MNDServiceException e) {
			logAndThrowException("Problem calling tool=" + toolName, e);
		}
		if (result != null) {
			return result.toString();
		} else {
			return "";
		}
	}

	public byte[] getGraphImage(int width, int height, String format) throws RemoteException {
		if (format == null) {
			format = "png";
		}
		try {
			ui.setInfoSetView_(0,-height,width,height);
			java.io.ByteArrayOutputStream array = new java.io.ByteArrayOutputStream();
			String tempPath = System.getProperty("JAVANCO_HOME") + "/output/temp";
			javax.imageio.ImageIO.setCacheDirectory(new java.io.File(tempPath));
			javax.imageio.ImageIO.write(ui.getActualViewImage(), format, array);
			return array.toByteArray();
		}
		catch (java.io.IOException e) {
			logAndThrowException("Problem retrieving graph image", e);
			throw new RemoteException("Problem retrieving graph image", e);
		}

	}

	private List<String> areaAttributesList = new ArrayList<String>();

	public void addAreaAttribute(String s) throws RemoteException {
		areaAttributesList.add(s);
	}

	public void resetAreaAttributes() throws RemoteException {
		areaAttributesList.clear();
	}

	public String getGraphImageHTMLMap(int width, int height) throws RemoteException {
		ui.init();
		if (ui.infoSetViewHasBeenSet() == false) {
			ui.setInfoSetView_(0,-height,width,height);
		}
		Hashtable<AbstractElementContainer, ElementCoord>
		displayedElements = ui.getDisplayedElementCoord(true);
		StringBuilder sb = new StringBuilder();
		sb.append("<map name='full'>\r\n");
		for (Map.Entry<AbstractElementContainer, ElementCoord> entry : displayedElements.entrySet()) {
			ElementCoord coord = entry.getValue();
			AbstractElementContainer cont = entry.getKey();
			coord.reverseYCoord(height);
			sb.append("<area ");
			if (coord instanceof NodeCoord) {
				sb.append("shape='circle' ");
			}
			if (coord instanceof LinkPolygon) {
				sb.append("shape='poly' ");
			}
			sb.append("coords='");
			int[] coords = coord.getCoords();
			for (int i = 0; i < coords.length ; i++) {
				sb.append(coords[i]);
				if (i + 1 < coords.length) {
					sb.append(",");
				}
			}
			sb.append("' ");
			for (String s :areaAttributesList) {
				Matcher matcher = pattern.matcher(s);
				boolean append = true;
				while(matcher.matches()) {
					if (cont.attribute(matcher.group(2), false) != null) {
						s = s.replace(matcher.group(1), cont.attribute(matcher.group(2), false).getValue());
						matcher = pattern.matcher(s);
					} else {
						append = false;
						break;
					}
				}
				if (append) {
					sb.append(s);
				}
				sb.append(" ");
			}
			sb.append(" />\r\n");
		}
		sb.append("</map>");
		return sb.toString();
	}

	public String scrollHorizontal(int nativePixels) throws RemoteException {
		ui.moveInfoSetViewHorizontal(nativePixels);
		return "<root><status>1</status></root>";
	}

	public String scrollVertical(int nativePixels) throws RemoteException {
		ui.moveInfoSetViewVertical(nativePixels);
		return "<root><status>1</status></root>";
	}

	public String zoom(int dir) throws RemoteException {
		ui.zoom(dir);
		return "<root><status>1</status></root>";
	}

	public String modifySize(int incr) throws RemoteException {
		ui.modifyDisplayedElementSize(incr);
		return "<root><status>1</status></root>";
	}

	public String selectElement(String key) throws RemoteException {
		StringBuilder sb = null;
		String end = null;
		AbstractElementContainer cont = retrieveElement(key);
		if (cont != null) {
			sb = new StringBuilder();
			if (cont instanceof NodeContainer) {
				NodeContainer nc = (NodeContainer)cont;
				sb.append("<element><node id=\"" + nc.getIndex() + "\">");
				end = "</node></element>";
			} else if (cont instanceof LinkContainer) {
				LinkContainer lc = (LinkContainer)cont;
				sb.append("<element><link orig=\"" + lc.getStartNodeIndex() + "\" dest=\"" + lc.getEndNodeIndex());
				sb.append("\" on_layer=\"" + lc.getContainingLayerContainer().getName() + "\">");
				end = "</link></element>";
			} else {
				sb.append("<element><layer>");
				end = "</layer></element>";
			}
		} else {
			return "<root><status>2</status><errorMsg>No element has key=\"" + key + "\"</errorMsg></root>";
		}
		for (NetworkAttribute att : cont.attributes()) {
			sb.append("<attribute name=\"" + att.getName() + "\" value=\"" + att.getValue() + "\"/>");
		}
		sb.append(end);
		return sb.toString();
	}

	public String unselectElement() throws RemoteException{
		ui.modifyDisplayedElementSize(0);
		return "<root><status>1</status></root>";
	}

	public String setAttribute(String keyid, String attName, String attValue) throws RemoteException{
		AbstractElementContainer cont = retrieveElement(keyid);
		if (cont != null) {
			cont.attribute(attName, true).setValue(attValue);
			cont.linkAttribute(attName, XMLTagKeywords.MAIN_DESCRIPTION.toString());
		}
		return selectElement(keyid);
	}


	public String rmiToString() throws RemoteException {
		return "Rmi stub for " + handler.toString();
	}


	private AbstractElementContainer retrieveElement(String key) {
		AbstractElementContainer cont = null;
		if (key.startsWith("N")) {
			Matcher matcher = node_pattern.matcher(key);
			matcher.matches();
			cont = handler.getNodeContainer(Integer.parseInt(matcher.group(1)));
		} else if (key.startsWith("L")) {
			Matcher matcher = link_pattern.matcher(key);
			matcher.matches();
			cont = handler.getLinkContainer(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), matcher.group(3));
		}
		return cont;


	}

	static Pattern pattern = null;
	static Pattern node_pattern = null;
	static Pattern link_pattern = null;

	static {
		pattern = Pattern.compile("[^%]*(%([^%]*)%).*?");
		node_pattern = Pattern.compile("N_([0-9]*)");
		link_pattern = Pattern.compile("L_([0-9]*),([0-9]*)_(.*)");

	}



}
