package ch.epfl.javancox.inputs.topology.linker;

import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;

public abstract class AbstractNodeLinker extends AbstractExperimentBlock {


	public abstract void linkNodes(AbstractGraphHandler agh, PRNStream stream);

	public abstract Map<String, String> getLinkerParameters();

	@Override
	public Map<String, String> getAllParameters() {
		Map<String, String> map = getLinkerParameters();
		map.put("node_linker", this.getClass().getSimpleName());
		return map;
	}
}
