package ch.epfl.general_libraries.path;

import java.util.ArrayList;

import ch.epfl.general_libraries.utils.Matrix;
import ch.epfl.general_libraries.utils.NodePair;

public class WorseCaseTrafficCalculator {
	
	private Matrix<ArrayList<NodePair>> mat;
	
	public WorseCaseTrafficCalculator(int size) {
		mat = new Matrix<ArrayList<NodePair>>(size);
	}
	
	public void addPaths(Path[] parray) {
		for (int i = 0 ; i < parray.length ; i++) {
			if (parray[i] == null) continue;
			NodePair ext = parray[i].getExtremities();
			for(NodePair np : parray[i].getPathSegments()) {
				ArrayList<NodePair> l = mat.getMatrixElement(np);
				if (l == null) {
					l = new ArrayList<NodePair>();
					mat.setMatrixElement(np, l);
				}
				l.add(ext);
			}
		}
	}

	public void getWorseTraffic() {
		
	}
}
