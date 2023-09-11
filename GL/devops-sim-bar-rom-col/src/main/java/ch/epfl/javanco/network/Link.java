package ch.epfl.javanco.network;

public interface Link  extends Element {

	/**
	 * Implementation method should by annotated with annotation @NotSerializable
	 * is AbstractElement
	 */
	public LinkContainer getLinkContainer();

	/**
	 * Implementation method should by annotated with annotation @NotSerializable
	 * is AbstractElement
	 */
	public int getStartNodeIndex();
	/**
	 * Implementation method should by annotated with annotation @NotSerializable
	 * is AbstractElement
	 */
	public int getEndNodeIndex();

}