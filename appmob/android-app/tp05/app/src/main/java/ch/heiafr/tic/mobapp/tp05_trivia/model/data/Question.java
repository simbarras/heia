package ch.heiafr.tic.mobapp.tp05_trivia.model.data;

import java.util.List;

/**
 * Data class to hold data about a question
 */
public class Question {

    //=== Attributes

    private int id;
    private final String categoryTitle;     // category title
    private final String type;              // multiple choice or true/false
    private final String difficulty;        // easy/medium/hard
    private final String question;          // question title
    private int correctAnswer;              // index of correct answer
    private int givenAnswer;                // index of given answer by the player
    private boolean correct;                // player has correctly answered
    private final List<String> answers;     // list of possible answers


    //=== Constructors

    public Question(String categoryTitle, String type, String difficulty, String question, int correctAnswer, List<String> answers) {
        this.categoryTitle = categoryTitle;
        this.type = type;
        this.difficulty = difficulty;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.answers = answers;
        givenAnswer = -1;
    }


    //=== Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getType() {
        return type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getGivenAnswer() {
        return givenAnswer;
    }

    public void setGivenAnswer(int givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
