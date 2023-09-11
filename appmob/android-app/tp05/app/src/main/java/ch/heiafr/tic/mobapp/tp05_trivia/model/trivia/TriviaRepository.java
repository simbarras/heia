package ch.heiafr.tic.mobapp.tp05_trivia.model.trivia;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Category;
import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Question;
import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.helpers.Api;

/**
 * Repository handling the whole trivia logic and holding data
 */
public class TriviaRepository {

    //=== Constants

    public static final int MODE_HISTORY = 0;               // mode when history
    public static final int MODE_GAME = 1;                  // mode when game
    public static final int DEFAULT_CORRECT_ANSWER = 0;     // default correct answer position
    public static final int QUESTION_NUMBER = 3;           // number of questions
    public static final float MEDAL_GOLD = 0.8f;            // gold medal rating
    public static final float MEDAL_SILVER = 0.6f;          // silver medal rating
    public static final float MEDAL_BRONZE = 0.4f;          // bronze medal rating
    public static final int ANSWER_NUMBER = 4;              // number of answers per question
    public static final int QUESTION_INTERVAL = 1500;       // time interval before next question
    public static final String QUESTION_TYPE = "multiple";  // question type
    public static final String EASY = "easy";               // easy difficulty text
    public static final String MEDIUM = "medium";           // medium difficulty text


    //=== Attributes

    private final Application application;                  // application context

    private MutableLiveData<TriviaMedal> medal;             // result medal image
    private MutableLiveData<String> textMedal;              // result medal text
    private MutableLiveData<Integer> corrects;              // correctly answered questions

    private MutableLiveData<Boolean> gameFinish;
    private CountDownTimer countDown;
    private int questionIndex;

    private MutableLiveData<List<Category>> categories; //TODO non sono sicuro
    private MutableLiveData<List<Question>> questions; //TODO non sono sicuro
    private MutableLiveData<Question> currentQuestion; //TODO non sono sicuro

    private Api api;

    //=== Singleton

    private static TriviaRepository INSTANCE;       // class unique instance

    // Create the unique instance of the TriviaRepository class.
    private TriviaRepository(Application application) {
        this.application = application;
        // TODO: To implement
        /*medal.postValue(TriviaMedal.GOLD);
        textMedal.postValue("Gold");
        medal.postValue(TriviaMedal.SILVER);
        textMedal.postValue("Sliver");
        medal.postValue(TriviaMedal.BRONZE);
        textMedal.postValue("Bronze");*/

        api = new Api(application);
        categories = new MutableLiveData<>();
        currentQuestion = new MutableLiveData<>();
        questions = new MutableLiveData<>();
        gameFinish = new MutableLiveData<>();
        corrects = new MutableLiveData<>();
        textMedal = new MutableLiveData<>();
        medal = new MutableLiveData<>();

        Api.OnRequestResult callbackApi = new Api.OnRequestResult() {
            @Override
            public void consumeCategories(Category[] resultCategories) {
                List<Category> cats = new ArrayList<>();
                for (Category cat: resultCategories){
                    System.out.println(cat.getTitle());
                    cats.add(cat);
                }
                categories.postValue(cats);
            }

            @Override
            public void consumeQuestions(Question[] question) {
            }
        };

        api.getCategory(callbackApi);

        //category = new Api(application).getCategory();

    }

    // Get the unique instance of the TriviaRepository class.
    public static TriviaRepository getInstance(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new TriviaRepository(application);
        }
        return INSTANCE;
    }


    //=== Getters and setters

    // Gets the number of correctly answered questions.
    public LiveData<Integer> getCorrects() {
        return corrects;
    }

    // Get the medal image.
    public LiveData<TriviaMedal> getMedal() {
        return medal;
    }

    // Get the text associated to the medal.
    public LiveData<String> getTextMedal() {
        return textMedal;
    }

    // TODO: To implement

    public LiveData<List<Category>> getCategory() {return categories;} //TODO non sicuro
    public LiveData<Question> getQuestion() {return currentQuestion;} //TODO non sicuro
    public LiveData<Boolean> getGameFinish(){return gameFinish;}
    public void addCorrect(int i) {
        corrects.setValue(corrects.getValue() + i);
    }

    //=== Public methods
    public void determineMedal() {
        // TODO: To implement
    }

    public void fetchQuestions(int cat){
        Api.OnRequestResult callbackApi = new Api.OnRequestResult() {
            @Override
            public void consumeCategories(Category[] resultCategories) {
            }

            @Override
            public void consumeQuestions(Question[] question) {
                List<Question> quests = new ArrayList<>();
                for (Question q: question){
                    quests.add(q);
                }
                TriviaRepository.this.questions.postValue(quests);
            }
        };

        api.getQuestion(cat, QUESTION_NUMBER, callbackApi);
    }

    public void startGame(int mode) {
        gameFinish.setValue(false);
        if (mode == MODE_GAME) {
            questionIndex = 0;
            corrects.setValue(DEFAULT_CORRECT_ANSWER);
        }
    }


    public void startCountDown() {
        countDown = new CountDownTimer(QUESTION_INTERVAL, QUESTION_INTERVAL) {
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                nextQuestion();
            }
        };
        countDown.start();
    }

    // TODO: To implement


    //=== Private methods

    private void nextQuestion() {
        if (questionIndex < QUESTION_NUMBER) {
            currentQuestion.setValue(questions.getValue().get(questionIndex++));
        } else {
            this.gameFinish.setValue(true);
        }
    }


}
