package ch.epfl.javanco.pluggings;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import ch.epfl.general_libraries.clazzes.ClassLister;
import ch.epfl.general_libraries.utils.DynamicInstancier;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.imports.AbstractTopologyImporter;
import ch.epfl.javanco.io.JavancoFile;
import ch.epfl.javanco.ui.swing.FileChooser;

public class ImportGraph extends JavancoTool {

	JDialog dial;
	
	@Override
	public void run(final AbstractGraphHandler agh, final Frame f) {

		final JavancoFile file = FileChooser.promptForOpenFileWithTitle(f, "Choose file to import");

		ClassLister<AbstractTopologyImporter> cl = new ClassLister<AbstractTopologyImporter>("ch.epfl.javanco.imports", AbstractTopologyImporter.class);

		Collection<Class<AbstractTopologyImporter>> claCol = cl.getSortedClasses();

		dial = new JDialog(f);

		final JComboBox jcb = new JComboBox(claCol.toArray(new Object[claCol.size()]));

		dial.setLayout(new FlowLayout());
		dial.add(jcb);

		JButton go = new JButton("GO");
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				AbstractTopologyImporter ati = (AbstractTopologyImporter)DynamicInstancier.getInstance((Class<?>)jcb.getSelectedItem());
				System.out.println(ati);
				try {
					ati.importTopology(agh, file);
					dial.setVisible(false);
				} catch (Exception e) {
					dial.setVisible(false);
					JOptionPane.showMessageDialog(f, "Error happened");
				}

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
