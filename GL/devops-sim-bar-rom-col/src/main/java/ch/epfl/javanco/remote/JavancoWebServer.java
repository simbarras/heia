package ch.epfl.javanco.remote;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Properties;

import simple.http.connect.Connection;
import simple.http.connect.ConnectionFactory;
import simple.http.load.MapperEngine;
import simple.http.serve.FileContext;
import simple.http.serve.ProtocolHandlerFactory;
import ch.epfl.general_libraries.webserver.HeaderHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.io.JavancoFile;


/**
 * <object>JavancoServer</object> creates the web server and listens for requests.<br>
 * 
 * @author Christophe Trefois
 *
 */
public class JavancoWebServer extends AbstractServer {
	static Properties properties = null;
	/**
	 * Name of the properties file
	 */
	private final static String WEB_SERVER_PROPERTIES_FILE = "javancoWebServer.properties.xml";
	/**
	 * Name of the property (required) giving the root directory
	 */															   
	public final static String WEBSERVER_PORT_PROPERTY_NAME = "ch.epfl.javancowebserver.port";

	public static void startJavancoWebServer(Integer port, String webserverDir) {
		try {
			System.out.println(" dddddd     " + webserverDir);
			
			Javanco.initJavanco();
			logInit(JavancoWebServer.class, "WEB SERVER");
			
			if (webserverDir.endsWith("/")) {
				webserverDir = webserverDir.substring(0, webserverDir.length()-1);
			}
			logger.debug("Webserver directory is " + webserverDir);

			JavancoFile.findAndProcessPropertiesFile(webserverDir + "/" + WEB_SERVER_PROPERTIES_FILE);
			RemoteDeamon.launchDeamon();

			String previousName = Thread.currentThread().getName();
			Thread.currentThread().setName("Javanco WebServer launcher");
			Connection connection = getConnection(webserverDir);
			if (port == null) {
				port = Integer.parseInt(Javanco.getProperty(WEBSERVER_PORT_PROPERTY_NAME));
			}
			try {
				logger.info("WebServer starting on port [" + port + "]...");
				connection.connect(new ServerSocket(port));
				logger.info("WebServer Started - Status: [SUCCESSFUL]");

			} catch (IOException e) {
				logger.debug("WebServer Not Started - Status: [FAILED]");
				logger.error("IO Exception", e);
				System.exit(-1);
			}
			Thread.currentThread().setName(previousName);
		}
		catch (java.net.BindException e) {
			logger.error("Impossible to start server because another program is already using the port");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static FileContext getContext(String dir) throws Exception {
		if (dir == null) {
			logger.error("Cannot load Javanco WebServer properties file, aborting...");
			System.exit(-1);
		}
		// keep zhis two lines : first on makes the ressources extract and the second create the file
		URL url = JavancoFile.findRessource(dir, true);

		File rootDirFile = new File(url.toURI());

		logger.info("Javanco WebServer root dir is :\r\n-->   " + rootDirFile);

		JavancoFile configDirFile = new JavancoFile(rootDirFile.toString() + File.separator + "config");
		logger.info("Javanco WebServer config dir is :\r\n-->   " + configDirFile);
		FileContext context = new FileContext(
				rootDirFile.getCanonicalFile(),
				configDirFile.getCanonicalFile()
		);

		logger.info("Context defined at :\r\n-->   " + context.getBasePath());
		return context;
	}

	private static Connection getConnection(String webserverDir) throws Exception {
		MapperEngine engine = null;

		FileContext context = getContext(webserverDir);

		try {
			engine = new MapperEngine(context,context.getBasePath());
		} catch (IOException e) {
			logger.error("IO Exception", e);
		}

		HeaderHandler handler = new HeaderHandler(ProtocolHandlerFactory.getInstance(engine));
		return ConnectionFactory.getConnection(handler);
	}
}
