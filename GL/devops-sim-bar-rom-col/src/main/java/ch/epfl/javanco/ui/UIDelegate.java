package ch.epfl.javanco.ui;

import java.util.ArrayList;

import ch.epfl.general_libraries.event.RunnerEventListener;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.SelectedTypeChangedAdapter;
import ch.epfl.javanco.xml.GraphicalDataHandler;


public class UIDelegate {
	private AbstractUI                   defUI        = null;
	//	private AbstractGraphicalUI          defGUI       = null;
	private GraphicalDataHandler        dh              = null;

	public  SelectedTypeChangedAdapter  selectedAdapter = null;
	private ArrayList<AbstractUI>	uiList   = new ArrayList<AbstractUI>();

	private AbstractGraphHandler        assosAgh        = null;

	public UIDelegate(AbstractGraphHandler agh) {
		this.assosAgh = agh;
		selectedAdapter = new SelectedTypeChangedAdapter(assosAgh);
		agh.addSelectedTypeChangedListener(selectedAdapter);
	}

	/*	public AbstractUI addUI(Class<? extends AbstractUI> manClass) {
		AbstractUI man = this.getNewUI(assosAgh, manClass);
		registerNewUI(man);
		return man;
	}*/

	public void setGraphicalDataHandler(GraphicalDataHandler gdh) {
		if (dh == null) {
			dh = gdh;
		} else {
			throw new IllegalStateException("Attempt to add multiple graphical data hanlder");
		}
	}

	public GraphicalDataHandler getGraphicalDataHandler() {
		return dh;
	}

	public void prepareForSave() {
		for (AbstractUI ui : uiList) {
			ui.prepareForSave();
		}
	}

	public AbstractUI getDefaultUI() {
		return defUI;
	}

	public AbstractGraphicalUI getDefaultGraphicalUI() {
		if (defUI instanceof AbstractGraphicalUI) {
			return (AbstractGraphicalUI)defUI;
		}
		return null;
	}

	public AbstractGraphicalUI getDefaultGraphicalUI(AbstractGraphHandler agh, boolean createIfNone) {
		AbstractGraphicalUI ui = getDefaultGraphicalUI();
		if (ui == null) {
			ui = new AbstractGraphicalUI(AbstractGraphicalUI.getDefaultNetworkPainter(), agh, null){};
		}
		return ui;
	}

	public ArrayList<RunnerEventListener> getRunnerEventListeners() {
		ArrayList<RunnerEventListener> runners = new ArrayList<RunnerEventListener>();
		for (AbstractUI ui : uiList) {
			if (ui instanceof AbstractGraphicalUI) {
				runners.add(((AbstractGraphicalUI)ui).getRunnerEventListener());
			}
		}
		return runners;
	}

	public SelectedTypeChangedAdapter getTypeCreationAdapter() {
		return selectedAdapter;
	}

	public void registerNewUI(AbstractUI ui) {
		if (this.defUI == null) {
			defUI = ui;
		}
		uiList.add(ui);
	}

}
