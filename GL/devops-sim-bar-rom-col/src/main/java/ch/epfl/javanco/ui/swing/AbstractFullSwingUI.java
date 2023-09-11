package ch.epfl.javanco.ui.swing;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.graphics.NetworkPainter;
import ch.epfl.javanco.network.AbstractElementContainer;
import ch.epfl.javanco.ui.FrameBasedInterface;
import ch.epfl.javanco.ui.GlobalInterface;

public abstract class AbstractFullSwingUI extends AbstractSwingUI {

	protected   FrameBasedInterface               associatedGlobalInterface;
	protected GraphicalNetworkEditor editor;

	public AbstractFullSwingUI(NetworkPainter painter,
			AbstractGraphHandler agh,
			GlobalInterface superInterface) {
		super(painter, agh, superInterface);
		if (superInterface instanceof FrameBasedInterface) {
			associatedGlobalInterface = (FrameBasedInterface)superInterface;
		}
		this.editor = new GraphicalNetworkEditor(this);
	}

	@Override
	public void displayPropertiesImpl(AbstractElementContainer object) {
		XMLDefinitionEditor editor = new XMLDefinitionEditor(associatedGlobalInterface.getMainFrame(),
				true,
				getAssociatedAbstractGraphHandler().isEditable());
		if (object != null) {
			editor.editElement(object);
		}
	}

	public void setAssociatedGlobalInterface(FrameBasedInterface face) {
		associatedGlobalInterface = face;
	}		

	@Override
	public void init() {
		super.setGraphicalNetworkDisplayer(editor.getDisplayer());
		super.init();
		handler.addSelectedTypeChangedListener(this);
		handler.addUIRefreshEventListener(this);
		handler.addEditedLayerListener(this);
	}

	@Override
	public void displayWarning(String s) {
		PopupDisplayer.popupErrorMessage(s, associatedGlobalInterface.getMainFrame());
	}

	@Override
	public void displayWarning(String s, Throwable t) {
		PopupDisplayer.popupErrorMessage(s,t,associatedGlobalInterface.getMainFrame());
	}

}
