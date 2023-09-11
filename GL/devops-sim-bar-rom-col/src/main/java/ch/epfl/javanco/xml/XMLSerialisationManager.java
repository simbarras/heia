package ch.epfl.javanco.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultDocument;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.io.JavancoFile;

/**
 * This class handles the serialisation of the network to an XML file and the deserialisation
 * (and validation) of an XML file to a network document.
 * @author lchatela
 */
public class XMLSerialisationManager {

	private static Logger logger =  new Logger(XMLSerialisationManager.class);

	//	private UIManager uiManager = null;
	/**
	 * This class is used to validate an XML document against a given XMLSchema.<br>
	 * The validation does not stop at the first error and displays all the errors found
	 * to the user. It stops only if a fatal error is encountered.
	 * @author lchatela
	 */
	public static class XMLValidation {

		/**
		 * Set to true if any kind of error (warning, error or fatal error) occured while validating.
		 */
		public boolean error = false;

		/**
		 * Contains the error messages to display to the user.
		 */
		public StringBuffer errorMsg = new StringBuffer();

		/**
		 * Reads and validates the given xml document against the given xml schema.
		 * @param XMLDoc the XML document to read.
		 * @param XMLSchema the XML schema to validate against.
		 * @param reader the SAXReader to use.
		 * @return the document created through the parsing of XMLDoc.
		 * @throws SAXException
		 */
		public Document validate(java.io.Reader XMLStreamReader, JavancoFile XMLSchema, SAXReader reader) throws SAXException {
			Document document = null;
			error = false;
			errorMsg.append("The file opened is not valid.\n\n\n");

			try {
				// set the validation against XML schemas
				reader.setValidation(true);
				reader.setFeature("http://apache.org/xml/features/validation/schema", true);

				// define which XML schema to use
				String schemaURL = XMLSchema.toURI().toURL().toExternalForm();
				reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schemaURL);

				// defines the error handler
				reader.setErrorHandler(new XMLReaderErrorHandler(this));

				// reads and validates
				document = reader.read(XMLStreamReader);
			}
			catch (DocumentException de) {
				// a final error has occured
				if (de.getNestedException() instanceof SAXException){
					throw (SAXException) de.getNestedException();
				}
				else {
					throw new IllegalStateException(de);
				}
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}

			return document;

		}

	}

	/**
	 * This class implements {@link org.xml.sax.ErrorHandler} in order to display to the user
	 * all the warnings and errors encountered while parsing a document.<br/>
	 * In the case of a warning or a simple error, the parsing must continue so that all the problems
	 * and not only the first one are displayed to the user.<br/>
	 * In the case of a fatal error, the parsing is stopped.
	 * @author lchatela
	 *
	 */
	public static class XMLReaderErrorHandler implements ErrorHandler {

		XMLValidation src;

		public XMLReaderErrorHandler(XMLValidation src){
			this.src = src;
		}

		public void warning(SAXParseException exception){

			src.error = true;

			src.errorMsg.append("- Line "+exception.getLineNumber()+" : "+exception.getMessage());
			if (exception.getMessage().contains("keyref_link_orig")){
				src.errorMsg.append("\n  (The value of the attribute 'orig' of a link must correspond to the 'id' attribute of a node.)");
			}
			else if (exception.getMessage().contains("keyref_link_dest")){
				src.errorMsg.append("\n  (The value of the attribute 'dest' of a link must correspond to the 'id' attribute of a node.)");
			}
			src.errorMsg.append("\n\n");
		}

		public void error(SAXParseException exception){

			src.error = true;

			src.errorMsg.append("- Line "+exception.getLineNumber()+" : "+exception.getMessage());
			if (exception.getMessage().contains("keyref_link_orig")){
				src.errorMsg.append("\n  (The value of the attribute 'orig' of a link must correspond to the id of an existing node.)");
			}
			else if (exception.getMessage().contains("keyref_link_dest")){
				src.errorMsg.append("\n  (The value of the attribute 'dest' of a link must correspond to the id of an existing node.)");
			}
			src.errorMsg.append("\n\n");
		}

		public void fatalError(SAXParseException exception) throws SAXException {

			src.error = true;

			SAXException saxE = new SAXException("- Fatal error line "+exception.getLineNumber()+" : "+exception.getMessage());
			src.errorMsg.append(saxE.getMessage());

			throw saxE;
		}
	}

	public static JavancoXMLElement openNetwork(File graphFile, AbstractGraphHandler agh) throws SAXException, java.io.FileNotFoundException {
		return openNetwork(new java.io.FileReader(graphFile), agh);
	}

	/**
	 * Reads (and validates, if asked) the XML document representing the network.
	 * @param graphFile the XML document representing the network.
	 * @param frame the frame in which the popup will be displayed.
	 * @return a dom4j document corresponding to the parsed graphFile.
	 * @throws SAXException
	 */
	public static JavancoXMLElement openNetwork(java.io.Reader in, AbstractGraphHandler agh) throws SAXException {
		JavancoXMLElement element = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			element = openNetwork(in, buffer);
		} catch (SAXException e){
			agh.displayWarning(buffer.toString());
			throw e;
		}
		if (buffer.size() != 0){
			agh.displayWarning(buffer.toString());
		}
		return element;
	}

	public static JavancoXMLElement openNetwork(JavancoFile graphFile, OutputStream output) throws SAXException, java.io.FileNotFoundException {
		return openNetwork(new java.io.FileReader(graphFile), output);
	}

	/**
	 * Reads (and validates, if asked) the XML document representing the network, displays the possible
	 * errors on the output stream and returns the resulting document.
	 * @param graphFile the XML document representing the network.
	 * @param output the ouput stream on which the errors will be displayed.
	 * @return a dom4j document corresponding to the parsed graphFile.
	 * @throws SAXException
	 */
	public static JavancoXMLElement openNetwork(java.io.Reader read, OutputStream output) throws SAXException {
		JavancoXMLElement toRead = null;

		if (System.getProperty("ch.epfl.javanco.XMLvalidation")=="true"){
			// read and validate the xml document
			JavancoFile schema = loadSchemaFile(output);
			if ((schema == null) || (!schema.exists())) {
				try {
					output.write(("The given XML Schema doesn't exist ("+
							schema.getAbsolutePath()+").\n" +
							"Therefore there is no verification that the " +
					"opened network's structure is correct.").getBytes());
				} catch (IOException e) {}
				toRead = read(read);
			} else {
				logger.info("Reading and validating XML document");
				toRead = readAndValidate(read, schema, output);
			}
		} else {
			toRead = read(read);
		}
		NetworkDocumentHelper.clearDefaultTextElement(toRead.getBackedElement());
		return toRead;
	}

	/**
	 * Writes the XML <code>Document</code> into a file.
	 * @param document The XML Document to write
	 * @param graphFile The file into which to write
	 */
	public static void writeXML(JavancoXMLElement element, OutputStream out, org.dom4j.io.OutputFormat format) {
		try {
			Document document = new DefaultDocument(element.getBackedElement());
			NetworkXMLWriter wr = new NetworkXMLWriter(out, format);
			wr.write(document);
		}
		catch (java.io.IOException ex) {
			throw new IllegalStateException("Exception occured", ex);
		}
	}

	public static void writeXML(JavancoXMLElement element, OutputStream out) {
		writeXML(element, out, new OutputFormat("  ",true));
	}

	public static void dumpGraph(AbstractGraphHandler agh, OutputStream out)  {
		writeXML(agh.getXML(), out);
	}

	/**
	 * Writes the errors encountered during the validation on the given output stream.
	 * @param validator The validator containing the errors
	 * @param output The <code>OutputStream</code> on which to write the errors
	 */
	private static void writeErrorMessage(XMLValidation validator, OutputStream output) {
		// displays the errors, if they are some, on the output stream
		try {
			output.write(validator.errorMsg.toString().getBytes());
			logger.error(validator.errorMsg.toString());
		} catch (IOException e){
			throw new IllegalStateException(e);
		}
	}

	private static JavancoXMLElement readAndValidate(java.io.Reader input, JavancoFile schema, OutputStream output) {
		logger.info("Reading and validating XML document...");
		SAXReader reader = new SAXReader(NetworkDocumentFactory.getInstance());
		XMLValidation validator = new XMLValidation();
		JavancoXMLElement toRead = null;
		try {
			// "validate" throws a SAXException in case of a fatal error
			toRead = new JavancoXMLElement((validator.validate(input, schema, reader)).getRootElement());
		}
		catch (SAXException e) {}
		finally {
			if (validator.error){
				writeErrorMessage(validator, output);
			} else {
				logger.info("Read and validation completed");
			}

		}
		return toRead;
	}

	private static JavancoXMLElement read(java.io.Reader input) {
		logger.info("Reading XML document...");
		NetworkDocumentFactory factory = NetworkDocumentFactory.getInstance();
		factory.lock();
		SAXReader reader = new SAXReader(factory);
		JavancoXMLElement toRead = null;
		try {
			org.dom4j.Document doc = reader.read(input);
			toRead = new JavancoXMLElement(doc.getRootElement());
		} catch (org.dom4j.DocumentException e) {
			throw new IllegalStateException(e);
		}
		finally {
			factory.unlock();
			logger.info("Reading completed");
		}
		return toRead;
	}

	private static JavancoFile loadSchemaFile(OutputStream output) {
		try {
			String path = Javanco.getProperty("ch.epfl.javanco.schemaDir")+"/"
			+Javanco.getProperty("ch.epfl.javanco.schemaName");
			return new JavancoFile(path);
		}
		catch (java.security.AccessControlException e) {
		}
		return null;
	}

	private XMLSerialisationManager() {}

}