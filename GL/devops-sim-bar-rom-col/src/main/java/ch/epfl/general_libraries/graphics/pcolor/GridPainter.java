package ch.epfl.general_libraries.graphics.pcolor;

import java.awt.Color;
import java.awt.Graphics;

public class GridPainter {
	
	public int mode;
	
	public void paintToGraphics(Graphics g, int pixelWidth, int pixelHeight, int size) {
		int border = 3;
		
		if (size > 25) border = 2;
		if (size > 50) border = 1;
		if (size > 100) border = 0;
		if (mode % 3 == 0) border = 0;
		if (mode % 3 == 1) border = 1;
		
		g.setColor(Color.WHITE);
		for (int i = 1 ; i < size ; i++) {
			g.fillRect((i*pixelWidth), 0, border, size*pixelHeight);
			g.fillRect(0, (i*pixelHeight), size*pixelWidth, border);			
		}
		
		
	}
}