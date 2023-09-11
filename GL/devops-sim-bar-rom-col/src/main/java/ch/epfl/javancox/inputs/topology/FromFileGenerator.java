package ch.epfl.javancox.inputs.topology;

import java.io.File;
import java.util.Map;

import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.imports.AdjacencyListImporter;
import javancox.topogen.AbstractTopologyGenerator;

public class FromFileGenerator extends AbstractTopologyGenerator {
	
	private String fileName;
	private String type;
	
	public FromFileGenerator(String fileName) {
		this.fileName = fileName;
	}
	
	public FromFileGenerator(String fileName, String type) {
		this.fileName = fileName;
		this.type = type;
	}

	@Override
	public void generate(AbstractGraphHandler agh) {
		if (type.equals("adj")) {
			AdjacencyListImporter imp = new AdjacencyListImporter();
			try {
				imp.importTopology(agh, new File(fileName));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			agh.openNetworkFile(fileName);
		}
	}

	@Override
	public String getName() {
		return "file_" + fileName; 
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getGeneratorParameters() {
		return SimpleMap.getMap("filename", fileName);
	}

	@Override
	public int getNumberOfNodes() {
		return -1;
	}

}
