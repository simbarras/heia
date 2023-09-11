package ch.epfl.javanco.network;

import java.io.Serializable;


public class DefaultLinkImpl extends AbstractElement implements Serializable, Link, Comparable<DefaultLinkImpl> {

	/*	private static Logger logger = new Logger(DefaultLinkImpl.class);

	public static final long serialVersionUID = 0;*/

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DefaultLinkImpl() {
	}

	public int compareTo(DefaultLinkImpl lnk) {
		int c = this.getStartNodeIndex() - lnk.getStartNodeIndex();
		if (c != 0) {
			return c;
		}
		c = this.getEndNodeIndex() - lnk.getEndNodeIndex();
		if (c != 0) {
			return c;
		}
		return 0;
	}

	@DoNotSerialize public LinkContainer getLinkContainer() {
		return (LinkContainer)getContainer();
	}
	@DoNotSerialize public int getStartNodeIndex() {
		return getLinkContainer().getStartNodeIndex();
	}
	@DoNotSerialize public int getEndNodeIndex() {
		return getLinkContainer().getEndNodeIndex();
	}


}