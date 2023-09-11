package ch.epfl.general_libraries.graphics.pcolor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


import ch.epfl.general_libraries.graphics.ColorMap;


public class PcolorPainter {
	
	public Dimension paintToGraphics(Graphics _g, Dimension d, double[][] data, int[][] dataI, ColorMap cmap, GridPainter grid, boolean relative) {	
		
		int width = (int)d.getWidth();
		int height = (int)d.getHeight();
		
		Graphics2D g = (Graphics2D) _g;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.DARK_GRAY);
		
		int pixelWidth;
		int pixelHeight;
		Dimension dim;
		
		if (data != null) {
			pixelWidth = width/data.length;
			pixelHeight = height/data[0].length;
			
			int size = cmap.size()-1;			
			for (int i = 0 ; i < data.length ; i++) {
				int jdim = data[0].length;
				for (int j = 0 ; j < jdim ; j++) {
					int param2;
					if (relative) {
						param2 = (i+j)%jdim;
					} else {
						param2 = j;
					}
					if (data[i][(i+j)%jdim] < 0 || data[i][param2] > 1) {
						System.out.println("Not normalised");
						return null;
					}
					if (Double.isNaN(data[i][param2])) {
						g.setColor(Color.WHITE);
					} else {
						int col = (int)(data[i][param2]*size);
						g.setColor(cmap.getColor(size - col));
					}
					
					g.fillRect(i*pixelWidth, j*pixelHeight, pixelWidth, pixelHeight);
				}
			}
			grid.paintToGraphics(g, pixelWidth, pixelHeight, Math.max(data.length, data[0].length));	
			dim = new Dimension(data.length*pixelWidth, data[0].length*pixelHeight);
		} else {
			pixelWidth = width/dataI.length;
			pixelHeight = height/dataI[0].length;
			
			int size = cmap.size()-1;
			
			for (int i = 0 ; i < dataI.length ; i++) {
				for (int j = 0 ; j < dataI[0].length ; j++) {
					if (dataI[i][j] < 0 || dataI[i][j] > 1) {
						System.out.println("Not normalised");
						return null;
					}
					int col = (int)(dataI[i][j]*size);
					g.setColor(cmap.getColor(size - col));
					g.fillRect(i*pixelWidth, j*pixelHeight, pixelWidth, pixelHeight);
				}
			}
			grid.paintToGraphics(g, pixelWidth, pixelHeight, Math.max(dataI.length, dataI[0].length));	
			dim = new Dimension(dataI.length*pixelWidth, dataI[0].length*pixelHeight);
		}
		return dim;
	}

}
