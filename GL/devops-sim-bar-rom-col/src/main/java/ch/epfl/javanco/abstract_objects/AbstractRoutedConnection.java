package ch.epfl.javanco.abstract_objects;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.traffic.ConnectionSupport;
import ch.epfl.general_libraries.traffic.RoutedConnection;
import ch.epfl.general_libraries.traffic.RoutedConnectionSegment;
import ch.epfl.javanco.network.LinkContainer;

public abstract class AbstractRoutedConnection extends AbstractConnection implements RoutedConnection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Path p;
	private List<? extends RoutedConnectionSegment> segmentList;

	public AbstractRoutedConnection(Path routing) {
		p = routing;
	}

	public Path getRouting(){
		return p;
	}
	
	@Override
	public int getStartNodeIndex() {
		return p.getFirst();
	}
	@Override
	public int getEndNodeIndex() {
		return p.getLast();
	}	
	
	public void setRouting(Path p) {
		if (this.p != null) {
			throw new IllegalStateException("Trying to replace the previous routing");
		}
		this.p = p;
		check();
	}
	
	public void reroute(Path p) {
		this.p = p;
		check();
	}
	
	public void check() {
		LinkContainer lc = getLinkContainer();
		if (lc != null) {
			if (p.getFirst() != lc.getStartNodeIndex()) {
				throw new IllegalStateException();
			}
			if (p.getLast() != lc.getEndNodeIndex()) {
				throw new IllegalStateException();
			}
		}
	}

	public List<? extends RoutedConnectionSegment> getConnectionSegments() {
		return segmentList;
	}
	
	public void setConnectionSegments(List<? extends RoutedConnectionSegment> list) {
		this.segmentList = list;
		for (RoutedConnectionSegment r : list) {
			r.setParentConnection(this);
		}
	}

	public List<? extends ConnectionSupport> getSupportingLinks() {
		List<ConnectionSupport> list = new ArrayList<ConnectionSupport>(segmentList.size());
		for (RoutedConnectionSegment segment : segmentList) {
			list.add(segment.getSupportingLink());
		}
		return list;
	}
	
	@Override
	public String toString() {
		if (p != null) {
			return "Connection routed on " + p;
		} else {
			return "Unrouted connection:" + getLinkContainer().getStartNodeIndex() + "-" + getLinkContainer().getEndNodeIndex();
		}
	}	

}
