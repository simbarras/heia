package javancox.layout;

import java.util.Map;

import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.javanco.base.AbstractGraphHandler;

public abstract class AbstractTopologyLayout extends AbstractExperimentBlock {

	@Override
	public Map<String, String > getAllParameters() {
		Map<String, String> map = getNewMap();
		map.put("layout_type", this.getClass().getSimpleName());
		map.putAll(getLayoutParameters());
		return map;
	}

	public abstract Map<String, String> getLayoutParameters();

	public abstract void assignNodesPosition(int maxScreenx, int maxScreeny, AbstractGraphHandler agh);

	public void assignNodesPosition(AbstractGraphHandler agh) {
		assignNodesPosition(500, 500, agh);
	}
}
