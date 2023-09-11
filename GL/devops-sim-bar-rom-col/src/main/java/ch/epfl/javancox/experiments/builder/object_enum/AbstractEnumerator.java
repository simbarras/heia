package ch.epfl.javancox.experiments.builder.object_enum;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.gui.ProgressBarDialog;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel.ObjectIterator;

public abstract class AbstractEnumerator<X> {
	
	public abstract void beforeIteration();
	public abstract void iterating(X object) throws Exception;
	public abstract void afterIteration();
	public abstract void clearEnumerationResults();
	public abstract void clearCaches();
	public abstract Object getObjectToWaitFor();
	
	protected transient boolean isRunning = false;		
	protected boolean stopCalculations = false;	
	private int running = 0;
	private int started = 0;
	
	public void stopTreeManager() {
		if (this.isRunning) {
			this.stopCalculations = true;
			synchronized (getObjectToWaitFor()) {
				getObjectToWaitFor().notifyAll();
			}
		}
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}	
	
	public void runInNewThread(final Runnable callback,  
							   final ObjectIterator<X> ite,
							   final ProgressBarDialog progressManager,
							   final int thread) {
		if (thread <= 1) {
			Thread execute = new Thread() {
				@Override
				public void run() {
					runInSameThread(callback, ite, progressManager);
				}
			};
			execute.setName("Cockpit tree runner");
			execute.start();		
		} else {
			// For multi thread the AWT Jave CANNOT suffice to organize the tasks as progress bar need update, oct 2015
			Thread execute = new Thread() {
				@Override
				public void run() {			
					runInMultiThreads(callback, ite, progressManager, thread);
				};
			};
			execute.setName("Experiment exec master thread");
			execute.start();
		}
	}

	public void runInSameThread(final Runnable callback, 
								ObjectIterator<X> ite, 
								final ProgressBarDialog progressManager) {
		isRunning = true;
		this.beforeIteration();
		iterate(ite, progressManager);
		if (ite != null)
		ite.cleanUp();
		this.afterIteration();
		stopCalculations = false;
		isRunning = false;
		if (progressManager != null && progressManager.isVisible()) {
			progressManager.setVisible(false);
		}
		if (callback != null) 
			callback.run();		
	}
	
	public void runInMultiThreads(final Runnable callback, 
								  final ObjectIterator<X> ite,
								  final ProgressBarDialog progressManager,
								  int threads) {
		isRunning = true;
		beforeIteration();
		final Object reference = this;	
		running = 0;
		started = 0;
		synchronized(reference) {
			try {
				for (int i = 0 ; i < threads ; i++) {
					Thread t = new Thread() {
						@Override
						public void run() {
							try {
								synchronized (reference) {
									running++;
									started++;
									reference.notifyAll();
								}
								iterate(ite, progressManager);
							}
							finally {
								synchronized (reference) {
									System.out.println(this.getName() + " will die");
									running--;
									reference.notifyAll();
								}						
							}
						}
					};	
					t.setName("Parallel runner " + i);
					t.start();
				}
				while (started < threads) {
					reference.wait();
				}
				while (running > 0) {
					reference.wait();
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (ite != null)
		ite.cleanUp();
		afterIteration();	
		stopCalculations = false;
		isRunning = false;
		if (progressManager.isVisible()) {
			progressManager.setVisible(false);
		}
		if (callback != null) 
			callback.run();	
	}
	
	private void iterate(ObjectIterator<X> ite, ProgressBarDialog progress) {
		boolean localStop = false;
		while (!stopCalculations && !localStop) {
			try {
				X enumeratedObject = null;
				synchronized(this) {
					if (ite.hasNext()) {
						enumeratedObject = ite.next();
					}
				}
				if (enumeratedObject != null) {
					long time = System.nanoTime();
					if (!(enumeratedObject instanceof WrongExperimentException)) {
						this.iterating(enumeratedObject);
						time = System.nanoTime() - time;
						if (time > 1e9) {
							System.gc();
						}
					}
					if (progress != null) {
						progress.incrementProgression();
					}
				} else {
					localStop = true;
				}
			} catch (final Exception e) {
				e.printStackTrace();
				SwingUtilities.invokeLater( new Runnable() {			
					public void run() {
						JOptionPane.showMessageDialog(null,
								"Error during experiment execution :\n" + e.getMessage()
								+ "\nStopping experiments.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				});
			//	stopCalculations = true;
			}
		}	
	}	


}
