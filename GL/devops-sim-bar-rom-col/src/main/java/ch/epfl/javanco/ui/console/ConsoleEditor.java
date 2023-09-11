package ch.epfl.javanco.ui.console;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.GraphHandlerFactory;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.graphics.ElementCoord;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.ui.FrameBasedInterface;


/**
 * @author Christophe Trefois
 *
 */
public class ConsoleEditor implements FrameBasedInterface {

	/**
	 * Instance of the GraphHandlerFactory
	 */
	GraphHandlerFactory graphHandlerFactory = null;
	/**
	 * Scanner to get input from user over command line
	 */
	//Scanner scanner = null;
	java.io.BufferedReader scanner = null;
	private String currentXMLDir = null;
	private String currentScriptDir = null;

	// TODO: Only 1 Graph possible to load atm
	AbstractGraphHandler handler = null;
	ConsoleUI ui = null;

	ConsoleFrame fr = null;

	private java.io.PrintStream out = null;
	private java.io.InputStream in = null;

	/**
	 * Main UI for Console Mode
	 * @param fac the main GraphHandlerFactory instance
	 */
	public ConsoleEditor(GraphHandlerFactory fac) throws java.io.IOException {
		this.graphHandlerFactory = fac;
		//		fac.setUIManagerClass(ConsoleUIManager.class);

		fr = new ConsoleFrame(120);
		in = fr.in;
		out = fr.out;

		// scanner = new Scanner(in);
		java.io.InputStreamReader reader = new java.io.InputStreamReader(in);
		scanner = new java.io.BufferedReader(reader);
		initialiseDefaultDirectories();

		out.println("Welcome to Javanco - Console Based");
		out.println("Please make your selection below !");
		while (createMenu()) {
		}
	}

	public ConsoleFrame getMainFrame() {
		return fr;
	}

	public AbstractGraphHandler getCurrentlyActiveAgh() {
		return handler;
	}

	/**
	 * Initialize the directories
	 *
	 */
	private void initialiseDefaultDirectories() {
		currentXMLDir = Javanco.getProperty("ch.epfl.javanco.xmlFilesDir");
		if (currentXMLDir == null) {
			currentXMLDir = Javanco.getProperty("user.dir");
		}
		currentScriptDir  = Javanco.getProperty("ch.epfl.javanco.groovyScriptsDir");
		if (currentScriptDir == null) {
			currentScriptDir = Javanco.getProperty("user.dir");
		}
		out.println(currentXMLDir + " " + currentScriptDir);
	}

	/**
	 * Create the Shell Menu
	 */
	public boolean createMenu() {
		out.println("----------------------------------");
		out.println("1. Create New Default Network");
		out.println("2. Open Existing .xml Network Description File");
		out.println("----------------------------------");
		out.println("0. Exit Program");

		switch(getNextIntFromCommandLine()) {
		case 1:
			createNewNetwork();
			break;
		case 2:
			openNetwork();
			break;
		case 0:
			System.exit(0);
			break;
		default:
			out.println("Wrong Choice - Please try again");
			return true;
		}
		return false;
	}

	/**
	 * Attemps to create a new default network. User can input Graph Name only for now.
	 *
	 */
	public void createNewNetwork() {
		out.println("Enter the Graph Name please: ");
		try {
			String graphName = scanner.readLine();
			getNewAbstractGraphHandler();
			this.handler.newNetwork(graphName);
			out.println("Graph Name: " + graphName);
			out.println("Graph Created successfully !");
		}
		catch(Exception e) {
			e.printStackTrace();
			out.println("Impossible to create new layer with the given properties.");
		}
		individualNetworkMenu();
	}

	/**
	 * Opens a network. The user choses from a list of files.
	 *
	 */
	public void openNetwork() {
		out.println("Choose File to Open from : ");
		
		String xmlDir = Javanco.getProperty(Javanco.JAVANCO_DEFAULT_XMLGRAPH_DIR_PROPERTY);

		out.println(xmlDir + "\n");
		JavancoFile dir = new JavancoFile(xmlDir);

		JavancoFile[] fileList = dir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			out.println(i + " -> " + fileList[i].getName());
		}
		int indexToOpen = getNextIntFromCommandLine();

		getNewAbstractGraphHandler();

		this.handler.openNetworkFile(fileList[indexToOpen].getName());
		individualNetworkMenu();
	}

	private void getNewAbstractGraphHandler() {
		handler = graphHandlerFactory.getNewGraphHandler();
		ui = new ConsoleUI(handler, this);
	}

	/**
	 * Show menu for a particular Network
	 *
	 */
	public void individualNetworkMenu() {
		boolean isRunning = true;
		while(isRunning) {
			out.println("--------------------------------------");
			out.println("   NETWORK MENU - possible actions :  ");
			out.println("--------------------------------------");
			out.println("1. Add New Node");
			out.println("2. View Current Number of Nodes");
			out.println("3. Remove a Node");
			out.println("4. Create Link");
			out.println("5. Save Image of Graph");
			out.println("6. Show Links");
			out.println("7. Remove a link");
			out.println("8. Save Network");
			out.println("9. Zooming in");
			out.println("10. Zooming out");
			out.println("11. Set viewport to best fit");
			out.println("12. Set viewport to native");
			out.println("0. Back to Main Menu");
			out.println("--------------------------------------");
			switch(getNextIntFromCommandLine()) {
			// Add a new node
			case 1:
				addNewNode();
				break;
				// View current number of nodes
			case 2:
				out.println("Number of current Nodes: " + this.handler.getNodeContainers().size());
				displayNetworkNodes();
				break;
				// Remove a node
			case 3:
				displayNetworkNodes();
				deleteNode();
				displayNetworkNodes();
				break;
				// Add New Link
			case 4:
				displayNetworkNodes();
				addNewLink();
				break;
				// Save Image
			case 5:
				saveImageToFile();
				break;
				// Display all links
			case 6:
				displayNetworkLinks();
				break;
				// Remove a link
			case 7:
				ArrayList<LinkContainer>networkList = getLinks();

				displayNetworkLinks();

				out.println("Choose Link to delete");
				int linkToDelete = getNextIntFromCommandLine();
				AbstractElementContainer cont = networkList.get(linkToDelete);

				this.handler.removeElement(cont);
				break;
				// Save the network to a file
			case 8:
				out.println("Saving Current Network to : " + Javanco.getProperty(Javanco.JAVANCO_DEFAULT_XMLGRAPH_DIR_PROPERTY));
				try {
					this.handler.saveNetwork("myTest.xml");
					out.println("Saving Completed !");
				}
				catch (IOException e) {
					out.println("Saving NOT complete : " + e);
				}
				break;
				// Exit
			case 9:
				out.println("Zooming in");
				zoom(1);
				break;
			case 10:
				out.println("Zooming out");
				zoom(-1);
				break;
			case 11:
				setBestFit();
				break;
			case 12:
				setNative();
				break;
			case 0:
				createMenu();
			default:
				out.println("Wrong Choice");
				individualNetworkMenu();
				break;
			}
		}
	}

	/**
	 * Display all network Links
	 *
	 */
	public void displayNetworkLinks() {
		ArrayList<LinkContainer> networkList = getLinks();
		int i = 0;
		for (LinkContainer element : networkList) {
			out.println(i + " -> Link from Node : " + element.getStartNodeIndex() + " to Node: " + element.getEndNodeIndex());
			i++;
		}
	}

	public void zoom(int direction) {
		ui.zoom(direction);
	}

	/**
	 * Saves Image as PNG file
	 * TODO - Fix it so it works
	 */
	public void saveImageToFile() {

		/*	out.println("Enter image width");
		int width = 	getNextIntFromCommandLine();
		out.println("Enter image height");
		int height = getNextIntFromCommandLine();*/

		//	uiManager.setDisplaySize(width, height);

	/*	String[] tab = new String[]{GraphDisplayInformationSet.DISPLAY_BEST_FIT,
				GraphDisplayInformationSet.DISPLAY_NATIVE,
				GraphDisplayInformationSet.DISPLAY_STRECHED,
				GraphDisplayInformationSet.DISPLAY_TRUNKED};*/

	//	for (String s : tab) {
			BufferedImage image =		ui.getActualViewImage();
			BufferedImage imageTri =    ui.getActualViewImage(200, 200);

			String dirPath = Javanco.getProperty(Javanco.JAVANCO_DEFAULT_OUTPUTDIR_PROPERTY) + "/Trefex" + "/";
			JavancoFile imageFile = new JavancoFile(dirPath + "total__" + ".png");
			JavancoFile imageFileTri = new JavancoFile(dirPath + "view__" + ".png");

			imageFile.mkdirs();
			try {
				ImageIO.write(image, "png", imageFile);
				ImageIO.write(imageTri, "png", imageFileTri);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Hashtable<AbstractElementContainer, ElementCoord> displayedElements = ui.getDisplayedElementCoord(true);
			System.out.println("=================================");
			for (Map.Entry<AbstractElementContainer,ElementCoord> entry : displayedElements.entrySet()) {
				System.out.println("--  " + entry.getKey().toShortString() + " -- " + entry.getValue());
			}

	//	}
	}

	/*	private GraphicalUIManager getUIManager() {
		return (GraphicalUIManager)handler.getUIManager();
	}*/

	private void setNative() {
		//		getUIManager().setViewNative();
	}

	private void setBestFit() {
		//	getUIManager().setViewBestFit();
	}

	/**
	 * Add a new link to the network. User is asked for Origin and Destination Index
	 *
	 */
	public void addNewLink() {
		out.println("Enter Origin Index: ");
		int orig = getNextIntFromCommandLine();
		out.println("Enter Destination Index: ");
		int dest = getNextIntFromCommandLine();
		this.handler.newLink(orig, dest);
		//	getUIManager().newLink(orig, dest);
	}

	/**
	 * Delete a node from the network.
	 *
	 */
	public void deleteNode() {
		out.println("Choose Element to Delete: ");
		int intFromCmdLine = getNextIntFromCommandLine();
		try {
			AbstractElementContainer cont = this.handler.getNodeContainer(intFromCmdLine);
			handler.removeElement(cont);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a new node to the network. User is asked for X and Y coordinates
	 *
	 */
	public void addNewNode() {
		out.println("Enter X: ");
		int x = getNextIntFromCommandLine();
		out.println("Enter Y: ");
		int y = getNextIntFromCommandLine();
		NodeContainer nodecont = this.handler.newNode(x, y);
		Point nodeCoord = nodecont.getCoordinate();
		out.println("Node Created - X: " + nodeCoord.x + "\tY: " + nodeCoord.y);
	}

	/**
	 * Display All Nodes in Text Format
	 *
	 */
	private void displayNetworkNodes() {
		int i = 0;
		for (NodeContainer node : handler.getNodeContainers()) {
			Point nodeCoord = node.getCoordinate();
			out.println(i + " - X: " + nodeCoord.x + "\tY: " + nodeCoord.y);
			i++;
		}
	}

	/**
	 * Gets all Links from all Nodes inside all Layera
	 * @return Collection of all Links
	 */
	private ArrayList<LinkContainer> getLinks() {
		ArrayList<LinkContainer> coll = new ArrayList<LinkContainer>();
		for (LayerContainer llayer : this.handler.getLayerContainers()) {
			for (LinkContainer llink : llayer.getLinkContainers()) {
				coll.add(llink);
			}
		}
		return coll;
	}

	/**
	 * Retrieves an int from the command line prompt
	 * @return The inputted number
	 */
	public int getNextIntFromCommandLine() {
		int choice = 0;
		try {
			String s = scanner.readLine();
			choice = (new Scanner(s)).nextInt();
		} catch (Exception e) {
			out.println("Try to type a Number ! ^.^");
		}

		return choice;
	}

}

class ConsoleFrame extends javax.swing.JFrame  {
	private static final long serialVersionUID = -7848273813181102998L;
	private static final int CONSOLE_LINES = 33;
	private static final int COLUMNS_DEFAUTL_NUMBER = 120;

	public java.io.PrintStream out = null;
	public java.io.InputStream in = null;

	class ConsoleOutputStream extends java.io.OutputStream {

		int[] buffer = null;
		int pointer = 0;
		int columns = COLUMNS_DEFAUTL_NUMBER;

		public ConsoleOutputStream(int columns) {
			buffer = new int[columns];
			this.columns = columns;
		}

		@Override
		public void write(int b) {
			buffer[pointer] = b;
			pointer++;
			if ((b == 10) || (pointer == columns -1)) {
				String s = new String(buffer, 0, pointer);
				pointer = 0;
				append(s);
			}
		}
	}

	private java.io.PipedInputStream pipeIn = null;
	private java.io.PipedOutputStream pipeOut= null;
	private java.io.OutputStreamWriter outWriter = null;
	private javax.swing.JTextArea consoleHistory = null;
	private javax.swing.JLabel promptLabel = null;
	private javax.swing.JTextField prompt = null;
	private int consoleColumns = COLUMNS_DEFAUTL_NUMBER;

	private String[] consoleHistoryText = null;
	private int      actualRowPointer = 0;

	public ConsoleFrame(int columns) throws java.io.IOException {

		consoleColumns = columns;

		pipeIn = new java.io.PipedInputStream();
		pipeOut = new java.io.PipedOutputStream(pipeIn);
		outWriter = new java.io.OutputStreamWriter(pipeOut);
		in = pipeIn;
		out = new java.io.PrintStream(new ConsoleOutputStream(columns));

		initComponents();
		pack();
		setTitle("Javanco - Console");
		setVisible(true);
		prompt.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
					returnTyped();
				}
			}
		});
		prompt.requestFocus(false);


	}

	private void initComponents() {

		java.awt.Font defFont = new java.awt.Font("Courier",java.awt.Font.PLAIN, 12);

		java.awt.Container mainPane = this.getContentPane();
		consoleHistoryText = new String[CONSOLE_LINES];
		consoleHistory = new javax.swing.JTextArea(CONSOLE_LINES,consoleColumns);
		consoleHistory.setBackground(java.awt.Color.BLACK);
		consoleHistory.setForeground(java.awt.Color.WHITE);
		consoleHistory.setEditable(false);
		consoleHistory.setFont(defFont);

		prompt = new javax.swing.JTextField("",consoleColumns);
		prompt.setBackground(java.awt.Color.BLACK);
		prompt.setForeground(java.awt.Color.WHITE);
		prompt.setFont(defFont);
		prompt.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK, 0));

		promptLabel = new javax.swing.JLabel(" $>");
		promptLabel.setFont(defFont);
		promptLabel.setForeground(java.awt.Color.WHITE);


		prompt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent e) {}
			public void removeUpdate(javax.swing.event.DocumentEvent e) {}
			public void insertUpdate(javax.swing.event.DocumentEvent e) {}
		});

		javax.swing.JPanel southPane = new javax.swing.JPanel(new java.awt.FlowLayout());

		southPane.add(promptLabel);
		southPane.add(prompt);
		southPane.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK, 0));
		southPane.setBackground(java.awt.Color.BLACK);
		southPane.setForeground(java.awt.Color.WHITE);

		mainPane.setLayout(new java.awt.BorderLayout());
		mainPane.add(consoleHistory, java.awt.BorderLayout.CENTER);
		mainPane.add(southPane, java.awt.BorderLayout.SOUTH);

	}

	private void returnTyped() {
		try {
			String command = prompt.getText();
			//	command = command.replace(" $>", "");
			outWriter.write(command, 0, command.length());
			outWriter.write('\n');
			prompt.setText("");
			outWriter.flush();
			//	append(" $>" + command + '\n');
		}
		catch (Exception e) {}
	}


	private void append(String s) {
		consoleHistoryText[actualRowPointer] = s;
		actualRowPointer++;
		if (actualRowPointer >= CONSOLE_LINES) {
			actualRowPointer = 0;
		}
		updateConsoleHistory();
	}

	private void updateConsoleHistory() {
		consoleHistory.setText("");
		for (int i = 0 ; i < CONSOLE_LINES ; i++) {
			int nextLine = (actualRowPointer + i) % CONSOLE_LINES;
			if (consoleHistoryText[nextLine] == null) {
				consoleHistory.append("\n");
			} else {
				consoleHistory.append(" " + consoleHistoryText[nextLine]);
			}
		}
	}
}
