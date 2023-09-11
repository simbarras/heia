package ch.epfl.javanco.xml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Branch;
import org.dom4j.Node;
import org.dom4j.tree.DefaultText;

/**
 * <code>NetworkDocumentHelper</code> provides useful methods for manipulating
 * network documents.
 * @author lchatela
 * @see org.dom4j.DocumentHelper
 */
public class NetworkDocumentHelper {

	/**
	 * Removes all the elements containing only white spaces (coming from the indentation
	 * in the document) of the given branch
	 * @param b The branch to clear of white spaces elements
	 */
	@SuppressWarnings("unchecked")
	public static void clearDefaultTextElement(Branch b) {
		List<Node> toRemove = new ArrayList<Node>();
		for (Node node : (List<Node>)b.content()) {
			if (node instanceof DefaultText) {
				String txt = node.getText().trim();
				if (txt.length() == 0){
					toRemove.add(node);
				} else {
					// removes the tabs before the first letter
					txt = node.getText();
					while (txt.startsWith("\n") || txt.startsWith("\t")) {
						txt = txt.substring(2);
					}
					node.setText(txt);
				}
			}
			if (node instanceof Branch) {
				clearDefaultTextElement((Branch)node);
			}
		}
		for (Node node : toRemove) {
			b.remove(node);
		}
	}
}
