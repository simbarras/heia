package ch.epfl.javanco.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

import org.dom4j.Element;

/**
 * @author Christophe Trefois
 *
 */
public interface IRemoteGraphWrapper extends Remote {

	/**
	 * Create a new Network with no nodes and basic settings
	 * @param graphName the name of the Graph
	 * @return the name of the created graph
	 * @throws RemoteException
	 */
	public String createNetwork(String graphName) throws RemoteException;

	public String getXML() throws RemoteException;

	/**
	 * Add a new Node to the current AbstractGraphHandler
	 * @param x the X value of the Node
	 * @param y the Y value of the Node
	 * @return an XML structure including the coordinate x, y and the status (1: Success, 2: Error)
	 * @throws RemoteException
	 */
	public String addNewNode(int x, int y) throws RemoteException;

	/**
	 * Retrieve the handled graph name
	 * @return the graph name
	 * @throws RemoteException
	 */
	public String getGraphName() throws RemoteException;
	public String callTool(String name, Hashtable<String,String> arguments) throws RemoteException;


	/**
	 * Adds a new Link to the Networks
	 * @param orig The Origin Node
	 * @param dest The Destination Node
	 * @return an XML structure including the origin, the destination and the status (1: Success, 2: Error)
	 * @throws RemoteException
	 */
	public String addNewLink(int orig, int dest) throws RemoteException;

	/**
	 * Opens a Network given a valid XML structure
	 * @param fileToOpen The contents of the whole "XML" should be in this StringBuilder
	 * @return The status of the action (1: Success, 2: Error)
	 * @throws RemoteException
	 */
	public String openNetwork(String fileToOpen) throws RemoteException;

	public String openNetworkFromFile(String file) throws RemoteException;

	/**
	 * Executes any Groovy statements provided as argument on the current handler
	 * @param statementToExec The Groovy statements to execute
	 * @return The status of the action (1: Success, 2: Error)
	 * @throws RemoteException
	 */
	public String executeGroovy(String statementToExec) throws RemoteException;

	/**
	 * Retrieve the whole network details using the XML structure from Javanco
	 * @return the XML Structure
	 * @throws RemoteException
	 */
	public String networkStats() throws RemoteException;

	public Element getNetworkDocument() throws RemoteException;

	/**
	 * Removes a Link from the current graph. <br/>
	 * 
	 * @param orig The origin of the Link
	 * @param dest The Destination of the Link
	 * @return The status of the action (1: Success, 2: Error)
	 * @throws RemoteException
	 */
	public String removeLink(int orig, int dest, String layer) throws RemoteException;

	/**
	 * Removes a Node from the list using the provided index
	 * @param nodeNum The Index of the node to remove
	 * @return The status of the action (1: Success, 2: Error)
	 * @throws RemoteException
	 */
	public String removeNode(int nodeNum) throws RemoteException;

	public byte[] getGraphImage(int width, int height, String format) throws RemoteException;

	public void addAreaAttribute(String s) throws RemoteException;

	public void resetAreaAttributes() throws RemoteException;

	public String getGraphImageHTMLMap(int width, int height) throws RemoteException;

	public String scrollHorizontal(int nativePixels) throws RemoteException;

	public String scrollVertical(int nativePixels) throws RemoteException;

	public String zoom(int dir) throws RemoteException;

	public String modifySize(int incr) throws RemoteException;

	public String selectElement(String key) throws RemoteException;

	public String unselectElement() throws RemoteException;

	public String setAttribute(String keyid, String attName, String attValue) throws RemoteException;

	public String rmiToString() throws RemoteException;
}
