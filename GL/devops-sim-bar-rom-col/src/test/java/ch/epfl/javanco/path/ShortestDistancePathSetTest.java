package ch.epfl.javanco.path;

import javancox.topogen.AbstractTopologyGenerator;

import org.junit.jupiter.api.Test;

import ch.epfl.general_libraries.path.BFSEnumeratedPathSet;
import ch.epfl.general_libraries.path.PathRelativeFilter;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class ShortestDistancePathSetTest {

	
	@Test 
	public void testElim() throws Exception {
		for (int i = 3 ; i < 6 ; i++) {
			AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
			agh.newLayer("physical");
			agh.activateMainDataHandler();
			agh.activateGraphicalDataHandler();			
			
			AbstractTopologyGenerator.createGrid(agh, i*i, i, 20);
			agh.getEditedLayer().setLinksLengths(XMLTagKeywords.LENGTH.toString());	
				
					
			CombinationBasedShortestDistancePathSet ps = new 
				CombinationBasedShortestDistancePathSet(agh, "physical", XMLTagKeywords.LENGTH.toString(), false);
			
			JavancoShortestPathSet pset = new JavancoShortestPathSet(agh, XMLTagKeywords.LENGTH.toString(), false);
			pset.setSymmetry();
			
			PathRelativeFilter filter = new PathRelativeFilter(pset, new AttributeBasedCalculator(agh, "physical", "length"), 1);
			
			BFSEnumeratedPathSet ps2 = new BFSEnumeratedPathSet(agh.getEditedLayer().getUndirectedIncidenceMatrixDouble(), 15000, filter);
			
			ps2.enumerateAndStoreAll();			
			
			
			NodePair np = ps.getMostConnectedNodePair();
			if (ps.getPaths(np.getStartNode(), np.getEndNode()).size() !=
				ps2.getPaths(np.getStartNode(), np.getEndNode()).size()) {
				throw new IllegalStateException();
			}
		}
	}
	
	@Test
	public void testEnum() throws Exception {
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		agh.newLayer("physical");
		
		AbstractTopologyGenerator.createPolygonNodes(agh, 15, 200, 0, 0);
		
		for (int i = 0 ; i < agh.getHighestNodeIndex() ; i++) {
			agh.newLink(i, i+1);
		}
		agh.newLink(agh.getHighestNodeIndex(), 0);
		
		agh.getEditedLayer().setLinksLengths(XMLTagKeywords.LENGTH.toString());
		
		BFSEnumeratedPathSet ps = new BFSEnumeratedPathSet(agh.getEditedLayer().getUndirectedIncidenceMatrixDouble(), 10);
		
		ps.enumerateAndStoreAll();
		
		for (int i = 0 ; i < ps.getMatrixSize() ; i++) {
			for (int j = i+1 ; j < ps.getMatrixSize()  ; j++) {
				if (i == j) continue;
				if (j == i + 10 || i == j + 10) {
					if (ps.getPaths(i,j).size() != 2) {
						throw new IllegalStateException();
					}					
				}
			}
		}
	}
}
