package tic.heiafr.ch.tp03_taboo.taboo;

import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tic.heiafr.ch.tp03_taboo.taboo.GameTimer.HUNDRED_MILLIS;
import static tic.heiafr.ch.tp03_taboo.taboo.GameTimer.SECOND;

import tic.heiafr.ch.tp03_taboo.R;

/**
 * Class that manages a single taboo match
 *
 * @author Rafic Galli
 * @version 30.04.2020
 * @since 1.0
 */
public class TabooManager implements GameTimer.OnGameTimerEvent {

    // Interface used to listen to events related a game timer
    public interface OnTabooUpdate {
        void onTick(int remainingTime, int progress);
        void onTurnFinished();
    }

    // Default game parameters
    public static final boolean DEFAULT_DIFFICULT = true;
    public static final int DEFAULT_NUMBER_OF_CARDS = 30;
    public static final int MAX_NUMBER_OF_CARDS = 56;
    public static final int DEFAULT_TURN_DURATION = 45;
    public static final int DEFAULT_NUMBER_OF_PASS = 2;
    public static final int DEFAULT_ERROR_PENALTY = 5;

    // Game Settings
    private int numberOfCards;
    private int turnDuration;
    private int numberOfPass;
    private int errorPenalty;
    private List<Taboo> cards;
    private List<Taboo> playedThisRound;
    private List<Taboo> played;

    // Game info
    private List<Team> teams;
    private int successes;
    private int failures;
    private int availablePass;
    private int currentTeam;
    private Taboo currentCard;
    private GameTimer timer;
    private boolean isPlaying;
    private boolean gameOver;

    private static OnTabooUpdate listener;

    private static TabooManager instance;  // unique instance

    private TabooManager() {} // constructor

    /**
     * Get the unique instance of the GameTimer class
     * @param listener used for timer updates
     * @return unique instance of GameTimer class
     */
    public static TabooManager getInstance(OnTabooUpdate listener) {
        if (instance == null) {
            instance = new TabooManager();
        }
        TabooManager.listener = listener;
        return instance;
    }

    /**
     * Constructor used to create a game with given parameters without listening to timer events
     *
     * @param assetManager  Used to retrieve the json asset containing taboo cards
     * @param isHard        True to set the game on easy. False to set it on hard
     * @param teams         Teams names playing the game
     * @param numberOfCards Number of cards in the game
     * @param turnDuration  Duration in seconds for each turn
     * @param numberOfPass  Number of pass that each team have at the begin of each turn
     * @param errorPenalty  Penalty time when making an error
     */
    public void setupGame(AssetManager assetManager, boolean isHard, List<String> teams, int numberOfCards, int turnDuration, int numberOfPass, int errorPenalty) {
        // Checking that the given parameters are valid. Otherwise, setting them to default values
        if (numberOfCards < 1 || numberOfCards > MAX_NUMBER_OF_CARDS)
            this.numberOfCards = DEFAULT_NUMBER_OF_CARDS;
        else
            this.numberOfCards = numberOfCards;

        if (turnDuration < 1)
            this.turnDuration = DEFAULT_TURN_DURATION;
        else
            this.turnDuration = turnDuration;

        if (numberOfPass < 0)
            this.numberOfPass = DEFAULT_NUMBER_OF_PASS;
        else
            this.numberOfPass = numberOfPass;

        if (errorPenalty < 0)
            this.errorPenalty = DEFAULT_ERROR_PENALTY;
        else
            this.errorPenalty = errorPenalty;

        this.teams = new ArrayList<>();
        for (String name : teams) {
            this.teams.add(new Team(name));
        }

        successes = 0;
        failures = 0;
        availablePass = numberOfPass;
        currentTeam = 0;
        isPlaying = false;
        playedThisRound = new ArrayList<>();
        played = new ArrayList<>();
        gameOver = false;

        loadTaboos(assetManager, isHard);           // Loading taboo cards based on game difficult
    }

    /**
     * Start the current turn. A new countdown timer is then started
     */
    public void startTurn() {
        if (!isPlaying) {
            newCard();
            isPlaying = true;
            timer = GameTimer.getInstance(this);
            timer.startTimer(turnDuration * SECOND, HUNDRED_MILLIS);
        }
    }

    /**
     * Validate the cards that have been played during the previous round
     */
    public void validateTurn() {
        Team team = teams.get(currentTeam);
        while (!playedThisRound.isEmpty()) {
            Taboo card = playedThisRound.remove(0);
            if (card.getState() == CardState.WON) {
                team.incrementScore();
                played.add(card);
            } else {
                cards.add(card);
            }
        }

        if (played.size() < numberOfCards) {
            nextTurn();
            startTurn();
        } else {
            teams.sort((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));
            gameOver = true;
        }
    }

    /**
     * Call this method when the current playing team answers correctly to the current
     * taboo card
     */
    public void success() {
        if (isPlaying) {
            successes++;
            currentCard.setState(CardState.WON);
            newCard();
        }
    }

    /**
     * Call this method when the current playing team answers wrongly to the current taboo card
     */
    public void failure() {
        if (isPlaying) {
            failures++;
            currentCard.setState(CardState.FAILED);
            timer.subtractMillis(errorPenalty * SECOND);
            newCard();
        }
    }

    /**
     * Call this method when the current playing team pass the current taboo card. This will
     * pick a new random taboo card
     */
    public void pass() {
        if (isPlaying) {
            if (--availablePass < 0)
                availablePass = 0;
            else {
                currentCard.setState(CardState.PASSED);
                newCard();
            }
        }
    }

    /**
     * Resume the timer
     */
    public void resumeTimer() {
        timer.resumeTimer();
    }

    /**
     * Pause the timer
     */
    public void pauseTimer() {
        timer.pauseTimer();
    }

    /**
     * Stop the timer
     */
    public void stopTimer() {
        timer.stopTimer();
    }

    /**
     * @return The current team that is playing the current turn. The first team starts at
     * the value 0
     */
    public String getCurrentTeam() {
        return teams.get(currentTeam).getName();
    }

    /**
     * @return The number of available pass for the current round
     */
    public int getAvailablePass() {
        return availablePass;
    }

    /**
     * @return The number of correct answers given by the current playing team
     */
    public int getCurrentSuccess() {
        return successes;
    }

    /**
     * @return All the number of incorrect answers for each team. The index represent the
     * desired team
     */
    public int getCurrentFailures() {
        return failures;
    }

    /**
     * @return The duration in seconds for each turn
     */
    public int getTurnDuration() {
        return turnDuration;
    }

    /**
     * @return The current picked taboo card
     */
    public Taboo getCard() {
        return currentCard;
    }

    /**
     * @return The list of teams
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * @return The number of remaining cards
     */
    public int getNumberOfRemainingCards() {
        return cards.size();
    }

    /**
     * @return The number of pass
     */
    public int getNumberOfPass() {
        return numberOfPass;
    }

    /**
     * @return Cards that have been played this round
     */
    public List<Taboo> getPlayedThisRound() {
        return playedThisRound;
    }

    /**
     * @return If the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * @return If the game is playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Pick a new random taboo card from the previously loaded list
     */
    private void newCard() {
        if (cards.size() != 0) {
            currentCard = cards.remove(0);
            playedThisRound.add(currentCard);
        } else {
            timer.stopTimer();
            isPlaying = false;
            listener.onTurnFinished();
        }
    }

    /**
     * Prepare the next turn. If the game is finished or a turn is going on, this method
     * does nothing
     */
    private void nextTurn() {
        currentTeam = ++currentTeam % teams.size();
        availablePass = numberOfPass;
        successes = 0;
        failures = 0;
    }

    /**
     * Load the list of taboo cards from a json file in the asset folder
     *
     * @param assetManager Used to retrieve the json asset containing taboo cards
     * @param isHard       Used to select the json file. True to load a set of hard taboo cards. False
     *                     for easy taboo cards
     */
    private void loadTaboos(AssetManager assetManager, boolean isHard) {
        try {
            String fileName = "taboo_cards_" + (isHard ? "hard.json" : "easy.json");
            InputStream inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer);

            cards = new ArrayList<>(Arrays.asList(new Gson().fromJson(json, Taboo[].class)));
            while(cards.size() > numberOfCards)
                cards.remove(numberOfCards-1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called on each tick as defined in the second parameter of the method startTimer()
     * @param l remaining time
     */
    @Override
    public void onTick(long l) {
        if (listener != null) {
            int time = (int) (l / SECOND);
            int progress = time * 100 / turnDuration;
            listener.onTick(time, progress);
        }
    }

    /**
     * Method called when the countdown of the current round is finished and used to notify
     * the listener
     */
    @Override
    public void onFinish() {
        isPlaying = false;

        if (listener != null) {
            listener.onTurnFinished();
        }
    }
}
