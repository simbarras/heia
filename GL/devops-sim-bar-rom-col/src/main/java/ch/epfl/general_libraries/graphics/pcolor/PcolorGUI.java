package ch.epfl.general_libraries.graphics.pcolor;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.gui.AskQuestion;
import ch.epfl.general_libraries.math.MoreMaths;
import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.Normaliser;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.LinkContainer;

public class PcolorGUI {
	
	public static interface Adapter<T> {
		
		public int adapt(T val);
	}

	public static void main(String[] args) {
		
		double t = 1;
		
		double[][] data = new double[32][128];
		for (int i = 0 ; i < data.length ; i++) {
			for (int j = 0 ; j < data[0].length ; j++) {
				if (i == j+1) {
					data[i][j] = t;
					t -= (1d/32d);
				}
			} 
		}
		
		
		PcolorGUI gui = new PcolorGUI(data);
		gui.showInFrame();
	}
	
	Window window;
	ColorMap choosenCmap = ColorMap.getSSTMap();
	
	JComponent paintedComponent;
	Dimension dim;
	int xmult;
	int ymult;
	double[][] data;
	int[][] dataI;
	String title;
	
	private PcolorPainter painter = new PcolorPainter();
	private GridPainter grid;
	private boolean relative = false;
	
	public PcolorGUI(boolean[][] in) {
		this(Matrix.toDouble(in));
	}
	
	public <T> PcolorGUI(T[][] t, Adapter<T> adapt) {
		data = new double[t.length][t[0].length];
		for (int i = 0 ; i < t.length ; i++) {
			for (int j = 0 ; j < t[i].length ; j++) {
				data[i][j] = adapt.adapt(t[i][j]);
			}
		}
		data = Matrix.normalize(data);
		grid = new GridPainter();
	}
	
	public PcolorGUI(String string, int[][] incidenceMatrix) {
		this(string, incidenceMatrix, new GridPainter());
	}	
	
	public PcolorGUI(final double[][] data) {
		this("", data);
	}	

	public PcolorGUI(String string, double[][] intensity) {
		this(string, intensity, new GridPainter());
	}
	
	public PcolorGUI(final String title, final int[][] dataI, final GridPainter grid) {
		this.grid = grid;
		this.dataI = dataI;
		this.title = title;
	}	
	
	public PcolorGUI(final String title, final double[][] data, final GridPainter grid) {
		this.grid = grid;
		this.data = data;
		this.title = title;
	}
	
	
	
	private void initPaintedComponent() {
		paintedComponent = new JComponent() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paintComponent(Graphics g) {
				dim = painter.paintToGraphics(g, this.getSize(), data, dataI, choosenCmap, grid, relative);
				if (data != null) {
					xmult = (int)Math.max(1, dim.getWidth() / data.length);
					ymult = (int)Math.max(1, dim.getHeight() / data[0].length);
				}
			}
		};
		
		paintedComponent.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = (e.getX() / xmult);
				int y = (e.getY() / ymult);
				
				if (data != null) {
					if (x >= data.length) return;
					if (y >= data[0].length) return;
					paintedComponent.setToolTipText("x:"+ x + " y:" + y + " | " + data[x][y]);
				} else {
					if (x >= dataI.length) return;
					if (y >= dataI[0].length) return;
					paintedComponent.setToolTipText("x:"+ x + " y:" + y + " | " + dataI[x][y]);
				}
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		paintedComponent.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}		
			@Override
			public void mousePressed(MouseEvent e) {}			
			@Override
			public void mouseExited(MouseEvent e) {}		
			@Override
			public void mouseEntered(MouseEvent e) {}			
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu menu = new JPopupMenu();
				JMenuItem border = new JMenuItem("Show border");
				menu.add(border);
				border.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						grid.mode++;
						paintedComponent.repaint();
						
					}
				});
				JMenuItem save = new JMenuItem("saveImage");
				menu.add(save);
				save.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						if (dim != null) {
							BufferedImage bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
							Graphics g = bi.getGraphics();
							painter.paintToGraphics(g, dim, data, dataI, choosenCmap, grid, relative);								try {
							ImageIO.write((RenderedImage)bi, "png", new File(AskQuestion.askString("Filename?")+".png"));
							} catch (IOException eio) {
									// TODO Auto-generated catch block
								eio.printStackTrace();
							}
						}
					}
				});
				
				JMenuItem setSize = new JMenuItem("set Size");
				menu.add(setSize);
				setSize.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						window.setSize(data.length*8, data[0].length*8 +22);
					}
				});				
				
				JMenu colorMap = new JMenu("colorMap");
				menu.add(colorMap);
				
				JMenuItem gray = new JMenuItem("Gray scale");
				colorMap.add(gray);
				gray.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						choosenCmap = ColorMap.getGrayScale();
						repaint();
					}
				});	
				
				JMenuItem defaultCmap = new JMenuItem("Default");
				colorMap.add(defaultCmap);
				defaultCmap.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						choosenCmap = ColorMap.getSSTMap();
						repaint();
					}
				});	
				
				JMenuItem saveGraph = new JMenuItem("Save equivalent graph");
				menu.add(saveGraph);
				saveGraph.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
						agh.activateMainDataHandler();
						agh.newLayer("physical");
						for (int i = 0 ; i < data.length ; i++) {
							agh.newNode();
						}
						for (int i = 0 ; i < data.length ; i++) {
							for (int j = 0 ; j < data[0].length ; j++) {
								if (data[i][j] > 0) {
									LinkContainer lc = agh.newLink(i, j);
									lc.attribute("weigth").setValue(data[i][j]);
								}
							}
						}
						try {
							agh.saveNetwork("fromPcolor.xml");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});		
				
				JMenu displayType = new JMenu("Display");
				menu.add(displayType);
				
				JMenuItem abs = new JMenuItem("Absolute");
				displayType.add(abs);
				abs.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						relative = false;
						repaint();
					}
				});	
				
				JMenuItem rel = new JMenuItem("Relative");
				displayType.add(rel);
				rel.addActionListener(new ActionListener() {	
					@Override
					public void actionPerformed(ActionEvent e) {
						relative = true;
						repaint();
					}
				});				
				
				menu.show(paintedComponent, e.getX(), e.getY());
				

			}
		});
	}

	public void repaint() {
		paintedComponent.repaint();
	}

	public void showInFrame(String title) {
		if (title == null) {
			title = this.title;
		}
		JFrame f = new JFrame(title);	
		initPaintedComponent();
		f.setSize(data.length*xmult, data[0].length*ymult+22);			
		f.setContentPane(paintedComponent);
		f.setVisible(true);		
		window = f;
	}
	
	public void showInFrame() {
		showInFrame("");
	}
	
	public void setYMult(int mult) {
		this.ymult = mult;
	}
	
	public void setXMult(int mult) {
		this.xmult = mult;
	}	

	public void showInDialog(Frame f2) {
		JDialog diag = new JDialog();
		diag.setSize(data.length*4, data[0].length*4+22);		
		initPaintedComponent();
		diag.setContentPane(paintedComponent);
		diag.setVisible(true);		
		window = diag;		
	}

	public void close() {
		window.setVisible(false);
	}

	public static void show(int[][] mat) {
		new PcolorGUI(Matrix.normalize(mat)).showInFrame();;
	}

	
		
	
}
