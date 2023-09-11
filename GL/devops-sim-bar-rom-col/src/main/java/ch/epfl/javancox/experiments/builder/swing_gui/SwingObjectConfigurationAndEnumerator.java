package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.gui.ProgressBarDialog;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javancox.experiments.builder.object_enum.AbstractEnumerator;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel.ObjectIterator;

//import com.sun.xml.internal.ws.util.CompletedFuture;


public class SwingObjectConfigurationAndEnumerator<X> implements InstanceDynamicTreeListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger;

	JButton go = new JButton("Run");
	JTextField tre = new JTextField();
	protected JPanel info = new JPanel();

	protected HouseMadeTree<X> tree;
	Class<? extends X> clas;
	protected ClassRepository classRepo;
	private AbstractEnumerator<X> manager;
	private JLabel isReadyLabel;


	public SwingObjectConfigurationAndEnumerator(Class<? extends X> c, AbstractEnumerator<X> m) {
		this(c, m, null);
	}

	public SwingObjectConfigurationAndEnumerator(Class<? extends X> c, 
												 AbstractEnumerator<X> m, 
												 String[] prefixes) {
		logger = new Logger(SwingObjectConfigurationAndEnumerator.class);
		this.manager = m;
		logger.info("Creation of the class repository");
		classRepo = new ClassRepository(prefixes);
		logger.info("Done with class repo");
		if (Experiment.globals.classRepo == null) Experiment.globals.classRepo = classRepo;
		this.clas = c;
	}
	
	public void show() {
		this.show(null);
	}
	
	public void show(String file) {
		logger.info("Loading initial interface (can take time...)");
		try {	
			isReadyLabel = new JLabel("Not ready");			
			
			if (file != null) {
				logger.debug("Loading experiment tree from file " + file);
				tree = new HouseMadeTree<X>(ObjectConstuctionTreeModel.loadFromFile(classRepo,clas, file)); 
			} else {
				setNewTreeWithDialog();
			}
			if (tree == null) return;
			
			tree.getModel().addInstanceDynamicTreeListener(this);
			
			info = new JPanel();

			logger.trace("Create frame");
			final JFrame f = new JFrame();
			f.setSize(1000, 700);
			f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			split.setDividerLocation(620);
			split.add(tree);
			tre.setColumns(2);	
			final JLabel nbThreads = new JLabel("Nb threads to use:");
			go = new JButton("Run");
			go.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (go.getText().equals("Stop")) {
						manager.stopTreeManager();
						go.setText("Run");
					} else if (go.getText().equals("Run")) {
						launchExecution();
					}
				}
			});		
			info.add(isReadyLabel);
			info.add(go);
			info.add(nbThreads);
			info.add(tre);
			
						
			final JButton clearDb = new JButton("ClearDB");
			clearDb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					manager.clearEnumerationResults();
				}
			});	
			clearDb.setFocusable(false);
			info.add(clearDb);
			
			final JButton clearCache = new JButton("Clear cache");
			clearCache.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					manager.clearCaches();
				}
			});	
			clearCache.setFocusable(false);
			info.add(clearCache);
			
			final JButton loadTree = new JButton("Load tree");
			loadTree.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					f.setVisible(false);
					show(null);	
				}
			});	
			loadTree.setFocusable(false);
			info.add(loadTree);			
			split.add(info);
			f.add(split);
			logger.trace("Setting the frame visible");
			f.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void treeReadyAction(ActionEvent e) {
		isReadyLabel.setText("Tree ready for : " + tree.getInstancesCount() + " experiments");
		go.setVisible(true);
	}

	@Override
	public synchronized void treeNotReadyAction(ActionEvent e) {
		isReadyLabel.setText("No result, Tree not ready.");
		go.setVisible(false);
	}	
	
	private void delayedDisplay(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.ERROR_MESSAGE);				
			}
			
		});		
	}

	private void launchExecution() {
		int t = 1;
		ObjectIterator<X> ite;
		try {
			t = Integer.parseInt(tre.getText());
		}
		catch (NumberFormatException e) {}
		try {
			ite = ((ObjectConstuctionTreeModel<X>)tree.getModel()).getObjectIterator();	
			if (ite.hasNext() == false) {
				delayedDisplay("No experiment to run");
				return;
			}
		}
		catch (final Exception e) {
			delayedDisplay("Error (" + e.getClass() + ": " + e.getLocalizedMessage() + ") in batch creation, no action");
			e.printStackTrace();
			return;
		}

		

		
		ProgressBarDialog progress = new ProgressBarDialog(tree.getInstancesCount());
		progress.setDialogVisible();
		go.setText("Stop");		
		manager.runInNewThread(getCallBack(), ite ,progress , t);
	}
	
	private Runnable getCallBack() {
		return new Runnable() {
			public void run() {
				go.setText("Run");
			}
		};
	}
	
	private void setNewTreeWithDialog() {	
		Object[] options = {"Create new", "Load from default file", "load from file", "Run default file"};
		int n = JOptionPane.showOptionDialog(null,
				"Load an existing configuration tree, or create a new one?",
				"New tree",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,     //do not use a custom Icon
				options,  //the titles of buttons
				options[0]); //default button title
		if (n < 0) return;
		logger.trace("User selected option: " + options[n]);
		switch (n) {
		case 0:
			createFullNewTree();			
			break;
		case 1:
			try {
				ObjectConstuctionTreeModel<X> treeMod = ObjectConstuctionTreeModel.loadFromFile(classRepo, clas);
				tree = new HouseMadeTree<X>(treeMod);			
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog( null, "File error!\nA new configuration tree will be created.", "Error", JOptionPane.ERROR_MESSAGE);
				createFullNewTree();
			}
			break;
		case 2:
			try {
				String s = JOptionPane.showInputDialog("Give file name", "file name");
				tree = new HouseMadeTree<X>(ObjectConstuctionTreeModel.loadFromFile(classRepo, clas, s));  
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog( null, "File error!\nA new configuration tree will be created.", "Error", JOptionPane.ERROR_MESSAGE);
				createFullNewTree();
			}
			break;
		case 3:
			try {
				tree = new HouseMadeTree<X>(ObjectConstuctionTreeModel.loadFromFile(classRepo, clas));
				launchExecution();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default :
			System.exit(0);
		}
	}
	
	private void createFullNewTree() {
		try {
			tree = new HouseMadeTree<X>(new ObjectConstuctionTreeModel<X>(clas, classRepo));
		} catch (Exception e) {
			throw new IllegalStateException(e);			
		}
	}
}
