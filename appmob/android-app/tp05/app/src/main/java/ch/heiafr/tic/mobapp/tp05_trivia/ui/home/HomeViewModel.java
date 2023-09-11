package ch.heiafr.tic.mobapp.tp05_trivia.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Category;
import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

public class HomeViewModel extends AndroidViewModel {

    private final TriviaRepository triviaRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        triviaRepository = TriviaRepository.getInstance(application);
    }

    public LiveData<List<Category>> getCategories() {
        return triviaRepository.getCategory();
    }
}
