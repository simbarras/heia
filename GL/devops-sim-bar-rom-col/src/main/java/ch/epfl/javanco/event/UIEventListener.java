package ch.epfl.javanco.event;

public interface UIEventListener {

	public void displayWarning(String s);
	public void displayWarning(String s, Throwable t);

}