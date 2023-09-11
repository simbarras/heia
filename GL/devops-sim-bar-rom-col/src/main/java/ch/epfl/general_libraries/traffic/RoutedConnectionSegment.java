package ch.epfl.general_libraries.traffic;


public class RoutedConnectionSegment {
	
	private RoutedConnection parentConnection;
	private ConnectionSupport parentLink;

	public RoutedConnectionSegment(ConnectionSupport parent) {
		this.parentLink = parent;
	}

	public void setParentConnection(RoutedConnection c) {
		this.parentConnection = c;
	}

	public void setParentLink(ConnectionSupport link) {
		parentLink = link;
	}

	public ConnectionSupport getSupportingLink() {
		return parentLink;
	}

	public RoutedConnection getParentConnection() {
		return parentConnection;
	}	
	
	
}
