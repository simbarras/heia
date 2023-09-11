package ch.epfl.general_libraries.random.remote;

import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.ParserAdapter;

import simple.http.Request;
import simple.http.Response;
import simple.http.load.Service;
import simple.http.serve.Context;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.random.JavaBasedPRNStream;
import ch.epfl.general_libraries.random.MersenneTwister;
import ch.epfl.general_libraries.random.ModMT19937BasedPRNStream;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.web.SoapMessage;

public class PRGWebService extends Service implements PRNGSoapMessageReader {

	/**
	 * Logger used for intelligent debugging.
	 */
	private static final Logger logger = new Logger(PRGWebService.class);

	private boolean sax;
	private boolean direct;
	int[][] state;
	int state_pointer;
	String function;
	int samples;
	int seed;

	public PRGWebService(Context context) throws Exception {
		super(context);
		sax = Boolean.parseBoolean(System.getProperty("ch.epfl.random.PRGWebService.useSax"));
		direct = Boolean.parseBoolean(System.getProperty("ch.epfl.random.PRGWebService.direct"));
	}

	@Override
	public void process(Request req, Response resp) /*throws Exception*/ {
		String threadName = Thread.currentThread().getName();
		try {

			Thread.currentThread().setName("PRGService");
			//     		logger.debug("Entering PRNWebService");

			// get SOap message from request
			if (sax) {
				parseUsingSaxAndReplyDirectly(req.getInputStream(), resp);
			} else {
				parseAndReplyUsingDom(req, resp);
			}


			resp.commit();

		}
		catch (Exception e) {
			logger.error("Exception occured in PRGWebservice",e);
		}
		finally {
			Thread.currentThread().setName(threadName);
			try {
				resp.getOutputStream().close();
			}
			catch (IOException e) {
				logger.error("Error while closing stream");
			}
		}
	}

	private synchronized void parseUsingSaxAndReplyDirectly(InputStream stream, Response resp) throws Exception  {
		try {
			//			logger.debug("Parsing request using SAX");
			state_pointer = 0;
			state = null;
			System.setProperty("org.xml.sax.parser","org.apache.xerces.parsers.SAXParser");
			ParserAdapter parser = new ParserAdapter();
			parser.setContentHandler(new PRNMessageSAXParser(this));
			parser.parse(new InputSource(stream));

			//	logger.debug("Parsing done");
			if (direct) {
				sendResponseDirectly(function, samples, seed, state, null, resp);
			} else {
				createAndSendResponse(function, samples, seed, state, null, resp);
			}

		}
		catch (SAXException e) {
			throw new IOException("Impossible to reply ",e);
		}
	}

	public void tellVal(long l) {
	}

	public void tellState(int column, int l) {
		/*	if ( state_pointer < 5) {
			logger.debug("State["+column+"][" + state_pointer + "] set to " + l);
		}*/
		try {
			state[column][state_pointer] = l;
			state_pointer++;
		}
		catch (Exception e) {
			logger.error("Exception", e);
			System.exit(0);
		}
	}

	public void tellStateSize(int[] sizes) {
		state = new int[sizes[0]][];
		for (int i = 0 ; i < state.length ; i++) {
			state[i] = new int[sizes[i+1]];
		}
	}

	public void tellSamples(int i) {
		samples = i;
	}

	public void tellNextColumn() {
		state_pointer = 0;
	}

	public void tellPrngType(String s) {
		function = s;
	}
	public void tellSeed(int i) {
		seed = i;
	}

	private void createAndSendResponse(String function,int samples,Integer seed, int[][] state,Namespace msgNameSpace,Response resp) throws Exception  {
		PRNStream source = getPRNSource(function, state, seed);
		// initialise return message
		SoapMessage respSoap = createResponseMessage(samples, source, msgNameSpace);

		//	 	logger.debug("Sending message");
		//	 	logger.trace(respSoap.getBodyElement());
		respSoap.writeMessage(resp.getOutputStream());
		resp.setText("HTTP/1.1 200 OK");
		resp.set("Content-Type", "text/xml"); // Important to set XML Content-Type !!
		resp.set("test-test", "test");
	}

	private PRNStream getPRNSource(String function, int[][] state__, Integer seed) throws ClassNotFoundException, IOException {
		PRNStream source;
		if (state__ != null) {
			String stateStr = "";
			if (logger.isDebug()) {
				for (int i = 0 ; i < 2 ; i++) {
					stateStr += " " + state__[0][i];
				}
			}
			if (logger.isDebug()) {
				logger.info("Function : " + function + "| Samples : " + samples + " | state " + state__[0].length + "," + stateStr );
			}
			source = getPRNSource(function, state__);
		} else {
			//			logger.info("Function : " + function + " | Samples : " + samples + " | seed " + seed);
			source = getPRNSource(function, seed);
		}
		return source;
	}

	private void sendResponseDirectly(String function,
			int samples,
			int seed,
			int[][] state,
			Namespace msgNameSpace,
			Response resp) throws Exception  {
		PRNStream source = getPRNSource(function, state, seed);
		java.io.OutputStream output = resp.getOutputStream();
		java.io.ByteArrayOutputStream byteA = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream bb = new java.io.DataOutputStream(byteA);
		bb.writeBytes("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope\"");
		bb.writeBytes(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		bb.writeBytes(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
		bb.writeBytes(" xmlns:soap-enc=\"http://schemas.xmlsoap.org/soap/encoding/\"");
		bb.writeBytes(" SOAP-ENV:encodingstyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
		bb.writeBytes("<SOAP-ENV:Header/><SOAP-ENV:Body>");
		bb.writeBytes("<m:get_prngResponse xmlns:m=\"http://tcom.epfl.ch/pnrgweb/v1\">");
		bb.writeBytes("<m:nb_samples>");
		bb.writeBytes(samples + "");
		bb.writeBytes("</m:nb_samples><m:samples>");
		for (int i = 0 ; i < samples ; i++) {
			bb.writeBytes("<m:v>");
			bb.writeBytes(source.nextUnsignedInt() + "");
			bb.writeBytes("</m:v>");
		}
		bb.writeBytes("</m:samples><m:state_size>");
		state = getStateTab(source);
		bb.writeBytes(state.length + ",");
		for (int i = 0 ; i < state.length ; i++) {
			bb.writeBytes(state[i].length+"");
			if (i < state.length - 1) {
				bb.writeBytes(",");
			}
		}
		bb.writeBytes("</m:state_size><m:state>");
		for (int i = 0 ; i < state.length ; i++) {
			bb.writeBytes("<m:c");
			bb.writeBytes(i + ">");
			for (int j = 0 ; j < state[i].length ; j++) {
				bb.writeBytes("<m:s>");
				bb.writeBytes(state[i][j] + "</m:s>");
			}
			bb.writeBytes("</m:c");
			bb.writeBytes(i + ">");
		}
		bb.writeBytes("</m:state></m:get_prngResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>");
		byteA.writeTo(output);
	}



	private void parseAndReplyUsingDom(Request req, Response resp) throws Exception {
		SoapMessage msg = new SoapMessage(req.getInputStream());

		// get document from soapMessage
		Document doc = msg.getAssociatedDocument();
		logger.debug("Extracting info from request");
		logger.trace(doc.getRootElement());
		Element body = msg.getBodyElement();
		Element call = body.element(RandomWebServiceConstants.FUNCTION_REQUEST_TAG);
		Namespace msgNameSpace = call.getNamespace();
		String function__ = getFunction(call).getText();
		Integer samples__ = getSamples(call);
		//	String output = getOutput(call).getText();
		int[][] state__ = getState(call);
		Integer seed__ = getSeed(call);

		/*	System.out.println(function__);
		System.out.println(samples__);
		System.out.println(seed__);
		System.out.println(state__);
		System.out.println(msgNameSpace);
		System.out.println(resp);*/

		//		cAndSendResponse(function__, (int)samples__, (int)seed__, state__, msgNameSpace, resp);
		createAndSendResponse(function__, samples__, seed__, state__, msgNameSpace, resp);
	}

	private PRNStream getPRNSource(String function, int[][] state) throws ClassNotFoundException {
		PRNStream source = createPRNSource(function, 0);
		source.setStateInt(state);
		return source;
	}

	private PRNStream getPRNSource(String function, Integer seed) throws ClassNotFoundException, IOException {
		PRNStream source = createPRNSource(function, seed);
		source.setSeed(seed);
		return source;
	}

	private PRNStream createPRNSource(String function, int seed) {
		if (function.equals("mt19937")) {
			return new ModMT19937BasedPRNStream();
		} else if (function.equals("fourTap")) {
			return new MersenneTwister(seed);
		} else {
			return new JavaBasedPRNStream(seed);
		}
	}

	private int[][] getStateTab(PRNStream source) {
		//	logger.debug("Adding state of PRNG...");
		int[][] stateTab = null;
		try {
			stateTab = source.getStateInt();
		}
		catch (Exception e) {
			logger.error("Error when extracting state for PRNG",e);
		}
		return stateTab;
	}

	private SoapMessage createResponseMessage(int samples, PRNStream source, Namespace msgNameSpace) {
		//	logger.debug("Creating response message");
		PRNGSoapMessage respSoap = new PRNGSoapMessage();
		respSoap.createResponseStructure(samples);

		//	logger.debug("Adding samples");
		for (int i = 0 ; i < samples ; i++) {
			respSoap.addSample(source.nextUnsignedInt());
		}

		respSoap.setState(getStateTab(source));

		return respSoap;
	}

	public Element getFunction(Element get_prng) {
		Element e = get_prng.element(RandomWebServiceConstants.PRNGTYPE_TAG);
		return e;
	}

	public Integer getSeed(Element get_prng) {
		Element e = get_prng.element(RandomWebServiceConstants.SEED_TAG);
		if (e == null) {
			return null;
		} else{
			return Integer.parseInt(e.getText());
		}
	}

	public Integer getSamples(Element get_prng) {
		Element e = get_prng.element(RandomWebServiceConstants.NUMBER_OF_SAMPLES_TAG);
		if (e == null) {
			return null;
		} else {
			return Integer.parseInt(e.getText());
		}
	}

	public Element getOutput(Element get_prng) {
		Element e = get_prng.element(RandomWebServiceConstants.OUTPUT_TAG);
		return e;
	}

	public int[][] getState(Element get_prng) {
		return PRNGSoapMessage.getState(get_prng);

	}

}
