package ch.epfl.general_libraries.graphics;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;

public class ToolBox {
	/** Returns an ImageIcon, or null if the path was invalid. */
	public static ImageIcon createImageIcon__(String imagePath) {
		ImageIcon ii = null;
		String path = null;
		try {

			ii = new ImageIcon(imagePath);
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
	
	public static ImageIcon createImageIcon__(URL imagePath) {
		ImageIcon ii = null;
		try {
			ii = new ImageIcon(imagePath);
			return ii;
		}
		catch (java.security.AccessControlException e) {
		}
		return null;
	}	

	public static Color getInverseColor(Color color) {
		Color toReturn = Color.YELLOW;
		if (color != null) {
			int red, green, blue;
			if (color.getRed() + color.getGreen() + color.getBlue() > 381) {
				red = Math.max(0, color.getRed() - 127);
				green = Math.max(0, color.getGreen() - 127);
				blue = Math.max(0, color.getBlue() - 127);
			} else {
				red = Math.min(255, color.getRed() + 127);
				green = Math.min(255, color.getGreen() + 127);
				blue = Math.min(255, color.getBlue() + 127);
			}
			return new Color(red, green, blue, color.getAlpha());
		}
		return toReturn;
	}
}