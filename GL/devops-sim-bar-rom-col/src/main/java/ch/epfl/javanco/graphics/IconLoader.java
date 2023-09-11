package ch.epfl.javanco.graphics;

import javax.swing.ImageIcon;

import ch.epfl.javanco.base.Javanco;

public class IconLoader {

	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon(String image) {
		ImageIcon ii = null;
		String path = null;
		try {
			path = Javanco.getProperty(Javanco.JAVANCO_DIR_IMAGES_PROPERTY) + "/" + image;

			ii = new ImageIcon(path);
		}
		catch (java.security.AccessControlException e) {
		}
		if (ii != null) {
			return ii;
		} else {
			System.err.println("Couldn't find file: " + path);
			System.out.println("Couldn't find file: " + path);
			return null;
		}
	}


}
