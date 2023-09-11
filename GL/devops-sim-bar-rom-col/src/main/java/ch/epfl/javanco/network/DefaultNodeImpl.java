package ch.epfl.javanco.network;

import java.io.Serializable;


public class DefaultNodeImpl extends AbstractElement implements Serializable, Node {

	//private static Logger logger = new Logger(DefaultNodeImpl.class);


	public static final long serialVersionUID = 0;
	public DefaultNodeImpl() {

	}

	@DoNotSerialize public NodeContainer getNodeContainer() {
		return (NodeContainer)getContainer();
	}

	@DoNotSerialize public int getIndex() {
		return getNodeContainer().getIndex();
	}

	public void indexChanged(int i) {}
}