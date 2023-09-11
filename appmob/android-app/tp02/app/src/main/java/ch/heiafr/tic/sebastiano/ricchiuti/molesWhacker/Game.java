package ch.heiafr.tic.sebastiano.ricchiuti.molesWhacker;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class Game implements IGame {
    private int score;
    private TextView lblTimer;
    private TextView lblScore;
    private Button startButton;
    private boolean isPlaying;
    private MyGameTimer myTimer;
    private ImageView[][] moles;
    private int[] currentMole;
    private Random rnd;


    public Game() {
        myTimer = new MyGameTimer(this);
        rnd = new Random();
        currentMole = new int[]{-1, -1};
        this.score = 0;

    }


    @Override
    public void initialize(TextView timer, TextView score, ImageView[][] moles, Button startButton) {
        lblTimer = timer;
        lblScore = score;
        this.moles = moles;
        this.startButton = startButton;
    }

    @Override
    public void preStart() {
        stop();
        isPlaying = false;
        for (int i = 0; i < moles.length; i++) {
            for (int j = 0; j < moles[i].length; j++) {
                moles[i][j].setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void start() {
        score = 0;
        myTimer.start();
        resume();
    }

    @Override
    public void play(int x, int y) {
        if (!isPlaying) return;
        if (x == currentMole[0] && y == currentMole[1]) {
            score++;
            nextMole();
        }
        displayScore();
    }

    @Override
    public void pause() {
        myTimer.pause();
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void resume() {
        startButton.setText("Stop");
        isPlaying = true;
        displayScore();
        currentMole[0] = -1;
        currentMole[1] = -1;

        myTimer.resume();
        nextMole();
    }

    @Override
    public void stop() {
        startButton.setText("Start");
        myTimer.stop();
        isPlaying = false;
    }

    public void displayTimer(long timer) {
        timer = timer / 1000;
        lblTimer.setText(String.valueOf(timer));
    }

    private void displayScore() {
        lblScore.setText(String.valueOf(score));
    }

    private void nextMole() {
        int x, y;
        do {
            x = rnd.nextInt(moles[0].length );
            y = rnd.nextInt(moles.length);
        } while (x == currentMole[0] && y == currentMole[1]);
        currentMole[0] = x;
        currentMole[1] = y;
        onlyShowXYImage(x, y);
    }

    private void onlyShowXYImage(int x, int y) {
        for (int i = 0; i < moles.length; i++) {
            for (int j = 0; j < moles[i].length; j++) {
                if (j == x && i == y) {
                    moles[i][j].setVisibility(View.VISIBLE);
                } else {
                    moles[i][j].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public int getScore(){
        return score;
    }

    @Override
    public int getTime(){
        return Integer.parseInt(lblTimer.getText().toString());
    }

    public void setScore(int score){
        this.score = score;
    }

    @Override
    public void setTime(int time) {

    }

    @Override
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
