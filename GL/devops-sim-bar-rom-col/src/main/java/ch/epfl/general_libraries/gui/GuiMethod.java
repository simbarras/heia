package ch.epfl.general_libraries.gui;

//import java.awt.BorderLayout;
//import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.epfl.general_libraries.event.CasualEvent;
import ch.epfl.general_libraries.gui.reflected.ForcedParameterDef;
import ch.epfl.general_libraries.gui.reflected.ParameterDef;
import ch.epfl.general_libraries.utils.Pair;


public class GuiMethod<V> {

	public static abstract class Job implements Runnable {
		
		protected CasualEvent event;
		public void setEvent(CasualEvent e) {
			this.event = e;
		}		
		
		protected List<GuiParameter<?>> parameters;

		private void setParameters(List<GuiParameter<?>> parameters) {
			this.parameters = parameters;
		}

	}

	private static class ConstructorJob<X> extends Job {
		Constructor<X> c;
		ObjectReceiver<X> r;
		public ConstructorJob(Constructor<X> c,ObjectReceiver<X> r) {
			this.c = c;
			this.r = r;
		}

		public void run() {
			Object[] args = new Object[parameters.size()];
			for (int i = 0 ; i < parameters.size() ; i++) {
				args[i] = parameters.get(i).getParameter();
			}
			try {
				r.setObject(c.newInstance(args));
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static JPanel createPanel(List<Pair<Method, Job>> pairList, List<Class> toIgnore) {
		MethodOrConstructorDefinition[] mcs = new MethodOrConstructorDefinition[pairList.size()];
		Job[] jobs = new Job[pairList.size()];
		int i = 0;
		for (Pair<Method, Job> p : pairList) {
			mcs[i] = new MethodDefinition(p.getFirst());
			jobs[i] = p.getSecond();
			i++;
		}
		return createPanel(mcs, jobs, toIgnore);
	}

	public static JPanel createPanel(MethodOrConstructorDefinition[] mc, Job[] j, List<Class> toIgnore) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gdc = new GridBagConstraints();
		gdc.gridx = 0;
		gdc.gridy = 0;
		gdc.gridwidth = 1;
		for (int i = 0 ; i < mc.length ; i++) {
			GuiMethod.do_(mc[i], panel, gdc, j[i], toIgnore);
			//	GuiMethod.do_(new MethodDefinition(p.getFirst()), panel, gdc, p.getSecond());
			gdc.gridy++;
		}
		return panel;
	}

	@SuppressWarnings("unchecked")
	public static JPanel createConstructorPanel(Constructor[] cons, ObjectReceiver r, List<Class> toIgnore ) {
		MethodOrConstructorDefinition[] m = new MethodOrConstructorDefinition[cons.length];
		Job[] jobs = new Job[cons.length];

		for (int i = 0 ; i < cons.length ; i++) {
			m[i] = new ConstructorDefinition(cons[i]);
			jobs[i] = new ConstructorJob(cons[i], r);
		}
		return createPanel(m, jobs, toIgnore);
	}

	public static interface MethodOrConstructorDefinition {
		public Class[] getParameterTypes();
		public Annotation[][] getParameterAnnotations();
		public String getName();
		public String getType();
	}

	public static class MethodDefinition implements MethodOrConstructorDefinition {
		Method m;
		public MethodDefinition(Method m) {
			this.m = m;
		}
		public Class[] getParameterTypes() {
			return m.getParameterTypes();
		}
		public Annotation[][] getParameterAnnotations() {
			return m.getParameterAnnotations();
		}
		public String getName() {
			return m.getName();
		}
		public String getType() { return "Method"; }
	}

	public static class ConstructorDefinition implements MethodOrConstructorDefinition {
		Constructor<?> c;
		public ConstructorDefinition(Constructor<?> c) {
			this.c =c;
		}
		public Class[] getParameterTypes() {
			return c.getParameterTypes();
		}
		public Annotation[][] getParameterAnnotations() {
			return c.getParameterAnnotations();
		}
		public String getName() {
			return c.getDeclaringClass().getSimpleName();
		}
		public String getType() {
			return "Constructor";
		}
	}	

	@SuppressWarnings("unchecked")
	public static void do_(MethodOrConstructorDefinition m, JPanel globalPanel, GridBagConstraints globalGdc, final Job job, List<Class> toIgnore) {
		JButton jButton = new JButton("Generate");
		int pos = 0;
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gdc = new GridBagConstraints();

		final List<GuiParameter<?>> parameters = new ArrayList<GuiParameter<?>>();
		Class<?>[] paramType = m.getParameterTypes();
		Annotation[][] paramAnot = m.getParameterAnnotations();

		gdc.gridx = 0;
		gdc.gridy = pos;
		gdc.weighty = 1;
		gdc.gridwidth = 2;
		gdc.anchor = GridBagConstraints.SOUTH;
		panel.add(new JLabel("<html><u>"+  m.getType()+ " : " + m.getName() + "</u></html>"), gdc);

		gdc.weighty = 0;
		gdc.gridwidth = 1;
		gdc.anchor = GridBagConstraints.CENTER;
		pos++;

		for(int i = 0; i < paramType.length; i++) {

			Class<?> t = paramType[i];
			boolean ignore = false;
			for (Class ignoreMayBe : toIgnore) {
				if (ignoreMayBe.isAssignableFrom(t)) {
					ignore = true;
					break;
				}
			}
			if (ignore) 
				continue;

			String name = null;

			String[] forcedParamValues = null;

			for(Annotation a: paramAnot[i]) {
				if (ParameterDef.class.isAssignableFrom(a.annotationType())) {
					name = ((ParameterDef)a).name();
				}
				if (ForcedParameterDef.class.isAssignableFrom(a.annotationType())) {
					forcedParamValues = ((ForcedParameterDef)a).possibleValues();
				}
			}
			if (name == null) {
				name = t.getSimpleName();
			}

			JLabel label = new JLabel(name);

			gdc.gridx = 0;
			gdc.gridy = pos;

			panel.add(label, gdc);

			GuiParameter<?> parameter = GuiParameter.createParameter(t, forcedParamValues);

			gdc.gridx = 1;
			gdc.gridy = pos;
			gdc.fill = GridBagConstraints.BOTH;
			gdc.weightx = 1;

			panel.add(parameter, gdc);

			gdc.weightx = 0;
			gdc.fill = GridBagConstraints.NONE;
			pos++;

			parameters.add(parameter);
		}
		gdc.gridx = 0;
		gdc.gridwidth = 2;
		gdc.anchor = GridBagConstraints.EAST;
		gdc.gridy = pos;


		panel.add(jButton, gdc);
		gdc.gridy++;
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getParam(job, parameters);
			}
		});


		globalPanel.add(panel, globalGdc);




		//	return parameters;
	}

	public static void getParam(final Job job, List<GuiParameter<?>> parameters) {
		for (GuiParameter<?> p : parameters) {
			if (!p.isValidParam()) {
				System.out.println("parameter illegal");
				return;
			}
		}
		job.setParameters(parameters);
		Thread t = new Thread() {
			public void run() {
				job.run();
			}
		};
		t.start();

	}


}
