package ch.epfl.javancox.inputs.topology;

import java.util.Map;
import java.util.TreeMap;

import umontreal.ssj.probdist.Distribution;

import javancox.layout.AbstractTopologyLayout;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javancox.inputs.topology.layout_reorganiser.UnequalTreeLayout;

public class ScaleFreeNetworkGenerator extends AbstractRandomAspatialTopologyGenerator implements TreeGenerator {

	private float param;
	private AbstractTopologyLayout layout;
	
	private boolean withSeed = false;
	private int seed = 0;
	
	private Distribution distForNodeNumber;

	public ScaleFreeNetworkGenerator(int nbNodes, float param, PRNStream stream) {
		super(nbNodes,stream);
		this.param = param;
	}
	
	public ScaleFreeNetworkGenerator(int nbNodes, float param, int seed) {
		super(nbNodes,PRNStream.getDefaultStream(seed));
		this.withSeed = true;
		this.seed = seed;
		this.param = param;
	}	

	public ScaleFreeNetworkGenerator(int nbNdes, float param) {
		this(nbNdes, param, PRNStream.getRandomStream());
	}
	
	public ScaleFreeNetworkGenerator(float param, Distribution dist) {
		super((int)dist.getMean(), PRNStream.getRandomStream());
		this.distForNodeNumber = dist;
		this.param = param;
	}
	
	public ScaleFreeNetworkGenerator(int nbNodes, float param, AbstractTopologyLayout layout) {
		this(nbNodes, param);
		this.layout = layout;
	}
	
	public ScaleFreeNetworkGenerator(int nbNodes, float param, AbstractTopologyLayout layout, PRNStream stream) {
		this(nbNodes, param, stream);
		this.layout = layout;
	}	

	@Override
	public void generateTree(AbstractGraphHandler agh) {
		generate(agh);
	}
	
	@Override
	public void generateTree(AbstractGraphHandler agh, PRNStream stream) {
		if (!withSeed)
			super.setStream(stream);
		generate(agh);
	}	

	@Override
	public Map<String, String> getRandomGeneratorParameters() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		if (distForNodeNumber != null) {
			map.put("Average number of nodes",nbNodes+"");
		} else {
			map.put("number_of_nodes",nbNodes+"");
		}
		if (withSeed) {
			map.put("Scalefree gen seed", seed+"");
		}
		map.put("scalefree_param", param+"");
		if (layout != null) {
			map.putAll(layout.getAllParameters());
		}
		return map;
	}
	
	@Override
	public double getAverageNumberOfLeaves() {
//	These numbers were derived for 5,8,15,25,50,100,500, and 1000 nodes using best fit 
		
		double averageNumLeaves=0, p0;
		if(param<=0){
			if(nbNodes<6.5)
				p0=Math.pow(param,3)*0.0003+Math.pow(param,2)*0.0062+0.0479*param+0.5097;
			else if(nbNodes<11.5)
				p0=Math.pow(param,3)*0.0006+Math.pow(param,2)*0.0113+0.0776*param+0.4627;
			else if(nbNodes<20)
				p0=Math.pow(param,3)*0.0005+Math.pow(param,2)*0.0122+0.1071*param+0.4911;
			else if(nbNodes<37)
				p0=Math.pow(param,3)*0.0004+Math.pow(param,2)*0.0108+0.1065*param+0.4775;
			else if(nbNodes<75)
				p0=Math.pow(param,3)*0.0002+Math.pow(param,2)*0.0066+0.0934*param+0.4786;
			else if(nbNodes <300)
				p0=Math.pow(param,3)*0.0002+Math.pow(param,2)*0.0084+0.1034*param+0.4842;
			else if(nbNodes < 750)
				p0=Math.pow(param,3)*0.0004+Math.pow(param,2)*0.0109+0.1149*param+0.4989;
			else
				p0=Math.pow(param,3)*0.0004+Math.pow(param,2)*0.0111+0.1156*param+0.4951;
		}else{
			if(nbNodes<6.5)
				p0=-Math.pow(param,3)*0.0006+Math.pow(param,2)*0.0038+0.0290*param+0.5205;
			else if(nbNodes<11.5)
				p0=Math.pow(param,3)*0.001-Math.pow(param,2)*0.0215+0.1550*param+0.4455;
			else if(nbNodes<20)
				p0=Math.pow(param,3)*0.0021-Math.pow(param,2)*0.0387+0.2363*param+0.4263;
			else if(nbNodes<37)
				p0=Math.pow(param,3)*0.0032-Math.pow(param,2)*0.0539+0.2955*param+0.4132;
			else if(nbNodes<75)
				p0=Math.pow(param,3)*0.0044-Math.pow(param,2)*0.0689+0.3493*param+0.4063;
			else if(nbNodes <300)
				p0=Math.pow(param,3)*0.0052-Math.pow(param,2)*0.0796+0.3874*param+0.3948;
			else if(nbNodes < 750)
				p0=-Math.pow(param,4)*0.0008+Math.pow(param,3)*0.0193-Math.pow(param,2)*0.158+0.5433*param+0.3433;
			else
				p0=-Math.pow(param,4)*0.001+Math.pow(param,3)*0.0218-Math.pow(param,2)*0.1727+0.5763*param+0.3263;
		}
		averageNumLeaves=p0*nbNodes;
		return averageNumLeaves; 
	}

	@Override
	public double getAverageHubNumber() { //'d' in paper
		double averageHub=0,p0,p1;
		if(param<=0){
			if(nbNodes<6.5)
				p0=Math.pow(param,3)*0.0003+Math.pow(param,2)*0.0062+0.0479*param+0.5097;
			else if(nbNodes<11.5)
				p0=Math.pow(param,3)*0.0006+Math.pow(param,2)*0.0113+0.0776*param+0.4627;
			else if(nbNodes<20)
				p0=Math.pow(param,3)*0.0005+Math.pow(param,2)*0.0122+0.1071*param+0.4911;
			else if(nbNodes<37)
				p0=Math.pow(param,3)*0.0004+Math.pow(param,2)*0.0108+0.1065*param+0.4775;
			else if(nbNodes<75)
				p0=Math.pow(param,3)*0.0002+Math.pow(param,2)*0.0066+0.0934*param+0.4786;
			else if(nbNodes <300)
				p0=Math.pow(param,3)*0.0002+Math.pow(param,2)*0.0084+0.1034*param+0.4842;
			else if(nbNodes < 750)
				p0=Math.pow(param,3)*0.0004+Math.pow(param,2)*0.0109+0.1149*param+0.4989;
			else
				p0=Math.pow(param,3)*0.0004+Math.pow(param,2)*0.0111+0.1156*param+0.4951;
		}else{
			if(nbNodes<6.5)
				p0=-Math.pow(param,3)*0.0006+Math.pow(param,2)*0.0038+0.0290*param+0.5205;
			else if(nbNodes<11.5)
				p0=Math.pow(param,3)*0.001-Math.pow(param,2)*0.0215+0.1550*param+0.4455;
			else if(nbNodes<20)
				p0=Math.pow(param,3)*0.0021-Math.pow(param,2)*0.0387+0.2363*param+0.4263;
			else if(nbNodes<37)
				p0=Math.pow(param,3)*0.0032-Math.pow(param,2)*0.0539+0.2955*param+0.4132;
			else if(nbNodes<75)
				p0=Math.pow(param,3)*0.0044-Math.pow(param,2)*0.0689+0.3493*param+0.4063;
			else if(nbNodes <300)
				p0=Math.pow(param,3)*0.0052-Math.pow(param,2)*0.0796+0.3874*param+0.3948;
			else if(nbNodes < 750)
				p0=-Math.pow(param,4)*0.0008+Math.pow(param,3)*0.0193-Math.pow(param,2)*0.158+0.5433*param+0.3433;
			else
				p0=-Math.pow(param,4)*0.001+Math.pow(param,3)*0.0218-Math.pow(param,2)*0.1727+0.5763*param+0.3263;
		}
		if(param<=1){
			if(nbNodes<6.5)
				p1=Math.pow(param,3)*0.0005+Math.pow(param,2)*0.0022-0.0277*param+0.2954;
			else if(nbNodes<11.5)
				p1=Math.pow(param,3)*0.0007+Math.pow(param,2)*0.0032-0.0465*param+0.3541;
			else if(nbNodes<20)
				p1=Math.pow(param,3)*0.0013+Math.pow(param,2)*0.0092-0.0686*param+0.2991;
			else if(nbNodes<37)
				p1=Math.pow(param,3)*0.0012+Math.pow(param,2)*0.0085-0.076*param+0.2928;
			else if(nbNodes<75)
				p1=Math.pow(param,3)*0.001+Math.pow(param,2)*0.0081-0.0737*param+0.2686;
			else if(nbNodes <300)
				p1=Math.pow(param,3)*0.0011+Math.pow(param,2)*0.0084-0.0764*param+0.2715;
			else if(nbNodes < 750)
				p1=Math.pow(param,3)*0.001+Math.pow(param,2)*0.0075-0.0808*param+0.2569;
			else
				p1=Math.pow(param,3)*0.001+Math.pow(param,2)*0.0078-0.0805*param+0.2618;
		}else{
			if(nbNodes<6.5)
				p1=Math.pow(param,2)*0.0046-0.0616*param+0.3063;
			else if(nbNodes<11.5)
				p1=-Math.pow(param,3)*0.0002+Math.pow(param,2)*0.0069-0.0628*param+0.2291;
			else if(nbNodes<20)
				p1=-Math.pow(param,3)*0.0019+Math.pow(param,2)*0.0324-0.1775*param+0.3503;
			else if(nbNodes<37)
				p1=0.0004*Math.pow(param,4)-Math.pow(param,3)*0.0095+Math.pow(param,2)*0.0787-0.2854*param+0.405;
			else if(nbNodes<75)
				p1=0.0007*Math.pow(param,4)-Math.pow(param,3)*0.015+Math.pow(param,2)*0.1136-0.3715*param+0.454;
			else if(nbNodes <300)
				p1=0.001*Math.pow(param,4)-Math.pow(param,3)*0.02+Math.pow(param,2)*0.1456-0.4521*param+0.5086;
			else if(nbNodes < 750)
				p1=0.0002*Math.pow(param,6)-0.0054*Math.pow(param,5)+Math.pow(param,4)*0.0629-Math.pow(param,3)*0.3798+Math.pow(param,2)*1.2417-2.0795*param+1.3931;
			else
				p1=0.0002*Math.pow(param,6)-0.0069*Math.pow(param,5)+Math.pow(param,4)*0.0793-Math.pow(param,3)*0.4705+Math.pow(param,2)*1.5063-2.457*param+1.5915;
			}
		averageHub=1-p1 +(nbNodes-1)*(1-p0-p1);
		return averageHub;
		
	}	

	private transient int[] degrees;
	private transient double[] weights;
	private transient int totalLinks;
	private transient AbstractGraphHandler agh;

	private void addLink(int a, int b) {
		agh.newLink(a, b);
		degrees[a] += 1;
		weights[a] = Math.pow(degrees[a]*2, param);
		degrees[b] += 1;
		weights[b] = Math.pow(degrees[b]*2, param);
		totalLinks = totalLinks + 2;
	}

	@Override
	public void generateRandomTopology(AbstractGraphHandler agh) {
		int nbNodeLocal;
		if (distForNodeNumber != null) {
			nbNodeLocal = (int)distForNodeNumber.inverseF(getStream().nextDouble());
			if (nbNodeLocal <= 0) throw new IllegalStateException("Cannot generate a graph with a negative number of nodes");
		} else {
			nbNodeLocal = nbNodes;
		}
		ensureLayer(agh);
		this.agh = agh;
		degrees = new int[nbNodeLocal];
		weights = new double[nbNodeLocal];
		totalLinks = 0;
		for(int k = 0 ; k < nbNodeLocal ; k++){
			degrees[k] = 0;
			weights[k] = 0;
		}
		agh.newNode(0,0);
		agh.newNode(0,0);
		addLink(0,1);

		for (int i = 2 ; i < nbNodeLocal ; i++) {
			agh.newNode(0,0);
			int alt = getStream().getRandomIndex(weights);

			addLink(i,alt);
		}
		if (layout != null) {
			layout.assignNodesPosition(agh);
		}
	}	

	@Override
	public String getName() {
		return "ScaleFree_Network";
	}
	
	public static class BarabasiAlbert extends WebTopologyGeneratorStub {

		@MethodDef
		public String generateScaleFree(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int nbNodes) throws InstantiationException{

			String s = test(nbNodes);	
			if (s != null) return s;			
			ScaleFreeNetworkGenerator sc = new ScaleFreeNetworkGenerator(nbNodes, 0, lay, PRNStream.getRandomStream());
			sc.generate(agh);
			return null;
		}

		@MethodDef
		public String generateScaleFree(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int nbNodes,
				@ParameterDef (name="Power factor")float param) throws InstantiationException{

			String s = test(nbNodes);	
			if (s != null) return s;			
			ScaleFreeNetworkGenerator sc = new ScaleFreeNetworkGenerator(nbNodes, param, lay, PRNStream.getRandomStream());
			sc.generate(agh);
			return null;
		}
		
		@MethodDef
		public String generateScaleFree(AbstractGraphHandler agh,
				@ParameterDef (name="Desired number of vertices")int nbNodes,
				@ParameterDef (name="Power factor")float param,
				@ParameterDef (name="Seed") int seed) throws InstantiationException{

			String s = test(nbNodes);	
			if (s != null) return s;			
			if (seed != 0) {
				ScaleFreeNetworkGenerator sc = new ScaleFreeNetworkGenerator(nbNodes, param, lay, PRNStream.getDefaultStream(seed*nbNodes));
				sc.generate(agh);
			} else {
				ScaleFreeNetworkGenerator sc = new ScaleFreeNetworkGenerator(nbNodes, param, lay, PRNStream.getDefaultStream(nbNodes));
				sc.generate(agh);				
			}
			return null;
		}		
		
		private static UnequalTreeLayout lay = new UnequalTreeLayout();


	}
}