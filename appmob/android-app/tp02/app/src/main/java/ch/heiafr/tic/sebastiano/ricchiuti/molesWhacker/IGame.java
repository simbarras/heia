package ch.heiafr.tic.sebastiano.ricchiuti.molesWhacker;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public interface IGame {

    void initialize(TextView timer, TextView score, ImageView[][] moles, Button startButton);
    void preStart();
    void start();
    void play(int x, int y);
    void pause();
    boolean isPlaying();
    void resume();
    void stop();
    int getScore();
    int getTime();
    void setScore(int score);
    void setTime(int time);
    void setPlaying(boolean isPlaying);
}
