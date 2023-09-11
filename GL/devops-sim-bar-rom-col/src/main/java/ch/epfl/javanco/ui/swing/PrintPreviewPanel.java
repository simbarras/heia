package ch.epfl.javanco.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import ch.epfl.javanco.printer.GraphPrinter;
import ch.epfl.javanco.printer.GraphPrinter.InputType;
import ch.epfl.javanco.printer.GraphPrinter.OutputType;

public class PrintPreviewPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private PreviewPanel jPreviewPanel = null;
	private JPanel jPanelButtons = null;
	private JButton jButtonPageLayout = null;
	private JPanel jPanelInput = null;
	private JRadioButton jRadioButtonScreenView = null;
	private JRadioButton jRadioButtonWholeGraph = null;
	private JRadioButton jRadioButtonCustom = null;
	private JPanel jPanelCustomInput = null;
	private JTextField jTextFieldInputX = null;
	private JTextField jTextFieldInputY = null;
	private JTextField jTextFieldInputWidth = null;
	private JTextField jTextFieldInputHeight = null;
	private final GraphPrinter graphPrinter;
	private JScrollPane jScrollPane = null;
	private JPanel jPanelOutput = null;
	private JRadioButton jRadioButtonFitToPage = null;
	private JRadioButton jRadioButtonSameAsScreen = null;
	private JRadioButton jRadioButtonCustomO = null;
	private JPanel jPanelCustomOutput = null;
	private JTextField jTextFieldOutputWidth = null;
	private JTextField jTextFieldOutputHeight = null;
	private JLabel jLabelInX = null;
	private JLabel jLabelInY = null;
	private JLabel jLabelInW = null;
	private JLabel jLabelInH = null;
	private JLabel jLabelOutW = null;
	private JLabel jLabelOutH1 = null;
	private JPanel jPanelBottom = null;
	private JSlider jSliderZoom = null;
	private JLabel jLabelSize = null;


	private double zoom = 1;
	private JButton jButtonPrint = null;
	@SuppressWarnings("serial")
	class PreviewPanel extends JComponent {
		public PreviewPanel() {
			super();
		}
		public void newSize() {
			PageFormat pageFormat = graphPrinter.getPageFormat();
			double width = (pageFormat.getWidth() + 20) * zoom;
			double height = (pageFormat.getHeight() + 20) * zoom;

			setPreferredSize(new Dimension((int)width, (int)height));
			setSize(new Dimension((int)width, (int)height));
			//repaint();
		}
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D) g;

			AffineTransform at = g2d.getTransform();
			g2d.scale(zoom, zoom);
			graphPrinter.paintPreview(g, 0);

			g2d.setTransform(at);

		}
	};

	public PrintPreviewPanel(final GraphPrinter graphPrinter) {
		super();
		this.graphPrinter = graphPrinter;
		getJPreviewPanel().newSize();
		initialize();

		class IntVerifier extends InputVerifier implements ActionListener, KeyListener {

			@Override
			public boolean verify(JComponent input) {
				JTextField jtf = (JTextField)input;
				try {
					Integer.parseInt(jtf.getText());
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}


			public void actionPerformed(ActionEvent e) {
				JTextField source = (JTextField)e.getSource();
				if (verify(source)) {
					applyCustomInput();
				}
				source.selectAll();
			}


			@Override
			public void keyPressed(KeyEvent e) {}


			@Override
			public void keyReleased(KeyEvent e) {}


			@Override
			public void keyTyped(KeyEvent e) {
				if (verify((JTextField) e.getSource())) {
					applyCustomInput();
				}
			}
		}

		class DoubleVerifier extends InputVerifier implements ActionListener, KeyListener {

			@Override
			public boolean verify(JComponent input) {
				JTextField jtf = (JTextField)input;
				try {
					Double.parseDouble(jtf.getText());
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}


			public void actionPerformed(ActionEvent e) {
				JTextField source = (JTextField)e.getSource();
				if (verify(source)) {
					applyCustomOutput();
				} else {
					source.selectAll();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}


			@Override
			public void keyReleased(KeyEvent e) {}


			@Override
			public void keyTyped(KeyEvent e) {
				if (verify((JTextField) e.getSource())) {
					applyCustomOutput();
				}
			}
		};


		IntVerifier intVerifier = new IntVerifier();
		jTextFieldInputX.setInputVerifier(intVerifier);
		jTextFieldInputY.setInputVerifier(intVerifier);
		jTextFieldInputWidth.setInputVerifier(intVerifier);
		jTextFieldInputHeight.setInputVerifier(intVerifier);

		jTextFieldInputX.addActionListener(intVerifier);
		jTextFieldInputY.addActionListener(intVerifier);
		jTextFieldInputWidth.addActionListener(intVerifier);
		jTextFieldInputHeight.addActionListener(intVerifier);

		DoubleVerifier doubleVerifier = new DoubleVerifier();

		jTextFieldOutputWidth.setInputVerifier(doubleVerifier);
		jTextFieldOutputHeight.setInputVerifier(doubleVerifier);

		jTextFieldOutputWidth.addActionListener(doubleVerifier);
		jTextFieldOutputHeight.addActionListener(doubleVerifier);

		jTextFieldInputX.addKeyListener(intVerifier);
		jTextFieldInputY.addKeyListener(intVerifier);
		jTextFieldInputWidth.addKeyListener(intVerifier);
		jTextFieldInputHeight.addKeyListener(intVerifier);

		jTextFieldOutputWidth.addKeyListener(doubleVerifier);
		jTextFieldOutputHeight.addKeyListener(doubleVerifier);

		FocusListener focusListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((JTextField)e.getSource()).selectAll();
			}
		};


		jTextFieldInputX.addFocusListener(focusListener);
		jTextFieldInputY.addFocusListener(focusListener);
		jTextFieldInputWidth.addFocusListener(focusListener);
		jTextFieldInputHeight.addFocusListener(focusListener);

		jTextFieldOutputWidth.addFocusListener(focusListener);
		jTextFieldOutputHeight.addFocusListener(focusListener);


		ActionListener changeInput = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle view = null;
				Boolean enableCustomInput = null;
				if (e.getSource() == jRadioButtonScreenView && jRadioButtonScreenView.isSelected()) {
					graphPrinter.setInputType(InputType.SCREENVIEW);
					view = graphPrinter.getScreenViewRectangle();
					enableCustomInput = false;
					jPreviewPanel.repaint();
				}
				else if (e.getSource() == jRadioButtonWholeGraph && jRadioButtonWholeGraph.isSelected()) {
					graphPrinter.setInputType(InputType.WHOLEGRAPH);
					view = graphPrinter.getWholeGraphView();
					enableCustomInput = false;
					jPreviewPanel.repaint();
				}
				else if (e.getSource() == jRadioButtonCustom && jRadioButtonCustom.isSelected()) {
					graphPrinter.setInputType(InputType.CUSTOM);
					applyCustomInput();
					enableCustomInput = true;
					jPreviewPanel.repaint();
				}

				if (view != null) {
					jTextFieldInputX.setText("" +view.x);
					jTextFieldInputY.setText("" +view.y);
					jTextFieldInputWidth.setText("" +view.width);
					jTextFieldInputHeight.setText("" +view.height);
				}

				if (enableCustomInput != null) {
					jTextFieldInputX.setEnabled(enableCustomInput);
					jTextFieldInputY.setEnabled(enableCustomInput);
					jTextFieldInputWidth.setEnabled(enableCustomInput);
					jTextFieldInputHeight.setEnabled(enableCustomInput);
				}
			}
		};

		ActionListener changeOutput = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean enableCustomOutpput = null;

				if (e.getSource() == jRadioButtonFitToPage && jRadioButtonFitToPage.isSelected()) {
					graphPrinter.setOutputType(OutputType.FITTOPAGE);

					PageFormat pf = graphPrinter.getPageFormat();

					jTextFieldOutputHeight.setText("" + round(GraphPrinter.pixelToCm((int)pf.getImageableHeight())));
					jTextFieldOutputWidth.setText("" + round(GraphPrinter.pixelToCm((int)pf.getImageableWidth())));


					enableCustomOutpput = false;
					jPreviewPanel.repaint();
				}
				else if (e.getSource() == jRadioButtonCustomO && jRadioButtonCustomO.isSelected()) {
					graphPrinter.setOutputType(OutputType.CUSTOM);
					applyCustomOutput();
					enableCustomOutpput = true;
					jPreviewPanel.repaint();
				}

				if (enableCustomOutpput != null) {
					jTextFieldOutputWidth.setEnabled(enableCustomOutpput);
					jTextFieldOutputHeight.setEnabled(enableCustomOutpput);
				}
			}
		};

		jRadioButtonScreenView.addActionListener(changeInput);
		jRadioButtonWholeGraph.addActionListener(changeInput);
		jRadioButtonCustom.addActionListener(changeInput);

		jRadioButtonFitToPage.addActionListener(changeOutput);
		jRadioButtonSameAsScreen.addActionListener(changeOutput);
		jRadioButtonCustomO.addActionListener(changeOutput);

		ButtonGroup inputGroup = new ButtonGroup();
		inputGroup.add(jRadioButtonScreenView);
		inputGroup.add(jRadioButtonWholeGraph);
		inputGroup.add(jRadioButtonCustom);

		ButtonGroup outputGroup = new ButtonGroup();
		outputGroup.add(jRadioButtonFitToPage);
		outputGroup.add(jRadioButtonSameAsScreen);
		outputGroup.add(jRadioButtonCustomO);

		jRadioButtonScreenView.doClick();
		jRadioButtonFitToPage.doClick();
		jSliderZoom.setValue(60);
	}


	private void setAllEnabled(boolean enable) {
		jButtonPageLayout.setEnabled(enable);
		jButtonPrint.setEnabled(enable);
		jRadioButtonCustom.setEnabled(enable);
		jRadioButtonCustomO.setEnabled(enable);
		jRadioButtonFitToPage.setEnabled(enable);
		jRadioButtonSameAsScreen.setEnabled(enable);
		jRadioButtonScreenView.setEnabled(enable);
		jRadioButtonWholeGraph.setEnabled(enable);
		jLabelSize.setEnabled(enable);
		jSliderZoom.setEnabled(enable);
		jPanelInput.setEnabled(enable);
		jPanelOutput.setEnabled(enable);


		jLabelInH.setEnabled(enable);
		jLabelInW.setEnabled(enable);
		jLabelInX.setEnabled(enable);
		jLabelInY.setEnabled(enable);
		jLabelOutH1.setEnabled(enable);
		jLabelOutW.setEnabled(enable);


		if (jRadioButtonCustomO.isSelected()) {
			jTextFieldOutputHeight.setEnabled(enable);
			jTextFieldOutputWidth.setEnabled(enable);
		}

		if (jRadioButtonCustom.isSelected()) {
			jTextFieldInputHeight.setEnabled(enable);
			jTextFieldInputWidth.setEnabled(enable);
			jTextFieldInputX.setEnabled(enable);
			jTextFieldInputY.setEnabled(enable);
		}

	}

	private double round(double x) {
		return Math.round(x*100) / 100.;
	}

	private void applyCustomInput() {
		int x = Integer.parseInt(jTextFieldInputX.getText());
		int y = Integer.parseInt(jTextFieldInputY.getText());
		int w = Integer.parseInt(jTextFieldInputWidth.getText());
		int h = Integer.parseInt(jTextFieldInputHeight.getText());

		graphPrinter.setCustomInput(new Rectangle(x, y, w, h));
		jPreviewPanel.repaint();
	}

	private void applyCustomOutput() {
		double w = Double.parseDouble(jTextFieldOutputWidth.getText());
		double h = Double.parseDouble(jTextFieldOutputHeight.getText());

		graphPrinter.setCustomOutput(GraphPrinter.cmToPixel(w), GraphPrinter.cmToPixel(h));
		jPreviewPanel.repaint();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(677, 224);
		this.add(getJPanelButtons(), BorderLayout.NORTH);
		this.add(getJScrollPane(), BorderLayout.CENTER);
		this.add(getJPanelBottom(), BorderLayout.SOUTH);
	}

	/**
	 * This method initializes jPreviewPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private PreviewPanel getJPreviewPanel() {
		if (jPreviewPanel == null) {
			jPreviewPanel = new PreviewPanel();
		}
		return jPreviewPanel;
	}

	/**
	 * This method initializes jPanelButtons
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 3;
			gridBagConstraints19.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			jPanelButtons = new JPanel();
			jPanelButtons.setLayout(new GridBagLayout());
			jPanelButtons.add(getJButtonPageLayout(), new GridBagConstraints());
			jPanelButtons.add(getJPanelInput(), gridBagConstraints);
			jPanelButtons.add(getJPanelOutput(), new GridBagConstraints());
			jPanelButtons.add(getJButtonPrint(), gridBagConstraints19);
		}
		return jPanelButtons;
	}

	/**
	 * This method initializes jButtonPageLayout
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonPageLayout() {
		if (jButtonPageLayout == null) {
			jButtonPageLayout = new JButton();
			jButtonPageLayout.setText("Page layout");
			jButtonPageLayout.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					graphPrinter.setPageFormat(PrinterJob.getPrinterJob().pageDialog(graphPrinter.getPageFormat()));
					jPreviewPanel.newSize();
					jPreviewPanel.repaint();
				}
			});
		}
		return jButtonPageLayout;
	}

	/**
	 * This method initializes jPanelInput
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelInput() {
		if (jPanelInput == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.insets = new Insets(0, 10, 0, 0);
			gridBagConstraints9.gridy = 3;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 1;
			jPanelInput = new JPanel();
			jPanelInput.setLayout(new GridBagLayout());
			jPanelInput.setBorder(BorderFactory.createTitledBorder(null, "Input", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelInput.add(getJRadioButtonScreenView(), gridBagConstraints3);
			jPanelInput.add(getJRadioButtonWholeGraph(), gridBagConstraints1);
			jPanelInput.add(getJRadioButtonCustom(), gridBagConstraints2);
			jPanelInput.add(getJPanelCustomInput(), gridBagConstraints9);
		}
		return jPanelInput;
	}

	/**
	 * This method initializes jRadioButtonScreenView
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonScreenView() {
		if (jRadioButtonScreenView == null) {
			jRadioButtonScreenView = new JRadioButton();
			jRadioButtonScreenView.setText("Screen view");
		}
		return jRadioButtonScreenView;
	}

	/**
	 * This method initializes jRadioButtonWholeGraph
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonWholeGraph() {
		if (jRadioButtonWholeGraph == null) {
			jRadioButtonWholeGraph = new JRadioButton();
			jRadioButtonWholeGraph.setText("Whole graph");
		}
		return jRadioButtonWholeGraph;
	}

	/**
	 * This method initializes jRadioButtonCustom
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonCustom() {
		if (jRadioButtonCustom == null) {
			jRadioButtonCustom = new JRadioButton();
			jRadioButtonCustom.setText("Custom");
		}
		return jRadioButtonCustom;
	}

	/**
	 * This method initializes jPanelCustomInput
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCustomInput() {
		if (jPanelCustomInput == null) {
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 2;
			gridBagConstraints16.insets = new Insets(0, 5, 0, 0);
			gridBagConstraints16.anchor = GridBagConstraints.EAST;
			gridBagConstraints16.gridy = 1;
			jLabelInH = new JLabel();
			jLabelInH.setText("Height");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 2;
			gridBagConstraints15.anchor = GridBagConstraints.EAST;
			gridBagConstraints15.insets = new Insets(0, 5, 0, 0);
			gridBagConstraints15.gridy = 0;
			jLabelInW = new JLabel();
			jLabelInW.setText("Width");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.anchor = GridBagConstraints.EAST;
			gridBagConstraints14.gridy = 1;
			jLabelInY = new JLabel();
			jLabelInY.setText("Y");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = GridBagConstraints.EAST;
			gridBagConstraints13.gridy = 0;
			jLabelInX = new JLabel();
			jLabelInX.setText("X");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints7.gridx = 3;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints6.gridx = 3;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 1.0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			jPanelCustomInput = new JPanel();
			jPanelCustomInput.setLayout(new GridBagLayout());
			jPanelCustomInput.add(getJTextFieldInputX(), gridBagConstraints4);
			jPanelCustomInput.add(getJTextFieldInputY(), gridBagConstraints5);
			jPanelCustomInput.add(getJTextFieldInputWidth(), gridBagConstraints6);
			jPanelCustomInput.add(getJTextFieldInputHeight(), gridBagConstraints7);
			jPanelCustomInput.add(jLabelInX, gridBagConstraints13);
			jPanelCustomInput.add(jLabelInY, gridBagConstraints14);
			jPanelCustomInput.add(jLabelInW, gridBagConstraints15);
			jPanelCustomInput.add(jLabelInH, gridBagConstraints16);
		}
		return jPanelCustomInput;
	}

	/**
	 * This method initializes jTextFieldInputX
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldInputX() {
		if (jTextFieldInputX == null) {
			jTextFieldInputX = new JTextField(5);
			jTextFieldInputX.setText("InputX");
		}
		return jTextFieldInputX;
	}

	/**
	 * This method initializes jTextFieldInputY
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldInputY() {
		if (jTextFieldInputY == null) {
			jTextFieldInputY = new JTextField(5);
			jTextFieldInputY.setText("InputY");
		}
		return jTextFieldInputY;
	}

	/**
	 * This method initializes jTextFieldInputWidth
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldInputWidth() {
		if (jTextFieldInputWidth == null) {
			jTextFieldInputWidth = new JTextField(5);
			jTextFieldInputWidth.setText("InputWidth");
		}
		return jTextFieldInputWidth;
	}

	/**
	 * This method initializes jTextFieldInputHeight
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldInputHeight() {
		if (jTextFieldInputHeight == null) {
			jTextFieldInputHeight = new JTextField(5);
			jTextFieldInputHeight.setText("InputHeight");
		}
		return jTextFieldInputHeight;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJPreviewPanel());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPanelOutput
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelOutput() {
		if (jPanelOutput == null) {
			GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
			gridBagConstraints91.insets = new Insets(0, 10, 0, 0);
			gridBagConstraints91.gridy = 3;
			gridBagConstraints91.gridx = 0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.gridy = 2;
			gridBagConstraints21.gridx = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.gridx = 0;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			jPanelOutput = new JPanel();
			jPanelOutput.setLayout(new GridBagLayout());
			jPanelOutput.setBorder(BorderFactory.createTitledBorder(null, "Output", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			jPanelOutput.add(getJRadioButtonFitToPage(), gridBagConstraints31);
			jPanelOutput.add(getJRadioButtonSameAsScreen(), gridBagConstraints11);
			jPanelOutput.add(getJRadioButtonCustomO(), gridBagConstraints21);
			jPanelOutput.add(getJPanelCustomOutput(), gridBagConstraints91);
		}
		return jPanelOutput;
	}

	/**
	 * This method initializes jRadioButtonFitToPage
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonFitToPage() {
		if (jRadioButtonFitToPage == null) {
			jRadioButtonFitToPage = new JRadioButton();
			jRadioButtonFitToPage.setText("Fit to page");
		}
		return jRadioButtonFitToPage;
	}

	/**
	 * This method initializes jRadioButtonSameAsScreen
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonSameAsScreen() {
		if (jRadioButtonSameAsScreen == null) {
			jRadioButtonSameAsScreen = new JRadioButton();
			jRadioButtonSameAsScreen.setText("Same as screen");
		}
		return jRadioButtonSameAsScreen;
	}

	/**
	 * This method initializes jRadioButtonCustomO
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJRadioButtonCustomO() {
		if (jRadioButtonCustomO == null) {
			jRadioButtonCustomO = new JRadioButton();
			jRadioButtonCustomO.setText("Custom");
		}
		return jRadioButtonCustomO;
	}

	/**
	 * This method initializes jPanelCustomOutput
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelCustomOutput() {
		if (jPanelCustomOutput == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.anchor = GridBagConstraints.EAST;
			gridBagConstraints18.gridy = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.anchor = GridBagConstraints.EAST;
			gridBagConstraints17.gridy = 0;
			jLabelOutH1 = new JLabel();
			jLabelOutH1.setText("Height (cm)");
			jLabelOutW = new JLabel();
			jLabelOutW.setText("Width (cm)");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.weightx = 1.0;
			jPanelCustomOutput = new JPanel();
			jPanelCustomOutput.setLayout(new GridBagLayout());
			jPanelCustomOutput.add(getJTextFieldOutputWidth(), gridBagConstraints10);
			jPanelCustomOutput.add(getJTextFieldOutputHeight(), gridBagConstraints12);
			jPanelCustomOutput.add(jLabelOutW, gridBagConstraints17);
			jPanelCustomOutput.add(jLabelOutH1, gridBagConstraints18);
		}
		return jPanelCustomOutput;
	}

	/**
	 * This method initializes jTextFieldOutputWidth
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldOutputWidth() {
		if (jTextFieldOutputWidth == null) {
			jTextFieldOutputWidth = new JTextField(5);
			jTextFieldOutputWidth.setText("InputWidth");
		}
		return jTextFieldOutputWidth;
	}

	/**
	 * This method initializes jTextFieldOutputHeight
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldOutputHeight() {
		if (jTextFieldOutputHeight == null) {
			jTextFieldOutputHeight = new JTextField(5);
			jTextFieldOutputHeight.setText("InputHeight");
		}
		return jTextFieldOutputHeight;
	}

	/**
	 * This method initializes jPanelBottom
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBottom() {
		if (jPanelBottom == null) {
			jLabelSize = new JLabel();
			jLabelSize.setText("Preview size");
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			jPanelBottom = new JPanel();
			jPanelBottom.setLayout(flowLayout);
			jPanelBottom.add(jLabelSize, null);
			jPanelBottom.add(getJSliderZoom(), null);
		}
		return jPanelBottom;
	}

	/**
	 * This method initializes jSliderZoom
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getJSliderZoom() {
		if (jSliderZoom == null) {
			jSliderZoom = new JSlider();
			jSliderZoom.setMinimum(5);
			jSliderZoom.setValue(100);
			jSliderZoom.setMaximum(200);
			jSliderZoom.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					jLabelSize.setText("Preview size " + jSliderZoom.getValue() + "%");
					zoom = jSliderZoom.getValue() / 100.;
					jPreviewPanel.newSize();
				}
			});
		}
		return jSliderZoom;
	}

	/**
	 * This method initializes jButtonPrint
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonPrint() {
		if (jButtonPrint == null) {
			jButtonPrint = new JButton();
			jButtonPrint.setText("Print");
			jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setAllEnabled(false);
					jButtonPrint.setText("Printing ...");
					if (!graphPrinter.print()) {
						JOptionPane.showMessageDialog(PrintPreviewPanel.this, "Error while printing.", "Print process", JOptionPane.ERROR_MESSAGE);
					}

					setAllEnabled(true);
					jButtonPrint.setText("Print");
				}
			});
		}
		return jButtonPrint;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
