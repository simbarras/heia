package ch.epfl.javancox.experiments.builder.tree_model;

import java.io.Serializable;

public abstract class AbstractDefinition implements Serializable {

	private static final long serialVersionUID = 1L;
	protected String def;
	abstract void localToString(String prefix, StringBuilder sb);
}