package ch.epfl.javancox.topology_analysis2;

import javancox.topogen.AbstractTopologyGenerator;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI;

public class TopologyAnalysis2 implements Experiment {
	
	private AbstractTopologyGenerator atg;
	private AbstractGraphAnalyser[] lyz;
	private boolean showInJavanco = false;
	
	public TopologyAnalysis2(AbstractTopologyGenerator atg, AbstractGraphAnalyser lyz) {
		this.atg = atg;
		this.lyz = new AbstractGraphAnalyser[]{lyz};
	}
	
	public TopologyAnalysis2(boolean showInJavanco, AbstractTopologyGenerator atg, AbstractGraphAnalyser lyz) {
		this.atg = atg;
		this.lyz = new AbstractGraphAnalyser[]{lyz};
		this.showInJavanco = showInJavanco;
	}
	
	public TopologyAnalysis2(AbstractTopologyGenerator atg, AbstractGraphAnalyser ... lis ) {
		this.atg = atg;
		this.lyz = lis;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		AbstractGraphHandler agh = atg.generate();
		if (showInJavanco) {
			JavancoDefaultGUI gui = JavancoDefaultGUI.getAndShowDefaultGUI();
			gui.getGraphHandlerFactory().registerAgh(agh);
		}
		Execution e = new Execution();
		DataPoint dp = new DataPoint(atg.getAllParameters());
		int size = dp.getProperties().size();
		for (AbstractGraphAnalyser l : lyz) {
			l.analyse(agh, man, dp, e);
		}
		if (dp.getProperties().size() > size) {
			e.addDataPoint(dp);
		}
		man.addExecution(e);
		
	}

}
