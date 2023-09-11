package ch.epfl.javancox.experiments.builder.object_enum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

/**
 * Handles enumerated experiment objects (thus implements ObjectEnumerationManager<Experiment>)
 * This mainly consists in calling the run() method of each object, and placing
 * the results in the db object. 
 * @author Rumley
 *
 */
public class ExperimentExecutionManager<T extends Experiment> extends AbstractEnumerator<T> {
	protected SmartDataPointCollector db = new SmartDataPointCollector();
	protected int i;
	protected long start;
	protected boolean success = true;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm.ss"); 
	private static ArrayList<Class> registeredCachedClasses = new ArrayList<Class>();
	
	public static void registerAsCachedClass(Class<?> c) {
		try {
			c.getMethod("clearCache", new Class[]{});
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Cannot register a class that has no \"clearCache\" method ");
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		}		
		registeredCachedClasses.add(c);
	}

	@Override
	public void clearEnumerationResults() {
		db.clear();
	}
	
	@Override
	public void clearCaches() {
		for (Class<?> c : registeredCachedClasses) {
			try {
				c.getMethod("clearCache", new Class[]{}).invoke(null, new Object[]{});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void beforeIteration() {
		this.start = System.currentTimeMillis();
		this.i = 1;
	}

	@Override
	public void iterating(Experiment object) throws Exception {
		success = true;
		long yourmilliseconds = System.currentTimeMillis();   
		Date resultdate = new Date(yourmilliseconds);
		System.out.print(sdf.format(resultdate));
		synchronized(this) {
		
			System.out.println(": Experiment " + this.i);
			++this.i;
		}
		try {
			object.run(db, null);
		}
		catch (WrongExperimentException e) {
			System.out.println("Warning: wrong experiment: " + e.getMessage());
		}
		catch (Throwable e) {
			success = false;
			if (e instanceof Exception) {
				throw (Exception)e;
			} else {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public void afterIteration() {
		if (success) {
			System.out.println(this.i-1 + " experiments runned in : "
					+ (System.currentTimeMillis() - this.start) + " ms");
			DefaultResultDisplayingGUI.displayDefault(db);
		}
	}

	@Override
	public Object getObjectToWaitFor() {
		return db;
	}
}
