package ch.epfl.javancox.topogen_webapp;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javancox.topogen.PrimitiveTypeGeneratorStub;
import simple.http.Request;
import simple.http.Response;
import simple.http.serve.Content;
import simple.http.serve.Context;
import simple.util.net.Parameters;
import ch.epfl.general_libraries.clazzes.ClassLister;
import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.webserver.AbstractService;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javancox.topology_analysis.GraphPropertiesFormatter;

class GeneratorProxy {
	String generatorName;
	ArrayList<Method> methods;
	PrimitiveTypeGeneratorStub gen;

	StringBuffer getGeneratorTab() {
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"tabbertab\"><h2>");
		sb.append(generatorName);
		sb.append("</h2><p>");
		sb.append(getMethodsContent());
		sb.append("</p></div>");
		return sb;
	}

	StringBuffer getMethodsContent() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for (Method m : methods) {
			sb.append("<fieldset style=\"margin: 1em; text-align: left;\">");
			sb.append("<legend>Method : " + m.getName() + "</legend>");
			Annotation[][] anots = m.getParameterAnnotations();
			sb.append("<form id='input"+m.getName()+""+index+"' name=\"input\" action=\"topogen\" method=\"POST\">");
			sb.append("<table><tr><th>Parameters : </th><th></th></tr>");
			for (int i = 0 ; i < anots.length ; i++) {
				sb.append("<tr>");
				for (Annotation anot : anots[i]) {
					if (anot.annotationType().equals(ParameterDef.class)) {
						sb.append("<td>");
						sb.append(((ParameterDef)anot).name());
						sb.append("</td>");
					}
				}
				sb.append("<td>");
				if (anots[i].length == 1) {
					if (anots[i][0].annotationType().equals(ParameterDef.class)) {
						int argLength = ((ParameterDef)anots[i][0]).maxArgLength();
						sb.append("<input type=\"text\" name=\"" + i +"\" value=\"\" size=\""+argLength+"\" maxlength=\""+argLength+"\"/>");
					}
				} else {
					for (Annotation anot : anots[i]) {
						if (anot.annotationType().equals(ForcedParameterDef.class)) {
							for (String s : ((ForcedParameterDef)anot).possibleValues()) {
								sb.append("<input type=\"radio\" name=\""+i+"\"  value=\"" + s + "\"/>" + s);
							}
						}
					}
					sb.append("<p/>");
				}
				sb.append("</td></tr>");
			}
			sb.append("</table><table><tr>");
			sb.append("<input type=\"hidden\" name=\"generatorName\" value=\"" + generatorName +"\"/>");
			sb.append("<input type=\"hidden\" name=\"methodName\" value=\"" + m.getName() +"\"/>");
			sb.append("<input type=\"hidden\" id='format"+m.getName()+""+index+"' name=\"format\" value=\"\"/>");
			sb.append("\r\n");			
			sb.append("<tr><td></td><td>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='PNG-S';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"See PNG\"/>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='XML-S';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"See XML\"/>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='TXT-S';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"See TXT\"/>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='SVG-S';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"See SVG\"/>");
			sb.append("</td><td>");
			sb.append("\r\n");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='STATS';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"See properties\"/>");
			sb.append("</td></tr><tr><td></td><td>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='PNG';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"Download PNG\"/>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='XML';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"Download XML\"/>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='TXT';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"Download TXT\"/>");
			sb.append("</td><td><input type=\"button\" onclick=\"document.getElementById('format"+m.getName()+""+index+"').value='SVG';document.getElementById('input"+m.getName()+""+index+"').submit();\" value=\"Download SVG\"/>");
			sb.append("\r\n");
			sb.append("</tr></table></form></fieldset>");
			index++;
		}
		return sb;
	}
}


public class TopoGenWebInterface extends AbstractService {

	/**
	 * Name of the properties file
	 */
	public final static String TOPOGEN_PROPERTIES_FILE = "div/javancoTopologyGenerators.properties";

	/**
	 * Logger used for intelligent debugging.
	 */
	private static final Logger logger = new Logger(TopoGenWebInterface.class);

	private ArrayList<GeneratorProxy> proxies = new ArrayList<GeneratorProxy>();
	
	
	private static GraphPropertiesFormatter formatter = new GraphPropertiesFormatter();	

	public Logger getLogger() {
		return logger;
	}

	public static void main(String[] args) {
		if (args.length <= 0) {
			System.out.println("Args are required : prefixes");
			System.exit(0);
		}
		Javanco.initJavancoUnsafe();
		List<String> prefixes = (List<String>)java.util.Arrays.asList(args);
		ClassLister<PrimitiveTypeGeneratorStub> cl = new ClassLister<PrimitiveTypeGeneratorStub>(prefixes, PrimitiveTypeGeneratorStub.class);
		try {
			FileWriter fw = new FileWriter(TOPOGEN_PROPERTIES_FILE);
			ArrayList<String> genList = new ArrayList<String>();
			for (Class<PrimitiveTypeGeneratorStub> c : cl.getSortedClasses()) {
				genList.add(c.getName() + "\r\n");
			}			
			Collections.sort(genList);
			for (String s : genList) {
				fw.append(s);
			}
			fw.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public TopoGenWebInterface(Context context) throws Exception {
		super(context);
		try {
			ClassLister<PrimitiveTypeGeneratorStub> cl = new ClassLister<PrimitiveTypeGeneratorStub>(
					Javanco.getProperty(Javanco.JAVANCO_DEFAULT_CLASSPATH_PREFIXES_PROPERTY).split(";"), PrimitiveTypeGeneratorStub.class);
			for (Class<PrimitiveTypeGeneratorStub> c : cl.getSortedClasses()) {
				registerGenerator(c);
			}
		}
		catch (Error e) {
			URL url = JavancoFile.findRessource(TOPOGEN_PROPERTIES_FILE);
			BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = read.readLine()) != null) {
				Class<?> c = Class.forName(line);
				if (PrimitiveTypeGeneratorStub.class.isAssignableFrom(c)) {
					registerGenerator(c);
					logger.info("Generator registered : " + c);
				}
			}
		}
	}

	private void registerGenerator(Class<?> c) throws Exception {
		logger.debug("Found generator " + c.getSimpleName());
		GeneratorProxy proxy = new GeneratorProxy();
		proxy.gen = (PrimitiveTypeGeneratorStub)c.getConstructor(new Class[]{}).newInstance(new Object[]{});
		proxy.generatorName = proxy.gen.getClass().getSimpleName();
		ArrayList<Method> met = new ArrayList<Method>();
		for (Method m : c.getMethods()) {
			if (m.getAnnotation(MethodDef.class) != null) {
				logger.debug("Found method " + m.getName());
				met.add(m);
			}
		}
		proxy.methods = new ArrayList<Method>(met);
		proxies.add(proxy);
	}

	private String getTabs() {
		StringBuffer sb = new StringBuffer();
		for (GeneratorProxy proxy : proxies) {
			sb.append(proxy.getGeneratorTab());
		}
		return sb.toString();
	}

	private int countParameters(Request req) throws IOException {
		int i = 0;
		while (req.getParameter((i+1)+"") != null) {
			i++;
		}
		return i;
	}

	private void processParametersRequest(Request req, Response resp) throws IOException {
		int params = countParameters(req);
		for (GeneratorProxy proxy : proxies) {
			if (proxy.generatorName.equals(req.getParameter("generatorName"))) {
				for (Method m : proxy.methods) {
					if (m.getName().equals(req.getParameter("methodName"))) {
						if (m.getParameterTypes().length == params+1) {
							String format = req.getParameter("format");
							executeMethodAndSendResult(req.getParameters(), m, resp, format);
							return;
						}
					}
				}
			}
		}
		try {
			resp.setCode(500);
			java.io.OutputStreamWriter ow = new java.io.OutputStreamWriter(resp.getOutputStream());
			ow.append("Unkown method. Try to reload page or erase cache");
			ow.close();
		}
		catch (Exception ex) {}		
	}

	private void executeMethodAndSendResult(Parameters list, Method m, Response resp, String format) {
		try {
			try {
				int paramNumber = m.getParameterTypes().length;
				Object[] params = new Object[paramNumber];
				AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(true);
				agh.activateMainDataHandler();
				agh.activateGraphicalDataHandler();
				agh.newLayer("physical");
				params[0] = agh;
				Class[] paramTypes = m.getParameterTypes();
				for (int i = 1 ; i < paramNumber ; i++) {
					if (paramTypes[i].equals(String.class)) {
						params[i] = (String)list.get(i+"");
					} else if (paramTypes[i].equals(Integer.TYPE)) {
						params[i] = new Integer((String)list.get(i+""));
					} else if (paramTypes[i].equals(Boolean.TYPE)) {
						params[i] = new Boolean((String)list.get(i+""));
					} else if (paramTypes[i].equals(Float.TYPE)) {
						params[i] = new Float((String)list.get(i+""));
					}
				}
				logger.info("Generating a new topology with generator " + m.getDeclaringClass().getSimpleName() + " and method " + m.getName());
				PrimitiveTypeGeneratorStub gen = (PrimitiveTypeGeneratorStub)(m.getDeclaringClass().newInstance());
				String s = (String)m.invoke(gen,params);
				if (s != null) {
					resp.set("Content-Type", "text/html");				
					Content cont = context.getContent("/generatorError.html");
					java.io.OutputStreamWriter ow = new java.io.OutputStreamWriter(resp.getOutputStream());
					String sss = cont.toString().replace("#ERROR TEXT#", s);
					ow.write(sss);
					ow.flush();
				} else {
	
					gen.setLengths(((AbstractGraphHandler)params[0]));
		
					setRespFormat(format, resp, m);
					formatGraph(format, agh, resp.getOutputStream());
					logger.info("Graph sent successfully");
				}
			}
			catch (java.lang.reflect.InvocationTargetException e) {
				if (e.getCause() != null) {
					resp.setCode(500);
					java.io.OutputStreamWriter ow = new java.io.OutputStreamWriter(resp.getOutputStream());
					ow.append("Cannot serve the demand : " + e.getCause().getMessage());
					ow.close();				
				} else {
					throw e;
				}
			}
		}
		catch (Exception e) {
			try {
				resp.setCode(500);
				java.io.OutputStreamWriter ow = new java.io.OutputStreamWriter(resp.getOutputStream());
				ow.append("Cannot serve the demand.");
				ow.close();
			}
			catch (Exception ex) {}
		}
	}
	
	private void setRespFormat(String format, Response resp, Method m) {
		if (format.equals("PNG-S")) {
			resp.set("Content-Type", "image/png");
		} else if (format.equals("SVG-S")) {
			resp.set("Content-Type", "image/svg+xml");
		} else if (format.equals("XML-S")) {
			resp.set("Content-Type", "text/xml");
		} else if (format.equals("TXT-S")) {
			resp.set("Content-Type", "text/plain");
		} else if (format.equals("STATS")) {
			resp.set("Content-Type", "text/html");
		} else {
			resp.set("Content-Type", "application/x-download");
			resp.set("Content-Disposition", "attachment; filename=" + m.getName()+"."+ format.toLowerCase());
		}
	}

	private void formatGraph(String format, AbstractGraphHandler agh, OutputStream out) {
		try {
			if (format.startsWith("PNG")) {
				agh.saveGraphImage(out, 800, 800);
			} else if (format.startsWith("SVG")) {
				agh.saveGraphSVG(out, 800, 800);
				//resp.set("Content-Type", "image/svg+xml");
			} else if (format.startsWith("XML-S")) {
				agh.dumpGraph(out);
				//	resp.set("Content-Type", "text/xml");
			} else if (format.startsWith("TXT-S")) {
				agh.dumpGraph(out, "txt");
				//	resp.set("Content-Type", "text/plain");
			} else if (format.equals("STATS")) {
				formatter.format(agh, out);
			} else {
				agh.dumpGraph(out);
			}
			out.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public synchronized void process(Request req, Response resp) {
		super.startProcess(req,resp);
	//	Document documentToSend = null;
		resp.set("Content-Type", "text/html");
		OutputStream out = null;
		try {
			out = resp.getOutputStream();
			if (req.getParameters().size() > 0) {
				processParametersRequest(req,resp);
			} else {
				Content cont = context.getContent("/callGenerator.html");
				java.io.OutputStreamWriter ow = new java.io.OutputStreamWriter(out);
				String s = cont.toString().replace("#MORE_TAB_TAG#", getTabs());
				ow.write(s);
				ow.flush();
			}
			out.close();
		} catch (Exception e) {
			logger.error("Unhandled Problem", e);
		} finally { // Send the response back to the browser
			try { 
				out.close();	
			} catch (Exception e) {
			}
		}
		super.endProcess(req, resp);
	}
}

