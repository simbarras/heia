package ch.epfl.javancox.results_manager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Scrollbar;
import java.awt.Shape;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

public class ScrollableLegend extends JPanel implements AdjustmentListener, MouseWheelListener {


	private static final long serialVersionUID = 2658813867371861899L;
	private Legend legend;
	private Scrollbar scrollBar;

	public ScrollableLegend(Map<String, Paint> seriePaint, Map<String, Shape> serieShape, String shape) {
		this.setLayout(new BorderLayout());
		this.legend = new Legend(seriePaint, serieShape, shape);
		this.legend.setBackground(Color.GREEN);
		this.scrollBar = new Scrollbar(Scrollbar.VERTICAL);
		this.scrollBar.addAdjustmentListener(this);
		this.scrollBar.setMinimum(0);
		this.add(this.legend, BorderLayout.CENTER);
		this.add(this.scrollBar, BorderLayout.EAST);
		this.addMouseWheelListener(this);
		this.setMaximumSize(new Dimension(1000, 1000));
		this.setBackground(Color.BLUE);
	}


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == scrollBar){
			if (this.scrollBar.getValue() < 0) {
				this.scrollBar.setValue(0);
			}
			this.legend.setScrollVertical(-300*this.scrollBar.getValue() + 20);
			this.repaint();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.scrollBar.setValue(this.scrollBar.getValue() + e.getWheelRotation());
		if (this.scrollBar.getValue() < 0) {
			this.scrollBar.setValue(0);
		}
		this.legend.setScrollVertical(-300*this.scrollBar.getValue() + 20);
		this.repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (this.getHeight() >= this.legend.getHeight()) {
			this.scrollBar.setMinimum(0);
			this.scrollBar.setMaximum(0);
		} else {
			this.scrollBar.setMinimum(0);
			this.scrollBar.setMaximum(this.legend.getHeight()/300);
		}
		super.paint(g);
	}


	private class Legend extends JPanel {

		private static final long serialVersionUID = 4655924193308329747L;

		private Map<String, Paint> seriePaint;
		private Map<String, Shape> serieShape;
		private String shape;
		private int scrollVertical;


		public Legend(Map<String, Paint> seriePaint, Map<String, Shape> serieShape, String shape) {
			super();
			this.seriePaint = seriePaint;
			this.serieShape = serieShape;
			this.shape = shape;
			this.scrollVertical = 20;
		}

		@Override
		public void paint(Graphics g){
			super.paint(g);
			boolean shapeSelected =(!shape.equals(""));
			Graphics2D g2 = (Graphics2D)g;
			List<String> keys = new ArrayList<String>(seriePaint.keySet());
			Collections.sort(keys);
			int i=20;
			int j=this.scrollVertical;
			int legendSize = -1;

			if (shapeSelected) {
				List<String> shapeKeys = new ArrayList<String>(serieShape.keySet());
				Collections.sort(shapeKeys);
				for(String e : shapeKeys) {
					String key = shape + "= '" + e + "'";
					g2.setColor(Color.BLACK);
					g2.drawString(key, i, j);
					Shape s = serieShape.get(e);
					if (s != null) {
						if (s instanceof Ellipse2D) {
							g2.fillOval(i-8, j-8, 8, 8);
						}
						else {
							g2.translate(i-6, j-6);
							g2.fill(s);
							g2.translate(-(i-6), -(j-6));
						}
					}
					i = 20;
					j += 20;
					for(Entry<String, Paint> ep : seriePaint.entrySet()) {
						if (ep.getKey().contains(key)) {
							String[] keySet = ep.getKey().split(" AND ");
							String newKey = "";
							for(String k : keySet) {
								if(!k.equals(key)) {
									if(!newKey.equals("")) {
										newKey += " AND ";
									}
									newKey += k;
								}
							}
							g2.setColor((Color)seriePaint.get(ep.getKey()));
							g2.drawString(newKey, i, j);
							if (legendSize < 0) {
								legendSize = 6* newKey.length() + 5;
							}
							i += legendSize;
							if (i + legendSize>this.getWidth()) {
								i = 20;
								j += 20;
								if (j > this.getHeight()) {
									this.setSize(this.getWidth(), j+20);
								}
							}
						}
					}
					i = 20;
					j += 20;
				}
			} else {
				for(String k : keys) {
					g2.setColor((Color)seriePaint.get(k));
					g2.drawString(k, i, j);
					Shape s = (Shape)serieShape.values().toArray()[0];
					if (s != null) {
						if (s instanceof Ellipse2D) {
							g2.fillOval(i-8, j-8, 8, 8);
						}
						else {
							g2.translate(i-6, j-6);
							g2.fill(s);
							g2.translate(-(i-6), -(j-6));
						}
					}
					if(legendSize < 0) {
						legendSize = 6*k.length() + 5;
					}
					i += legendSize;
					if (i + legendSize>this.getWidth()) {
						i = 20;
						j += 20;
						if (j > this.getHeight()) {
							this.setSize(this.getWidth(), j+20);
						}
					}
				}
			}
		}

		public void setScrollVertical(int sv) {
			this.scrollVertical = sv;
		}
	}

}
