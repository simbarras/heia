package ch.epfl.javancox.experiments.builder.swing_gui;

import java.awt.event.ActionEvent;
import java.io.Serializable;

public interface InstanceDynamicTreeListener extends Serializable {
	public void treeReadyAction(ActionEvent e);
	public void treeNotReadyAction(ActionEvent e);
}
