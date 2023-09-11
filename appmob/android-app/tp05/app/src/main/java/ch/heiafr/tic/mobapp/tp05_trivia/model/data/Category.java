package ch.heiafr.tic.mobapp.tp05_trivia.model.data;

/**
 * Data class to hold data about a category
 */
public class Category {

    //=== Attributes

    private final int id;             // category id in the trivia api
    private final String name;       // category name


    //=== Constructors

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }


    //=== Getters

    public int getId() {
        return id;
    }

    public String getTitle() {
        return name;
    }
}
