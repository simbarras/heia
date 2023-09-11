package ch.epfl.javanco.exports;

import java.io.IOException;
import java.io.OutputStream;

import ch.epfl.javanco.base.AbstractGraphHandler;

public abstract class AbstractTopologyExporter {

	public abstract void exportTopology(AbstractGraphHandler agh, OutputStream s) throws IOException;


}
