package ch.heiafr.tic.sebastiano.ricchiuti.molesWhacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class MainActivity extends AppCompatActivity {

    private IGame game = new Game();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("On create ");

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            setContentView(R.layout.landscape);
        } else {
            // In portrait
            setContentView(R.layout.activity_main);
        }

        Button startButton = findViewById(R.id.button);
        TableLayout table = findViewById(R.id.imgTable);
        Vibrator myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        ImageView[][] imgMoles = new ImageView[((LinearLayout) table.getChildAt(0)).getChildCount()][table.getChildCount()];
        for (int i = 0; i < table.getChildCount(); i++) {
            LinearLayout elt = (LinearLayout) table.getChildAt(i);
            for (int j = 0; j < elt.getChildCount(); j++) {
                imgMoles[j][i] = (ImageView) elt.getChildAt(j);
                Integer x = i;
                Integer y = j;
                imgMoles[j][i].setOnClickListener((v) -> {
                    if (game.isPlaying()) {
                        myVib.vibrate(10);
                        game.play(x, y);
                    }
                });
            }
        }

        startButton.setOnClickListener((v) -> {
            if (game.isPlaying()) {
                game.preStart();
            } else {
                game.start();
            }
        });

        game.initialize(findViewById(R.id.txVTimerValue), findViewById(R.id.txVScoreValue), imgMoles, startButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("On start ");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("On restore");
        game.setScore(savedInstanceState.getInt("SCORE"));
        boolean isPlaying = savedInstanceState.getBoolean("IS_PLAYING");
        if (isPlaying) {
            game.resume();
        } else {
            game.preStart();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("On resume, Scenario 2 --> quit and resume");

        if (game.isPlaying()) {
            game.resume();
        } else {
            game.preStart();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //outState.putInt("TIME", game.getScore());
        outState.putInt("SCORE", game.getScore());
        outState.putBoolean("IS_PLAYING", game.isPlaying());
        System.out.println("On save ");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("On pause, Scenario 1 --> bouton back");
        game.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("On stop  ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("On pause destroy ");
    }

}