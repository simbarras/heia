package ch.epfl.javanco.path;

import java.util.Comparator;
import java.util.TreeMap;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.PathCalculator;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;


/**
 * This class provides a common base for the evaluation
 * of path and distances in a network. Its main function
 * is the method <code>getPathValue</code> which returns
 * the cost of a path in the network. Each sub-class should
 * define the <code>getSegmentValue</code> method which returns
 * the cost of one single link in the network.
 * <br> If the cost of
 * a path is simply the sum of all its constitutive single links,
 * then the method <code>getPathValue</code> can be used directly.
 * Otherwise, it must be overriden.
 */
public abstract class JavancoPathCalculator extends PathCalculator {

	//	public final static String DEFAULT_LAYER_NAME = "physical";

	protected AbstractGraphHandler agh = null;

	private boolean cache = false;
	private TreeMap<Path, Float> cachedlengths;

	public JavancoPathCalculator(AbstractGraphHandler agh) {
		this.agh = agh;
	}

	public void setAgh(AbstractGraphHandler agh) {
		this.agh = agh;
		cachedlengths = new TreeMap<Path, Float>();
	}

	public void setPathCache(boolean b) {
		this.cache = b;
		if (cache == true) {
			cachedlengths = new TreeMap<Path, Float>();
		}
	}

	@Override
	public float getPathValue(Path myPath) {
		if (cache) {
			Float f = cachedlengths.get(myPath);
			if (f != null) {
				return f;
			}
		}
		float total = 0;
		for (int i = 0 ; i < myPath.size()-1;i++) {
			total += getSegmentValue(myPath.get(i), myPath.get(i+1));
		}
		/*	for (Iterator<Float> it = myPath.pathValuesIterator(this) ; it.hasNext() ; ) {
			total += it.next();
		}*/
		if (cache) {
			cachedlengths.put(myPath, total);
		}
		return total;
	}
	
	/**
	 * Use the currently edited layer
	 */
	JavancoShortestPathSet getShortestPathSet(boolean directed) {
		return new JavancoShortestPathSet(agh, agh.getEditedLayer(), this, directed);
	}	

	private JavancoShortestPathSet getShortestPathSet(LayerContainer lc, boolean directed) {
		return new JavancoShortestPathSet(agh, lc, this, directed);
	}

	public float[][] getRoutingCosts(LayerContainer lc, boolean directed) {
		JavancoShortestPathSet s = getShortestPathSet(lc, directed);
		s.setSymmetry();
		return getRoutingCost(s);
	}

	@Override
	public Comparator<NodePair> getNodePairComparator() {
		return new Comparator<NodePair>() {
			public int compare(NodePair np1, NodePair np2) {
				float val1 = getSegmentValue(np1);
				float val2 = getSegmentValue(np2);
				if (val1 < val2) {
					return -1;
				} else {
					return 1;
				}
			}
		};
	}

	public abstract float getSegmentValue(NodePair s);
}
