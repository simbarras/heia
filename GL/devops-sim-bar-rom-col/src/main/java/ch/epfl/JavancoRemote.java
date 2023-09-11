package ch.epfl;

import java.util.Arrays;

import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.remote.JavancoRmiServer;
import ch.epfl.javanco.remote.JavancoWebServer;
import ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI;

public class JavancoRemote {
	public static void main(String[] args) throws Exception {
		String operationMode = null;
		Integer port = null;
		String site = null;
		String state = ""; // we initialise state to empty string to avoid displaying something
		if(args.length > 0) {
			for (int i = 0 ; i <= args.length - 1 ; i = i+2) {
				if (args[i].equals("-m")) {
					operationMode = args[i+1];
				} else if (args[i].equals("-port")) {
					port = Integer.parseInt(args[i+1]);
				} else if (args[i].equals("-site")) {
					site = args[i+1];
				} else if (args[i].equals("-state")) {
					state = args[i+1];
				} else {
					System.out.println("Wrong usage");
					System.out.println("Accepted parameters are : ");
					System.out.println("-m <operation_mode> ([rmi|web|rmiweb|mixed] - default : rmiweb)");
					System.out.println("-port <port_on_which_run (int)>");
					System.out.println("-site <site to setup - folder in $JAVANCO_HOME/web>");
					System.out.println("-state <in which state you are - typically provide git commit>");
					System.out.println("\r\nGiven parameters were : ");
					System.out.println(Arrays.toString(args));
					System.exit(-1);
				}
			}
		} else {
			site = "web/javanco_online";
			operationMode = "rmiweb";
			port = 8080;
		}
		startServer(operationMode, port, site, state);
	}
		
	public static void startServer(String operationMode, int webPort, String webFolder, String state) throws Exception {
		System.setProperty("ch.epfl.JavancoRemote.state", state);
		if ( operationMode == null || operationMode.equals("rmiweb")) {
			Javanco.initJavanco();
	//		JavancoFile.findAndProcessPropertiesFile(JavancoWebServer.WEB_SERVER_PROPERTIES_FILE);
			JavancoRmiServer.startJavancoRMIServer(Javanco.getDefaultGraphHandlerFactory(), 1099, 20);
			JavancoWebServer.startJavancoWebServer(webPort, webFolder);
		} else if (operationMode.equals("web")) {
			JavancoWebServer.startJavancoWebServer(webPort, webFolder);
		} else 	if (operationMode.equals("rmi")) {
			Javanco.initJavanco();
			JavancoRmiServer.startJavancoRMIServer(Javanco.getDefaultGraphHandlerFactory(), 1099, 20);
		} else if (operationMode.equals("mixed")) {
			Javanco.initJavanco();
		//	JavancoFile.findAndProcessPropertiesFile(Javanco.DEFAULT_PROPERTIES_LOCATION);
			JavancoRmiServer.startJavancoRMIServer(Javanco.getDefaultGraphHandlerFactory(), 1099, 20);
			JavancoWebServer.startJavancoWebServer(webPort, webFolder);
			JavancoDefaultGUI.getAndShowDefaultGUI(true);
		} else {
			throw new Exception("Unkown mode : " + operationMode);
		}
	}
}

