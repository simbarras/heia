package ch.epfl.javancox.inputs.topology;

import java.awt.Point;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class WebsGenerator extends AbstractPlanarDeterministicGenerator {

	private int numberOfLevels;
	private int ray;
	private int centerX;
	private int centerY;
	private int faces;

	public WebsGenerator(int numberOfLevels,int ray, int centerX, int centerY, int faces) {
		super();

		this.numberOfLevels = numberOfLevels;
		this.ray = ray;
		this.centerX = centerX;
		this.centerY = centerY;
		this.faces = faces;
	}

	public WebsGenerator(int numberOfLevels, int faces) {
		this(numberOfLevels, 60, 0, 0, faces);
	}

	@Override
	public String getName() {
		return "web_generator";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("figure_sides",faces+"");
		map.put("number_of_levels", numberOfLevels+"");
		map.put("scale_factor", ray+"");
		return map;
	}

	@Override
	public int getNumberOfNodes() {
		return faces*numberOfLevels;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		switch(faces) {
		case 3:
			generateTriangleWeb(agh);
			break;
		case 4:
			generateSquareWeb(agh);
			break;
		case 6:
			generateHexagonWeb(agh);
			break;
		default:
			generatePolygonWeb(agh);
		}
	}
	
	private void generatePolygonWeb(AbstractGraphHandler agh) {
		ensureLayer(agh);
		for(int i=1; i<=numberOfLevels;i++){
			createPolygonNodes(agh, faces, (i*ray),centerX,centerY,0);
		}
		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if((s!=d)&&(agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
					Point source = agh.getNodeContainer(s).getCoordinate();
					Point dest = agh.getNodeContainer(d).getCoordinate();
					double distance = source.distance(dest);
					if(Math.abs(ray-((distance)))<5){
						agh.newLink(s,d);
					}
				}
			}
		}		
	}

	private void generateSquareWeb(AbstractGraphHandler agh) {
		ensureLayer(agh);
		for(int i=1; i<=numberOfLevels;i++){
			createSquare(agh,(i*ray),centerX,centerY,0);
		}
		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if((s!=d)&&(agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
					Point source = agh.getNodeContainer(s).getCoordinate();
					Point dest = agh.getNodeContainer(d).getCoordinate();
					double distance = source.distance(dest);
					if(Math.abs(ray-((distance)))<5){
						agh.newLink(s,d);
					}
				}
			}
		}

	}

	private void generateTriangleWeb(AbstractGraphHandler agh) {
		ensureLayer(agh);

		for(int i=1; i<=numberOfLevels;i++){
			createTriangle(agh,(i*ray),centerX,centerY,0);
		}

		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if((s!=d)&&(agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
					Point source = agh.getNodeContainer(s).getCoordinate();
					Point dest = agh.getNodeContainer(d).getCoordinate();
					double distance = source.distance(dest);
					if(Math.abs(ray-((distance)))<5){
						agh.newLink(s,d);
					}
				}
			}
		}

	}

	private void generateHexagonWeb(AbstractGraphHandler agh) {
		ensureLayer(agh);

		for(int i=1; i<=numberOfLevels;i++){
			createHexagon(agh,(i*ray),centerX,centerY,0);
		}

		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if((s!=d)&&(agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null) ){
					Point source = agh.getNodeContainer(s).getCoordinate();
					Point dest = agh.getNodeContainer(d).getCoordinate();
					double distance = source.distance(dest);
					if(Math.abs(ray-((distance)))<5){
						agh.newLink(s,d);
					}
				}
			}
		}
	}

	private void createHexagon(AbstractGraphHandler agh,int ray, int centerX, int centerY, double orientation) {

		double x1 = centerX + ray * Math.sin(orientation+4*Math.PI/3);
		double y1 = centerY + ray * Math.cos(orientation+4*Math.PI/3);
		NodeContainer n1=agh.newNode((int)x1,(int)y1);

		double x2 = centerX + ray * Math.sin(orientation+5*Math.PI/3);
		double y2 = centerY + ray * Math.cos(orientation+5*Math.PI/3);
		NodeContainer n2=agh.newNode((int)x2,(int)y2);

		double x3 = centerX + ray * Math.sin(orientation+0);
		double y3 = centerY + ray * Math.cos(orientation+0);
		NodeContainer n3=agh.newNode((int)x3,(int)y3);

		double x4 = centerX + ray * Math.sin(orientation+Math.PI/3);
		double y4 = centerY + ray * Math.cos(orientation+Math.PI/3);
		NodeContainer n4=agh.newNode((int)x4,(int)y4);

		double x5 = centerX + ray * Math.sin(orientation+2*Math.PI/3);
		double y5 = centerY + ray * Math.cos(orientation+2*Math.PI/3);
		NodeContainer n5=agh.newNode((int)x5,(int)y5);

		double x6 = centerX + ray * Math.sin(orientation+Math.PI);
		double y6 = centerY + ray * Math.cos(orientation+Math.PI);
		NodeContainer n6=agh.newNode((int)x6,(int)y6);

		agh.newLink(n1.getIndex(),n2.getIndex());
		agh.newLink(n2.getIndex(),n3.getIndex());
		agh.newLink(n3.getIndex(),n4.getIndex());
		agh.newLink(n4.getIndex(),n5.getIndex());
		agh.newLink(n5.getIndex(),n6.getIndex());
		agh.newLink(n6.getIndex(),n1.getIndex());
	}
	private void createTriangle(AbstractGraphHandler agh,int ray, int centerX, int centerY, double orientation) {

		double x1 = centerX + ray * Math.sin(orientation+4*Math.PI/3);
		double y1 = centerY + ray * Math.cos(orientation+4*Math.PI/3);
		NodeContainer n1=agh.newNode((int)x1,(int)y1);

		double x2 = centerX + ray * Math.sin(orientation+2*Math.PI/3);
		double y2 = centerY + ray * Math.cos(orientation+2*Math.PI/3);
		NodeContainer n2=agh.newNode((int)x2,(int)y2);

		double x3 = centerX + ray * Math.sin(orientation+Math.PI*2);
		double y3 = centerY + ray * Math.cos(orientation+Math.PI*2);
		NodeContainer n3=agh.newNode((int)x3,(int)y3);



		agh.newLink(n1.getIndex(),n2.getIndex());
		agh.newLink(n2.getIndex(),n3.getIndex());
		agh.newLink(n3.getIndex(),n1.getIndex());


	}
	private void createSquare(AbstractGraphHandler agh,int ray, int centerX, int centerY, double orientation) {

		double x1 = centerX + ray * Math.sin(orientation+5*Math.PI/4);
		double y1 = centerY + ray * Math.cos(orientation+5*Math.PI/4);
		NodeContainer n1=agh.newNode((int)x1,(int)y1);

		double x2 = centerX + ray * Math.sin(orientation+3*Math.PI/4);
		double y2 = centerY + ray * Math.cos(orientation+3*Math.PI/4);
		NodeContainer n2=agh.newNode((int)x2,(int)y2);

		double x3 = centerX + ray * Math.sin(orientation+Math.PI/4);
		double y3 = centerY + ray * Math.cos(orientation+Math.PI/4);
		NodeContainer n3=agh.newNode((int)x3,(int)y3);

		double x4 = centerX + ray * Math.sin(orientation+7*Math.PI/4);
		double y4 = centerY + ray * Math.cos(orientation+7*Math.PI/4);
		NodeContainer n4=agh.newNode((int)x4,(int)y4);

		agh.newLink(n1.getIndex(),n2.getIndex());
		agh.newLink(n2.getIndex(),n3.getIndex());
		agh.newLink(n3.getIndex(),n4.getIndex());
		agh.newLink(n4.getIndex(),n1.getIndex());

	}

	public static class Webs extends WebTopologyGeneratorStub {
		@MethodDef
		public String generateSpiderWeb(AbstractGraphHandler agh,
				@ForcedParameterDef (possibleValues={"Triangle", "Square", "Hexagon"})
				@ParameterDef (name="base figure type") String type,
				@ParameterDef (name="number of levels") int numberOfLevels,
				@ParameterDef (name="radius increment length") int radius) throws InstantiationException{

			if (numberOfLevels > 100 || numberOfLevels < 0)
				return "The number of level must be <= 100 (and positive)";

			if(type.equals("Square")){
				WebsGenerator gen = new WebsGenerator(numberOfLevels, radius, 350,350,4);
				gen.generate(agh);
			} else if (type.equals("Triangle")){
				WebsGenerator gen = new WebsGenerator(numberOfLevels, radius, 350,350,3);
				gen.generate(agh);
			}else if (type.equals("Hexagon")){
				WebsGenerator gen = new WebsGenerator(numberOfLevels, radius, 350,350,6);
				gen.generate(agh);
			}
			return null;
		}	
		
		@MethodDef
		public String generateSpiderWeb2(AbstractGraphHandler agh,
				@ParameterDef (name="base figure edges") int edges,
				@ParameterDef (name="number of levels") int numberOfLevels,
				@ParameterDef (name="radius increment length") int radius) throws InstantiationException{

			if (numberOfLevels > 100 || numberOfLevels < 0)
				return "The number of level must be <= 100 (and positive)";


			WebsGenerator gen = new WebsGenerator(numberOfLevels, radius, 350,350,edges);
			gen.generate(agh);

			return null;
		}		
		
	}

}
