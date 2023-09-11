package ch.epfl.javanco.network;


public interface Element extends java.io.Serializable {

	/**
	 * Implementation method should by annotated with annotation @NotSerializable
	 * is AbstractElement
	 */
	public AbstractElementContainer getContainer();

	/**
	 * Implemention of this method should check if <code>cont</code>
	 * is a LinkContainer
	 */
	public void setContainer(AbstractElementContainer cont);

}
