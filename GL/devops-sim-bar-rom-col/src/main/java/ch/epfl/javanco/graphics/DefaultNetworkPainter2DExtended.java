package ch.epfl.javanco.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

import ch.epfl.general_libraries.graphics.ToolBox;

public class DefaultNetworkPainter2DExtended extends DefaultNetworkPainter2D {

	public DefaultNetworkPainter2DExtended() {
	}

	/**
	 * Draws a node on the graphics g.
	 * 
	 * @param view Rectangle of the view.
	 * @param node Node to paint.
	 * @param g	Graphics where to paint the node.
	 * @param set Information about the way to draw the node.
	 * @param obs ImageObserver used to paint images.
	 */
	@Override
	protected void drawNode(PaintableNode node,
			Graphics g,
			GraphDisplayInformationSet set) {
		ImageObserver obs = null; //Could be useful to specify an ImageObserver
		NodeCoord nodePoints = computeNodeCorners(node,set);
		int x = nodePoints.x;
		int y = nodePoints.y;
		int width = nodePoints.width;
		int height = nodePoints.height;
		int imgWidth = 0;
		int imgHeight = 0;

		Image img = node.getIconImage();

		if (img == null || img.getHeight(obs) < 0 || img.getWidth(obs) < 0) {
			g.setColor(set.backgroundColor);
			// erase what is below
			g.fillOval(x, y, width, height);
			g.setColor(new Color(0,0,0,node.alpha));
			// display border
			g.fillOval(x, y, width, height);
			g.setColor(getColor(node.color, node.alpha));
			// display center
			g.fillOval(x+2, y+2, width-4, height-4);
			g.setColor(new Color(0,0,0,node.alpha));
		}
		else {
			img = scaleImage(img,width,height, 1, obs);
			imgWidth = img.getWidth(obs);
			imgHeight = img.getHeight(obs);
			Graphics2D g2 = (Graphics2D) g;

			if (GraphicalPropertiesLoader.getGraphicalPropertiesLoader().getColoredIcons() &&
					!node.color.equals(new Color(255, 255, 255))) {
				Paint oldPaint = g2.getPaint();

				Point2D center = new Point2D.Float(x+width/2f, y+height/2f);
				float radius = (Math.max(imgWidth, imgHeight)/2f) * 1.5f;

				float[] fractions = {0f, 0.7f, 1f};
				Color[] colors = {getColor(node.color, 255), getColor(node.color, 50),  getColor(node.color, 0)};
				g2.setPaint(new RadialGradientPaint(center, radius ,fractions, colors));

				g.fillOval((int)(center.getX() - radius), (int)(center.getY() - radius), (int)(radius * 2), (int)(radius * 2));
				g2.setPaint(oldPaint);
			}
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)node.alpha/(float)255));
			g.drawImage(img, x + width/2 - imgWidth/2, y + height/2 - imgHeight/2, obs);
		}

	/*	float fontHeight = modifySize(node.labelFontSize, set);
		java.awt.Font f = new java.awt.Font("Arial", java.awt.Font.BOLD, (int)fontHeight);
		g.setFont(f);
		FontMetrics fm =  g.getFontMetrics(f);*/

		
		
		drawNodeId(node, set, g, nodePoints, computeShiftDueToIcon(node, set, imgHeight));
		drawNodeLabel(node, set, g, nodePoints, computeShiftDueToIcon(node, set, imgHeight));

	//	g.drawString("" + node.id, x + width/2-fm.stringWidth("" + node.id)/2, y +  (int)(height/2. + fontHeight/2. - decalage));
	//	g.drawString("" + node.label, x + width/2-fm.stringWidth("" + node.label)/2, y + (int)(height/2. + imgHeight/2. + 2.3*fontHeight));

		//g.drawRect(x + width/2 - img.getWidth(obs)/2, y + height/2 - img.getHeight(obs)/2, img.getWidth(obs), img.getHeight(obs));
	}
	
	protected float computeShiftDueToIcon(PaintableNode node, GraphDisplayInformationSet set, int imgHeight) {
		float fontHeight = modifySize(node.labelFontSize - 3, set);
		float decalage = 0;
		if(imgHeight != 0) {
			decalage = (float)(imgHeight/2.) + fontHeight/2 + 2;
		}
		return decalage;
	}

	/**
	 * Draws an outlined node on the graphics g.
	 * 
	 * @param view Rectangle of the view.
	 * @param node Node to paint.
	 * @param g	Graphics where to paint the node.
	 * @param set Information about the way to draw the node.
	 * @param obs ImageObserver used to paint images.
	 */
	@Override
	protected void drawOutlinedNode(PaintableNode node, Graphics g, GraphDisplayInformationSet set) {
		ImageObserver obs = null; //Could be useful to specify an ImageObserver
		if (HIGHLIGHT) {
			NodeCoord nodePoints = computeNodeCorners(node,set);
			int x = nodePoints.x;
			int y = nodePoints.y;
			int width = nodePoints.width;
			int height = nodePoints.height;
			int imgWidth = 0;
			int imgHeight = 0;

			Image img = node.getIconImage();

			if (img == null || img.getHeight(obs) < 0 || img.getWidth(obs) < 0) {
				g.setColor(Color.BLACK);
				g.fillOval(x, y, width, height);
				g.setColor(ToolBox.getInverseColor(node.color));
				g.fillOval(x+1, y+1, width-2, height-2);
				g.setColor(new Color(0,0,0,node.alpha));
				/*
				g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 1+node.labelFontSize));
				g.drawString("" + node.id,x + width/2,y + height/2);
				g.drawString("" + node.label, x + node.labelRelativePositionX, y + node.labelRelativePositionY);
				 */
			}
			else {
				img = scaleImage(img,width,height, 1.3, obs);
				imgWidth = img.getWidth(obs);
				imgHeight = img.getHeight(obs);

				if (GraphicalPropertiesLoader.getGraphicalPropertiesLoader().getColoredIcons() &&
						!node.color.equals(new Color(255, 255, 255))) {
					Graphics2D g2 = (Graphics2D) g;
					Paint oldPaint = g2.getPaint();

					Point2D center = new Point2D.Float(x+width/2f, y+height/2f);
					float radius = (Math.max(imgWidth, imgHeight)/2f) * 1.5f;

					float[] fractions = {0f, 0.7f, 1f};
					Color[] colors = {getColor(node.color, 255), getColor(node.color, 50),  getColor(node.color, 0)};
					g2.setPaint(new RadialGradientPaint(center, radius ,fractions, colors));

					g.fillOval((int)(center.getX() - radius), (int)(center.getY() - radius), (int)(radius * 2), (int)(radius * 2));
					g2.setPaint(oldPaint);
				}

				g.drawImage(img, x + width/2 - imgWidth/2, y + height/2 - imgHeight/2, obs);
			}
			

			
			//float fontHeight = modifySize(node.labelFontSize - 3, set);
			drawNodeId(node, set, g, nodePoints, computeShiftDueToIcon(node, set, imgHeight));
			drawNodeLabel(node, set, g, nodePoints, computeShiftDueToIcon(node, set, imgHeight));
			
			
	/*		java.awt.Font f = getFont(node.labelFontSize, set);
			g.setFont(f);
			FontMetrics fm =  g.getFontMetrics(f);

			fontHeight = modifySize(node.labelFontSize - 3, set);
			

			g.drawString("" + node.id, x + width/2-fm.stringWidth("" + node.id)/2, y +  (int)(height/2. + fontHeight/2. - decalage));
			g.drawString("" + node.label, x + width/2-fm.stringWidth("" + node.label)/2, y + (int)(height/2. + imgHeight/2. + fontHeight));
*/
			//g.drawRect(x + width/2 - img.getWidth(obs)/2, y + height/2 - img.getHeight(obs)/2, img.getWidth(obs), img.getHeight(obs));
		} else {
			drawNode(node, g, set);
		}
	}
}
