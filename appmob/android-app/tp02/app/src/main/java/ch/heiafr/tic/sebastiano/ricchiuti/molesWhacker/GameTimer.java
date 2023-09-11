package ch.heiafr.tic.sebastiano.ricchiuti.molesWhacker;

import android.os.CountDownTimer;
import android.util.Log;

/**
 *
 */
public class GameTimer {

    /* *****************************************************************************************
     * ATTRIBUTES
     **************************************************************************************** */

    private static OnGameTimerEvent listener;   // listener for timer updates
    private CountDownTimer countDownTimer;      // timer object

    private boolean paused;                     // is the countdown timer paused or not
    private long countDownLeft;                 // remaining time left when paused
    private long countDownTick;                 // interval for updates

    /* *****************************************************************************************
     * SINGLETON PATTERN
     **************************************************************************************** */

    private static GameTimer instance;  // unique instance

    private GameTimer() { paused = false; } // constructor

    /**
     * Get the unique instance of the GameTimer class
     * @param listener used for timer updates
     * @return unique instance of GameTimer class
     */
    public static GameTimer getInstance(OnGameTimerEvent listener) {
        if (instance == null) {
            instance = new GameTimer();
        }
        GameTimer.listener = listener;
        return instance;
    }

    /* *****************************************************************************************
     * PUBLIC METHODS
     **************************************************************************************** */

    /**
     * Start the countdown timer
     * @param countDownTime total time
     * @param countDownTick interval for updates
     */
    public void startTimer(long countDownTime, long countDownTick) {
        this.countDownTick = countDownTick;
        start(countDownTime, countDownTick);
    }

    /**
     * Resume the countdown timer
     */
    public void resumeTimer() {
        if (paused) {
            start(countDownLeft, countDownTick);
        }
    }

    /**
     * Pause the countdown timer
     */
    public void pauseTimer() {
        if (countDownTimer != null) {
            paused = true;
            countDownTimer.cancel();
        }
    }

    /**
     * Stop and destroy the countdown timer
     */
    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    /* *****************************************************************************************
     * PRIVATE METHODS
     **************************************************************************************** */

    /**
     * Internal method to handle the start of the countdown
     * @param time total time
     * @param tick interval for updates
     */
    private void start(long time, long tick) {
        paused = false;
        countDownTimer = new CountDownTimer(time, tick) {
            @Override
            public void onTick(long l) {
                Log.i("onMolesWacker", "onTick");
                countDownLeft = l;
                listener.onTick(l);
            }

            @Override
            public void onFinish() {
                Log.i("onMolesWacker", "onFinish");
                listener.onFinish();
            }
        };
        countDownTimer.start();
    }

    /* *****************************************************************************************
     * INTERFACE
     **************************************************************************************** */

    /**
     * Interface to implement when using a GameTimer object
     */
    public interface OnGameTimerEvent {

        /**
         * Called on each tick as defined in the second parameter of the method startTimer()
         * @param l remaining time
         */
        void onTick(long l);

        /**
         * Called once the countdown has finished
         */
        void onFinish();
    }
}
