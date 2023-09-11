package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.ActivityFull;

public class ConcurrentModificationException extends Exception {

    ActivityFull activityFull;
    public ConcurrentModificationException(String message, ActivityFull activityFull) {
        super(message);
        this.activityFull = activityFull;
    }

    public ActivityFull getActivityFull() {
        return activityFull;
    }
}
