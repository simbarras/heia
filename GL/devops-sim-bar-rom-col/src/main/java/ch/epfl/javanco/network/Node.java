package ch.epfl.javanco.network;

public interface Node extends Element {

	/**
	 * @throws IllegalStateException If current abstract node has to reference to a
	 * valid <code>NodeContainer</code> object. This can happend if this method is
	 * called during node instanciation.
	 * <br><b>
	 * Implementation method should by annotated with annotation @NotSerializable
	 * is AbstractElement</b>
	 */
	public NodeContainer getNodeContainer() ;

	/**
	 * Implementation method should by annotated with annotation @NotSerializable
	 * is AbstractElement
	 */
	public int getIndex() ;

	public void setContainer(AbstractElementContainer cont);

	public void indexChanged(int newIndex);

}