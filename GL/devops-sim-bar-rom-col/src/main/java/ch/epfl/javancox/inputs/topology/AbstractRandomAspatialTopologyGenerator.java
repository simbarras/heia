package ch.epfl.javancox.inputs.topology;

import ch.epfl.general_libraries.random.PRNStream;

public abstract class AbstractRandomAspatialTopologyGenerator extends AbstractRandomTopologyGenerator {


	public AbstractRandomAspatialTopologyGenerator(int nbnodes, PRNStream stream) {
		super(nbnodes, stream);
	}

}
