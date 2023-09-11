package ch.epfl.javancox.topogen_webapp;

import ch.epfl.JavancoRemote;

public class RunTopoServer {

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			JavancoRemote.startServer("rmiweb", Integer.parseInt(args[0]), "web/generators_online", "-");
		} else {
			JavancoRemote.startServer("rmiweb", 8080, "web/generators_online", "-");
		}
	}

}
