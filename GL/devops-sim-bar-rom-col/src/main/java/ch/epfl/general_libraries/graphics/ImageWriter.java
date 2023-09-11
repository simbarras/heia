package ch.epfl.general_libraries.graphics;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class ImageWriter {

	
	public static void writeImageInFilePNG(String fileName, BufferedImage im) {
		OutputStream s;
		try {
			s = new java.io.FileOutputStream(fileName+".png");
			writeImagePNGtostream(s, im);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeImagePNGtostream(OutputStream s, BufferedImage im) throws IOException {
		javax.imageio.ImageIO.write(im, "png", s);
		s.close();
	}
}
