package ch.heiafr.tic.mobapp.tp05_trivia.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

public class ProfileViewModel extends AndroidViewModel {
    private final TriviaRepository triviaRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        triviaRepository = TriviaRepository.getInstance(application);
    }
}
