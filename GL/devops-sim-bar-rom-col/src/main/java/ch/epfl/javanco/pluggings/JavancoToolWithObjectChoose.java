package ch.epfl.javanco.pluggings;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ch.epfl.general_libraries.utils.DynamicInstancier;
import ch.epfl.javanco.base.AbstractGraphHandler;

public abstract class JavancoToolWithObjectChoose extends JavancoTool {

	public abstract class ToDoWithObject {
		public abstract void do__(Object obj);
	}
	
	JDialog dial;	
	
	public void runWithObjectChoose(final AbstractGraphHandler agh, Frame f, Collection<Class<?>> clList,
			final ToDoWithObject todo) {

		dial = new JDialog(f);

		final JComboBox jcb = new JComboBox(clList.toArray(new Object[clList.size()]));

		dial.setLayout(new FlowLayout());
		dial.add(jcb);

		JButton go = new JButton("GO");
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				final Object obj = DynamicInstancier.getInstance((Class)jcb.getSelectedItem());
				new Thread() {
					public void run() {
						todo.do__(obj);	
					}
				}.start();
							
			}
		});
		dial.add(go);
		dial.pack();
		dial.setVisible(true);
	}


	@Override
	public void internalFrameClosing() {
		dial.setVisible(false);
		
	}

}
