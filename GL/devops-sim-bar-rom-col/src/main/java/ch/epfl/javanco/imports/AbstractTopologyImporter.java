package ch.epfl.javanco.imports;

import java.io.File;

import ch.epfl.javanco.base.AbstractGraphHandler;

public abstract class AbstractTopologyImporter {

	public abstract void importTopology(AbstractGraphHandler agh, File f) throws Exception;


}
