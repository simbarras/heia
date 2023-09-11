package ch.epfl.javanco.graphics;

import java.util.EventObject;

public interface Movable extends Clickable {
	void saveNewPosition(EventObject e);
	void setX(int x);
	void setY(int y);
	int getX();
	int getY();
	int getId();
}