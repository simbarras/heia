package ch.epfl.javanco.ui.swing;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.graphics.Clickable;
import ch.epfl.javanco.graphics.Curvable;
import ch.epfl.javanco.graphics.Linkable;
import ch.epfl.javanco.graphics.Movable;
import ch.epfl.javanco.graphics.PaintableLink;
import ch.epfl.javanco.graphics.PaintableNode;
import ch.epfl.javanco.graphics.Pointable;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.ui.AbstractInteractiveGraphicalUI;
import ch.epfl.javanco.xml.GraphicalDataHandler;
import ch.epfl.javanco.xml.NetworkAttribute;
import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * Contains a default implementation of the {@link NetworkEditorUI} interface, and
 * thus provides a user interface for though client, mouse oriented. The
 * <code>DefaultGraphUIImpl</code> provides two useful methods to permit its
 * integration in the whole framework. The {@link #getDisplayPanel()} method
 * returns a "ready to add" panel, all ready linked with all mouse, keyboard or
 * other interfaces. The {@link #refresh()} method will be called if some
 * element that is displayed has been changed.<p>
 * 
 * A <code>DefaultGraphUIImpl</code> is connected (as the reference) of two
 * objects. Through a {@link NetworkGUIConnector} object, a
 * <code>DefaultGraphUIImpl</code> object will :<ul>
 * <li> get general informations about the displayed network, like which layer
 * is actually  edited or if there is an element at a given point of the screen.</li>
 * <li> trigger embedded actions, like node or link addition, scrolling, zoom etc.</li>
 * <li> "meet" the painter itself, since the painting process is decoupled from
 * the user interface itself</li></ul><p>
 *
 * To access the network data themselves, the <code>DefaultGraphUIImpl</code> uses
 * the second parameter of the constructor, the {@link GraphicalDataHandler}
 * object.
 * 
 */
public class GraphicalNetworkEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = new Logger(GraphicalNetworkEditor.class);

	private JScrollBar horizontalSBar = null;
	private JScrollBar verticalSBar = null;
	private int verticalScrollingFlag = 0;
	private int horizontalScrollingFlag = 0;
	private GraphicalNetworkDisplayer displayer;
	protected AbstractInteractiveGraphicalUI aGui;
	private Dimension lastSize = null;
	
	Rectangle cachedNodeSpace = null;
	Rectangle cachedView = null;	

	/**
	 * Default constructor.
	 * @param uiManager Used to interact with the rest of the package
	 * @param getDataHandler Used to interact with the rest of the package
	 */
	public GraphicalNetworkEditor(AbstractInteractiveGraphicalUI sui_) {
		aGui = sui_;
		displayer = new GraphicalNetworkDisplayer(sui_);
		horizontalSBar = new JScrollBar(Adjustable.HORIZONTAL);
		verticalSBar = new JScrollBar(Adjustable.VERTICAL);

		this.setLayout(new BorderLayout());
		this.add(displayer,BorderLayout.CENTER);
		this.add(horizontalSBar, BorderLayout.SOUTH);
		this.add(verticalSBar, BorderLayout.EAST);
		horizontalSBar.setEnabled(false);
		verticalSBar.setEnabled(false);

		InternalUI internalUI = getNewInternalUI();

		horizontalSBar.addAdjustmentListener(internalUI);
		verticalSBar.addAdjustmentListener(internalUI);
		this.addMouseListener(internalUI);
		this.addMouseWheelListener(internalUI);
		this.addKeyListener(internalUI);
		this.addComponentListener(internalUI);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addMouseMotionListener(internalUI);
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));		
	}
	
	public GraphicalNetworkDisplayer getDisplayer() {
		return displayer;
	}	
	
	@Override
	public void paint(Graphics g) {
		try {
			super.paint(g);
			scroll();
			updateScrollBars();
		}
		catch (Exception e) {
			logger.debug("Exception in paint", e);
		}				
	}	
	
	@Override
	public void setSize(int x, int y) {
		// intercept calls
	}

	protected InternalUI getNewInternalUI() {
		return new InternalUI(this);
	}

	private void updateScrollBars() {
		Rectangle nodeSpace = aGui.getNodeSpace();
		if (nodeSpace != null) {
			Rectangle view = aGui.getInfoSetView();
			if ((cachedNodeSpace == null) || (cachedView == null) || !(cachedView.equals(view))|| !(cachedNodeSpace.equals(nodeSpace))) {
				cachedNodeSpace = nodeSpace;
				cachedView = view;
				if (cachedView != null) {

					int viewSizeX = cachedView.width;
					int viewSizeY = cachedView.height;
					int viewOriginX = cachedView.x;
					int viewOriginY = cachedView.y;

					// horizontal
					int minValue = Math.min(viewOriginX, nodeSpace.x);
					int maxValue = Math.max(viewOriginX + viewSizeX, nodeSpace.x + nodeSpace.width);
					int startValue = viewOriginX;
					int extentValue = viewSizeX;
					horizontalSBar.setUnitIncrement(Math.round((maxValue - minValue) / 50f));
					horizontalSBar.setBlockIncrement(Math.round((maxValue - minValue) / 10f));
					horizontalSBar.setEnabled((minValue < startValue) || (startValue + extentValue < maxValue));
					horizontalSBar.setValues(startValue, extentValue, minValue, maxValue);

					// vertical
					maxValue = -Math.min(viewOriginY, nodeSpace.y);
					minValue = -Math.max(viewOriginY + viewSizeY, nodeSpace.y + nodeSpace.height);
					startValue = -viewOriginY - viewSizeY;
					extentValue = viewSizeY;
					verticalSBar.setUnitIncrement(Math.round((maxValue - minValue) / 50f));
					verticalSBar.setBlockIncrement(Math.round((maxValue - minValue) / 10f));
					verticalSBar.setEnabled((minValue < startValue) || (startValue + extentValue < maxValue));
					verticalSBar.setValues(startValue, extentValue, minValue, maxValue);
					//		System.out.println("min " + minValue + " max " + maxValue + " start " + startValue + " ex " + extentValue);

				}
			}
		}
	}

	private void updateViewSize() {
		if (lastSize != null) {
			// this is the normal case where the view is already fixed and must be adapted
			Rectangle view = aGui.getInfoSetView();
		//	if (view == null) return;

			int width = (Math.round((float)view.width  * (float)this.getSize().width / lastSize.width));
			int height = (Math.round((float)view.height * (float)this.getSize().height / lastSize.height));

			aGui.setInfoSetView_(view.x,view.y,width,height);

			lastSize = this.getSize();
		} else {
			if (aGui.getInfoSetView() == null) {
				aGui.setDisplaySize(this.getBounds().width, this.getBounds().height);
				aGui.setNative();
			//	sui.setInfoSetView(-50,-50,,);
			}
			lastSize = this.getSize();
		}
	}

	private void scroll() {
		if (this.horizontalScrollingFlag != 0) {
			aGui.moveInfoSetViewHorizontal(this.horizontalScrollingFlag);
		}
		if (this.verticalScrollingFlag != 0) {
			aGui.moveInfoSetViewVertical(this.verticalScrollingFlag);
		}
	}


	private Point graphicsToNode(int x, int y) {
		Rectangle view = aGui.getInfoSetView();
		if (view == null) {
			updateViewSize();
		}
		view = aGui.getInfoSetView();
		
		Dimension dispSize = displayer.getSize();

		float tx = (float)(x * view.width ) / (float)dispSize.width;
		float ty = (float)((dispSize.height-y) * view.height) / (float)dispSize.height;

		float sx = view.x + tx;
		float sy = view.y + ty;

		return new Point(Math.round(sx), Math.round(sy));


	}
	private Point graphicsToNode(Point p) {
		return graphicsToNode(p.x, p.y);
	}

	protected class InternalUI implements MouseListener,
	MouseMotionListener,
	KeyListener,
	MouseWheelListener,
	AdjustmentListener,
	ComponentListener {

		private Point				 clickedPoint        = null;
		protected Point                doublyClickedPoint  = null;
		private boolean              ctrlPressed         = false;
		private boolean              shiftPressed        = false;
		private boolean 			 altPressed          = false;
		protected boolean            button1Pressed      = false;
		protected boolean            button3Pressed      = false;
		protected Curvable             curvedLink          = null;
		protected int[]				 curvation = null;
		private Clickable            clickedObject       = null;
		private List<PaintableLink>  movedNodeLinks      = new ArrayList<PaintableLink>();
		private long                 lastClick           = 0;
		private Pointable 			 pointed             = null;



		protected InternalUI(JComponent ui) {
		//	outerClass = ui;
			lastClick = System.nanoTime();
		}

		public void adjustmentValueChanged(AdjustmentEvent e) {
			Rectangle view = aGui.getInfoSetView();
			if (e.getAdjustable().equals(horizontalSBar)) {
				aGui.setInfoSetView_(horizontalSBar.getValue(), view.y, view.width, view.height);
			} else if (e.getAdjustable().equals(verticalSBar)) {
				//			//	JScrollBar sbar = verticalSBar;
				//				int i = 0;
				aGui.setInfoSetView_(view.x, -(verticalSBar.getValue()+view.height), view.width, view.height);
			} else {
				assert (1==0) : "should not be here";
			}
		}

		public void componentHidden(ComponentEvent e) {	}
		public void componentMoved(ComponentEvent e){	}
		public void componentResized(ComponentEvent e){
			updateViewSize();
			updateScrollBars();
		}
		public void componentShown(ComponentEvent e){
			updateViewSize();
			updateScrollBars();
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
				ctrlPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				shiftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_ALT) {
				altPressed = false;
				endFitting(e);
			}
		}
		
		boolean fitused = false;

		private void endFitting(KeyEvent e) {
			if (fitused) {
				if (aGui.askUserConfirmation("Save new node position?")) {
					aGui.saveRefit();
				}
				fitused = false;
			}
			aGui.reinitRefitFactor();
		}
		
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (ctrlPressed) {
				if (e.getWheelRotation() > 0) {
					for (int i = 0 ; i < e.getWheelRotation() ; i++) {
						aGui.zoom(1);
					}
				} else {
					for (int i = 0 ; i < -e.getWheelRotation() ; i++) {
						aGui.zoom(-1);
					}
				}
			}
			else if (shiftPressed) {
				aGui.modifyDisplayedElementSize(e.getWheelRotation());
			}
			else if (altPressed) {
				fitused = true;
				int rot = -e.getWheelRotation();
				if (rot > 0) {
					aGui.refit(rot*(11f/10f), e);
				} else {
					aGui.refit(-(10f/11f)/rot, e);
				}
			}
		}		

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
				ctrlPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				shiftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_ALT) {
				altPressed = true;
			}
			Rectangle view = aGui.getInfoSetView();

			int viewSizeX = view.width;
			int viewSizeY = view.height;

			int x_increment = Math.max(Math.round(viewSizeX / 200f), 1);
			int y_increment = Math.max(Math.round(viewSizeY / 200f), 1);
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (!(shiftPressed)) {
					if (altPressed) {
						aGui.updateBackgroundImagePosition(0,-x_increment);
					} else {
						aGui.moveInfoSetViewHorizontal(-x_increment);
					}
				} else {
					if (altPressed) {
						aGui.updateBackgroundImageSize(0,-x_increment);
					} else {
						aGui.resizeInfoSetViewHorizontal(-x_increment);
					}
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (!(shiftPressed)) {
					if (altPressed) {
						aGui.updateBackgroundImagePosition(0,x_increment);
					} else {
						aGui.moveInfoSetViewHorizontal(x_increment);
					}
				} else {
					if (altPressed) {
						aGui.updateBackgroundImageSize(0,x_increment);
					} else {
						aGui.resizeInfoSetViewHorizontal(x_increment);
					}
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				if (!(shiftPressed)) {
					if (altPressed) {
						aGui.updateBackgroundImagePosition(1,-y_increment);
					} else {
						aGui.moveInfoSetViewVertical(-y_increment);
					}
				} else {
					if (altPressed) {
						aGui.updateBackgroundImageSize(1,-y_increment);
					} else {
						aGui.resizeInfoSetViewVertical(-y_increment);
					}
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (!(shiftPressed)) {
					if (altPressed) {
						aGui.updateBackgroundImagePosition(1,x_increment);
					} else {
						aGui.moveInfoSetViewVertical(x_increment);
					}
				} else {
					if (altPressed) {
						aGui.updateBackgroundImageSize(1,x_increment);
					} else {
						aGui.resizeInfoSetViewVertical(x_increment);
					}
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_MINUS) {
				if (ctrlPressed) {
					aGui.zoom(1);
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_PLUS) {
				if (ctrlPressed) {
					aGui.zoom(-1);
				}
			}
		}


		public void keyTyped(KeyEvent e) {}

		public void mouseReleased(MouseEvent e) {
			try {
				clickedPoint = null;
				if (e.getButton() == MouseEvent.BUTTON1) {
					button1Released(e);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					button3Pressed = false;
					if ((button1Pressed == true) && (doublyClickedPoint != null)){
						doublyClickedPoint = null;
						return;
					}
					if ((button1Pressed == false) && (doublyClickedPoint != null)){
						doublyClickedPoint = null;
						return;
					}
					Pointable temp = aGui.getElementAt(e.getPoint());
					JPopupMenu jm = buildPopupMenu(temp);
					if (jm != null) {
						jm.pack();
						jm.show(GraphicalNetworkEditor.this,e.getX(), e.getY());
					}
					clickedObject = null;
				}
			}
			catch (IllegalStateException ex) {
				if (GraphicalNetworkEditor.this.aGui.getGlobalInterface() instanceof JFrame) {
					PopupDisplayer.popupErrorMessage(ex.getMessage(), (JFrame)GraphicalNetworkEditor.this.aGui.getGlobalInterface());
				} else {
					PopupDisplayer.popupErrorMessage(ex.getMessage(), null);					
				}
			}
		}

		protected JPopupMenu buildPopupMenu(Pointable temp) {
			JPopupMenu jm = new JPopupMenu();

			JMenuItem prop= new JMenuItem("Properties...");
			prop.addActionListener(new ActionListener() {
				Clickable object;
				ActionListener init(Clickable object) {
					this.object = object;
					return this;
				}
				public void actionPerformed(ActionEvent e) {
					aGui.displayProperties(object);
				}
			}.init((temp instanceof Clickable) ? (Clickable)temp : null));
			jm.add(prop);

			if (temp != null) {
				AbstractElementContainer cont = temp.getElementContainer();
				if (cont.getContainedObject() instanceof InteractiveElement) {
					InteractiveElement el = (InteractiveElement)cont.getContainedObject();
					if (el.hasContextualMenu()) {
						jm.add(el.getElementMenu());
					}
				}

				addSomeMenus(jm, temp);
			}

			/*	if (uiManager.isEditable()) {
				prop= new JMenuItem("Trim node space");
				prop.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						uiManager.trimNodeSpace();
					}
				});
				jm.add(prop);
			}*/
			if (temp != null) {
				if (temp instanceof Clickable) {
					if (aGui.getAssociatedAbstractGraphHandler().isEditable()) {
						JMenuItem del = new JMenuItem("Delete");
						del.addActionListener((new ActionListener() {
							private Clickable c = null;
							ActionListener init(Clickable c) {
								this.c = c;
								return this;
							}
							public void actionPerformed(ActionEvent e) {
								aGui.getAssociatedAbstractGraphHandler().removeElement(c.getElementContainer(), e);
							}
						}).init((Clickable)temp));
						jm.add(del);
					}
				}
			}

			return jm;
		}
		
		protected void addSomeGlobalMenus(JPopupMenu jm) {
			final LayerContainer editedLayer = aGui.getAssociatedAbstractGraphHandler().getEditedLayer();
			
			//---- Menu size modificator
			class Rezomming {
				public boolean state = false;
			}
			final Rezomming rezomming = new Rezomming();

			JMenu sizeModificator = new JMenu("Size modificator");
			final JLabel sizeModificatorValue = new JLabel((int)(aGui.getSizeModificator()*100) + "%");
			final JSlider sizeModificatorSlider = new JSlider(SwingConstants.HORIZONTAL, 
					(int)(100*Math.min(0.1, aGui.getSizeModificator())), 
					(int)(100*Math.max(3, aGui.getSizeModificator())), 
					(int)(100*aGui.getSizeModificator()));
			sizeModificatorSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					if (aGui.getSizeModificator() != sizeModificatorSlider.getValue()/100f && !rezomming.state) {
						aGui.setSizeModificator(sizeModificatorSlider.getValue()/100f);

						sizeModificatorValue.setText(sizeModificatorSlider.getValue() + "%");
					}
				}
			});

			JMenuItem sizeModificatorReset = new JMenuItem("Reset");
			sizeModificatorReset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					aGui.setSizeModificator(1);
				}
			});

			sizeModificator.add(sizeModificatorReset);
			sizeModificator.add(sizeModificatorSlider);
			sizeModificator.add(sizeModificatorValue);
			jm.add(sizeModificator);			
			//----

			//---- Menu Zoom
			JMenu zoom = new JMenu("Zoom");
			final JLabel zoomValue = new JLabel(aGui.getZoom() + "%");
			final JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 
					Math.min(10, aGui.getZoom()), 
					Math.max(300, aGui.getZoom()), 
					aGui.getZoom());
			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					if (aGui.getZoom() != slider.getValue()) {
						aGui.setZoom(slider.getValue());

						zoomValue.setText(slider.getValue() + "%");

						rezomming.state = true;
						sizeModificatorSlider.setValue((int)(aGui.getSizeModificator()*100));
						sizeModificatorSlider.setMinimum((int)(100*Math.min(sizeModificatorSlider.getMinimum()/100f, aGui.getSizeModificator())));
						sizeModificatorSlider.setMaximum((int)(100*Math.max(sizeModificatorSlider.getMaximum()/100f, aGui.getSizeModificator())));
						sizeModificatorValue.setText(sizeModificatorSlider.getValue() + "%");
						rezomming.state = false;
					}
				}
			});

			JMenuItem zoomReset = new JMenuItem("Reset");
			zoomReset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					aGui.setZoom(100);
				}
			});

			zoom.add(zoomReset);
			zoom.add(slider);
			zoom.add(zoomValue);
			jm.add(zoom);			
			//----

			//---- Menu Link width
			JMenu linkWidth = new JMenu("General link width");
			final NetworkAttribute defaultWidthAtt = 
				editedLayer.attribute(XMLTagKeywords.DEFAULT_LINK_WIDTH, false);

			int defaultWidth;

			if (defaultWidthAtt == null) {
				defaultWidth = 2;
			} else {
				defaultWidth = defaultWidthAtt.intValue();
			}


			//Try to get the maximum link width
			int maxLinkWidth = 20;
			try {
				String property = Javanco.getProperty(Javanco.JAVANCO_GRAPHICS_MAX_LINK_WIDTH_PROPERTY);
				if (property != null) {
					maxLinkWidth = Integer.parseInt(property);
				}
			}
			catch(NumberFormatException e1) {
				maxLinkWidth = 20;
			}

			final JLabel linkWidthValue = new JLabel(defaultWidth + "");
			final JSlider linkWidthSlider = new JSlider(SwingConstants.HORIZONTAL, Math.min(1, defaultWidth), Math.max(maxLinkWidth, defaultWidth), defaultWidth);
			linkWidthSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					NetworkAttribute defaultWidthAttNotNull;
					if (defaultWidthAtt != null) {
						defaultWidthAttNotNull = defaultWidthAtt;
					} else {
						defaultWidthAttNotNull = editedLayer.attribute(XMLTagKeywords.DEFAULT_LINK_WIDTH, true);
					}

					if (defaultWidthAttNotNull.intValue() != linkWidthSlider.getValue()) {
						aGui.refreshAndRepaint();
					//	applyColorf.run();
						defaultWidthAttNotNull.setValue(linkWidthSlider.getValue(), e);

						linkWidthValue.setText(linkWidthSlider.getValue() + "");
					}
				}
			});

			JMenuItem linkWidthReset = new JMenuItem("Reset");
			linkWidthReset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					NetworkAttribute defaultWidthAttNotNull;
					if (defaultWidthAtt != null) {
						defaultWidthAttNotNull = defaultWidthAtt;
					} else {
						defaultWidthAttNotNull = editedLayer.attribute(XMLTagKeywords.DEFAULT_LINK_WIDTH, true);
					}

					defaultWidthAttNotNull.setValue(2, e);
				}
			});

			linkWidth.add(linkWidthReset);
			linkWidth.add(linkWidthSlider);
			linkWidth.add(linkWidthValue);
			jm.add(linkWidth);
			//----
			
			JMenu color = new JMenu("Color");
			final JColorChooser cc = new JColorChooser();
			color.add(cc);
			color.setText("Background color");
			cc.setColor(aGui.getBackgroundColor());

			cc.getSelectionModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					aGui.setBackgroundColor(cc.getColor());
				}
			});
			jm.add(color);						
		}

		protected void addSomeMenus(JPopupMenu jm, Pointable pointedObject) {
			jm.addSeparator();
			if (pointedObject == null) {
				addSomeGlobalMenus(jm);	
			} else {
				addSomeElementMenus(jm, pointedObject);
			}
			jm.addSeparator();		
			//Save infos when the menu disappears
			jm.addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					aGui.prepareForSave();
//					applyColorf.run();
				}

				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
					aGui.prepareForSave();
//					applyColorf.run();
				}
			});
		}					
		
		protected void addSomeElementMenus(JPopupMenu jm, Pointable pointedObject) {

						
//			final Runnable applyColorf = applyColor;			
			//---- Menu Color
			JMenu color = new JMenu("Color");
			final JColorChooser cc = new JColorChooser();
			
			color.add(cc);
			jm.add(color);			

			if (pointedObject instanceof PaintableNode) {
				final PaintableNode node = (PaintableNode) pointedObject;
				final Color firstColor = node.color;

				color.setText("Node color");
				cc.setColor(node.color);
				cc.getSelectionModel().addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						node.color = cc.getColor();
						if (node != null && firstColor != null && !firstColor.equals(cc.getColor())) {
							node.getElementContainer().attribute(XMLTagKeywords.NODE_COLOR).setValue(TypeParser.parseColor(cc.getColor()), e);
							repaint();
						}
					}
				});
			}
			else if (pointedObject instanceof PaintableLink) {
				final PaintableLink link = (PaintableLink) pointedObject;
				final Color firstColor = link.color;
				color.setText("Link color");
				cc.setColor(link.color);

				cc.getSelectionModel().addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						link.color = cc.getColor();
						if (link != null && firstColor != null && !firstColor.equals(cc.getColor())) {
							link.getElementContainer().attribute(XMLTagKeywords.LINK_COLOR).setValue(TypeParser.parseColor(cc.getColor()), e);
							repaint();
						}
					}
				});
			}						
			//----			
			
			
			//---- Menu Link curve
			if (pointedObject != null && pointedObject instanceof PaintableLink) {
				final PaintableLink link = (PaintableLink) pointedObject;							
				JMenu linkCurve = new JMenu("Link curve");

				final int[] actual = new int[]{link.curveStart, link.curveStartAngle, link.curveEnd, link.curveEndAngle};
				final XMLTagKeywords[] atts = new XMLTagKeywords[]{XMLTagKeywords.LINK_CURVE_START,
												  XMLTagKeywords.LINK_CURVE_START_ANGLE,
												  XMLTagKeywords.LINK_CURVE_END,
												  XMLTagKeywords.LINK_CURVE_END_ANGLE};
				final JSlider[] sliderTab = new JSlider[4];
				final JLabel[] labTab = new JLabel[4];
				final boolean[] modified = {false};
				for (int i = 0 ; i < actual.length ; i++) {
					final int j = i;
					labTab[i] = new JLabel(actual[i] + "");
					sliderTab[i] = new JSlider(SwingConstants.HORIZONTAL, -90, 90, actual[i]);
					sliderTab[i].addChangeListener(new ChangeListener() {
		
						@Override
						public void stateChanged(ChangeEvent e) {
							modified[0] = true;
							int val = sliderTab[j].getValue();
							link.getElementContainer().attribute(atts[j]).setValue(val, e);	
							labTab[j].setText(val+"");
							switch (j) {
								case 0:
									link.curveStart = val;
									link.curveEnd = val;
									sliderTab[2].setValue(val);
									labTab[2].setText(val+"");
									break;
								case 1: 
									link.curveStartAngle = val;
									link.curveEndAngle = -val;
									sliderTab[3].setValue(-val);
									labTab[3].setText(-val+"");
									break;
								case 2: link.curveEnd = val;
									break;
								case 3: link.curveEndAngle = val;
									break;
								default:
							}				
						}
					});	
					linkCurve.add(sliderTab[j]);
					linkCurve.add(labTab[j]);																					
				}
				JMenuItem reset = new JMenuItem("Reset");
				reset.addActionListener(new ActionListener() {
				@Override
					public void actionPerformed(ActionEvent e) {
						for (int i = 0;  i < actual.length ; i++) {
							link.getElementContainer().attribute(atts[i]).setValue(sliderTab[i].getValue(), e);	
							labTab[i].setText(0+"");
							sliderTab[i].setValue(0);							
						}
					}
				});

				linkCurve.add(reset);								  
				jm.add(linkCurve);
				
				linkCurve.addMenuListener(new javax.swing.event.MenuListener() {
					public void menuCanceled(javax.swing.event.MenuEvent e) {
						confirm();
					}
					public void menuDeselected(javax.swing.event.MenuEvent e) {
						confirm();
					}
					public void menuSelected(javax.swing.event.MenuEvent e) {
						confirm();
					}
					
					private void confirm() {
						if (modified[0]) {
							for (int i = 0 ; i < atts.length ; i++) {
								link.getElementContainer().linkAttribute(atts[i].toString());
							}
						}
					}					
					
				});
			}
			//----
		}

		public void mousePressed(MouseEvent e) {
			grabFocus();
			clickedPoint = graphicsToNode(e.getPoint());
			aGui.setBeeingConstructedLink(null);
			if (e.getButton() == MouseEvent.BUTTON1) {
				button1Pressed = true;
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				button3Pressed = true;
			}
			if (button1Pressed && button3Pressed) {
				if ((pointed == null) && (clickedObject == null)) {
					doublyClickedPoint = graphicsToNode(e.getPoint());
					return;
				}
			}
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (aGui.getAssociatedAbstractGraphHandler().isEditable()) {
					setClickedObject(e);
					long ttt = System.nanoTime() - lastClick;
					if (ttt < 300000000) {
						aGui.displayProperties(clickedObject);
						clickedObject = null;
						button1Pressed = false;
					}
				}
			}
			if (ctrlPressed || shiftPressed) {
				if (clickedObject instanceof Curvable) {
					curvation = ((Curvable)clickedObject).getCurve();
				}
			}
		}

		private void button1Released(MouseEvent e) {
			button1Pressed = false;
			if ((button3Pressed == true) && (doublyClickedPoint != null)) {
				return;
			}
			if ((button3Pressed == false) && (doublyClickedPoint != null)) {
				doublyClickedPoint = null;
				return;
			}
			Movable movedNode = aGui.getMovedNode();
			if (movedNode != null) {
				if (movedNode instanceof PaintableNode) {
					((PaintableNode)movedNode).isMoving = false;
				}
				movedNode.saveNewPosition(e);
				aGui.setMovedNode(null);
				movedNodeLinks.clear();
				clickedObject = null;
				checkPointed(e);
				aGui.refreshAndRepaint();
				return;
			}
			if (curvedLink != null) {
				curvedLink.saveCurve();
				curvedLink = null;
				clickedObject = null;
				checkPointed(e);
				return;
			}
			if (aGui.getBeeingConstructedLink() != null) {
				PaintableLink link = aGui.getBeeingConstructedLink();
				Pointable temp = aGui.getElementAt(e.getPoint());
				if (temp != null) {
					if (temp instanceof Linkable) {
						Linkable endNode = (Linkable)temp;
						if (endNode.getId() != link.orig) {
							link.endX = endNode.getX();
							link.endY = endNode.getY();
							link.dest = endNode.getId();
							aGui.getAssociatedAbstractGraphHandler().newLink(link.orig, link.dest, "default", "default", "default", e);
						}
					}
				}
				aGui.setBeeingConstructedLink(null);
				clickedObject = null;
				checkPointed(e);
				return;
			}
			if (aGui.isNodeSelected()) {
				if (clickedObject == null) {
					Point p = graphicsToNode(e.getPoint());
					aGui.getAssociatedAbstractGraphHandler().newNode(p.x, p.y, "default", "default", "default", e);
					checkPointed(e);
				}
			}
			if (clickedObject != null) {
				lastClick = System.nanoTime();
				clickedObject = null;
			}
		}

		private void setClickedObject(MouseEvent e) {
			Pointable temp = aGui.getElementAt(e.getPoint());
			if (temp != null) {
				if (temp instanceof Clickable) {
					clickedObject = (Clickable)temp;
				}
			}
		}


		public void mouseClicked(MouseEvent e) {}

		public void mouseDragged(MouseEvent e) {
			Point pointedPoint = graphicsToNode(e.getPoint());
			if ((button1Pressed) || (button3Pressed)) {
				if (doublyClickedPoint != null) {
					int changeX = pointedPoint.x - doublyClickedPoint.x;
					int changeY = pointedPoint.y - doublyClickedPoint.y;
					Rectangle view = aGui.getInfoSetView();

					aGui.setInfoSetView_(view.x - changeX, view.y - changeY, view.width, view.height);
				}
			}
			if (aGui.getAssociatedAbstractGraphHandler().isEditable()) {
				Movable movedNode = aGui.getMovedNode();
				if (aGui.isLinkSelected()) {
					checkPointed(e);
					if (button1Pressed) {
						if (aGui.getBeeingConstructedLink() != null) {
							PaintableLink link = aGui.getBeeingConstructedLink();
							link.endX = pointedPoint.x;
							link.endY = pointedPoint.y;
							aGui.setBeeingConstructedLink(link);
						} else {
							if (clickedObject != null) {
								if (clickedObject instanceof Linkable) {
									Linkable linkedNode = (Linkable)clickedObject;
									PaintableLink link = new PaintableLink();
									link.startX = linkedNode.getX();
									link.startY = linkedNode.getY();
									link.endX = pointedPoint.x;
									link.endY = pointedPoint.y;
									link.orig =   linkedNode.getId();
									link.key = link.orig + "->" + "?";
									aGui.setBeeingConstructedLink(link);
								}
							}
						}
					}
				} else if (movedNode != null) {
					if (movedNode instanceof PaintableNode) {
						((PaintableNode) movedNode).isMoving = true;
					}

					movedNode.setX(pointedPoint.x);
					movedNode.setY(pointedPoint.y);
					for (PaintableLink link : movedNodeLinks) {
						if (movedNode.getId() == link.orig) {
							link.startX = pointedPoint.x;
							link.startY = pointedPoint.y;
						} else if (movedNode.getId() == link.dest) {
							link.endX = pointedPoint.x;
							link.endY = pointedPoint.y;
						} else {
							assert (1==0) : "grosse erreur";
						}
					}
					repaint();
				} else {
					if (clickedObject != null) {
						if (clickedObject instanceof PaintableNode) {
							PaintableNode m = (PaintableNode)clickedObject;
							aGui.setMovedNode(m);
							for (PaintableLink link : aGui.getPaintableLinks()) {
								if ((link.orig == m.getId()) || (link.dest == m.getId())) {
									movedNodeLinks.add(link);
								}
							}
						}
						if (clickedObject instanceof Curvable) {
							curvedLink = (Curvable)clickedObject;
							
							curvation = curvedLink.getCurve();
							
							Point p = graphicsToNode(e.getPoint());
							Point dir = curvedLink.getDirection();
							double prodScal = (p.x - clickedPoint.x)*dir.y + (p.y - clickedPoint.y)*dir.x;
					
							curve(p, prodScal);
							repaint();
						}
					}
				}
			}
		}
		
		private void curve(Point p, double prodScal) {
			if (ctrlPressed && shiftPressed) {
				if (prodScal > 0) {
					curvedLink.setCurveAngleA(curvation[2] + (int)p.distance(clickedPoint));
					curvedLink.setCurveAngleB(curvation[3] -(int)p.distance(clickedPoint));
				} else {
					curvedLink.setCurveAngleA(curvation[2] -(int)p.distance(clickedPoint));
					curvedLink.setCurveAngleB(curvation[3] + (int)p.distance(clickedPoint));
				}
			} else if (ctrlPressed) {
				if (prodScal > 0) {
					curvedLink.setCurveA(curvation[0] + (int)p.distance(clickedPoint));
					curvedLink.setCurveB(curvation[1] + (int)p.distance(clickedPoint));
				} else {
					curvedLink.setCurveA(curvation[0] -(int)p.distance(clickedPoint));
					curvedLink.setCurveB(curvation[1] -(int)p.distance(clickedPoint));
				}
			} else if (shiftPressed) {
				if (prodScal > 0) {
					curvedLink.setCurveB(curvation[1] + (int)p.distance(clickedPoint));
				} else {
					curvedLink.setCurveB(curvation[1] -(int)p.distance(clickedPoint));
				}
			}			
		}

		public void mouseExited(MouseEvent e) {
			if (button1Pressed) {
				if (e.getPoint().x >= getSize().width) {
					horizontalScrollingFlag = 1; // down
				} else if (e.getPoint().x <= 0) {
					horizontalScrollingFlag = -1; // up
				}
				if (e.getPoint().y >= getSize().height) {
					verticalScrollingFlag = -1; // right
				} else if (e.getPoint().y <= 0) {
					verticalScrollingFlag = 1; // left
				}
				repaint();
			}
		}
		
		private Cursor moveNode = new Cursor(Cursor.MOVE_CURSOR);
		private Cursor pointLink = new Cursor(Cursor.HAND_CURSOR);
		private Cursor pointer = new Cursor(Cursor.CROSSHAIR_CURSOR);
		private Cursor arrow = new Cursor(Cursor.DEFAULT_CURSOR);

		public void mouseEntered(MouseEvent e) {
			/*	 		System.out.println(e.getModifiersEx());
	 		if ((e.getModifiersEx() & e.ALT_DOWN_MASK) > 0) {
	 			int iii = 0;
	 		}*/
			if (((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == 0) && (button1Pressed == true)) {
				button1Released(e);
			}
			verticalScrollingFlag = 0;
			horizontalScrollingFlag = 0;
		}

		public void mouseMoved(MouseEvent e) {
			checkPointed(e);
		}

		private void checkPointed(MouseEvent e) {
			
			Pointable actual = aGui.getElementAt(e.getPoint());
			if (actual != null) {
				if (!(actual.equals(pointed))) {
					if (pointed != null)
						pointed.setPointed(false);
					pointed = actual;
					actual.setPointed(true);
					repaint();
					if (actual instanceof PaintableNode) {
						if (!aGui.isLinkSelected()) {
							setCursor(moveNode);
						} else {
							setCursor(pointer);
						}
					} else {
						setCursor(pointLink);
					}
				}
			} else {
				if (aGui.isLinkSelected() || aGui.isNodeSelected()) {
					setCursor(pointer);	
				} else {
					setCursor(arrow);
				}
				if (pointed != null) {
					pointed.setPointed(false);
					pointed = null;
					repaint();	
				}
			}
		}
	}
}
