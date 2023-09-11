package ch.heia.isc.ol.simulife.command;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.CellElt;

import java.util.*;

/**
 * Command pattern implementation
 */
public class TurnCommand {

    // List of the history of the states of the board
    private final List<List<CellElt>> states;
    // Index of the state that is currently displayed. -1 means no state is displayed.
    private int currentStateIndex;

    public TurnCommand() {
        states = new ArrayList<>();
        currentStateIndex = -1;
    }

    /**
     * Check if the next state is known or need to be computed.
     *
     * @return false if the iterator is at the end of the list
     */
    public boolean hasFuture() {
        return currentStateIndex + 1 < states.size();
    }

    /**
     * Redo the last undo if there is one or do a new turn
     *
     * @param board
     * @return the priorityqueue with the list of the elements that are stored
     */
    public PriorityQueue<CellElt> redo(Board board) {
        List<CellElt> currentState;
        if (hasFuture()) {
            // Here the incrementation is done before because the iterator point the current state
            currentState = createCopy(states.get(++currentStateIndex));
            System.out.println("Restore state: " + currentStateIndex);
            System.out.println("Turn: " + currentStateIndex);
            System.out.println("Stored state: " + states.size());
            System.out.println("Current state: " + currentState);
        } else {
            currentState = new ArrayList<>(board.getAllElements());
            currentStateIndex++;
            System.out.println("Creating new state: " + currentStateIndex);
        }
        return new PriorityQueue<>(currentState);
    }

    /**
     * Add the current state of the board to the list of states
     *
     * @param currentState
     */
    public void saveState(List<CellElt> currentState) {
        states.add(createCopy(new ArrayList<>(currentState)));
        logCurrentState();
    }


    /**
     * Make a step back of the simulation
     *
     * @return the priorityqueue with the list of the elements that are stored (up to date)
     */
    public PriorityQueue<CellElt> undo() {
        if (currentStateIndex <= 0) {
            System.out.println("No more undo");
            return null;
        }

        // Here the decrementation is done before because the iterator point the current state
        List<CellElt> currentState = states.get(--currentStateIndex);
        logCurrentState();
        return new PriorityQueue<>(createCopy(currentState));

    }

    /**
     * Create a copy (clone) of the list of elements to not have the reference
     *
     * @param element
     * @return the copy of the list of elements
     */
    private List<CellElt> createCopy(List<CellElt> element) {
        List<CellElt> copy = new ArrayList<>();
        for (CellElt cellElt : element) {
            copy.add(cellElt.clone());
        }
        return copy;
    }

    private void logCurrentState() {
        System.out.println("Current state index: " + currentStateIndex);
        System.out.println("Number of stored states: " + states.size());
        System.out.println("Current state (description): " + states.get(currentStateIndex));
    }
}

