package ch.epfl.general_libraries.webserver;
import java.io.PrintStream;
import java.util.Date;

import simple.http.Request;
import simple.http.Response;
import simple.http.load.Service;
import simple.http.serve.Context;
import simple.http.session.Session;

/**
 * @author Trefex
 *
 */
public class HelloSessionService extends Service {

	public HelloSessionService(Context context) {
		super(context);
	}

	@Override
	public void process(Request request, Response response) throws Exception {
		Session session = request.getSession();
		PrintStream out = response.getPrintStream();
		System.out.println(request);
		if(session.isEmpty()) {
			session.put("date", new Date());
		}
		Integer count = new Integer(0);

		if(session.contains("count")) {
			count = (Integer)session.get("count");
		}
		session.put("count", new Integer(count.intValue() + 1));

		response.set("Content-Type", "text/html");

		out.println("<html>");
		out.println("<body>");
		out.println("This session was created: " + session.get("date"));
		out.println("<br>");
		out.println("This session has been referenced: " + session.get("count"));
		out.println("<br>");
		out.println("Session ID:");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
}
