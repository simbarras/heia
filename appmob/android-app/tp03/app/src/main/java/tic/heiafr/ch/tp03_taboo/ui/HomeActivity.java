package tic.heiafr.ch.tp03_taboo.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import android.widget.EditText;

import java.util.ArrayList;

import tic.heiafr.ch.tp03_taboo.R;

/**
 *
 */
public class HomeActivity extends AppCompatActivity {

    public static final String CODE_DIFFICULT = "code_difficult";
    public static final String CODE_TEAMS = "code_teams";
    public static final String CODE_PASS = "code_pass";
    public static final String CODE_DURATION = "code_duration";
    public static final String CODE_CARDS = "code_cards";
    public static final String CODE_ERROR_PENALTY = "code_error_penalty";
    public static final String CODE_NEW_GAME = "code_new_game";

    private EditText team1;
    private EditText team2;
    private EditText team3;
    private EditText team4;
    private EditText numberOfCards;
    private EditText turnDuration;
    private EditText numberOfPass;
    private EditText errorPenalty;
    private SwitchCompat difficultSwitch;

    /**
     * Initialise the activity
     *
     * @param savedInstanceState previous saved state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
    }

    /**
     * Fetch the views and validate the input fields.
     */
    private void initUI() {
        team1 = findViewById(R.id.team_1_txt);
        team2 = findViewById(R.id.team_2_txt);
        team3 = findViewById(R.id.team_3_txt);
        team4 = findViewById(R.id.team_4_txt);
        numberOfCards = findViewById(R.id.number_of_cards_txt);
        turnDuration = findViewById(R.id.turn_duration_txt);
        numberOfPass = findViewById(R.id.number_of_pass_txt);
        difficultSwitch = findViewById(R.id.difficult_switch);
        errorPenalty = findViewById(R.id.error_penalty_txt);
        Button playButton = findViewById(R.id.play_button);

        playButton.setOnClickListener((view) -> {
            String team1Text = team1.getText().toString().trim();
            String team2Text = team2.getText().toString().trim();
            String team3Text = team3.getText().toString().trim();
            String team4Text = team4.getText().toString().trim();
            String cardsText = numberOfCards.getText().toString().trim();
            String durationText = turnDuration.getText().toString().trim();
            String passText = numberOfPass.getText().toString().trim();
            String errorPenaltyText = errorPenalty.getText().toString().trim();

            ArrayList<String> teamsText = new ArrayList<>();
            if (!team1Text.isEmpty())
                teamsText.add(team1Text);
            if (!team2Text.isEmpty())
                teamsText.add(team2Text);
            if (!team3Text.isEmpty())
                teamsText.add(team3Text);
            if (!team4Text.isEmpty())
                teamsText.add(team4Text);

            if (teamsText.size() < 2 || cardsText.isEmpty() || durationText.isEmpty() || passText.isEmpty() || errorPenaltyText.isEmpty())
                return;

            playGame(difficultSwitch.isChecked(), teamsText,
                    Integer.parseInt(cardsText),
                    Integer.parseInt(durationText),
                    Integer.parseInt(passText),
                    Integer.parseInt(errorPenaltyText));
        });
    }

    /**
     * Launch the activity where the game will happen
     * @param isHard taboos difficulty
     * @param teams team names
     * @param numberOfCards number of taboos for the whole game
     * @param turnDuration available time per turn
     * @param numberOfPass available passes per turn
     * @param errorPenalty penalty when a taboo is wrongly guessed
     */
    private void playGame(boolean isHard, ArrayList<String> teams, int numberOfCards, int turnDuration, int numberOfPass, int errorPenalty) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(CODE_DIFFICULT, isHard);
        intent.putExtra(CODE_TEAMS, teams);
        intent.putExtra(CODE_CARDS, numberOfCards);
        intent.putExtra(CODE_DURATION, turnDuration);
        intent.putExtra(CODE_PASS, numberOfPass);
        intent.putExtra(CODE_ERROR_PENALTY, errorPenalty);
        intent.putExtra(CODE_NEW_GAME, true);
        startActivity(intent);
    }
}
