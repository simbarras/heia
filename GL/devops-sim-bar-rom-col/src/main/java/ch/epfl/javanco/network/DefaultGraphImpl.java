package ch.epfl.javanco.network;

import java.io.Serializable;

import ch.epfl.javanco.xml.XMLTagKeywords;

/**
 * Default layer implementation
 */
public class DefaultGraphImpl extends AbstractElement implements Layer, Serializable {

	//private static Logger logger = new Logger(DefaultGraphImpl.class);

	public static final long serialVersionUID = 0;
	public DefaultGraphImpl() {
	}

	@DoNotSerialize public XMLTagKeywords getElementKeyword() {
		return XMLTagKeywords.LAYER;
	}

	@DoNotSerialize public LayerContainer getLayerContainer() {
		return (LayerContainer) super.getContainer();
	}



	/**
	 * An empty <code>String</code> array. Each extension of <code>AbstractLayer
	 * </code> must define such an array named <code>"NODES_TYPES"</code> which
	 * contains the list of the node types accepted in this layer. The
	 * <code>DefaultNodeImpl</code> is arbitrarly accepted
	 */
	public final static String[] NODES_TYPES = {};
	/**
	 * An empty <code>String</code> array. Each extension of <code>AbstractLayer
	 * </code> must define such an array named <code>"LINKS_TYPES"</code> which
	 * contains the list of the link types accepted in this layer. The
	 * <code>DefaultLinkImpl</code> is arbitrarly accepted
	 */
	public final static String[] LINKS_TYPES = {};



}