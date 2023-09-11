package tic.heiafr.ch.tp04_taboo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import tic.heiafr.ch.tp04_taboo.R;
import tic.heiafr.ch.tp04_taboo.taboo.Taboo;
import tic.heiafr.ch.tp04_taboo.taboo.TabooManager;

/**
 * Activity charged to manage a single taboo game
 *
 * @author Rafic Galli
 * @version 30.04.2020
 * @since 1.0
 */
public class PlayFrag extends Fragment implements TabooManager.OnTabooUpdate {

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
    private View view;

    public PlayFrag(){
        super(R.layout.activity_play);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabooManager = TabooManager.getInstance(this);
        boolean newGame = PlayFragArgs.fromBundle(getArguments()).getCodeNewGame();

        if (newGame) {
            List<String> teams = new ArrayList<>();
            for (String team : PlayFragArgs.fromBundle(getArguments()).getTeamsString()) {
                teams.add(team);
            }
            boolean isHard = PlayFragArgs.fromBundle(getArguments()).getIsHard();
            int numberOfCards = PlayFragArgs.fromBundle(getArguments()).getNumberOfCards();
            int turnDuration = PlayFragArgs.fromBundle(getArguments()).getTurnDuration();
            int numberOfPass = PlayFragArgs.fromBundle(getArguments()).getNumberOfPass();
            int errorPenalty = PlayFragArgs.fromBundle(getArguments()).getErrorPenality();

            if (savedInstanceState == null) {
                tabooManager.setupGame(getContext().getAssets(), isHard, teams, numberOfCards, turnDuration, numberOfPass, errorPenalty);
                tabooManager.startTurn();
            }
        } else {
            if (savedInstanceState == null) {
                tabooManager.startTurn();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_play, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tabooManager.resumeTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        tabooManager.pauseTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tabooManager.stopTimer();
    }

    /**
     *
     */
    private void initUI(View v) {
        timerText = v.findViewById(R.id.timer_text);
        remainingCardsText = v.findViewById(R.id.remaining_cards);
        currentTeamText = v.findViewById(R.id.current_team);
        successText = v.findViewById(R.id.success);
        failuresText = v.findViewById(R.id.failures);
        availablePass = v.findViewById(R.id.available_pass);
        wordText = v.findViewById(R.id.word);
        taboo1Text = v.findViewById(R.id.taboo_1);
        taboo2Text = v.findViewById(R.id.taboo_2);
        taboo3Text = v.findViewById(R.id.taboo_3);
        taboo4Text = v.findViewById(R.id.taboo_4);
        timerBar = v.findViewById(R.id.timer_bar);
        Button errorButton = v.findViewById(R.id.error_button);
        Button passButton = v.findViewById(R.id.pass_button);
        Button correctButton = v.findViewById(R.id.correct_button);

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
        Navigation.findNavController(view).navigate(R.id.action_playFrag_to_confirmFrag);
    }
}
