package ch.epfl.general_libraries.event;

import java.util.EventObject;


public class RunnerEvent extends EventObject {

	public static final long serialVersionUID = 0;

	public RunnerEvent(Runnable t) {
		super(t);
	}

}