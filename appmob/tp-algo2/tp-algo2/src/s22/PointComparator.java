package s22;
import java.util.Comparator;
import java.awt.Point;

public class PointComparator implements Comparator<Point> {
  public static final Comparator<Point> HORIZONTALLY  
           = new PointComparator(false);
  public static final Comparator<Point> VERTICALLY   
           = new PointComparator(true );

  private final boolean isVertical;
  
  private PointComparator(boolean isVertical) {
    this.isVertical = isVertical;
  }

  public int compare (Point a, Point b) {
    if (isVertical) return compareVertically(a, b);
    return compareHorizontally(a, b);
  }

  private static int compareVertically (Point a, Point b) {
    if (a.y < b.y) return -1;
    if (a.y > b.y) return +1;
    if (a.x < b.x) return -1;
    if (a.x > b.x) return +1;
    return 0;
  }

  private static int compareHorizontally (Point a, Point b) {
    if (a.x < b.x) return -1;
    if (a.x > b.x) return +1;
    if (a.y < b.y) return -1;
    if (a.y > b.y) return +1;
    return 0;
  }
}
