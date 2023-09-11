package ch.epfl.javanco.path;

import ch.epfl.general_libraries.path.PathRelativeFilter;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.xml.XMLTagKeywords;

public class JavancoShortestPathRelativeFilter extends PathRelativeFilter {
	
	public JavancoShortestPathRelativeFilter(AbstractGraphHandler agh) {
		super(new JavancoShortestPathSet(agh, XMLTagKeywords.LENGTH.toString(), false), 
			new AttributeBasedCalculator("physical", "length"),1);
	}

}
