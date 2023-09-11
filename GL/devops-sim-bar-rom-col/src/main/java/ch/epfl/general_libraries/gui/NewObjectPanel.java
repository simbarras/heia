package ch.epfl.general_libraries.gui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import ch.epfl.general_libraries.clazzes.ClassRepository;
import ch.epfl.general_libraries.random.MantissaBasedPRNStream;

public class NewObjectPanel<X> implements ObjectReceiver<X> {

	private static ClassRepository lister;
	
	public static void initStatic(String[] prefixes) {
		lister = new ClassRepository(prefixes);		
	}

	X x;
	JDialog dial;
	Notifiable<X> toNotify;

	public NewObjectPanel(Class<? extends X> c) throws Exception {
		this(c, new String[]{"ch.epfl","umontreal.ssj.probdist"}, new DefaultNotifiable<X>());
	}
	
	public NewObjectPanel(Class<? extends X> c, String prefix) throws Exception {
		this(c, new String[]{prefix}, new DefaultNotifiable<X>());
	}
	
	public NewObjectPanel(Class<? extends X> c, Notifiable<X> toNotify) throws Exception {
		this(c, new String[]{"ch.epfl","umontreal.ssj.probdist"}, toNotify);
	}		

	@SuppressWarnings("unchecked")
	public NewObjectPanel(Class<? extends X> c, String[] prefixes, Notifiable<X> toNotify) throws Exception {
		super();
		this.toNotify = toNotify;
		dial = new JDialog();
		dial.setSize(400,400);
		
		Class<?> anonym = c;
		
		ClassRepository localLister;
		if (lister != null) {
			localLister = lister;
		} else {
			localLister = new ClassRepository(prefixes);
			lister = localLister;
		}

		//	if (Modifier.isAbstract( c.getModifiers() )) {
		JTabbedPane tabPane = new JTabbedPane();
		dial.add(tabPane);
		Collection classCol = lister.getClasses(anonym);
		for (Class<?> cl : (Collection<Class>)classCol) {
			Constructor[] a = cl.getConstructors();
			/*	for (int i = 0 ; i < a.length ; i++) {
					cons.add(a[i]);
				}*/
			tabPane.add(cl.getSimpleName(), GuiMethod.createConstructorPanel(a, this, new ArrayList<Class>()));
		}


		/*	} else {
			dial.add(GuiMethod.createConstructorPanel(c.getConstructors(), this));
		}*/
		dial.setVisible(true);

	}
	
	public Object getNotifiable() {
		return toNotify;
	}

	public void setObject(X x) {
		this.x = x;
		dial.setVisible(false);
		if (toNotify != null) {
			toNotify.notify_(x);
		}
	}

	public X getObject() {
		return x;
	}



	public static void main(String[] args) throws Exception {
		new NewObjectPanel<MantissaBasedPRNStream>(MantissaBasedPRNStream.class);
	}
}
