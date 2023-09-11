package ch.epfl.general_libraries.gui;

import java.lang.Object;
import ch.epfl.general_libraries.event.RunnerEventHandler;

public abstract class Runner extends RunnerEventHandler implements Runnable {

	private class LocalMonitor extends Object {
		String message = "";

		void setMessage(String s) {
			message = s;
		}

		boolean equals(String s) {
			return message.equals(s);
		}
	}

	protected java.io.PrintStream out = null;
	private Object __pauseMonitor = new Object();
	private LocalMonitor __timerMonitor = this.new LocalMonitor();
	private RunnerEventHandler eventHandler = null;

	private RunningSpeed speed = null;
	private RunningState state = null;

	public abstract void runnerStep();
	public abstract String getKey();
	public abstract String getName();

	public Runner() {
		eventHandler = this;
	}

	public Runner(RunnerEventHandler hl) {
		eventHandler = hl;
	}

	protected void init() {
		setState(RunningState.CREATED);
		setSpeed(RunningSpeed.NONE);
	}


	public void run() {
		try {
			try {
				mainLoop();
			}
			catch (InterruptedException ee) {
				setState(RunningState.TERMINATED);
				setSpeed(RunningSpeed.NONE);
			}
		}
		catch (Exception e) {
			dumpStackTrace(e);
			setState(RunningState.CRASHED);
			setSpeed(RunningSpeed.NONE);
		}
	}

	private void mainLoop() throws InterruptedException {

		setState(RunningState.RUNNING);
		setSpeed(RunningSpeed.NORMAL);
		while(true) {
			synchronized (__timerMonitor) {
				__timerMonitor.wait(speed.getDuration());
				this.runnerStep();
			}
			if (__timerMonitor.equals("pause")) {
				__timerMonitor.setMessage("nothing");
				synchronized (__pauseMonitor) {
					__pauseMonitor.wait();
				}
			}
			if (__timerMonitor.equals("stop")) {
				__timerMonitor.setMessage("nothing");
				break;
			}
		}
		setState(RunningState.TERMINATED);
		setSpeed(RunningSpeed.NONE);
	}

	private void dumpStackTrace(Exception e) {
		if (out != null) {
			e.printStackTrace(out);
		} else {
			e.printStackTrace();
		}
	}


	protected void setState(RunningState newState) {
		if (!(newState.equals(state))) {
			state = newState;
			eventHandler.fireRunnerStateChangedEvent(this);
		}
	}

	protected void setSpeed(RunningSpeed newSpeed) {
		if (!(newSpeed.equals(speed))) {
			speed = newSpeed;
			eventHandler.fireRunnerSpeedChangedEvent(this);
		}
	}

	public void setSpeedCommand(int s) {
		setSpeed(RunningSpeed.get(s));
	}

	public void deleteCommand() {
		setState(RunningState.DELETED);
		eventHandler.fireRunnerStateChangedEvent(this);
	}

	public RunningSpeed getRunningSpeed() {
		return speed;
	}

	public RunningState getRunningState() {
		return state;
	}

	public void playCommand() {
		switch(state) {
		case CREATED :
		case STOPPED :
		case CRASHED :
		case TERMINATED :
			this.playInternal();
			break;
		case PAUSED :
			this.resumeInternal();
			break;
		case RUNNING :
		default:
		}
	}

	public void stopCommand() {
		switch(state) {
		case RUNNING :
		case PAUSED :
			this.stopInternal();
			break;
		case CREATED :
		case STOPPED :
		case CRASHED :
		case TERMINATED :
		default:
		}
	}

	public void pauseCommand() {
		switch(state) {
		case RUNNING :
			this.pauseInternal();
			break;
		case CREATED :
		case STOPPED :
		case CRASHED :
		case TERMINATED :
		case PAUSED :
		default:
		}
	}

	public void stepCommand() {
		switch(state) {
		case RUNNING :
		case CREATED :
		case PAUSED :
			this.stepInternal();
			break;
		case STOPPED :
		case CRASHED :
		case TERMINATED :
		default:
		}
	}

	private void playInternal() {
		try {
			new Thread(this).start();
		}
		catch (IllegalStateException e) {
			if (out != null) {
				out.append("A terminated script cannot be restarted");
			}
		}
		catch (Exception e) {
			dumpStackTrace(e);
			setState(RunningState.CRASHED);
		}
	}

	private void stepInternal() {
		__timerMonitor.setMessage("pause");
		synchronized (__timerMonitor) {
			__timerMonitor.notify();
		}
		__timerMonitor.setMessage("pause");
		synchronized (__pauseMonitor) {
			__pauseMonitor.notify();
		}
		setState(RunningState.PAUSED);
	}

	private void stopInternal() {
		__timerMonitor.setMessage("stop");
		synchronized (__pauseMonitor) {
			__pauseMonitor.notify();
		}
		synchronized (__timerMonitor) {
			__timerMonitor.notify();
		}
		setState(RunningState.STOPPED);
		setSpeed(RunningSpeed.NONE);
	}

	private void pauseInternal() {
		__timerMonitor.setMessage("pause");
		synchronized (__timerMonitor) {
			__timerMonitor.notify();
		}
		setState(RunningState.PAUSED);
	}

	private void resumeInternal() {
		__timerMonitor.setMessage("resume");
		synchronized (__pauseMonitor) {
			__pauseMonitor.notify();
		}
		setState(RunningState.RUNNING);
	}

	public enum RunningState {
		CREATED,
		RUNNING,
		PAUSED,
		STOPPED,
		CRASHED,
		TERMINATED,
		DELETED;
	}

	public enum RunningSpeed {
		ULTRA_SLOW (5000),
		VERY_SLOW (2000),
		SLOW (1000),
		NORMAL (500),
		FAST (100),
		VERY_FAST (20),
		REAL_TIME (1),
		NONE (-1);

		private int pauseDuration;

		RunningSpeed(int duration) {
			pauseDuration = duration;
		}

		public int getDuration() {
			return pauseDuration;
		}

		public int getRank() {
			return ordinal() - 1;
		}

		public static RunningSpeed get(int index) {
			switch (index) {
			case 0 :
				return ULTRA_SLOW;
			case 1 :
				return VERY_SLOW;
			case 2 :
				return SLOW;
			case 3 :
				return NORMAL;
			case 4 :
				return FAST;
			case 5 :
				return VERY_FAST;
			case 6 :
				return REAL_TIME;
			}
			return NONE;
		}

	}

}
