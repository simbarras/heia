package tic.heiafr.ch.tp03_taboo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import tic.heiafr.ch.tp03_taboo.R;
import tic.heiafr.ch.tp03_taboo.taboo.Taboo;
import tic.heiafr.ch.tp03_taboo.taboo.TabooManager;

/**
 * Activity charged to manage a single taboo game
 *
 * @author Rafic Galli
 * @version 30.04.2020
 * @since 1.0
 */
public class PlayActivity extends AppCompatActivity implements TabooManager.OnTabooUpdate {

    private TextView timerText;
    private TextView remainingCardsText;
    private TextView currentTeamText;
    private TextView failuresText;
    private TextView availablePass;
    private TextView successText;
    private TextView wordText;
    private TextView taboo1Text;
    private TextView taboo2Text;
    private TextView taboo3Text;
    private TextView taboo4Text;
    private ProgressBar timerBar;

    private TabooManager tabooManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        tabooManager = TabooManager.getInstance(this);

        Intent intent = getIntent();
        boolean newGame = intent.getBooleanExtra(HomeActivity.CODE_NEW_GAME, false);

        if (newGame) {
            List<String> teams = intent.getStringArrayListExtra(HomeActivity.CODE_TEAMS);
            boolean isHard = intent.getBooleanExtra(HomeActivity.CODE_DIFFICULT, TabooManager.DEFAULT_DIFFICULT);
            int numberOfCards = intent.getIntExtra(HomeActivity.CODE_CARDS, TabooManager.DEFAULT_NUMBER_OF_CARDS);
            int turnDuration = intent.getIntExtra(HomeActivity.CODE_DURATION, TabooManager.DEFAULT_TURN_DURATION);
            int numberOfPass = intent.getIntExtra(HomeActivity.CODE_PASS, TabooManager.DEFAULT_NUMBER_OF_PASS);
            int errorPenalty = intent.getIntExtra(HomeActivity.CODE_ERROR_PENALTY, TabooManager.DEFAULT_ERROR_PENALTY);

            if (savedInstanceState == null) {
                tabooManager.setupGame(getAssets(), isHard, teams, numberOfCards, turnDuration, numberOfPass, errorPenalty);
                tabooManager.startTurn();
            }
        } else {
            if (savedInstanceState == null) {
                tabooManager.startTurn();
            }
        }

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabooManager.resumeTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tabooManager.pauseTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabooManager.stopTimer();
    }

    /**
     *
     */
    private void initUI() {
        timerText = findViewById(R.id.timer_text);
        remainingCardsText = findViewById(R.id.remaining_cards);
        currentTeamText = findViewById(R.id.current_team);
        successText = findViewById(R.id.success);
        failuresText = findViewById(R.id.failures);
        availablePass = findViewById(R.id.available_pass);
        wordText = findViewById(R.id.word);
        taboo1Text = findViewById(R.id.taboo_1);
        taboo2Text = findViewById(R.id.taboo_2);
        taboo3Text = findViewById(R.id.taboo_3);
        taboo4Text = findViewById(R.id.taboo_4);
        timerBar = findViewById(R.id.timer_bar);
        Button errorButton = findViewById(R.id.error_button);
        Button passButton = findViewById(R.id.pass_button);
        Button correctButton = findViewById(R.id.correct_button);

        updateUI();
        timerText.setText(getString(R.string.integer, tabooManager.getTurnDuration()));
        timerBar.setProgress(100);

        errorButton.setOnClickListener((view) -> {
            tabooManager.failure();
            updateUI();
        });

        passButton.setOnClickListener((view) -> {
            tabooManager.pass();
            updateUI();
        });

        correctButton.setOnClickListener((view) -> {
            tabooManager.success();
            updateUI();
        });
    }

    /**
     *
     */
    private void updateUI() {
        currentTeamText.setText(getString(R.string.playing_team, tabooManager.getCurrentTeam()));
        remainingCardsText.setText(getString(R.string.remaining_cards, tabooManager.getNumberOfRemainingCards()));
        successText.setText(getString(R.string.integer, tabooManager.getCurrentSuccess()));
        failuresText.setText(getString(R.string.integer, tabooManager.getCurrentFailures()));
        availablePass.setText(getString(R.string.out_of, tabooManager.getAvailablePass(), tabooManager.getNumberOfPass()));

        Taboo card = tabooManager.getCard();
        String[] taboos = card.getTaboos();
        wordText.setText(card.getWord());
        taboo1Text.setText(taboos[0]);
        taboo2Text.setText(taboos[1]);
        taboo3Text.setText(taboos[2]);
        taboo4Text.setText(taboos[3]);
    }

    @Override
    public void onTick(int remainingTime, int progress) {
        timerText.setText(getString(R.string.integer, remainingTime));
        timerBar.setProgress(progress);
    }

    @Override
    public void onTurnFinished() {
        timerBar.setProgress(0);
        startActivity(new Intent(this, ConfirmActivity.class));
    }
}
