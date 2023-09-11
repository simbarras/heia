package ch.heiafr.tic.mobapp.tp05_trivia.ui.game;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Question;
import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

public class GameViewModel extends AndroidViewModel {

    private final TriviaRepository triviaRepository;


    //=== Constructors
    public GameViewModel(Application application, int mode, int id) {
        super(application);
        triviaRepository = TriviaRepository.getInstance(application);
        triviaRepository.startGame(mode);
        triviaRepository.fetchQuestions(id);
    }

    public void startCountdown() {
        triviaRepository.startCountDown();
    }

    public void incrementCorrect() {
        triviaRepository.addCorrect(1);
    }

    //=== Getters and setters

    public LiveData<Question> getCurrentQuestion() {
        return triviaRepository.getQuestion();
    }

    public LiveData<Boolean> isGameEnded() {
        return triviaRepository.getGameFinish();
    }


}
