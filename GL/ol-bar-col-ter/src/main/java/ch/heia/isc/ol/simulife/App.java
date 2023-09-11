package ch.heia.isc.ol.simulife;

import ch.heia.isc.ol.simulife.model.Board;
import ch.heia.isc.ol.simulife.model.BoardInitializer;
import ch.heia.isc.ol.simulife.model.Simulation;
import ch.heia.isc.ol.simulife.view.DefaultView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static boolean displayView = false;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Simulife");

        displayView = args.length > 0 && args[0].equals("--view");

        if (displayView) {
            BoardInitializer initializer = new BoardInitializer();

            // Start timer to measure the time of the startup
            long startTime = System.currentTimeMillis();

            Board board = initializer.initializeBoard();
            Simulation simulation = new Simulation(board);


            System.out.println("Running in GUI mode");
            DefaultView view = new DefaultView(board);
            view.registerUndoCommand(simulation::backwardSimulation);
            view.registerRedoCommand(simulation::forwardSimulation);
            view.registerPauseCommand(simulation::pauseSimulation);
            view.registerPauseInfo(simulation::isPaused);
            view.registerStoppedInfo(simulation::isFinished);

            simulation.registerRefreshCommand(view::refresh);

            view.setVisible(true);
            view.refresh();

            // Stop timer and print the time of the startup
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Startup time: " + elapsedTime + " ms");

            Thread thread = new Thread(() -> {
                try {
                    simulation.runLifeCycle();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            });
            thread.setName("Standalone");
            thread.start();
        }

        SpringApplication.run(App.class, args);


    }
}
