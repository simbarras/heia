package ch.epfl.javancox.inputs.topology.layout_reorganiser;

import java.util.Collection;
import java.util.Set;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.tree.AbstractSpanningTreeFinder;


public class UnequalTreeLayout extends AbstractTreeLayout {

	public UnequalTreeLayout() {
		this(0);
	}

	public UnequalTreeLayout(int rootIndex) {
		super(rootIndex);
	}

	public UnequalTreeLayout(AbstractSpanningTreeFinder finder) {
		super(finder);
	}

	@SuppressWarnings("unchecked")
	private void spaceAttribution (NodeContainer nc, int bLeft, int bRight){
		int sumMax = 0;
		Collection<NodeContainer> childs = allChild[nc.getIndex()];
		int[] maxs = new int[childs.size()];
		setNodeX(nc, (bRight - bLeft)/2 + bLeft);
		if(allChild[nc.getIndex()].size() != 0){
			int index = 0;
			for(NodeContainer candidate : childs){
				int[] levs = getMaxNodeInEachSubLevel(candidate);
				int max = 0;
				for (int i = 0 ; i < levs.length ; i++) {
					if (levs[i] > max) {
						max = levs[i];
					}
				}
				maxs[index] = max;
				index++;
				sumMax += max;
			}
			int spaceNode = (bRight - bLeft) / sumMax;
			int newLeft = bLeft;
			int k=0;
			for(NodeContainer candidate : childs){
				int newRight = newLeft + spaceNode * maxs[k];
				spaceAttribution(candidate, newLeft, newRight);
				k++;
				newLeft = newRight;
			}
		}
	}

	@Override
	public Set<Integer> positionNodesInTreeSub(int max_screen_x, 
											   int max_screen_y, 
											   AbstractGraphHandler agh, 
											   LayerContainer lc,
											   NodeContainer node0, int offset){
		//level + father + pos_y node attribution
		Set<Integer> set = setAllChildStructure(max_screen_x, max_screen_y, agh, lc,  node0.getIndex());
		
		spaceAttribution(node0,offset,max_screen_x+offset);
		
		return set;
	}

}
