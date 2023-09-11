package ch.epfl.javanco.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;

public class ComponentCounter {

	ArrayList<TreeSet<NodeContainer>> components = new ArrayList<TreeSet<NodeContainer>>();
	
	public ComponentCounter(AbstractGraphHandler agh) {
		this(agh, agh.getEditedLayer());
	}

	public ComponentCounter(AbstractGraphHandler agh, String layerName) {
		this(agh, agh.getLayerContainer(layerName));
	}

	public ComponentCounter(AbstractGraphHandler agh, LayerContainer lc) {
		TreeSet<NodeContainer> nodeSet = new TreeSet<NodeContainer>();
		for (NodeContainer nc : agh.getNodeContainers()) {
			nodeSet.add(nc);
		}
		while (nodeSet.size() > 0) {
			TreeSet<NodeContainer> newComponent = new TreeSet<NodeContainer>();
			NodeContainer removed = nodeSet.pollFirst();
			newComponent.add(removed);
			removeRecursive(removed, nodeSet, lc, newComponent);
			components.add(newComponent);
		}
	}

	public boolean isConvex() {
		return (components.size() == 1);
	}

	public int getNumberOfComponents() {
		return components.size();
	}

	public int getLargestComponentSize() {
		int max = 0;
		for (TreeSet<NodeContainer> tr : components) {
			if (tr.size() > max) {
				max = tr.size();
			}
		}
		return max;
	}

	public Set<NodeContainer> getLargestComponent() {
		int max = 0;
		TreeSet<NodeContainer> maxComp =null;
		for (TreeSet<NodeContainer> tr : components) {
			if (tr.size() > max) {
				max = tr.size();
				maxComp =tr;
			}
		}
		return maxComp;
	}

	public Collection<Set<NodeContainer>> getMinorComponents() {
		Set<NodeContainer> maxComp = getLargestComponent();
		Collection<Set<NodeContainer>> minors = new ArrayList<Set<NodeContainer>>(getNumberOfComponents()-1);
		for (Set<NodeContainer> tr : components) {
			if (tr.equals(maxComp) == false) {
				minors.add(tr);
			}
		}
		return minors;
	}

	private void removeRecursive(NodeContainer removed,
			TreeSet<NodeContainer> map,
			LayerContainer layc,
			TreeSet<NodeContainer> actualComponent) {
		if (removed != null) {
			for (LinkContainer lc : removed.getConnectedLinks(layc)) {
				NodeContainer rm = lc.getOtherNodeContainer(removed.getIndex());
				if (map.remove(rm)) {
					actualComponent.add(rm);
					removeRecursive(rm, map, layc, actualComponent);
				}
			}
		}
	}
}
