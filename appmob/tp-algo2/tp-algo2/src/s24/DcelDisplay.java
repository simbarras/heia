package s24;
/*
Voilà le programme que j’ai créé pour visualiser l’état d’une instance Dcel
 
- Pour afficher une fenêtre, on peut simplement créer une nouvelle instance
  de la classe DcelDisplay et lui passer en paramètre une instance Dcel
 
- le programme affiche directement par exemple:
 
  Dcel dcX = new Dcel();
  dcX.addEar(new Point[] { new Point(0, 2), new Point(1, 2), new Point(1, 0), new Point(0, 0) });
  dcX.addEar(new Point[]{ new Point(1,0), new Point(2, 1), new Point(3, 2), new Point(1, 0)});
  dcX.addEar(new Point[]{ new Point(1,0), new Point(2, 1), new Point(3, 2),
                         new Point(4, 3), new Point(5, 4), new Point(1, 0)});
  new DcelDisplay(dcX);
 
- Le programme affiche les mises à jours (c’est à dire que si on modifie
  quelque chose dans la Dcel qu’on a donné en paramètre, les modifications
  se “feront” également dans le DcelDisplay)
  Donc si on aimerait afficher que l’état d’une Dcel à un moment précis, il
  ne faut pas lui passer directement l’objet, mais une copie de la Dcel!
  Dans l’exemple suivant, on verra donc sur les 3 fenêtres la même ...
  
  Dcel dc = new Dcel(fromCoords(ear0));
  new DcelDisplay(dc);
  System.out.println("With Ear 0 "+dc);
  okOuter(dc, 0, 4);
  dc.addEar(fromCoords(ear1));
  new DcelDisplay(dc);
  System.out.println("With Ear 1"+dc);
  okOuter(dc, 1, 7);
  dc.addEar(fromCoords(ear2));
  new DcelDisplay(dc);
  System.out.println("With Ear 2"+dc);
  okOuter(dc, 2, 11); ...
 
- Si on a plusieurs fenêtres d’ouvertes (donc plusieurs instances de 
  DcelDisplay), en fermant une fenêtre, le programme se termine => 
  les autres fenêtres sont également fermées.
  
Florian Winkler
*/

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class DcelDisplay extends JFrame {
  
  private static final Color PANEL_EXPLICATION_COLOR = Color.DARK_GRAY;
  private static final Color COLOR_CONFIG_TEXT = Color.ORANGE;
 
  public DcelDisplay(Dcel dcel){
    super();
    final DcelDrawerPanel dcelPanel = new DcelDrawerPanel(dcel, DcelDrawerPanel.DisplayData.EdgesSet, true, true);
    Box boxExplicationsAndChooser = new Box(BoxLayout.Y_AXIS);
    JPanel panelExplicationsExt = new JPanel(new FlowLayout());
    JPanel panelExplicationsInt = new JPanel(new FlowLayout());
    JPanel panelExplicationsMadeBy = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    JPanel panelConfiguration = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    PanelOnOff panelShowGrid = new PanelOnOff("Grid", true);
    PanelOnOff panelShowAxe = new PanelOnOff("Axis", true);
    
    JPanel panelFinalUpper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    JPanel panelFinalLower = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    
    
    panelShowAxe.addOnOffChangedListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if(e.getSource() instanceof PanelOnOff){
          dcelPanel.setShowAxe(((PanelOnOff)e.getSource()).isOn());
        }
      }
    });
    
    panelShowGrid.addOnOffChangedListener(new ChangeListener() { 
        @Override
        public void stateChanged(ChangeEvent e) {
          if(e.getSource() instanceof PanelOnOff){
            dcelPanel.setShowGrid(((PanelOnOff)e.getSource()).isOn());
          }
        }
      });
    
    JButton btnMadeBy = new JButton("©2012 by Florian Winkler");
    JButton btnCopyToClipboard = new JButton("to clipboard");
    
    btnCopyToClipboard.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        try{
          ImageSelection imgSel = new ImageSelection(dcelPanel.getActualImage());
          Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
        }
        catch(Exception ex){
          String strMessage = "Error happened while trying to copy image to clipboard\n" +
            "Errortype: " + ex.getClass() + "\n" + "ErrorMessage: " + ex.getMessage();
          JOptionPane.showMessageDialog(DcelDisplay.this, strMessage, "Error happened", JOptionPane.ERROR_MESSAGE);
        }
      }
      
   // This class is used to hold an image while on the clipboard.
      class ImageSelection implements Transferable {
          private final Image image;

          public ImageSelection(Image image) {
              this.image = image;
          }

          // Returns supported flavors
          public DataFlavor[] getTransferDataFlavors() {
              return new DataFlavor[]{DataFlavor.imageFlavor};
          }

          // Returns true if flavor is supported
          public boolean isDataFlavorSupported(DataFlavor flavor) {
              return DataFlavor.imageFlavor.equals(flavor);
          }

          // Returns image
          public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
              if (!DataFlavor.imageFlavor.equals(flavor)) {
                  throw new UnsupportedFlavorException(flavor);
              }
              return image;
          }
      }
    });
    
    JLabel lblRed = new JLabel("Outer edge");
    JLabel lblGreen = new JLabel("Inner edge");
    JLabel lblHelp = new JLabel("(?)");
    JLabel lblDataToDisplay = new JLabel("Data to display: ");
    
    panelExplicationsExt.setBackground(PANEL_EXPLICATION_COLOR);
    panelExplicationsInt.setBackground(PANEL_EXPLICATION_COLOR);
    panelExplicationsMadeBy.setBackground(PANEL_EXPLICATION_COLOR);
    panelConfiguration.setBackground(PANEL_EXPLICATION_COLOR);
    panelFinalUpper.setBackground(PANEL_EXPLICATION_COLOR);
    panelFinalLower.setBackground(PANEL_EXPLICATION_COLOR);
    
    panelExplicationsMadeBy.add(Box.createHorizontalStrut(10));
    panelExplicationsMadeBy.add(btnCopyToClipboard);
    panelExplicationsMadeBy.add(Box.createHorizontalStrut(10));
    panelExplicationsMadeBy.add(btnMadeBy);
    
    String[] stringsMenuDisplayChooser = new String[] {
        "edgesSet", "facetsSet", "outer", "pointsSet"
        };
    final DcelDrawerPanel.DisplayData[] stringsMenuDisplayChooserDisplayData = new 
    DcelDrawerPanel.DisplayData[] { DcelDrawerPanel.DisplayData.EdgesSet,
        DcelDrawerPanel.DisplayData.FacetsSet,
        DcelDrawerPanel.DisplayData.Outer,
        DcelDrawerPanel.DisplayData.PointsSet};
    JComboBox<String> jComboBoxMenuDisplay = new JComboBox<>(stringsMenuDisplayChooser);
    
    jComboBoxMenuDisplay.addItemListener(new ItemListener() {
      
      @Override
      public void itemStateChanged(ItemEvent e) {
        int indexToSelect = -1;
        if(e.getSource() instanceof JComboBox<?>){
          indexToSelect = ((JComboBox<?>)e.getSource()).getSelectedIndex();
        }
        if(indexToSelect < 0 || indexToSelect >= stringsMenuDisplayChooserDisplayData.length){
          JOptionPane.showMessageDialog(DcelDisplay.this, "Out of bounds error while selecting item " + indexToSelect);
        }
        else{
          dcelPanel.setDataToDisplay(stringsMenuDisplayChooserDisplayData[indexToSelect]);
        }
      }
    });
    
    lblRed.setForeground(Color.RED);
    lblGreen.setForeground(Color.GREEN);
    lblHelp.setForeground(Color.WHITE);
    lblDataToDisplay.setForeground(COLOR_CONFIG_TEXT);
    
    lblHelp.setToolTipText("Outer edges are edges who are not containted dcel.outer.ring()");

    ToolTipManager.sharedInstance().setInitialDelay(0);

    panelExplicationsInt.add(lblRed);
    panelExplicationsInt.add(Box.createHorizontalStrut(10));
    panelExplicationsInt.add(lblGreen);
    panelExplicationsInt.add(Box.createHorizontalStrut(10));
    panelExplicationsInt.add(lblHelp);
    
    panelExplicationsExt.add(panelExplicationsInt);
    panelExplicationsExt.add(panelExplicationsMadeBy);
    
    panelConfiguration.add(lblDataToDisplay);
    panelConfiguration.add(jComboBoxMenuDisplay);
    panelConfiguration.add(panelShowAxe);
    panelConfiguration.add(panelShowGrid);
    
    
    panelFinalUpper.add(panelExplicationsExt);
    panelFinalLower.add(panelConfiguration);
    boxExplicationsAndChooser.add(panelFinalUpper);
    boxExplicationsAndChooser.add(new JSeparator());
    boxExplicationsAndChooser.add(panelFinalLower);
    
    btnMadeBy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent paramActionEvent) { }
    });
    this.add(boxExplicationsAndChooser, BorderLayout.NORTH);
    this.add(dcelPanel, BorderLayout.CENTER);
    
    
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.pack();
    
    // Get the size of the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    // Determine the new location of the window
    int w = this.getSize().width;
    int h = this.getSize().height;
    int x = (dim.width-w)/2;
    int y = (dim.height-h)/2;
    setLocation(x, y);
    
    this.setVisible(true);
  }
  
  private static class PanelOnOff extends JPanel {
    private final ArrayList<ChangeListener> changeListenerList =
            new ArrayList<>();
    
    private boolean isOn;
    public PanelOnOff(String configStr, boolean startIsOn){
      super(new FlowLayout(FlowLayout.LEFT, 0, 0));
      isOn = startIsOn;
      JLabel lblThisName = new JLabel(configStr);
      final JRadioButton radioBtnOn = new JRadioButton("On");
      JRadioButton radioBtnOff = new JRadioButton("Off");
      
      this.setBackground(PANEL_EXPLICATION_COLOR);
      lblThisName.setForeground(COLOR_CONFIG_TEXT);
      
      radioBtnOff.setBackground(PANEL_EXPLICATION_COLOR);
      radioBtnOn.setBackground(PANEL_EXPLICATION_COLOR);
      
      radioBtnOff.setForeground(COLOR_CONFIG_TEXT);
      radioBtnOn.setForeground(COLOR_CONFIG_TEXT);
      
      this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
      
      if(startIsOn)
        radioBtnOn.setSelected(true);
      else
        radioBtnOff.setSelected(true);
      
      ((FlowLayout)this.getLayout()).setHgap(10);
      
      ButtonGroup group = new ButtonGroup();
      group.add(radioBtnOn);
      group.add(radioBtnOff);
      
      radioBtnOn.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          isOn = radioBtnOn.isSelected();  
          for(ChangeListener l : changeListenerList)
            l.stateChanged(new ChangeEvent(PanelOnOff.this));
        }
      });
      
      this.add(lblThisName);
      this.add(radioBtnOn);
      this.add(radioBtnOff);
    }
    
    public boolean isOn(){
      return this.isOn;
    }
    
    public void addOnOffChangedListener(ChangeListener changeListener){
      changeListenerList.add(changeListener);
    }
    
  }
  // =======================================================================
  static class DcelDrawerPanel extends JPanel {
    private final Dcel dcel;
    private static final int POINT_WIDTH = 10; 
    private static final Color POINT_COLOR = Color.BLUE;
    private static final int GRID_POINT_WIDTH = 5;
    private static final Color GRID_POINT_COLOR = Color.GRAY;
    private static final Color GRID_LINE_COLOR = Color.GRAY;
    
    private static final int UNIT_WIDTH = 50;
    private static final int BORDER_GAP = 25;
    
    private static final int START_WIDTH = 500;
    private static final int START_HEIGHT = 500;
    
    private double scale_factor_x = 1.0;
    private double scale_factor_y = 1.0;
    
    private BufferedImage image = null;
    
    private boolean firstOuter = true;
    
    public enum DisplayData { EdgesSet, FacetsSet, Outer, PointsSet }

      private DisplayData dataToDisplay;
    
    private javax.swing.Timer drawTimer = null;
    
    private boolean showGrid, showAxe;
    
    public DcelDrawerPanel(Dcel dcel, DisplayData dataToDisplay,
                           boolean showGrid, boolean showAxe){
      super();
      
      this.showAxe = showAxe;
      this.showGrid = showGrid;
      this.dataToDisplay = dataToDisplay;
      
      setCorrectPreferredSize();
      this.dcel = dcel;
      
      drawTimer = new Timer(500, new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            updateDrawImage();
            repaint();
          }
        });
      
      updateDrawImage();
      
      this.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          scale_factor_x = 1.0 * getWidth() / START_WIDTH;
          scale_factor_y = 1.0 * getHeight() / START_HEIGHT;
          updateDrawImage();
          repaint();
        }
      });
      
      drawTimer.start();
      this.setVisible(true);
    }
    
    public Image getActualImage(){
      return this.image;
    }
    
    public void setDataToDisplay(DisplayData dataToDisplay){
      this.dataToDisplay = dataToDisplay;
      updateDrawImage();
      repaint();
      drawTimer.start();
    }
    
    private void setCorrectPreferredSize(){
      this.setSize(START_WIDTH, START_HEIGHT);
    }
    
    @Override
    public void paint(Graphics g) {
      g.drawImage(image, 0, 0, null);
    }
    
    private void updateDrawImage(){
      int width = Math.max(this.getWidth(), 1);
      int height = Math.max(this.getHeight(), 1);
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      setPreferredSize(new Dimension(width, height));

      Graphics2D g = image.createGraphics();
      
      AffineTransform transformer = new AffineTransform();
      
      transformer.scale(scale_factor_x, scale_factor_y);
      //transformer.translate(0, START_HEIGHT);
      
      
      g.setTransform(transformer);
      g.setStroke(new BasicStroke(2));
      
      updateImageInformation(g, this.dataToDisplay);
      
      // Flip the image vertically
      AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
      tx.translate(0, -image.getHeight(null));
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      image = op.filter(image, null);
    }
    
    public void setShowAxe(boolean showAxe){
      this.showAxe = showAxe;
      updateDrawImage();
      repaint();
    }
    
    public void setShowGrid(boolean showGrid){
      this.showGrid = showGrid;
      updateDrawImage();
      repaint();
    }
    
    private void createGridAndAxis(Graphics g){
      Point startH = getScreenLocationPointFromCoord(new Point(0, -1));
      Point endH = getScreenLocationPointFromCoord(new Point(0, 11));
      Point startV = getScreenLocationPointFromCoord(new Point(-1, 0));
      Point endV = getScreenLocationPointFromCoord(new Point(11, 0));

      if(this.showAxe){
        Color bakColor = g.getColor();
        g.setColor(GRID_LINE_COLOR);
        g.drawLine(startH.x, startH.y, endH.x, endH.y);
        g.drawLine(startV.x, startV.y, endV.x, endV.y);
        g.setColor(bakColor);
      }
      
      if(this.showGrid){
        for(int i = 0; i < 10; i++)
          for(int j = 0; j < 10; j++)
            drawSingleGridPoint(g, new Point(i, j));
      }
    }
    
    private void updateImageInformation(Graphics g, DisplayData dataToDisplay){
      //g.setColor(getBackground());
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, image.getWidth(), image.getHeight());
      g.setColor(Color.BLUE);
      
      // if we shall not display it, it is tested in createGridAndAxis
      createGridAndAxis(g);
      drawEdgesOrPoints(g, dataToDisplay);
    }
    
    private void drawEdgesOrPoints(Graphics g, DisplayData drawMode){
      if(drawTimer.isRunning()){
        try {
          // error handling
          if(dcel == null)
            throw new RuntimeException("Dcel is null");
          
          switch(drawMode) {
            case EdgesSet:
              // error handling
              if(dcel.edgesSet == null)
                throw new RuntimeException("edgesSet is null");
              addEdgesToImage(g, dcel.edgesSet.iterator());
              break;
              
            case FacetsSet:
              // error handling
              if(dcel.facetsSet == null)
                throw new RuntimeException("facetsSet is null");
              addEdgesToImage(g, dcel.facetsSet.iterator());
              break;
              
            case Outer:
              // error handling
              if(dcel.outer == null)
                throw new RuntimeException("outer is null");
              addEdgesToImage(g, dcel.outer.ring().iterator());
              break;
              
            case PointsSet:
              // error handling
              if(dcel.pointsSet == null)
                throw new RuntimeException("pointsSet is null");
              addPointsToImage(g, dcel.pointsSet.iterator());
              break;
          }
        }
        catch(Exception ex){
          drawTimer.stop();
          String strMessage = "Error happened while try to display data for : " + drawMode + "\n" +
            "Errortype: " + ex.getClass() + "\n" + "ErrorMessage: " + ex.getMessage();
          JOptionPane.showMessageDialog(DcelDrawerPanel.this, strMessage, "Error happened", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    
    private void addPointsToImage(Graphics g, Iterator<Point> ptsItr){
      while(ptsItr.hasNext()){
        drawSinglePoint(g, ptsItr.next());
      }
    }
    
    private void addEdgesToImage(Graphics g, Iterator<HalfEdge> halfEdgeItr){
      HalfEdge he;
      ArrayList<HalfEdge> heListInner = new ArrayList<>();
      ArrayList<HalfEdge> heListOuter = new ArrayList<>();
      while(halfEdgeItr.hasNext()){
        he = halfEdgeItr.next();
        if(dcel.outer.ring().contains(he))
          heListOuter.add(he);
        else
          heListInner.add(he);
      }
      
      if(firstOuter){
        for(HalfEdge h : heListOuter)
          drawOuterArrow(g, h);
        for(HalfEdge h: heListInner)
          drawInnerArrow(g, h);
      }
      else{
        for(HalfEdge h: heListInner)
          drawInnerArrow(g, h);
        for(HalfEdge h : heListOuter)
          drawOuterArrow(g, h);
      }
      firstOuter = !firstOuter;
    }
    
    private void drawInnerArrow(Graphics g, HalfEdge arrow){
      drawSinglePoint(g, arrow.origin);
      drawSinglePoint(g, arrow.destination());
      drawArrow(g, false, arrow);
    }
    
    private void drawOuterArrow(Graphics g, HalfEdge arrow){
      drawSinglePoint(g, arrow.origin);
      drawSinglePoint(g, arrow.destination());
      drawArrow(g, true, arrow);
    }
    
    private void drawArrow(Graphics g, boolean isOuter, HalfEdge edges){
      Point start, end;
      start = getScreenLocationPointFromCoord(edges.origin);
      end = getScreenLocationPointFromCoord(edges.destination());
      start = rotate(start, end, -3);
      end = rotate(end, start, 3);    
      drawArrow(g, (isOuter ? Color.RED : Color.GREEN ), start, end);
    }
    
    private void drawArrow(Graphics g, Color drawColor, Point start, Point end){
      int ARR_SIZE = 7;
      Color backupColor = g.getColor();
      g.setColor(drawColor);
      
      double OFFSET = calcAngleInRadian(start, end);  
      double dy = Math.sin(OFFSET) * 18;
      double dx = Math.cos(OFFSET) * 18;
      
      start.setLocation(start.x + dx, start.y + dy);
      end.setLocation(end.x - dx, end.y - dy);

      g.drawLine(start.x, start.y, end.x, end.y);

      Polygon head= new Polygon();
      for (int i = 0; i < 3; i++) {
        // was:   end.x + (i == 0 ? 1 : 1 ) * ...   ??
        head.addPoint((int) (end.x + ARR_SIZE * Math.cos(i * 2 * Math.PI / 3 + OFFSET)),
                      (int) (end.y + ARR_SIZE * Math.sin(i * 2 * Math.PI / 3 + OFFSET)));
      }
      g.fillPolygon(head);     
      g.setColor(backupColor);
    }
    
    private double calcAngleInRadian(Point start, Point end){
      double dX = end.x-start.x;
      double dY = end.y-start.y;
      double pente = Math.atan(dY / dX);
      if(start.x > end.x) pente = pente + degreeToRadian(180);
      return pente;
    }
    
    private void drawSinglePoint(Graphics g, Point p){
      Color bakColor = g.getColor();
      g.setColor(POINT_COLOR);
      Point screenLocation = getScreenLocationPointFromCoord(p);
      g.fillOval((int)(screenLocation.x - POINT_WIDTH / 2), 
                 (int)(screenLocation.y - POINT_WIDTH / 2),
                 POINT_WIDTH,
                 POINT_WIDTH);
      g.setColor(bakColor);
    }
    
    private void drawSingleGridPoint(Graphics g, Point p){
      Color bakColor = g.getColor();
      g.setColor(GRID_POINT_COLOR);
      Point screenLocation = getScreenLocationPointFromCoord(p);
      g.fillOval((int)(screenLocation.x - GRID_POINT_WIDTH / 2), 
                 (int)(screenLocation.y - GRID_POINT_WIDTH / 2),
                 GRID_POINT_WIDTH,
                 GRID_POINT_WIDTH);
      g.setColor(bakColor);
    }
    
    private Point getScreenLocationPointFromCoord(Point coord){
      return new Point(
          (int)(BORDER_GAP + 1.0 * UNIT_WIDTH * coord.x),
          (int)(BORDER_GAP + 1.0 * UNIT_WIDTH * coord.y));
    }
    
    private static Point rotate(Point actualPos, Point rotationPoint, double degree) {
        Point retPoint = new Point();
        retPoint.x = (int)((double)(actualPos.x - rotationPoint.x) * Math.cos(degreeToRadian(degree)) - (actualPos.y - rotationPoint.y) * Math.sin(degreeToRadian(degree)) + rotationPoint.x);
        retPoint.y = (int)((double)(actualPos.y - rotationPoint.y) * Math.cos(degreeToRadian(degree)) + (actualPos.x - rotationPoint.x) * Math.sin(degreeToRadian(degree)) + rotationPoint.y);
        return retPoint;
    }
    
    private static double degreeToRadian(double degree) {
        return (double)degree / 360 * 2 * Math.PI;
    }

  }

}
