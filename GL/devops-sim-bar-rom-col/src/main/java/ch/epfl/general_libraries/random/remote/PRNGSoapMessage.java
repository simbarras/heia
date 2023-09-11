package ch.epfl.general_libraries.random.remote;

import java.util.List;
import java.util.Vector;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultNamespace;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.general_libraries.web.SoapMessage;


public class PRNGSoapMessage extends SoapMessage {

	private static Logger logger = new Logger(PRNGSoapMessage.class);

	Namespace funcNameSpace = null;
	Element get_prng = null;
	Element samplesEl = null;
	QName valuesQname = null;

	int samples;
	public int[] stateFirstTen;

	public PRNGSoapMessage() {
		funcNameSpace
		= new DefaultNamespace(RandomWebServiceConstants.FUNCTION_NAMESPACE_PREFIX,
				RandomWebServiceConstants.FUNCTION_NAMESPACE_URI);
		stateFirstTen = new int[10];
	}

	public PRNGSoapMessage(Namespace ns) {
		funcNameSpace = ns;
		stateFirstTen = new int[10];
	}

	public void setState(int[][] state) {
		Element stateSizeElement = new DefaultElement(new QName(RandomWebServiceConstants.STATE_SIZE_TAG, funcNameSpace));
		get_prng.add(stateSizeElement);
		Element stateElement = new DefaultElement(new QName(RandomWebServiceConstants.STATE_ELEMENT_TAG, funcNameSpace));
		get_prng.add(stateElement);


		if (state != null) {
			QName stateQ = new QName(RandomWebServiceConstants.STATE_TAG, funcNameSpace);
			StringBuffer stateSize = new StringBuffer();
			stateSize.append(state.length + ",");
			for (int i = 0 ; i < state.length ; i++) {
				QName columnQ = new QName(RandomWebServiceConstants.STATE_COLUMN_TAG + i, funcNameSpace);
				Element columnE = new DefaultElement(columnQ);
				stateElement.add(columnE);
				for ( int j = 0 ; j < state[i].length ; j++) {
					Element st = new DefaultElement(stateQ);
					st.setText(state[i][j] + "");
					columnE.add(st);
				}
				for (int j = 0 ; j < 10 ; j++) {
					stateFirstTen[j] = state[0][j];
				}
				if (i +1 < state.length) {
					stateSize.append(state[i].length + ",");
				} else {
					stateSize.append(state[i].length);
				}
			}
			stateSizeElement.setText(stateSize.toString());
		}
	}

	public void setSeed(int seed) {
		Element seedElement = new DefaultElement(new QName(RandomWebServiceConstants.SEED_TAG, funcNameSpace));
		seedElement.setText(seed + "");
		seedElement.addAttribute(RandomWebServiceConstants.XSI_TYPE,RandomWebServiceConstants.XSD_INT);
		get_prng.add(seedElement);
	}

	public void createRequestStructure(int n, String generatorName) {
		createSOAPEnvelope();
		samples = n;

		get_prng = new DefaultElement(new QName(RandomWebServiceConstants.FUNCTION_REQUEST_TAG, funcNameSpace));
		Element function = new DefaultElement(new QName(RandomWebServiceConstants.PRNGTYPE_TAG, funcNameSpace));
		function.setText(generatorName);
		function.addAttribute(RandomWebServiceConstants.XSI_TYPE,RandomWebServiceConstants.XSD_STRING);
		Element samples = new DefaultElement(new QName(RandomWebServiceConstants.NUMBER_OF_SAMPLES_TAG, funcNameSpace));
		samples.setText(n + "");
		samples.addAttribute(RandomWebServiceConstants.XSI_TYPE,RandomWebServiceConstants.XSD_INT);
		Element output = new DefaultElement(new QName(RandomWebServiceConstants.OUTPUT_TAG, funcNameSpace));
		output.setText("SOAP");
		output.addAttribute(RandomWebServiceConstants.XSI_TYPE,RandomWebServiceConstants.XSD_STRING);

		get_prng.add(function);
		get_prng.add(samples);
		get_prng.add(output);

		Element body = getBodyElement();
		body.add(get_prng);
	}

	public void createResponseStructure(int samples) {
		createSOAPEnvelope();

		valuesQname = new QName(RandomWebServiceConstants.SAMPLE_TAG,funcNameSpace);
		get_prng = new DefaultElement(new QName(RandomWebServiceConstants.FUNCTION_RESPONSE_TAG, funcNameSpace));

		Element number = new DefaultElement(new QName(RandomWebServiceConstants.NUMBER_OF_SAMPLES_TAG, funcNameSpace));
		number.setText(samples + "");
		get_prng.add(number);
		samplesEl = new DefaultElement(new QName(RandomWebServiceConstants.SAMPLES_TAG, funcNameSpace));
		get_prng.add(samplesEl);

		Element body = getBodyElement();
		body.add(get_prng);
	}

	public void addSample(long s) {
		if (samplesEl != null) {
			Element val = new DefaultElement(valuesQname);
			val.setText(s + "");
			samplesEl.add(val);
		}
	}

	@SuppressWarnings("unchecked")
	public static int[][] getState(Element get_prng) {

		Element stateEl = get_prng.element(RandomWebServiceConstants.STATE_ELEMENT_TAG);
		if (stateEl == null) {
			logger.debug("SoapMessage has no state");
			return null;
		}

		Element stateSize = get_prng.element(RandomWebServiceConstants.STATE_SIZE_TAG);
		if (stateSize == null) {
			logger.debug("SoapMessage has no state");
			return null;
		}
		String text = stateSize.getText();
		if (text.equals("")) {
			logger.debug("SoapMessage has no state");
			return null;
		}
		String[] vals = text.split(",");
		int[] intVals = new int[vals.length];
		for (int i = 0 ; i < vals.length ; i++) {
			intVals[i] = Integer.parseInt(vals[i]);
		}


		Vector<Element> vect = new Vector<Element>(intVals[0]);
		vect.setSize(intVals[0]);
		for (int i = 0 ; i < intVals.length - 1 ; i++) {
			String columnName = RandomWebServiceConstants.STATE_COLUMN_TAG + i;
			Element c = stateEl.element(columnName);
			vect.set(i,c);
		}
		if (vect.size() == 0) {
			return null;
		}
		int[][] state = new int[vect.size()][];
		for (int i = 0 ; i < vect.size() ; i++) {
			List<Node> values = vect.get(i).elements();
			state[i] = new int[values.size()];
			for (int j = 0 ; j < state[i].length ; j++) {
				state[i][j] = Integer.parseInt(values.get(j).getText());
			}
		}
		return state;
	}
}