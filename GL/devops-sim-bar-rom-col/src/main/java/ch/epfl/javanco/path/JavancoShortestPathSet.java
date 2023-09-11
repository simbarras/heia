package ch.epfl.javanco.path;

import java.util.Collection;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.path.PathCalculator;
import ch.epfl.general_libraries.path.PathFilter;
import ch.epfl.general_libraries.path.PathSet;
import ch.epfl.general_libraries.path.ShortestPathAlgorithm;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.utils.JavancoCollections;


public class JavancoShortestPathSet extends PathSet {
	
	double[][] costs;

	protected JavancoShortestPathSet(int nodes) {
		super(nodes, null);
	}
	
	public JavancoShortestPathSet(AbstractGraphHandler agh, String attriName, boolean directed) {
		this(agh, agh.getEditedLayer(), attriName, directed, null);
	}	

	public JavancoShortestPathSet(AbstractGraphHandler agh, String layerName, String attriName) {
		this(agh, layerName, attriName, false, null);
	}

	public JavancoShortestPathSet(AbstractGraphHandler agh, LayerContainer layer, String attriName, boolean directed) {
		this(agh.getNodeContainers(), layer.getLinkContainers(), attriName, directed, null);
	}	

	public JavancoShortestPathSet(AbstractGraphHandler agh,
			String layerName,
			String attriName,
			boolean directed,
			PathFilter filter) {
		this(agh.getNodeContainers(),
				agh.getLayerContainer(layerName).getLinkContainers(),
				attriName,
				directed,
				filter);
	}
	
	public JavancoShortestPathSet(AbstractGraphHandler agh,
			LayerContainer layer,
			String attriName,
			boolean directed,
			PathFilter filter) {
		this(agh.getNodeContainers(),
				layer.getLinkContainers(),
				attriName,
				directed,
				filter);
	}	

	public JavancoShortestPathSet(Collection<NodeContainer> nodes, Collection<LinkContainer> links, String attriName){
		this(nodes, links, attriName, false, null);
	}

	public JavancoShortestPathSet(Collection<NodeContainer> nodes,
			Collection<LinkContainer> links,
			String attriName,
			boolean directed,
			PathFilter filter) {
		super(JavancoCollections.maxIndex(nodes)+1, filter);
		compute(nodes,links,attriName,directed);
	}

	public JavancoShortestPathSet(AbstractGraphHandler agh,
			String onLayer,
			PathCalculator calc,
			boolean directed) {
		this(agh, agh.getLayerContainer(onLayer), calc, directed);
	}


	public JavancoShortestPathSet(AbstractGraphHandler agh,
			LayerContainer layC,
			PathCalculator calc,
			boolean directed) {

		super(agh.getHighestNodeIndex()+1);

		if (layC == null) {
			throw new NullPointerException();
		}

		int maxIndex = agh.getHighestNodeIndex();
		double[][] lengths = new double[maxIndex+1][maxIndex+1];

		for (int i = 0 ; i < maxIndex+1 ; i++) {
			for (int j = 0 ; j < maxIndex+1 ; j++) {
				LinkContainer lc = layC.getLinkContainer(i,j,directed);
				if (lc != null) {
					lengths[i][j] = calc.getSegmentValue(i,j);
				} else {
					lengths[i][j] = -1;
				}
			}
		}

		ShortestPathAlgorithm alg = new ShortestPathAlgorithm(lengths);

		alg.computeAll();
		costs = alg.getDistances();

		for (Path p : alg) {
		/*	if (p == null) {
				int debug = 0;			
			}
			else*/ if (directed || (p.getFirst() < p.getLast())) {
				addPath(p);
			}
		}
	}

	public double[][] getCosts() {
		return costs;
	}

	protected void compute(Collection<NodeContainer> nodes,
			Collection<LinkContainer> links,
			String attriName,
			boolean directed){
		JavancoShortestPath shortestPath = new JavancoShortestPath(nodes,links,attriName,directed);
		shortestPath.computeAll();
		costs = shortestPath.getDistances();
		for (Path p : shortestPath) {
			if (p != null && (directed || (p.getFirst() < p.getLast()))) {
				addPath(p);
			}
		}
	}






}
