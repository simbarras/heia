package ch.epfl.javanco.abstract_objects;

import java.util.ArrayList;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.results.Property;
import ch.epfl.general_libraries.results.ResultProperty;
import ch.epfl.general_libraries.traffic.RoutedConnection;
import ch.epfl.javanco.network.DefaultLinkImpl;

public abstract class AbstractConnection extends DefaultLinkImpl implements RoutedConnection {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract String getConnectionType();
	
	public ArrayList<Property> getConnectionProperties() {
		Path p = getRouting();
		ArrayList<Property> ar = new ArrayList<Property>(3);
		ar.add(new Property(getConnectionType()+"_start", p.getFirst()));
		ar.add(new Property(getConnectionType()+"_end", p.getLast()));
		ar.add(new Property(getConnectionType()+"connection", p.getExtremities().toString()));
		ar.add(new ResultProperty(getConnectionType()+"_routing (per connection)", p.toString()));
		ar.add(new ResultProperty(getConnectionType()+"_hops (per connection)", p.getNumberOfHops()));
		return ar;
	}
}
