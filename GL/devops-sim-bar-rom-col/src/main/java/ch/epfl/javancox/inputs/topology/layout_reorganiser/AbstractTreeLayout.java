package ch.epfl.javancox.inputs.topology.layout_reorganiser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javancox.layout.AbstractTopologyLayout;
import ch.epfl.general_libraries.utils.MoreArrays;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.tree.AbstractSpanningTreeFinder;

public abstract class AbstractTreeLayout extends AbstractTopologyLayout {

	protected ArrayList[] allChild;
	protected int max_level;
	protected int[] numChild;
	protected int rootIndex = 0;
	protected int[][] futPos;

	private AbstractSpanningTreeFinder finder;

	public AbstractTreeLayout(int rootIndex) {
		this.rootIndex = rootIndex;
	}

	public AbstractTreeLayout(AbstractSpanningTreeFinder finder) {
		this.finder = finder;
	}


	@Override
	public Map<String, String> getLayoutParameters() {
		Map<String, String> map = getNewMap(1);
		return map;
	}
	
	protected void positionNodesInTree(int maxScreenx, int maxScreeny, AbstractGraphHandler agh, LayerContainer lc, int rootIndex) {
		int nbNodes = agh.getHighestNodeIndex()+1;
		
		Set<Integer> allIndexes = agh.getNodeIndexesSet();
		
		numChild = new int[nbNodes];

		// hard definition of the root index - by convention, 0
		NodeContainer node0 = agh.getNodeContainer(rootIndex);
		
		int offset = 0;
		
		while (allIndexes.size() > 0) {
			
			if (node0 == null) {
				node0 = agh.getNodeContainer(allIndexes.iterator().next());
			}

			Set<Integer> set = positionNodesInTreeSub(maxScreenx, maxScreeny, agh, lc, node0, offset);
			
			allIndexes.removeAll(set);
			node0 = null;
			offset = MoreArrays.max(futPos[0])+10;
		}	
	}
	

	protected abstract Set<Integer> positionNodesInTreeSub(int maxScreenx, 
														   int maxScreeny, 
														   AbstractGraphHandler agh, 
														   LayerContainer lc,
														   NodeContainer node0,
														   int offset);

	@Override
	public void assignNodesPosition(AbstractGraphHandler agh) {
		rootIndex = findRoot(agh);
		for (NodeContainer lc : agh.getNodeContainers()) {
			lc.removeAttribute("father");
			lc.removeAttribute("level");
		}
		for (LinkContainer lc : agh.getLinkContainers()) {
			lc.removeAttribute("spanning");
		}
		int[] extre = agh.getExtremities();
		int inputWidth = (extre[1]-extre[0]);
		int inputHeight = (extre[3]-extre[2]);
		if (inputHeight <= 1 && inputWidth <= 1) {
			assignNodesPosition(1000, 1000, agh);
			int minX = Integer.MAX_VALUE;
			int maxX = -Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = -Integer.MAX_VALUE;
			for (int i = 0 ; i < futPos[0].length ; i++) {
				if (futPos[0][i] < minX) {
					minX = futPos[0][i];
				}
				if (futPos[0][i] > maxX) {
					maxX = futPos[0][i];
				}
				if (futPos[1][i] < minY) {
					minY = futPos[1][i];
				}
				if (futPos[1][i] > maxY) {
					maxY = futPos[1][i];
				}												
			}
			float difX = maxX - minX;
			float difY = maxY - minY;
			for (int i = 0 ; i < futPos[0].length ; i++) {
				futPos[0][i] = (int)Math.round((float)(futPos[0][i] - minX)/difX*1000f);
				futPos[1][i] = (int)Math.round((float)(futPos[1][i] - minY)/difY*1000f);				
			}			
		} else {
			assignNodesPosition(inputWidth, inputHeight, agh);			
		}
		for (NodeContainer nc : agh.getNodeContainers()) {
			nc.attribute("pos_x").setValue(futPos[0][nc.getIndex()]);
			nc.attribute("pos_y").setValue(futPos[1][nc.getIndex()]);
		}
	}

	private int findRoot(AbstractGraphHandler agh) {
		for (int i = 0 ; i <= agh.getHighestNodeIndex() ; i++) {
			NodeContainer nc = agh.getNodeContainer(i);
			if (nc == null) continue;
			if (nc.attribute("root", false) != null) return nc.getIndex();
		}
		return rootIndex;
	}

	@Override
	public void assignNodesPosition(int maxScreenx, int maxScreeny, AbstractGraphHandler agh) {
		futPos = new int[2][agh.getHighestNodeIndex()+1];
		if (finder != null) {
			rootIndex = finder.createSpanningTree(agh, null, "spanning");
			positionNodesInTree(maxScreenx, maxScreeny, agh, agh.getLayerContainer("spanning"), rootIndex);
			agh.removeElement(agh.getLayerContainer("spanning"));
		} else {
			positionNodesInTree(maxScreenx, maxScreeny, agh, null, rootIndex);
		}
	}


	@SuppressWarnings("unchecked")
	protected Set<Integer> exploreSubTree(NodeContainer nc, int max_screen_y, LayerContainer lc) {
		int ncLevel = nc.attribute("level").intValue();
		int ncIndex = nc.getIndex();

		int levelPlusOne = ncLevel+1;
		Collection<NodeContainer> nodeCol;
		if (lc != null) {
			nodeCol = nc.getConnectedNodes(lc);
		} else {
			nodeCol = nc.getConnectedNodes();
		}
		Set<Integer> nodePlaced = new TreeSet<Integer>();
		for (NodeContainer child : nodeCol) {
			if (child.attribute("level", false) == null) {
				if (levelPlusOne > max_level) {
					max_level = levelPlusOne;
				}
				nodePlaced.add(child.getIndex());
				child.attribute("level").setValue(levelPlusOne);
				child.attribute("father").setValue(ncIndex);
				setNodeY(child, max_screen_y - (100 * (ncLevel + 1)));
				allChild[ncIndex].add(child);
				nodePlaced.addAll(exploreSubTree(child, max_screen_y, lc));
			}
		}
		numChild[ncIndex] = allChild[ncIndex].size();
		return nodePlaced;
	}

	@SuppressWarnings("unchecked")
	protected int[] getMaxNodeInEachSubLevel(NodeContainer nc) {
		if (numChild[nc.getIndex()] > 0) {
			int[] subNodesTotal = new int[]{1};
			for (NodeContainer candidate : (Collection<NodeContainer>)allChild[nc.getIndex()]) {
				int[] subNode = getMaxNodeInEachSubLevel(candidate);
				if (subNodesTotal.length < subNode.length+1) {
					subNodesTotal = Arrays.copyOf(subNodesTotal, subNode.length+1);
				}
				for (int i = 0 ; i < subNode.length ; i++) {
					subNodesTotal[i+1] += subNode[i];
				}
			}
			return subNodesTotal;
		} else {
			return new int[]{1};
		}
	}

	protected Set<Integer> setAllChildStructure(int max_screen_x, 
												int max_screen_y, 
												AbstractGraphHandler agh, 
												LayerContainer lc,
												int rootIndex_) {
		int nbNodes = agh.getNumberOfNodes();
		NodeContainer root = agh.getNodeContainer(rootIndex_);
		root.attribute("level").setValue(0);
		root.attribute("father").setValue(0);
		setNodeX(root, max_screen_x/2);
		setNodeY(root, max_screen_y);
		max_level=0;
		allChild = new ArrayList[nbNodes];
		for(int i = 0; i < allChild.length; i++) {
			allChild[i] = new ArrayList<NodeContainer>();
			numChild[i] = 0;
		}
		Set<Integer> placed = exploreSubTree(root, max_screen_y, lc);
		placed.add(rootIndex_);
		return placed;
	}

	protected void setNodeX(NodeContainer nc, int x) {
		futPos[0][nc.getIndex()] = x;
		//nc.attribute("pos_x").setValue(x+xOffset);
	}

	protected void setNodeY(NodeContainer nc, int y) {
		futPos[1][nc.getIndex()] = y;
		//nc.attribute("pos_y").setValue(y+yOffset);
	}

}
