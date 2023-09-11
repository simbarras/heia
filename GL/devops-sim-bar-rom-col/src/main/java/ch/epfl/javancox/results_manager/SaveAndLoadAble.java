package ch.epfl.javancox.results_manager;


public interface SaveAndLoadAble {

	public void loadFromFile(java.io.File f);

	public void saveToFile(java.io.File f);

}
