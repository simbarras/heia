package ch.epfl.javanco.pluggings;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javancox.topogen.GeneratorStub;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ch.epfl.general_libraries.clazzes.ClassLister;
import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.gui.GuiMethod;
import ch.epfl.general_libraries.gui.GuiParameter;
import ch.epfl.general_libraries.gui.GuiMethod.Job;
import ch.epfl.general_libraries.gui.reflected.MethodDef;
import ch.epfl.general_libraries.utils.Pair;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.ui.AbstractGraphicalUI;
import ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI;

public class Generators extends JavancoTool {
	
	private JTabbedPane jTabbedPane = null;
	private JDialog diag;
	private AbstractGraphHandler agh__;
	private JavancoDefaultGUI frame;
	
	@Override
	public void run(final AbstractGraphHandler agh, final Frame f) {
		this.popupPanel();
		this.initialize(f);
		this.agh__ = agh;
		frame = (ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI)f;		
		diag.setVisible(true);	
	}
	
	private void initialize(Frame f) {
		diag = new JDialog(f);
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.BOTH;
		gridBagConstraints11.weighty = 1.0;
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 0;
		gridBagConstraints11.weightx = 1.0;
		diag.setLayout(new GridBagLayout());
		diag.add(getJTabbedPane(), gridBagConstraints11);
		diag.setSize(300,600);
	}	

	private void popupPanel() {
		ClassLister<GeneratorStub> lister = new ClassLister<GeneratorStub>(
				Javanco.getProperty(Javanco.JAVANCO_DEFAULT_CLASSPATH_PREFIXES_PROPERTY).split(";"), GeneratorStub.class);		
		for (Class<GeneratorStub> c : lister.getSortedClasses()) {
			getJTabbedPane().addTab(c.getSimpleName(), new JScrollPane(createPanel(c)));
		}
	}
	
	private JPanel createPanel(final Class<? extends GeneratorStub> c) {
		List<Pair<Method, Job>> pairList = new ArrayList<Pair<Method, Job>>();
		for (final Method m: c.getMethods()) {
		//	Annotation[] annot = m.getAnnotations();
			if (m.getAnnotation(MethodDef.class) != null) {
				GeneratorJob job = new GeneratorJob(m, c);
				Pair<Method, Job> pair = new Pair<Method, Job>(m, job);
				pairList.add(pair);
			}
		}
		return GuiMethod.createPanel(pairList, Arrays.asList(new Class[]{AbstractGraphHandler.class}));
	}		

	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
		}
		return jTabbedPane;
	}	
	
	public class GeneratorJob extends Job {
		Method m;
		Class<? extends GeneratorStub> c;
		
		public GeneratorJob(Method m, Class<? extends GeneratorStub> c) {
			this.m = m;
			this.c = c;
		}
		
		protected Object[] setAghAndParameters(AbstractGraphHandler agh) {
			Iterator<GuiParameter<?>> it = parameters.iterator();								
			Object[] args = new Object[parameters.size()+1];								
			args[0] = agh;								
			for (int i = 1 ; i < args.length ; i++) {
				args[i] = it.next().getParameter();
			}
			return args;
		}		
		
		public void run() {	
			agh__.clearLayers(true);			
			CasualEvent ev = new CasualEvent(this);							
			
			Object[] args = setAghAndParameters(agh__);

			agh__.setModificationEventEnabled(false, ev);
			try {
				GeneratorStub tg = c.newInstance();
				m.invoke(tg, args);
			} catch (Exception e1) {
				System.err.println("Can't invoke method " + m.getName());
				agh__.getUIDelegate().getDefaultGraphicalUI(agh__, true).displayWarning(e1.getCause().getMessage());
				e1.printStackTrace();
			}
			agh__.setModificationEventEnabled(true, ev);
			agh__.fireAllElementsModificationEvent(ev);
			AbstractGraphicalUI ui = agh__.getUIDelegate().getDefaultGraphicalUI(agh__, true);
			ui.setBestFitAndKeepNodeRatio(frame.getActuallyActiveInternalFrame().getSize());			
		}	
	}

	@Override
	public void internalFrameClosing() {
		diag.setVisible(false);
	}		
	
}
