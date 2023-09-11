package ch.epfl.general_libraries.graphics.timeline;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import ch.epfl.general_libraries.gui.AskQuestion;
import ch.epfl.general_libraries.gui.ReturnKeyListener;

public class TimeLineGUI implements AdjustmentListener, MouseWheelListener, MouseMotionListener, KeyListener, MouseListener {
	
	public static void main(String[] args) {
		TimeLine tl = new TimeLine();
		tl.addJobPhase(100, 200, TimeLine.EnumType.OK ,"phase1");
		tl.addJobPhase(300, 450, TimeLine.EnumType.OK,"phase2");
		tl.addRead(223);
		tl.addSend(120, 0, 23 /*bits*/);
		
		TimeLine tl2 = new TimeLine();
		tl2.addJobPhase(10, 100, TimeLine.EnumType.OK,"phase1");
		tl2.addJobPhase(200, 470, TimeLine.EnumType.ERROR,"phase2");
		tl2.addReceive(180, 0);
		
		final TimeLineSet l = new TimeLineSet();
		l.add(tl);
		l.add(tl2);
		
		l.matchCommunications();
		
		
		new TimeLineGUI(l);
	}
	
	final JFrame f;
	JComponent paintedComponent;
	private JScrollBar horizontalSBar = null;
	private JScrollBar verticalSBar = null;
	private TimeLineSetPainter painter = new TimeLineSetPainter();
	private TimeLineSet timeLineSet;
	private int xLimitLeft;
	private int xLimitRight;
	
	private int visibleLeft;
	private int visibleRight;
	
	private int yLimitMax;
	private int visibleUp;
	private int visibleDown;
	
	private boolean ctrlPressed = false;
	private boolean shiftPressed = false;
	

	public TimeLineGUI(final TimeLineSet l) {
		f = new JFrame("Timeline GUI");
		timeLineSet = l;
		f.setSize(700, 400);
		xLimitLeft = 0;
		xLimitRight = (int) timeLineSet.getLastEventTime();
		yLimitMax = timeLineSet.size();
		visibleDown = 32;		
		
		visibleLeft = 0;
		visibleRight = xLimitRight;
		
		paintedComponent = new JComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {
				timeLineSet.matchCommunicationsAsync();
				painter.paintToGraphics(g, this.getSize(), timeLineSet, visibleLeft, visibleRight, visibleUp, visibleDown);
			}		
		};
		
		paintedComponent.addMouseWheelListener(this);
		paintedComponent.addMouseMotionListener(this);
		paintedComponent.addMouseListener(this);
		f.addKeyListener(this);
		
		JMenuBar menuBar = new JMenuBar();
		f.setJMenuBar(menuBar);
		
		JMenu actionsMenu = new JMenu("Actions");
		menuBar.add(actionsMenu);
		
		addDefineTimeMenuItem(actionsMenu, f);
		addDefineLineHeightItem(actionsMenu, f);	
		actionsMenu.addSeparator();
		addShowMessageSizesMenuItem(actionsMenu);
		addShowArrowsMenuItem(actionsMenu);
		addShowJobDescMenuItem(actionsMenu);
		actionsMenu.addSeparator();		
		addPaintToFileMenuItem(actionsMenu, f);
		
		horizontalSBar = new JScrollBar(Adjustable.HORIZONTAL);
		verticalSBar = new JScrollBar(Adjustable.VERTICAL);
		JPanel localPanel = new JPanel();
		localPanel.setLayout(new BorderLayout());
		localPanel.add(paintedComponent,BorderLayout.CENTER);
		localPanel.add(horizontalSBar, BorderLayout.SOUTH);
		localPanel.add(verticalSBar, BorderLayout.EAST);
		horizontalSBar.setEnabled(true);
		verticalSBar.setEnabled(true);
		horizontalSBar.addAdjustmentListener(this);
		verticalSBar.addAdjustmentListener(this);
		horizontalSBar.setValues(visibleLeft, visibleRight-visibleLeft, xLimitLeft, xLimitRight);
		verticalSBar.setValues(visibleUp, visibleDown - visibleUp, 0, yLimitMax);
		
		f.setContentPane(localPanel);
		f.setVisible(true);		
	}



	private void addPaintToFileMenuItem(JMenu actionsMenu, JFrame f2) {
		// TODO Auto-generated method stub
		JMenuItem item = new JMenuItem("Save actual image to file...");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int w = paintedComponent.getWidth();
				int h = paintedComponent.getHeight();
				BufferedImage bi = new
				    BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics g = bi.getGraphics();
				painter.paintToGraphics(g, paintedComponent.getSize(), timeLineSet, visibleLeft, visibleRight, visibleUp, visibleDown);
				try {
					ImageIO.write((RenderedImage)bi, "png", new File(AskQuestion.askString("Filename?")+".png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
		actionsMenu.add(item);
	}
	
	private void addShowJobDescMenuItem(JMenu actionsMenu) {
		final JMenu arrowType = new JMenu("Job labels...");
		actionsMenu.add(arrowType);
		
		ButtonGroup group = new ButtonGroup();
		
		String[] texts = new String[]{"None", "All"};
		for (int i = 0 ; i < 2 ; i++) {
			final int j = i;
			final JRadioButtonMenuItem noArrows = new JRadioButtonMenuItem(texts[i]);
			group.add(noArrows);		
			noArrows.addActionListener(new ActionListener()  {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (noArrows.isSelected()) {
						painter.setJobDescType(j);
						paintedComponent.repaint();
					}
					
				}	
			});
			if (i == 1) {
				noArrows.doClick();
			}			
			arrowType.add(noArrows);
		}		
	}	

	private void addShowMessageSizesMenuItem(JMenu actionsMenu) {
		final JMenu arrowType = new JMenu("Communications labels...");
		actionsMenu.add(arrowType);
		
		ButtonGroup group = new ButtonGroup();
		
		String[] texts = new String[]{"None", "Size", "Description", "Both"};
		for (int i = 0 ; i < 4 ; i++) {
			final int j = i;
			final JRadioButtonMenuItem noArrows = new JRadioButtonMenuItem(texts[i]);
			group.add(noArrows);		
			noArrows.addActionListener(new ActionListener()  {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (noArrows.isSelected()) {
						painter.setArrowType(j);
						paintedComponent.repaint();
					}
					
				}	
			});
			if (i == 2) {
				noArrows.doClick();
			}			
			arrowType.add(noArrows);
		}
		
		
	}
	
	private void addShowArrowsMenuItem(JMenu actionsMenu) {
		final JMenu arrowType = new JMenu("See communications...");
		actionsMenu.add(arrowType);
		
		ButtonGroup group = new ButtonGroup();
		
		String[] texts = new String[]{"None", "Only starting and ending in current display", "Either starting or ending in current display", "All arrows"};
		for (int i = 0 ; i < 4 ; i++) {
			final int j = i;
			final JRadioButtonMenuItem noArrows = new JRadioButtonMenuItem(texts[i]);
			group.add(noArrows);
		
			noArrows.addActionListener(new ActionListener()  {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (noArrows.isSelected()) {
						painter.setPaintArrowsType(j);
						paintedComponent.repaint();
					}
					
				}	
			});
			if (i == 3) {
				noArrows.doClick();
			}			
			arrowType.add(noArrows);
		}	
	}	


	private void addDefineTimeMenuItem(JMenu actionsMenu, final JFrame parent) {
		final JMenuItem item = new JMenuItem("Set time interval");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JDialog diag = new JDialog(parent, true);
				diag.setLayout(new FlowLayout());
				diag.add(new JLabel("Range:"));
				final JTextField f1 = new JTextField(10);
				final JTextField f2 = new JTextField(10);
				f1.setText(visibleLeft+"");
				f2.setText(visibleRight+"");
				diag.add(f1);
				diag.add(new JLabel("-"));
				diag.add(f2);
				JButton ok = new JButton("OK");
				diag.add(ok);
				f1.addKeyListener(new ReturnKeyListener(ok));
				f2.addKeyListener(new ReturnKeyListener(ok));				
				
				ok.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						diag.setVisible(false);
						int left = Integer.parseInt(f1.getText());
						int right = Integer.parseInt(f2.getText());
						horizontalSBar.setValues(left, right-left, xLimitLeft, (right > xLimitRight) ? right : xLimitRight);
						paintedComponent.repaint();
					}
				});			
				diag.setSize(new Dimension(300, 80));
				diag.setVisible(true);	
			}
		});
		actionsMenu.add(item);	
	}
	
	private void addDefineLineHeightItem(JMenu actionsMenu, final JFrame parent) {
		final JMenuItem item = new JMenuItem("Set number of pixels per line");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JDialog diag = new JDialog(parent, true);
				diag.setLayout(new FlowLayout());
				diag.add(new JLabel("Height:"));
				final JTextField f1 = new JTextField(10);
				f1.setText(painter.yheightPerLine + "");
				
				diag.add(f1);
				final JButton ok = new JButton("OK");
				diag.add(ok);
				ok.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						diag.setVisible(false);
						int height = Integer.parseInt(f1.getText());
						int componentHeight = painter.yheightPerLine * (visibleDown - visibleUp);
						int newComponentHeight = height * (visibleDown - visibleUp);
						int diff = componentHeight - newComponentHeight;
						
						Rectangle bounds = parent.getBounds();
						bounds.height -= diff;
						parent.setBounds(bounds);
						paintedComponent.repaint();
					}
				});
			//	diag.setFocusable(true);
				diag.setSize(new Dimension(300, 80));
				f1.addKeyListener(new ReturnKeyListener(ok));
				diag.setVisible(true);	
				SwingUtilities.invokeLater(new Runnable() {
					   @Override
					   public void run() {
					      f1.requestFocus();			
					   }
					 
				});
				
			}
		});
		actionsMenu.add(item);
	}	


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getAdjustable().equals(horizontalSBar)) {
			visibleLeft = horizontalSBar.getValue();
			visibleRight = horizontalSBar.getVisibleAmount() + visibleLeft;
			paintedComponent.repaint();
		} else if (e.getAdjustable().equals(verticalSBar)) {
			visibleUp = verticalSBar.getValue();
			visibleDown = verticalSBar.getVisibleAmount() + visibleUp;
			paintedComponent.repaint();
		}	
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrlPressed = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftPressed = true;
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		if (ctrlPressed) {
			int rot = arg0.getWheelRotation();
			if (rot > 0) {
				visibleDown = Math.min(visibleDown + rot, yLimitMax);
			} else {
				visibleDown = Math.max(1, visibleDown + rot);
			}
			verticalSBar.setValues(visibleUp, visibleDown-visibleUp, 0, yLimitMax);
		} else if (shiftPressed) {
			double factor = Math.pow(1.5,arg0.getWheelRotation());
			double actuallyDisplayed = visibleRight - visibleLeft;
		
			
			
			double newDisplayed = Math.min(actuallyDisplayed * factor, xLimitRight);
			double diff = (actuallyDisplayed - newDisplayed);
			if (arg0.getWheelRotation() == -1) {
				double newCentralPoint = (double)arg0.getX()/(double)f.getWidth();	
				if (newCentralPoint < 0.2) { 
					newCentralPoint = 0;
				}
				if (newCentralPoint > 0.8) {
					newCentralPoint = 1;
				}
				visibleLeft = (int) Math.max(0, Math.min(visibleLeft + (diff*newCentralPoint), xLimitRight-1));
				visibleRight = (int) Math.max(visibleLeft+1, Math.min(visibleRight - (diff*(1-newCentralPoint)), xLimitRight));
			} else {
				visibleLeft = (int) Math.max(0, Math.min(visibleLeft + (diff*0.5), xLimitRight-1));
				visibleRight = (int) Math.max(visibleLeft+1, Math.min(visibleRight - (diff*0.5), xLimitRight));	
				if (visibleRight - visibleLeft < newDisplayed && newDisplayed > xLimitRight*0.9) {
					visibleLeft = 0;
					visibleRight = xLimitRight;
				}
			}
			
			horizontalSBar.setValues(visibleLeft, visibleRight-visibleLeft, xLimitLeft, xLimitRight);
		} else {
			int shift = arg0.getWheelRotation();
			boolean neg = (shift < 0);
			shift = (int)Math.pow(2, Math.abs(shift));
			shift = neg ? -shift : shift;			
			if (shift < 0) {
				shift = -Math.min(visibleUp, -shift);
			} else {
				shift = Math.min(yLimitMax - visibleDown, shift);
			}
			visibleUp += shift;
			visibleDown += shift;
			verticalSBar.setValues(visibleUp, visibleDown-visibleUp, 0, yLimitMax);
		}
		paintedComponent.repaint();
	}


	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}	
	
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int xOffset = TimeLineSetPainter.getXOffset();
		
		int mouseX = e.getX() - xOffset;
		double ratio = (double) mouseX/ (double) (paintedComponent.getWidth() - xOffset);
		int ns = (int) (visibleLeft + ((visibleRight - visibleLeft)*ratio));
		
		
		
	//	int ns = (int) painter.convertToComp(visibleLeft, visibleRight, paintedComponent.getWidth(), mouseX, 80);
		
		paintedComponent.setToolTipText(""+ ns);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrlPressed = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftPressed = false;
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1) {
			shiftPressed = true;
		}
		if (e.getButton() == 3) {
			ctrlPressed = true;
		}
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == 1) {
			shiftPressed = false;
		}
		if (e.getButton() == 3) {
			ctrlPressed = false;
		}	
	}
}
