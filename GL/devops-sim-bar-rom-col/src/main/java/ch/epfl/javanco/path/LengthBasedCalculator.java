package ch.epfl.javanco.path;

import java.awt.Point;

import ch.epfl.general_libraries.clazzes.ConstructorDef;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;

public class LengthBasedCalculator extends AttributeBasedCalculator {

	@ConstructorDef(def="Default, with undirected physical links")
	public LengthBasedCalculator() {
		super("physical", "length", false, false);
	}
	
	@ConstructorDef(ignore=true)
	public LengthBasedCalculator(AbstractGraphHandler agh) {
		this(agh, false);
	}	

	public LengthBasedCalculator(@ParamName(name="Consider directed physical links") boolean directed) {
		super("physical", "length", directed, false);
	}

	@ConstructorDef(ignore=true)
	public LengthBasedCalculator(AbstractGraphHandler agh, boolean directed) {
		super(agh, "physical", "length", directed, true);
	}
	@ConstructorDef(ignore=true)	
	public LengthBasedCalculator(AbstractGraphHandler agh, String onLayer, boolean directed) {
		super(agh, onLayer, "length", directed, true);
	}
	@ConstructorDef(ignore=true)	
	public LengthBasedCalculator(AbstractGraphHandler agh, boolean directed, boolean attCache) {
		super(agh, "physical", "length", directed, attCache);
	}
	@ConstructorDef(ignore=true)	
	public LengthBasedCalculator(AbstractGraphHandler agh, String onLayer, boolean directed, boolean attCache) {
		super(agh, onLayer, "length", directed, attCache);
	}

	@Override
	protected float getValue(LinkContainer lc) {
		float toReturn = 0;
		LayerContainer currentLayer = agh.getLayerContainer(super.onLayer);
		if (lc != null) {
			if(lc.attribute("length", false)== null){
				Point startPoint = currentLayer.getNode(lc.getStartNodeIndex()).getCoordinate();
				Point endPoint = currentLayer.getNode(lc.getEndNodeIndex()).getCoordinate();
				toReturn = (float)startPoint.distance(endPoint);
			}
			else{
				toReturn = lc.attribute("length").floatValue();
			}
		} else {
			return -1;
		}
		return toReturn;
	}
}
