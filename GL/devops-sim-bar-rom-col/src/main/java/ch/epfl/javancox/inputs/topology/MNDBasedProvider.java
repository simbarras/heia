package ch.epfl.javancox.inputs.topology;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javancox.topogen.AbstractTopologyGenerator.WebTopologyGeneratorStub;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javancox.inputs.AbstractTopologyProvider;

public class MNDBasedProvider extends AbstractTopologyProvider {

	private String fileName;
	private String topologyLayerName;
	private MODE direction;

	public enum MODE {
		NO_CHANGE,
		UNDIR_CHANGE_TO_CANONICAL,
		UNDIR_REMOVE_NON_CANONICAL,
		BIDIR_ALL_MISSING_LINKS
	}

	public static final MODE NO_CHANGE = MODE.NO_CHANGE;
	public static final MODE UNDIR_CHANGE_TO_CANONICAL = MODE.UNDIR_CHANGE_TO_CANONICAL;
	public static final MODE UNDIR_REMOVE_NON_CANONICAL = MODE.UNDIR_REMOVE_NON_CANONICAL;
	public static final MODE BIDIR_ALL_MISSING_LINKS = MODE.BIDIR_ALL_MISSING_LINKS;

	/*	public static final String "UNDIR_CHANGE_TO_CANONICAL";
	public static final String "UNDIR_REMOVE_NON_CANONICAL";
	public static final String "BIDIR_DOUBLE_ALL_LINKS";*/


	public MNDBasedProvider(@ParamName(name="filename") String fileName) {
		this(fileName, "physical", BIDIR_ALL_MISSING_LINKS);
	}
	
	/**
	 * If directed is false, the downer part of the connectivity matrix will be ignored <=>
	 * the links with origin node index > end node index will be deleted
	 */	 
	public MNDBasedProvider(String fileName, MODE direction) {
		this.fileName = fileName;
		this.topologyLayerName = "physical";
		this.direction = direction;
	}	

	/**
	 * If directed is false, the downer part of the connectivity matrix will be ignored <=>
	 * the links with origin node index > end node index will be deleted
	 */	 
	public MNDBasedProvider(String fileName, String topologyLayerName, MODE direction) {
		this.fileName = fileName;
		this.topologyLayerName = topologyLayerName;
		this.direction = direction;
	}
	
	@Override
	public String getTopologyName() {
		return fileName;
	}

	/**
	 * Method provideTopology
	 *
	 *
	 * @param agh
	 *
	 */
	@Override
	public void provideTopology(AbstractGraphHandler agh) {
		boolean sucess = agh.openNetworkFile(fileName);
		if (sucess == false) {
			throw new IllegalStateException("Cannot read specified file, probably file is not in the graphs directory");
		}
		LayerContainer layC = agh.getLayerContainer(topologyLayerName);
		switch(direction) {
		case NO_CHANGE:
			return;
		case UNDIR_CHANGE_TO_CANONICAL:
			LinkedList<LinkContainer> toFlip = new LinkedList<LinkContainer>();
			LinkedList<AbstractElementContainer> toRemove = new LinkedList<AbstractElementContainer>();
			for (LinkContainer lc : layC.getLinkContainers()) {
				if (lc.getStartNodeIndex() > lc.getEndNodeIndex()) {
					if (layC.getLinkContainer(lc.getEndNodeIndex(), lc.getStartNodeIndex()) != null) {
						toRemove.add(lc);
					} else {
						toFlip.add(lc);
					}
				}
			}
			for (AbstractElementContainer lc : toRemove) {
				agh.removeElement(lc);
			}
			for (LinkContainer lc : toFlip) {
				lc.flip();
			}

			return;
		case UNDIR_REMOVE_NON_CANONICAL:
			toRemove = new LinkedList<AbstractElementContainer>();
			for (LinkContainer lc : layC.getLinkContainers()) {
				if (lc.getStartNodeIndex() > lc.getEndNodeIndex()) {
					toRemove.add(lc);
				}
			}
			agh.removeElements(toRemove);
			return;
		case BIDIR_ALL_MISSING_LINKS:
			layC.symmetrize();
			return;

		}
	}

	/**
	 * Method getTopologyLayerName
	 *
	 *
	 * @return
	 *
	 */
	@Override
	public String getTopologyLayerName() {
		return topologyLayerName;
	}

	/**
	 * Method getAllParameters
	 *
	 *
	 * @return
	 *
	 */
	@Override
	public Map<String,String> getAllParameters() {
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("topology_provided_by", "file");
		params.put("file",fileName);
		return params;
	}
	
	public static class Predefined extends WebTopologyGeneratorStub {

		@MethodDef
		public String load(AbstractGraphHandler agh,
				@ForcedParameterDef(possibleValues={"German 50", "Exodus", "NSFNET", "NATIONAL"})
				@ParameterDef (name="Name of the network")String name) throws InstantiationException{
			
			if (name.equals("German 50<br>")) name = "German_50.xml";
			if (name.equals("Exodus<br>")) name = "Exodus_uni.xml";
			if (name.equals("NSFNET<br>")) name = "NSFNET.xml";
			if (name.equals("NATIONAL<br>")) name = "National.xml";
			
			MNDBasedProvider prov = new MNDBasedProvider(name, "physical", UNDIR_CHANGE_TO_CANONICAL);
			
			prov.provideTopology(agh);
			return null;
		}



	}	
	
	
	
	
}
