package ch.epfl.general_libraries.event;

import java.util.ArrayList;
import java.util.List;

public class RunnerEventHandler {

	private List<RunnerEventListener> runnerEventListeners = null;

	public void addRunnerEventListener(RunnerEventListener listener) {
		if (runnerEventListeners == null) {
			runnerEventListeners = new ArrayList<RunnerEventListener>();
		}
		runnerEventListeners.add(listener);
	}

	public void removeRunnerEventListener(RunnerEventListener listener) {
		if (runnerEventListeners == null) {
			runnerEventListeners.remove(listener);
		}
	}

	public RunnerEventListener[] getRunnerEventListeners() {
		return (RunnerEventListener[])runnerEventListeners.toArray();
	}

	public void fireRunnerStateChangedEvent(Runnable t) {
		if (runnerEventListeners != null) {
			RunnerEvent event = new RunnerEvent(t);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerStateChanged(event);
			}
		}
	}

	public void fireRunnerSpeedChangedEvent(Runnable t) {
		if (runnerEventListeners != null) {
			RunnerEvent event = new RunnerEvent(t);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerSpeedChanged(event);
			}
		}
	}

	public void fireProgressionEvent(int percent) {
		if (runnerEventListeners != null) {
			//	RunnerEvent event = new RunnerEvent(ratio);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerProgressed(percent);
			}
		}
	}

	/*	public void fireEndedRunnerEvent(Thread t) {
		if (runnerEventListeners != null) {
			RunnerEvent event = new RunnerEvent(RunnerEvent.SCRIPT_ENDED, t);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerEnded(event);
			}
		}
	}

	public void fireCrashedRunnerEvent(Thread t) {
		if (runnerEventListeners != null) {
			RunnerEvent event = new RunnerEvent(RunnerEvent.SCRIPT_CRASHED, t);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerCrashed(event);
			}
		}
	}

	public void fireCreatedRunnerEvent(Thread t) {
		if (runnerEventListeners != null) {
			RunnerEvent event = new RunnerEvent(RunnerEvent.SCRIPT_CREATED, t);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerCreated(event);
			}
		}
	}

	public void fireDeletedRunnerEvent(Thread t) {
		if (runnerEventListeners != null) {
			RunnerEvent event = new RunnerEvent(RunnerEvent.SCRIPT_DELETED, t);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerCreated(event);
			}
		}
	}

	public void fireStateChangedRunnerEvent(Thread t) {
		if (runnerEventListeners != null) {
			RunnerEvent event = new RunnerEvent(RunnerEvent.SCRIPT_DELETED, t);
			for (RunnerEventListener listener : runnerEventListeners) {
				listener.runnerStateChanged(event);
			}
		}
	}	*/

}