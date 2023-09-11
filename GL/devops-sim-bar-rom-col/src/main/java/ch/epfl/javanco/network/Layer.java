package ch.epfl.javanco.network;


public interface Layer extends Element {

	/**
	 * Implementation method should by annotated with annotation @NotSerializable
	 * is AbstractElement
	 */
	public LayerContainer getLayerContainer();

}




