package ch.epfl.javanco.printer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import ch.epfl.javanco.ui.AbstractGraphicalUI;
import ch.epfl.javanco.ui.AbstractGraphicalUI.PrintProperties;

public class GraphPrinter {
	public enum InputType {SCREENVIEW, WHOLEGRAPH, CUSTOM};
	public enum OutputType {FITTOPAGE, CUSTOM};
	InputType inputType;
	OutputType outputType;

	private Rectangle  customInput;
	private int customOutputHeight;
	private int customOutputWidth;
	private PageFormat pageFormat;
	private final AbstractGraphicalUI graphicalUI;
	private PrinterJob printJob = PrinterJob.getPrinterJob();

	public GraphPrinter(AbstractGraphicalUI graphicalUI, PageFormat pageFormat) {
		this.graphicalUI = graphicalUI;
		this.inputType = InputType.SCREENVIEW;
		this.outputType = OutputType.FITTOPAGE;
		customInput = graphicalUI.getInfoSetView();


		customOutputWidth = 400;
		customOutputHeight = 400;

		this.pageFormat = pageFormat;

		applyChanges();
	}

	public void setCustomInput(Rectangle customInput) {
		this.customInput = customInput;
		applyChanges();
	}

	public void setCustomOutput(int width, int height) {
		customOutputWidth = width;
		customOutputHeight = height;
		applyChanges();
	}

	public void setInputType(InputType inputType) {
		this.inputType = inputType;
		applyChanges();
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
		applyChanges();
	}

	public void setPageFormat(PageFormat pageFormat) {
		this.pageFormat = pageFormat;
	}

	public PageFormat getPageFormat() {
		return pageFormat;
	}

	public Rectangle getScreenViewRectangle() {
		return graphicalUI.getInfoSetView();
	}

	public Rectangle getWholeGraphView() {
		Rectangle view = graphicalUI.getNodeSpace();
		if (view == null) {
			view = getScreenViewRectangle();
		}
		return view;
	}

	public boolean print(Graphics g, int page) {
		boolean ret;
		try {
			graphicalUI.print(g, pageFormat, page);
			ret = true;
		} catch (PrinterException e) {
			ret = false;
		}
		return ret;
	}

	public static int cmToPixel(double cm) {
		return inchToPixel(cmToInch(cm));
	}

	public static int inchToPixel(double inch) {
		return (int)Math.round(inch*72);
	}

	public static double pixelToInch(int pixel) {
		return pixel / 72.;
	}

	public static double pixelToCm(int pixel) {
		return inchToCm(pixelToInch(pixel));
	}

	public static double inchToCm(double inch) {
		return inch * 2.54;
	}

	public static double cmToInch(double cm) {
		return cm / 2.54;
	}

	public boolean paintPreview(Graphics g, int page) {
		g.translate(10, 10);
		g.setColor(Color.white);
		g.fillRect(0, 0, (int)pageFormat.getWidth(), (int)pageFormat.getHeight());

		boolean ret = print(g, page);

		g.setColor(Color.black);
		g.drawRect((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY(), (int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());

		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(2));

		g2.setColor(Color.black);
		g2.drawLine((int)pageFormat.getWidth()+1, -1, (int)pageFormat.getWidth()+1, (int)pageFormat.getHeight()+1);
		g2.drawLine(-1, (int)pageFormat.getHeight()+1, (int)pageFormat.getWidth()+1, (int)pageFormat.getHeight()+1);


		g2.setColor(new Color(230,230,230));
		g2.drawLine(-1, -1, (int)pageFormat.getWidth()+1, -1);
		g2.drawLine(-1, -1, -1, (int)pageFormat.getHeight()+1);

		g.translate(-10, -10);

		return ret;
	}


	public boolean print() {
		applyChanges();
		printJob.setPrintable(graphicalUI, pageFormat);
		boolean printed = false;
		if (printJob.printDialog()) {
			try {
				printJob.print();
				printed = true;
			} catch (PrinterException e) {
				printed = false;
			}
		}
		return printed;
	}

	private void applyChanges() {
		Rectangle input = null;
		int outputW = 0;
		int outputH = 0;

		switch (inputType) {
		case SCREENVIEW:
			input = getScreenViewRectangle();
			break;
		case WHOLEGRAPH:
			input = getWholeGraphView();
			break;
		case CUSTOM:
			input = customInput;
			break;
		}

		switch (outputType) {
		case FITTOPAGE:
			outputW = (int) pageFormat.getImageableWidth();
			outputH = (int) pageFormat.getImageableHeight();
			break;
		case CUSTOM:
			outputW = customOutputWidth;
			outputH = customOutputHeight;
			break;
		}

		PrintProperties pp = new PrintProperties(input, outputW, outputH);
		graphicalUI.setPrintProperties(pp);
	}

}
