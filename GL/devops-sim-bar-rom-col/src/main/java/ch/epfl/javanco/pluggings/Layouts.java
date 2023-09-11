package ch.epfl.javanco.pluggings;

import java.awt.Frame;
import java.util.Collection;

import javancox.layout.AbstractTopologyLayout;
import ch.epfl.general_libraries.clazzes.ClassLister;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;


public class Layouts extends JavancoToolWithObjectChoose {

	private static Collection clList;
	
	static boolean init = false;		
	
	@Override
	@SuppressWarnings("unchecked")	
	public void run(final AbstractGraphHandler agh, final Frame f) {
		if (!init) {
			ClassLister<AbstractTopologyLayout> cl = 
				new ClassLister<AbstractTopologyLayout>(
						Javanco.getProperty(Javanco.JAVANCO_DEFAULT_CLASSPATH_PREFIXES_PROPERTY).split(";"), AbstractTopologyLayout.class);
			clList = cl.getSortedClasses();	
			init = true;		
		}
		runWithObjectChoose(agh, f, clList, new ToDoWithObject() {
			public void do__(Object o) {
				AbstractTopologyLayout lay = (AbstractTopologyLayout)o;
				lay.assignNodesPosition(agh);
				agh.fireAllElementsModificationEvent(new ch.epfl.general_libraries.event.CasualEvent(this));
			}
		});
	}

}
