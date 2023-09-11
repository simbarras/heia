package ch.heia.isc.ol.simulife.model;

import ch.heia.isc.ol.simulife.command.TurnCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Simulation class
 */
public class Simulation {
    private final Board board;
    private final TurnCommand turnCommand;
    private Runnable onModificationAction;
    private boolean isPaused;
    private boolean isFinished;

    /**
     * Create a new simulation
     *
     * @param board The board to simulate
     */
    public Simulation(Board board) {
        this.board = board;
        turnCommand = new TurnCommand();
        this.isPaused = true;
        this.isFinished = false;
    }

    /**
     * This method is use by a thread to run the simulation
     * It runs a new step every 300ms and it can be paused
     * If forwardSimulation return that Homer died, the simulation is stopped
     *
     * @throws InterruptedException if the thread is interrupted
     */
    public void runLifeCycle() throws InterruptedException {
        while(true){

            Thread.sleep(300);

            while (isPaused || isFinished) {
                Thread.sleep(100);
                fireModificationEvent();
            }
            forwardSimulation();
        }
    }

    /**
     * Method to go one step forward in the simulation. If there are already some commands in the stack, it will execute them
     * else it will execute the next turn
     *
     * @return true if the simulation can go forward
     */
    public boolean forwardSimulation() {
        if (isFinished) return false;
        boolean hasFuture = turnCommand.hasFuture();
        PriorityQueue<CellElt> elements = turnCommand.redo(board);
        if (hasFuture) {
            System.out.println("Replace element on board");
            restoreState(elements);
            return true;
        } else {
            System.out.println("Compute next state");
            return performSimulation(elements);
        }
    }

    /**
     * Method to go one step backward in the simulation if we are not at the beginning
     */
    public void backwardSimulation() {
        PriorityQueue<CellElt> list = turnCommand.undo();
        if (list == null) {
            System.out.println("No more state to restore");
            return;
        }
        PriorityQueue<CellElt> elements = new PriorityQueue<>(list);
        restoreState(elements);
        isFinished = false;
    }

    /**
     * Restore the state of the board
     *
     * @param elements The elements to restore
     */
    private void restoreState(PriorityQueue<CellElt> elements) {
        board.clearBoard();
        CellElt e;
        while ((e = elements.poll()) != null) {
            if (e.getX() == -1 || e.getY() == -1) {
                System.out.println("Delete element");
                continue;
            }
            board.addElement(e, e.getX(), e.getY());
        }
        fireModificationEvent();
    }


    /**
     * Perform the simulation. It will execute the next turn and add the elements to the stack
     *
     * @param elements The elements to add to the stack
     * @return true if the simulation is not finished (Homer is not dead)
     */
    private boolean performSimulation(PriorityQueue<CellElt> elements) {
        List<CellElt> resultState = new ArrayList<>(elements);
        CellElt e;
        while ((e = elements.poll()) != null) {

            if (e.getX() == -1 || e.getY() == -1) {
                continue;
            }
            e.action();

            if (e.needToFinish()) {
                isFinished = true;
                return false;
            }
        }
        turnCommand.saveState(resultState);
        fireModificationEvent();
        return true;
    }

    public void pauseSimulation(boolean pause) {
        isPaused = pause;
    }

    /**
     * Register a refresh command of a view
     * It will be called to make the link between the simulation and the view
     *
     * @param onModificationAction The command to execute
     */
    public void registerRefreshCommand(Runnable onModificationAction) {
        this.onModificationAction = onModificationAction;
    }

    public void fireModificationEvent() {
        try {
            onModificationAction.run();
        } catch (NullPointerException e) {

        }
    }

    public int[][][] getState() {
        PriorityQueue<CellElt> elements = board.getAllElements();
        int[][][] state = new int[board.getHeight()][board.getWidth()][3];
        CellElt e;
        while ((e = elements.poll()) != null) {
            if (state[e.getY()][e.getX()][0] != 0) {
                continue;
            }
            state[e.getY()][e.getX()][0] = e.getColor().getRed();
            state[e.getY()][e.getX()][1] = e.getColor().getGreen();
            state[e.getY()][e.getX()][2] = e.getColor().getBlue();
        }
        return state;
    }

    /**
     * Get the board
     *
     * @return The board
     */
    public Board getBoard() {
        return board;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isFinished(){
        return isFinished;
    }


}
