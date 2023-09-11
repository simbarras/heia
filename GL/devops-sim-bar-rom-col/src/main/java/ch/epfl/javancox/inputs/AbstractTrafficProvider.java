package ch.epfl.javancox.inputs;

import java.util.Map;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;

public abstract class AbstractTrafficProvider extends AbstractProvider {
	
	private AbstractGraphHandler agh;

	/**
	 * The default traffic layer name is "demands"
	 */
	public static final String DEFAULT_TRAFFIC_LAYER_NAME = "demands";

	/**
	 * The default traffic layer name is "demands"
	 */
	public static final String DEFAULT_TRAFFIC_ATTRIBUTE_NAME = "demand";

	/**
	 * Returns the DEFAULT_TRAFFIC_LAYER_NAME = demands if not overriden
	 */
	public String getTrafficLayerName() {
		return DEFAULT_TRAFFIC_LAYER_NAME;
	}

	/**
	 * Returns the DEFAULT_TRAFFIC_ATTRIBUTE_NAME = demand if not overriden
	 */
	public String getTrafficAttributeName() {
		return DEFAULT_TRAFFIC_ATTRIBUTE_NAME;
	}


	public void provideTraffic(AbstractGraphHandler agh) {
		this.agh = agh;
		generateTraffic(agh);
	}
	public abstract Double[][] generateTrafficImpl(AbstractGraphHandler agh);
	public abstract String getTrafficGeneratorName();
	
	public void generateTraffic(AbstractGraphHandler agh) {
		Double[][] rates = generateTrafficImpl(agh);		
		ensureLayer(agh, getTrafficLayerName());
		LayerContainer layC = agh.getLayerContainer(getTrafficLayerName());
		for (int i = agh.getSmallestNodeIndex() ; i <= agh.getHighestNodeIndex() ; i++) {
			for (int j = agh.getSmallestNodeIndex(); j <= agh.getHighestNodeIndex() ; j++) {
				if (i == j) {
					continue;
				}
				if (agh.getNodeContainer(i) != null) {
					if (agh.getNodeContainer(j) != null) {
						LinkContainer lc = layC.getLinkContainer(i,j);
						if (lc == null) {
							lc = layC.newLink(i,j);
						}
						lc.attribute(getTrafficAttributeName()).setValue(rates[i][j]);
						lc.linkAttribute(getTrafficAttributeName());
					}
				}
			}
		}	
	}

	public Map<String, String> getAllParameters() {
		Map<String, String> m = getProviderParameters();
		m.put("traffic_generator", getTrafficGeneratorName());
		return m;
	}
	
	public Map<String, String> getProviderParameters() {
		return getNewMap();
	}

	public double[][] getProvidedTrafficRates() {
		return getMatrix(agh, getTrafficLayerName(), getTrafficAttributeName());
	}

	public static class EmptyTrafficProvider extends AbstractTrafficProvider {
		@Override
		public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
			Double[][] d = new Double[agh.getHighestNodeIndex()+1][agh.getHighestNodeIndex()+1];
			for (int i = 0; i < d.length ; i++) {
				for (int j = 0 ; j < d.length ; j++) {
					d[i][j] = 0.0;
				}
			}
			return d;
		}
		
		public String getTrafficGeneratorName() {
			return "emptytraffic";
		}
		
		@Override
		public String getTrafficLayerName() { return ""; }
		@Override
		public String getTrafficAttributeName() { return ""; }
		@Override
		public double[][] getProvidedTrafficRates() { return new double[0][0]; }
		@Override
		public Map<String, String> getAllParameters() { return getEmptyMap(); }
	}
	
	public static class IntegralTrafficProvider extends AbstractTrafficProvider {
		AbstractTrafficProvider prov;

		public IntegralTrafficProvider(AbstractTrafficProvider prov) {
			this.prov = prov;
		}

		public String getTrafficGeneratorName() {
			return "integral" + prov.getTrafficGeneratorName();
		}
		
		public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
			Double[][] d = prov.generateTrafficImpl(agh);
			for (int i = 0 ; i < d.length ; i++) {
				for (int j = 0 ; j < d.length ; j++) {
					if (d[i][j] != null) {
						d[i][j] = (double)Math.round(d[i][j]);
					}
				}
			}
			return d;			
		}								
	}
	
	public static class NormalisedTrafficProvider extends AbstractTrafficProvider {
		AbstractTrafficProvider prov;
		double load;
		
		public NormalisedTrafficProvider(AbstractTrafficProvider prov, float load) {
			this.prov = prov;
			this.load = load;
		}
		
		public NormalisedTrafficProvider(AbstractTrafficProvider prov, double load) {
			this.prov = prov;
			this.load = load;
		}		
		
		public Map<String, String> getAllParameters() {
			Map<String, String> m = prov.getProviderParameters();
			m.put("traffic_generator", prov.getTrafficGeneratorName());
			m.put("normalised_load", load+"");
			return m;
		}
		
		public String getTrafficGeneratorName() {
			return "normalised" + prov.getTrafficGeneratorName();
		}
		
		public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
			Double[][] d = prov.generateTrafficImpl(agh);
			int nb = 0;
			double sum = 0;
			for (int i = 0 ; i < d.length ; i++) {
				for (int j = 0 ; j < d.length ; j++) {
					if (d[i][j] != null) {
						sum += d[i][j];
						nb++;
					}
				}
			}
			double desired = (double)nb * load;
			double ratio = desired/sum;
			for (int i = 0 ; i < d.length ; i++) {
				for (int j = 0 ; j < d.length ; j++) {
					if (d[i][j] != null) {
						d[i][j] *= ratio;
					}
				}
			}
			return d;			
		}	
	}
	
	public static class AdaptedTrafficProvider extends AbstractTrafficProvider {
		AbstractTrafficProvider prov;
		float load;
		
		public AdaptedTrafficProvider(AbstractTrafficProvider prov, float load) {
			this.prov = prov;
			this.load = load;
		}
		
		public Map<String, String> getAllParameters() {
			Map<String, String> m = prov.getProviderParameters();
			m.put("traffic_generator", prov.getTrafficGeneratorName());
			m.put("load_coefficient", load+"");
			return m;
		}
		
		public String getTrafficGeneratorName() {
			return "adapted" + prov.getTrafficGeneratorName();
		}
		
		public Double[][] generateTrafficImpl(AbstractGraphHandler agh) {
			Double[][] d = prov.generateTrafficImpl(agh);
			for (int i = 0 ; i < d.length ; i++) {
				for (int j = 0 ; j < d.length ; j++) {
					if (d[i][j] != null) {
						d[i][j] *= load;
					}
				}
			}
			return d;			
		}	
	}	
		
}
