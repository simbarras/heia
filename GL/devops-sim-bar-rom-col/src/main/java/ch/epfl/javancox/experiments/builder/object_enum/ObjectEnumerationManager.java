package ch.epfl.javancox.experiments.builder.object_enum;

/**
 * Common definition of a class that perform operation on
 * enumerated objects, in between enumerations, etc.
 * @author Rumley
 *
 * @param <X>
 */
public interface ObjectEnumerationManager<X> {
	public void beforeIteration();
	public void iterating(X object) throws Exception;
	public void afterIteration();
	public void clearEnumerationResults();
	public void clearCaches();
	public Object getObjectToWaitFor();
}
