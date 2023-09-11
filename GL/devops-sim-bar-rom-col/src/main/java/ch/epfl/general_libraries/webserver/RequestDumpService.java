package ch.epfl.general_libraries.webserver;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import simple.http.Request;
import simple.http.Response;
import simple.http.load.Service;
import simple.http.serve.Context;
import ch.epfl.general_libraries.io.MultipleWriter;


public class RequestDumpService extends Service {

	public RequestDumpService(Context context) {
		super(context);
	}

	@Override
	public synchronized void process(Request req, Response response) throws Exception {
		response.set("Content-Type", "text/html");
		PrintStream out = response.getPrintStream();

		out.println("<html>");
		out.println("<body>");
		/*
        out.println("This session was created: " + session.get("date"));
        out.println("<br>");
        out.println("This session has been referenced: " + session.get("count"));
        out.println("<br>");
        out.println("Session ID:");
        out.println("</body>");
        out.println("</html>");
        out.close();*/
		OutputStreamWriter outSystem = new OutputStreamWriter(System.out);
		MultipleWriter mp = new MultipleWriter(new OutputStreamWriter(out));
		mp.addDestination(outSystem);
		mp.write("\r\nContent of HTTP header : \r\n\r\n");
		mp.write(">>> "+ req.getMethod() + " : " + req.getURI());
		int count = req.headerCount();
		for (int i = 0 ; i < count ; i++) {
			String value = req.getValue(i);
			String name  = req.getName(i);
			if (name.length() < 20) {
				name = name.concat("                     ");
			}
			if (value.length() < 20) {
				name = name.concat("                     ");
			}
			if (name.length() >= 20) {
				name = name.substring(0,19);
			}
			if (value.length() >= 50) {
				value = value.substring(0,49);
			}
			mp.write("    " + name + ": " + value);
			System.out.println();
			out.println("<br/>");
		}
		mp.write("\r\n\r\nContent of HTTP body : \r\n\r\n");
		int r;
		InputStreamReader isr = new InputStreamReader(req.getInputStream());
		while ((r =isr.read()) >= 0) {
			mp.write(r);
		}

		out.println("</body>");
		out.println("</html>");
		mp.removeDestination(outSystem);
		mp.close();
		out.close();
	}
}
