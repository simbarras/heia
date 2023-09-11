package ch.epfl.javancox.inputs.topology.linker;

import java.util.Collection;
import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;

public class RingNodeLinker extends AbstractNodeLinker {

	@Override
	public void linkNodes(AbstractGraphHandler agh, PRNStream n) {
		Collection<NodeContainer> nodes = agh.getNodeContainers();
		int beginNode = agh.getSmallestNodeIndex();
		int currentNode = beginNode;
		nodes.remove(agh.getNodeContainer(currentNode));
		int nextNode = AbstractTopologyGenerator.getClosestNode(agh,currentNode,nodes);

		while(!nodes.isEmpty()){
			agh.newLink(currentNode,nextNode);
			currentNode=nextNode;
			nodes.remove(agh.getNodeContainer(currentNode));
			nextNode = AbstractTopologyGenerator.getClosestNode(agh,currentNode,nodes);
		}
		agh.newLink(currentNode,beginNode);
	}

	@Override
	public Map<String, String> getLinkerParameters() {
		Map<String, String> map = getNewMap(1);
		return map;
	}

}
