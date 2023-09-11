package ch.epfl.general_libraries.gui;

public class DefaultNotifiable<X> implements Notifiable<X> {

	@Override
	public void notify_(X x) {
		try {
			synchronized (this) {
				notify();
			}
		}
		catch (Exception e) {
		}
		System.out.println("Notifiable has been informed that object " + x + " is ready");
	}

}
