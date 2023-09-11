package ch.heia.isc.ol.simulife.api;

import ch.heia.isc.gl1.simulife.interface_.IPositionnableElement;
import ch.heia.isc.ol.simulife.App;
import ch.heia.isc.ol.simulife.factory.CellEltFactory;
import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.BoardInitializer;
import ch.heia.isc.ol.simulife.model.Simulation;
import ch.heia.isc.ol.simulife.view.DefaultView;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@CrossOrigin
@RestController
public class REST {

    private final HashMap<String, Simulation> universes = new HashMap<>();

    /**
     * Get all UUIDs of the universes
     *
     * @return the String list of UUIDs
     */
    @GetMapping("/allUniverses")
    public String[] allUniverses() {
        return universes.keySet().toArray(new String[0]);
    }


    /**
     * Create a new universe
     *
     * @param body the body of the request containing the width and the height of the universe
     * @return the id of the universe (UUID)
     */
    @PostMapping("/universes")
    @ResponseBody
    public UUID createUniverse(@RequestBody(required = false) JSONObject body) {
        int width;
        int height;

        try {
            width = Integer.parseInt(body.get("width").toString());
        } catch (Exception e) {
            width = 10;
        }
        try {
            height = Integer.parseInt(body.get("height").toString());
        } catch (Exception e) {
            height = 10;
        }

        Board board = new BoardInitializer().initializeBoard(width, height);

        Simulation simulation = new Simulation(board);

        if (App.displayView) {
            DefaultView defaultView = new DefaultView(board);
            simulation.registerRefreshCommand(defaultView::refresh);
            defaultView.registerUndoCommand(simulation::backwardSimulation);
            defaultView.registerRedoCommand(simulation::forwardSimulation);
            defaultView.registerPauseCommand(simulation::pauseSimulation);
            defaultView.registerPauseInfo(simulation::isPaused);
            defaultView.registerStoppedInfo(simulation::isFinished);
            defaultView.setVisible(true);
            defaultView.refresh();
        }
        UUID uuid = UUID.randomUUID();
        String universeId = uuid.toString();
        universes.put(universeId, simulation);

        //Create a new thread for the simulation
        Thread thread = new Thread(() -> {
            try {
                simulation.runLifeCycle();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
        thread.setName("Simulation " + universeId);
        thread.start();

        return uuid;
    }


    /**
     * Delete a universe by its id
     *
     * @param id the id of the universe to delete
     * @return the id of the deleted universe
     */
    @DeleteMapping("/universes/{id}")
    public UUID deleteUniverse(@PathVariable String id) {
        universes.remove(id);
        return UUID.fromString(id);
    }

    /**
     * Get the supported types of cells
     *
     * @param id the universe id
     * @return a String array of supported types
     */
    @GetMapping("/supportedTypes/{id}")
    public String[] supportedTypes(@PathVariable String id) {
        List<CellEltFactory> factories = CellEltFactory.factories;

        String[] supportedTypes = new String[factories.size()];

        for (int i = 0; i < factories.size(); i++) {
            supportedTypes[i] = factories.get(i).getName();
        }

        return supportedTypes;
    }

    /**
     * Create a new element in the universe
     *
     * @param universeId the universe id
     * @param body       the body of the request containing the type and the coordinates of the element
     */
    @PostMapping("/elements/{universeId}")
    @ResponseBody
    public int[][][] createElement(@PathVariable String universeId, @RequestBody JSONObject body) {
        int[] tabXY = parseXY(body);
        int x = tabXY[0];
        int y = tabXY[1];
        String type = parseType(body);

        Simulation simulation = universes.get(universeId);
        List<CellEltFactory> factories = CellEltFactory.factories;

        CellEltFactory factory = factories.stream().filter(f -> f.getName().equals(type)).findFirst().orElse(null);
        if (factory != null) {
            Board board = simulation.getBoard();
            factory.createAndAddElement(board, x, y);
            simulation.fireModificationEvent();

        }
        return simulation.getState();
    }

    @DeleteMapping("/elements/{universeId}")
    @ResponseBody
    public int[][][] deleteElementsFromCoordinates(@PathVariable String universeId, @RequestBody JSONObject body) {
        int[] tabXY = parseXY(body);
        int x = tabXY[0];
        int y = tabXY[1];

        Simulation simulation = universes.get(universeId);
        Board board = simulation.getBoard();
        while (board.getICell(x, y).getNumberOfElements() > 0) {
            board.removeElement((IPositionnableElement) board.getICell(x, y).getTopElement());
        }
        simulation.fireModificationEvent();
        return simulation.getState();
    }

    /**
     * Get the state of the universe in a format of RGB values
     *
     * @param universeId the id of the universe
     * @return a 2D array of RGB values
     */
    @GetMapping("/universeState/{universeId}")
    public int[][][] getState(@PathVariable String universeId) {
        return universes.get(universeId).getState();
    }

    /**
     * Pause or resume the simulation
     *
     * @param id   the id of the universe
     * @param body the body of the request containing the mode (run or pause)
     */
    @PutMapping("/simulation/{id}")
    @ResponseBody
    public JSONObject pauseSimulation(@PathVariable String id, @RequestBody JSONObject body) {
        String mode = body.get("mode").toString();

        Simulation simulation = universes.get(id);
        if (simulation != null) {
            if (mode.equals("run")) {
                simulation.pauseSimulation(false);
            } else if (mode.equals("pause")) {
                simulation.pauseSimulation(true);
            }
        }
        return body;
    }

    /**
     * Step forward in the simulation by one step
     *
     * @param id the id of the universe
     * @return the state of the universe after the step if the simulation is paused, the current state otherwise
     */
    @GetMapping("/step/{id}")
    public int[][][] stepForward(@PathVariable String id) {
        if (universes.get(id).isPaused()) {
            universes.get(id).forwardSimulation();
        }
        return universes.get(id).getState();
    }

    /**
     * Step backward in the simulation
     *
     * @param id the id of the universe
     * @return the state of the universe if the simulation is paused, the current state otherwise
     */
    @GetMapping("/stepBack/{id}")
    public int[][][] stepBackward(@PathVariable String id) {
        if (universes.get(id).isPaused()) {
            universes.get(id).backwardSimulation();
        }
        return universes.get(id).getState();
    }


    /**
     * Get if we use icons or not
     *
     * @return true if we use icons, false otherwise (a String)
     */
    @GetMapping("/useIcons")
    public boolean useIcons() {
        return false;
    }


    /**
     * Parse the x and y coordinates from the body of the request
     *
     * @param body JSONObject containing the x and y coordinates
     * @return an array of 2 integers (x and y)
     */
    private int[] parseXY(JSONObject body) {
        int x = Integer.parseInt(body.get("x").toString());
        int y = Integer.parseInt(body.get("y").toString());

        return new int[]{x, y};
    }

    /**
     * Parse the type of the element from the body of the request
     *
     * @param body JSONObject containing the type of the element
     * @return the type of the element (String)
     */
    private String parseType(JSONObject body) {
        return body.get("type").toString();

    }
}
