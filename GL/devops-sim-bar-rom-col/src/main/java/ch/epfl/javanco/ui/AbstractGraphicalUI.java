package ch.epfl.javanco.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.event.RunnerEventListener;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.graphics.DefaultNetworkPainter2D;
import ch.epfl.javanco.graphics.ElementCoord;
import ch.epfl.javanco.graphics.GraphDisplayInformationSet;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.graphics.PaintableLink;
import ch.epfl.javanco.graphics.Pointable;
import ch.epfl.javanco.graphics.ViewStrecher;
import ch.epfl.javanco.network.AbstractElementContainer;


public abstract class AbstractGraphicalUI extends AbstractUI implements Printable {

	/**
	 * Timer used to refresh periodically.
	 * <BR>#author fmoulin
	 * @see #isAnimated()
	 * @see #setAnimated()
	 */
	private Timer timer = null;

	protected GraphDisplayInformationSet       graphInfoSet            = null;
	private   NetworkPainter                   painter                 = null;
	private   PrintProperties                  printProperties         = null;
	private GlobalInterface                  globalInterface         = null;

	public AbstractGraphicalUI(NetworkPainter painter,
			AbstractGraphHandler agh,
			GlobalInterface superInterface) {
		super(agh);
	//	synchronized (this) {
			this.painter = painter;
			this.setGlobalInterface(superInterface);
			this.graphInfoSet = new GraphDisplayInformationSet(handler);
			handler.activateMainDataHandler();
			Rectangle total = graphInfoSet.getTotalView();
			// by default, all the graph is displayed. This can be changed later
			setDisplaySize(total.width, total.height);
	//	}
	}
	
	public AbstractGraphicalUI(NetworkPainter painter,
			AbstractGraphHandler agh) {
		this(painter, agh, null);
	}

	public void setNetworkPainter(NetworkPainter np) {
		this.painter = np;
	}
	
	public NetworkPainter  getNetworkPainter() {
		return this.painter;
	}
	
	/**
	 * This method returns null. To have a RunnerEventListener actually used, implementing class
	 * must override this method.
	 * @return
	 */
	public RunnerEventListener getRunnerEventListener() {
		return null;
	}	

	/** Consider return only a copy here. Modifying the original might be dangerous */
	public GraphDisplayInformationSet getGraphDisplayInformationSet() {
		return graphInfoSet;
	}

	public List<PaintableLink> getPaintableLinks() {
		return graphInfoSet.getLinksHCopy();
	}

	public Rectangle getInfoSetView() {
		return graphInfoSet.getViewRectangle();
	}
	
	public void setBestFit(java.awt.Dimension d) {
		if (graphInfoSet.viewFixed) {
			setInfoSetView_(ViewStrecher.getBestFit(graphInfoSet.getTotalView(), d));
		} else {
			setInfoSetView_(ViewStrecher.getBestFit(handler, d));
		}
	}
	
	public float setBestFitAndKeepNodeRatio(java.awt.Dimension d) {
		Rectangle r = getInfoSetView();
		Rectangle rnew = ViewStrecher.getBestFit(graphInfoSet.getTotalView(), d);
		if (r != null) {
			float m = Math.max((float)r.width/(float)rnew.width, (float)r.height/(float)rnew.height);
			modifyDisplayedElementSize(m);
			setInfoSetView_(rnew);
			return m;
		} else {
			setInfoSetView_(rnew);
			return 1;
		}
	}
	
	public void setBackgroundImageDisplayed(boolean b) {
		graphInfoSet.displayBackgroundImage = b;
	}
	
	public boolean isDisplayingBackgroundImage() {
		return graphInfoSet.displayBackgroundImage;
	}
	
	public boolean hasBackgroundImageToDisplay() {
		return graphInfoSet.backImage != null;
	}
	
	public void setUnStreched() {
		java.awt.Rectangle r = graphInfoSet.getViewRectangle();
		java.awt.Dimension d = graphInfoSet.getDisplaySize();
		if (r != null && d != null) {
			setInfoSetView_(ViewStrecher.getMaintainedKeepingOrigin(r,d));
		}
	}
	
	public void setNative() {
		java.awt.Dimension d = graphInfoSet.getDisplaySize();
		resetDisplayedElementSize();
		setInfoSetView_(-50, -50, d.width, d.height);			
	}	

	protected void setInfoSetView_(int x, int y, int width, int height) {
	//	synchronized (this) {
			java.awt.Rectangle r = graphInfoSet.getViewRectangle();
			//if (r == null) return;
			if (r == null || r.width != width ||
				r.height != height ||
				r.x != x ||
				r.x != y) {
				graphInfoSet.setView(x, y, width, height);
				prepareForSave();		
			}
	//	}
	}

	protected void setInfoSetView_(Rectangle rect) {
		setInfoSetView_(rect.x, rect.y, rect.width, rect.height );
	}
	
	public void setDisplaySize(Dimension d) {
		setDisplaySize(d.width, d.height);
	}

	public void setDisplaySize(int width, int height) {
		Dimension d = graphInfoSet.getDisplaySize();
		if (d == null || d.width != width
			|| d.height != height) {
			graphInfoSet.setDisplaySize(new Dimension(width, height));
			prepareForSave();
		}
	}

	public void setElementSize(float f) {
		if (f != graphInfoSet.sizeModificator) {
			graphInfoSet.sizeModificator = f;
			prepareForSave();
		}
	}
	
	public void resetDisplayedElementSize() {
		setElementSize(1);
	}	
	
	public void modifyDisplayedElementSize(float factor) {
		setElementSize(graphInfoSet.sizeModificator * factor);
	}	

	public boolean infoSetViewHasBeenSet() {
		return graphInfoSet.viewFixed;
	}	

	public void paintItToComponent(Graphics g, JComponent comp) {
		setDisplaySize(comp.getWidth(), comp.getHeight());
		painter.paintItToGraphics(g, getGraphDisplayInformationSet());
	}
	
	
	public java.awt.image.BufferedImage getActualViewImage() {
		if (painter != null) {
			graphInfoSet.fullRefresh(this.handler, handler.getUIDelegate().getGraphicalDataHandler());
			return painter.paintItToImage(graphInfoSet);
		} else {
			throw new IllegalStateException("UIManager " + this  + " has no painter attached, thus cannot return an image");
		}		
	}
	
	public java.awt.image.BufferedImage getActualViewImage(int x, int y) {
		if (painter != null) {
			graphInfoSet.fullRefresh(this.handler, handler.getUIDelegate().getGraphicalDataHandler());
			Dimension saveDisplay = graphInfoSet.getDisplaySize();
			this.setDisplaySize(x, y);
			java.awt.image.BufferedImage im =  painter.paintItToImage(graphInfoSet);
			if (saveDisplay != null) setDisplaySize(saveDisplay);
			return im;
		} else {
			throw new IllegalStateException("UIManager " + this  + " has no painter attached, thus cannot return an image");
		}		
	}
	
	public java.awt.image.BufferedImage getFullView(int x, int y) {
		if (painter != null) {
			graphInfoSet.fullRefresh(this.handler, handler.getUIDelegate().getGraphicalDataHandler());
			Rectangle currentView = graphInfoSet.getViewRectangle();
			float m = this.setBestFitAndKeepNodeRatio(new Dimension(x,y));
			Dimension saveDisplay = graphInfoSet.getDisplaySize();
			this.setDisplaySize(x, y);
			java.awt.image.BufferedImage im = painter.paintItToImage(graphInfoSet);
			if (saveDisplay != null) setDisplaySize(saveDisplay);
			if (currentView != null) setInfoSetView_(currentView);
			modifyDisplayedElementSize(1/m);
			return im;
		} else {
			throw new IllegalStateException("UIManager " + this  + " has no painter attached, thus cannot return an image");
		}			
	}
	
	public BufferedImage getFullView() {
		if (painter != null) {
			graphInfoSet.fullRefresh(this.handler, handler.getUIDelegate().getGraphicalDataHandler());
			Rectangle currentView = graphInfoSet.getViewRectangle();
			Dimension display = graphInfoSet.getDisplaySize();
			float m = this.setBestFitAndKeepNodeRatio(display);
			java.awt.image.BufferedImage im = painter.paintItToImage(graphInfoSet);
			if (currentView != null) setInfoSetView_(currentView);
			modifyDisplayedElementSize(1/m);
			return im;
		} else {
			throw new IllegalStateException("UIManager " + this  + " has no painter attached, thus cannot return an image");
		}
	}	

/*	public java.awt.image.BufferedImage getGraphImage(boolean trimedToActualView) {
		if (painter != null) {
			graphInfoSet.fullRefresh(this.handler, handler.getUIDelegate().getGraphicalDataHandler());
			if (trimedToActualView) {
				return painter.paintItToImage(graphInfoSet.getViewRectangle(), graphInfoSet);
			} else {
				Rectangle view = ViewStrecher.getBestFit(handler, graphInfoSet.getViewRectangle().getSize());
				return painter.paintItToImage(view , graphInfoSet);
			}
		} else {
			throw new IllegalStateException("UIManager " + this  + " has no painter attached, thus cannot return an image");
		}
	}*/
	
	



/*	public void saveGraphImage(String fileName, int w, int h) throws java.io.IOException {
		OutputStream s = new java.io.FileOutputStream(fileName+".png");
		saveGraphImage(s, w, h);
		s.close();
	}*/

/*	public void saveGraphImage(OutputStream stream, int w, int h) throws java.io.IOException {
		
		int[] coord = handler.getExtremitiesWithMargin();
		Rectangle rect = new Rectangle(coord[0], coord[2], coord[1]-coord[0], coord[3]-coord[2]);
		java.awt.image.BufferedImage img = painter.paintItToImage(rect , graphInfoSet);
		javax.imageio.ImageIO.write(img, "png", stream);*/
		/*	int wO = this.graphInfoSet.displaySize.width;
		int hO = this.graphInfoSet.displaySize.height;*/

	/*	this.setDisplaySize(w, h);
		if (graphInfoSet.viewFixed == false) {
			setInfoSetView_(ViewStrecher.getBestFit(handler, w, h));
		}
		java.awt.image.BufferedImage img = getGraphImage(false);
		javax.imageio.ImageIO.write(img, "png", stream);*/
		
		/*	this.setDisplaySize(wO, hO);	*/
//	}


	/**
	 * Provide an access to the list of the displayed elements, for non GUI components.
	 * @param trimedToActualView if <code>true</code>, returns only the reference of the
	 * actually visible elements (elements present in the view). Otherwise, return all
	 * displayed elements (including elements that are outside of the "view", but may be
	 * visible when scrolling"
	 */
	public java.util.Hashtable<AbstractElementContainer, ElementCoord> getDisplayedElementCoord(boolean trimedToActualView) {
		if (painter != null) {
			graphInfoSet.fullRefresh(this.handler, handler.getUIDelegate().getGraphicalDataHandler());
			return painter.getDisplayedElementsPositions(graphInfoSet);
		} else {
			return new java.util.Hashtable<AbstractElementContainer, ElementCoord>();
		}
	}

	/**
	 * Permits to the UI delegate to know what is the actual nodeSpace. The nodeSpace
	 * (in the node coordinate system, what is the coord used in the XML definition file)
	 * contains any point where one object has been encountered once. The nodeSpace is
	 * used for scrolling. It is possible to trim it whit the method <code>trimNodeSpace()</code>
	 */
	public Rectangle getNodeSpace() {
		if (painter != null) {
			return painter.getGraphicalSpace(graphInfoSet);
		} else {
			return null;
		}
	}

	/**
	 * Permits to the UI delegate to know if something is displayed at the clicked
	 * point. Since display is an independent mechanism, otherwise UI has no way
	 * to know what is displayed and what is not.
	 */
/*	public Pointable getElementAt(Point p, int imageWidth, int imageHeight) {
		if (painter != null) {
			return painter.getElementAt(graphInfoSet.getViewRectangle(),p, graphInfoSet);
		} else {
			return null;
		}
	}*/

	public Pointable getElementAt(Point p) {
		if (painter != null) {
			return painter.getElementAt(p, graphInfoSet);
		} else {
			return null;
		}
	}

	public void prepareForSave() {
		graphInfoSet.saveGraphicalData(handler.getUIDelegate().getGraphicalDataHandler());
	}

	/**
	 * Start or stop a thread to repaint the component periodically. Used to animate links.
	 * <BR>#author fmoulin
	 * 
	 * @param enable define if the thread should be start or stop
	 * @see #isAnimated()
	 * @see #timer
	 */
	public void setAnimated(boolean enable) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (enable) {
			timer = new Timer(true);
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					handler.fireAllElementsModificationEvent(new CasualEvent(this));
				}
			};
			timer.schedule(task, 0, 30);
		}

		getGraphDisplayInformationSet().useAnimation = enable;
	}


	/**
	 * Test if the refresh timer is running.
	 * <BR>#author fmoulin
	 * 
	 * @see #setAnimated(boolean)
	 * @see #timer
	 */
	public boolean isAnimated() {
		return timer != null;
	}

	public static class PrintProperties {
		private final Rectangle input;
		private final Dimension output;

		public Rectangle getInput() {
			return input;
		}

		public Dimension getOutput() {
			return output;
		}

		public PrintProperties(Rectangle inputView, int width, int height) {
			input = inputView;
			output = new Dimension(width, height);
		}
	}

	public void setPrintProperties(PrintProperties pp) {
		printProperties = pp;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
	throws PrinterException {
		if (painter != null) {

			if (pageIndex == 0) {
				Dimension dim = graphInfoSet.getDisplaySize();
				Rectangle dim2 = graphInfoSet.getViewRectangle();
				
				graphInfoSet.setDisplaySize(printProperties.getOutput());
				graphInfoSet.setView(printProperties.getInput());
				
				dim = graphInfoSet.getDisplaySize();

				graphics.translate((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY());
				Rectangle oldClip = graphics.getClipBounds();
				graphics.clipRect(0, 0, dim.width, dim.height);
				graphics.clipRect(0, 0, (int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());
				painter.paintItToGraphics(graphics, graphInfoSet);

				graphics.setClip(oldClip);

				graphics.translate(-(int)pageFormat.getImageableX(), -(int)pageFormat.getImageableY());

				graphInfoSet.setView(dim2);

				return PAGE_EXISTS;
			} else {
				return NO_SUCH_PAGE;
			}
		} else {
			throw new IllegalStateException("UIManager " + this  + " has no painter attached, thus cannot print the network");
		}
	}

	@SuppressWarnings("unchecked")
	public static NetworkPainter getDefaultNetworkPainter() {
		String prop = Javanco.getProperty(Javanco.JAVANCO_DEFAULT_NETWORK_PAINTER_PROPERTY);
		Class<?> c = null;
		if (prop == null) {
			c = DefaultNetworkPainter2D.class;
		} else {
			try {
				c = Class.forName(prop);
			}
			catch (ClassNotFoundException e) {
				c = DefaultNetworkPainter2D.class;
			}
		}
		try {
			java.lang.reflect.Constructor<NetworkPainter> con = (Constructor<NetworkPainter>) c.getConstructor(new Class[]{});
			NetworkPainter np = con.newInstance(new Object[]{});
			return np;
		}
		catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
		catch (InstantiationException e) {
			throw new IllegalStateException(e);
		}
		catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		catch (java.lang.reflect.InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	public GlobalInterface getGlobalInterface() {
		return globalInterface;
	}

	public void setGlobalInterface(GlobalInterface globalInterface) {
		this.globalInterface = globalInterface;
	}



}
