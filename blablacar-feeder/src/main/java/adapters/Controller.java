package adapters;

import application.BlablacarTripProvider;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    private final BlablacarTripProvider tripProvider;
    private final ScheduledExecutorService scheduler;
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Controller.class.getName());

    public Controller(BlablacarTripProvider tripProvider) {
        this.tripProvider = tripProvider;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        LOGGER.info("Starting Blablacar feeder service...");
        scheduler.scheduleAtFixedRate(() -> {
            try {
                tripProvider.fetchAndProcessAllTrips();
            } catch (Exception e) {
                LOGGER.warning("Error during trip processing: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public void stop() {
        LOGGER.info("Shutting down Blablacar feeder service...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}