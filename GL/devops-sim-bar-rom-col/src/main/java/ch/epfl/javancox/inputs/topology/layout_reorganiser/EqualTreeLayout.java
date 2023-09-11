package ch.epfl.javancox.inputs.topology.layout_reorganiser;

import java.util.Collection;
import java.util.Set;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.tree.AbstractSpanningTreeFinder;


public class EqualTreeLayout extends AbstractTreeLayout {

	public EqualTreeLayout() {
		this (0);
	}


	public EqualTreeLayout(int rootIndex) {
		super(rootIndex);
	}

	public EqualTreeLayout(AbstractSpanningTreeFinder finder) {
		super(finder);
	}

	@SuppressWarnings("unchecked")
	private void spaceAttribution_2 (NodeContainer nc, int xMax, int offset, int[] numNodesLevel, int[] index, AbstractGraphHandler agh){
		Collection<NodeContainer> childs = allChild[nc.getIndex()];
		if(allChild[nc.getIndex()].size() != 0){
			for(NodeContainer candidate : childs){
				int level =candidate.attribute("level").intValue();
				if(numNodesLevel[level] == 1){
					NodeContainer nfather = agh.getNodeContainer(candidate.attribute("father").intValue());
					setNodeX(candidate, futPos[0][nfather.getIndex()]+offset);
				}else{
					setNodeX(candidate, xMax * index[level] / (numNodesLevel[level]-1)+offset);
				}
				index[level] += 1;
				spaceAttribution_2(candidate, xMax, offset, numNodesLevel, index, agh);
			}
		}
	}

	@Override
	public Set<Integer> positionNodesInTreeSub(int max_screen_x, 
									   int max_screen_y,
									   AbstractGraphHandler agh,
									   LayerContainer lc,
									   NodeContainer node0,
									   int offset){

		//level + father + pos_y node attribution
		Set<Integer> set = setAllChildStructure(max_screen_x, max_screen_y, agh, lc, node0.getIndex());

		//pos_x node attribution
		int[] numNodesLevel = getMaxNodeInEachSubLevel(node0);
		int[] index = new int[numNodesLevel.length];
		spaceAttribution_2(node0, max_screen_x, offset, numNodesLevel, index, agh);
		
		return set;
	}
}
