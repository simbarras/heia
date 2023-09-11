package ch.epfl.javanco.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.general_libraries.event.RunnerEvent;
import ch.epfl.general_libraries.gui.Runner;
import ch.epfl.general_libraries.gui.SpeedControlPanel;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.scripting.GroovyScriptManager;


/**
 *
 * @author  rumley
 */
public class ScriptControlPanel extends SpeedControlPanel implements ListSelectionListener {

	public static final long serialVersionUID = 0;
	private static ScriptControlPanel actuallyDisplayed = null;
	private Hashtable<String, Runner> runnerList = null;
	//private GroovyScriptManager.Runner selected = null;
	private LocalListModel model = null;
	private javax.swing.JFrame parentFrame = null;
	private JScrollPane listScrollPane;
	private JList threadList;
	private JLabel runningThreadsLabel;

	/** Creates new form ScriptControllerDialog */
	public ScriptControlPanel(javax.swing.JFrame parent) {
		super(parent, false, Javanco.getProperty(Javanco.JAVANCO_DIR_IMAGES_PROPERTY));
		this.parentFrame = parent;
		runnerList = new Hashtable<String, Runner>();
		model = new LocalListModel(runnerList);
		initComponents();
	}

	@Override
	synchronized public void setVisible(boolean visible) {
		if (visible) {
			if (actuallyDisplayed != null) {
				this.setLocation(actuallyDisplayed.getLocation());
				actuallyDisplayed.setVisible(false);
			}
			actuallyDisplayed = this;
			super.setVisible(true);
		} else {
			actuallyDisplayed = null;
			super.setVisible(false);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		refreshDisplay();
	}

	private void refreshDisplay() {
		model.update();
		Runner r = (Runner)threadList.getSelectedValue();
		super.setCurrentRunner(r);
	}

	@Override
	public void runnerStateChanged(RunnerEvent e) {
		Runner runner = (Runner)e.getSource();
		switch (runner.getRunningState()) {
		case CREATED :
			setVisible(true);
			runnerList.put(runner.getKey(), runner);
			refreshDisplay();
			break;
		case STOPPED :
		case CRASHED :
		case TERMINATED :
		case PAUSED :
		case RUNNING :
			refreshDisplay();
			break;
		case DELETED :
			runnerList.remove(runner.getKey());
			refreshDisplay();
			if (runnerList.size() == 0) {
				setVisible(false);
			}
		}
	}

	@Override
	public void runnerSpeedChanged(RunnerEvent e) {
		refreshDisplay();
	}

	private void initComponents() {
		listScrollPane = new JScrollPane();
		threadList = new JList();
		threadList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		threadList.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 10));
		threadList.setModel(model);
		threadList.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = threadList.locationToIndex(e.getPoint());
					model.displayInfos(index);
				}
			}
		});
		listScrollPane.setViewportView(threadList);

		leftPanelForUser.add(listScrollPane, BorderLayout.CENTER);
		runningThreadsLabel = new JLabel();
		runningThreadsLabel.setText("   Running scripts threads");
		runningThreadsLabel.setPreferredSize(new Dimension(34, 25));
		leftPanelForUser.add(runningThreadsLabel, BorderLayout.NORTH);

		threadList.addListSelectionListener(this);
	}

	private class LocalListModel extends AbstractListModel {

		public static final long serialVersionUID = 0;
		String[] strings = null;
		Runner[] runners = null;
		Hashtable<String, Runner> runnerList = null;

		LocalListModel(Hashtable<String, Runner> runnerList) {
			this.runnerList = runnerList;
			update();
		}

		public void update() {
			strings = new String[runnerList.size()];
			runners = new Runner[runnerList.size()];
			int i = 0;
			for (Map.Entry<String, Runner> entry : runnerList.entrySet()) {
				strings[i] = entry.getValue().toString();
				runners[i] = entry.getValue();
				i++;
			}
			fireContentsChanged(this,0, runners.length -1);
		}

		public Object getElementAt(int i) {
			if (i < runners.length) {
				return runners[i];
			} else {
				return null;
			}
		}

		public int getSize() {
			return strings.length;
		}

		public void displayInfos(int index) {
			GroovyScriptManager.Runner runner = (GroovyScriptManager.Runner)getElementAt(index);
			PopupDisplayer.popupInfoMessage(runner.getLog(), runner.getName(), parentFrame);
		}

	}
}
