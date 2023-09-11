package ch.epfl.javancox.inputs.topology;

import java.util.List;
import java.util.Map;

import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class PetersenGenerator extends AbstractDeterministicGenerator {
	
	private boolean withLabelsAndDir;
	private int dimensions;
	
	public PetersenGenerator() {
		
	}
	
	public PetersenGenerator(boolean withLabelsAndDir) {
		this.withLabelsAndDir = withLabelsAndDir;
	}
	
	public PetersenGenerator(int dimensions) {
		this.dimensions = dimensions;
	}	
	
	

	@Override
	public void generate(AbstractGraphHandler agh) {
		super.createPolygonNodes(agh, 5, 200, 100, 100);
		super.createPolygonNodes(agh, 5, 100, 100, 100);
		int l =0;
		for (int i = 0 ; i < 5 ; i++) {
			LinkContainer lc = agh.newLink(i, (i+1) % 5);
			if (withLabelsAndDir) {
				lc.attribute("label").setValue(l++);
			}
		}
		for (int i = 0 ; i < 5 ; i++) {		
			LinkContainer lc =  agh.newLink(i, i+5);
			if (withLabelsAndDir) {
				lc.attribute("label").setValue(l++);
			}
		}
		for (int i = 0 ; i < 5 ; i++) {		
			LinkContainer lc = agh.newLink(i+5, ((i+2) % 5) + 5);
			if (withLabelsAndDir) {
				lc.attribute("label").setValue(l++);
			}			
		}
		if (withLabelsAndDir) {
			for (LinkContainer lc : agh.getLinkContainers()) {
				lc.attribute("directed").setValue("true");
			}
		}
		if (dimensions > 1) {
			for (int i = 0 ; i < dimensions - 1 ; i++) {
				addDimension(agh, i);
			}
		}
	}
	
	private void addDimension(AbstractGraphHandler agh, int dim) {
		int currentNodes = agh.getNumberOfNodes();
		List<LinkContainer> existingLinks = agh.getLinkContainers();
		ColorMap cmap = ColorMap.getDefaultMap();
		for (int i = 0 ; i < 9 ; i++) {
			for (int j = 0 ; j < currentNodes ; j++) {
				agh.newNode();
			}
			for (int j = 0 ; j < existingLinks.size() ; j++) {
				LinkContainer existing = existingLinks.get(j);
				LinkContainer newL = agh.newLink(existing.getStartNodeIndex() + ((i+1)*currentNodes), existing.getEndNodeIndex() + ((i+1)*currentNodes));
				newL.attribute("link_color").setValue(existing.attribute("link_color").getValue());
			}
		}
		for (int k = 0 ; k < currentNodes ; k++) {
			for (int j = 0 ; j < existingLinks.size() ; j++) {
				LinkContainer existing = existingLinks.get(j);
				System.out.println((existing.getStartNodeIndex()*10 + k) + " - " + (existing.getEndNodeIndex()*10 + k));
				LinkContainer newL = agh.newLink(existing.getStartNodeIndex()*10 + k, existing.getEndNodeIndex()*10 + k);				
				newL.attribute("link_color").setValue(cmap.getColorAsInt(dim));
			}		
		}
	}	
	
	

	@Override
	public String getName() {
		return "petersen";
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap();
	}

	@Override
	public int getNumberOfNodes() {
		return 10;
	}
	
	public static class Petersen_ extends WebTopologyGeneratorStub {

		@MethodDef
		public String petersen(AbstractGraphHandler agh) {

			PetersenGenerator pg = new PetersenGenerator();
			
			pg.generate(agh);
			return "";
		}	
		
		@MethodDef
		public String petersen(AbstractGraphHandler agh, boolean f) {

			PetersenGenerator pg = new PetersenGenerator(f);
			
			pg.generate(agh);
			return "";
		}
		
		@MethodDef
		public String petersen(AbstractGraphHandler agh, int dim) {

			PetersenGenerator pg = new PetersenGenerator(dim);
			
			pg.generate(agh);
			return "";
		}		
	}

}
