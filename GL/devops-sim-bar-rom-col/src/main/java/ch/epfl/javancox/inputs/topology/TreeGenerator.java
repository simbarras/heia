package ch.epfl.javancox.inputs.topology;

import java.util.Map;

import ch.epfl.general_libraries.random.PRNStream;
import ch.epfl.javanco.base.AbstractGraphHandler;

public interface TreeGenerator {
	
	public Map<String, String> getAllParameters();

	public void generateTree(AbstractGraphHandler agh);

	public int getNumberOfNodes();

	public double getAverageNumberOfLeaves();

	public double getAverageHubNumber();

	public void generateTree(AbstractGraphHandler agh, PRNStream stream);

//	public double getMeanNumberOfNodes();

}
