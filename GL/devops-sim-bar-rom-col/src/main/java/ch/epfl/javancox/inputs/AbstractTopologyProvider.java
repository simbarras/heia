package ch.epfl.javancox.inputs;

import ch.epfl.javanco.base.AbstractGraphHandler;

public abstract class AbstractTopologyProvider extends AbstractProvider {

	public abstract void provideTopology(AbstractGraphHandler agh);

	public abstract String getTopologyLayerName();
	
	public abstract String getTopologyName();

}
