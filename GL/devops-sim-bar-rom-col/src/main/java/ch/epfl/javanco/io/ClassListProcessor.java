package ch.epfl.javanco.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;

public class ClassListProcessor<T> extends LinkedList<Class<? extends T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public ClassListProcessor(String pathTofile) {
		try {
			URL url = JavancoFile.findRessource(pathTofile);
			BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = read.readLine()) != null) {
				try {
					Class c = Class.forName(line);
					Class<? extends T> cCasted = c;
					add(cCasted);
				}
				catch (ClassCastException e) {}
				catch (ClassNotFoundException e) {}
			}
		}
		catch (Exception e) {}
	}
}
