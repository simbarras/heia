package ch.epfl.javancox.inputs.compounds;

import java.util.Map;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;
import ch.epfl.javancox.inputs.AbstractCapacityProvider;
import ch.epfl.javancox.inputs.AbstractTopologyProvider;
import ch.epfl.javancox.inputs.AbstractTrafficProvider;

public abstract class NetworkExperimentInput extends AbstractGraphExperimentInput {
	

	public abstract double[][] getTrafficMatrix();
	public abstract double[][] getCapacityMatrix();
	public abstract String getTrafficLayerName();
	public abstract String getCapacityLayerName();
	public abstract boolean hasCapacity();
	public abstract String getTopologyName();
	
	public LayerContainer getTrafficLayer() {
		return agh.getLayerContainer(getTrafficLayerName());
	}
	public LayerContainer getCapacityLayer() {
		return agh.getLayerContainer(getCapacityLayerName());
	}
	public LayerContainer getPhysicalLayer() {
		return agh.getLayerContainer(getPhysicalLayerName());
	}
	
	public abstract String getTrafficAttributeName();
	public abstract String getCapacityAttributeName();
/*	public abstract String getCapacityLayerName();*/

	public double[][] getMatrix(String layerName,String attName) {
		LayerContainer layC = agh.getLayerContainer(layerName);
		return layC.getMatrixLinkAttributeD(attName);
	}
	
	public double[][] getMatrix(String layerName,String attName, boolean sym) {
		LayerContainer layC = agh.getLayerContainer(layerName);
		return layC.getMatrixLinkAttributeD(attName, sym);
	}	
	
	public static class FromFile extends NetworkExperimentInput {
		private String fileName;
		private String phyLayerName;
		private String lengthAttName;
		private String trafLayerName;
		private String trafAttName;
		private String capAttName;
		protected AbstractGraphHandler agh;
		public FromFile(String fileName,
				String phyLayerName,
				String lengthAttName,
				String trafLayerName,
				String trafAttName,
				String capAttName) {
			this.fileName = fileName;
			this.phyLayerName = phyLayerName;
			this.lengthAttName = lengthAttName;
			this.trafLayerName = trafLayerName;
			this.trafAttName = trafAttName;
			this.capAttName = capAttName;
		}
		
		@Override
		public String getTopologyName() {
			return fileName;
		}		

		@Override
		public void createImpl(AbstractGraphHandler agh) {
			//	agh = inst.getAgh();
			agh.openNetworkFile(fileName);
			setLinksLengths(agh);
			//	inst.addParam("input_file",fileName);
		}

		@Override
		public Map<String, String> getAllParameters() {
			//return getMap(new String[]{"input_file",fileName});
			return null;
		}

		@Override
		public double[][] getTrafficMatrix() {
			return super.getMatrix(/*agh,*/ trafLayerName, trafAttName);
		}

		@Override
		public double[][] getCapacityMatrix() {
			return super.getMatrix(/*agh,*/ phyLayerName, capAttName);
		}
		
		public boolean hasCapacity() {
			// does not check for each attribute
			return (agh.getLayerContainer(phyLayerName) != null);
		}

		@Override
		public String getPhysicalLayerName() {
			return phyLayerName;
		}
		@Override
		public String getTrafficLayerName() {
			return trafLayerName;
		}
		public String getCapacityLayerName() {
			return phyLayerName;
		}
		
		@Override
		public String getTrafficAttributeName() {
			return trafAttName;
		}
		@Override
		public String getLengthAttributeName() {
			return lengthAttName;
		}
		@Override
		public String getCapacityAttributeName() {
			return capAttName;
		}			
		
	}

	public static class FromProviders extends NetworkExperimentInput {
		private AbstractTopologyProvider topoProv;
		private AbstractTrafficProvider traffProv;
		private AbstractCapacityProvider capProv;
		
		protected FromProviders() {
		}

		public FromProviders(AbstractTopologyProvider provider) {
			this.topoProv = provider;
		}

		public FromProviders(AbstractTopologyProvider topProv,
				AbstractTrafficProvider traffProv) {
			this(topProv);
			this.traffProv = traffProv;
		}

		public FromProviders(AbstractTopologyProvider topProv,
				AbstractTrafficProvider traffProv,
				AbstractCapacityProvider capProv) {
			this(topProv, traffProv);
			this.capProv = capProv;
		}
		
		@Override
		public String getTopologyName() {
			return topoProv.getTopologyName();
		}		

		@Override
		public void createImpl(AbstractGraphHandler agh) {
			topoProv.provideTopology(agh);
			setLinksLengths(agh);
			if (traffProv != null) {
				traffProv.provideTraffic(agh);
			}
			if (capProv != null) {
				capProv.provideCapacity(this);
			}
		}

		@Override
		public Map<String, String> getAllParameters() {
			Map<String, String> map = getNewMap(3);

			map.putAll(topoProv.getAllParameters());
			if (traffProv != null) {
				map.putAll(traffProv.getAllParameters());
			}
			if (capProv != null) {
				map.putAll(capProv.getAllParameters());
			}
			return map;
		}

		@Override
		public double[][] getTrafficMatrix() {
			if (traffProv != null) {
				return traffProv.getProvidedTrafficRates();
			} else {
				throw new IllegalStateException("No traffic matrix can be extracted, since no traffic provider has been given");
			}
		}
		
		public boolean hasCapacity() {
			return (capProv != null);
		}		

		@Override
		public double[][] getCapacityMatrix() {
			if (capProv != null) {
				return capProv.getProvidedCapacities();
			} else {
				throw new IllegalStateException("No capacity matrix can be extracted, since no capacity provider has been given");
			}

		}

		@Override
		public String getPhysicalLayerName() {
			return topoProv.getTopologyLayerName();
		}
		@Override
		public String getTrafficLayerName() {
			if (traffProv != null) {
				return traffProv.getTrafficLayerName();
			} else {
				throw new IllegalStateException("No traffic layer can be extracted, since no traffic generator has been given");
			}
		}
		@Override
		public String getTrafficAttributeName() {
			return traffProv.getTrafficAttributeName();
		}
		@Override
		public String getLengthAttributeName() {
			return XMLTagKeywords.LENGTH.toString();
		}

		@Override
		public String getCapacityAttributeName() {
			return capProv.getCapacityAttributeName();
		}
		@Override
		public String getCapacityLayerName() {
			return capProv.getCapacityLayerName();
		}
	}	
	
	
}	

