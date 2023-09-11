package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class FractalMoore extends AbstractPlanarDeterministicGenerator implements TreeGenerator {
	
	private int d;
	private int k;
	
	private double distDiv = 2.5;
	private int size = 200;
	

	public FractalMoore(int d, int k) {
		this.d = d;
		this.k = k;
	}
	
	public FractalMoore(int d, int k, double distDiv, int size) {
		this.d = d;
		this.k = k;
		this.distDiv = distDiv;
		this.size = size;
	}	
	
	@Override
	public void generateTree(AbstractGraphHandler agh) {
		generate(agh);
	}
	
	@Override
	public void generateTree(AbstractGraphHandler agh, PRNStream stream) {
		generate(agh);
	}	
	

	@Override
	public void generate(AbstractGraphHandler agh) {
		//double portionAngle = 180d/(d-2);

		NodeContainer node = agh.newNode(0,0);
		for (int i = 0 ; i < d ; i++) {
			iterate(agh, node,90+ i*360d/d, size, 0);
		}
	}
	

	private void iterate(AbstractGraphHandler agh, NodeContainer node, double angle, double dist, int level) {
		if (level == k) return;
		double portionAngle = 180d/(d-2);		
	    NodeContainer n = paint(agh, node, angle, dist, level);
	    for (int i = 0 ; i < d-1 ; i++) {
	    	double pangle;
	        if (d != 3) {
	            pangle = angle+90-(i*portionAngle);
	        } else {
	            pangle = angle+60-(i*120);
	        }
	        iterate(agh, n, pangle, dist/distDiv, level+1);
	    }
}

	private NodeContainer paint(AbstractGraphHandler agh, NodeContainer node, double angle, double dist, int level) {
	    double anglerad = 2*Math.PI*angle/360d;
	    double x = Math.cos(anglerad)*dist;
	    double y = Math.sin(anglerad)*dist;
	    double xac = node.attribute(XMLTagKeywords.POS_X).intValue();
	    double yac = node.attribute(XMLTagKeywords.POS_Y).intValue();
	    NodeContainer newNode = agh.newNode((int)(xac+x), (int)(yac+y));
	    newNode.attribute(XMLTagKeywords.NODE_COLOR).setValue(ColorMap.getDefaultMap().getColorAsString(level));
	    agh.newLink(node.getIndex(), newNode.getIndex());
	    return newNode;
	}	
	
	@Override
	public String getName() {
		return "Fractal Moore";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("d", d+"", "k", k+"", "distDiv", distDiv+"", "size", size+"");
	}

	@Override
	public int getNumberOfNodes() {
		int accum = 1;
		for (int i= 0 ; i < k ; i++) {
			accum += d*Math.pow((d-1),i);
		}
		return accum;
	}
	
	@Override
	public double getAverageNumberOfLeaves() {
		return d*Math.pow((d-1),k-1);
	}

	@Override
	public double getAverageHubNumber() {
		if (d == 2) return 1;
		int accum = 1;
		for (int i= 0 ; i < k-1 ; i++) {
			accum += d*Math.pow((d-1),i);
		}
		return accum;		
	}	
	
	
	public static class FractalMoore_ extends WebTopologyGeneratorStub {
		@MethodDef
		public String generateMoore(AbstractGraphHandler agh,
				@ParameterDef (name="d") int d,
				@ParameterDef (name="k") int k) throws InstantiationException{
			
			FractalMoore gen = new FractalMoore(d, k);
			gen.generate(agh);
			return null;
		}	
		
		@MethodDef
		public String generateMooreWithDetails(AbstractGraphHandler agh,
				@ParameterDef (name="d") int d,
				@ParameterDef (name="k") int k,
				@ParameterDef (name="Dist div") double distD,
				@ParameterDef (name="size") int size) throws InstantiationException{
			
			FractalMoore gen = new FractalMoore(d, k,distD, size);
			gen.generate(agh);
			return null;
		}		
	}
}
