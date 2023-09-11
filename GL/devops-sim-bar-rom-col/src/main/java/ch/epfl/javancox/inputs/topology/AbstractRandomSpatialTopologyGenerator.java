package ch.epfl.javancox.inputs.topology;

import ch.epfl.general_libraries.random.PRNStream;

public abstract class AbstractRandomSpatialTopologyGenerator extends AbstractRandomTopologyGenerator {

	public AbstractRandomSpatialTopologyGenerator(int nbNodes, PRNStream stream) {
		super(nbNodes, stream);
	}

}
