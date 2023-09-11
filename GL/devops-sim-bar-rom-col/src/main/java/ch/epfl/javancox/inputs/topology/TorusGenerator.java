package ch.epfl.javancox.inputs.topology;

import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class TorusGenerator extends AbstractDeterministicGenerator {
	protected int torusHorizontalSize;
	protected int torusVerticalSize;
	protected int sideLength;

	public TorusGenerator(int torusHorizontalSize, int torusVerticalSize, int sideLength) {
		this.torusHorizontalSize = torusHorizontalSize;
		this.torusVerticalSize   = torusVerticalSize;
		this.sideLength = sideLength;
		if (torusHorizontalSize < 3 || torusVerticalSize < 3) 
			throw new IllegalArgumentException("Torus horizontal or vertical size must be equal or greater than 3");
	}
	
	public TorusGenerator(int size) {
		this.sideLength = 50;
		this.torusHorizontalSize = size;
		this.torusVerticalSize = size;
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("torus_width",torusHorizontalSize+"");
		map.put("torus_height", torusVerticalSize+"");
		map.put("link_width", sideLength+"");
		return map;
	}

	@Override
	public int getNumberOfNodes() {
		return torusHorizontalSize*torusVerticalSize;
	}

	/**
	 * Generate a torus
	 * @param agh The AbstractGraphHandler
	 * @param layerName The layer
	 * @param torusLimit The torus dimension
	 * @param sideLength The length of the side
	 */
	@Override
	public void generate(AbstractGraphHandler agh) {
		TessellationGenerator tessel = new TessellationGenerator(torusVerticalSize, torusHorizontalSize, sideLength, 4);
		tessel.generate(agh);
		
		int vertSize = sideLength * torusVerticalSize;
		int horSize = sideLength * torusHorizontalSize;
		
		int vC;
		int hC;
		if (vertSize > 700 || horSize > 700) {
			vC = vertSize / 65;
			hC = horSize / 65;
		} else {
			vC = 10;
			hC = 10;
		}	
		
		for (int i = 0 ; i < torusHorizontalSize*torusVerticalSize ; i=i+torusHorizontalSize) {
			/* changed by Ke 
			 * originally:
			LinkContainer lc = agh.newLink(i, i+torusHorizontalSize-1);
			*/
			LinkContainer lc = agh.newLink(i+torusHorizontalSize-1, i);
			lc.attribute(XMLTagKeywords.LINK_CURVE_START).setValue(vC);
			if (lc.getElement("main_description") != null) {
				lc.linkAttribute(XMLTagKeywords.LINK_CURVE_START,"main_description");
			}
		}
		for (int i = 0 ; i < torusHorizontalSize ; i++) {
			/* changed by Ke 
			 * originally:
			LinkContainer lc = agh.newLink(i, i+((torusVerticalSize-1)*torusHorizontalSize));
			*/
			LinkContainer lc = agh.newLink(i+((torusVerticalSize-1)*torusHorizontalSize), i);
			lc.attribute(XMLTagKeywords.LINK_CURVE_START).setValue(hC);
			if (lc.getElement("main_description") != null) {
				lc.linkAttribute(XMLTagKeywords.LINK_CURVE_START,"main_description");	
			}
		}
		
		
	/*	for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){

			int sourceY= agh.getNodeContainer(s).attribute(XMLTagKeywords.POS_Y).intValue();
			int sourceX= agh.getNodeContainer(s).attribute(XMLTagKeywords.POS_X).intValue();

			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				int destY= agh.getNodeContainer(d).attribute(XMLTagKeywords.POS_Y).intValue();
				int destX= agh.getNodeContainer(d).attribute(XMLTagKeywords.POS_X).intValue();
				if((s!=d)&&(Point2D.distance(sourceX,sourceY,destX,destY)==(torusHorizontalSize-1)*sideLength)){
					if((destY==sourceY)&&(agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
						agh.newLink(s,d).attribute(XMLTagKeywords.LINK_CURVE_START).setValue(21);
						agh.getLinkContainer(s,d).linkAttribute(XMLTagKeywords.LINK_CURVE_START,"main_description");
					}
					if((agh.getNodeContainer(d).attribute(XMLTagKeywords.POS_X).intValue()==sourceX)&&(agh.getLinkContainer(s,d)==null) ){
						agh.newLink(s,d).attribute(XMLTagKeywords.LINK_CURVE_START).setValue(21);

						agh.getLinkContainer(s,d).linkAttribute(XMLTagKeywords.LINK_CURVE_START,"main_description");
					}

				}
			}
		}*/
	}


	@Override
	public String getName() {
		return "torus_generator";
	}

	public static class Torus extends WebTopologyGeneratorStub {

		/**
		 * Generate a torus
		 * @param agh The AbstractGraphHandler
		 * @param layerName The layer
		 * @param torusLimit The torus dimension
		 * @param sideLength The length of the side
		 */
		@MethodDef
		public String generateTorusMesh(AbstractGraphHandler agh,
				@ParameterDef (name="Torus horizontal size (number of nodes)") int torusHorizontalSize,
				@ParameterDef (name="Torus vertical size (number of nodes)") int torusVerticalSize)throws InstantiationException{

			if ( torusHorizontalSize * torusVerticalSize > 1000)
				return "The product \"h size\" * \" v size\" must be smaller or equal to 1000";


			TorusGenerator torus = new TorusGenerator(torusHorizontalSize,torusVerticalSize,100);
			torus.generate(agh);
			return null;
		}


	}


}
