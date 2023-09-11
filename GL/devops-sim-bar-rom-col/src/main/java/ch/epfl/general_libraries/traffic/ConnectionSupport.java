package ch.epfl.general_libraries.traffic;

import java.util.List;

public interface ConnectionSupport {
	
	public void addSupportedConnection(RoutedConnection e);
	
	public List<RoutedConnection> getSupportedConnections();
	
}
