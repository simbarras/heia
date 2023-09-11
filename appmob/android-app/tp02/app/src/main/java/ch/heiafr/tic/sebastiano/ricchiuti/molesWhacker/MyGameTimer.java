package ch.heiafr.tic.sebastiano.ricchiuti.molesWhacker;

public class MyGameTimer implements GameTimer.OnGameTimerEvent {

    private GameTimer myGameTimer;
    private Game myGame;
    private final long howMuchTimeProgame = 10000;
    private final long intervalForUpdate = 100;

    public MyGameTimer(Game game) {
        myGameTimer = GameTimer.getInstance(this);
        myGame = game;
    }

    public void start() {
        myGameTimer.startTimer(howMuchTimeProgame, intervalForUpdate);
    }

    public void stop() {
        myGameTimer.stopTimer();
    }

    public void pause() {
        myGameTimer.pauseTimer();
    }

    public void resume(){
        myGameTimer.resumeTimer();
    }

    @Override
    public void onTick(long l) {
        myGame.displayTimer(l);
    }

    @Override
    public void onFinish() {
        myGameTimer.stopTimer();
        myGame.preStart();
    }
}
