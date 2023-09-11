package tic.heiafr.ch.tp04_taboo.taboo;

/**
 * Class representing a single taboo card
 *
 * @author Rafic Galli
 * @version 30.04.2020
 * @since 1.0
 */
public class Taboo {

    private String word;
    private String[] taboos;

    private CardState state;

    /**
     * Constructor creating the read-only taboo card
     *
     * @param word The word that a team has to guess
     * @param taboos The list of forbidden words
     */
    public Taboo (String word, String[] taboos) {
        this.word = word;
        this.taboos = taboos;
        state = CardState.FAILED;
    }

    /**
     * @return The word that a team has to guess
     */
    public String getWord () {
        return word;
    }

    /**
     * @return The list of forbidden words
     */
    public String[] getTaboos () {
        return taboos;
    }

    /**
     * @return If the card has been correctly/wrongly/passed played
     */
    public CardState getState() {
        return state;
    }

    /**
     * Set the state of the taboo
     * @param state card state (WON/FAILED/PASSED)
     */
    public void setState(CardState state) {
        this.state = state;
    }
}
