package ch.epfl.general_libraries.random.remote;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class PRNMessageSAXParser extends DefaultHandler {


	PRNGSoapMessageReader stream = null;

	private boolean recordVal = false;
	private boolean recordState = false;
	//private boolean recordStateColumn = false;
	private boolean recordStatesSize = false;

	private boolean recordPrngType = false;
	private boolean recordSamples = false;
	private boolean recordSeed = false;



	private int columnRecorded = -1;

	private StringBuffer valRecorded;

	public PRNMessageSAXParser(PRNGSoapMessageReader stream) {
		this.stream = stream;
		valRecorded = new StringBuffer();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) {
		if (localName.equals(RandomWebServiceConstants.SAMPLE_TAG)) {
			recordVal = true;
			return;
		}
		if (localName.equals(RandomWebServiceConstants.STATE_TAG)) {
			recordState = true;
			return;
		}
		if (localName.startsWith(RandomWebServiceConstants.STATE_COLUMN_TAG)) {
			columnRecorded = Integer.parseInt(localName.replace(RandomWebServiceConstants.STATE_COLUMN_TAG,""));
			return;
		}
		if (localName.equals(RandomWebServiceConstants.STATE_SIZE_TAG)) {
			recordStatesSize = true;
			return;
		}
		if (localName.equals(RandomWebServiceConstants.PRNGTYPE_TAG)) {
			recordPrngType = true;
			return;
		}
		if (localName.equals(RandomWebServiceConstants.NUMBER_OF_SAMPLES_TAG)) {
			recordSamples = true;
		}
		if (localName.equals(RandomWebServiceConstants.SEED_TAG)) {
			recordSeed = true;
		}
	}



	@Override
	public void endElement(String uri, String localName, String qName) {
		if (localName.equals(RandomWebServiceConstants.SAMPLE_TAG)) {
			stream.tellVal(Long.parseLong(valRecorded.toString()));
			valRecorded = new StringBuffer();
			recordVal = false;
			return;
		}
		if (localName.equals(RandomWebServiceConstants.STATE_TAG)) {
			stream.tellState(columnRecorded, Integer.parseInt(valRecorded.toString()));
			valRecorded = new StringBuffer();
			recordState = false;
			return;
		}
		if (localName.startsWith(RandomWebServiceConstants.STATE_COLUMN_TAG)) {
			stream.tellNextColumn();
			//		logger.debug("nextColumn");
			return;
		}
		if (localName.equals(RandomWebServiceConstants.STATE_SIZE_TAG)) {
			String[] s = valRecorded.toString().split(",");
			int[] vals = new int[s.length];
			for (int i = 0; i < s.length ; i++) {
				vals[i] = Integer.parseInt(s[i]);
			}
			stream.tellStateSize(vals);
			valRecorded = new StringBuffer();
			recordStatesSize = false;
			return;
		}
		if (localName.equals(RandomWebServiceConstants.PRNGTYPE_TAG)) {
			stream.tellPrngType(valRecorded.toString());
			recordPrngType = false;
			valRecorded = new StringBuffer();
			return;
		}
		if (localName.equals(RandomWebServiceConstants.NUMBER_OF_SAMPLES_TAG)) {
			stream.tellSamples(Integer.parseInt(valRecorded.toString()));
			valRecorded = new StringBuffer();
			recordSamples = false;
		}
		if (localName.equals(RandomWebServiceConstants.SEED_TAG)) {
			stream.tellSeed(Integer.parseInt(valRecorded.toString()));
			valRecorded = new StringBuffer();
			recordSeed = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		if (recordVal || recordState) {
			valRecorded.append(ch, start, length);
			return;
		}
		if (recordStatesSize || recordSeed || recordSamples || recordPrngType) {
			valRecorded.append(ch, start, length);
			return;
		}
	}
}
