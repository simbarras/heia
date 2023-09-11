package ch.epfl.general_libraries.event;

import java.util.EventObject;

public class CasualEvent extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EventObject parent;	
		
	public CasualEvent(Object source) {
		super(source);
	}
	
	public CasualEvent(EventObject obj) {
		super(obj);
		parent = obj;
	}
		
	public void setParent(EventObject ev) {
		this.parent = ev;
	}
	
	public EventObject getRoot() {
		if (parent != null) {
			if (parent instanceof CasualEvent) {		
				return ((CasualEvent)parent).getRoot();
			}
			return this;
		} else {
			return this;
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof CasualEvent) {
			CasualEvent jv = (CasualEvent)o;
			if (jv.getRoot() == this.getRoot()) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean equalsOrParentEquals(EventObject e) {
		if (this.equals(e)) return true;
		if (e != null) 
			if (e instanceof CasualEvent) {
				return equalsOrParentEquals(((CasualEvent)e).parent);
			}
		if (parent != null)
			if (parent instanceof CasualEvent) {
				return ((CasualEvent)parent).equalsOrParentEquals(e);
			}
		return false;
	}	
	
	public String toString() {
		return super.toString() + (parent != null ? " --> " + parent.toString() : "");
	}	
	
	
}
