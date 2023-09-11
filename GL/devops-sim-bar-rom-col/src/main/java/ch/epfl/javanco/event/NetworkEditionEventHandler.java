package ch.epfl.javanco.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.network.Element;
import ch.epfl.javanco.network.LayerContainer;
import ch.epfl.javanco.network.LinkContainer;
import ch.epfl.javanco.network.NodeContainer;


public abstract class NetworkEditionEventHandler {

	private static Logger logger = new Logger(NetworkEditionEventHandler.class);

	private java.util.HashSet<SelectedTypeChangedListener> selectedTypeChangedListeners = null;

	@SuppressWarnings("unused")
	private long lastTime = System.nanoTime();

	public void addSelectedTypeChangedListener(SelectedTypeChangedListener listener) {
		if (selectedTypeChangedListeners == null) {
			selectedTypeChangedListeners = new java.util.HashSet<SelectedTypeChangedListener>();
		}
		selectedTypeChangedListeners.add(listener);
	}

	public void removeSelectedTypeChangedListener(SelectedTypeChangedListener listener) {
		if (selectedTypeChangedListeners != null) {
			selectedTypeChangedListeners.remove(listener);
		}
	}

	public SelectedTypeChangedListener[] getSelectedTypeChangedListeners() {
		return selectedTypeChangedListeners.toArray(new SelectedTypeChangedListener[selectedTypeChangedListeners.size()]);
	}

	public void fireSelectedTypeChangedEvent(Class<? extends Element> c, boolean isNode, boolean isLink, boolean isLayer) {
		SelectedTypeChangedEvent event = new SelectedTypeChangedEvent(this, c, isNode, isLink, isLayer);
		if (selectedTypeChangedListeners != null) {
			for (SelectedTypeChangedListener listener : selectedTypeChangedListeners) {
				listener.selectedTypeChanged(event);
			}
		}
	}

	private java.util.HashSet<EditedLayerListener> editedLayerListeners = null;

	public void addEditedLayerListener(EditedLayerListener listener) {
		if (editedLayerListeners == null) {
			editedLayerListeners = new java.util.HashSet<EditedLayerListener>();
		}
		editedLayerListeners.add(listener);
	}

	public void removeElementCreationListener(EditedLayerListener listener) {
		if (editedLayerListeners != null) {
			editedLayerListeners.remove(listener);
		}
	}

	public EditedLayerListener[] getEditedLayerListeners() {
		return editedLayerListeners.toArray(new EditedLayerListener[editedLayerListeners.size()]);
	}

	public void fireLayerEditedChangedEvent(LayerContainer layerContainer) {
		if (editedLayerListeners != null) {
			EditedLayerEvent event = new EditedLayerEvent(layerContainer);
			for (EditedLayerListener listener : editedLayerListeners) {
				listener.editedLayerChanged(event);
			}
		}
	}

	/*                        DISPLAY CHANGE LISTENER                   */
	/********************************************************************/
	private java.util.HashSet<UIEventListener> uiEventListeners = null;

	public void addUIRefreshEventListener(UIEventListener listener) {
		if (uiEventListeners == null) {
			uiEventListeners = new java.util.HashSet<UIEventListener>();
		}
		uiEventListeners.add(listener);
	}

	public void removeUIRefreshEventListener(UIEventListener listener) {
		if (uiEventListeners != null) {
			uiEventListeners.remove(listener);
		}
	}

	public UIEventListener[] getUIEventListeners() {
		return uiEventListeners.toArray(new UIEventListener[uiEventListeners.size()]);
	}

/*	public void fireRefreshDisplayUIEvent() {
		if (this.eventEnabled) {
			if (uiEventListeners != null) {
				for (UIEventListener listener : uiEventListeners) {
					listener.refreshAndRepaintAllDisplay();
				}
			}
		}
	}*/

	public void fireDisplayWarningEvent(String s) {
		if (uiEventListeners != null) {
			for (UIEventListener listener : uiEventListeners) {
				listener.displayWarning(s);
			}
		}
	}

	public void fireDisplayWarningEvent(String s, Throwable t) {
		if (uiEventListeners != null) {
			for (UIEventListener listener : uiEventListeners) {
				listener.displayWarning(s, t);
			}
		}
	}

	/*                       MODIFICATION LISTENER                       */
	/********************************************************************/

	private boolean whileBegin = false;
	private boolean eventEnabled = true;

	public boolean isEventEnabled() {
		return eventEnabled;
	}
	
	public void setModificationEventEnabledWithoutCallingBigChanges(boolean en) {
		eventEnabled = en;	
	} 

	public synchronized void setModificationEventEnabled(boolean en, CasualEvent ev) {
		if (en) {
			if (whileBegin == true) {
				for (ElementListener listener : graphicalElementListeners) {
					try {
						listener.endBigChanges(ev);
					}
					catch (Exception e) {
						logger.error("Catched exception for security reason", e);
					}
					eventEnabled = true;
				}
				whileBegin = false;
			}
		} else {
			if (whileBegin == false) {
				whileBegin = true;
				for (ElementListener listener : graphicalElementListeners) {
					listener.beginBigChanges(ev);
					eventEnabled = false;
				}
			}
		}
	}

	private List<ElementListener> graphicalElementListeners = new ArrayList<ElementListener>();
	private List<ElementListener> structuralElementListeners = new ArrayList<ElementListener>();	
	

	public void addGraphicalElementListener(ElementListener listener) {
		if (graphicalElementListeners.contains(listener) == false) {
			graphicalElementListeners.add(listener);
		}
	}
	
	public void addStructuralElementListener(ElementListener listener) {
		if (structuralElementListeners.contains(listener) == false) {
			structuralElementListeners.add(listener);
		}
	}	

	public void removeGraphicalElementListener(ElementListener listener) {
		graphicalElementListeners.remove(listener);
	}
	
	public void removeStructuralElementListener(ElementListener listener) {
		structuralElementListeners.remove(listener);
	}	

	public void fireElementEvent(ElementEvent ev) {


		LayerContainer layC = null;
		LinkContainer linkC = null;
		NodeContainer nodeC = null;
		if (ev.concernsLayer()) {
			layC = ev.getLayerContainer();
		} else if (ev.concernsLink()) {
			linkC = ev.getLinkContainer();
		} else if (ev.concernsNode()) {
			nodeC = ev.getNodeContainer();
		} else {
			throw new IllegalArgumentException("Not implemented with " + ev.getContainer().getClass().getSimpleName());
		}

		if (ev.isCreation()) {
			if (ev.concernsLayer()) {
				for (ElementListener listener : structuralElementListeners) {
					listener.layerCreated(layC, ev);
				}
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.layerCreated(layC, ev);
					}
				}
			} else if (ev.concernsLink()) {
				for (ElementListener listener : structuralElementListeners) {
					listener.linkCreated(linkC, ev);
				}
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.linkCreated(linkC, ev);
					}
				}								
			} else if (ev.concernsNode()) {
				for (ElementListener listener : structuralElementListeners) {
					listener.nodeCreated(nodeC, ev);
				}
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.nodeCreated(nodeC, ev);
					}
				}			
			} else {
				throw new IllegalArgumentException("Not implemented with " + ev.getContainer().getClass().getSimpleName());
			}
		} else if (ev.isSuppression()) {
			if (ev.concernsLayer()) {
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.layerSuppressed(layC, ev);
					}
				}
				for (ElementListener listener : structuralElementListeners) {
					listener.layerSuppressed(layC, ev);
				}				
			} else if (ev.concernsLink()) {
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.linkSuppressed(linkC, ev);
					}
				}
				for (ElementListener listener : structuralElementListeners) {
					listener.linkSuppressed(linkC, ev);
				}				
			} else if (ev.concernsNode()) {
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.nodeSuppressed(nodeC, ev);
					}
				}
				for (ElementListener listener : structuralElementListeners) {
					listener.nodeSuppressed(nodeC, ev);
				}				
			} else {
				throw new IllegalArgumentException("Not implemented with " + ev.getContainer().getClass().getSimpleName());
			}
		} else if (ev.isModification()) {
			if (ev.concernsAll()) {
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.allElementsModified(ElementEvent.getAllElementEvent());
					}
				}
				for (ElementListener listener : structuralElementListeners) {
					listener.allElementsModified(ElementEvent.getAllElementEvent());
				}				
			} else {
				for (ElementListener listener : structuralElementListeners) {
					listener.elementModified(ev);
				}
				if (eventEnabled) {
					for (ElementListener listener : graphicalElementListeners) {
						listener.elementModified(ev);
					}
				}				
			}
		}

	}
	
	private EventObject GRAPH_LOADED = new EventObject(this);

	public void fireGraphLoadedEvent() {
		for (ElementListener listener : graphicalElementListeners) {
			listener.graphLoaded(GRAPH_LOADED);
		}
		for (ElementListener listener : structuralElementListeners) {
			listener.graphLoaded(GRAPH_LOADED);
		}			
	}
	
	public void fireAllElementsModificationEvent(CasualEvent ev) {

		for (ElementListener listener : graphicalElementListeners) {
			listener.allElementsModified(ev);
		}
		for (ElementListener listener : structuralElementListeners) {
			listener.allElementsModified(ev);
		}		
	}

}

