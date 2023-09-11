package ch.epfl.javanco.path;

import ch.epfl.general_libraries.path.PathFilter;
import ch.epfl.general_libraries.path.PathRelativeFilter;

public class JavancoPathRelativeFilter extends PathRelativeFilter {

	public JavancoPathRelativeFilter(JavancoPathCalculator pcalc, 
			float limitPath) {
				
		this(pcalc, limitPath, limitPath, PathRelativeFilter.PLUS);
	}
	
	public JavancoPathRelativeFilter(JavancoPathCalculator pcalc, 
			float limitPath,
			boolean directed) {
				
		this(pcalc, limitPath, limitPath, PathRelativeFilter.PLUS, true, null, directed);
	}	


	public JavancoPathRelativeFilter(JavancoPathCalculator pcalc, 
			float limitPath,
			float limitPrefix,
			int operation) {
				
		this(pcalc,limitPath, limitPrefix, operation, true, null, false);
	}

	public JavancoPathRelativeFilter(JavancoPathCalculator pcalc, 
			float limitPath,
			float limitPrefix,
			int operation,
			boolean inclusive,
			boolean directed) {
				
		this(pcalc,limitPath, limitPrefix, operation, inclusive, null, directed);
	}	
	
	public JavancoPathRelativeFilter(JavancoPathCalculator pcalc, 
			float limitPath,
			float limitPrefix,
			int operation,
			boolean inclusive,
			PathFilter nextFilter,
			boolean directed) {
				
			super(nextFilter);
		this.limitPath = limitPath;
		this.limitPrefix = limitPrefix;
		this.operation = operation;
		this.inclusive = inclusive;	
		this.pc = pcalc;		
		this.referencePathSet = pcalc.getShortestPathSet(directed);
		if (!directed) {	
			this.referencePathSet.setSymmetry();
		}
		checkInit();
			
	}
	
}
