package ch.epfl.javancox.inputs.topology;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class WattsStrogatzGenerator extends AbstractRandomSpatialTopologyGenerator {

	
	private PRNStream stream;
	
	private float beta;
	
	private int K;
	
	private PolygonGenerator polyGen;
	
	public WattsStrogatzGenerator(int nbNodes, int K, float beta) {
		this(nbNodes, K, beta, PRNStream.getRandomStream());
	}
	
	public WattsStrogatzGenerator(int nbNodes, int K, float beta, int seed) {	
		this(nbNodes, K, beta, PRNStream.getDefaultStream(seed));
	}
	
	public WattsStrogatzGenerator(int nbNodes, int K, float beta, PRNStream stream) {
		super(nbNodes, stream);
		this.stream = stream;
		if (K % 2 != 0) throw new IllegalArgumentException("K parameter must be even");
		this.beta = beta;
		this.K = K;
		Integer[] tab = new Integer[K/2];
		for (int i = 1 ; i <= K/2 ; i++) {
			tab[i-1] = i;
		}
		this.polyGen = new PolygonGenerator(nbNodes, 600, 300, 300, tab, false);
	}

	@Override
	public Map<String, String> getRandomGeneratorParameters() {
		Map<String, String> map = getNewMap(1);
		map.put("beta_param",beta+"");
		map.put("K",K+"");
		return map;
	}

	
	@Override
	public void generateRandomTopology(AbstractGraphHandler agh) {
		polyGen.generate(agh);
		int highest = agh.getHighestNodeIndex();
		ArrayList<LinkContainer> toRemove = new ArrayList<LinkContainer>();
		for (int i = 0 ; i < highest ; i++) {
			LinkContainer lc = null;
			for (int k = 1 ; k < K/2 ; k++) {
				double d1 = stream.nextDouble();
				if (d1 < beta) {
					if (i+k > highest) {
						lc = agh.getLinkContainer(i, (i+k) % (highest+1));
					} else {
						lc = agh.getLinkContainer(i, i+k);					
					} 				
				}
				if (lc != null) {
					toRemove.add(lc);	
				}				
			}		
		}
		for (LinkContainer lc : toRemove) {
			agh.removeElement(lc);
		}
		for (LinkContainer lc : toRemove) {
			Set<Integer> exclude = lc.getStartNodeContainer().getConnectedNodeIndexes();
			exclude.add(lc.getStartNodeIndex());
			int rand = stream.nextInt(agh.getHighestNodeIndex()-exclude.size());
			for (Integer i : exclude) {
				if (rand >= i) rand++; else break;
			}
			agh.newLink(lc.getStartNodeIndex(), rand);
		}
	}
	
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public static class WattsStrogatz extends WebTopologyGeneratorStub {
		@MethodDef
		public String generateWattsAndStrogatz(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int nbNodes,
				@ParameterDef (name="Beta parameter (probability for each edge") float beta)throws InstantiationException{

			String s = test(nbNodes, 2000);	
			if (s != null) return s;

			WattsStrogatzGenerator gen = new WattsStrogatzGenerator(nbNodes, 4, beta, PRNStream.getRandomStream());	
			gen.generate(agh);		
			return null;
		}
		
		@MethodDef
		public String generateWattsAndStrogatz(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int nbNodes,
				@ParameterDef (name="Beta parameter (probability for each edge)") float beta,
				@ParameterDef (name="Seed") int seed)throws InstantiationException{

			String s = test(nbNodes, 2000);	
			if (s != null) return s;

			WattsStrogatzGenerator gen = new WattsStrogatzGenerator(nbNodes, 4, beta, PRNStream.getDefaultStream(seed));	
			gen.generate(agh);		
			return null;
		}
		
		@MethodDef
		public String generateWattsAndStrogatz(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices") int nbNodes,
				@ParameterDef (name="Beta parameter (probability for each edge)") float beta,
				@ParameterDef (name="K parameter (start number of neighbours)") int K,
				@ParameterDef (name="Seed") int seed)throws InstantiationException{

			String s = test(nbNodes, 2000);	
			if (s != null) return s;
			if (K % 2 != 0) return "K parameter must be even";

			WattsStrogatzGenerator gen = new WattsStrogatzGenerator(nbNodes, K, beta, PRNStream.getDefaultStream(seed));	
			gen.generate(agh);		
			return null;
		}				

	}		
	
	
}
