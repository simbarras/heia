package ch.epfl.javanco.graphics;


public class NodeCoord implements ElementCoord {
	int x;
	int y;
	int width;
	int height;
	@Override
	public String toString() {
		return "NodeCoord : [" + x + "," + y + "], size [" + width + "," + height + "]";
	}
	public void reverseYCoord(int height) {
		y = height - y;
	}
	public int[] getCoords() {
		return new int[]{x+Math.round(width/2f),y+Math.round(height/2f), Math.round(height/2f)};
	}
}