package ch.epfl.general_libraries.event;

import java.util.EventListener;

public interface RunnerEventListener extends EventListener {

	public void runnerSpeedChanged(RunnerEvent e);
	public void runnerStateChanged(RunnerEvent e);
	public void runnerProgressed(int percent);

}