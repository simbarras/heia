package ch.epfl.javancox.inputs.topology.layout_reorganiser;

import java.util.List;
import java.util.Map;

import javancox.layout.AbstractTopologyLayout;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;


public class CircleLayout extends AbstractTopologyLayout {


	public static enum MODE {
		NO_OPT,
		DEGREE_BASED
	}

	private MODE mode;
	
	public CircleLayout() {
		this.mode = MODE.NO_OPT;
	}

	public CircleLayout(MODE mode) {
		this.mode = mode;
	}

	@Override
	public void assignNodesPosition(int maxScreenx, int maxScreeny, AbstractGraphHandler agh) {
		agh.setModificationEventEnabledWithoutCallingBigChanges(false);
		int[] positions = findSequence(agh);
	//	int[] positions = TypeParser.parseIntArray(AskQuestion.askString("sequence"));
		if (positions.length != agh.getHighestNodeIndex()+1) {
			throw new IllegalStateException();
		}
		int centralPointX = maxScreenx/2;
		int centralPointY = maxScreeny/2;
		int width = Math.min(centralPointX, centralPointY);
		float angleIncrement = (float)(2f*Math.PI)/(agh.getHighestNodeIndex()+1);
		for (int i = 0 ; i < positions.length ; i++) {
			int x = (int)(Math.sin(angleIncrement*i)*width);
			int y = (int)(Math.cos(angleIncrement*i)*width);
			NodeContainer nc = agh.getNodeContainer(positions[i]);
			if (nc != null) {
				nc.attribute(XMLTagKeywords.POS_X).setValue(x+centralPointX);
				nc.attribute(XMLTagKeywords.POS_Y).setValue(y+centralPointY);
			}
		}
		agh.setModificationEventEnabledWithoutCallingBigChanges(true);
	}

	public int[] findSequence(AbstractGraphHandler agh) {
		switch(mode) {
		case NO_OPT:
			return findSequenceNoOpt(agh);
		case DEGREE_BASED:
			return findSequenceDeg(agh);
		}
		return null;
	}

	public int[] findSequenceNoOpt(AbstractGraphHandler agh) {
		int[] seq = new int[agh.getHighestNodeIndex()+1];
		for (int i = 0 ; i < seq.length ; i++) {
			seq[i] = i;
		}
		return seq;
	}

	public int[] findSequenceDeg(AbstractGraphHandler agh) {
		int nbNodes = agh.getHighestNodeIndex()+1;
		boolean[] done = new boolean[nbNodes];
		int[] positions = new int[nbNodes];
		int position = 1;
		int nbDone = 1;
		positions[0] = 0;
		done[0] = true;
		while (nbDone < nbNodes) {
			NodeContainer nc = agh.getNodeContainer(positions[position-1]);
			List<NodeContainer> neigh = nc.getConnectedNodes();
			int choosenIndex = findIndex(neigh, done);

			if (choosenIndex < 0) {
				choosenIndex = 0;
				while (done[choosenIndex]) {
					choosenIndex++;
				}
			}
			positions[position] = choosenIndex;
			done[choosenIndex] = true;
			nbDone++;
			position++;
		}


		return positions;
	}

	private int findIndex(List<NodeContainer> neighs, boolean[] done) {
		for (NodeContainer nc : neighs) {
			int index = nc.getIndex();
			if (done[index] == false) {
				return index;
			}
		}
		return -1;
	}

	@Override
	public Map<String, String> getLayoutParameters() {
		return getEmptyMap();
	}

}
