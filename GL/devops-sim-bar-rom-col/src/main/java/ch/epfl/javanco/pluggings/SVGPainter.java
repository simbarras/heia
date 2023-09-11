package ch.epfl.javanco.pluggings;

import java.awt.Frame;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.DefaultNetworkPainter2DExtended;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.ui.AbstractGraphicalUI;

public class SVGPainter extends JavancoTool {

	public SVGPainter() {
		// TODO Auto-generated constructor stub
	}

	@Override	
	public void run(final AbstractGraphHandler agh, final Frame f) {
		promptForSaveAsSVG(agh);
	}
	
	private static Pair<SVGGraphics2D, Rectangle> createBasics(AbstractGraphHandler agh) {
		// Get a DOMImplementation.
		DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		SVGDocument document = (SVGDocument)domImpl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

		// Create an instance of the SVG Generator.
		SVGGraphics2D svgG = new SVGGraphics2D(document);
		
		AbstractGraphicalUI ui = agh.getUIDelegate().getDefaultGraphicalUI();
		
		
		NetworkPainter painter = ui.getNetworkPainter();
		if (painter == null)
			painter = new DefaultNetworkPainter2DExtended();
		
		painter.paintItToGraphics(svgG, ui.getGraphDisplayInformationSet());

		return new Pair<SVGGraphics2D, Rectangle>(svgG, ui.getInfoSetView());
	}
	

	private static void promptForSaveAsSVG(AbstractGraphHandler agh) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new FileNameExtensionFilter("Fichiers svg", "svg"));
		int ret = jfc.showSaveDialog(jfc);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (file.getName().endsWith(".svg") == false) {
				file = new File(file.getPath() + ".svg");
			}
			
			try {
				Pair<SVGGraphics2D, Rectangle> svgGDimension = createBasics(agh);				
				FileWriter fileWriter = new FileWriter(file);
				svgGDimension.getFirst().stream(fileWriter, false);
				fileWriter.close();

				String svgFileName = file.getName();
				file = new File(file.getParentFile(), file.getName() + ".htm");
				fileWriter = new FileWriter(file);
				fileWriter.write("<html>" +
						"<body>" +
						"<h1>SVG</h1>" +
						"<embed src=\"" + svgFileName + "\" width=\"" +
						svgGDimension.getSecond().width + "\" height=\"" +
						svgGDimension.getSecond().height + "\" type=\"image/svg+xml\" />" +
						"</body>" +
				"</html>");
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

	public static void saveGraphSVG(AbstractGraphHandler agh, OutputStream stream, int w, int h) throws java.io.IOException {
		Pair<SVGGraphics2D, Rectangle> svgGDimension = createBasics(agh);

		java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(stream);
		
		svgGDimension.getFirst().stream(wr, false);
	}

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub
		
	}

}
