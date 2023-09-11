package ch.epfl.javancox.inputs.compounds;


import ch.epfl.general_libraries.experiment_aut.AbstractExperimentBlock;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.xml.XMLTagKeywords;

public abstract class AbstractGraphExperimentInput extends AbstractExperimentBlock {
	
	public abstract void createImpl(AbstractGraphHandler agh);
	public abstract String getPhysicalLayerName();
	public abstract String getLengthAttributeName();
	public boolean created = false;
	
	protected AbstractGraphHandler agh;
	
	public void create() {
		if (!created) {
			agh = Javanco.getDefaultGraphHandler(true);
			getAgh().activateMainDataHandler();
			getAgh().activateGraphicalDataHandler();
			getAgh().setModificationEventEnabledWithoutCallingBigChanges(false);
			createImpl(agh);
			getAgh().setModificationEventEnabledWithoutCallingBigChanges(true);
		} else {
			agh = getAgh();
		}
		created = true;
	}
	
	public AbstractGraphHandler getAgh() {
		return agh;
	}
	
	public LayerContainer getTopologyLayer() {
		return getAgh().getLayerContainer(getPhysicalLayerName());
	}	

	public static void setLinksLengths(AbstractGraphHandler agh) {
		double totalDist = 0;
		boolean needToCreate = false;
		for (LinkContainer lc : agh.getLinkContainers()) {
			if (lc.attribute("length", false) == null) {
				needToCreate = true;
				break;
			}
		}
		if (needToCreate) {
			for (LinkContainer lc : agh.getLinkContainers()) {
				totalDist += lc.setGeodesicLinkLength();
			}
			if (totalDist == 0) {
				for (LinkContainer lc : agh.getLinkContainers()) {
					lc.attribute(XMLTagKeywords.LENGTH).setValue("1");
				}
			}
		}
	}	
}
