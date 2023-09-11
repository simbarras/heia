package ch.epfl.javanco.base;


public class DefaultGraphHandler extends AbstractGraphHandler {

	public DefaultGraphHandler(GraphHandlerFactory factory) {
		super(factory, true);
	}

	@Override
	public String toString() {
		return "DefaultGraphHandler with " + getNodeContainers().size() + " nodes and " +
		getLinkContainers().size() + " links";
	}
}
