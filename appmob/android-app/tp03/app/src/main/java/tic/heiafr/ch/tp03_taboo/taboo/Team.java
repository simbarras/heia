package tic.heiafr.ch.tp03_taboo.taboo;

/**
 *
 */
public class Team {

    private String name;        // team name
    private int score;          // team score

    /**
     * Constructor creating a team
     * @param name team name
     */
    public Team(String name) {
        this.name = name;
    }

    /**
     * @return Get the team name
     */
    public String getName() {
        return name;
    }

    /**
     * @return Get the number of correctly answered taboo
     */
    public int getScore() {
        return score;
    }

    /**
     * Increment the team score
     */
    public void incrementScore() {
        this.score++;
    }
}
