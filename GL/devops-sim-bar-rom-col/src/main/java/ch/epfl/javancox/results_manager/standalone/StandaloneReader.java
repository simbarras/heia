package ch.epfl.javancox.results_manager.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;

import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;


public class StandaloneReader {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length == 0){
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File("."));
			chooser.showOpenDialog(null);
			read(chooser.getSelectedFile());
		} else {
			read(new File(args[0]));
		}
		

		
	}
	
	public static void read(File file) throws Exception {
		SmartDataPointCollector db = new SmartDataPointCollector();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			insertLine(line, db);
		}
		DefaultResultDisplayingGUI.displayDefault(db);
		br.close();
		
	}
	
	public static void insertLine(String line, AbstractResultsManager db) {
		Execution ex = new Execution();	
		DataPoint dp = new DataPoint();
		String[] sep = line.split("\\$");
		String[] inputs = sep[0].split(",");
		String[] outputs = sep[1].split(",");
		for (int i = 0 ; i < inputs.length ; i++ ) {
			String[] dd = inputs[i].split("=");
			dp.addProperty(dd[0], dd[1]);
		}
		for (int i = 0 ; i < outputs.length ; i++) {
			String[] dd = outputs[i].split("=");
			dp.addResultProperty(dd[0], dd[1]);
		}	
		ex.addDataPoint(dp);
		db.addExecution(ex);		
	}
	
}
