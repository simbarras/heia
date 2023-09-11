package ch.epfl.javanco.exports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class S_D_W_X_Y_Exporter extends AbstractTopologyExporter {

	@Override
	public void exportTopology(AbstractGraphHandler agh, OutputStream s) throws IOException {
		OutputStreamWriter fw = new OutputStreamWriter(s);

		appendLine(fw, "# nodes");
		appendLine(fw, agh.getNodeContainers().size());
		appendLine(fw, "# edges");
		appendLine(fw, agh.getLinkContainers().size());
		appendLine(fw, "# Nodes (x y)");
		for (NodeContainer nc : agh.getNodeContainers()) {
			appendLine(fw, nc.attribute(XMLTagKeywords.POS_X).intValue() + " " +
					nc.attribute(XMLTagKeywords.POS_Y).intValue());
		}
		appendLine(fw, "# Edges (i j wt)");
		for (LinkContainer lc : agh.getLinkContainers()) {
			appendLine(fw, lc.getStartNodeIndex() +
					" " +
					lc.getEndNodeIndex() +
					" " +
					lc.attribute("length").intValue());
		}

		fw.close();

	}

	public static void appendLine(Writer fw, String s) throws IOException {
		fw.append(s);
		fw.append("\r\n");
	}

	public static void appendLine(Writer fw, int s) throws IOException {
		fw.append(s+"");
		fw.append("\r\n");
	}
}
