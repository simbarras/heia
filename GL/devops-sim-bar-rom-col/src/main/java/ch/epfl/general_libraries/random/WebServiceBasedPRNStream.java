package ch.epfl.general_libraries.random;

import java.io.IOException;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.random.remote.PRNGSoapMessage;
import ch.epfl.general_libraries.random.remote.PRNGSoapMessageReader;
import ch.epfl.general_libraries.random.remote.PRNMessageSAXParser;
import ch.epfl.general_libraries.random.remote.RandomWebServiceConstants;
import ch.epfl.general_libraries.random.remote.RemotePRNStream;
import ch.epfl.general_libraries.web.SoapMessage;
import ch.epfl.general_libraries.web.WebServiceClient;

public class WebServiceBasedPRNStream extends RemotePRNStream implements PRNGSoapMessageReader {

	private static Logger logger = new Logger(WebServiceBasedPRNStream.class);

	public String url;
	public String generatorName;
	public int seed;
	public boolean useSax;
	public boolean parallel;
	WebServiceClient client;
	public long timePassedWaiting = 0;
	public long timePassedProcessing = 0;

	public WebServiceBasedPRNStream(String url,
			String generatorName,
			int seed,
			int cacheSize,
			int shotSize,
			boolean useSax,
			boolean parallel) {
		super(cacheSize, shotSize, parallel, seed);
		this.url = url;
		this.generatorName = generatorName;
		this.seed = seed;
		this.useSax = useSax;
		this.parallel = parallel;
		client = new WebServiceClient(url);
	}

	@Override
	public PRNStream clone(int seed) {
		return new WebServiceBasedPRNStream(this.url, this.generatorName, seed, cacheSize, shotSize, useSax, parallel);
	}

	@Override
	protected void getNumbers(int n) throws IOException {
		PRNGSoapMessage msg = new PRNGSoapMessage();
		msg.createRequestStructure(n, generatorName);
		if (getStateInt() != null) {
			int[][] state = getStateInt();
			//	logger.debug("State is not null therefore sent with message");
			msg.setState(state);
		} else {
			//			logger.debug("No state detected, sending seed " + seed);
			msg.setSeed(seed);
		}
		sendMessage(msg);
	}

	public void setState(int[][] i) throws IOException{
		PRNGSoapMessage msg = new PRNGSoapMessage();
		msg.createRequestStructure(0, generatorName);
		msg.setState(i);
		sendMessage(msg);
	}

	@Override
	public void setSeedInternal(int i) {
		try {
			PRNGSoapMessage msg = new PRNGSoapMessage();
			msg.createRequestStructure(0, generatorName);
			msg.setSeed(i);
			sendMessage(msg);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void resetInternal() {
		throw new IllegalStateException("Not implemented");
	}


	private void sendMessage(PRNGSoapMessage msg) throws IOException {
		try {
			//			logger.debug("Sending SOAP message to get " + msg.samples + " numbers from remote PRNG");
			//			logger.trace("Message sent is ");
			if (logger.isDebug()) {
				logger.trace(msg.getAssociatedDocument().getRootElement());
				if (msg.stateFirstTen != null) {
					for (int i = 0 ; i < 10 ; i++) {
						logger.debug("Sending message with state ["+i+"] :" + msg.stateFirstTen[i]);
					}
				}
			}
			if (useSax) {
				requestUsingSax(msg);
			} else {
				requestUsingDom(msg);
			}
		}
		catch (IOException e) {
			logger.error("Failed to contact remote PRNServer", e);
			throw e;
		}
	}

	private void requestUsingSax(SoapMessage msg) throws IOException {
		state_pointer = 0;
		state = null;
		long start = System.nanoTime();
		client.call(msg, new PRNMessageSAXParser(this));
		this.setStateInt(state);
		start = (System.nanoTime() - start) / 1000;
		timePassedProcessing += start;
	}

	private void requestUsingDom(SoapMessage msg) throws IOException {
		long start = System.nanoTime();

		SoapMessage resp = new SoapMessage(client.sendMessage(msg));
		start = (System.nanoTime() - start) / 1000;
		timePassedWaiting += start;
		if (logger.isDebug()) {
			logger.debug("Message sent successfully and response arrived. Time required : " + start + "microsecs");

			logger.trace("Message received is ");
			logger.trace(resp.getAssociatedDocument().getRootElement());
		}

		start = System.nanoTime();
		processResponse(resp);
		start = (System.nanoTime() - start) / 1000;
		timePassedProcessing += start;
	}

	private void processResponse(SoapMessage resp) {
		Element body = resp.getBodyElement();
		Element get_prng = body.element(RandomWebServiceConstants.FUNCTION_RESPONSE_TAG);
		processSamples(get_prng);
		processState(get_prng);
	}

	@SuppressWarnings("unchecked")
	private void processSamples(Element get_prng) {
		logger.trace("Buffer before" + getBufferState());

		List<Element> samples = get_prng.element(RandomWebServiceConstants.SAMPLES_TAG).elements();
		for (Node n : samples) {
			Long d = Long.parseLong(n.getText());
			this.writeLong(d);
		}
		logger.trace("Buffer after" + getBufferState());
	}

	private void processState(Element get_prng) {
		this.setStateInt(PRNGSoapMessage.getState(get_prng));
	}

	int[][] state;
	int state_pointer;

	public void tellVal(long l) {
		this.writeLong(l);
	}

	public void tellState(int column, int l) {
		//		logger.debug("State[" + state_pointer + "] set to " + l);
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
	}

	public void tellPrngType(String s) {
	}
	public void tellSeed(int i) {
	}

	public void tellNextColumn() {
		state_pointer = 0;
	}

	@Override
	public String getState() {
		return "state not implemented yet";
	}

	@Override
	public String toString() {
		return "remote:" + this.url + ":" + this.generatorName;
	}

	@Override
	public String getType() {
		return "remote_random_stream_"+url+"_"+generatorName;
	}

	@Override
	public int getSeed() {
		return this.seed;
	}
}
