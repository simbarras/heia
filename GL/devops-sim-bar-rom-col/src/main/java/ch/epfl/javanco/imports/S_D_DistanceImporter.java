package ch.epfl.javanco.imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class S_D_DistanceImporter extends AbstractTopologyImporter {

	/**
	 * Method importTopology
	 *
	 *
	 * @param agh
	 * @param f
	 *
	 @throws Exception
	 *
	 */
	@Override
	public void importTopology(AbstractGraphHandler agh, File f) throws Exception {
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);

		br.readLine();
		int nodes = Integer.parseInt(br.readLine());
		for (int i = 0 ; i < nodes ; i++) {
			agh.newNode();
		}
		br.readLine();
		br.readLine();
		String s = "";
		while (s.startsWith("#") == false) {
			s = br.readLine();
			String[] parts = s.split(" ");
			LinkContainer lc = agh.newLink(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
			lc.attribute("length").setValue(parts[2]);
		}
		br.close();

	}
}
