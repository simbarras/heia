package s22;
import java.awt.Point;

public class Segm {
  private Point from;
  private Point to;
  public Segm(Point a, Point b) { from=a; to=b; }

  public Point from() { return from; }
  public Point to  () { return to;   }
}
