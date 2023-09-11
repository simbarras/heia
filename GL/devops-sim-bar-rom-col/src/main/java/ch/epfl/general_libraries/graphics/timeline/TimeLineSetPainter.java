package ch.epfl.general_libraries.graphics.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.general_libraries.graphics.timeline.TimeLine.Job;
import ch.epfl.general_libraries.graphics.timeline.TimeLineSet.Communication;
import ch.epfl.general_libraries.simulation.Time;

public class TimeLineSetPainter {
	
	private int paintArrowsType = 2;
	private int arrowType = 0;
	private int jobDescType = 0;
	
	public void setPaintArrowsType(int i) {
		paintArrowsType = i;
	}
	
	public void setArrowType(int i) {
		arrowType = i;
	}
	
	public void setJobDescType(int j) {
		jobDescType = j;
	}	
	
	private transient int visibleUp;
	private transient int visibleDown;

    FontMetrics arial10FontMetrics;
    FontMetrics genevaFontMetrics;
    Time timeUnit;
    int yheightPerLine; 
    Font arial10 = new java.awt.Font("Arial", java.awt.Font.BOLD, 10);
    Font geneva = new java.awt.Font("Geneva", java.awt.Font.BOLD, 12);
	
	public void paintToGraphics(Graphics _g, Dimension d, TimeLineSet lines, double startTime, double endTime, int visibleUp, int visibleDown) {
		Graphics2D g = (Graphics2D) _g;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i = 0 ; i < lines.size() ; i++) {
			lines.get(i).displayedIndex = i;
		}
		g.setFont(geneva);
		genevaFontMetrics = g.getFontMetrics();
		g.setFont(arial10);
		arial10FontMetrics = g.getFontMetrics();
		
		
		timeUnit = lines.getTimeUnit();
		yheightPerLine = (int)((d.getHeight()-20)/(visibleDown - visibleUp));
		int xOffset = getXOffset();
		int yOffset = 0;
		HashMap<TimeLine, Integer> map = new HashMap<TimeLine, Integer>();
		for (int i = visibleUp ; i < visibleDown ; i++) {
			TimeLine line = lines.get(i);
			paintLine(g, line, yheightPerLine, xOffset, yOffset, startTime, endTime, (int)d.getWidth());
			map.put(line, yOffset+(yheightPerLine/2));
			yOffset += yheightPerLine;
		}
		this.visibleUp = visibleUp;
		this.visibleDown = visibleDown;
		if (paintArrowsType > 1) {
		// must be put in function of a boolean
			for (int i = visibleDown ; i < lines.size() ; i++) {
				map.put(lines.get(i), yOffset+(yheightPerLine/2));
				yOffset += yheightPerLine;
			}
			yOffset = 0;
			for (int i = visibleUp -1 ; i >= 0 ; i--) {	
				yOffset -= yheightPerLine;	
				map.put(lines.get(i),  yOffset+(yheightPerLine/2));
			}	
		}
		if (paintArrowsType > 0) {
			if (lines.isMatched()) {
				//if (arrowType % 2 == 1) {
			        g.setFont(geneva);
			//	}
				for (Communication c : lines.getCommunications()) {
					paintCom(g, c, map, startTime, endTime, (int)d.getWidth(), xOffset);
				}
				g.setFont(arial10);
			}
		}		
		paintRule(g, xOffset, (int) d.getWidth(), (int)d.getHeight(), startTime, endTime, lines);
	}
	
	private void paintRule(Graphics2D g, int xOffset, int widthInPixel, int heightInPixel, double startTime, double endTime, TimeLineSet lines) {
		g.setColor(Color.DARK_GRAY);
		int yOffset = yheightPerLine*(visibleDown-visibleUp);
		g.fillRect(0, yOffset, widthInPixel, heightInPixel);				
		g.setColor(Color.WHITE);		
		// rule --> about one tick each 40 pixels
		int nbTicks = widthInPixel / 40;
		double diffTime = endTime - startTime;
		double timePerTick = (double)diffTime / (double)nbTicks;
		int realTimePerTick = (int) Math.pow(10,1+Math.floor(Math.log10(timePerTick)));
		if (realTimePerTick <= 0) return;
		long position2 = (int)startTime % realTimePerTick;	
		long position = (int)startTime - position2 + realTimePerTick;
		
		boolean x10 = (nbTicks > 10);
		
		double referenceUnit = x10 ? position*10 : position;
		
		Time pos1 = new Time(referenceUnit, timeUnit.getUnit()).multiply(1000);		
		String unit = pos1.getAdequateUnit();
	
		
		double val = pos1.getAdequateUnitValue();
		double ratio = val/referenceUnit;
		
		String lastS = "";
		while (position < endTime) {
			double realPos = position*ratio;
			String newS = "";
			if (realPos - (int)realPos == 0) {
				newS = (int)realPos + unit;
			} else {
				newS = String.format("%1.2f", realPos) + unit;	
			}
			if (newS.equals(lastS)) {
				position += realTimePerTick;				
				continue;				
			}
			lastS = newS;
			double d = convertToComp(startTime, endTime, widthInPixel, position, xOffset);			
			g.drawString(lastS, (int) d+2, yOffset+10);
			g.drawLine((int) d, yOffset, (int) d, yOffset+30);
			position += realTimePerTick;
		}
		g.setColor(Color.DARK_GRAY);	
		g.fillRect(0, 0, xOffset, heightInPixel);
		g.setColor(Color.BLACK);
		g.fillRect(xOffset-4, 0, 4, heightInPixel);
		int y = yheightPerLine;
		for (int i = visibleUp ; i < visibleDown ; i++) {
			g.drawLine(0, y, xOffset, y);
			y += yheightPerLine;
		}
		g.setColor(Color.WHITE);
		if (yheightPerLine >= 9) {
			y = yheightPerLine;
			for (int i = visibleUp ; i < visibleDown ; i++) {
				String title = lines.get(i).title;
				if (title != null)
					g.drawString(lines.get(i).title, 2, y-3);
				y += yheightPerLine;
			}
		}
	}
	

	
	private void paintCom(Graphics2D g, Communication c, HashMap<TimeLine, Integer> map, double startTime, double endTime, int componentWidth, int xOffset) {
		int ind1 = c.start.displayedIndex;
		int ind2 = c.end.displayedIndex;	
		if (paintArrowsType == 1) {
			if (((ind1 >= visibleUp && ind1 < visibleDown) && (ind2 >= visibleUp && ind2 < visibleDown)) == false) return;
		}
		if (paintArrowsType == 2) {
			if (((ind1 >= visibleUp && ind1 < visibleDown) || (ind2 >= visibleUp && ind2 < visibleDown)) == false) return;
		}
		int xStart = (int) convertToComp(startTime, endTime, componentWidth, c.startTime, xOffset);
		int xEnd = (int) convertToComp(startTime, endTime, componentWidth, c.endTime, xOffset);
		Integer i1 = map.get(c.start);
		Integer i2 = map.get(c.end);
		String text = "";
		switch (arrowType) {
		case 0:
			break;
		case 1: 
			text = c.size;
			break;
		case 2:
			text = c.desc;
			break;
		case 3:
			if (c.desc != null) {
				text = c.desc + "\n" + c.size;
			} else {
				text = c.reference + "\n" + c.size;
			}
		}
		if (i1 == null || i2 == null) return;
		drawArrow(g, xStart,i1 , xEnd, i2, text, arrowType == 3);
	}
	
	private void drawArrow(Graphics2D g1, int x1, int y1, int x2, int y2, String id, boolean twoLines) {
		g1.setColor(Color.BLACK);
		Graphics2D g = (Graphics2D)g1.create();
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        if (id != null && id.equals("") == false) {
        	int wi = 0;
        	if (twoLines) {
        		String[] ff = id.split("\n");
        		for (int i = 0 ; i < ff.length ; i++) {
        			wi = Math.max(wi, genevaFontMetrics.stringWidth(ff[i]));
        		}
        	} else {
        		wi = genevaFontMetrics.stringWidth(id);
        	}
        	int he = twoLines ? 24 : 12;
        	g.fillRect(len/2 - wi/2 - 2, -he/2 - 2, wi + 4, he + 4);
        	g.setColor(Color.WHITE);
        	g.fillRect(len/2 - wi/2 - 1, -he/2 - 1, wi + 2, he + 2);
        	g.setColor(Color.BLACK);
        	if (twoLines) {
        		String[] ff = id.split("\n");
        		int a = -he/2;
        		for (int i = 0 ; i < ff.length ; i++) {
        			g.drawString(ff[i], len/2 - wi/2, a+ he/2-1);
        			a += he/2;
        		}
        		
        		
        	} else {
        		g.drawString(id, len/2 - wi/2, he/2-1);
        	}
        }
        g.fillPolygon(new int[] {len, len-8, len-8, len},
                      new int[] {0, -4, 4, 0}, 4);
        
	}
	
	public double convertToComp(double start, double end, int componentWidth, double pointToFind, int xOffset) {
		double timeWidth = (end-start);
		return xOffset + ((componentWidth-xOffset)*(pointToFind-start))/timeWidth;
	}
	
	private void paintLine(Graphics2D g, TimeLine line, int height, int xOffset, int yOffset, double startTime, double endTime, int remainingWidth) {
		
		g.setColor(Color.WHITE);
		g.fillRect(xOffset, yOffset+2, remainingWidth, height-2);
		g.setColor(Color.BLACK);
		g.drawLine(xOffset, yOffset+height/2, remainingWidth, yOffset+height/2);
		ArrayList<Job> jobs = line.getJobPhases();
		int nbJobs = jobs.size();
		int painted = 0;
		for (int i = 0 ; i < 3 ; i++) {
			for (int j = 0 ; j < nbJobs ; j++) {
				Job job = jobs.get(j);
				if (job.pass != i) {
					continue;
				} else {
					paintJob(g, line, job, startTime, endTime, remainingWidth, xOffset, yOffset, height);
					painted++;
				}
			}
			if (painted == nbJobs) break;
		}
		for (Double dd : line.getReads()) {
			int d = (int) convertToComp(startTime, endTime, remainingWidth, dd, xOffset);
			int lineHeight;
			if (height < 10) {
				lineHeight = 4;
			} else if (height > 90) {
				lineHeight = 15;
			} else {
				lineHeight = height/6;
			}
			g.setPaint(Color.BLACK);			
			g.fillRect(d-2, yOffset+(height/2)-lineHeight, 2, lineHeight*2);
			g.fillArc(d-3, yOffset+(height/2)-3, 6, 6, 90, 180);
		}
		/*for (Map<K, V>.Entry<Integer, Double> ent : line.getReceiveEvents()) {
			
		}*/
	}
	

	
	private void paintJob(Graphics2D g, TimeLine line, TimeLine.Job job, double startTime, double endTime, int remainingWidth, int xOffset, int yOffset, int height) {

		double jobStart = job.start;
		double jobEnd = job.end;
		boolean jobFullyBefore = (jobEnd < startTime);
		boolean jobFullyAfter = (jobStart > endTime);
		if (jobFullyAfter == false && jobFullyBefore == false) {
			int xStart = (int) convertToComp(startTime, endTime, remainingWidth, jobStart, xOffset);
			int xEnd = (int) convertToComp(startTime, endTime, remainingWidth, jobEnd, xOffset);
			int yStart = yOffset;
			int yEnd = yOffset+height;
			Color colorToUse = job.color;
			if (colorToUse == null) {
				colorToUse = line.specialColor;
			}
			Paint gradientPaint;
			if (job.type != null) {
				gradientPaint = job.type.getPaint(yStart, yEnd);
			} else {
				gradientPaint = new GradientPaint(0, yStart, colorToUse, 0, yEnd, colorToUse);
			}	
			g.setPaint(gradientPaint);
	        g.fillRect(xStart, yStart+1, xEnd-xStart, yEnd-yStart-5);
	        g.setColor(Color.BLACK);
	        g.drawRect(xStart, yStart+1, xEnd-xStart, yEnd-yStart-5);
	        String desc;
	        if (this.jobDescType == 1) {
	        	desc = " " + job.desc;
	        } else {
	        	desc = "";
	        }
	 
	        if (yEnd-yStart > 16) {
	            FontMetrics fm =  g.getFontMetrics(g.getFont());
	        	if ( fm.stringWidth(desc) < xEnd-xStart) {
		        	String[] ss = desc.split("\n");
		        	int yoff = 12;
		        	for (String s : ss) {
		        		g.drawString(s, xStart+1, yStart+yoff);
		        		yoff += 12;
		        	}
	        	}
	        }			
		}		
	}

	public static int getXOffset() {
		return 60;
	}



}