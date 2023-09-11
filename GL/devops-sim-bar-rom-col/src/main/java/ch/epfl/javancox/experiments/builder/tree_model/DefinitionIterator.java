package ch.epfl.javancox.experiments.builder.tree_model;

import java.util.Iterator;


public abstract class DefinitionIterator implements Iterator<AbstractDefinition> {
	abstract void reset();
	abstract AbstractDefinition current();
	abstract boolean hasFutureNext();
	
	public Iterable<AbstractDefinition> toIterable() {
		return new Iterable<AbstractDefinition>() {

			@Override
			public Iterator<AbstractDefinition> iterator() {
				return DefinitionIterator.this;
			}
			
		};
	}
}