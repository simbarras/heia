package ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.helpers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Category;
import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Question;

public class Api {

    private final String URL_QUESTIONS = "https://opentdb.com/api.php?";
    private final String URL_CATEGORY = "https://opentdb.com/api_category.php";
    private final RequestQueue requestQueue;

    public Api(Application application) {
        requestQueue = Volley.newRequestQueue(application);
    }

    public void getCategory(OnRequestResult callback) {
        StringRequest stringRequest = new StringRequest(URL_CATEGORY,
                response -> {
                    JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
                    JsonArray categoriesJson = responseJson.getAsJsonArray("trivia_categories");

                    Gson gson = new Gson();
                    Category[] categories = new Category[categoriesJson.size()];
                    for (int i = 0; i < categoriesJson.size(); i++) {
                        categories[i] = gson.fromJson(categoriesJson.get(i), Category.class);
                    }
                    callback.consumeCategories(categories);
                }, error -> {
            // Not handled but should be.
        });
        requestQueue.add(stringRequest);
    }

    public void getQuestion(int cat, int amount, OnRequestResult callback) {
        StringRequest stringRequest = new StringRequest(URL_QUESTIONS + "amount=" + amount + "&category=" + cat, response -> {
            JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
            JsonArray questionsJson = responseJson.getAsJsonArray("results");

            Gson gson = new Gson();
            Question[] questions = new Question[amount];
            for (int i = 0; i < questionsJson.size(); i++) {
                questions[i] = gson.fromJson(questionsJson.get(i), Question.class);
            }
            callback.consumeQuestions(questions);
        }, error -> {
        });
        requestQueue.add(stringRequest);
    }


    public interface OnRequestResult {
        void consumeCategories(Category[] categories);

        void consumeQuestions(Question[] question);
    }

}