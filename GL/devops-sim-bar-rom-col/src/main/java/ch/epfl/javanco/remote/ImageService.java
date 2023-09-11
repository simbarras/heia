package ch.epfl.javanco.remote;


import java.io.OutputStream;

import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;

public class ImageService extends AbstractJavancoService {

	public static final int DEFAULT_IMAGE_WIDTH = 500;
	public static final int DEFAULT_IMAGE_HEIGHT = 400;

	public static final String DEFAULT_IMAGE_FORMAT = "png";

	/**
	 * Logger used for intelligent debugging.
	 */
	private static final Logger logger = new Logger(ImageService.class);

	@Override
	public Logger getLogger() {
		return logger;
	}
	public ImageService(Context context) {
		super(context);
	}

	@Override
	public void process(Request req, Response resp) throws java.io.IOException {
		super.startProcess(req,resp);

		//	OutputStreamWriter writer = null;
		try {
			String sessionID = getSessionID(req);

			if (!super.checkFactory()) {
				if (req.getParameter("map") != null) {
					sendDummyMap(req,resp);
				} else {
					sendDummyImage(req,resp);
				}
				return;
			}
			IRemoteGraphWrapper wrapper = super.getCurrentWrapper(sessionID, false);

			if (req.getParameter("map") != null) {
				sendHTMLMap(req,resp,wrapper);
				// return HTML map
			} else {
				sendImage(req,resp, wrapper);
				// return PNG images
			}

			/*	if (req.getPath().getPath().contains("test")) {

				logger.debug(wrapper.getGraphImageHTMLMap(500,400));

			}*/


		}
		catch (Throwable ex) {
			logger.error("Error happend in WebService",ex);
		}
		super.endProcess(req, resp);
	}

	private void sendHTMLMap(Request req, Response resp, IRemoteGraphWrapper wrapper) {
		OutputStream s = null;
		try {
			s = resp.getOutputStream();
			if (wrapper != null) {
				int[] size = getWidthHeigth(req);
				String map = "<img usemap=\"#full\" width=\"" + size[0] + "\" height=\"" + size[1];
				map += "\" border=\"1\" id=\"image_container\" src=\"images/graph" + req.hashCode();
				map += ".png?width=" + size[0] + "&height=" + size[1] + "\"/>";
				map += wrapper.getGraphImageHTMLMap(size[0], size[1]);

				logger.debug("Sending map...");
				s = resp.getOutputStream();

				resp.set("Content-Type", "text/html");
				resp.set("Cache-Control", "public, no-cache");

				s.write(map.getBytes());
				s.flush();
				s.close();
			} else {
				String voidMap = "<img usemap=\"#full\" width=\"500\" height=\"400\" border=\"1\" id=\"image_container\" src=\"images/blank.png\"/>";
				voidMap +="<map name=\"full\"></map>";
				s.write(voidMap.getBytes());
				s.flush();
				s.close();
			}
		}
		catch (Throwable t) {
			if (!t.getMessage().equals("Broken pipe")) {
				logger.error("cannot send map", t);
				try {
					s.close();
				}
				catch (Exception e) {}
			}
		}
	}

	private void sendDummyMap(Request req, Response resp) {
		OutputStream s = null;
		try {
			s = resp.getOutputStream();
			String voidMap = "<img usemap=\"#full\" width=\"500\" height=\"400\" border=\"1\" id=\"image_container\" src=\"images/blank.png\"/>";
			voidMap +="<map name=\"full\"></map>";
			s.write(voidMap.getBytes());
			s.flush();
			s.close();
		}
		catch (Throwable t) {
			logger.error("cannot send map", t);
			try {
				s.close();
			}
			catch (Exception e) {}
		}
	}

	private void sendDummyImage(Request req, Response resp) {
		resp.set("Content-Type", "image/png");
		resp.set("Cache-Control", "public, no-cache");
		super.returnFile("/no_graph.png",resp);
	}

	private void sendImage(Request req, Response resp, IRemoteGraphWrapper wrapper) {
		OutputStream s = null;
		try {
			if (wrapper != null) {
				int[] size = getWidthHeigth(req);
				String format = getImageFormat(req);

				byte[] image = wrapper.getGraphImage(size[0],size[1], format);

				logger.debug("Sending image...");
				s = resp.getOutputStream();

				resp.set("Content-Type", "image/" + format);
				resp.set("Cache-Control", "public, no-cache");

				s.write(image);
				s.flush();
				s.close();
			}
		}
		catch (Throwable t) {
			logger.error("cannot send image", t);
			try {
				s.close();
			}
			catch (Exception e) {}
		}
	}

	private int[] getWidthHeigth(Request req) {
		int width = DEFAULT_IMAGE_WIDTH;
		int height = DEFAULT_IMAGE_HEIGHT;
		try {
			String wp = req.getParameter("width");
			String hp = req.getParameter("height");
			if (wp != null) {
				width = Integer.parseInt(wp);
				if (width > 1000) {
					width = 1000;
				}
			}
			if (hp != null) {
				height = Integer.parseInt(hp);
				if (height > 1000) {
					height = 1000;
				}
			}
		}
		catch (Throwable t)	{}
		logger.trace("Requested image has width =" + width + " and height=" + height);
		return new int[]{width, height};
	}

	private String getImageFormat(Request req) {
		String format = DEFAULT_IMAGE_FORMAT;
		try {
			String pa = req.getPath().getExtension();
			if (format != null) {
				if (format.length() < 5) {
					format = pa;
				}
			}
		}
		catch (Throwable t)	{}
		logger.trace("Requested image format is " + format);
		return format;
	}
}
