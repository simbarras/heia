package ch.epfl.javanco.path;

import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.xml.NetworkAttribute;


public class AttributeBasedCalculator extends JavancoPathCalculator {

	private float[][] cachedLengths;
	private boolean cache = false;
	private boolean cacheSet = false;
	private String attName;
	protected String onLayer;
	private boolean directed = false;

	public AttributeBasedCalculator(String layer, String attName) {
		this(null, layer, attName);
	}

	public AttributeBasedCalculator(String layer, String attName, boolean directed, boolean attCache) {
		this(null, layer, attName, directed, attCache);
	}

	public AttributeBasedCalculator(AbstractGraphHandler agh, String onLayer, String attName){
		super(agh);
		this.attName =attName;
		this.onLayer = onLayer;
	}
	
	public AttributeBasedCalculator(AbstractGraphHandler agh, String onLayer, String attName, boolean directed){
		this(agh, onLayer, attName, directed, true);
	}	
	
	public AttributeBasedCalculator(AbstractGraphHandler agh,
			String onLayer,
			String attName,
			boolean directed,
			boolean attCache){
		super(agh);
		this.directed = directed;
		this.attName =attName;
		this.onLayer = onLayer;
		this.cache = attCache;
	}


	@Override
	public void setAgh(AbstractGraphHandler agh) {
		if (this.agh == agh) return;
		cacheSet = false;
		super.setAgh(agh);
		if (cache) {
			cachedLengths = new float[agh.getHighestNodeIndex()+1][agh.getHighestNodeIndex()+1];
			for (int i = 0 ; i < cachedLengths.length ; i++) {
				for (int j = 0 ; j < cachedLengths[i].length ; j++) {
					if (i != j) {
						NodePair np = new NodePair(i,j);
						cachedLengths[i][j] = getSegmentValue(np);
					}
				}
			}
			this.cacheSet = true;
		}
	}

	public JavancoShortestPathSet getShortestPathSet(boolean directed) {
		return new JavancoShortestPathSet(agh, onLayer, this, directed);
	}

	public String getLayerName() {
		return onLayer;
	}

	@Override
	public float getSegmentValue(NodePair s){
		return getSegmentValue(s.getStartNode(),s.getEndNode());
	}

	@Override
	public float getSegmentValue(int i, int j) {
		if (cacheSet) {
			return cachedLengths[i][j];
		} else {
			LayerContainer currentLayer = agh.getLayerContainer(onLayer);
			LinkContainer lc = null;
			if(currentLayer==null){
				throw new NullPointerException("No such layer : " + onLayer);
			}
			if(directed){
				lc = currentLayer.getLinkContainer(i,j);
			}
			else{
				lc = currentLayer.getLinkContainer(i,j);
				if(lc==null){
					lc = currentLayer.getLinkContainer(j,i);
				}
			}
			return getValue(lc);
		}
	}

	protected float getValue(LinkContainer lc) {
		if (lc != null) {
			NetworkAttribute att = lc.attribute(attName);
			if (att.getValue().equals("")) {
				return 1;
			} else {
				return att.floatValue();
			}
		} else {
			return -1;
		}
	}
}
