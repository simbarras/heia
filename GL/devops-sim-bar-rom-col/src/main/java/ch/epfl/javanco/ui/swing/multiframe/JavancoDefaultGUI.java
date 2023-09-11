package ch.epfl.javanco.ui.swing.multiframe;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.codehaus.groovy.control.CompilationFailedException;
import org.dom4j.Attribute;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.GraphHandlerFactory;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.event.GraphCreationListener;
import ch.epfl.javanco.graphics.IconLoader;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.DefaultGraphImpl;
import ch.epfl.javanco.scripting.ScriptExecutionException;
import ch.epfl.javanco.ui.AbstractGraphicalUI;
import ch.epfl.javanco.ui.FrameBasedInterface;
import ch.epfl.javanco.ui.swing.FileChooser;
import ch.epfl.javanco.ui.swing.GroovyConsole;
import ch.epfl.javanco.ui.swing.NewLayerDialog;
import ch.epfl.javanco.ui.swing.PopupDisplayer;

public class JavancoDefaultGUI extends JFrame implements WindowListener,
ActionListener,
InternalFrameListener,
GraphCreationListener,
ComponentListener,
FrameBasedInterface {

	public static final long serialVersionUID = 0;

	private GraphHandlerFactory graphHandlerFactory =  null;
	private GroovyConsole console = null;

	private Hashtable<String, JInternalFrame> internalFrameList = new Hashtable<String, JInternalFrame>();

	private Hashtable<JInternalFrame, AbstractGraphHandler> frameTable = new Hashtable<JInternalFrame, AbstractGraphHandler>();

	private boolean editable = true;

	private static String currentXMLDir = null;
	private static String currentScriptDir = null;

	private JInternalFrame currentlyActive = null;
	
	private static JavancoDefaultGUI gui;
	
	public static JavancoDefaultGUI getAndShowDefaultGUI() {
		return getAndShowDefaultGUI(true);
	}
	
	public static JavancoDefaultGUI getAndShowDefaultGUI(boolean editable) {
		if (gui == null) {
			gui = new JavancoDefaultGUI(Javanco.getDefaultGraphHandlerFactory(), editable);
		}
		return gui;
	}

	private JavancoDefaultGUI() {
		this(Javanco.getDefaultGraphHandlerFactory(), true);
	}

	private JavancoDefaultGUI(GraphHandlerFactory factory, boolean editable) {
		initialiseDefaultDirectories();
		factory.addGraphCreationListener(this);
		graphHandlerFactory = factory;

		this.editable = editable;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice() ;
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		Rectangle bounds = gc.getBounds();
		setLocation(10 + bounds.x, 10 + bounds.y);
		setPreferredSize(new Dimension(bounds.width - 50, bounds.height - 140));

		initComponents();
		pack();

		setTitle("Javanco");

		setVisible(true);
	}
	
	public GraphHandlerFactory getGraphHandlerFactory() {
		return graphHandlerFactory;
	}

	@Override
	public JFrame getMainFrame() {
		return this;
	}
	
	@Override
	public void graphRegistered(AbstractGraphHandler agh) {
		// checking if agh is alredy there
		for (AbstractGraphHandler alreadyIn : frameTable.values()) {
			if (alreadyIn == agh) return;
		}
		// if not, do as if created
		graphCreated(agh);
	}

	public void graphCreated(AbstractGraphHandler agh) {
		//	InternalFrameBasedUI sui = (InternalFrameBasedUI)agh.getUIDelegate().addUI(InternalFrameBasedUI.class);
		agh.activateGraphicalDataHandler();
		InternalFrameBasedUI sui;
		try {
			Class.forName("javax.media.opengl.GLEventListener");
			sui = new InternalFrameBasedUIPlus3D(AbstractGraphicalUI.getDefaultNetworkPainter(), agh, this);
		}
		catch (Exception e) {
			sui = new InternalFrameBasedUI(AbstractGraphicalUI.getDefaultNetworkPainter(), agh, this);
		}

		if (sui != null) {
			sui.init();
			sui.setAssociatedGlobalInterface(this);
			//	sui.setUniqueFrame(this);
			JInternalFrame gif = sui.getGraphInternalFrame();
			frameTable.put(gif, agh);

			String title = agh.getHandledGraphName();
			if ((title == null) || (title.equals(""))) {
				title = AbstractGraphHandler.DEFAULT_GRAPH_NAME;
			}
			title = getUniqueName(title);
			agh.setHandledGraphName(title);
			gif.setTitle(title);
			gif.setVisible(true);
			gif.addInternalFrameListener(this);
			gif.addComponentListener(this);
			networkDesktopPane.add(gif);
			internalFrameList.put(title, gif);
			setActiveFrame(gif);
			buildWindowsMenu();
			buildFileMenu();
		}
	}

	public void graphDeleted(AbstractGraphHandler agh) {
		String toRem = null;
		for (Map.Entry<String, JInternalFrame> entry : internalFrameList.entrySet()) {
			JInternalFrame iFrame = entry.getValue();
			if (frameTable.get(iFrame).equals(agh)) {
				iFrame.setVisible(false);
				if (iFrame.equals(currentlyActive)) {
					currentlyActive = null;
				}
				toRem = entry.getKey();
				break;
			}
		}
		if (toRem != null) {
			JInternalFrame iframe = internalFrameList.remove(toRem);
			frameTable.remove(iframe);
			buildWindowsMenu();
			buildScriptMenu();
			buildFileMenu();
		}
	}

	public void graphRenamed(AbstractGraphHandler agh) {
		JInternalFrame f = findFrame(agh);
		if (f != null) {
			f.setTitle(agh.getHandledGraphName());
		}
	}

	public AbstractGraphHandler getCurrentlyActiveAgh() {
		return this.frameTable.get(currentlyActive);
	}
	
	public JInternalFrame getActuallyActiveInternalFrame() {
		return currentlyActive;
	}
	
	private JInternalFrame findFrame(AbstractGraphHandler agh) {
		for (Map.Entry<JInternalFrame, AbstractGraphHandler> entry : frameTable.entrySet()) {
			if (entry.getValue().equals(agh)) {
				return entry.getKey();
			}
		}
		return null;		
	}
	
	public Dimension getFrameDimension(AbstractGraphHandler agh) {
		return findFrame(agh).getSize();
	}

	private String getUniqueName(String s) {
		String temp = s;
		int increment = 2;
		while (internalFrameList.get(temp) != null) {
			temp = s + " (" + increment + ")";
			increment++;
		}
		return temp;
	}

	private void initialiseDefaultDirectories() {
		currentXMLDir = JavancoFile.getDefaultMNDfilesDir();
		currentScriptDir = JavancoFile.getDefaultGroovyScriptDir();
	}

	public void windowActivated(WindowEvent e) {

	}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {
	}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	private void newSimpleMenuItemActionPerformed(ActionEvent evt) {
			try {
				AbstractGraphHandler handler = graphHandlerFactory.getNewGraphHandler();
				handler.newNetwork("", "physical", DefaultGraphImpl.class,
						new ArrayList<Attribute>(0));
			}
			catch(Exception e) {
				e.printStackTrace();
				String message = "Impossible to create new layer with the given properties.";
				PopupDisplayer.popupErrorMessage(message, e, this);
			}
	}	


	private void newMenuItemActionPerformed(ActionEvent evt) {
		NewLayerDialog dialog = new NewLayerDialog(this, true, graphHandlerFactory.getJavancoClassesLoader());
		dialog.setVisible(true);
		if (dialog.hasResult()) {
			try {
				AbstractGraphHandler handler = graphHandlerFactory.getNewGraphHandler();
				handler.newNetwork(dialog.getGraphName(), dialog.getLayerName(), dialog.getLayerClass(),
						dialog.getNewLayerElement());
			}
			catch(Exception e) {
				e.printStackTrace();
				String message = "Impossible to create new layer with the given properties.";
				PopupDisplayer.popupErrorMessage(message, e, this);
			}
		}
	}

	private void  openMenuItemActionPerformed(ActionEvent evt) {
		JavancoFile file = FileChooser.promptForOpenFile(this, currentXMLDir, "xml", "Select XML network file");
		if (file != null) {
			String filename = file.getName().toLowerCase();
			if (!filename.endsWith(".xml")) {
				return;
			}
			String dir = file.getParentFile().getAbsolutePath();
			if (!dir.equals(currentXMLDir)){
				currentXMLDir = dir;
			}
			AbstractGraphHandler gh = graphHandlerFactory.getNewGraphHandler();
			gh.openNetworkFile(file);
		}
	}

	private void setActiveFrame(JInternalFrame frame) {
		try {
			if (currentlyActive != null) {
				currentlyActive.setSelected(false);
			}
			frame.setSelected(true);
		}
		catch (java.beans.PropertyVetoException e) {
			throw new IllegalStateException(e);
		}
		currentlyActive = frame;
		buildWindowsMenu();
		buildScriptMenu();
	}

	public void internalFrameClosing(InternalFrameEvent e) {
		AbstractGraphHandler agh = frameTable.get(e.getInternalFrame());
		if (agh != null) {
			graphHandlerFactory.recycleGraphHandler(agh);
		}

		/*	for (Map.Entry<String, JInternalFrame> entry : internalFrameList.entrySet()) {
			if (e.getInternalFrame().equals(entry.getValue())) {
				this.graphHandlerFactory.recycleGraphHandler(entry.getValue().getAbstractGraphHandler());
				return;
			}
		}*/
	}
	public void internalFrameDeiconified(InternalFrameEvent e) {}
	public void internalFrameIconified(InternalFrameEvent e) {}
	public void internalFrameClosed(InternalFrameEvent e) {}
	public void internalFrameOpened(InternalFrameEvent e) {}
	public void internalFrameActivated(InternalFrameEvent e) {
		currentlyActive = e.getInternalFrame();
		buildScriptMenu();
		buildFileMenu();
	}
	public void internalFrameDeactivated(InternalFrameEvent e) {
		if (e.getInternalFrame().equals(currentlyActive)) {
			currentlyActive = null;
			buildScriptMenu();
			buildFileMenu();
		}
	}
	public void componentHidden(ComponentEvent e) {	}
	public void componentMoved(ComponentEvent e){	}
	public void componentResized(ComponentEvent e){	}
	public void componentShown(ComponentEvent e){	}

	private void  saveMenuItemActionPerformed(ActionEvent evt) {
		JavancoFile file = FileChooser.promptForSaveFile(this, currentXMLDir, "xml");
		if (file != null) {
			String dir = file.getParentFile().getAbsolutePath();
			if (!dir.equals(currentXMLDir)){
				currentXMLDir = dir;
			}
			String filename = file.getName().toLowerCase();
			if (!filename.endsWith(".xml")){
				file = new JavancoFile(file.getAbsolutePath()+".xml");
			}
			try {
				getActualHandler().saveNetwork(file, "xml");
			}
			catch (Exception e) {
				PopupDisplayer.popupErrorMessage("Save operation failed : ", e, this);
			}
		}
	}

	private boolean displayEditionFunction() {
		if (editable == true) {
			if (currentlyActive != null) {
				return frameTable.get(currentlyActive).isEditable();
				//	return currentlyActive.getAbstractGraphHandler().isEditable();
			} else {
				return true;
			}
		}
		return false;
	}

	private void launchScript(final JavancoFile f) {
		try {
			AbstractGraphHandler handler = getActualHandler();
			if (handler == null) {
				handler = graphHandlerFactory.getNewGraphHandler();
				handler.getGroovyScriptManager().executeScript(f);
			} else {
				final AbstractGraphHandler h2 = handler;
				Thread t = new Thread() {
					
					@Override
					public void run() {
						try {
							h2.getGroovyScriptManager().executeScript(f);
						} catch (CompilationFailedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} catch (ScriptExecutionException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
				};
				t.start();
				
			}
			buildScriptMenu();
		}
		catch (ScriptExecutionException e) {
			e.displayInfoDialog(this);
		}
		catch(Exception e) {
			e.printStackTrace();
			String message = "Impossible to run selected script";
			PopupDisplayer.popupErrorMessage(message, e, this);
		}
	}

	private void startGroovyConsole() {
		if (console == null) {
			console = new GroovyConsole(this);
		}
		console.setVisible(true);
	}

	private AbstractGraphHandler getActualHandler() {
		if (currentlyActive != null) {
			return frameTable.get(currentlyActive);
		}
		return null;
	}


	public Dimension getDesktopPaneSize() {
		return networkDesktopPane.getSize();
	}

	private final static String WINDOWS_ACTION = "windows action";
	private final static String LAUNCH_SCRIPT_ACTION = "launch script action";
	private final static String CHOOSE_SCRIPT_ACTION = "choose script action";
	private final static String GROOVY_CONSOLE_ACTION = "groovy console action";

	private void buildScriptMenu() {
		if (!displayEditionFunction()) {
			return;
		}
		jScriptMenu.removeAll();
		JMenuItem launchGroovyConsoleMenuItem = new JMenuItem();
		launchGroovyConsoleMenuItem.setText("Start Groovy Console");
		launchGroovyConsoleMenuItem.setActionCommand(GROOVY_CONSOLE_ACTION);
		if (currentlyActive != null) {
			launchGroovyConsoleMenuItem.setEnabled(true);
		} else {
			launchGroovyConsoleMenuItem.setEnabled(false);
		}
		launchGroovyConsoleMenuItem.addActionListener(this);
		jScriptMenu.add(launchGroovyConsoleMenuItem);
		JMenuItem launchScriptMenuItem = new JMenuItem();
		launchScriptMenuItem.setText("Launch script...");
		launchScriptMenuItem.setActionCommand(CHOOSE_SCRIPT_ACTION);
		launchScriptMenuItem.setEnabled(true);
		launchScriptMenuItem.addActionListener(this);
		jScriptMenu.add(launchScriptMenuItem);
		if (getActualHandler() != null) {
			Map<String, JavancoFile> scriptMap = getActualHandler().getGroovyScriptManager().getRecentScriptsMap();
			if (scriptMap.size() > 0) {
				jScriptMenu.add(new javax.swing.JSeparator());
				for (final Map.Entry<String, JavancoFile> entry : scriptMap.entrySet()) {
					JMenuItem scriptItem = new JMenuItem();
					scriptItem.setText(entry.getKey());
					scriptItem.setActionCommand(LAUNCH_SCRIPT_ACTION);
					scriptItem.addActionListener(this);
					jScriptMenu.add(scriptItem);
				}
			}
		}
		frameMenuBar.repaint();
	}

	private void buildWindowsMenu() {
		jWindowsMenu.removeAll();
		if (internalFrameList.size() < 1) {
			JMenuItem windowsMenuItem = new JMenuItem();
			windowsMenuItem.setText("No opened documents");
			windowsMenuItem.setEnabled(false);
			jWindowsMenu.add(windowsMenuItem);
		}
		for (Map.Entry<String, JInternalFrame> entry : this.internalFrameList.entrySet()) {
			JMenuItem windowsMenuItem = new JMenuItem();
			if (entry.getValue().equals(currentlyActive)) {
				windowsMenuItem.setIcon(IconLoader.createImageIcon("selected_menu_icon.png"));
			} else {
				windowsMenuItem.setIcon(IconLoader.createImageIcon("not_selected_menu_icon.png"));
			}
			windowsMenuItem.setActionCommand(WINDOWS_ACTION);
			windowsMenuItem.setText(entry.getKey());
			windowsMenuItem.setEnabled(true);
			windowsMenuItem.addActionListener(this);
			jWindowsMenu.add(windowsMenuItem);
		}
		frameMenuBar.repaint();
	}

	private void buildFileMenu() {
		if (this.currentlyActive == null) {
			saveMenuItem.setEnabled(false);
		} else {
			saveMenuItem.setEnabled(true);
		}
		frameMenuBar.repaint();
	}

	public void actionPerformed(ActionEvent evt) {
		Object o = evt.getSource();
		if (o instanceof JMenuItem) {
			JMenuItem item = (JMenuItem)o;
			if (item.getActionCommand().equals(CHOOSE_SCRIPT_ACTION)) {
				JavancoFile file = FileChooser.promptForOpenFile(this, currentScriptDir, "groovy", "Select groovy script to launch");
				if (file != null) {
					String dir = file.getParentFile().getAbsolutePath();
					if (!dir.equals(currentScriptDir)){
						currentScriptDir = dir;
					}
					launchScript(file);
				}
			} else if (item.getActionCommand().equals(LAUNCH_SCRIPT_ACTION)) {
				launchScript(getActualHandler().getGroovyScriptManager().getRecentScriptsMap().get(item.getText()));
			} else if (item.getActionCommand().equals(WINDOWS_ACTION)) {
				setActiveFrame(internalFrameList.get(item.getText()));
			} else if (item.getActionCommand().equals(GROOVY_CONSOLE_ACTION)) {
				startGroovyConsole();
			}
		}
	}

	private void initComponents() {
		networkDesktopPane = new javax.swing.JDesktopPane();
		frameMenuBar = new javax.swing.JMenuBar();
		javax.swing.JMenu jFileMenu = new javax.swing.JMenu();
		JMenuItem openMenuItem = new JMenuItem();
		saveMenuItem = new JMenuItem();
		JMenuItem newMenuItem = new JMenuItem();
		JMenuItem newMenuItemSimple = new JMenuItem();

		jScriptMenu = new javax.swing.JMenu();
		jWindowsMenu = new javax.swing.JMenu();
		javax.swing.JMenu jOthersMenu = new javax.swing.JMenu();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(new java.awt.Color(200, 200, 200));

		networkDesktopPane.setPreferredSize(new Dimension(600,500));
		networkDesktopPane.setBackground(new java.awt.Color(200,200,200));
		networkDesktopPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		networkDesktopPane.setDragMode(javax.swing.JDesktopPane.OUTLINE_DRAG_MODE);

		getContentPane().add(networkDesktopPane, java.awt.BorderLayout.CENTER);

		jFileMenu.setText("File");
		
		newMenuItemSimple.setText("New");
		newMenuItemSimple.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newSimpleMenuItemActionPerformed(evt);
			}
		});
		
		
		newMenuItem.setText("New (with configuration details)");
		newMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newMenuItemActionPerformed(evt);
			}
		});
		
		openMenuItem.setText("Open...");
		openMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openMenuItemActionPerformed(evt);
			}
		});
		saveMenuItem.setText("Save");
		saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveMenuItemActionPerformed(evt);
			}
		});
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		if (displayEditionFunction()) {
			jFileMenu.add(newMenuItemSimple);
			jFileMenu.add(newMenuItem);
			
		}
		jFileMenu.add(openMenuItem);
		if (displayEditionFunction()) {
			jFileMenu.add(new javax.swing.JSeparator());
		}
		if (displayEditionFunction()) {
			jFileMenu.add(saveMenuItem);
		}
		if (displayEditionFunction()) {
			jFileMenu.add(new javax.swing.JSeparator());
		}
		jFileMenu.add(quitItem);


		buildFileMenu();
		frameMenuBar.add(jFileMenu);

		jScriptMenu.setText("Scripts");
		buildScriptMenu();
		if (displayEditionFunction()) {
			frameMenuBar.add(jScriptMenu);
		}

		jWindowsMenu.setText("Windows");
		buildWindowsMenu();
		frameMenuBar.add(jWindowsMenu);

		jOthersMenu.setText("?");

		frameMenuBar.add(jOthersMenu);

		setJMenuBar(frameMenuBar);

		pack();

	}

	private javax.swing.JDesktopPane networkDesktopPane;
	private javax.swing.JMenu jScriptMenu;
	private javax.swing.JMenuBar frameMenuBar;
	private javax.swing.JMenu jWindowsMenu;
	private JMenuItem saveMenuItem;

}
