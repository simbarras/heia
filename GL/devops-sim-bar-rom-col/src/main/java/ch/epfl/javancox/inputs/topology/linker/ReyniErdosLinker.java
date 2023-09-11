package ch.epfl.javancox.inputs.topology.linker;

import java.util.Map;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class ReyniErdosLinker extends AbstractNodeLinker {

	private float linkProbability;

	public ReyniErdosLinker(float linkProbability) {
		this.linkProbability = linkProbability;
	}

	@Override
	public void linkNodes(AbstractGraphHandler agh, PRNStream stream) {
		int aux= agh.getNumberOfNodes();
		int numberOfNodes =aux;
		int numberOfLinks = (int)(linkProbability * (numberOfNodes*(numberOfNodes-1))/2);
		for(int s=agh.getSmallestNodeIndex();s<=agh.getHighestNodeIndex();s++){
			for(int d=agh.getSmallestNodeIndex();d<=agh.getHighestNodeIndex();d++){
				if(s!=d){
					if (stream.nextDouble()<=linkProbability){
						if ((agh.getLinkContainer(s,d)==null)&&(agh.getLinkContainer(d,s)==null)&&(aux!=numberOfLinks)){
							agh.newLink(s,d);
							aux++;
						}
					}
				}
			}
		}
	}

	@Override
	public Map<String, String> getLinkerParameters() {
		Map<String, String> map = getNewMap(1);
		return map;
	}

}
