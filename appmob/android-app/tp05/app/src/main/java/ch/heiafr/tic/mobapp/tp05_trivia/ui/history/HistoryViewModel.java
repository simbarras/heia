package ch.heiafr.tic.mobapp.tp05_trivia.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

public class HistoryViewModel extends AndroidViewModel {
    private final TriviaRepository triviaRepository;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        triviaRepository = TriviaRepository.getInstance(application);
    }
}
