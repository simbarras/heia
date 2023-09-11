package ch.epfl.javanco.ui.swing;


import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class handles the display of popup windows to the user.
 */
public class PopupDisplayer {

	public static void popupErrorMessage(String message, Throwable t, JFrame frame) {
		StringBuffer messageB = new StringBuffer();
		messageB.append(message);
		messageB.append("\nReason : ");
		messageB.append(t.toString());
		t.printStackTrace();
		popupErrorMessage(messageB.toString(), frame);
	}

	/**
	 * Displays a popup window with an error image, "Error" as title and the message parameter as content.
	 * @param message the error message to display to the user.
	 * @param frame the frame in which the popup will be displayed.
	 */
	public static void popupErrorMessage(String message, JFrame frame) {
		popupErrorMessage(message, "Error", frame);
	}

	/**
	 * Displays a popup window with an error image, and the given title and message.
	 * @param message the error message to display to the user.
	 * @param title the title of the popup window (e.g. "Caution").
	 * @param frame the frame in which the popup will be displayed.
	 */
	public static void popupErrorMessage(String message, String title, JFrame frame){
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE) ;
	}

	/**
	 * Displays a popup window with an infomration image, and the given title and message.
	 * @param message the error message to display to the user.
	 * @param title the title of the popup window (e.g. "Caution").
	 * @param frame the frame in which the popup will be displayed.
	 */
	public static void popupInfoMessage(String message, String title, JFrame frame){
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE) ;
	}

}


