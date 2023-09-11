package ch.epfl.javanco.imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.base.AbstractGraphHandler;

public class AdjacencyListImporter extends AbstractTopologyImporter {

	@Override
	public void importTopology(AbstractGraphHandler agh, File f)
			throws Exception {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		importTopology(agh, br);

	}
	
	public void importTopology(AbstractGraphHandler agh, URL url) throws IOException {
		importTopology(agh, new BufferedReader(new InputStreamReader(url.openStream())));
	}
	
	private void importTopology(AbstractGraphHandler agh, BufferedReader re) throws IOException {
		try {
			agh.setModificationEventEnabledWithoutCallingBigChanges(false);
			String line = null;
			ArrayList<ArrayList<Integer>> tot = new ArrayList<ArrayList<Integer>>();
			int max = 0;
			while ((line = re.readLine()) != null) {
				if (line.equals("")) continue;
				int[] indexes = TypeParser.parseIntArray(line);
				ArrayList<Integer> list = new ArrayList<Integer>(indexes.length);
				for (int i : indexes) {
					list.add(i);
					if (i > max) max = i;
				}
				tot.add(list);
			}
			for (int i = 0 ; i <= max ; i++) {
				agh.newNode();
			}
			int index = 0;
			for (ArrayList<Integer> l : tot) {
				for (int h : l) {
					if (index < h) 
						agh.newLink(index, h);
				}
				index++;
			}
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
		agh.setModificationEventEnabled(true, new CasualEvent(this));
		
	}

}
