package ch.epfl.general_libraries.graphics;

import java.awt.Color;

public class ColorMap {
	
	private static ColorMap defaultDarkTones = getDarkTonesDefaultMap();

	private int[] map;

	private ColorMap() {
	}
	
	private ColorMap(double[][] d) {
		map = new int[d.length];
		for (int i = 0 ; i < map.length ; i++) {
			int red = (int)(d[i][0] * 255);
			int green = (int)(d[i][1] * 255);
			int blue = (int)(d[i][2] * 255);
			
			int col = red << 16;
			int col2 = green << 8;
			
			
			
			map[i] = col + col2 + blue;
		}
	}
	
	public static String getRandomColorAsString() {
		return (int)(Math.random() * 255) + "," + (int)(Math.random() * 255) + "," + (int)(Math.random() * 255);
	}
	
	public static Color getRandomColor() {
		return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
	}
	
	public static Color getLighterTone(Color c, int totalTones, int index) {
		double red = (255-c.getRed());
		double green = (255-c.getGreen());
		double blue = (255-c.getBlue());
		double coeff = (double)(totalTones - index)/(double)totalTones;
		return new Color(255 - (int)(red*coeff), 255 - (int)(green*coeff), 255 - (int)(coeff*blue), c.getAlpha());
	}
	
	public static Color getDarkerTone(Color c, int totalTones, int index) {
		double red = c.getRed();
		double green = c.getGreen();
		double blue = c.getBlue();
		double coeff = (double)(totalTones - index)/(double)totalTones;
		return new Color((int)(red*coeff), (int)(green*coeff), (int)(coeff*blue));
	}	

	public Color getColor(int index) {
		return new Color(map[index % map.length]);
	}
	
	public Color getColor(int index, float alpha) {
		int tot = (int)(alpha*255) * 0x1000000;
		tot += map[index % map.length];
		return new Color(tot, true);
	}

	public String getColorAsString(int index) {
		Color c = getColor(index);
		return toStringColor(c);
	}

	public int getColorAsInt(int index) {
		return map[index % map.length];
	}

	public int size() {
		return map.length;
	}
	
	public static ColorMap getSSTMap() {
		return new ColorMap(sstMac);
	}

	public static ColorMap getClearTonesDefaultMap() {
		return getRandomColorMap(255, 16, 0.5f, 1f, 0.05f, 15);
	}

	public static ColorMap getDarkTonesDefaultMap() {
		return getRandomColorMap(255,1,0.35f,0.5f,0.12f,7);
	}
	
	public static Color getDarkTone(int index) {
		return defaultDarkTones.getColor(index);
	}
	
	public static Color getDarkTone(int index, float alpha) {
		return defaultDarkTones.getColor(index, alpha);
	}	

	public static ColorMap getDefaultMap() {
		return getRandomColorMap(255,7,0.35f,0.75f,0.13f,6);
	}

	public static ColorMap getShortMap() {
		return getRandomColorMap(16,32,0.35f,0.75f,0.055f,15);
	}

	public static ColorMap getAnotherMap() {
		return ColorMap.getRandomColorMap(31,6,0.4f,1f,0.25f,3);
	}

	public static ColorMap getRingMap(int a, int b) {
		return getRingMap(a,b,0.9f, 0.9f);
	}

	public static ColorMap getStrongColorsMap() {
		return getRingMap(64, 360, 0.89f, 1f);
	}


	public static ColorMap getRingMap(int a, int b, float s, float brightness) {
		int i = 1;
		while (((i*a) % b) != a) {
			i++;
		}
		ColorMap map = new ColorMap();
		map.map = new int[i];
		for (int j = 0 ; j < i ; j++) {
			float chrom = ((j*a) % b) / ((float)b);
			java.awt.Color c = new java.awt.Color(java.awt.Color.HSBtoRGB(chrom, s, brightness));
			map.map[j] = c.getRGB();
		}
		return map;
	}

	public static ColorMap getRingMapPlus(int a, int b, int c) {
		int i = 2;
		while (((i*a) % b) != a) {
			i++;
		}
		i--;
		ColorMap map = new ColorMap();
		map.map = new int[i*c*2];
		int idx = 0;
		int idy = 0;
		int idz = 0;
		float sat = 1f;
		float brightness = 1f;
		while (idx < map.map.length) {
			sat = 1f - (float)idy/(float)c;
			brightness = 1f - (float)idz/(float)c;
			for (int j = 0 ; j < i ; j++) {
				float chrom = ((j*a) % b) / ((float)b);
				java.awt.Color col = new java.awt.Color(java.awt.Color.HSBtoRGB(chrom, sat , brightness + (1-brightness)*0.2f));
				map.map[idx] = (col.getRed() << 16) + (col.getGreen() << 8) + (col.getBlue());
				idx++;
			}
			if (idx % i == 0) {
				idy++;
			}
			if (idy % c == 0) {
				idy = 0;
				idz++;
			}
		}
		return map;
	}

	public static ColorMap getRandomColorMap(int size, int seed, float min_lum, float max_lum, float min_dif, int dif_range) {
		if (size <= 0) {
			throw new IllegalArgumentException("Color map size must be positive");
		}
		if (min_lum < 0 || min_lum > 1) {
			throw new IllegalArgumentException("Minimal luminosity must be between 0 and 1");
		}
		if (max_lum < 0 || max_lum > 1) {
			throw new IllegalArgumentException("Maximal luminosity must be between 0 and 1");
		}
		if (min_dif < 0 || min_dif > 1) {
			throw new IllegalArgumentException("Inter color dif must be between 0 and 1");
		}
		if (dif_range < 0 || dif_range >= size) {
			throw new IllegalArgumentException("Range of differences must be comprised between 0 and the size-1");
		}
		if (dif_range * min_dif > 0.9) {
			throw new IllegalArgumentException("Product of min_dif and dif_range must be < 0.9");
		}


		ColorMap map = new ColorMap();
		map.map = new int[size];

		int intMaxLum = (int)( max_lum * (3.08f*255));
		int intMinLum = (int)( min_lum * (3.08f*255));

		java.util.Random rand = new java.util.Random(seed);

		int[][] prevs = new int[dif_range][3];
		for (int i = 0 ; i < prevs.length ; i++) {
			for (int j = 0 ; j < prevs[i].length ; j++) {
				prevs[i][j] = Integer.MAX_VALUE;
			}
		}
		int r,g,b;
		int tot;
		for (int i = 0 ; i < size ; i++) {
			r = rand.nextInt(255);
			g = rand.nextInt(255);
			b = rand.nextInt(255);
			tot = (int)(r*1.02f)+(int)(g*1.06f)+b;
			if (!minDif(r,g,b,prevs, min_dif) || (tot < intMinLum) || (tot > intMaxLum)) {
				i--;
				continue;
			} else {
				map.map[i] = (r << 16) + (g << 8) + b;
				prevs[i%dif_range][0] = r;
				prevs[i%dif_range][1] = g;
				prevs[i%dif_range][2] = b;
			}
		}
		return map;
	}

	private static boolean minDif(int r, int g, int b, int[][] prevs, float minDif) {
		int minDifInt = (int)(minDif * (3*255));
		for (int i = 0 ; i < prevs.length ; i++) {
			int dif = Math.abs(prevs[i][0] - r) + Math.abs(prevs[i][1] - g) + Math.abs(prevs[i][2] - b);
			if (dif < minDifInt) {
				return false;
			}
		}
		return true;
	}
	
    public static double[][] sstMac = new double[][]{
    		{1.0000   ,      0   ,      0},
    		{    0.9500  ,  0.0500    ,     0},
        	{    0.9000 ,   0.1000     ,    0},
        	{    0.8500 ,   0.1500     ,    0},
            {    0.8000 ,   0.2000   ,      0},
            {    0.7500 ,   0.2500   ,      0},
            {    0.7000  ,  0.3000  ,       0},
            {    0.6500 ,   0.3500  ,       0},
            {    0.6000 ,   0.4000   ,      0},
            {    0.5500  ,  0.4500      ,   0},
            {    0.5000  ,  0.5000      ,   0},
            {    0.4500 ,   0.5500  ,       0},
    {0.4000  ,  0.6000   ,      0},
    {0.3500  ,  0.6500  ,       0},
    {0.3000  ,  0.7000   ,      0},
    {0.2500   , 0.7500  ,       0},
    {0.2000,    0.8000 ,        0},
    {0.1500  ,  0.8500   ,      0},
    {0.1000  ,  0.9000    ,     0},
    {0.0500  ,  0.9500    ,     0},
    	{     0  ,  1.0000   ,      0},
    	{     0  ,  0.9545 ,   0.0455},
    	{     0  ,  0.9091 ,   0.0909},
    	{     0 ,   0.8636   , 0.1364},
    	{     0   , 0.8182 ,   0.1818},
    	{     0  ,  0.7727  ,  0.2273},
    	{     0  ,  0.7273 ,   0.2727},
    	{     0  ,  0.6818   , 0.3182},
    	{     0 ,   0.6364   , 0.3636},
         {0   , 0.5909  ,  0.4091},
         {0 ,   0.5455,    0.4545},
         {0 ,   0.5000 ,   0.5000},
         {0   , 0.4545 ,   0.5455},
         {0  ,  0.4091 ,   0.5909},
         {0  ,  0.3636 ,   0.6364},
         {0  ,  0.3182 ,   0.6818},
         {0 ,   0.2727,    0.7273},
         {0  ,  0.2273 ,   0.7727},
         {0  ,  0.1818  ,  0.8182},
         {0   , 0.1364,    0.8636},
         {0  ,  0.0909 ,   0.9091},
         {0 ,   0.0455,    0.9545},
         {0  ,       0,    1.0000},
         {0  ,       0  ,  0.9524},
         {0    ,     0  ,  0.9048},
         {0    ,     0 ,   0.8571},
         {0    ,     0  ,  0.8095},
         {0   ,      0  ,  0.7619},
         {0    ,     0 ,   0.7143},
         {0      ,   0  ,  0.6667},
         {0   ,      0 ,   0.6190},
         {0   ,      0  ,  0.5714},
         {0     ,    0   , 0.5238},
         {0       ,  0  ,  0.4762},
         {0  ,       0 ,   0.4286},
         {0      ,   0   , 0.3810},
         {0   ,      0 ,   0.3333},
         {0     ,    0 ,   0.2857},
         {0   ,      0   , 0.2381},
         {0   ,      0  ,  0.1905},
         {0    ,     0 ,   0.1429},
         {0   ,      0 ,   0.0952},
         {0    ,     0  ,  0.0476},
         {0   ,      0   ,      0}};

	public static String toStringColor(Color c) {
		return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
	}

	public static ColorMap getGrayScale() {
		return new ColorMap() {
			
			public int size() {
				return 256;
			}
			
			public Color getColor(int index) {
				return new Color(index, index, index);
				
			}
		};
	}


	
	

}
