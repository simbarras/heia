package ch.epfl.general_libraries.webserver;
/**
 * 
 */

import simple.http.ProtocolHandler;
import simple.http.Request;
import simple.http.Response;

public class HeaderHandler implements ProtocolHandler {

	private ProtocolHandler handler;

	public HeaderHandler(ProtocolHandler handler) {
		this.handler = handler;
	}

	/**
	 * Set the Server Name and the Date in the HTTP Response Headers
	 */
	public void handle(Request req, Response resp) {
		resp.set("Server", "light Server/1.0 (Simple 3.1.3)");
		resp.setDate("Date", System.currentTimeMillis());
		resp.setDate("Last-Modified", System.currentTimeMillis());
		handler.handle(req, resp);
	}
}

