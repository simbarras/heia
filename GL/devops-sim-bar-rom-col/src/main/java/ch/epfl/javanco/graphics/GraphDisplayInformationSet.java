package ch.epfl.javanco.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.GraphicalDataHandler;

public class GraphDisplayInformationSet {
//	public static final String DISPLAY_STRECHED = "streched";
//	public static final String DISPLAY_NATIVE   = "native";
//	public static final String DISPLAY_TRUNKED  = "trunc";
//	public static final String DISPLAY_BEST_FIT = "best_fit";

	// information coming from topological description (handler)
	private List<PaintableLink> linksH = new ArrayList<PaintableLink>();
	private Vector<PaintableNode> nodesH = new Vector<PaintableNode>();
	public Vector<PaintableNode> nodesHClones = new Vector<PaintableNode>();
	public ArrayList<PaintableLayer> layers = new ArrayList<PaintableLayer>();
	public PaintableLayerGlobals layerGlobals = new PaintableLayerGlobals();

	// information coming from graphical data handler
	public Color backgroundColor = null;
	public int nodeSpaceImageOriginX  = -1;
	public int nodeSpaceImageOriginY = -1;
	public int nodeSpaceImageWidth = -1;
	public int nodeSpaceImageHeight = -1;
	private int viewWidth = 1;
	private int viewHeight = 1;
	private int viewOriginX = 0;
	private int viewOriginY = 0;
	public boolean viewFixed = false;
	public float defaultLabelFontSize = -1;
	public float sizeModificator = -1f;
	public String backImagePath = null;
//	public String displayType = "";
	private Dimension displaySize = null;
	public boolean useAnimation = false;
	public boolean displayBackgroundImage = true;

	// data depending on the current representation
	public Image backImage = null;

	/*******/	// information to set independently when GraphDisplayInformationSet is used in a persistent way
	/**/public PaintableLink     newLink    = null;
	/**///public Pointable         pointedObject  = null;
	public PaintableNode     movedNode = null;

	public GraphDisplayInformationSet(AbstractGraphHandler agh) {
		initializeDefaultGraphicalData();
		refreshElementsGraphicalInfos(agh);
	}

	public synchronized List<PaintableLink> getLinksHCopy() {
		return new ArrayList<PaintableLink>(linksH);
	}

	@SuppressWarnings("unchecked")
	public synchronized List<PaintableNode> getNodeHCopy() {
		return (List<PaintableNode>) nodesH.clone();
		
	//	System.out.println(nodesH.size());
	//	return new ArrayList<PaintableNode>(nodesH);
	}

	public Dimension getDisplaySize() {
	//	if (displaySize == null) {
	//		return new Dimension(viewWidth, viewHeight);
	//	} else {
			return displaySize;
	//	}
	}

	public synchronized Rectangle getViewRectangle() {
		if (viewFixed) {
			return new Rectangle(viewOriginX, viewOriginY, viewWidth, viewHeight);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return super.toString() + " " + displaySize;
	}

	public Rectangle getTotalView() {
		int lowestX = Integer.MAX_VALUE;
		int lowestY = Integer.MAX_VALUE;
		int highestX = Integer.MIN_VALUE;
		int highestY = Integer.MIN_VALUE;
		int minSize = Integer.MAX_VALUE;
		for (PaintableNode pn : getNodeHCopy()) {
			if (pn != null) {
				if (pn.posX < lowestX) {
					lowestX = pn.posX;
				}
				if (pn.posY < lowestY) {
					lowestY = pn.posY;
				}
				if (pn.posX > highestX) {
					highestX = pn.posX;
				}
				if (pn.posY > highestY) {
					highestY = pn.posY;
				}
				if (pn.size < minSize) {
					minSize = pn.size;
				}
			}
		}
		return new Rectangle(lowestX-minSize, lowestY-minSize, Math.max(minSize,highestX - lowestX)+(2*minSize), Math.max(minSize,highestY - lowestY)+(2*minSize));
	}

	public synchronized void setView(Rectangle rect) {
		viewWidth = rect.width;
		viewHeight = rect.height;
		viewOriginX = rect.x;
		viewOriginY = rect.y;
		viewFixed = true;
	}
	
	public synchronized void setDisplaySize(Dimension d) {
		displaySize = d;
	}

	public synchronized void setView(int x, int y, int w, int h) {
		viewOriginX = x;
		viewOriginY = y;
		viewWidth = w;
		viewHeight = h;
		viewFixed = true;
	}

	private void initializeDefaultGraphicalData() {
		String bgColor = Javanco.getProperty(Javanco.JAVANCO_GRAPHICS_DEFAULTBACKGROUNDCOLOR_PROPERTY);
		if (bgColor == null) {
			backgroundColor = TypeParser.parseColor("#D7D7C8");
		} else {
			backgroundColor = TypeParser.parseColor(bgColor);
		}
		viewWidth = 1;
		viewHeight = 1;
		viewOriginX = 0;
		viewOriginY = 0;
		defaultLabelFontSize = 10;
		sizeModificator = 1f;
		backImage = null;
//		displayType = DISPLAY_NATIVE;
//		displaySize = new Dimension(100,100);
		viewFixed = false;
	}

	public void fullRefresh(AbstractGraphHandler agh, GraphicalDataHandler gdh) {
		refreshGlobalGraphicalData(gdh);
		refreshElementsGraphicalInfos(agh);
	}

	public synchronized void saveGraphicalData(GraphicalDataHandler dataHandler) {
		if (dataHandler != null) {
			if (viewFixed) {
				dataHandler.attribute("viewRectangle_width").setValue(viewWidth, null);
				dataHandler.attribute("viewRectangle_height").setValue(viewHeight, null);
				dataHandler.attribute("viewRectangle_x").setValue(viewOriginX, null);
				dataHandler.attribute("viewRectangle_y").setValue(viewOriginY, null);
				dataHandler.attribute("backgroundImageLeftUpCorner_x").setValue(nodeSpaceImageOriginX, null);
				dataHandler.attribute("backgroundImageLeftUpCorner_y").setValue(nodeSpaceImageOriginY, null);
				dataHandler.attribute("backgroundImageSize_width").setValue(nodeSpaceImageWidth, null);
				dataHandler.attribute("backgroundImageSize_height").setValue(nodeSpaceImageHeight, null);
				dataHandler.attribute("sizeModificator").setValue(sizeModificator, null);
				dataHandler.attribute("backgroundColor").setValue(backgroundColor, null);
				dataHandler.attribute("defaultLabelFontSize").setValue(defaultLabelFontSize, null);
//				dataHandler.attribute("displayType").setValue(displayType, null);
				dataHandler.attribute("backImage").setValue(backImagePath, null);
		//		dataHandler.attribute("displaySize_x").setValue(displaySize.width, null);
		//		dataHandler.attribute("displaySize_y").setValue(displaySize.height, null);
			}
		}
	}

	public synchronized void refreshGlobalGraphicalData(GraphicalDataHandler dataHandler) {
		if (dataHandler != null) {
			if (dataHandler.attribute("viewRectangle_width",false) != null) {
				viewFixed = true;
				viewWidth = dataHandler.attribute("viewRectangle_width").intValue();
				viewHeight = dataHandler.attribute("viewRectangle_height").intValue();
				viewOriginX = dataHandler.attribute("viewRectangle_x").intValue();
				viewOriginY = dataHandler.attribute("viewRectangle_y").intValue();
				nodeSpaceImageOriginX = dataHandler.attribute("backgroundImageLeftUpCorner_x").intValue();
				nodeSpaceImageOriginY = dataHandler.attribute("backgroundImageLeftUpCorner_y").intValue();
				nodeSpaceImageWidth = dataHandler.attribute("backgroundImageSize_width").intValue();
				nodeSpaceImageHeight = dataHandler.attribute("backgroundImageSize_height").intValue();
				sizeModificator = dataHandler.attribute("sizeModificator").floatValue();
				backgroundColor = dataHandler.attribute("backgroundColor").colorValue(backgroundColor);
				defaultLabelFontSize= dataHandler.attribute("defaultLabelFontSize").floatValue(defaultLabelFontSize);
				setBackImage(dataHandler.attribute("backImage").getValue());
//				displayType = dataHandler.attribute("displayType").getValue();
		//		displaySize.width = dataHandler.attribute("displaySize_x").intValue();
		//		displaySize.height = dataHandler.attribute("displaySize_y").intValue();
			}
		}
	}

	public void setBackImage(String path) {
		if (path != null && !path.equals("")) {
			if (!(new JavancoFile(path).exists())) {
				path = Javanco.getProperty(Javanco.JAVANCO_DIR_IMAGES_PROPERTY) + "/" + path;
			}
			if (new JavancoFile(path).exists()) {
				java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
				backImagePath = path;
				backImage = tk.getImage(path);
			}
		}
	}

	public synchronized void setMovedNode(PaintableNode m) {
		movedNode = m;
		if (m != null) {
			nodesH.remove(m);
		}
	}

	/**
	 * This method translate the general structure of Javanco (AbstractGraphHandler)
	 * in a graphic oriented structure
	 */
	public synchronized void refreshElementsGraphicalInfos(AbstractGraphHandler handler) {
		try {
	//		synchronized(nodesH) {
	//			synchronized (linksH) {
					linksH.clear();
					nodesH.clear();
					nodesHClones.clear();
					nodesH.setSize(handler.getHighestNodeIndex()+1);
					//	nodesH.ensureCapacity(handler.getHighestNodeIndex()+1);
					if (handler.getHighestNodeIndex()+1 == 11) {
						handler.getHighestNodeIndex();
					}
					layers.clear();
					layerGlobals.initLevels();

					for (LayerContainer layerContainer : handler.getLayerContainers()) {
						if (layerContainer.isDisplayed()) {
							//String layerName = layerContainer.getKey();
							List<NodeContainer> nodes = layerContainer.getNodeContainers();
							for (int i = 0 ; i < nodes.size() ; i++) {
								NodeContainer nodeContainer = nodes.get(i);
								if (nodeContainer == null) { System.out.print("_"); }
								PaintableNode pnode = new PaintableNode(nodeContainer);
								pnode.init(nodeContainer);
								if (pnode.valid) {
									nodesH.set(pnode.id, pnode);
									layerGlobals.updateLevelsSize(nodeContainer);
								}
							}

							// This instruction has to be after the for loop
							// so the 'updateLevelsSize' function can detect
							// when it is called the first time.
							PaintableLayer pl = new PaintableLayer(layerContainer);
							pl.init(layerContainer);
							layers.add(pl);

							for (LinkContainer linkContainer : layerContainer.getLinkContainers()) {
								PaintableLink plink = new PaintableLink(linkContainer);
								plink.init(linkContainer);
								nodesH.setSize(Math.max(nodesH.size(),1+Math.max(plink.orig, plink.dest)));
								try {
									Point start = handler.getNodeContainer(plink.orig).getCoordinate();
									Point end = handler.getNodeContainer(plink.dest).getCoordinate();
									plink.startX = start.x;
									plink.startY = start.y;
									plink.endX   = end.x;
									plink.endY   = end.y;
								}
								// rendre cette horreur un peu moins horrible
								catch (Exception e) {
									plink.valid = false;
								}

								if (nodesH.get(plink.orig) == null) {
									NodeContainer orig = handler.getNodeContainer(plink.orig);
									PaintableNode pnode = new PaintableNode(orig);
									pnode.init(orig);
									pnode.alpha = 100;
									if (pnode.valid) {
										nodesH.set(plink.orig, pnode);
									}
								}

								PaintableNode plinkOrigPN = nodesH.get(plink.orig);
								if (plinkOrigPN != null && !plink.layer.equals(plinkOrigPN.layer)) {
									nodesHClones.addElement(plinkOrigPN.cloneToLayer(pl));
								}
								if (/*nodesH.size() <= plink.dest ||*/ nodesH.get(plink.dest) == null) {
									NodeContainer dest = handler.getNodeContainer(plink.dest);
									PaintableNode pnode = new PaintableNode(dest);
									pnode.init(dest);
									pnode.alpha = 100;
									if (pnode.valid) {
										/*	if (nodesH.size() <= plink.dest) {
										nodesH.setSize(plink.dest+1);
									}*/
										nodesH.set(plink.dest, pnode);
									}
								}

								PaintableNode plinkDestPN = nodesH.get(plink.dest);
								if (plinkDestPN != null && !plink.layer.equals(plinkDestPN.layer)) {
									nodesHClones.addElement(plinkDestPN.cloneToLayer(pl));
								}
								linksH.add(plink);
							}
						}

						// Layers need to be sorted so that the transparency effect can be
						// achieved completely. This comes from the fact that trasparent
						// objects that are further away need to be drawn first.
						Collections.sort(layers, new Comparator<PaintableLayer>() {
							public int compare(PaintableLayer lhs, PaintableLayer rhs) {
								int zDiff = lhs.getLayerZ() - rhs.getLayerZ();
								if(zDiff == 0) {
									return lhs.getName().compareTo(rhs.getName());
								} else {
									return zDiff;
								}
							}
						});
					}
	//			}
	//		}
		}
		catch (ArrayIndexOutOfBoundsException e) {}
	}
	
	public int getViewWidth() {
		return viewWidth;
	}
	
	public int getViewHeight() {
		return viewHeight;
	}
	
	public int getViewX() {
		return viewOriginX;
	}
	
	public int getViewY() {
		return viewOriginY;
	}
	
	public void incrementWidth(int i) {
		viewWidth += i;
	}
	
	public void incrementHeight(int i) {
		viewHeight += i;
	}
	
	public void incrementViewOriginX(int i) {
		viewOriginX += i;
	}
	
	public void incrementViewOriginY(int i) {
		viewOriginY += i;
	}
	
	public void setImageAsView() {
		nodeSpaceImageOriginX = viewOriginX;
		nodeSpaceImageOriginY = viewOriginY;
		nodeSpaceImageWidth   = viewWidth;
		nodeSpaceImageHeight  = viewHeight;
	}
	
	public Rectangle2D.Float getView2DRectangle() {
		return new Rectangle2D.Float(viewOriginX,
			viewOriginY,
			viewWidth,
			viewHeight);
	}
	
}
