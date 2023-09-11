package ch.epfl.javancox.inputs.topology;
import java.awt.Point;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class TessellationGenerator extends AbstractPlanarDeterministicGenerator {

	private int numberOfLevels;
	private int figuresByLevel;
	private int sideLength;
	private int figure;

	public TessellationGenerator(int numberOfLevels,int figuresByLevel,int sideLength,int figure) {
		super();
		this.numberOfLevels = numberOfLevels;
		this.figuresByLevel = figuresByLevel;
		this.sideLength     = sideLength;
		this.figure         = figure;
	}

	@Override
	public String getName() {
		return "tesselation_generator";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("tessellation_width",figuresByLevel+"");
		map.put("tessellation_height", numberOfLevels+"");
		map.put("tessellation_figure", figure+"");
		map.put("link_width",this.sideLength+"");
		return map;
	}

	@Override
	public int getNumberOfNodes() {
		switch (figure) {
		case 3:
		case 4:
			return figuresByLevel*numberOfLevels;

		case 6:
			return 2*(figuresByLevel + (figuresByLevel*numberOfLevels)+numberOfLevels);
		}
		throw new IllegalStateException("should not be here");
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		if (figure < 3 || figure > 6 || figure == 5) {
			throw new IllegalArgumentException("Figures of 3,4 or 6 are available");
		}
		ensureLayer(agh);
		switch (figure) {
		case 3:
			generateTriangleTessellation(agh);
			break;
		case 4:
			generateSquareTessellation(agh);
			break;
		case 6:
			generateHexagonTessellation(agh);
			break;
		}
	}


	/**
	 * Generate a triangular tessellation
	 * @param agh The AbstractGraphHandler
	 * @param layerName The layer
	 * @param numberOfLevels of the tessellation
	 * @param figuresByLevel[] The number of triangles by every level
	 * @param sideLength The length of the triangle side
	 */
	private void generateTriangleTessellation(AbstractGraphHandler agh) {
		double altitude = sideLength*Math.sin(Math.PI/3);
		for(int indexLevel = 0; indexLevel<numberOfLevels; indexLevel++){
			for(int index = 0; index < figuresByLevel;index++){
				if(indexLevel%2==0){
					agh.newNode((index*sideLength),(int)(indexLevel*altitude));
				}
				else{
					agh.newNode((sideLength/2+index*sideLength),(int)(indexLevel*altitude));
				}

			}
		}

		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if(s!=d){
					Point source = agh.getNodeContainer(s).getCoordinate();
					Point dest = agh.getNodeContainer(d).getCoordinate();
					double distance = source.distance(dest);


					if((distance - sideLength)<2){
						if((agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
							agh.newLink(s,d);

						}
					}
				}
			}
		}

	}
	/**
	 * Generate a square tessellation(grid)
	 * @param agh The AbstractGraphHandler
	 * @param layerName The layer
	 * @param numberOfLevels The number of lovels of the tessellation
	 * @param figuresByLevel[] The number of figures by every level
	 * @param sideLength The length of the side
	 */
	private void generateSquareTessellation(AbstractGraphHandler agh) {
		for(int i = 0; i<numberOfLevels; i++){
			for(int index = 0; index < figuresByLevel;index++){
				agh.newNode((index*sideLength),(i*sideLength));
			}
		}

		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if(s!=d){
					Point source = agh.getNodeContainer(s).getCoordinate();
					Point dest = agh.getNodeContainer(d).getCoordinate();
					double distance = source.distance(dest);
					if((distance - sideLength)<2){
						if((agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
							agh.newLink(s,d);

						}
					}
				}
			}
		}

	}

	/**
	 * Generate a hexagonal tessellation
	 * @param agh The AbstractGraphHandler
	 * @param layerName The layer
	 * @param numberOfLevels The number of lovels of the tessellation
	 * @param figuresByLevel[] The number of figures by every level
	 * @param sideLength The length of the side
	 */
	private void generateHexagonTessellation(AbstractGraphHandler agh) {
		double latitude = 2*sideLength*Math.sin(Math.PI/3);
		double altitude = 2*sideLength;

		for(int indexLevel=0; indexLevel <= numberOfLevels;indexLevel++){
			for(int indexFigure=0; indexFigure < figuresByLevel; indexFigure++){
				if(indexLevel%2==0){
					if(indexLevel==numberOfLevels){
						agh.newNode((int)(latitude+indexFigure*latitude),(int)(sideLength/2+indexLevel/2*(altitude+sideLength)));
					}
					else{
						agh.newNode((int)(indexFigure*latitude),(int)(sideLength/2+indexLevel/2*(altitude+sideLength)));
					}
					agh.newNode((int)(latitude/2+indexFigure*latitude),(int)(indexLevel/2*(altitude+sideLength)));
				}
				else{
					agh.newNode((int)(latitude/2+indexFigure*latitude),(int)(altitude+indexLevel/2*(altitude+sideLength)));
					agh.newNode((int)(indexFigure*latitude),(int)(altitude+indexLevel/2*(altitude+sideLength)-sideLength/2));
				}
			}
		}
		for(int indexLevel=0; indexLevel < numberOfLevels; indexLevel++){
			if(indexLevel%2==0){
				agh.newNode((int)(figuresByLevel*latitude),(int)(sideLength/2+indexLevel/2*(altitude+sideLength)));
				agh.newNode((int)(figuresByLevel*latitude),(int)(sideLength+sideLength/2+indexLevel/2*(altitude+sideLength)));
			}
			else{
				agh.newNode((int)(latitude/2+figuresByLevel*latitude),(int)(altitude+indexLevel/2*(altitude+sideLength)));
				agh.newNode((int)(latitude/2+figuresByLevel*latitude),(int)(altitude+indexLevel/2*(altitude+sideLength)+sideLength));
			}
		}
		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if(s!=d){
					Point source = agh.getNodeContainer(s).getCoordinate();
					Point dest = agh.getNodeContainer(d).getCoordinate();
					double distance = source.distance(dest);
					if((distance - sideLength)<2){
						if((agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
							agh.newLink(s,d);
						}
					}
				}
			}
		}
	}


	public static class Tesselation extends WebTopologyGeneratorStub {
		@MethodDef
		public String generateTessellation(AbstractGraphHandler agh,
				@ForcedParameterDef (possibleValues={"Triangle", "Square", "Hexagon"})
				@ParameterDef (name="base figure type") String type,
				@ParameterDef (name="number of levels") int numberOfLevels,
				@ParameterDef (name="figures by level") int figuresByLevel,
				@ParameterDef (name="links lengths") int sideLength) {

			if (numberOfLevels * figuresByLevel > 1000)
				return "The product \"number of levels\" * \" figures by level\" must be smaller or equal to 1000";

			if(type.equals("Triangle")){
				TessellationGenerator tessGen = new TessellationGenerator(numberOfLevels,figuresByLevel,sideLength,3);
				tessGen.generate(agh);
			} else if (type.equals("Square")){
				TessellationGenerator tessGen = new TessellationGenerator(numberOfLevels,figuresByLevel,sideLength,4);
				tessGen.generate(agh);
			}else if (type.equals("Hexagon")){
				TessellationGenerator tessGen = new TessellationGenerator(numberOfLevels,figuresByLevel,sideLength,6);
				tessGen.generate(agh);
			}
			return null;
		}
	}


}
