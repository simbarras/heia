package javancox.topogen;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.xml.XMLTagKeywords;

public abstract class GeneratorStub {

	public void setLengths(AbstractGraphHandler agh) {
		agh.getEditedLayer().setLinksLengths(XMLTagKeywords.LENGTH.toString());

	}
}
