package ch.epfl.general_libraries.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.epfl.general_libraries.event.RunnerEvent;
import ch.epfl.general_libraries.event.RunnerEventListener;
import ch.epfl.general_libraries.graphics.ToolBox;


public class SpeedControlPanel extends JDialog implements ActionListener, ChangeListener, RunnerEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel actualSpeedLabel;
	private JPanel mainPanel;
	private JButton pauseButton;
	private JButton playButton;
	private JButton stepButton;
	private JButton stopButton;
	private JButton deleteButton;
	private JPanel rightDownPanel;
	private JPanel rightPanelButtons;
	private JPanel rightUpPanelBControls;
	private JLabel runningSpeedLabel;

	private JSlider speedSlider;

	protected JPanel leftPanelForUser;

	private Runner currentRunner;

	private int sliderPosition = 0;
	private String iconDir;

	public SpeedControlPanel(JFrame f, boolean b, String iconDir) {
		super(f,b);
		this.iconDir = iconDir;
		initComponents();
		
	}

	public void actionPerformed(ActionEvent evt) {
		JButton source = (JButton)evt.getSource();
		if (currentRunner != null) {
			Thread t = Thread.currentThread();
			String same = t.getName();
			t.setName("Console script actionner");
			if (source.equals(this.playButton)) {
				currentRunner.playCommand();
			} else if (source.equals(this.pauseButton)) {
				currentRunner.pauseCommand();
			} else if (source.equals(this.stopButton)) {
				currentRunner.stopCommand();
			} else if (source.equals(this.stepButton)) {
				currentRunner.stepCommand();
			} else if (source.equals(this.deleteButton)) {
				currentRunner.deleteCommand();
			}
			t.setName(same);
		}
	}

	public void stateChanged(ChangeEvent evt) {
		int newPos = ((JSlider)evt.getSource()).getValue();
		if (newPos != sliderPosition) {
			if (currentRunner != null) {
				currentRunner.setSpeedCommand(newPos);
			}
			sliderPosition = newPos;
		}
	}

	protected void setCurrentRunner(Runner r) {
		currentRunner = r;
	}

	/**
	 * Method scriptStateChanged
	 *
	 *
	 * @param e
	 *
	 */
	public void runnerStateChanged(RunnerEvent e) {
		Runner runner = (Runner)e.getSource();
		switch (runner.getRunningState()) {
		case CREATED :
			setVisible(true);
			currentRunner = runner;
			break;
		case STOPPED :
		case CRASHED :
		case TERMINATED :
		case PAUSED :
		case RUNNING :
			break;
		case DELETED :
			setVisible(false);
			currentRunner = null;
		}
		setRunningSpeed(runner.getRunningSpeed());
	}

	public void runnerSpeedChanged(RunnerEvent e) {
		Runner runner = (Runner)e.getSource();
		setRunningSpeed(runner.getRunningSpeed());
	}

	public void runnerProgressed(int percent) {}

	private void setRunningSpeed(Runner.RunningSpeed s) {
		if (s != null) {
			speedSlider.setValue(s.ordinal());
			actualSpeedLabel.setText("   Actual speed : " + s);
		} else {
			actualSpeedLabel.setText("   Actual speed : (no runner)");
		}
	}



	private void initComponents() {
		mainPanel = new JPanel();
		leftPanelForUser = new JPanel();
		rightPanelButtons = new JPanel();
		rightUpPanelBControls = new JPanel();
		playButton = new JButton();
		pauseButton = new JButton();
		stopButton = new JButton();
		stepButton = new JButton();
		deleteButton = new JButton();
		rightDownPanel = new JPanel();
		runningSpeedLabel = new JLabel();
		speedSlider = new JSlider();
		actualSpeedLabel = new JLabel();

		getContentPane().setLayout(new GridLayout(1, 1));

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainPanel.setLayout(new GridLayout(1, 2, 5, 0));

		leftPanelForUser.setLayout(new BorderLayout());
		leftPanelForUser.setBorder(BorderFactory.createEtchedBorder());

		mainPanel.add(leftPanelForUser);

		rightPanelButtons.setLayout(new GridLayout(2, 1, 0, 5));

		rightUpPanelBControls.setLayout(new FlowLayout(FlowLayout.LEFT));

		rightUpPanelBControls.setBorder(BorderFactory.createEtchedBorder());
		rightUpPanelBControls.setMinimumSize(new Dimension(15, 15));
		rightUpPanelBControls.setPreferredSize(new Dimension(60, 60));
		playButton.setIcon(ToolBox.createImageIcon__(iconDir + "/play.gif"));
		playButton.setToolTipText("Go");
		playButton.setMaximumSize(new Dimension(48, 48));
		playButton.setMinimumSize(new Dimension(48, 48));
		playButton.setPreferredSize(new Dimension(48, 48));
		playButton.addActionListener(this);
		rightUpPanelBControls.add(playButton);

		pauseButton.setIcon(ToolBox.createImageIcon__(iconDir + "/pause.gif"));
		pauseButton.setToolTipText("Pause");
		pauseButton.setMaximumSize(new Dimension(48, 48));
		pauseButton.setMinimumSize(new Dimension(48, 48));
		pauseButton.setPreferredSize(new Dimension(48, 48));
		pauseButton.addActionListener(this);
		rightUpPanelBControls.add(pauseButton);

		stopButton.setIcon(ToolBox.createImageIcon__(iconDir + "/stop.gif"));
		stopButton.setToolTipText("Stop");
		stopButton.setMaximumSize(new Dimension(48, 48));
		stopButton.setMinimumSize(new Dimension(48, 48));
		stopButton.setPreferredSize(new Dimension(48, 48));
		stopButton.addActionListener(this);
		rightUpPanelBControls.add(stopButton);

		stepButton.setIcon(ToolBox.createImageIcon__(iconDir + "/step.gif"));
		stepButton.setToolTipText("step forward");
		stepButton.setMaximumSize(new Dimension(48, 48));
		stepButton.setMinimumSize(new Dimension(48, 48));
		stepButton.setPreferredSize(new Dimension(48, 48));
		stepButton.addActionListener(this);
		rightUpPanelBControls.add(stepButton);

		deleteButton.setIcon(ToolBox.createImageIcon__(iconDir + "/delete.gif"));
		deleteButton.setToolTipText("delete script");
		deleteButton.setMaximumSize(new Dimension(48, 48));
		deleteButton.setMinimumSize(new Dimension(48, 48));
		deleteButton.setPreferredSize(new Dimension(48, 48));
		deleteButton.addActionListener(this);
		rightUpPanelBControls.add(deleteButton);

		rightPanelButtons.add(rightUpPanelBControls);

		rightDownPanel.setLayout(new GridLayout(3, 1));

		rightDownPanel.setBorder(BorderFactory.createEtchedBorder());
		runningSpeedLabel.setText("   Running speed");
		rightDownPanel.add(runningSpeedLabel);

		speedSlider.setMajorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setSnapToTicks(true);
		speedSlider.setValue(0);
		speedSlider.setMaximum(6);
		speedSlider.setMinimum(0);
		speedSlider.addChangeListener(this);
		rightDownPanel.add(speedSlider);

		actualSpeedLabel.setText("   Actual speed : ");
		rightDownPanel.add(actualSpeedLabel);

		rightPanelButtons.add(rightDownPanel);

		mainPanel.add(rightPanelButtons);

		getContentPane().add(mainPanel);

		pack();
	}


}
