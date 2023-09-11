package ch.heiafr.tic.mobapp.tp05_trivia.ui.result;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import ch.heiafr.tic.mobapp.tp05_trivia.R;
import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

/**
 * ViewModel associated with the result screen
 */
public class ResultViewModel extends AndroidViewModel {

    //=== Attributes

    private final TriviaRepository triviaRepository;


    //=== Constructors

    /**
     * Create a ResultViewModel object.
     *
     * @param application application context
     */
    public ResultViewModel(final Application application) {
        super(application);

        triviaRepository = TriviaRepository.getInstance(application);
        triviaRepository.determineMedal();
    }


    //=== Getters and setters

    // gets the medal text.
    public LiveData<String> getTextMedal() {
        return triviaRepository.getTextMedal();
    }

    // gets the medal image.
    public LiveData<Drawable> getMedal() {
        return Transformations.map(triviaRepository.getMedal(), input -> {
            switch(input) {
                case GOLD:
                    return ContextCompat.getDrawable(getApplication(), R.drawable.ic_medal_gold);
                case SILVER:
                    return ContextCompat.getDrawable(getApplication(), R.drawable.ic_medal_silver);
                case BRONZE:
                    return ContextCompat.getDrawable(getApplication(), R.drawable.ic_medal_bronze);
                default:
                    return ContextCompat.getDrawable(getApplication(), R.drawable.ic_medal_fail);
            }
        });
    }

    // gets the number of question correctly answered.
    public LiveData<String> getScore() {
        return Transformations.map(triviaRepository.getCorrects(), input -> getApplication().getString(R.string.score_question, input, TriviaRepository.QUESTION_NUMBER));
    }
}
