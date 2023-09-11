package ch.epfl.javanco.algorithms;

import ch.epfl.general_libraries.graphics.ColorMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

/**
 *  This class provides graph coloring. The links of the edited layer are taken dependencies,
 *  and no interdependent pairs of node can be colored with the same color. The number of color
 *  is minimized.
 *  The results of the algorithm is retrievable by two means. First, the method solve returns
 *  the number of color used. Second, each node of the graph is associated with a "color"
 *  attribute containing an integer.
 *  This algorithm uses JaCoP as optimizer
 * @author rumley
 *
 */
public class GraphColouring {

	public GraphColouring() {
		// TODO Auto-generated constructor stub
	}

	public int solve(AbstractGraphHandler agh) {
	    return -1;
    }

    // TODO : #2 perform a better cleanup

//	public int solve(AbstractGraphHandler agh) {
//		Store store = new Store();  // define FD store
//        int size = agh.getNodeContainers().size();
//        // define finite domain variables
//        IntVar[] v = new IntVar[size];
//        for (int i=0; i<size; i++)
//            v[i] = new IntVar(store, "v"+i, 1, size);
//        // define constraints
//        for (LinkContainer lc : agh.getEditedLayer().getLinkContainers()) {
//        	store.impose(new XneqY(v[lc.getStartNodeIndex()], v[lc.getEndNodeIndex()]));
//        }
//
//        // search for a solution and print results
//        Search<IntVar> search = new DepthFirstSearch<IntVar>();
//        SelectChoicePoint<IntVar> select =
//            new InputOrderSelect<IntVar>(store, v,
//                                         new IndomainMin<IntVar>());
//        boolean result = search.labeling(store, select);
//
//        ColorMap m = ColorMap.getAnotherMap();
//
//        if ( result ) {
//        	int max = -1;
//        	for (int i = 0 ; i < agh.getNodeContainers().size() ; i++) {
//        		agh.getNodeContainer(i).attribute("color").setValue(v[i].value());
//        		agh.getNodeContainer(i).attribute("node_color").setValue(m.getColorAsString(10*v[i].value()));
//        		if (v[i].value() > max)
//        			max = v[i].value();
//        	}
//        	return max;
//        }
//        else {
//            System.out.println("*** No");
//            return -1;
//        }
//
//	}

}
