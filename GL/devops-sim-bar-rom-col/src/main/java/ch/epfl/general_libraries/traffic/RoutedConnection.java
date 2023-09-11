package ch.epfl.general_libraries.traffic;

import java.util.List;

import ch.epfl.general_libraries.path.Path;

public interface RoutedConnection {
	
	public List<? extends ConnectionSupport> getSupportingLinks();
	
	public List<? extends RoutedConnectionSegment> getConnectionSegments();	
		
	public Path getRouting();
}
